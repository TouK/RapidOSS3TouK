package build;
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
 * User: Administrator
 * Date: Mar 18, 2008
 * Time: 4:45:18 PM
 * To change this template use File | Settings | File Templates.
 */
 
 
// SUPPORTED TARGETS:
// ------------------	
// build						: builds RapidCMDB for Unix AND Windows only, no plugins or modules included
// buildWithPlugins				: builds RapidCMDB for Unix AND Windows WITH plugins
// buildWithPluginsAndModules	: builds RapidCMDB for Unix AND Windows WITH plugins and modules (samples included)
// buildUnix					: builds RapidCMDB for Unix, no plugins or modules included
// buildUnixWithPlugins			: builds RapidCMDB for Unix WITH plugins
// buildWindows					: builds RapidCMDB for Windows, no plugins or modules included
// buildWindowsWithPlugins		: builds RapidCMDB for Windows WITH plugins
// buildModules					: build samples
    
class RapidCmdbBuild extends Build {
	def UNIX = "Unix";
	def WINDOWS = "Windows";
	def osType; 
    def rapidUiBuild = new RapidUiPluginBuild();
    def riBuild = new RapidInsightPluginBuild();

    static void main(String[] args) {
        RapidCmdbBuild rapidCmdbBuilder = new RapidCmdbBuild();
        rapidCmdbBuilder.run(args);
        
    }

    def String getExcludedClasses() {
        if (!TEST) {
            return "**/*Test*, **/*Mock*, **/test/**";
        }
        return "";
    }


    def buildSample(sampleName) {
    	if(versionNo == null) {
    		ant.copy(todir: "$env.dist_rapid_server", file: env.version)
    		setVersionAndBuildNumber(env.versionInBuild);
    	}
        ant.delete(dir: env.distribution + "/RapidServer");
        ant.delete(file: "${env.distribution}/${sampleName}*.zip");

        ant.copy(todir: "$env.dist_rapid_suite/scripts") {
            ant.fileset(dir: "$env.rapid_cmdb_cvs/scripts") {
                ant.include(name: "${sampleName}*.groovy")
            };
        }
        
        ant.copy(todir: "$env.dist_modeler/scripts") {
            ant.fileset(dir: "$env.rapid_cmdb_modeler_cvs/scripts") {
                ant.include(name: "${sampleName}Setup.groovy")
            };
        }        

        def versionDate = getVersionWithDate();
        def zipFileName = "${env.distribution}/${sampleName}${versionDate}" + ".zip"
        ant.zip(destfile: zipFileName) {
            ant.zipfileset(dir: "$env.distribution/RapidServer")
        }
    }

    def testBuild() {
        TEST = true;
        buildWithPluginsAndModules();
        def versionDate = getVersionWithDate();
        ant.delete(dir: env.distribution + "/RapidServer");
        if (System.getProperty("os.name").indexOf("Windows") < 0){
        	ant.unzip(src: "$env.distribution/RapidCMDB_Unix$versionDate" + ".zip", dest: env.distribution);
        }
        else{
        	ant.unzip(src: "$env.distribution/RapidCMDB_Windows$versionDate" + ".zip", dest: env.distribution);
        }

        ant.copy(tofile: "$env.dist_rapid_suite/../conf/groovy-starter.conf", file:"${env.dev_docs}/groovy-starter-for-tests.conf", overwrite:"true")
        ant.copy(todir: "$env.dist_rapid_suite/grails-app/domain") {
            ant.fileset(dir: "$env.rapid_cmdb_cvs/grails-app/domain") {
                ant.include(name: "*.groovy")
                ant.include(name: "test/*")
            }
        }
    }

