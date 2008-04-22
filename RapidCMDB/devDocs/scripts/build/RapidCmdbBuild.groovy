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
class RapidCmdbBuild extends Build{
	 
	def versionNo; 
	def buildNo; 
	 
    static void main(String []args){
		RapidCmdbBuild rapidCmdbBuilder = new RapidCmdbBuild();
		rapidCmdbBuilder.run(args);
	}

	def String getExcludedClasses(){
		if (!TEST){
			return "**/*Test*, **/*Mock*, **/test/**";
		}
		return "";
	}

	def buildSmartsModules(){
        ant.delete(dir : env.distribution+"/RapidServer");
        ant.delete(file: "$env.distribution/SmartsModules.zip");
        ant.delete(dir : env.rapid_ext_build);
		ant.mkdir(dir : env.rapid_ext_build);

        ant.javac(srcdir : "$env.rapid_ext/smarts/java", destdir : env.rapid_ext_build, excludes: getExcludedClasses()){
			ant.classpath(refid : "classpath");
		}

		ant.copy(todir : "$env.dist_rapid_cmdb/grails-app/ext"){
			ant.fileset(dir : "$env.rapid_ext/smarts/groovy"){
                if(!TEST){
                    ant.exclude(name:"**/test/**")
                    ant.exclude(name:"**/*Test*")
                }
            };
        }

		ant.copy(file : "$env.rapid_cmdb_cvs/grails-app/domain/datasource/SmartsNotificationDatasource.groovy", toDir : "$env.dist_rapid_cmdb/grails-app/domain/datasource" );
		ant.copy(file : "$env.rapid_cmdb_cvs/grails-app/domain/datasource/SmartsTopologyDatasource.groovy", toDir : "$env.dist_rapid_cmdb/grails-app/domain/datasource" );
		ant.copy(file : "$env.rapid_cmdb_cvs/grails-app/domain/connection/SmartsConnection.groovy", toDir : "$env.dist_rapid_cmdb/grails-app/domain/connection" );
		ant.copy(file : "$env.rapid_cmdb_cvs/grails-app/controllers/connection/SmartsConnectionController.groovy", toDir : "$env.dist_rapid_cmdb/grails-app/controllers/connection" );
		ant.copy(file : "$env.rapid_cmdb_cvs/grails-app/controllers/datasource/SmartsNotificationDatasourceController.groovy", toDir : "$env.dist_rapid_cmdb/grails-app/controllers/datasource" );
		ant.copy(file : "$env.rapid_cmdb_cvs/grails-app/controllers/datasource/SmartsTopologyDatasourceController.groovy", toDir : "$env.dist_rapid_cmdb/grails-app/controllers/datasource" );
        ant.copy(todir : "$env.dist_rapid_cmdb/grails-app/views"){
			ant.fileset(dir : "$env.rapid_cmdb_cvs/grails-app/views"){
                ant.include(name:"smarts*/*")
            }
		}

        ant.copy(file : "$env.rapid_cmdb_cvs/web-app/indexSmarts.gsp", tofile : "$env.dist_rapid_cmdb/web-app/index.gsp");

        ant.jar(destfile : env.rapid_ext_jar, basedir : env.rapid_ext_build);
        ant.copy(file : env.rapid_ext_jar, toDir : env.dist_rapid_cmdb_lib);

        ant.zip(destfile : "$env.distribution/SmartsModules.zip"){
            ant.zipfileset(dir : "$env.distribution/RapidServer")
        }
    }
    def buildNetcoolModules(){
        ant.delete(dir : env.distribution+"/RapidServer");
        ant.delete(file: "$env.distribution/NetcoolModules.zip");

		ant.copy(todir : "$env.dist_rapid_cmdb/grails-app/ext"){
			ant.fileset(dir : "$env.rapid_ext/netcool/groovy"){
                if(!TEST){
                    ant.exclude(name:"**/test/**")
                    ant.exclude(name:"**/*Test*")
                }
            };
        }

		ant.copy(file : "$env.rapid_cmdb_cvs/grails-app/domain/datasource/NetcoolDatasource.groovy", toDir : "$env.dist_rapid_cmdb/grails-app/domain/datasource" );
		ant.copy(file : "$env.rapid_cmdb_cvs/grails-app/domain/connection/NetcoolConnection.groovy", toDir : "$env.dist_rapid_cmdb/grails-app/domain/connection" );
		ant.copy(file : "$env.rapid_cmdb_cvs/grails-app/controllers/connection/NetcoolConnectionController.groovy", toDir : "$env.dist_rapid_cmdb/grails-app/controllers/connection" );
		ant.copy(file : "$env.rapid_cmdb_cvs/grails-app/controllers/datasource/NetcoolDatasourceController.groovy", toDir : "$env.dist_rapid_cmdb/grails-app/controllers/datasource" );
        ant.copy(todir : "$env.dist_rapid_cmdb/grails-app/views"){
			ant.fileset(dir : "$env.rapid_cmdb_cvs/grails-app/views"){
                ant.include(name:"netcool*/*")
            }
		}

//        ant.copy(file : "$env.rapid_cmdb_cvs/web-app/indexSmarts.gsp", tofile : "$env.dist_rapid_cmdb/web-app/index.gsp");
        ant.zip(destfile : "$env.distribution/NetcoolModules.zip"){
            ant.zipfileset(dir : "$env.distribution/RapidServer")
        }
    }

