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
class RapidCmdbBuild extends Build {
	def UNIX = "Unix";
	def WINDOWS = "Windows";
	def osType; 
    def smartsBuild = new SmartsModuleBuild();
    def rapidInsightForNetcoolBuild = new RapidInsightForNetcoolBuild(this);
    def netcoolBuild = new NetcoolModuleBuild();
    def rapidUiBuild = new RapidUiPluginBuild();
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
        build();
        def versionDate = getVersionWithDate();
        ant.delete(dir: env.distribution + "/RapidServer");
        if (System.getProperty("os.name").indexOf("Windows") < 0){
        	ant.unzip(src: "$env.distribution/RapidCMDB_Unix$versionDate" + ".zip", dest: env.distribution);
        }
        else{
        	ant.unzip(src: "$env.distribution/RapidCMDB_Windows$versionDate" + ".zip", dest: env.distribution);
        }
        ant.unzip(src: "$env.distribution/SmartsModule${smartsBuild.getVersionWithDate()}" + ".zip", dest: "$env.distribution/RapidServer");
        //ant.unzip(src: "$env.distribution/NetcoolModule${netcoolBuild.getVersionWithDate()}" + ".zip", dest: "$env.distribution/RapidServer");
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

                // exclude Smarts classes
                ant.exclude(name: "controllers/datasource/Smarts*.groovy")
                ant.exclude(name: "controllers/connection/Smarts*.groovy")
                ant.exclude(name: "domain/datasource/Smarts*.groovy")
                ant.exclude(name: "domain/connection/Smarts*.groovy")
                ant.exclude(name: "views/smarts*/*")
                ant.exclude(name: "views/smarts*")
            }
        }
        ant.copy(file: "$env.rapid_cmdb_cvs/scripts/HelloWorld.groovy", toDir: "$env.dist_rapid_suite/scripts");

        ant.copy(todir: "$env.dist_rapid_suite/operations") {
            ant.fileset(dir: "$env.rapid_cmdb_cvs/operations")
        }

        ant.copy(todir: "$env.dist_rapid_server/licenses") {
            ant.fileset(dir: "$env.rapid_cmdb_cvs/licenses")
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
                ant.exclude(name: "adminSmarts.gsp")
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
      
        ant.copy(todir: "$env.dist_modeler/src") {
            ant.fileset(dir: "$env.rapid_cmdb_modeler_cvs/src") {
                if (!TEST) {
                    ant.exclude(name: "**/test/**")
                    ant.exclude(name: "**/*Test*")
                }
            }
        }
         ant.copy(todir: "$env.dist_modeler/web-app") {
            ant.fileset(dir: "$env.rapid_cmdb_modeler_cvs/web-app") 
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
                if (TEST && copyTests) {
                    ant.include(name: "**/test/**")
                }
                else if (TEST && !copyTests) {
                    ant.include(name: "**/*TestCase*")
                }
                else
                {
                    ant.exclude(name: "**/*Test*")                        
                }
                ant.include(name: "**/grails-app/**");
                ant.include(name: "**/plugins/**");
                ant.include(name: "**/src/**");
                ant.include(name: "**/web-app/**");
            }
        }
    }

    def build(){
    	clean();
    	buildPerOS(WINDOWS);
	    // save the zip file
    	ant.copy(todir: env.save) {
	        ant.fileset(dir: env.distribution) {
	            ant.include(name: "RapidCMDB*.zip")
	            ant.include(name: "RapidInsight*.zip")
	        }
	    }
        ant.delete(dir: env.distribution);
        ant.delete(dir: "$env.basedir/build");
        buildPerOS(UNIX);
        // bring back windows zips to distribution
    	ant.copy(todir: env.distribution) {
	        ant.fileset(dir: env.save) {
	            ant.include(name: "RapidCMDB*.zip")
	            ant.include(name: "RapidInsight*.zip")
	        }
	    }
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
        
        if(osType == UNIX){
        	ant.copy(todir: "${env.dist_rapid_server}/bin", file: "${env.rapid_cmdb_commons_cvs}/rsbatch.sh")
        }
        if ((System.getProperty("os.name").indexOf("Windows") < 0) && (osType == UNIX))
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
        netcoolBuild.run([]);
        rapidUiBuild.run([]);
        smartsBuild.run([]);
        buildSample("Sample1");
        buildSample("Sample2");
        rapidInsightForNetcoolBuild.run([]);
        return zipFileName;
    }

    def getVersionWithDate() {
        return "_$versionNo" + "_" + "$buildNo";
    }

    def setVersionAndBuildNumber() {
        def verFile = new File(env.versionInBuild);
        def verReader = verFile.newReader();
        versionNo = verReader.readLine().substring(9);

        buildNo = new java.text.SimpleDateFormat("yyMMddHH").format(new Date(System.currentTimeMillis()));
        verFile.append("\rBuild: " + buildNo);
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