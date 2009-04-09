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

class JiraPluginBuild extends Build{
	def version = "$env.rapid_jira/RIJiraPluginVersion.txt";
	
    static void main(String[] args) {
        JiraPluginBuild jiraPluginBuild = new JiraPluginBuild();
        jiraPluginBuild.build();
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
    	def versionInBuild = "${rapidSuiteDir}/RIJiraPluginVersion.txt";
    	clean(distDir);
        ant.copy(file: version, tofile: versionInBuild);
        setVersionAndBuildNumber(versionInBuild);
        def versionDate = getVersionWithDate();
        ant.copy(todir: "${rapidSuiteDir}/grails-app") {
            ant.fileset(dir: "$env.rapid_cmdb_cvs/grails-app"){
            	ant.include(name: "**/*Jira*.groovy")
            	ant.include(name: "**/jiraConnector/*")
            }
        }
        ant.copy(todir: "${rapidSuiteDir}/grails-app") {
            ant.fileset(dir: "$env.rapid_insight/grails-app"){
            	ant.include(name: "**/*Jira*.groovy")
            }
        }        
        ant.copy(todir: "${rapidSuiteDir}/operations") {
            ant.fileset(dir: "$env.rapid_cmdb_cvs/operations"){
            	ant.include(name: "**/*Jira*.groovy")
            }
        }
        ant.copy(todir: "${rapidSuiteDir}/operations") {
            ant.fileset(dir: "$env.rapid_insight/operations"){
            	ant.include(name: "**/*Jira*.groovy")
            }
        }
        ant.copy(file:"${env.rapid_insight}/overridenOperations/jiraTicketing/RsTicketOperations.groovy", toFile:"${rapidSuiteDir}/operations/RsTicketOperations.groovy",overwrite:true);
        ant.copy(todir: "${rapidSuiteDir}/lib") {
            ant.fileset(dir: "$env.rapid_jira/lib")
        }
        
        copyDependentJars(rapidSuiteDir);
        
        ant.copy(todir: "${rapidSuiteDir}/grails-app/ext") {
            ant.fileset(dir: "$env.rapid_jira/src/groovy")
        }

        if(distDir.equals(env.dist_modules)){
	        ant.zip(destfile: "$env.distribution/JiraPluginPlugin$versionDate" + ".zip") {
	            ant.zipfileset(dir: "$env.dist_modules")
	        }
        }
         
        println "JiraPlugin Build Done";
    }

    def copyDependentJars(rapidSuiteDir) {
        ant.copy(file: (String) classpath.getProperty("axis_jar"), toDir: "${rapidSuiteDir}/lib");
        ant.copy(file: (String) classpath.getProperty("jaxrpc_jar"), toDir: "${rapidSuiteDir}/lib");
        ant.copy(file: (String) classpath.getProperty("commons-discovery-0_2_jar"), toDir: "${rapidSuiteDir}/lib");
        ant.copy(file: (String) classpath.getProperty("wsdl4j-1_5_1_jar"), toDir: "${rapidSuiteDir}/lib");
    }
}