    def testBuild(){
        TEST = true;
        def zipFileName = build();
        ant.delete(dir : env.distribution+"/RapidServer");
        ant.unzip(src : zipFileName, dest : env.distribution);
        ant.unzip(src : "$env.distribution/SmartsModules.zip", dest : "$env.distribution/RapidServer");
        ant.unzip(src : "$env.distribution/NetcoolModules.zip", dest : "$env.distribution/RapidServer");
    }

	def build(){
		clean();
		ant.copy(todir : "$env.dist_rapid_cmdb"){
			ant.fileset(file : "$env.rapid_cmdb_cvs/application.properties");
			ant.fileset(file : "$env.rapid_cmdb_cvs/rs.bat");
			ant.fileset(file : "$env.rapid_cmdb_cvs/rs.sh");
			ant.fileset(file : "$env.rapid_cmdb_cvs/rsbatch.sh");
			ant.fileset(file : env.version);
		}
		
		setVersionAndBuildNumber();

		ant.copy(todir : "$env.dist_rapid_cmdb/grails-app"){
			ant.fileset(dir : "$env.rapid_cmdb_cvs/grails-app"){
                ant.exclude(name:"**/test/**")
                ant.exclude(name:"**/*Test*")
                ant.exclude(name:"domain/*.groovy")
                ant.exclude(name:"controllers/*.groovy")

                // exclude Smarts classes
                ant.exclude(name:"controllers/datasource/Smarts*.groovy")
                ant.exclude(name:"controllers/connection/Smarts*.groovy")
                ant.exclude(name:"domain/datasource/Smarts*.groovy")
                ant.exclude(name:"domain/connection/Smarts*.groovy")
                ant.exclude(name:"views/smarts*/*")
                ant.exclude(name:"views/smarts*")

                // exclude Netcool classes
                ant.exclude(name:"controllers/datasource/Netcool*.groovy")
                ant.exclude(name:"controllers/connection/Netcool*.groovy")
                ant.exclude(name:"domain/datasource/Netcool*.groovy")
                ant.exclude(name:"domain/connection/Netcool*.groovy")
                ant.exclude(name:"views/netcool*/*")
                ant.exclude(name:"views/netcool*")
            }
		}

		ant.copy(file : "$env.rapid_cmdb_cvs/scripts/HelloWorld.groovy", toDir : "$env.dist_rapid_cmdb/scripts" );

		ant.copy(todir : "$env.dist_rapid_cmdb/lib"){
			ant.fileset(dir : "$env.rapid_cmdb_cvs/lib")
        }

		ant.copy(todir : "$env.dist_rapid_cmdb/licenses"){
			ant.fileset(dir : "$env.rapid_cmdb_cvs/licenses")
        }

		ant.copy(todir : "$env.dist_rapid_cmdb/plugins"){
			ant.fileset(dir : "$env.rapid_cmdb_cvs/plugins"){
                if(!TEST){
                    ant.exclude(name:"**/test/**")
                    ant.exclude(name:"**/*Test*")
                }
            }
        }
		ant.copy(todir : "$env.dist_rapid_cmdb/src"){
			ant.fileset(dir : "$env.rapid_cmdb_cvs/src"){
//                ant.exclude(name:"**/java/**")
                if(!TEST){
                    ant.exclude(name:"**/test/**")
                    ant.exclude(name:"**/*Test*")
                }
            }
        }
		ant.copy(todir : "$env.dist_rapid_cmdb/web-app"){
			ant.fileset(dir : "$env.rapid_cmdb_cvs/web-app"){
                if(!TEST){
                    ant.exclude(name:"**/test/**")
                    ant.exclude(name:"**/*Test*")
                }
                ant.exclude(name:"indexSmarts.gsp")
            }
        }
        if(TEST){
           ant.copy(todir : "$env.dist_rapid_cmdb/test"){
			    ant.fileset(dir : "$env.rapid_cmdb_cvs/test")
            }
        }

		buildDependent();
//        ant.delete(dir : env.rapid_cmdb_build);
//		ant.mkdir(dir : env.rapid_cmdb_build);
//		ant.javac(srcdir : env.rapid_cmdb_src, destdir : env.rapid_cmdb_build, excludes: getExcludedClasses()){
//			ant.classpath(refid : "classpath");
//		}
//		ant.jar(destfile : env.rapid_cmdb_jar, basedir : env.rapid_cmdb_build, manifest : env.versionInBuild);
//        ant.copy(file : env.rapid_cmdb_jar, toDir : env.dist_rapid_cmdb_lib);
		copyDependentJars();
		unzipGrails();
		if(System.getProperty("os.name").indexOf("Windows") < 0)
        {
            def process = "dos2unix ${env.distribution}/RapidServer/bin/startGrails".execute()
            process = "dos2unix ${env.distribution}/RapidServer/bin/grails".execute()
            process = "dos2unix ${env.distribution}/RapidServer/bin/cygrails".execute()
            process = "dos2unix ${env.distribution}/RapidServer/bin/grails-debug".execute()
            process = "dos2unix ${env.distribution}/RapidServer/RapidCMDB/rs.sh".execute();
            process = "dos2unix ${env.distribution}/RapidServer/RapidCMDB/rsbatch.sh".execute();
        }
        def versionDate = getVersionWithDate();
        def zipFileName = "$env.distribution/RapidCMDB$versionDate"+".zip"
		ant.zip(destfile : zipFileName){
            ant.zipfileset(dir : "$env.distribution");
        }
        buildSmartsModules();
        buildNetcoolModules();
        return zipFileName;
	}

