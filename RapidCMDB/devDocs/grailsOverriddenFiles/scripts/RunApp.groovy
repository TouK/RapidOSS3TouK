import org.codehaus.groovy.grails.cli.GrailsScriptRunner
import org.codehaus.groovy.grails.plugins.PluginManagerHolder
import org.mortbay.jetty.Connector
import org.mortbay.jetty.Server
import org.mortbay.jetty.nio.SelectChannelConnector
import org.mortbay.jetty.security.SslSocketConnector
import org.mortbay.jetty.webapp.WebAppContext
import sun.security.tools.KeyTool
import org.apache.commons.io.FileUtils
import java.util.Map.Entry

/*
* Copyright 2004-2005 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

/**
 * Gant script that executes Grails using an embedded Jetty server
 *
 * @author Graeme Rocher
 *
 * @since 0.4
 */

Ant.property(environment:"env")
grailsHome = System.getProperty("grails.home");
if(grailsHome == null)
{
    grailsHome = Ant.antProject.properties."env.GRAILS_HOME"
}
else
{
    Ant.antProject.setProperty("env.GRAILS_HOME", grailsHome);
}
userHome = Ant.antProject.properties."user.home"
Ant.property(file:"${grailsHome}/build.properties")
grailsVersion =  Ant.antProject.properties.'grails.version'
keystore = "${userHome}/.grails/${grailsVersion}/ssl/keystore"
keystoreFile = new File("${keystore}")
keyPassword = "123456"

grailsServer = null
rootContextLoader = null;
grailsContext = null
autoRecompile = System.getProperty("disable.auto.recompile") ? !(System.getProperty("disable.auto.recompile").toBoolean()) : true

// How often should recompilation occur while the application is running (in seconds)?
// Defaults to 3s.
recompileFrequency = System.getProperty("recompile.frequency")
recompileFrequency = recompileFrequency ? recompileFrequency.toInteger() : 3


includeTargets << new File ( "${grailsHome}/scripts/Init.groovy" )
includeTargets << new File ( "${grailsHome}/scripts/Package.groovy" )
rootSrcDirs = [
        new File("${grailsHome}/scripts"),
        new File("${grailsHome}/RapidSuite/src"),
        new File("${grailsHome}/RapidSuite/grails-app"),
        new File("${grailsHome}/RapidSuite/plugins")
];


shouldPackageTemplates=true


def getHttpsPort()
{
    return System.getProperty("server.https.port");
}
def getSourceFileList()
{
    def excludedDirs =  [
            new File("${grailsHome}/RapidSuite/grails-app/templates"),
            new File("${grailsHome}/RapidSuite/grails-app/i18n"),
            new File("${grailsHome}/RapidSuite/grails-app/views")
    ];
    def allSourceFiles = [:]
    rootSrcDirs.each{srcDir->
        def files = FileUtils.listFiles (srcDir, ["groovy", "java"] as String[], true);
        files.each{File srcFile->
            def isExcludedFile = excludedDirs.find{srcFile.canonicalPath.startsWith (it.canonicalPath)};
            if(!isExcludedFile)
            {
                allSourceFiles[srcFile.canonicalPath] = [updateTime:srcFile.lastModified()]
            }
        }
    }
    return allSourceFiles;
}

