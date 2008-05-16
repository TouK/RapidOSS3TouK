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
class NetcoolModuleBuild extends Build{
	 
	def version = "$env.rapid_ext/netcool/netcoolModuleVersion.txt";
	def versionInBuild = "$env.dist_rapid_cmdb/netcoolModuleVersion.txt";
	 
    static void main(String []args){
    	NetcoolModuleBuild netcoolModuleBuild = new NetcoolModuleBuild();
    	netcoolModuleBuild.run(args);
	}

	def String getExcludedClasses(){
		if (!TEST){
			return "**/*Test*, **/*Mock*, **/test/**";
		}
		return "";
	}

    def build(){
        ant.delete(dir : env.distribution+"/RapidServer");
        ant.delete(file: "$env.distribution/NetcoolModule*.zip");

		ant.copy(todir : "$env.dist_rapid_cmdb/grails-app/ext"){
			ant.fileset(dir : "$env.rapid_ext/netcool/groovy"){
                if(!TEST){
                    ant.exclude(name:"**/test/**")
                    ant.exclude(name:"**/*Test*")
                }
            };
        }
		ant.copy(file : version, tofile : versionInBuild );
		ant.copy(file : "$env.rapid_cmdb_cvs/grails-app/domain/datasource/NetcoolDatasource.groovy", toDir : "$env.dist_rapid_cmdb/grails-app/domain/datasource" );
		ant.copy(file : "$env.rapid_cmdb_cvs/grails-app/domain/connection/NetcoolConnection.groovy", toDir : "$env.dist_rapid_cmdb/grails-app/domain/connection" );
		ant.copy(file : "$env.rapid_cmdb_cvs/grails-app/controllers/connection/NetcoolConnectionController.groovy", toDir : "$env.dist_rapid_cmdb/grails-app/controllers/connection" );
		ant.copy(file : "$env.rapid_cmdb_cvs/grails-app/controllers/datasource/NetcoolDatasourceController.groovy", toDir : "$env.dist_rapid_cmdb/grails-app/controllers/datasource" );
        ant.copy(todir : "$env.dist_rapid_cmdb/grails-app/views"){
			ant.fileset(dir : "$env.rapid_cmdb_cvs/grails-app/views"){
                ant.include(name:"netcool*/*")
            }
		}
        ant.copy(file : "$env.rapid_cmdb_cvs/web-app/indexNetcool.gsp", tofile : "$env.dist_rapid_cmdb/web-app/index.gsp");
        setVersionAndBuildNumber(versionInBuild);
        def versionDate = getVersionWithDate();
        def zipFileName = "$env.distribution/NetcoolModule$versionDate"+".zip"
        ant.zip(destfile : zipFileName){
            ant.zipfileset(dir : "$env.distribution/RapidServer")
        }
    }
}