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

    static void main(String[] args) {
        SmartsModuleBuild smartsModuleBuild = new SmartsModuleBuild();
        smartsModuleBuild.run(args);
    }

    def String getExcludedClasses() {
        if (!TEST) {
            return "**/*Test*, **/*Mock*, **/test/**";
        }
        return "";
    }


    def build() {
        ant.delete(dir: env.rapid_ext_build);
        ant.mkdir(dir: env.rapid_ext_build);

        ant.javac(srcdir: "$env.rapid_ext/smarts/java", destdir: env.rapid_ext_build, excludes: getExcludedClasses()) {
            ant.classpath(refid: "classpath");
        }
        ant.jar(destfile: env.rapid_smarts_jar, basedir: env.rapid_ext_build);
        ant.copy(file: env.rapid_smarts_jar, toDir: env.dist_rapid_suite_lib);

        ant.copy(file: (String) classpath.getProperty("skclient_jar"), toDir: "$env.dist_rapid_server_lib");
        ant.copy(file: (String) classpath.getProperty("net_jar"), toDir: "$env.dist_rapid_server_lib");
        createPlugin(env.rapid_smarts, ["applications/**", "operations/**", "generatedModels/**"]);
        ant.delete(file: env.dist_rapid_server_lib + "/skclient_jar");
        ant.delete(file: env.dist_rapid_server_lib + "/net_jar");
    }
}