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
        [new File("${workspaceDir}/RapidModules/RapidCMDBModeler"), new File("${rootDir.absolutePath}/Modeler")],
        [new File("${workspaceDir}/RapidModules/RcmdbCommons"), new File("${rootDir.absolutePath}/Modeler")]
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

if (!new File("${rootDir.getCanonicalPath()}/Modeler/plugins/rapid-testing-0.1").exists())
{
    Properties props = new Properties();
    props.put("RCMDB_UNIX", "true")
    props.put("RCMDB_WINDOWS", "true")
    props.put("MODELER", "true")
    props.put("TEST", "true")
    props.put("JREDIR", "C:\\Program Files\\Java\\jre1.6.0_04")
    FileOutputStream out = new FileOutputStream("${workspaceDir}/Distribution/build.properties")
    props.store(out, "");
    out.close();

    def rcmdbBuild = new RapidCmdbBuild();
    rcmdbBuild.main(["${workspaceDir}/Distribution/build.properties"] as String[]);
    ANT.unzip(src: "${workspaceDirFile.getAbsolutePath()}/RapidModules/RapidTesting/grails-rapid-testing-0.1.zip", dest: rootDir.getAbsolutePath() + "/Modeler/plugins/rapid-testing-0.1");
    ANT.move(toDir: "${rootDir.getAbsolutePath()}/Modeler/web-app/test") {
        ANT.fileset(dir: "${rootDir.getAbsolutePath()}/Modeler/plugins/rapid-testing-0.1/web-app/test");
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