def isChanged(Map oldFiles, Map newFiles)
{
    def iter = newFiles.entrySet().iterator();
    while(iter.hasNext()){
        Entry entry = iter.next();
        def filePath = entry.getKey();
        def fileConf = entry.getValue();
        def oldFileConf = oldFiles.remove (filePath)
        if(oldFileConf == null)
        {
            return true;
        }
        else if(oldFileConf.updateTime != fileConf.updateTime)
        {
            return true;      
        }
    }
    return !oldFiles.isEmpty();
}
def saveSourceFileList(Map allSourceFiles)
{

    File sourceListFile = new File("${projectWorkDir}/rsSources.bin");
    println "Saving current source file configuration to ${sourceListFile.path}"
    sourceListFile.parentFile.mkdirs();
    sourceListFile.delete();
    FileOutputStream fout = new FileOutputStream(sourceListFile);
    ObjectOutputStream out = new ObjectOutputStream(fout)
    try{
        out.writeObject (allSourceFiles);
    }
    catch(e){
        println "Could not save source file configuration to file ${sourceListFile.path}. Reason: ${e.toString()}"
    }
    finally {
        out.close();
        fout.close();
    }
}
def loadOldSourceFileConfiguration()
{
    File sourceListFile = new File("${projectWorkDir}/rsSources.bin");
    println "Loading old source file configuration from ${sourceListFile.path}"
    if(sourceListFile.exists())
    {
        try{
            FileInputStream fin = new FileInputStream(sourceListFile);
            ObjectInputStream objIn = new ObjectInputStream(fin)
            return objIn.readObject();
        }catch(e){
            //ignore
        }

    }
    else
    {
        println "No old source file configuration exist"
    }
    return [:];
}
target ('default': "Run's a Grails application in Jetty") {
    def oldSourceFileConfiguration = loadOldSourceFileConfiguration();
    def newSourceFileConfiguration = getSourceFileList();
    if(isChanged(oldSourceFileConfiguration, newSourceFileConfiguration))
    {
        println "At least one of the source files is changed. Source files will be recompiled."
        Ant.delete(dir : projectWorkDir);
    }
    else
    {
        println "No source file modification. No recompilation is required."   
    }
    depends( checkVersion, configureProxy, packageApp )
    saveSourceFileList (newSourceFileConfiguration);
    Runtime.getRuntime().addShutdownHook(new Thread({
    	if(grailsServer != null)
    	{
    		grailsServer.stop();
    	}
    }));
    runApp()
	watchContext()

}
target ( runApp : "Main implementation that executes a Grails application") {
	System.setProperty('org.mortbay.xml.XmlParser.NotValidating', 'true')
    try {
        print "Running Grails application.."

		boolean stopNotificationThread = false;
		Thread t = Thread.start{
            def dotCount = 1;
            while(!stopNotificationThread)
            {
                print "."
                if(dotCount % 175 == 0)
                {
                    println "";
                }
                dotCount++;
                Thread.sleep(100);
            }
        }
        def server = configureHttpServer()
        try{
            profile("start server") {
                server.start()
            }
        }finally{
            stopNotificationThread = true;
            t.join();
            println "";
        }
        event("StatusFinal", ["Server running. Browse to http://localhost:${serverPort}${serverContextPath} ${getHttpsPort() != null?" OR https://localhost:${getHttpsPort()}${serverContextPath}":""}"])
    } catch(Throwable t) {
        t.printStackTrace()
        event("StatusFinal", ["Server failed to start: $t"])
    }
}
target( watchContext: "Watches the WEB-INF/classes directory for changes and restarts the server if necessary") {
    long lastModified = classesDir.lastModified()
    boolean keepRunning = true
    boolean isInteractive = System.getProperty("grails.interactive.mode") == "true"

    if(isInteractive) {
        def daemonThread = new Thread( {
            println "--------------------------------------------------------"
            println "Application loaded in interactive mode, type 'exit' to shutdown server or your command name in to continue (hit ENTER to run the last command):"

            def reader = new BufferedReader(new InputStreamReader(System.in))
            def cmd = reader.readLine()
            def scriptName
            while(cmd!=null) {
                if(cmd == 'exit' || cmd == 'quit') break
                if(cmd != 'run-app') {
                    scriptName = cmd ? GrailsScriptRunner.processArgumentsAndReturnScriptName(cmd) : scriptName
                    if(scriptName) {
                        def now = System.currentTimeMillis()
                        GrailsScriptRunner.callPluginOrGrailsScript(scriptName)
                        def end = System.currentTimeMillis()
                        println "--------------------------------------------------------"
                        println "Command [$scriptName] completed in ${end-now}ms"
                    }
                }
                else {
                    println "Cannot run the 'run-app' command. Server already running!"
                }
                try {
                    println "--------------------------------------------------------"
                    println "Application loaded in interactive mode, type 'exit' to shutdown server or your command name in to continue (hit ENTER to run the last command):"

                    cmd = reader.readLine()
                } catch (IOException e) {
                    cmd = ""
                }
            }

            println "Stopping Grails server..."
            grailsServer.stop()
            keepRunning = false

        })
        daemonThread.daemon = true
        daemonThread.run()
    }

    while(true) {
        if (Boolean.getBoolean("restart.application"))
        {
            sleep(3000);
            try {
                if(!rootContextLoader)
                {
                    rootContextLoader = Thread.currentThread().getContextClassLoader()
                }
                grailsServer.stop()
                compile()
                classLoader = new URLClassLoader([classesDir.toURI().toURL()] as URL[], rootContextLoader)
                PluginManagerHolder.pluginManager = null
                // reload plugins
                loadPlugins()
                setupWebContext()
                grailsServer.setHandler( webContext )
                print "Reloading Grails application.."
                boolean stopNotificationThread = false;
                Thread t = Thread.start{
                    def dotCount = 1;
                    while(!stopNotificationThread)
                    {
                        print "."
                        if(dotCount % 175 == 0)
                        {
                            println "";    
                        }
                        dotCount++;
                        Thread.sleep(100);
                    }
                }
                try{
                    grailsServer.start()
                }finally{
                    stopNotificationThread = true;
                    t.join();
                    println "";
                }
                System.setProperty("restart.application", "false")
                event("StatusFinal", ["Server running. Browse to http://localhost:${serverPort}${serverContextPath} ${getHttpsPort() != null?" OR https://localhost:${getHttpsPort()}${serverContextPath}":""}"])
            } catch (Throwable e) {
                GrailsUtil.sanitizeStackTrace(e)
                e.printStackTrace()
            }
        }
        sleep(50)
    }
}

target( configureHttpServer : "Returns a jetty server configured with an HTTP connector") {
    def server = new Server()
    grailsServer = server
    def connectors = [new SelectChannelConnector()]
    connectors[0].setPort(serverPort)
    server.setConnectors( (Connector [])connectors )
    addHttpsConfiguration();
	setupWebContext()
    server.setHandler( webContext )
    event("ConfigureJetty", [server])
    return server
}

target( setupWebContext: "Sets up the Jetty web context") {
    webContext = new WebAppContext("${basedir}/web-app", serverContextPath)
    def configurations = [org.mortbay.jetty.webapp.WebInfConfiguration,
                          org.mortbay.jetty.plus.webapp.Configuration,
                          org.mortbay.jetty.webapp.JettyWebXmlConfiguration,
                          org.mortbay.jetty.webapp.TagLibConfiguration]*.newInstance()
    def jndiConfig = new org.mortbay.jetty.plus.webapp.EnvConfiguration()
	if(config.grails.development.jetty.env) {
		def res = resolveResources(config.grails.development.jetty.env)
		if(res) {
			jndiConfig.setJettyEnvXml(res[0].URL)
		}
	}
	configurations.add(1,jndiConfig)
    webContext.configurations = configurations
    webContext.setDefaultsDescriptor("${grailsHome}/conf/webdefault.xml")
    webContext.setClassLoader(classLoader)
    webContext.setDescriptor(webXmlFile.absolutePath)
}

target( stopServer : "Stops the Grails Jetty server") {
	if(grailsServer) {
		grailsServer.stop()
	}
    event("StatusFinal", ["Server stopped"])
}





target ( addHttpsConfiguration : "Main implementation that adds HTTPS listener to a Grails application") {
    def serverHttpsPortStr = getHttpsPort();
    if(serverHttpsPortStr != null)
    {

        def keyPasswordSystemProp = System.getProperty("server.https.key.password");
        if(keyPasswordSystemProp != null)
        {
            keyPassword =  keyPasswordSystemProp;  
        }
        def serverHttpsPort = Integer.parseInt(serverHttpsPortStr)
        if (!(keystoreFile.exists())) {
            createCert()
        }
        def secureListener = new SslSocketConnector()
        secureListener.setPort(serverHttpsPort)
        secureListener.setMaxIdleTime(50000)
        secureListener.setPassword("${keyPassword}")
        secureListener.setKeyPassword("${keyPassword}")
        secureListener.setKeystore("${keystore}")
        secureListener.setNeedClientAuth(false)
        secureListener.setWantClientAuth(true)
        def connectors = grailsServer.getConnectors().toList()
        connectors.add(secureListener)
        grailsServer.setConnectors(connectors.toArray(new Connector[0]))
    }
}

target(createCert:"Creates a keystore and SSL cert for use with HTTPS"){
 	println 'Creating SSL Cert...'
    if(!keystoreFile.getParentFile().exists() &&
        !keystoreFile.getParentFile().mkdir()){
        def msg = "Unable to create keystore folder: " + keystoreFile.getParentFile().getCanonicalPath()
        event("StatusFinal", [msg])
        throw new RuntimeException(msg)
    }
    String[] keytoolArgs = ["-genkey", "-alias", "localhost", "-dname",
                "CN=localhost,OU=Test,O=Test,C=US", "-keyalg", "RSA",
                "-validity", "365", "-storepass", "key", "-keystore",
                "${keystore}", "-storepass", "${keyPassword}",
                "-keypass", "${keyPassword}"]
    KeyTool.main(keytoolArgs)
    println 'Created SSL Cert'
}
