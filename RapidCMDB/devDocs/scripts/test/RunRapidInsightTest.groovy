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

import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.NameFileFilter
import org.apache.commons.io.filefilter.NotFileFilter
import build.RapidCmdbBuild
import test.BuildDirListener
import org.apache.commons.io.filefilter.AndFileFilter
import org.apache.commons.io.filefilter.PrefixFileFilter
import build.RapidInsightBuild

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


def watchConfig = [
        [new File("${workspaceDir}/RapidModules/RapidCMDB"), new File("${rootDir.absolutePath}/RapidSuite")],
        [new File("${workspaceDir}/RapidModules/RcmdbCommons"), new File("${rootDir.absolutePath}/RapidSuite")],
        [new File("${workspaceDir}/RapidModules/ext/database/groovy"), new File("${rootDir.absolutePath}/RapidSuite/grails-app/ext")],
        [new File("${workspaceDir}/RapidModules/ext/http/groovy"), new File("${rootDir.absolutePath}/RapidSuite/grails-app/ext")],
        [new File("${workspaceDir}/RapidModules/RapidInsight/grails-app"), new File("${rootDir.absolutePath}/RapidSuite/grails-app"), ["taglib"]],
        [new File("${workspaceDir}/RapidModules/RapidInsight/operations"), new File("${rootDir.absolutePath}/RapidSuite/operations")],
        [new File("${workspaceDir}/RapidModules/RapidInsight/grails-app/taglib"), new File("${rootDir.absolutePath}/RapidSuite/plugins/rapid-insight-0.1/grails-app/taglib")],
        [new File("${workspaceDir}/RapidModules/RapidInsight/test"), new File("${rootDir.absolutePath}/RapidSuite/test")],
        [new File("${workspaceDir}/Hyperic"), new File("${rootDir.absolutePath}/RapidSuite"), ["applications", "integration", "application.properties"]],
        [new File("${workspaceDir}/Hyperic/applications/RapidInsight"), new File("${rootDir.absolutePath}/RapidSuite")],
        [new File("${workspaceDir}/Hyperic/test/integration"), new File("${rootDir.absolutePath}/RapidSuite/test/integration")],
        [new File("${workspaceDir}/Apg"), new File("${rootDir.absolutePath}/RapidSuite"), ["applications", "application.properties"]],
        [new File("${workspaceDir}/Apg/applications/RapidInsight"), new File("${rootDir.absolutePath}/RapidSuite")]
]


def dirListeners = [];
def excludedDirs = [".svn", "reports"]




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
def envVars = getEnvVars(rootDir);
if (!new File("${rootDir.getCanonicalPath()}/RapidSuite/plugins/rapid-testing-0.1").exists())
{
    Properties props = new Properties();
    props.put("RI_UNIX", "false")
    props.put("RCMDB_UNIX", "true")
    props.put("RCMDB_WINDOWS", "true")
    props.put("APG", "true")
    props.put("OPENNMS", "true")
    props.put("HYPERIC", "true")
    props.put("NETCOOL", "true")
    props.put("SMARTS", "true")
    props.put("TEST", "true")
    props.put("JREDIR", "C:\\Program Files\\Java\\jre1.6.0_04")
    FileOutputStream out = new FileOutputStream("${workspaceDir}/Distribution/build.properties")
    props.store(out, "");
    out.close();

    def riBuild = new RapidInsightBuild();
    riBuild.main(["${workspaceDir}/Distribution/build.properties"] as String[]);
    ANT.unzip(src: "${workspaceDirFile.getAbsolutePath()}/RapidModules/RapidTesting/grails-rapid-testing-0.1.zip", dest: rootDir.getAbsolutePath() + "/RapidSuite/plugins/rapid-testing-0.1");
    ANT.move(toDir: "${rootDir.getAbsolutePath()}/RapidSuite/web-app/test") {
        ANT.fileset(dir: "${rootDir.getAbsolutePath()}/RapidSuite/plugins/rapid-testing-0.1/web-app/test");
    }
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


