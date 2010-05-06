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
package test

import build.Build
import build.RapidInsightBuild
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
def rootDir = new File("${workspaceDir}/Distribution/RapidServer");
Process proc = null;

/**
RI=false
RCMDB=false
RCMDB_COMMONS=false
APG=false
OPENNMS=false
HYPERIC=false
NETCOOL=false
SMARTS=false
JIRA=TRUE
EXT=TRUE
RAPID_UI=true
**/
def testOptions = Build.getBuildOptions("testopts.txt")
println testOptions
def buildProperties = new Properties();
buildProperties.put("RCMDB_UNIX", "true")
buildProperties.put("RCMDB_WINDOWS", "true")
buildProperties.put("TEST", "true")
buildProperties.put("JREDIR", "C:\\Program Files\\Java\\jre1.6.0_04")
def watchConfig = [];
if(testOptions.RCMDB_COMMONS == "true")
{
    watchConfig.add([new File("${workspaceDir}/RapidModules/RcmdbCommons"), new File("${rootDir.absolutePath}/RapidSuite")]);
}
if(testOptions.RCMDB == "true")
{
    watchConfig.add([new File("${workspaceDir}/RapidModules/RapidCMDB"), new File("${rootDir.absolutePath}/RapidSuite")]);
    watchConfig.add([new File("${workspaceDir}/LicencedJars/lib/jdbc"), new File("${rootDir.absolutePath}/RapidSuite/lib"), []]);
}
if(testOptions.EXT == "true")
{
    watchConfig.add([new File("${workspaceDir}/RapidModules/ext/database/groovy"), new File("${rootDir.absolutePath}/RapidSuite/grails-app/ext")]);
    watchConfig.add([new File("${workspaceDir}/RapidModules/ext/http/groovy"), new File("${rootDir.absolutePath}/RapidSuite/grails-app/ext")]);    
}

