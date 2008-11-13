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
	def riBuild = new RapidInsightBuild();

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
        def zipFileName = "$env.distribution/RI4S_Windows$versionDate" + ".zip"
        ant.zip(destfile: zipFileName) {
            ant.zipfileset(dir: "$env.distribution"){
            	ant.exclude(name:".project");
            	ant.exclude(name:"*.zip");
            }
        }
    }

    def buildWindows(){
    	riBuild.createDirectories();
    	buildPerOS("Windows");
    }

    def buildUnix(){
    	riBuild.createDirectories();
    	buildPerOS("Unix");
    }

    def buildPerOS(osType) {
        def smartsPlugin = listFiles(new File(env.distribution), "grails-smarts");
        installPlugin(smartsPlugin, env.dist_rapid_suite, [Ant:ant], ["smarts_applications":"1"]);

        ant.copy(file : version, tofile : versionInBuild );
        setVersionAndBuildNumber(versionInBuild);
        def versionDate = getVersionWithDate();

         ant.java(fork : "true", classname : "com.ifountain.comp.utils.JsCssCombiner"){
			ant.arg(value : "-file");
			ant.arg(value : "${env.dist_rapid_suite}/grails-app/views/layouts/indexLayout.gsp");
			ant.arg(value : "-applicationPath");
			ant.arg(value : "${env.dist_rapid_suite}/web-app");
			ant.arg(value : "-target");
			ant.arg(value : "${env.dist_rapid_suite}/web-app");
			ant.arg(value : "-suffix");
			ant.arg(value : "${versionDate}");
			ant.classpath(){
				ant.pathelement(location : "${env.dist_rapid_suite_lib}/comp.jar");
				ant.pathelement(location : "${env.dist_rapid_server_lib}/commons-cli-1.0.jar");
				ant.pathelement(location : "${env.dist_rapid_server_lib}/commons-io-1.4.jar");
				ant.pathelement(location : "${env.dist_rapid_server_lib}/log4j-1.2.15.jar");
			}
		}	
        ant.move(file : "${env.dist_rapid_suite}/web-app/indexLayout.gsp", todir : "${env.dist_rapid_suite}/grails-app/views/layouts" );
        def adminViews = ["httpConnection", "httpDatasource","databaseConnection","ldapConnection", "databaseDatasource",
                "singleTableDatabaseDatasource", "snmpConnection", "snmpDatasource", "script", "rsUser", "group", "smartsConnector", "smartsConnection",
        "smartsConnectionTemplate", "smartsNotificationDatasource", "smartsTopologyDatasource", "smartsNotificationConnector", "smartsTopologyConnector"]

        adminViews.each{
            ant.copy(file : "${env.dist_rapid_suite}/grails-app/views/layouts/adminLayout.gsp", toFile : "${env.dist_rapid_suite}/grails-app/views/layouts/${it}.gsp", overwrite:true );
        }


        def zipFileName = "${env.distribution}/RI4S_$osType$versionDate" + ".zip"
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