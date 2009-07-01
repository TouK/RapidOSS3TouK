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

// SAMPLE OPTIONS FILE: (if omitted, defaults in constructor will be used)
// --------------------	
//RCMDB_UNIX=true
//RCMDB_WINDOWS=false
//SAMPLE1=false
//SAMPLE2=false
//ZIP=false
//MODELER=true
//TEST=false
//JREDIR=path to jre files on the build machine

class RapidCmdbBuild extends Build {
	boolean RCMDB_UNIX_OPT, RCMDB_WINDOWS_OPT, SAMPLE1_OPT, SAMPLE2_OPT, ZIP_OPT, MODELER_OPT, TEST_OPT;
	def JREDIR_OPT;    
    def setOptions(options){
    	if (options!=null){
    		RCMDB_UNIX_OPT = Boolean.parseBoolean(options.get("RCMDB_UNIX", "true"));
    		RCMDB_WINDOWS_OPT = Boolean.parseBoolean(options.get("RCMDB_WINDOWS", "false"));
    		SAMPLE1_OPT = Boolean.parseBoolean(options.get("SAMPLE1", "false"));
    		SAMPLE2_OPT = Boolean.parseBoolean(options.get("SAMPLE2", "false"));
    		ZIP_OPT = Boolean.parseBoolean(options.get("ZIP", "false"));
    		MODELER_OPT = Boolean.parseBoolean(options.get("MODELER", "false"));
    		TEST_OPT = Boolean.parseBoolean(options.get("TEST", "false"));
    		JREDIR_OPT = options.get("JREDIR");
    		if(JREDIR_OPT!=null) env.jreDir = JREDIR_OPT;
    	}    	
    }

    static def getTestOptions(){
       Properties options = new Properties();
       options.put("RCMDB_UNIX", "false")
       options.put("RCMDB_WINDOWS", "true")
       options.put("SAMPLE1", "false")
       options.put("SAMPLE2", "false")
       options.put("ZIP", "false")
       options.put("MODELER", "true")
       options.put("TEST", "true")
        return options;
    }
    
    static void main(String[] args) {
        def options;
        if (args.length > 0) {
            if(args[0] == "test"){
               options = getTestOptions();
            }
            else{
                options = Build.getBuildOptions(args[0]);
            }
        }
        RapidCmdbBuild rapidCmdbBuilder = new RapidCmdbBuild();
        rapidCmdbBuilder.setOptions(options);
        rapidCmdbBuilder.build();
    }
    
    def build() {
        clean();
        if(TEST_OPT) TEST = true;
        if(RCMDB_UNIX_OPT || RCMDB_WINDOWS_OPT) {
        	buildUnix();
//        	createPlugin(env.rapid_ui,[]);
        	if(RCMDB_WINDOWS_OPT){
        		addJreOnTopOfUnixAndZip("RCMDB");
        	}

        }
        if(TEST_OPT)copyForTesting();
        if(SAMPLE1_OPT) buildSample("Sample1");
        if(SAMPLE2_OPT) buildSample("Sample2");
        println "RCMDB Build Done";
    }
    
//    def String getExcludedClasses() {
//        if (!TEST) {
//            return "**/*Test*, **/*Mock*, **/test/**";
//        }
//        return "";
//    }

    def buildUnix() {
        ant.copy(todir: "$env.dist_rapid_suite", file: env.version)
        setVersionAndBuildNumber(env.versionInBuild);

        buildDependent();
        buildRCMDB();
        if(MODELER_OPT) buildRCMDBModeler();
//        copyDependentJars();
        unzipGrails();

        ant.copy(todir: "${env.dist_rapid_server}/bin", file: "${env.rapid_cmdb_commons_cvs}/rsbatch.sh")
//        if ((System.getProperty("os.name").indexOf("Windows") < 0))
//        {
//            def process = "dos2unix ${env.distribution}/RapidServer/bin/startGrails".execute()
//            process = "dos2unix ${env.distribution}/RapidServer/bin/grails".execute()
//            process = "dos2unix ${env.distribution}/RapidServer/bin/cygrails".execute()
//            process = "dos2unix ${env.distribution}/RapidServer/bin/grails-debug".execute()
//            process = "dos2unix ${env.distribution}/RapidServer/bin/rsbatch.sh".execute();
//            process = "dos2unix ${env.dist_rapid_suite}/rs.sh".execute();
//            process = "dos2unix ${env.dist_modeler}/rsmodeler.sh".execute();
//        }
        if (ZIP_OPT && RCMDB_UNIX_OPT){
	        def versionDate = getVersionWithDate();
	        def zipFileName = "$env.distribution/RCMDB_Unix$versionDate" + ".zip"
	        ant.zip(destfile: zipFileName) {
	            ant.zipfileset(dir: "$env.distribution", excludes:"**/*.vmoptions,**/*.exe,**/*.bat") {
	                ant.exclude(name: ".project");
	            }
	        }
        } 
    }