if(testOptions.RI == "true")
{
    buildProperties.put("RI_UNIX", "false")
    watchConfig.add([new File("${workspaceDir}/RapidModules/RapidInsight/grails-app"), new File("${rootDir.absolutePath}/RapidSuite/grails-app"), ["taglib"]]);
    watchConfig.add([new File("${workspaceDir}/RapidModules/RapidInsight/operations"), new File("${rootDir.absolutePath}/RapidSuite/operations")]);
    watchConfig.add([new File("${workspaceDir}/RapidModules/RapidInsight/grails-app/taglib"), new File("${rootDir.absolutePath}/RapidSuite/plugins/rapid-insight-0.1/grails-app/taglib")]);
    watchConfig.add([new File("${workspaceDir}/RapidModules/RapidInsight/test"), new File("${rootDir.absolutePath}/RapidSuite/test")]);
}
if(testOptions.HYPERIC == "true")
{
    buildProperties.put("HYPERIC", "false")
    watchConfig.add([new File("${workspaceDir}/Hyperic"), new File("${rootDir.absolutePath}/RapidSuite"), [new File("${workspaceDir}/Hyperic/applications").canonicalPath, new File("${workspaceDir}/Hyperic/integration").canonicalPath, new File("${workspaceDir}/Hyperic/application.properties").canonicalPath]]);
    watchConfig.add([new File("${workspaceDir}/Hyperic/applications/RapidInsight"), new File("${rootDir.absolutePath}/RapidSuite")]);
    watchConfig.add([new File("${workspaceDir}/Hyperic/test/integration"), new File("${rootDir.absolutePath}/RapidSuite/test/integration")]);
}
if(testOptions.RIVERMUSE == "true")
{
    buildProperties.put("RIVERMUSE", "false")
    watchConfig.add([new File("${workspaceDir}/Rivermuse"), new File("${rootDir.absolutePath}/RapidSuite"), [new File("${workspaceDir}/Rivermuse/applications").canonicalPath, new File("${workspaceDir}/Rivermuse/application.properties").canonicalPath]]);
    watchConfig.add([new File("${workspaceDir}/Rivermuse/applications/RapidInsight"), new File("${rootDir.absolutePath}/RapidSuite")]);
    watchConfig.add([new File("${workspaceDir}/Rivermuse/test/integration"), new File("${rootDir.absolutePath}/RapidSuite/test/integration")]);
}
if(testOptions.APG == "true")
{
    buildProperties.put("APG", "false")
    watchConfig.add([new File("${workspaceDir}/Apg"), new File("${rootDir.absolutePath}/RapidSuite"), [new File("${workspaceDir}/Apg/applications").canonicalPath, new File("${workspaceDir}/Apg/application.properties").canonicalPath]]);
    watchConfig.add([new File("${workspaceDir}/Apg/applications/RapidInsight"), new File("${rootDir.absolutePath}/RapidSuite")]);
}
if(testOptions.SMARTS == "true")
{
    buildProperties.put("SMARTS", "false")
    watchConfig.add([new File("${workspaceDir}/LicencedJars/lib/smarts"), new File("${rootDir.absolutePath}/RapidSuite/lib"), []]);
    watchConfig.add([new File("${workspaceDir}/Smarts"), new File("${rootDir.absolutePath}/RapidSuite"), [new File("${workspaceDir}/Smarts/applications").canonicalPath, new File("${workspaceDir}/Smarts/application.properties").canonicalPath]]);
    watchConfig.add([new File("${workspaceDir}/Smarts/applications/RapidInsightForSmarts"), new File("${rootDir.absolutePath}/RapidSuite")]);
}
if(testOptions.NETCOOL == "true")
{
    buildProperties.put("NETCOOL", "false")
    watchConfig.add([new File("${workspaceDir}/LicencedJars/lib/jdbc"), new File("${rootDir.absolutePath}/RapidSuite/lib"), []]);
    watchConfig.add([new File("${workspaceDir}/Netcool"), new File("${rootDir.absolutePath}/RapidSuite"), [new File("${workspaceDir}/Netcool/applications").canonicalPath, new File("${workspaceDir}/Netcool/application.properties").canonicalPath]]);
    watchConfig.add([new File("${workspaceDir}/Netcool/applications/RapidInsightForNetcool"), new File("${rootDir.absolutePath}/RapidSuite")]);
    watchConfig.add([new File("${workspaceDir}/Netcool/test/integration"), new File("${rootDir.absolutePath}/Netcool/test/integration")]);
}
if(testOptions.OPENNMS == "true")
{
    buildProperties.put("OPENNMS", "false")
    watchConfig.add([new File("${workspaceDir}/OpenNms"), new File("${rootDir.absolutePath}/RapidSuite"), [new File("${workspaceDir}/OpenNms/applications").canonicalPath, new File("${workspaceDir}/OpenNms/application.properties").canonicalPath]]);
    watchConfig.add([new File("${workspaceDir}/OpenNms/applications/RapidInsight"), new File("${rootDir.absolutePath}/RapidSuite")]);
    watchConfig.add([new File("${workspaceDir}/OpenNms/test/integration"), new File("${rootDir.absolutePath}/OpenNms/test/integration")]);
}
if(testOptions.JIRA == "true")
{
    watchConfig.add([new File("${workspaceDir}/RapidModules/ext/jira/lib"), new File("${rootDir.absolutePath}/RapidSuite/lib")]);
    watchConfig.add([new File("${workspaceDir}/ThirdParty/lib/webservices"), new File("${rootDir.absolutePath}/RapidSuite/lib")]);
    watchConfig.add([new File("${workspaceDir}/RapidModules/ext/jira/src/groovy"), new File("${rootDir.absolutePath}/RapidSuite/grails-app/ext")]);
}
if(testOptions.RAPID_UI == "true")
{
    watchConfig.add([new File("${workspaceDir}/RapidModules/RapidUiPlugin/grails-app"), new File("${rootDir.absolutePath}/RapidSuite/plugins/rapid-ui-0.1/grails-app"), [new File("${workspaceDir}/RapidModules/RapidUiPlugin/grails-app/conf").canonicalPath, new File("${workspaceDir}/RapidModules/RapidUiPlugin/grails-app/templates").canonicalPath]]);
    watchConfig.add([new File("${workspaceDir}/RapidModules/RapidUiPlugin/src"), new File("${rootDir.absolutePath}/RapidSuite/plugins/rapid-ui-0.1/src")]);
    watchConfig.add([new File("${workspaceDir}/RapidModules/RapidUiPlugin/RapidUiGrailsPlugin.groovy"), new File("${rootDir.absolutePath}/RapidSuite/plugins/rapid-ui-0.1/RapidUiGrailsPlugin.groovy")]);
    watchConfig.add([new File("${workspaceDir}/RapidModules/RapidUiPlugin/scripts"), new File("${rootDir.absolutePath}/RapidSuite/scripts")]);
    watchConfig.add([new File("${workspaceDir}/RapidModules/RapidUiPlugin/operations"), new File("${rootDir.absolutePath}/RapidSuite/operations")]);
    watchConfig.add([new File("${workspaceDir}/RapidModules/RapidUiPlugin/grails-app/templates"), new File("${rootDir.absolutePath}/RapidSuite/grails-app/templates")]);
    watchConfig.add([new File("${workspaceDir}/RapidModules/RapidUiPlugin/test"), new File("${rootDir.absolutePath}/RapidSuite/test")]);
    watchConfig.add([new File("${workspaceDir}/RapidModules/RapidUiPlugin/web-app"), new File("${rootDir.absolutePath}/RapidSuite/web-app")]);
}