	def getVersionWithDate(){
        return "_$versionNo" + "_" + "$buildNo";
    }
	
	def setVersionAndBuildNumber(){
		def verFile = new File (env.versionInBuild);
		def verReader =verFile.newReader();
		versionNo = verReader.readLine().substring(9);
		
		buildNo =  new java.text.SimpleDateFormat("yyMMddHH").format(new Date(System.currentTimeMillis()));
		verFile.append("\nBuild: " + buildNo);
	}

    def buildDependent(){
        new RapidCompBuild().run ([]);
        new RapidCoreBuild().run ([]);
        new RapidExtBuild().run ([]);
    }

	def copyDependentJars(){
		ant.copy(file : (String)classpath.getProperty("commons-betwixt-0_8_jar"), toDir : env.dist_rapid_cmdb_lib );
		ant.copy(file : (String)classpath.getProperty("commons-digester-1_7_jar"), toDir : env.dist_rapid_cmdb_lib);
		ant.copy(file : (String)classpath.getProperty("commons-configuration-1_2_jar"), toDir : env.dist_rapid_cmdb_lib );
        ant.copy(file : (String)classpath.getProperty("commons-collections-3_2_jar"), toDir : env.dist_rapid_cmdb_lib );
		ant.copy(file : (String)classpath.getProperty("commons-codec-1_3_jar"), toDir : env.dist_rapid_cmdb_lib);
		ant.copy(file : (String)classpath.getProperty("commons-httpclient-3_0_1_jar"), toDir : env.dist_rapid_cmdb_lib);
	}

    def unzipGrails(){
        ant.unzip(src : (String)classpath.getProperty("grails-1_0_1_zip"), dest : env.distribution);
        ant.copy(file : (String)classpath.getProperty("runner_jar"), toDir : env.distribution + "/RapidServer/lib");
    }

	def clean(){
		ant.delete(dir : env.distribution);
		ant.delete(dir : "$env.basedir/build");
	}

}