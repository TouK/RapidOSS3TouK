/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
package build

import java.util.regex.Pattern
import java.util.regex.Matcher



class Build extends Parent{
	
	def versionNo; 
	def buildNo;
	public final static String JSPATTERN = "\\s*<\\s*script[^>]*src=\\s*\"([^http][^\\s]*)\"[^>]*(/>|>[^<]*</\\s*script>)\\s*";
    public final static String CSSPATTERN = "\\s*<\\s*link[^>]*href=\"([^\\s]*)\"[^>]*(/>|>[^<]*</\\s*link>)\\s*";
	
	Build(){
//		if(System.getProperty("os.name").toLowerCase().indexOf("windows") > -1){
//			ant.taskdef(name : "exe4j", classname : "com.exe4j.Exe4JTask", classpath : (String)classpath.getProperty("exe4jlib_jar"));
//			ant.taskdef(name : "nsis", classname : "info.waynegrant.ant.NsisTask", classpath : env.repository_project+"/RapidRepository/lib/build/wat-12.jar");
//		}
		setClasspathForBuild();
	}
	
	static def getBuildOptions(String file){
		Properties options = new Properties();
	    try {
	    	options.load(new FileInputStream(file));
	    } catch (IOException e) {
	    	println "using default options";
	    }
	    return options;
	}

	static def replaceJavascriptAndCss(String file, String jsFile, String cssFile){
         String htmlText = clearPattern(new File(file).getText(), JSPATTERN);
         htmlText = clearPattern(htmlText, CSSPATTERN);
         htmlText = htmlText.replaceFirst("<head>", "<head>\n<script type=\"text/javascript\" src=\"${jsFile}\"></script>\n<link rel=\"stylesheet\" type=\"text/css\" href=\"${cssFile}\"></link>")
         new File(file).setText(htmlText);
    }

    static def clearPattern(wholeHTML, patternString){
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(wholeHTML);
        return matcher.replaceAll("");        
    }
//	void run(args){
//		if(args.size() >0){
//			println "Running target " + args[0];
//			invokeMethod(args[0], null);
//		}
//		else{
//			println "Running default target";
//			build();
//		}
//		println "Done";
//	}                                    
	
//	def cleanDistribution(){
//		ant.delete(dir : env.dist_rapid_suite);
//		ant.delete(){
//			ant.fileset(dir : env.distribution, excludes : "*.project*, *.classpath");
//		}
//	}


	def getVersionWithDate(){
        return "_$versionNo" + "_" + "$buildNo";
    }
    def getWebBasePath(){
        return "/RapidSuite/"
    }
	
	def setVersionAndBuildNumber(versionInBuild){
		def verFile = new File (versionInBuild);
		def verReader =verFile.newReader();
		versionNo = verReader.readLine().substring(9);
		
		buildNo =  new java.text.SimpleDateFormat("yyMMddHH").format(new Date(System.currentTimeMillis()));
		verFile.append("\r\nBuild: " + buildNo);
	}
	
    def createPlugin(pluginDir, pluginResources)
    {
        ant.copy(file:"$env.rapid_cmdb_cvs/devDocs/groovy-starter.conf", todir:"${env.dist_rapid_server}/conf")
        ant.exec(executable:"${getRsConsoleExecutableFileName(new File("${env.dist_rapid_suite}"))}", dir:"${new File("${env.dist_rapid_suite}").absolutePath}")
        {
            ant.arg(value:"compile")
            System.getenv().each{envKey, envVal->
                if(envKey != "RS_HOME")
                {
                    ant.env(key:"${envKey}", value:"${envVal}");
                }
            }
            ant.env(key:"RS_HOME", value:"${new File(env.dist_rapid_server).absolutePath}");
        }
        ant.exec(executable:"${getRsConsoleExecutableFileName(new File("${env.dist_rapid_suite}"))}", dir:"${new File("${pluginDir}").absolutePath}")
        {
            ant.arg(value:"package-plugin")
            if(!pluginResources.isEmpty())
            {

                if(System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0)
                {
                    def resources = "-Dplugin.resources=\"";
                pluginResources.each{
                    resources += it + ","
                }
                resources = resources.substring(0, resources.length()-1)+"\"";
                    ant.arg(value:resources)
                }
                else
                {
                    def resources = "-Dplugin.resources=";
                pluginResources.each{
                    resources += it + ","
                }
                resources = resources.substring(0, resources.length()-1)+"";
                    ant.arg(value:resources)
                    ant.env(key:"JAVA_OPTS", value:"${resources}");
                }
            }
            System.getenv().each{envKey, envVal->
                if(envKey != "RS_HOME")
                {
                    ant.env(key:"${envKey}", value:"${envVal}");
                }
            }
            ant.env(key:"RS_HOME", value:"${new File(env.dist_rapid_server).absolutePath}");

        }
        ant.move(todir:"${env.distribution}"){
            ant.fileset(file: "$pluginDir/grails-*.zip");
        }

        ant.delete(file: "$pluginDir/plugin.xml");
    }

