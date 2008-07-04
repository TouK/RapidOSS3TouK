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
    def smartsBuild = new SmartsModuleBuild();
    def netcoolBuild = new NetcoolModuleBuild();
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

//    def buildSmartsModules() {
//
//        ant.delete(dir: env.distribution + "/RapidServer");
//        ant.delete(file: "$env.distribution/SmartsModule*.zip");
//        ant.delete(dir: env.rapid_ext_build);
//        ant.mkdir(dir: env.rapid_ext_build);
//
//        ant.javac(srcdir: "$env.rapid_ext/smarts/java", destdir: env.rapid_ext_build, excludes: getExcludedClasses()) {
//            ant.classpath(refid: "classpath");
//        }
//
//        ant.copy(todir: "$env.dist_rapid_cmdb/grails-app/ext") {
//            ant.fileset(dir: "$env.rapid_ext/smarts/groovy") {
//                if (!TEST) {
//                    ant.exclude(name: "**/test/**")
//                    ant.exclude(name: "**/*Test*")
//                }
//            };
//        }
//
//        ant.copy(file: "$env.rapid_cmdb_cvs/grails-app/domain/datasource/SmartsNotificationDatasource.groovy", toDir: "$env.dist_rapid_cmdb/grails-app/domain/datasource");
//        ant.copy(file: "$env.rapid_cmdb_cvs/grails-app/domain/datasource/SmartsTopologyDatasource.groovy", toDir: "$env.dist_rapid_cmdb/grails-app/domain/datasource");
//        ant.copy(file: "$env.rapid_cmdb_cvs/grails-app/domain/connection/SmartsConnection.groovy", toDir: "$env.dist_rapid_cmdb/grails-app/domain/connection");
//        ant.copy(file: "$env.rapid_cmdb_cvs/grails-app/controllers/connection/SmartsConnectionController.groovy", toDir: "$env.dist_rapid_cmdb/grails-app/controllers/connection");
//        ant.copy(file: "$env.rapid_cmdb_cvs/grails-app/controllers/datasource/SmartsNotificationDatasourceController.groovy", toDir: "$env.dist_rapid_cmdb/grails-app/controllers/datasource");
//        ant.copy(file: "$env.rapid_cmdb_cvs/grails-app/controllers/datasource/SmartsTopologyDatasourceController.groovy", toDir: "$env.dist_rapid_cmdb/grails-app/controllers/datasource");
//        ant.copy(todir: "$env.dist_rapid_cmdb/grails-app/views") {
//            ant.fileset(dir: "$env.rapid_cmdb_cvs/grails-app/views") {
//                ant.include(name: "smarts*/*")
//            }
//        }
//
//        ant.copy(file: "$env.rapid_cmdb_cvs/web-app/indexSmarts.gsp", tofile: "$env.dist_rapid_cmdb/web-app/index.gsp");
//
//        ant.jar(destfile: env.rapid_smarts_jar, basedir: env.rapid_ext_build, manifest: env.versionInBuild);
//        ant.copy(file: env.rapid_smarts_jar, toDir: env.dist_rapid_cmdb_lib);
//
//        def versionDate = getVersionWithDate();
//        def zipFileName = "$env.distribution/SmartsModule$versionDate" + ".zip"
//        ant.zip(destfile: zipFileName) {
//            ant.zipfileset(dir: "$env.distribution/RapidServer")
//        }
//    }

    def buildSample(sampleName) {
        ant.delete(dir: env.distribution + "/RapidServer");
        ant.delete(file: "${env.distribution}/${sampleName}*.zip");

        ant.copy(todir: "$env.dist_rapid_cmdb/scripts") {
            ant.fileset(dir: "$env.rapid_cmdb_cvs/scripts") {
                ant.include(name: "${sampleName}*.groovy")
            };
        }
        
        ant.copy(todir: "$env.dist_rapid_cmdb_modeler/scripts") {
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
        ant.unzip(src: "$env.distribution/RapidCMDB$versionDate" + ".zip", dest: env.distribution);
        ant.unzip(src: "$env.distribution/SmartsModule${smartsBuild.getVersionWithDate()}" + ".zip", dest: "$env.distribution/RapidServer");
        ant.unzip(src: "$env.distribution/NetcoolModule${netcoolBuild.getVersionWithDate()}" + ".zip", dest: "$env.distribution/RapidServer");
        ant.copy(todir: "$env.dist_rapid_cmdb/grails-app/domain") {
            ant.fileset(dir: "$env.rapid_cmdb_cvs/grails-app/domain") {
                ant.include(name: "*.groovy")
                ant.include(name: "test/*")
            }
        }
    }

    def buildRCMDB(){
         ant.copy(todir: "$env.dist_rapid_cmdb") {
            ant.fileset(file: "$env.rapid_cmdb_cvs/application.properties");
            ant.fileset(file: "$env.rapid_cmdb_cvs/rs.exe");
            ant.fileset(file: "$env.rapid_cmdb_commons_cvs/rsconsole.bat");
            ant.fileset(file: "$env.rapid_cmdb_commons_cvs/rsconsole.sh");
            ant.fileset(file: "$env.rapid_cmdb_cvs/rs.vmoptions");
            ant.fileset(file: "$env.rapid_cmdb_cvs/rs.sh");
        }
        ant.copy(todir: "$env.dist_rapid_cmdb/grails-app") {

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
        ant.copy(file: "$env.rapid_cmdb_cvs/scripts/HelloWorld.groovy", toDir: "$env.dist_rapid_cmdb/scripts");

        ant.copy(todir: "$env.dist_rapid_cmdb/operations") {
            ant.fileset(dir: "$env.rapid_cmdb_cvs/operations")
        }

        ant.copy(todir: "$env.dist_rapid_server/licenses") {
            ant.fileset(dir: "$env.rapid_cmdb_cvs/licenses")
        }

        ant.copy(todir: "$env.dist_rapid_cmdb/plugins") {
            ant.fileset(dir: "$env.rapid_cmdb_cvs/plugins")
        }
        ant.copy(todir: "$env.dist_rapid_cmdb/src") {
            ant.fileset(dir: "$env.rapid_cmdb_cvs/src") {
                if (!TEST) {
                    ant.exclude(name: "**/test/**")
                    ant.exclude(name: "**/*Test*")
                }
            }
        }
         ant.copy(todir: "$env.dist_rapid_cmdb/web-app") {
            ant.fileset(dir: "$env.rapid_cmdb_cvs/web-app") {
                ant.exclude(name: "indexSmarts.gsp")
            }
        }
        if (TEST) {
            ant.copy(todir: "$env.dist_rapid_cmdb/test") {
                ant.fileset(dir: "$env.rapid_cmdb_cvs/test")
            }
        }

        copyCommons(env.dist_rapid_cmdb);
    }

    def buildRCMDBModeler(){
        ant.copy(todir: "$env.dist_rapid_cmdb_modeler") {
            ant.fileset(file: "$env.rapid_cmdb_modeler_cvs/application.properties");
            ant.fileset(file: "$env.rapid_cmdb_modeler_cvs/rsmodeler.exe");
            ant.fileset(file: "$env.rapid_cmdb_commons_cvs/rsconsole.bat");
            ant.fileset(file: "$env.rapid_cmdb_commons_cvs/rsconsole.sh");
            ant.fileset(file: "$env.rapid_cmdb_modeler_cvs/rsmodeler.vmoptions");
            ant.fileset(file: "$env.rapid_cmdb_modeler_cvs/rsmodeler.sh");
            ant.fileset(file: env.invalidNames);
        }
        ant.copy(todir: "$env.dist_rapid_cmdb_modeler/grails-app") {
            ant.fileset(dir: "$env.rapid_cmdb_modeler_cvs/grails-app") {
                if (!TEST) {
                    ant.exclude(name: "**/test/**")
                    ant.exclude(name: "**/*Test*")
                }
            }
        }
        ant.copy(file: "$env.rapid_cmdb_cvs/scripts/HelloWorld.groovy", toDir: "$env.dist_rapid_cmdb_modeler/scripts");
      
        ant.copy(todir: "$env.dist_rapid_cmdb_modeler/src") {
            ant.fileset(dir: "$env.rapid_cmdb_modeler_cvs/src") {
                if (!TEST) {
                    ant.exclude(name: "**/test/**")
                    ant.exclude(name: "**/*Test*")
                }
            }
        }
         ant.copy(todir: "$env.dist_rapid_cmdb_modeler/web-app") {
            ant.fileset(dir: "$env.rapid_cmdb_modeler_cvs/web-app") 
        }
        if (TEST) {
            ant.copy(todir: "$env.dist_rapid_cmdb_modeler/test") {
                ant.fileset(dir: "$env.rapid_cmdb_modeler_cvs/test")
            }
        }
        copyCommons(env.dist_rapid_cmdb_modeler);
    }

    def copyCommons(toDir){
        ant.copy(todir: toDir) {
            ant.fileset(dir: "$env.rapid_cmdb_commons_cvs") {
                if (!TEST) {
                    ant.exclude(name: "**/*Test*")
                }             	
                ant.include(name: "**/grails-app/**");
                ant.include(name: "**/plugins/**");
                ant.include(name: "**/src/**");
                ant.include(name: "**/web-app/**");
                if (TEST) {
                    ant.include(name: "**/test/**")
                }                
            }
        }
    }

    def build() {
        clean();
        ant.copy(todir: "$env.dist_rapid_server", file: env.version)
        setVersionAndBuildNumber(env.versionInBuild);

        buildRCMDB();
        buildRCMDBModeler();

        buildDependent();
        copyDependentJars();
        unzipGrails();
        ant.copy(todir: "${env.distribution}/RapidServer/bin", file: "$env.rapid_cmdb_commons_cvs/rsbatch.sh")
        if (System.getProperty("os.name").indexOf("Windows") < 0)
        {
            def process = "dos2unix ${env.distribution}/RapidServer/bin/startGrails".execute()
            process = "dos2unix ${env.distribution}/RapidServer/bin/grails".execute()
            process = "dos2unix ${env.distribution}/RapidServer/bin/cygrails".execute()
            process = "dos2unix ${env.distribution}/RapidServer/bin/grails-debug".execute()
            process = "dos2unix ${env.distribution}/RapidServer/bin/rsbatch.sh".execute();
            process = "dos2unix ${env.distribution}/RapidServer/RapidCMDB/rs.sh".execute();
            process = "dos2unix ${env.distribution}/RapidServer/RapidCMDBModeler/rsmodeler.sh".execute();
        }
        def versionDate = getVersionWithDate();
        def zipFileName = "$env.distribution/RapidCMDB$versionDate" + ".zip"
        ant.zip(destfile: zipFileName) {
            ant.zipfileset(dir: "$env.distribution"){
            	ant.exclude(name:".project");
            }
        }
        netcoolBuild.run([]);
        smartsBuild.run([]);
        buildSample("Sample1");
        buildSample("Sample2");
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
        ant.copy(file: (String) classpath.getProperty("commons-betwixt-0_8_jar"), toDir: env.dist_rapid_cmdb_lib);
        ant.copy(file: (String) classpath.getProperty("commons-betwixt-0_8_jar"), toDir: env.dist_rapid_cmdb_modeler_lib);
        ant.copy(file: (String) classpath.getProperty("commons-digester-1_7_jar"), toDir: env.dist_rapid_cmdb_lib);
        ant.copy(file: (String) classpath.getProperty("commons-digester-1_7_jar"), toDir: env.dist_rapid_cmdb_modeler_lib);
        ant.copy(file: (String) classpath.getProperty("commons-configuration-1_2_jar"), toDir: env.dist_rapid_cmdb_lib);
        ant.copy(file: (String) classpath.getProperty("commons-configuration-1_2_jar"), toDir: env.dist_rapid_cmdb_modeler_lib);
        ant.copy(file: (String) classpath.getProperty("commons-collections-3_2_jar"), toDir: env.dist_rapid_cmdb_lib);
        ant.copy(file: (String) classpath.getProperty("commons-collections-3_2_jar"), toDir: env.dist_rapid_cmdb_modeler_lib);
        ant.copy(file: (String) classpath.getProperty("commons-codec-1_3_jar"), toDir: env.dist_rapid_cmdb_lib);
        ant.copy(file: (String) classpath.getProperty("commons-codec-1_3_jar"), toDir: env.dist_rapid_cmdb_modeler_lib);
        ant.copy(file: (String) classpath.getProperty("commons-httpclient-3_0_1_jar"), toDir: env.dist_rapid_cmdb_lib);
        ant.copy(file: (String) classpath.getProperty("commons-httpclient-3_0_1_jar"), toDir: env.dist_rapid_cmdb_modeler_lib);
        ant.copy(file: (String) classpath.getProperty("SNMP4J_jar"), toDir: env.dist_rapid_cmdb_lib);
        ant.copy(file: (String) classpath.getProperty("SNMP4J_jar"), toDir: env.dist_rapid_cmdb_modeler_lib);
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
        ant.delete(dir: env.distribution);
        ant.delete(dir: "$env.basedir/build");
    }

}