    def buildDependent() {
        new RapidCompBuild().build();
        new RapidCoreBuild().build();
        new RapidExtBuild().build();
    }

    def copyDependentJars(dirToCopyTo) {
        ant.copy(file: (String) classpath.getProperty("commons-transaction-1_2_jar"), toDir: dirToCopyTo);
        ant.copy(file: (String) classpath.getProperty("commons-betwixt-0_8_jar"), toDir: dirToCopyTo);
        ant.copy(file: (String) classpath.getProperty("commons-digester-1_7_jar"), toDir: dirToCopyTo);
        ant.copy(file: (String) classpath.getProperty("commons-configuration-1_2_jar"), toDir: dirToCopyTo);
        ant.copy(file: (String) classpath.getProperty("commons-collections-3_2_jar"), toDir: dirToCopyTo);
        ant.copy(file: (String) classpath.getProperty("commons-codec-1_3_jar"), toDir: dirToCopyTo);
        ant.copy(file: (String) classpath.getProperty("commons-httpclient-3_0_1_jar"), toDir: dirToCopyTo);
        ant.copy(file: (String) classpath.getProperty("SNMP4J_jar"), toDir: dirToCopyTo);
        ant.copy(file: (String) classpath.getProperty("STComm_jar"), toDir: dirToCopyTo);
        ant.copy(file: (String) classpath.getProperty("smack_jar"), toDir: dirToCopyTo);
        ant.copy(file: (String) classpath.getProperty("smackx_jar"), toDir: dirToCopyTo);
        ant.copy(file: (String) classpath.getProperty("smppapi-0_3_7_jar"), toDir: dirToCopyTo);
        ant.copy(file: (String) classpath.getProperty("joscar-0_9_3-patched_jar"), toDir: dirToCopyTo);
        
        ant.copy(file: "${env.third_party}/lib/jrobin/jrobin-1.5.9.1.jar", toDir: dirToCopyTo);
        ant.copy(file: "${env.third_party}/lib/jrobin/convertor-1.5.9.1.jar", toDir: dirToCopyTo);
        ant.copy(file: "${env.third_party}/lib/jrobin/inspector-1.5.9.1.jar", toDir: dirToCopyTo);

        ant.copy(file: "${env.third_party}/lib/javamail/mailapi.jar", toDir: dirToCopyTo);
        ant.copy(file: "${env.third_party}/lib/javamail/smtp.jar", toDir: dirToCopyTo);
        if (TEST) {
            ant.copy(file: "${env.third_party}/lib/javamail/pop3.jar", toDir: dirToCopyTo);            
            ant.copy(file: "${env.third_party}/lib/selenium/selenium-java-client-driver.jar", toDir: dirToCopyTo);
        }
        

        ant.copy(file : env.rapid_comp_jar, toDir : dirToCopyTo);
        ant.copy(file : env.rapid_core_jar, toDir : dirToCopyTo);
    }

    def unzipGrails() {
        ant.unzip(src: (String) classpath.getProperty("grails-1_0_3_zip"), dest: env.distribution);
        ant.copy(file: (String) classpath.getProperty("runner_jar"), toDir: env.distribution + "/RapidServer/lib");
        ant.copy(file: "${env.rapid_cmdb_cvs}/devDocs/grailsOverriddenFiles/files/src/war/WEB-INF/web2.4.template.xml", todir: "$env.dist_rapid_server/src/war/WEB-INF", overwrite: true)
        ant.copy(file: "${env.rapid_cmdb_cvs}/devDocs/grailsOverriddenFiles/scripts/RunApp.groovy", todir: "$env.dist_rapid_server/scripts", overwrite: true)
        ant.copy(file: "${env.rapid_cmdb_cvs}/devDocs/grailsOverriddenFiles/scripts/TestApp.groovy", todir: "$env.dist_rapid_server/scripts", overwrite: true)
        ant.copy(file: "${env.rapid_cmdb_cvs}/devDocs/grailsOverriddenFiles/scripts/PackagePlugin.groovy", todir: "$env.dist_rapid_server/scripts", overwrite: true)
        ant.copy(file: "${env.rapid_cmdb_cvs}/devDocs/grailsOverriddenFiles/scripts/Init.groovy", todir: "$env.dist_rapid_server/scripts", overwrite: true)
        ant.move(file: env.dist_rapid_server + "/LICENSE", tofile: env.dist_rapid_server + "/licenses/GRAILS_LICENSE");
        ant.delete(file: env.dist_rapid_server + "/INSTALL");
        ant.delete(file: env.dist_rapid_server + "/README");
    }

