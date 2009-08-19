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
class NetcoolModuleBuild extends Build {
    def version = "$env.rapid_netcool/RINetcoolVersion.txt";

    static void main(String[] args) {
        NetcoolModuleBuild netcoolModuleBuild = new NetcoolModuleBuild();
        netcoolModuleBuild.build();
    }
    def clean(distDir) {
        if (distDir.equals(env.dist_modules)) {
            ant.delete(dir: env.dist_modules);
            ant.mkdir(dir: env.dist_modules);
        }
    }

    def build() {
        build(env.dist_modules);
    }

    def build(distDir) {
        def rapidSuiteDir = "${distDir}/RapidSuite";
        def versionInBuild = "${rapidSuiteDir}/RINetcoolVersion.txt";
        clean(distDir);
        ant.copy(file: version, tofile: versionInBuild);
        setVersionAndBuildNumber(versionInBuild);
        def versionDate = getVersionWithDate();
        ant.copy(todir: "$rapidSuiteDir/grails-app", overwrite: true) {
            ant.fileset(dir: "$env.rapid_netcool/grails-app")
        }
        ant.copy(todir: "$rapidSuiteDir/operations") {
            ant.fileset(dir: "$env.rapid_netcool/operations")
        }
        ant.copy(todir: "$rapidSuiteDir/src/groovy") {
            ant.fileset(dir: "$env.rapid_netcool/src/groovy")
            {
                if (!TEST) {
                    ant.exclude(name: "**/test/**")
                    ant.exclude(name: "**/*Test*")
                }
            }
        }
        if (TEST)
        {
            //for token definition files to be found , should be changed
            ant.copy(todir: "$rapidSuiteDir/conversion") {
                ant.fileset(dir: "$env.rapid_netcool/conversion")
            }
            //for classes to be compiled
            ant.copy(todir: "$rapidSuiteDir/src/groovy") {
                ant.fileset(dir: "$env.rapid_netcool/conversion/groovy")
            }
            ant.copy(todir: "$rapidSuiteDir/src/java") {
                ant.fileset(dir: "$env.rapid_netcool/conversion/java")
            }
        }
        copyDependentJars("$rapidSuiteDir/lib");

        ant.copy(toDir: "${rapidSuiteDir}/generatedModels/grails-app/domain") {
            ant.fileset(file: "${env.rapid_netcool}/applications/RapidInsightForNetcool/grails-app/domain/*.groovy");
        }
        ant.copy(todir: rapidSuiteDir) {
            ant.fileset(dir: "$env.rapid_netcool/applications/RapidInsightForNetcool") {
                ant.exclude(name: "**/netcoolDataGenerator.groovy")
                ant.exclude(name: "**/NetcoolDemoValues.groovy")
                ant.exclude(name: "**/NetcoolRealValues.groovy")
                ant.exclude(name: "**/demoGenerator.groovy")
            }
        }
        ant.copy(toDir: "${rapidSuiteDir}/../solutions", overwrite: true) {
            ant.fileset(file: "${env.rapid_insight}/solutions/**");
        }
        ant.copy(toDir: "${rapidSuiteDir}/../solutions", overwrite: true) {
            ant.fileset(file: "${env.rapid_netcool}/applications/solutions/**");
        }
        //        replaceJavascriptAndCss("${rapidSuiteDir}/grails-app/views/layouts/indexLayout.gsp", "/RapidSuite/indexLayout_${buildNo}.js", "/RapidSuite/indexLayout_${buildNo}.css")
        if (distDir.equals(env.dist_modules)) {
            ant.zip(destfile: "$env.distribution/NetcoolPlugin$versionDate" + ".zip") {
                ant.zipfileset(dir: "$env.dist_modules")
            }
        }
        if (TEST) {
            ant.copy(todir: "${rapidSuiteDir}/test") {
                ant.fileset(dir: "$env.rapid_netcool/test")
            }
        }
        println "Netcool Build Done";
    }

    def copyDependentJars(dirToCopyTo) {
        if (TEST)
        {
            ant.copy(file: (String) classpath.getProperty("antlr-3_1_1-runtime_jar"), toDir: dirToCopyTo);
        }

    }

}