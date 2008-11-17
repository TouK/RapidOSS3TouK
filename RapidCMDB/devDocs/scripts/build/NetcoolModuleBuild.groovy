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
    static void main(String[] args) {
        NetcoolModuleBuild netcoolModuleBuild = new NetcoolModuleBuild();
        netcoolModuleBuild.run(args);
    }
    def clean() {
        ant.delete(dir: env.dist_modules);
        ant.mkdir(dir: env.dist_modules);
    }
    def build() {
        clean();
        ant.copy(todir: "$env.dist_modules_rapid_suite/grails-app") {
            ant.fileset(dir: "$env.rapid_netcool/grails-app")
        }
        ant.copy(todir: "$env.dist_modules_rapid_suite/operations") {
            ant.fileset(dir: "$env.rapid_netcool/operations")
        }
        ant.copy(todir: "$env.dist_modules_rapid_suite/src/groovy") {
            ant.fileset(dir: "$env.rapid_netcool/src/groovy")
        }
        ant.copy(toDir: "${env.dist_modules_rapid_suite}/generatedModels/grails-app/domain") {
            ant.fileset(file: "${env.rapid_netcool}/applications/RapidInsightForNetcool/grails-app/domain/*.groovy");
        }
        ant.copy(todir: "$env.dist_modules_rapid_suite") {
            ant.fileset(dir: "$env.rapid_netcool/applications/RapidInsightForNetcool")
        }
        def versionDate = getVersionWithDate();
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
        def adminViews = ["httpConnection", "httpDatasource", "databaseConnection", "ldapConnection", "databaseDatasource",
                "singleTableDatabaseDatasource", "snmpConnection", "snmpDatasource", "script", "rsUser", "group", "netcoolConnector", "netcoolConversionParameter"]

        adminViews.each {
            ant.copy(file: "${env.dist_modules_rapid_suite}/grails-app/views/layouts/adminLayout.gsp", toFile: "${env.dist_modules_rapid_suite}/grails-app/views/layouts/${it}.gsp", overwrite: true);
        }
        ant.zip(destfile: "$env.distribution/NetcoolPlugin.zip") {
            ant.zipfileset(dir: "$env.dist_modules")
        }
    }

}