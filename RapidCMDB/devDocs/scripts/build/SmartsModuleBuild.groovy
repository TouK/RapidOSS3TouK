package build

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
class SmartsModuleBuild extends Build {
	 def version = "$env.rapid_smarts/RISmartsVersion.txt";

    static void main(String[] args) {
        SmartsModuleBuild smartsModuleBuild = new SmartsModuleBuild();
        smartsModuleBuild.build();
    }
    def clean(distDir) {
        ant.delete(dir: env.rapid_smarts_build);
        ant.mkdir(dir: env.rapid_smarts_build);
    	if(distDir.equals(env.dist_modules)){
	        ant.delete(dir: env.dist_modules);
	        ant.mkdir(dir: env.dist_modules);
    	}
    }
    def String getExcludedClasses() {
        if (!TEST) {
            return "**/*Test*, **/*Mock*, **/test/**";
        }
        return "";
    }

    def build() {
    	build(env.dist_modules);
    }
    
    def build(distDir) {
    	def rapidSuiteDir = "${distDir}/RapidSuite";
    	def versionInBuild = "${rapidSuiteDir}/RISmartsVersion.txt";
    	clean(distDir);
        ant.copy(file: version, tofile: versionInBuild);
        setVersionAndBuildNumber(versionInBuild);
        def versionDate = getVersionWithDate();
        ant.javac(srcdir: "$env.rapid_smarts/src/java", destdir: env.rapid_smarts_build, excludes: getExcludedClasses()) {
            ant.classpath(refid: "classpath");
        }
        ant.jar(destfile: env.rapid_rssmarts_jar, basedir: env.rapid_smarts_build);
        ant.copy(file: env.rapid_rssmarts_jar, toDir: "$rapidSuiteDir/lib");
        ant.copy(todir: "$rapidSuiteDir/grails-app",overwrite:true) {
            ant.fileset(dir: "$env.rapid_smarts/grails-app")
        }
        ant.copy(todir: "$rapidSuiteDir/operations") {
            ant.fileset(dir: "$env.rapid_smarts/operations")
        }
        ant.copy(todir: "$rapidSuiteDir/src/groovy") {
            ant.fileset(dir: "$env.rapid_smarts/src/groovy")
        }
        ant.copy(toDir:"${rapidSuiteDir}/generatedModels/grails-app/domain")
        {
            ant.fileset(file:"${env.rapid_smarts}/applications/RapidInsightForSmarts/grails-app/domain/*.groovy");
        }
        ant.copy(todir: rapidSuiteDir) {
            ant.fileset(dir: "$env.rapid_smarts/applications/RapidInsightForSmarts") {
                ant.exclude(name: "**/overridenOperations/**")
            }
        }
        //copy RI solutions
        ant.copy(toDir: "${rapidSuiteDir}/../solutions",overwrite:true) {
            ant.fileset(file: "${env.rapid_insight}/solutions/**");
        }
        //copy Smarts solutions
        ant.copy(toDir: "${rapidSuiteDir}/../solutions",overwrite:true) {
            ant.fileset(file: "${env.rapid_smarts}/applications/solutions/**");
        }
        //activate State Calculation Solution For Smarts, dont apply to test build
        if (!TEST) {
            ant.copy(toDir: "${rapidSuiteDir}",overwrite:true) {
                ant.fileset(file: "${rapidSuiteDir}/../solutions/stateCalculation/**");
            }
        }
        
        if(!TEST){            
            ant.copy(todir: "$rapidSuiteDir/operations",overwrite:true) {
                ant.fileset(dir: "$env.rapid_smarts/applications/RapidInsightForSmarts/overridenOperations/default")
            }
        }

       if(TEST){
            ant.copy(todir:"$env.dist_rapid_server_lib", file:(String)classpath.getProperty("skclient_jar"))
            ant.copy(todir:"$env.dist_rapid_server_lib", file:(String)classpath.getProperty("net_jar"))
            ant.copy(todir: "${rapidSuiteDir}/test") {
                ant.fileset(dir: "$env.rapid_smarts/test")
            }
        }
        if(distDir.equals(env.dist_modules)){
	        ant.zip(destfile: "$env.distribution/SmartsPlugin$versionDate" + ".zip") {
	            ant.zipfileset(dir: "$env.dist_modules")
	        }
        }
        println "Smarts Build Done";
    }
}