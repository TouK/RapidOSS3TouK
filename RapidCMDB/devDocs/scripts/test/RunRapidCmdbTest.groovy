import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.NameFileFilter
import org.apache.commons.io.filefilter.NotFileFilter
import test.DirListener
import build.RapidCmdbBuild

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 20, 2008
 * Time: 11:14:09 AM
 * To change this template use File | Settings | File Templates.
 */
def ANT = new AntBuilder();
long t = System.currentTimeMillis();
def workspaceDir = "d:/workspace"
def zipFile = new File("${workspaceDir}/ThirdParty/lib/grails/grails-1.0.3.zip");
def rootDir = new File("${workspaceDir}/test/RapidServer");
Process proc = null;


def watchConfig = [
        [new File("${workspaceDir}/RapidModules/RapidCMDB"), new File("${workspaceDir}/test/RapidServer/RapidCMDB")],
        [new File("${workspaceDir}/RapidModules/RcmdbCommons"), new File("${workspaceDir}/test/RapidServer/RapidCMDB")],
        [new File("${workspaceDir}/RapidModules/ext/database/groovy"), new File("${workspaceDir}/test/RapidServer/RapidCMDB/grails-app/ext")],
        [new File("${workspaceDir}/RapidModules/ext/http/groovy"), new File("${workspaceDir}/test/RapidServer/RapidCMDB/grails-app/ext")],
        [new File("${workspaceDir}/RapidModules/ext/netcool/groovy"), new File("${workspaceDir}/test/RapidServer/RapidCMDB/grails-app/ext")],
        [new File("${workspaceDir}/RapidModules/ext/rapidinsight/groovy"), new File("${workspaceDir}/test/RapidServer/RapidCMDB/grails-app/ext")],
        [new File("${workspaceDir}/RapidModules/ext/smarts/groovy"), new File("${workspaceDir}/test/RapidServer/RapidCMDB/grails-app/ext")]
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
            new File("${workspaceDir}/ThirdParty/lib/snmp"),
            new File("${workspaceDir}/ThirdParty/lib/testing"),
            new File("${workspaceDir}/LicencedJars/lib/jdbc"),
            new File("${workspaceDir}/LicencedJars/lib/smarts")
    ]

    libs.each{File jarFile->
        if(jarFile.isDirectory())
        {
            FileUtils.listFiles(jarFile, ["jar"] as String[], true).each{
                ANT.copy(file: it.path, toDir: "${rootDir.path}/RapidCMDB/lib");
            }
        }
        else
        {
            ANT.copy(file: jarFile.path, toDir: "${rootDir.path}/RapidCMDB/lib");
        }
    }

    FileUtils.listFiles(new File("${workspaceDir}/LicencedJars"), ["jar"] as String[], true).each
    {
        ANT.copy(file: it.path, toDir: "${rootDir.path}/RapidCMDB/lib");
    }
    def cmdbBuild = new RapidCmdbBuild();
    cmdbBuild.run(["testBuild"]);
    ANT.copy(toDir: "${rootDir.path}/RapidCMDB/lib")
    {
        ANT.fileset(dir: "$workspaceDir/Distribution/RapidServer/RapidCMDB/lib")
    }
    ANT.unzip(src: "$workspaceDir/Distribution/SmartsModule${cmdbBuild.smartsBuild.getVersionWithDate()}" + ".zip", dest: "${rootDir.path}");
    ANT.unzip(src: "$workspaceDir/Distribution/NetcoolModule${cmdbBuild.netcoolBuild.getVersionWithDate()}" + ".zip", dest: "${rootDir.path}");


    watchConfig.each{dirPairs ->
        def srcDir = dirPairs[0]
        def destDir = dirPairs[1]
        FileUtils.copyDirectory (srcDir, destDir, new NotFileFilter(new NameFileFilter(".svn")));
        dirListeners += new DirListener(srcDir, destDir, excludedDirs);
    }

    println "Installing testing plugin"
    def envVars = getEnvVars(rootDir);
    proc = Runtime.getRuntime().exec("${rootDir.getPath()}/RapidCMDB/test.bat install-plugin ${workspaceDir}/RapidModules/RapidTesting/grails-rapid-testing-0.1.zip".toString(), envVars as String[], new File(rootDir.getAbsolutePath()+"/RapidCMDB"));
    proc.consumeProcessOutput(System.out, System.err);
    proc.waitFor();
}
else
{
    watchConfig.each{dirPairs ->
        def srcDir = dirPairs[0]
        def destDir = dirPairs[1]
        dirListeners += new DirListener(srcDir, destDir, excludedDirs);
    }
}

println "Stating Application"
System.addShutdownHook {
    dirListeners.each{DirListener list->
        list.destroy();
    }
    if(proc)
    {
        proc.waitForOrKill (1000);
    }
}
def envVars = getEnvVars(rootDir);
proc = Runtime.getRuntime().exec("${rootDir.getPath()}/RapidCMDB/test.bat run-app".toString(), envVars as String[], new File(rootDir.getAbsolutePath()+"/RapidCMDB"));
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