    def buildRCMDB(){
         ant.copy(todir: "$env.dist_rapid_suite") {
            ant.fileset(file: "$env.rapid_cmdb_cvs/application.properties");
            ant.fileset(file: "$env.rapid_cmdb_cvs/rs.exe");
            ant.fileset(file: "$env.rapid_cmdb_commons_cvs/rsconsole.bat");
            ant.fileset(file: "$env.rapid_cmdb_cvs/rs.vmoptions");
            ant.fileset(file: "$env.rapid_cmdb_commons_cvs/rsconsole.sh");
            ant.fileset(file: "$env.rapid_cmdb_cvs/rs.sh");
        }
        ant.copy(todir: "$env.dist_rapid_suite/grails-app") {

            ant.fileset(dir: "$env.rapid_cmdb_cvs/grails-app") {
                if (!TEST) {
                    ant.exclude(name: "**/test/**")
                    ant.exclude(name: "**/*Test*")
                    ant.exclude(name: "domain/*.groovy")
                }
            }
        }
        ant.copy(file: "$env.rapid_cmdb_cvs/scripts/HelloWorld.groovy", toDir: "$env.dist_rapid_suite/scripts");
        ant.copy(file: "$env.rapid_cmdb_cvs/scripts/TransferLdapUsers.groovy", toDir: "$env.dist_rapid_suite/scripts");
        ant.copy(file: "$env.rapid_cmdb_cvs/scripts/RSFileReader.groovy", toDir: "$env.dist_rapid_suite/scripts");
        ant.copy(file: "$env.rapid_cmdb_cvs/scripts/ExportUtility.groovy", toDir: "$env.dist_rapid_suite/scripts");
        ant.copy(file: "$env.rapid_cmdb_cvs/scripts/ImportUtility.groovy", toDir: "$env.dist_rapid_suite/scripts");
        ant.copy(file: "$env.rapid_cmdb_cvs/scripts/SampleExportScript.groovy", toDir: "$env.dist_rapid_suite/scripts");
        ant.copy(file: "$env.rapid_cmdb_cvs/scripts/SampleImportScript.groovy", toDir: "$env.dist_rapid_suite/scripts");
        ant.copy(file: "$env.rapid_cmdb_cvs/scripts/modelCreator.groovy", toDir: "$env.dist_rapid_suite/scripts");

        ant.copy(todir: "$env.dist_rapid_suite/operations") {
            ant.fileset(dir: "$env.rapid_cmdb_cvs/operations")
        }

        ant.copy(todir: "$env.dist_rapid_server/licenses") {
            ant.fileset(dir: "$env.rapid_cmdb_cvs/licenses"){
            	ant.exclude(name: "grails_license.txt")
            	ant.exclude(name: "IFountain End User License Agreement.pdf")
            }
        }

        ant.copy(todir: "$env.dist_rapid_suite/plugins") {
            ant.fileset(dir: "$env.rapid_cmdb_cvs/plugins")
        }
        ant.copy(todir: "$env.dist_rapid_suite/src") {
            ant.fileset(dir: "$env.rapid_cmdb_cvs/src") {
                if (!TEST) {
                    ant.exclude(name: "**/test/**")
                    ant.exclude(name: "**/*Test*")
                }
            }
        }
         ant.copy(todir: "$env.dist_rapid_suite/web-app") {
            ant.fileset(dir: "$env.rapid_cmdb_cvs/web-app") {
                if (!TEST) {
                    ant.exclude(name: "**/test/**")
                    ant.exclude(name: "**/*Test*")
                }
            }
        }
        if (TEST) {
            ant.copy(todir: "$env.dist_rapid_suite/test") {
                ant.fileset(dir: "$env.rapid_cmdb_cvs/test")
            }
        }

        copyCommons(env.dist_rapid_suite, true);
        if(osType == WINDOWS){
	        ant.copy(todir: "$env.dist_rapid_server/jre") {
	            ant.fileset(dir: "$env.jreDir")
	        }
        }
    }

    def buildRCMDBModeler(){
        ant.copy(todir: "$env.dist_modeler") {
            ant.fileset(file: "$env.rapid_cmdb_modeler_cvs/application.properties");
            ant.fileset(file: "$env.rapid_cmdb_modeler_cvs/rsmodeler.exe");
            ant.fileset(file: "$env.rapid_cmdb_commons_cvs/rsconsole.bat");
            ant.fileset(file: "$env.rapid_cmdb_modeler_cvs/rsmodeler.vmoptions");
            ant.fileset(file: "$env.rapid_cmdb_commons_cvs/rsconsole.sh");
            ant.fileset(file: "$env.rapid_cmdb_modeler_cvs/rsmodeler.sh");
            ant.fileset(file: env.invalidNames);
        }
        ant.copy(todir: "$env.dist_modeler/grails-app") {
            ant.fileset(dir: "$env.rapid_cmdb_modeler_cvs/grails-app") {
                if (!TEST) {
                    ant.exclude(name: "**/test/**")
                    ant.exclude(name: "**/*Test*")
                }
            }
        }
        ant.copy(file: "$env.rapid_cmdb_cvs/scripts/HelloWorld.groovy", toDir: "$env.dist_modeler/scripts");
        ant.copy(file: "$env.rapid_cmdb_modeler_cvs/scripts/ModelHelper.groovy", toDir: "$env.dist_modeler/scripts");
        ant.copy(file: "$env.rapid_cmdb_cvs/scripts/TransferLdapUsers.groovy", toDir: "$env.dist_modeler/scripts");
        ant.copy(todir: "$env.dist_modeler/src") {
            ant.fileset(dir: "$env.rapid_cmdb_modeler_cvs/src") {
                if (!TEST) {
                    ant.exclude(name: "**/test/**")
                    ant.exclude(name: "**/*Test*")
                }
            }
        }
        ant.copy(todir: "$env.dist_modeler/web-app") {
            ant.fileset(dir: "$env.rapid_cmdb_modeler_cvs/web-app") {
                if (!TEST) {
                    ant.exclude(name: "**/test/**")
                    ant.exclude(name: "**/*Test*")
                }
            }
        }
        if (TEST) {
            ant.copy(todir: "$env.dist_modeler/test") {
                ant.fileset(dir: "$env.rapid_cmdb_modeler_cvs/test")
            }
        }
        copyCommons(env.dist_modeler, false);
    }

