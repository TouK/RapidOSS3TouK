/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
package build
/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 4, 2008
 * Time: 10:35:09 AM
 * To change this template use File | Settings | File Templates.
 */
class OpenNmsBuild extends Build{
	def version = "$env.rapid_opennms/RIOpenNmsVersion.txt";
	def versionInBuild = "$env.dist_modules_rapid_suite/RIOpenNmsVersion.txt"; 
	
   static void main(String[] args) {
        OpenNmsBuild openNmsBuild = new OpenNmsBuild();
        openNmsBuild.run(args);
    }

    def clean() {
        ant.delete(dir: env.dist_modules);
        ant.mkdir(dir: env.dist_modules);
    }
    def build() {
        clean();
        ant.copy(file: version, tofile: versionInBuild);
        setVersionAndBuildNumber(versionInBuild);
        def versionDate = getVersionWithDate();
        ant.copy(todir: "$env.dist_modules_rapid_suite/grails-app") {
            ant.fileset(dir: "$env.rapid_opennms/grails-app")
        }
        ant.copy(todir: "$env.dist_modules_rapid_suite/operations") {
            ant.fileset(dir: "$env.rapid_opennms/operations")
        }
        ant.copy(todir: "$env.dist_modules_rapid_suite/src/groovy") {
            ant.fileset(dir: "$env.rapid_opennms/src/groovy")
        }
        ant.copy(toDir: "${env.dist_modules_rapid_suite}/generatedModels/grails-app/domain") {
            ant.fileset(file: "${env.rapid_opennms}/applications/RapidInsight/grails-app/domain/*.groovy");
        }
        ant.copy(todir: "$env.dist_modules_rapid_suite") {
            ant.fileset(dir: "$env.rapid_opennms/applications/RapidInsight")
        }

        // copy integration files to be copied into OpenNMS server 
        ant.copy(todir: "$env.dist_modules/OpenNMS") {
            ant.fileset(dir: "$env.rapid_opennms/applications/RapidInsight")
        }
        
        ant.zip(destfile: "$env.distribution/OpenNmsPlugin$versionDate" + ".zip") {
            ant.zipfileset(dir: "$env.dist_modules")
        }
        //ant.zip(destfile: "${env.distribution}/opennms-RI-plugin$versionDate" + ".zip"){
        //    ant.zipfileset(dir:"${env.rapid_opennms}/integration")
        //}
    }
}