def dirListeners = [];
def excludedDirs = [".svn", "reports"]





def envVars = getEnvVars(rootDir);
if (!new File("${rootDir.getCanonicalPath()}/RapidSuite/plugins/rapid-testing-0.1").exists())
{
    FileOutputStream out = new FileOutputStream("${workspaceDir}/Distribution/build.properties")
    buildProperties.store(out, "");
    out.close();

    def riBuild = new RapidInsightBuild();
    riBuild.main(["${workspaceDir}/Distribution/build.properties"] as String[]);
    ANT.unzip(src: "${workspaceDirFile.getAbsolutePath()}/RapidModules/RapidTesting/grails-rapid-testing-0.1.zip", dest: rootDir.getAbsolutePath() + "/RapidSuite/plugins/rapid-testing-0.1");
    ANT.move(toDir: "${rootDir.getAbsolutePath()}/RapidSuite/web-app/test") {
        ANT.fileset(dir: "${rootDir.getAbsolutePath()}/RapidSuite/plugins/rapid-testing-0.1/web-app/test");
    }

    ANT.copy(file : "${workspaceDir}/RapidModules/RapidCMDB/devDocs/Test.properties", toDir : "${rootDir.getAbsolutePath()}/RapidSuite/");

}

if(System.getProperty("debug") == "true")
{
    ANT.copy(file : "${workspaceDir}/RapidModules/RapidCMDB/devDocs/grailsOverriddenFiles/bin/startGrailsDebuggingForTests.bat", toFile : "${rootDir.getAbsolutePath()}/bin/startGrails.bat", overwrite:"true");
}
else
{
    ANT.copy(file : "${workspaceDir}/RapidModules/RapidCMDB/devDocs/grailsOverriddenFiles/bin/startGrailsForTests.bat", toFile : "${rootDir.getAbsolutePath()}/bin/startGrails.bat", overwrite:"true");    
}


watchConfig.each {dirPairs ->
    File srcDir = dirPairs[0]
    File destDir = dirPairs[1]
    def tmpExcludedDirs = [];
    tmpExcludedDirs.addAll(excludedDirs);
    if (dirPairs.size() > 2)
    {
        tmpExcludedDirs.addAll(dirPairs[2]);
    }
    def tmpExcludedDirsMap = [:];
    tmpExcludedDirs.each {
        tmpExcludedDirsMap[it] = it;
    }
    dirListeners += new BuildDirListener(srcDir, destDir, tmpExcludedDirsMap);
}



println "Stating Application"
System.addShutdownHook {
    dirListeners.each {BuildDirListener list ->
        list.destroy();
    }
    if (proc)
    {
        proc.waitForOrKill(1000);
    }
}
ANT.copy(file : "${workspaceDir}/RapidModules/RapidCMDB/devDocs/groovy-starter-for-integration-tests.conf", toFile : "${rootDir.getAbsolutePath()}/conf/groovy-starter.conf", overwrite:"true");
path = "${getTestExecutableFileName(rootDir)} run-app".toString();
println "Running command ${path} to run application"
proc = Runtime.getRuntime().exec(path, envVars as String[], new File(rootDir.getAbsolutePath() + "/RapidSuite"));
proc.consumeProcessOutput(System.out, System.err);
println "TOOK ${System.currentTimeMillis() - t} secs to start testing application."
proc.waitFor();

def getEnvVars(rootDir)
{
    def tEnvVars = [];
    System.getenv().each {key, value ->
        tEnvVars += "${key}=${value}".toString()
    }
    tEnvVars.add("RS_HOME=${rootDir.getAbsolutePath()}".toString());
    return tEnvVars;
}

def getTestExecutableFileName(File rootDir)
{
    if (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0)
    {
        return "${rootDir.getAbsolutePath()}/RapidSuite/test.bat";
    }
    else
    {
        def command = "${rootDir.getAbsolutePath()}/RapidSuite/test.sh";
        def process = "sudo chmod +x ${command}".execute();
        process.consumeProcessOutput(System.out, System.err);
        process.waitFor();
        process = "sudo dos2unix ${command}".execute();
        process.consumeProcessOutput(System.out, System.err);
        process.waitFor();
        return command;
    }
}


