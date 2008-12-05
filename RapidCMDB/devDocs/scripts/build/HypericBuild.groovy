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
 * User: deneme
 * Date: Aug 27, 2008
 * Time: 1:29:38 PM
 * To change this template use File | Settings | File Templates.
 */
class HypericBuild extends Build {
	def version = "$env.rapid_hyperic/RIHypericVersion.txt";
	def versionInBuild = "$env.dist_modules_rapid_suite/RIHypericVersion.txt"; 
	
    static void main(String[] args) {
        HypericBuild hypericBuild = new HypericBuild();
        hypericBuild.run(args);
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
            ant.fileset(dir: "$env.rapid_hyperic/grails-app")
        }
        ant.copy(todir: "$env.dist_modules_rapid_suite/operations") {
            ant.fileset(dir: "$env.rapid_hyperic/operations")
        }
        ant.copy(todir: "$env.dist_modules_rapid_suite/src/groovy") {
            ant.fileset(dir: "$env.rapid_hyperic/src/groovy")
        }
        ant.copy(toDir: "${env.dist_modules_rapid_suite}/generatedModels/grails-app/domain") {
            ant.fileset(file: "${env.rapid_hyperic}/applications/RapidInsight/grails-app/domain/*.groovy");
        }
        ant.copy(todir: "$env.dist_modules_rapid_suite") {
            ant.fileset(dir: "$env.rapid_hyperic/applications/RapidInsight")
        }

        ant.copy(file: "${env.dist_modules_rapid_suite}/web-app/hypericAdmin.gsp", toFile: "${env.dist_modules_rapid_suite}/grails-app/views/hypericConnection/list.gsp", overwrite: true); 
        
        // copy integration files to be copied into Hyperic server 
        ant.copy(todir: "$env.dist_modules/Hyperic") {
            ant.fileset(dir: "${env.rapid_hyperic}/integration/hyperic/plugin")
        }
        
        ant.zip(destfile: "$env.distribution/HypericPlugin$versionDate" + ".zip") {
            ant.zipfileset(dir: "$env.dist_modules")
        }
        //ant.zip(destfile: "${env.distribution}/hyperic-RI-plugin$versionDate" + ".zip"){
        //    ant.zipfileset(dir:"${env.rapid_hyperic}/integration/hyperic/plugin")
        //}
    }

}