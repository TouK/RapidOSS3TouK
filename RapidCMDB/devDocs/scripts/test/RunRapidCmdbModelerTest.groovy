package test
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.NameFileFilter
import org.apache.commons.io.filefilter.NotFileFilter
import build.RapidCmdbBuild
import test.BuildDirListener

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 20, 2008
 * Time: 11:14:09 AM
 * To change this template use File | Settings | File Templates.
 */
def ANT = new AntBuilder();
long t = System.currentTimeMillis();
def workspaceDir = ".."
def workspaceDirFile = new File(workspaceDir);
def zipFile = new File("${workspaceDir}/ThirdParty/lib/grails/grails-1.0.3.zip");
def rootDir = new File("${workspaceDir}/test/RapidCMDBModeler/RapidServer");
Process proc = null;


def watchConfig = [
        [new File("${workspaceDir}/RapidModules/RapidCMDBModeler"), new File("${rootDir.absolutePath}/Modeler")],
        [new File("${workspaceDir}/RapidModules/RcmdbCommons"), new File("${rootDir.absolutePath}/Modeler")]
]


def dirListeners = [];
def excludedDirs = [".svn":".svn",
            "reports":"reports"]
if(!rootDir.exists())
{
    ANT.unzip(src:zipFile.getAbsolutePath(), dest:rootDir.getAbsolutePath()+"/..")

    def libs = [
            new File("${workspaceDir}/ThirdParty/lib/commons"),
            new File("${workspaceDir}/ThirdParty/lib/jdbc"),
            new File("${workspaceDir}/ThirdParty/lib/tools.jar"),
            new File("${workspaceDir}/ThirdParty/lib/log4j-1.2.13.jar"),
            new File("${workspaceDir}/ThirdParty/lib/grails/runner.jar"),
            new File("${workspaceDir}/ThirdParty/lib/testing"),
            new File("${workspaceDir}/LicencedJars/lib/jdbc"),
    ]

    libs.each{File jarFile->
        if(jarFile.isDirectory())
        {
            FileUtils.listFiles(jarFile, ["jar"] as String[], true).each{
                ANT.copy(file: it.path, toDir: "${rootDir.path}/Modeler/lib");
            }
        }
        else
        {
            ANT.copy(file: jarFile.path, toDir: "${rootDir.path}/Modeler/lib");
        }
    }

    def cmdbBuild = new RapidCmdbBuild();
    cmdbBuild.run(["testBuild"]);
    ANT.copy(toDir: "${rootDir.absolutePath}/Modeler/lib")
    {
        ANT.fileset(dir: "$workspaceDir/Distribution/RapidServer/Modeler/lib")
    }


    watchConfig.each{dirPairs ->
        def srcDir = dirPairs[0]
        def destDir = dirPairs[1]
        FileUtils.copyDirectory (srcDir, destDir, new NotFileFilter(new NameFileFilter([".svn", ".classpath"] as String[])));
        dirListeners += new BuildDirListener(srcDir, destDir, excludedDirs);
    }

    println "Installing testing plugin"
    def envVars = getEnvVars(rootDir);
    def path = "${workspaceDirFile.absolutePath}/RapidModules/RapidCMDB/${getTestExecutableFileName()} install-plugin ${workspaceDirFile.getAbsolutePath()}/RapidModules/RapidTesting/grails-rapid-testing-0.1.zip".toString();
    println "Running command ${path} to install testing plugin"
    proc = Runtime.getRuntime().exec(path, envVars as String[], new File(rootDir.getAbsolutePath()+"/Modeler"));
    proc.consumeProcessOutput(System.out, System.err);
    proc.waitFor();

}
else
{
    watchConfig.each{dirPairs ->
        def srcDir = dirPairs[0]
        def destDir = dirPairs[1]
        dirListeners += new BuildDirListener(srcDir, destDir, excludedDirs);
    }
}

println "Stating Application"
System.addShutdownHook {
    dirListeners.each{BuildDirListener list->
        list.destroy();
    }
    if(proc)
    {
        proc.waitForOrKill (1000);
    }
}
def envVars = getEnvVars(rootDir);

def path = "${workspaceDirFile.absolutePath}/RapidModules/RapidCMDB/${getTestExecutableFileName()} run-app".toString();
println "Running command ${path} to run application"
proc = Runtime.getRuntime().exec(path, envVars as String[], new File(rootDir.getAbsolutePath()+"/Modeler"));
proc.consumeProcessOutput(System.out, System.err);
println "TOOK ${System.currentTimeMillis() - t} secs to start testing application."
proc.waitFor();

def getEnvVars(rootDir)
{
    def tEnvVars = ["RS_HOME=${rootDir.getAbsolutePath()}".toString()];
    System.getenv().each{key, value->
        tEnvVars += "${key}=${value}".toString()
    }
    return  tEnvVars;
}

def getTestExecutableFileName()
{
    if(System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0)
    {
        return "test.bat";
    }
    else
    {
        return "test.sh";
    }
}


