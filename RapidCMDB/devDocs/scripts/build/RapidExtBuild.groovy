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
 * User: mustafa
 * Date: Mar 19, 2008
 * Time: 1:37:14 AM
 * To change this template use File | Settings | File Templates.
 */
class RapidExtBuild extends Build {
	
    public static void main(String[] args) {
        RapidExtBuild rapidExtBuilder = new RapidExtBuild();
        rapidExtBuilder.build();
    }
    def String getExcludedClasses() {
        if (!TEST) {
            return "**/*Test*, **/*Mock*, **/test/**";
        }
        return "";
    }

    def build() {
        clean();
        compile();
        copyResourcesForJar();
        ant.jar(destfile: env.rapid_ext_jar, basedir: env.rapid_ext_build);
        ant.copy(file: env.rapid_ext_jar, toDir: env.dist_rapid_suite_lib);

        copyDependentJars();
    }

    def copyDependentJars() {
    }

    def clean() {
        ant.delete(dir: env.rapid_ext_build);
        ant.mkdir(dir: env.rapid_ext_build);
    }
    def compile() {
        ant.javac(srcdir: "$env.rapid_ext/database/java", destdir: env.rapid_ext_build, excludes: getExcludedClasses()) {
            ant.classpath(refid: "classpath");
        }
        ant.javac(srcdir: "$env.rapid_ext/snmp/java", destdir: env.rapid_ext_build, excludes: getExcludedClasses()) {
            ant.classpath(refid: "classpath");
        }
    }


    def copyResourcesForJar() {
        ant.copy(todir: "$env.dist_rapid_suite/grails-app/ext") {
            ant.fileset(dir: "$env.rapid_ext/database/groovy") {
                if (!TEST) {
                    ant.exclude(name: "**/test/**")
                    ant.exclude(name: "**/*Test*")
                }
            };
            ant.fileset(dir: "$env.rapid_ext/rapidinsight/groovy") {
                if (!TEST) {
                    ant.exclude(name: "**/test/**")
                    ant.exclude(name: "**/*Test*")
                }
            };
            ant.fileset(dir: "$env.rapid_ext/http/groovy") {
                if (!TEST) {
                    ant.exclude(name: "**/test/**")
                    ant.exclude(name: "**/*Test*")
                }
            };
            ant.fileset(dir: "$env.rapid_ext/email/groovy") {
                if (!TEST) {
                    ant.exclude(name: "**/test/**")
                    ant.exclude(name: "**/*Test*")
                }
            };
        }
    }
}