    def clean() {
        ant.delete(dir: env.distribution);
        ant.delete(dir: "$env.basedir/build");
    }

    def buildSample(sampleName) {
        if (versionNo == null) {
            ant.copy(todir: "$env.dist_rapid_suite", file: env.version)
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

    def buildRCMDB() {
        ant.copy(todir: "$env.dist_rapid_suite") {
            ant.fileset(file: "$env.rapid_cmdb_cvs/application.properties");
            ant.fileset(file: "$env.rapid_cmdb_cvs/rs.exe");
            ant.fileset(file: "$env.rapid_cmdb_commons_cvs/rsconsole.bat");
            ant.fileset(file: "$env.rapid_cmdb_cvs/rs.vmoptions");
            ant.fileset(file: "$env.rapid_cmdb_commons_cvs/rsconsole.sh");
            ant.fileset(file: "$env.rapid_cmdb_cvs/rs.sh");
            ant.fileset(file: env.invalidNames);
        }
        if (TEST) {
            ant.copy(todir: "$env.dist_rapid_suite") {
                ant.fileset(file: "$env.rapid_cmdb_cvs/test.sh");
                ant.fileset(file: "$env.rapid_cmdb_cvs/test.bat");
            }
        }
        ant.copy(todir: "$env.dist_rapid_suite/grails-app") {
            ant.fileset(dir: "$env.rapid_cmdb_cvs/grails-app") {
            	ant.exclude(name: "**/*Jira*")
            	ant.exclude(name: "**/jiraConnector/**")
                if (!TEST) {
                    ant.exclude(name: "**/test/**")
                    ant.exclude(name: "**/*Test*")
                    ant.exclude(name: "domain/*.groovy")
                }
            }
        }
        
        ant.copy(todir: "$env.dist_rapid_suite/scripts") {
            ant.fileset(dir: "$env.rapid_cmdb_cvs/scripts")
        }
        ant.copy(todir: "$env.dist_rapid_suite/operations") {
            ant.fileset(dir: "$env.rapid_cmdb_cvs/operations"){
            	ant.exclude(name: "**/*Jira*")
            }
        }

        ant.copy(todir: "$env.dist_rapid_server/licenses") {
            ant.fileset(dir: "$env.rapid_cmdb_cvs/licenses") {
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
//        if (osType == WINDOWS) {
//            ant.copy(todir: "$env.dist_rapid_server/jre") {
//                ant.fileset(dir: "$env.jreDir")
//            }
//        }
        copyDependentJars(env.dist_rapid_suite_lib);
        ant.copy(file: (String) classpath.getProperty("tools_jar"), toDir: env.dist_rapid_server_lib);
    }

    def buildRCMDBModeler() {
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
        copyDependentJars(env.dist_modeler_lib);
    }

    def copyCommons(toDir, copyTests) {
        ant.copy(todir: toDir) {
            ant.fileset(dir: "$env.rapid_cmdb_commons_cvs") {
                if (TEST && copyTests){
                    ant.include(name: "**/test/**")
                }
                else if (TEST && !copyTests)
                {
                    ant.include(name: "**/*TestCase*")
                }
                else {
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

        ant.copy(todir: "$toDir/plugins/searchable-0.5-SNAPSHOT/lib") {
            ant.fileset(dir: "$env.third_party/lib/compass")
        }
    }

//    def copyForTesting() {
//        TEST = true;
//        build();
//        def versionDate = getVersionWithDate();
//        ant.delete(dir: env.distribution + "/RapidServer");
//        if (System.getProperty("os.name").indexOf("Windows") < 0) {
//            ant.unzip(src: "$env.distribution/RapidCMDB_Unix$versionDate" + ".zip", dest: env.distribution);
//        }
//        else {
//            ant.unzip(src: "$env.distribution/RapidCMDB_Windows$versionDate" + ".zip", dest: env.distribution);
//        }
//
//        ant.copy(tofile: "$env.dist_rapid_suite/../conf/groovy-starter.conf", file: "${env.dev_docs}/groovy-starter-for-tests.conf", overwrite: "true")
//        ant.copy(todir: "$env.dist_rapid_suite/grails-app/domain") {
//            ant.fileset(dir: "$env.rapid_cmdb_cvs/grails-app/domain") {
//                ant.include(name: "*.groovy")
//                ant.include(name: "test/*")
//            }
//        }
//    }

}