    def installPlugin(pluginFile, destionationApplicationPath, params, systemParams)
    {

        def pluginFilePath = pluginFile.absolutePath;
        def pluginName = pluginFile.name.substring("grails-".length(), pluginFile.name.length()-".zip".length())
        ant.unzip(src: pluginFilePath, dest: env.dist_rapid_suite+"/plugins/${pluginName}");
        def classLoader = new GroovyClassLoader();
        def installScriptClass = classLoader.parseClass(new File("${destionationApplicationPath}/plugins/${pluginName}/scripts/_Install.groovy"))

        def prevSystemParams = [:]
        systemParams["base.dir"] = destionationApplicationPath;
        systemParams.each{paramName, paramValue->
            prevSystemParams[paramName] = System.getProperty(paramName)
            System.setProperty(paramName, paramValue)
        }

        def instance = installScriptClass.newInstance();
        instance.properties.putAll( params);
        instance.setBinding(new Binding(params));
        instance.run();

        prevSystemParams.each{paramName, paramValue->
            if(paramValue)
            {
                System.setProperty(paramName, paramValue)
            }
        }
    }

    def setClasspathForBuild(){
		ant.path(id : "classpath"){
			ant.pathelement(location : env.rapid_comp_jar);
			ant.pathelement(location : env.rapid_core_jar);
			ant.pathelement(location : env.rapid_ext_jar);
			ant.pathelement(location : env.rapid_cmdb_jar);
			ant.pathelement(location : env.rapid_rssmarts_jar);
			ant.pathelement(location : (String)classpath.getProperty("smack_jar"));
			ant.pathelement(location : (String)classpath.getProperty("smackx_jar"));
			ant.pathelement(location : (String)classpath.getProperty("joscar-0_9_3-patched_jar"));
			ant.pathelement(location : (String)classpath.getProperty("STComm_jar"));
			ant.pathelement(location : (String)classpath.getProperty("RCExtensions_jar"));
			ant.pathelement(location : (String)classpath.getProperty("activation_jar"));
			ant.pathelement(location : (String)classpath.getProperty("blowfishj-2_14_jar"));
			ant.pathelement(location : (String)classpath.getProperty("bsf_jar"));
			ant.pathelement(location : (String)classpath.getProperty("bsh-2_0b2_jar"));
			ant.pathelement(location : (String)classpath.getProperty("commons-beanutils_jar"));
			ant.pathelement(location : (String)classpath.getProperty("commons-cli-1_0_jar"));
			ant.pathelement(location : (String)classpath.getProperty("commons-codec-1_3_jar"));
			ant.pathelement(location : (String)classpath.getProperty("core-3_1_1_jar"));
			ant.pathelement(location : (String)classpath.getProperty("jsp-2_1_jar"));
			ant.pathelement(location : (String)classpath.getProperty("jsp-api-2_1_jar"));
			ant.pathelement(location : (String)classpath.getProperty("servlet-api-2_5-6_1_7_jar"));
			ant.pathelement(location : (String)classpath.getProperty("commons-collections-3_2_jar"));
			ant.pathelement(location : (String)classpath.getProperty("commons-configuration-1_2_jar"));
			ant.pathelement(location : (String)classpath.getProperty("commons-digester-1_7_jar"));
			ant.pathelement(location : (String)classpath.getProperty("commons-httpclient-3_0_1_jar"));
			ant.pathelement(location : (String)classpath.getProperty("commons-lang-2_1_jar"));
			ant.pathelement(location : (String)classpath.getProperty("commons-logging_jar"));
			ant.pathelement(location : (String)classpath.getProperty("commons-pool-1_4_jar"));
			ant.pathelement(location : (String)classpath.getProperty("commons-io-1_4_jar"));
			ant.pathelement(location : (String)classpath.getProperty("commons-transaction-1_2_jar"));
			ant.pathelement(location : (String)classpath.getProperty("derby_jar"));
			ant.pathelement(location : (String)classpath.getProperty("derbyclient_jar"));
			ant.pathelement(location : (String)classpath.getProperty("derbynet_jar"));
			ant.pathelement(location : (String)classpath.getProperty("javax_servlet_jar"));	
			ant.pathelement(location : (String)classpath.getProperty("commons-transaction-1_2_jar"));	
			ant.pathelement(location : (String)classpath.getProperty("jakarta-oro-2_0_8_jar"));
			ant.pathelement(location : (String)classpath.getProperty("jdom_jar"));
			ant.pathelement(location : (String)classpath.getProperty("js_jar"));
			ant.pathelement(location : (String)classpath.getProperty("groovy-all-1_5_1_jar"));
			ant.pathelement(location : (String)classpath.getProperty("groovy-xmlrpc-0_3_jar"));
			ant.pathelement(location : (String)classpath.getProperty("log4j-1_2_13_jar"));			
			ant.pathelement(location : (String)classpath.getProperty("net_jar"));
			ant.pathelement(location : (String)classpath.getProperty("org_mortbay_jetty_jar"));			
			ant.pathelement(location : (String)classpath.getProperty("regexp_jar"));
			ant.pathelement(location : (String)classpath.getProperty("rife-1_3_1-jdk14_jar"));
			ant.pathelement(location : (String)classpath.getProperty("skclient_jar"));
			ant.pathelement(location : (String)classpath.getProperty("wat-12_jar"));
			ant.pathelement(location : (String)classpath.getProperty("xbean_jar"));
			ant.pathelement(location : (String)classpath.getProperty("prevayler-2_3_jar"));
			ant.pathelement(location : (String)classpath.getProperty("xerces_jar"));
			ant.pathelement(location : (String)classpath.getProperty("SNMP4J_jar"));
			ant.pathelement(location : (String)classpath.getProperty("mail_jar"));
			ant.pathelement(location : (String)classpath.getProperty("xstream-1_1_3_jar"));
			ant.pathelement(location : (String)classpath.getProperty("xpp3_min-1_1_3_8_jar"));
			ant.pathelement(location : (String)classpath.getProperty("truelicense_jar"));
			ant.pathelement(location : (String)classpath.getProperty("trueswing_jar"));
			ant.pathelement(location : (String)classpath.getProperty("truexml_jar"));
			ant.pathelement(location : (String)classpath.getProperty("org_mortbay_jetty_jar"));
			ant.pathelement(location : (String)classpath.getProperty("org_mortbay_jmx_jar"));
			ant.pathelement(location : (String)classpath.getProperty("start_jar"));
			ant.pathelement(location : (String)classpath.getProperty("stop_jar"));
			ant.pathelement(location : (String)classpath.getProperty("smppapi-0_3_7_jar"));
			ant.pathelement(location : (String)classpath.getProperty("stax-api-1_0_1_jar"));
			ant.pathelement(location : (String)classpath.getProperty("stax-1_2_0_jar"));
			ant.pathelement(location : (String)classpath.getProperty("DdlUtils-1_0_jar"));
			ant.pathelement(location : (String)classpath.getProperty("commons-betwixt-0_8_jar"));
			ant.pathelement(location : (String)classpath.getProperty("SNMP4J_jar"));
			ant.pathelement(location : (String)classpath.getProperty("compass_jar"));
			ant.pathelement(location : (String)classpath.getProperty("easymock_jar"));
			ant.pathelement(location : (String)classpath.getProperty("lucene-analyzers_jar"));
			ant.pathelement(location : (String)classpath.getProperty("lucene-core_jar"));
			ant.pathelement(location : (String)classpath.getProperty("lucene-highlighter_jar"));
			ant.pathelement(location : (String)classpath.getProperty("lucene-queries_jar"));
			ant.pathelement(location : (String)classpath.getProperty("lucene-snowball_jar"));
			ant.pathelement(location : (String)classpath.getProperty("lucene-spellchecker_jar"));

			//Required for compiling test classes
			ant.pathelement(location : (String)classpath.getProperty("junit_jar"));
			ant.pathelement(location : (String)classpath.getProperty("httpunit_jar"));
			ant.pathelement(location : (String)classpath.getProperty("jrequire_eclipse_jar"));
			ant.pathelement(location : (String)classpath.getProperty("mysql-connector-java-3_1_8-bin_jar"));
			ant.pathelement(location : (String)classpath.getProperty("ojdbc14_jar"));
			ant.pathelement(location : (String)classpath.getProperty("poi-2_5_1-final-20040804_jar"));//Excel
			ant.pathelement(location : (String)classpath.getProperty("web-api_jar")); //HP ServiceDesk tests
			ant.pathelement(location : (String)classpath.getProperty("selenium-java-client-driver_jar"));
			ant.pathelement(location : (String)classpath.getProperty("selenium-server_jar"));
			ant.pathelement(location : (String)classpath.getProperty("cobra_jar"));
			
			// GWT
			ant.pathelement(location : (String)classpath.getProperty("gwt-dev-windows_jar"));
			ant.pathelement(location : (String)classpath.getProperty("gwt-servlet_jar"));
			ant.pathelement(location : (String)classpath.getProperty("gwt-user_jar"));
			
		}
	}

