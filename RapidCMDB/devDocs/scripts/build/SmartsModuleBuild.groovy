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
 * User: Tugrul Kinikoglu
 * Date: Mar 18, 2008
 * Time: 4:45:18 PM
 */
class SmartsModuleBuild extends Build{
	 
	def version = "$env.rapid_ext/smarts/smartsModuleVersion.txt"; 
	def versionInBuild = "$env.dist_rapid_cmdb/smartsModuleVersion.txt";
	 
    static void main(String []args){
    	SmartsModuleBuild smartsModuleBuild = new SmartsModuleBuild();
    	smartsModuleBuild.run(args);
	}

	def String getExcludedClasses(){
		if (!TEST){
			return "**/*Test*, **/*Mock*, **/test/**";
		}
		return "";
	}

	def build(){
        ant.delete(dir : env.distribution+"/RapidServer");
        ant.delete(file: "$env.distribution/SmartsModule*.zip");
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

		ant.copy(file : version, tofile : versionInBuild );
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

        ant.copy(file : "$env.rapid_cmdb_cvs/web-app/adminSmarts.gsp", tofile : "$env.dist_rapid_cmdb/web-app/admin.gsp");

        ant.jar(destfile : env.rapid_smarts_jar, basedir : env.rapid_ext_build);
        ant.copy(file : env.rapid_smarts_jar, toDir : env.dist_rapid_cmdb_lib);

        setVersionAndBuildNumber(versionInBuild);
        def versionDate = getVersionWithDate();
        def zipFileName = "$env.distribution/SmartsModule$versionDate"+".zip"
        ant.zip(destfile : zipFileName){
            ant.zipfileset(dir : "$env.distribution/RapidServer")
        }
    }
}