    def copyCommons(toDir, copyTests){
        ant.copy(todir: toDir) {
            ant.fileset(dir: "$env.rapid_cmdb_commons_cvs") {                            
                if (TEST && copyTests)
                {
                   ant.include(name: "**/test/**")                                       
                }
                else if(TEST && !copyTests)
                {
                   ant.include(name: "**/*TestCase*")  
                }
                else{
                   ant.exclude(name: "**/test/**")   
                   ant.exclude(name: "**/*Test*")   
                }
                ant.include(name: "**/grails-app/**");
                ant.include(name: "**/plugins/**");
                ant.include(name: "**/src/**");
                ant.include(name: "**/operations/**");
                ant.include(name: "**/web-app/**");
            }
        }
    }

    def build(){
    	clean();
    	buildPerOS(UNIX);
    	addJreOnTopOfUnixAndZip();
    }
    
    def buildWithPlugins(){
    	clean();
    	buildPerOSWithPlugins(UNIX);
    	addJreOnTopOfUnixAndZip();
    }    
    
    def buildWithPluginsAndModules(){
    	clean();
    	buildPerOSWithPlugins(UNIX);
    	addJreOnTopOfUnixAndZip();
    	buildModules();
    }     
    
    def addJreOnTopOfUnixAndZip(){
    	ant.copy(todir: "$env.dist_rapid_server/jre") {
            ant.fileset(dir: "$env.jreDir")
        }
        def versionDate = getVersionWithDate();
        def zipFileName = "$env.distribution/RapidCMDB_Windows$versionDate" + ".zip"
        ant.zip(destfile: zipFileName) {
            ant.zipfileset(dir: "$env.distribution"){
            	ant.exclude(name:".project");
            	ant.exclude(name:"*.zip");
            	ant.exclude(name:"**/temp/**");
            }
        }
    }
        
    def buildUnix(){
    	clean();
    	buildPerOS(UNIX);
    }
    
    def buildUnixWithPlugins(){
    	clean();
    	buildPerOSWithPlugins(UNIX);
    }
    
    def buildWindows(){
    	clean();
    	buildPerOS(WINDOWS);
    }
    
    def buildWindowsWithPlugins(){
    	clean();
    	buildPerOSWithPlugins(WINDOWS);
    }
    
    def buildPerOS(type) {
        osType = type;
        ant.copy(todir: "$env.dist_rapid_server", file: env.version)
        setVersionAndBuildNumber(env.versionInBuild);

        buildRCMDB();
        buildRCMDBModeler();

        buildDependent();
        copyDependentJars();
        unzipGrails();
        
       	ant.copy(todir: "${env.dist_rapid_server}/bin", file: "${env.rapid_cmdb_commons_cvs}/rsbatch.sh")
        if ((System.getProperty("os.name").indexOf("Windows") < 0))
        {
            def process = "dos2unix ${env.distribution}/RapidServer/bin/startGrails".execute()
            process = "dos2unix ${env.distribution}/RapidServer/bin/grails".execute()
            process = "dos2unix ${env.distribution}/RapidServer/bin/cygrails".execute()
            process = "dos2unix ${env.distribution}/RapidServer/bin/grails-debug".execute()
            process = "dos2unix ${env.distribution}/RapidServer/bin/rsbatch.sh".execute();
            process = "dos2unix ${env.dist_rapid_suite}/rs.sh".execute();
            process = "dos2unix ${env.dist_modeler}/rsmodeler.sh".execute();
        }
        def versionDate = getVersionWithDate();
        def zipFileName = "$env.distribution/RapidCMDB_$osType$versionDate" + ".zip"
        ant.zip(destfile: zipFileName) {
            ant.zipfileset(dir: "$env.distribution"){
            	ant.exclude(name:".project");
            }
        }

        return zipFileName;
    }
    