    def getRsConsoleExecutableFileName(File rootDir)
    {
        if(System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0)
        {
            return "${rootDir.getAbsolutePath()}/rsconsole.bat";
        }
        else
        {
            def command = "${rootDir.getAbsolutePath()}/rsconsole.sh";
            def process = "chmod +x ${command}".execute();
            process.consumeProcessOutput(System.out, System.err);
            process.waitFor();
            process = "dos2unix ${command}".execute();
            process.consumeProcessOutput(System.out, System.err);
            process.waitFor();
            return command;
        }
    }
    
    def addJreOnTopOfUnixAndZip(jarNamePrepend) {
        ant.copy(todir: "$env.dist_rapid_server/jre") {
            ant.fileset(dir: "$env.jreDir")
        }
        def versionDate = getVersionWithDate();
        if (ZIP_OPT){
	        def zipFileName = "${env.distribution}/${jarNamePrepend}_Windows$versionDate" + ".zip"
	        ant.zip(destfile: zipFileName) {
	        	ant.zipfileset(dir: "$env.distribution/RapidServer", prefix: "RapidServer")
//	            ant.zipfileset(dir: "$env.distribution") {
//	                ant.exclude(name: ".project");
//	                ant.exclude(name: "*.zip");
//	                ant.exclude(name: "**/temp/**");
//	            }
	        }
        }
    }    
    
    def copyForTesting() {
//      TEST = true;
//      build();
//      def versionDate = getVersionWithDate();
//      ant.delete(dir: env.distribution + "/RapidServer");
//      if (System.getProperty("os.name").indexOf("Windows") < 0) {
//          ant.unzip(src: "$env.distribution/RapidCMDB_Unix$versionDate" + ".zip", dest: env.distribution);
//      }
//      else {
//          ant.unzip(src: "$env.distribution/RapidCMDB_Windows$versionDate" + ".zip", dest: env.distribution);
//      }

      ant.copy(tofile: "$env.dist_rapid_suite/../conf/groovy-starter.conf", file: "${env.dev_docs}/groovy-starter-for-tests.conf", overwrite: "true")
      ant.copy(todir: "$env.dist_rapid_suite/grails-app/domain") {
          ant.fileset(dir: "$env.rapid_cmdb_cvs/grails-app/domain") {
              ant.include(name: "*.groovy")
              ant.include(name: "test/*")
          }
      }
  }

}