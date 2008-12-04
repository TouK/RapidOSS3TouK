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
	 def versionInBuild = "$env.dist_modules_rapid_suite/RISmartsVersion.txt";

    static void main(String[] args) {
        SmartsModuleBuild smartsModuleBuild = new SmartsModuleBuild();
        smartsModuleBuild.run(args);
    }
    def clean() {
        ant.delete(dir: env.rapid_smarts_build);
        ant.mkdir(dir: env.rapid_smarts_build);
        ant.delete(dir: env.dist_modules);
        ant.mkdir(dir: env.dist_modules);
    }
    def String getExcludedClasses() {
        if (!TEST) {
            return "**/*Test*, **/*Mock*, **/test/**";
        }
        return "";
    }

    def build() {
        clean();
        ant.copy(file: version, tofile: versionInBuild);
        setVersionAndBuildNumber(versionInBuild);
        def versionDate = getVersionWithDate();
        ant.javac(srcdir: "$env.rapid_smarts/src/java", destdir: env.rapid_smarts_build, excludes: getExcludedClasses()) {
            ant.classpath(refid: "classpath");
        }
        ant.jar(destfile: env.rapid_rssmarts_jar, basedir: env.rapid_smarts_build);
        ant.copy(file: env.rapid_rssmarts_jar, toDir: "$env.dist_modules_rapid_suite/lib");
        ant.copy(todir: "$env.dist_modules_rapid_suite/grails-app") {
            ant.fileset(dir: "$env.rapid_smarts/grails-app")
        }
        ant.copy(todir: "$env.dist_modules_rapid_suite/operations") {
            ant.fileset(dir: "$env.rapid_smarts/operations")
        }
        ant.copy(todir: "$env.dist_modules_rapid_suite/src/groovy") {
            ant.fileset(dir: "$env.rapid_smarts/src/groovy")
        }
        ant.copy(toDir:"${env.dist_modules_rapid_suite}/generatedModels/grails-app/domain")
        {
            ant.fileset(file:"${env.rapid_smarts}/applications/RapidInsightForSmarts/grails-app/domain/*.groovy");
        }
        ant.copy(todir: "$env.dist_modules_rapid_suite") {
            ant.fileset(dir: "$env.rapid_smarts/applications/RapidInsightForSmarts")
        }
        ant.java(fork: "true", classname: "com.ifountain.comp.utils.JsCssCombiner") {
            ant.arg(value: "-file");
            ant.arg(value: "${env.dist_modules_rapid_suite}/grails-app/views/layouts/indexLayout.gsp");
            ant.arg(value: "-applicationPath");
            ant.arg(value: "${env.dist_rapid_suite}/web-app");
            ant.arg(value: "-target");
            ant.arg(value: "${env.dist_modules_rapid_suite}/web-app");
            ant.arg(value: "-suffix");
            ant.arg(value: "${versionDate}");
                ant.classpath(refid: "classpath");
        }
        ant.move(file: "${env.dist_modules_rapid_suite}/web-app/indexLayout.gsp", todir: "${env.dist_modules_rapid_suite}/grails-app/views/layouts");
        ant.zip(destfile: "$env.distribution/SmartsPlugin$versionDate" + ".zip") {
            ant.zipfileset(dir: "$env.dist_modules")
        }
    }
}