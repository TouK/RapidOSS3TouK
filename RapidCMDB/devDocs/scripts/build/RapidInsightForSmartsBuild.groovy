package build

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Aug 11, 2008
 * Time: 2:08:43 PM
 */
//SUPPORTED TARGETS:
//------------------
//build			: builds RI4S for Unix AND Windows
//buildUnix		: builds RI4S for Unix
//buildWindows	: builds RI4S for Windows

class RapidInsightForSmartsBuild extends Build{
	def version = "$env.rapid_smarts/RI4SVersion.txt";
	def versionInBuild = "$env.dist_rapid_suite/RI4SVersion.txt";
	def rapidCMDBBuild = new RapidCmdbBuild();

    static void main(String[] args) {
        RapidInsightForSmartsBuild rapidInsightForSmartsBuilder = new RapidInsightForSmartsBuild();
        rapidInsightForSmartsBuilder.run(args);
    }

    def String getExcludedClasses() {
        if (!TEST) {
            return "**/*Test*, **/*Mock*, **/test/**";
        }
        return "";
    }

    def build(){
    	buildUnix();
    	addJreOnTopOfUnixAndZip();
    }

    def addJreOnTopOfUnixAndZip(){
    	ant.copy(todir: "$env.dist_rapid_server/jre") {
            ant.fileset(dir: "$env.jreDir")
        }
        def versionDate = getVersionWithDate();
        def zipFileName = "$env.distribution/RapidInsightForSmarts_Windows$versionDate" + ".zip"
        ant.zip(destfile: zipFileName) {
            ant.zipfileset(dir: "$env.distribution"){
            	ant.exclude(name:".project");
            	ant.exclude(name:"*.zip");
            }
        }
    }

    def buildWindows(){
    	rapidCMDBBuild.buildWindowsWithPlugins();
    	buildPerOS("Windows");
    }

    def buildUnix(){
    	rapidCMDBBuild.buildUnixWithPlugins();
    	buildPerOS("Unix");
    }

    def buildPerOS(osType) {
        ant.delete(dir:env.dist_rapid_server);

        def rapidCmdb = listFiles(new File(env.distribution), "RapidCMDB");
        ant.unzip(src: rapidCmdb.absolutePath, dest: env.distribution);
        ant.delete(dir:env.dist_modeler);

        def smartsPlugin = listFiles(new File(env.distribution), "grails-smarts");
        installPlugin(smartsPlugin, env.dist_rapid_suite, [Ant:ant], ["smarts_applications":"1"]);

        def rapidUiPlugin = listFiles(new File(env.distribution), "grails-rapid-ui");
        installPlugin(rapidUiPlugin, env.dist_rapid_suite, [Ant:ant], [:]);

        ant.copy(file : version, tofile : versionInBuild );
        setVersionAndBuildNumber(versionInBuild);
        def versionDate = getVersionWithDate();

//        def osType = "Unix";
//        if (rapidCmdb.getName().indexOf("Windows") > -1) osType = "Windows"
        def zipFileName = "${env.distribution}/RapidInsightForSmarts_$osType$versionDate" + ".zip"
        ant.zip(destfile: zipFileName) {
           ant.zipfileset(dir : "$env.distribution/RapidServer", prefix:"RapidServer")
        }
    }

    def listFiles(File rootDir, String regexp)
    {
        File file = null;
        rootDir.listFiles().each{File f->
            if(f.name.startsWith(regexp))
            {
                file = f;
                return;
            }
        }
        return file;
    }

}