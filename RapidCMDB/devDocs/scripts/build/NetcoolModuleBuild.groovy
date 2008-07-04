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
	 

    static void main(String []args){
    	NetcoolModuleBuild netcoolModuleBuild = new NetcoolModuleBuild();
    	netcoolModuleBuild.run(args);
	}

    def build(){
        ant.copy(file:"$env.rapid_cmdb_cvs/devDocs/groovy-starter.conf", todir:"${env.dist_rapid_server}/conf")
        ant.exec(executable:"${new File("${env.dist_rapid_cmdb}/rsconsole.bat").absolutePath}", dir:"${new File("${env.dist_rapid_cmdb}").absolutePath}")
        {
            ant.arg(value:"compile")
            System.getenv().each{envKey, envVal->
                ant.env(key:"${envKey}", value:"${envVal}");
            }
            ant.env(key:"RS_HOME", value:"${new File(env.dist_rapid_server).absolutePath}");
        }
        ant.exec(executable:"${new File("${env.dist_rapid_cmdb}/rsconsole.bat").absolutePath}", dir:"${new File("${env.rapid_netcool}").absolutePath}")
        {
            ant.arg(value:"package-plugin")
            ant.arg(value:"-Dplugin.resources=\"applications/**,operations/**\"")
            System.getenv().each{envKey, envVal->
                ant.env(key:"${envKey}", value:"${envVal}");    
            }
            ant.env(key:"RS_HOME", value:"${new File(env.dist_rapid_server).absolutePath}");

        }
        ant.move(todir:"${env.distribution}"){
            ant.fileset(file: "$env.rapid_netcool/*netcool*.zip");
        }
    }

}