    def buildPerOSWithPlugins(type){
    	def zipFileName = buildPerOS(type);
    	buildAdditionalPlugins();
    	return zipFileName;
    }
    
    def buildAdditionalPlugins(){
    	rapidUiBuild.run([]);
    	riBuild.run([]);
    }
    
    def buildModules(){
        buildSample("Sample1");
        buildSample("Sample2");    	
    }

    def getVersionWithDate() {
        return "_$versionNo" + "_" + "$buildNo";
    }

    def buildDependent() {
        new RapidCompBuild().run([]);
        new RapidCoreBuild().run([]);
        new RapidExtBuild().run([]);
    }

    def copyDependentJars() {
        ant.copy(file: (String) classpath.getProperty("commons-betwixt-0_8_jar"), toDir: env.dist_rapid_suite_lib);
        ant.copy(file: (String) classpath.getProperty("commons-betwixt-0_8_jar"), toDir: env.dist_modeler_lib);
        ant.copy(file: (String) classpath.getProperty("commons-digester-1_7_jar"), toDir: env.dist_rapid_suite_lib);
        ant.copy(file: (String) classpath.getProperty("commons-digester-1_7_jar"), toDir: env.dist_modeler_lib);
        ant.copy(file: (String) classpath.getProperty("commons-configuration-1_2_jar"), toDir: env.dist_rapid_suite_lib);
        ant.copy(file: (String) classpath.getProperty("commons-configuration-1_2_jar"), toDir: env.dist_modeler_lib);
        ant.copy(file: (String) classpath.getProperty("commons-collections-3_2_jar"), toDir: env.dist_rapid_suite_lib);
        ant.copy(file: (String) classpath.getProperty("commons-collections-3_2_jar"), toDir: env.dist_modeler_lib);
        ant.copy(file: (String) classpath.getProperty("commons-codec-1_3_jar"), toDir: env.dist_rapid_suite_lib);
        ant.copy(file: (String) classpath.getProperty("commons-codec-1_3_jar"), toDir: env.dist_modeler_lib);
        ant.copy(file: (String) classpath.getProperty("commons-httpclient-3_0_1_jar"), toDir: env.dist_rapid_suite_lib);
        ant.copy(file: (String) classpath.getProperty("commons-httpclient-3_0_1_jar"), toDir: env.dist_modeler_lib);
        ant.copy(file: (String) classpath.getProperty("SNMP4J_jar"), toDir: env.dist_rapid_suite_lib);
        ant.copy(file: (String) classpath.getProperty("SNMP4J_jar"), toDir: env.dist_modeler_lib);
        ant.copy(file: (String) classpath.getProperty("tools_jar"), toDir: env.dist_rapid_server_lib);
    }

    def unzipGrails() {
        ant.unzip(src: (String) classpath.getProperty("grails-1_0_3_zip"), dest: env.distribution);
        ant.copy(file: (String) classpath.getProperty("runner_jar"), toDir: env.distribution + "/RapidServer/lib");
        ant.copy(file:"${env.rapid_cmdb_cvs}/devDocs/grailsOverriddenFiles/scripts/RunApp.groovy", todir: "$env.dist_rapid_server/scripts", overwrite:true)
        ant.copy(file:"${env.rapid_cmdb_cvs}/devDocs/grailsOverriddenFiles/scripts/TestApp.groovy", todir: "$env.dist_rapid_server/scripts", overwrite:true)
        ant.copy(file:"${env.rapid_cmdb_cvs}/devDocs/grailsOverriddenFiles/scripts/PackagePlugin.groovy", todir: "$env.dist_rapid_server/scripts", overwrite:true)
        ant.copy(file:"${env.rapid_cmdb_cvs}/devDocs/grailsOverriddenFiles/scripts/Init.groovy", todir: "$env.dist_rapid_server/scripts", overwrite:true)
        ant.move(file: env.dist_rapid_server + "/LICENSE", tofile: env.dist_rapid_server + "/licenses/GRAILS_LICENSE");
        ant.delete(file: env.dist_rapid_server + "/INSTALL");
        ant.delete(file: env.dist_rapid_server + "/README");
    }

    def clean() {
    	ant.delete(dir: env.save);
        ant.delete(dir: env.distribution);
        ant.delete(dir: "$env.basedir/build");
    }

}