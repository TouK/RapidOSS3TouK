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
            }
        }
		buildDependent();
		copyDependentJars();
		unzipGrails();
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
    }

	def clean(){
		ant.delete(dir : env.distribution);
		ant.delete(dir : "$env.basedir/build");
	}

}