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
    static void main(String []args){
		RapidCmdbBuild rapidCmdbBuilder = new RapidCmdbBuild();
		rapidCmdbBuilder.run(args);
	}

	def String getExcludedClasses(){
		if (!TEST){
			return "**/*Test*, **/*Mock*, rcomp/test/**";
		}
		return "";
	}

	def buildSmartsModules(){
        ant.delete(dir : env.distribution+"/RapidServer");
        ant.delete(dir : env.rapid_ext_build);
		ant.mkdir(dir : env.rapid_ext_build);

        ant.javac(srcdir : "$env.rapid_ext/smarts/java", destdir : env.rapid_ext_build, excludes: getExcludedClasses()){
			ant.classpath(refid : "classpath");
		}

		ant.copy(todir : "$env.dist_rapid_cmdb/src/groovy"){
			ant.fileset(dir : "$env.rapid_ext/smarts/groovy"){
                ant.exclude(name:"**/test/**")
                ant.exclude(name:"**/*Test*")
            };
        }

		ant.copy(file : "$env.rapid_cmdb_cvs/grails-app/domain/datasource/SmartsNotificationDatasource.groovy", toDir : "$env.dist_rapid_cmdb/grails-app/domain/datasource" );
		ant.copy(file : "$env.rapid_cmdb_cvs/grails-app/domain/datasource/SmartsTopologyDatasource.groovy", toDir : "$env.dist_rapid_cmdb/grails-app/domain/datasource" );
		ant.copy(file : "$env.rapid_cmdb_cvs/grails-app/domain/connection/SmartsConnection.groovy", toDir : "$env.dist_rapid_cmdb/grails-app/domain/connection" );
		ant.copy(file : "$env.rapid_cmdb_cvs/grails-app/controllers/connection/SmartsConnectionController.groovy", toDir : "$env.dist_rapid_cmdb/grails-app/controllers/connection" );
		ant.copy(file : "$env.rapid_cmdb_cvs/grails-app/controllers/datasource/SmartsNotificationDatasourceController.groovy", toDir : "$env.dist_rapid_cmdb/grails-app/controllers/datasource" );
		ant.copy(file : "$env.rapid_cmdb_cvs/grails-app/controllers/datasource/SmartsTopologyDatasourceController.groovy", toDir : "$env.dist_rapid_cmdb/grails-app/controllers/datasource" );

        ant.copy(file : "$env.rapid_cmdb_cvs/web-app/indexSmarts.gsp", tofile : "$env.dist_rapid_cmdb/web-app/index.gsp");

        ant.jar(destfile : env.rapid_ext_jar, basedir : env.rapid_ext_build);
        ant.copy(file : env.rapid_ext_jar, toDir : env.dist_rapid_cmdb_lib);

        ant.zip(destfile : "$env.distribution/SmartsModules.zip"){
            ant.zipfileset(dir : "$env.distribution");
        }
    }

	def build(){
		clean();
		ant.copy(todir : "$env.dist_rapid_cmdb"){
			ant.fileset(file : "$env.rapid_cmdb_cvs/application.properties");
			ant.fileset(file : "$env.rapid_cmdb_cvs/rs.bat");
			ant.fileset(file : "$env.rapid_cmdb_cvs/rs.sh");
		}

		ant.copy(todir : "$env.dist_rapid_cmdb/grails-app"){
			ant.fileset(dir : "$env.rapid_cmdb_cvs/grails-app"){
                ant.exclude(name:"**/test/**")
                ant.exclude(name:"**/*Test*")
                ant.exclude(name:"/domain/*.groovy")
                ant.exclude(name:"/controllers/*.groovy")
                ant.exclude(name:"**/scripts/*.groovy")

                // exclude Smarts classes
                ant.exclude(name:"/controllers/datasource/Smarts*.groovy")
                ant.exclude(name:"/controllers/connection/Smarts*.groovy")
                ant.exclude(name:"/domain/datasource/Smarts*.groovy")
                ant.exclude(name:"/domain/connection/Smarts*.groovy")
            }
		}

		ant.copy(file : "$env.rapid_cmdb_cvs/grails-app/scripts/HelloWorld.groovy", toDir : "$env.dist_rapid_cmdb/grails-app/scripts" );

		ant.copy(todir : "$env.dist_rapid_cmdb/lib"){
			ant.fileset(dir : "$env.rapid_cmdb_cvs/lib")
        }

		ant.copy(todir : "$env.dist_rapid_cmdb/licenses"){
			ant.fileset(dir : "$env.rapid_cmdb_cvs/licenses")
        }

		ant.copy(todir : "$env.dist_rapid_cmdb/plugins"){
			ant.fileset(dir : "$env.rapid_cmdb_cvs/plugins"){
                ant.exclude(name:"**/test/**")
                ant.exclude(name:"**/*Test*")
            }
        }
		ant.copy(todir : "$env.dist_rapid_cmdb/src"){
			ant.fileset(dir : "$env.rapid_cmdb_cvs/src"){
                ant.exclude(name:"**/test/**")
                ant.exclude(name:"**/*Test*")
            }
        }
		ant.copy(todir : "$env.dist_rapid_cmdb/web-app"){
			ant.fileset(dir : "$env.rapid_cmdb_cvs/web-app"){
                ant.exclude(name:"**/test/**")
                ant.exclude(name:"**/*Test*")
                ant.exclude(name:"indexSmarts.gsp")
            }
        }
		buildDependent();
		copyDependentJars();
		unzipGrails();
		def process = "dos2unix ${env.distribution}/RapidServer/bin/startGrails".execute()
		process = "dos2unix ${env.distribution}/RapidServer/bin/grails".execute()
		process = "dos2unix ${env.distribution}/RapidServer/bin/cygrails".execute()
		process = "dos2unix ${env.distribution}/RapidServer/bin/grails-debug".execute()
		process = "dos2unix ${env.distribution}/RapidServer/RapidCMDB/rs.sh".execute();
		ant.zip(destfile : "$env.distribution/RapidCMDB.zip"){
            ant.zipfileset(dir : "$env.distribution");
        }
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