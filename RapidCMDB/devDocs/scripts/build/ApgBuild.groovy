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
 * User: Sezgin Kucukkaraaslan
 * Date: Dec 3, 2008
 * Time: 2:00:12 PM
 */
class ApgBuild extends Build{
	def version = "$env.rapid_apg/RIApgVersion.txt";
	
   static void main(String[] args) {
        ApgBuild apgBuild = new ApgBuild();
        apgBuild.build();
    }

    def clean(distDir) {
    	if(distDir.equals(env.dist_modules)){
	        ant.delete(dir: env.dist_modules);
	        ant.mkdir(dir: env.dist_modules);
    	}
    }
    
    def build() {
    	build(env.dist_modules);
    }
    
    def build(distDir) {
    	def rapidSuiteDir = "${distDir}/RapidSuite";
    	def versionInBuild = "${rapidSuiteDir}/RIApgVersion.txt";
    	clean(distDir);
        ant.copy(file: version, tofile: versionInBuild);
        setVersionAndBuildNumber(versionInBuild);
        def versionDate = getVersionWithDate();
        ant.copy(todir: "${rapidSuiteDir}/grails-app") {
            ant.fileset(dir: "$env.rapid_apg/grails-app")
        }
        ant.copy(todir: "${rapidSuiteDir}/operations") {
            ant.fileset(dir: "$env.rapid_apg/operations")
        }
        if (TEST) {
            ant.copy(todir: "${rapidSuiteDir}/test") {
                ant.fileset(dir: "$env.rapid_apg/test")
            }
        }
        ant.copy(todir: "${rapidSuiteDir}/lib") {
            ant.fileset(dir: "$env.rapid_apg/lib")
        }
        ant.copy(todir: "${rapidSuiteDir}/src/groovy") {
            ant.fileset(dir: "$env.rapid_apg/src/groovy")
            {
                if(!TEST)
                {
                    ant.exclude(name: "**/test/**")
                }
            }
        }
        ant.copy(todir: rapidSuiteDir) {
            ant.fileset(dir: "$env.rapid_apg/applications/RapidInsight")
        }
        if(distDir.equals(env.dist_modules)){
	        ant.zip(destfile: "$env.distribution/ApgPlugin$versionDate" + ".zip") {
	            ant.zipfileset(dir: "$env.dist_modules")
	        }
        }
        println "APG Build Done";
    }
}