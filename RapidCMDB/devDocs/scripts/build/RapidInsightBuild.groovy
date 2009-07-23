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

//SAMPLE OPTIONS FILE: (if omitted, defaults in constructor will be used)
//--------------------	
//RI_UNIX=true
//RI_WINDOWS=false
//RCMDB_UNIX=true
//RCMDB_WINDOWS=false
//SAMPLE1=false
//SAMPLE2=false
//ZIP=false
//APG=false
//OPENNMS=false
//JIRA=false
//HYPERIC=false
//NETCOOL=false
//SMARTS=false
//E_WINDOWS=false
//E_UNIX=false
//TEST=false
//JREDIR=path to jre files on the build machine

package build

class RapidInsightBuild extends Build {
    boolean RI_UNIX_OPT, RI_WINDOWS_OPT, APG_OPT, OPENNMS_OPT, JIRA_OPT, NETCOOL_OPT, SMARTS_OPT, HYPERIC_OPT, ENTERPRISE_WINDOWS_OPT, ENTERPRISE_UNIX_OPT, ZIP_OPT, TEST_OPT;
    def JREDIR_OPT;
    def version = "$env.rapid_insight/RIVersion.txt";
    def versionInBuild = "$env.dist_rapid_suite/RIVersion.txt";
    static def buildOptions;

    def setOptions(options) {
        if (options != null) {
            buildOptions = options;
            RI_UNIX_OPT = Boolean.parseBoolean(options.get("RI_UNIX", "false"));
            RI_WINDOWS_OPT = Boolean.parseBoolean(options.get("RI_WINDOWS", "true"));
            OPENNMS_OPT = Boolean.parseBoolean(options.get("OPENNMS", "false"));
            JIRA_OPT = Boolean.parseBoolean(options.get("JIRA", "false"));
            APG_OPT = Boolean.parseBoolean(options.get("APG", "false"));
            NETCOOL_OPT = Boolean.parseBoolean(options.get("NETCOOL", "false"));
            SMARTS_OPT = Boolean.parseBoolean(options.get("SMARTS", "false"));
            HYPERIC_OPT = Boolean.parseBoolean(options.get("HYPERIC", "false"));
            ENTERPRISE_WINDOWS_OPT = Boolean.parseBoolean(options.get("E_WINDOWS", "false"));
            ENTERPRISE_UNIX_OPT = Boolean.parseBoolean(options.get("E_UNIX", "false"));
            ZIP_OPT = Boolean.parseBoolean(options.get("ZIP", "false"));
            TEST_OPT = Boolean.parseBoolean(options.get("TEST", "false"));
            JREDIR_OPT = options.get("JREDIR");
            if(JREDIR_OPT!=null) env.jreDir = JREDIR_OPT; 
            TEST = TEST_OPT
        }
    }

    static def getTestOptions(){
	   Properties options = new Properties();
	   options.put("RI_UNIX", "false")
	   options.put("RI_WINDOWS", "true")
	   options.put("RCMDB_UNIX", "true")
	   options.put("RCMDB_WINDOWS", "false")
	   options.put("MODELER", "true")
	   options.put("OPENNMS", "true")
	   options.put("JIRA", "true")
	   options.put("APG", "true")
	   options.put("NETCOOL", "true")
	   options.put("SMARTS", "true")
	   options.put("HYPERIC", "true")
	   options.put("E_WINDOWS", "false")
	   options.put("E_UNIX", "false")
	   options.put("ZIP", "false")
	   options.put("TEST", "true")
	   return options;
    }

    static void main(String[] args) {
        long t = System.currentTimeMillis();
        if (args.length > 0) {
            if(args[0] == "test"){
               buildOptions = getTestOptions();
            }
            else{
                buildOptions = Build.getBuildOptions(args[0]);
            }


        }
        RapidInsightBuild rapidInsightBuilder = new RapidInsightBuild();
        rapidInsightBuilder.setOptions(buildOptions);
        rapidInsightBuilder.build();
        println "Build finished in ${(System.currentTimeMillis() - t)/1000.0} secs."
    }

    //    def String getExcludedClasses() {
    //        if (!TEST) {
    //            return "**/*Test*, **/*Mock*, **/test/**";
    //        }
    //        return "";
    //    }

    def build() {
        def t = System.currentTimeMillis();
        if(RI_UNIX_OPT || RI_WINDOWS_OPT) buildUnix();
        if (RI_WINDOWS_OPT) addJreOnTopOfUnixAndZip("RI");
        buildIntegrationPlugins();
        if (ENTERPRISE_WINDOWS_OPT) makeWindowsEnterprise();
        if (ENTERPRISE_UNIX_OPT) makeUnixEnterprise();
        println "RI Build Done in ${(System.currentTimeMillis()-t)/1000.0} secs.";
    }

    def buildUnix() {
        prepareRCMDB();
        ant.delete(dir: env.dist_rapid_server+"/jre");
        // copy xml file for sample data to be imported
        ant.copy(file: "$env.rapid_insight/sampleRiData.xml", tofile: "$env.dist_rapid_suite/sampleRiData.xml",overwrite:true);

        ant.copy(file: version, tofile: versionInBuild);
        setVersionAndBuildNumber(versionInBuild);
        def versionDate = getVersionWithDate();
        replaceGrailsLinks("${env.dist_rapid_suite}/grails-app/views/layouts/_layoutHeader.gsp")
        ant.java(fork: "true", classname: "com.ifountain.comp.utils.JsCssCombiner") {
            ant.arg(value: "-file");
            ant.arg(value: "$env.dist_rapid_suite/grails-app/views/layouts/_layoutHeader.gsp");
            ant.arg(value: "-applicationPath");
            ant.arg(value: "${env.dist_rapid_suite}/web-app");
            ant.arg(value: "-target");
            ant.arg(value: "${env.dist_rapid_suite}/web-app");
            ant.arg(value: "-suffix");
            ant.arg(value: "_${buildNo}");
            ant.classpath() {
                ant.pathelement(location: "${env.dist_rapid_suite_lib}/comp.jar");
                ant.pathelement(location: "${env.dist_rapid_server_lib}/commons-cli-1.0.jar");
                ant.pathelement(location: "${env.dist_rapid_server_lib}/commons-io-1.4.jar");
                ant.pathelement(location: "${env.dist_rapid_server_lib}/log4j-1.2.15.jar");
            }
        }
        ant.move(file: "${env.dist_rapid_suite}/web-app/_layoutHeader.gsp", todir: "${env.dist_rapid_suite}/grails-app/views/layouts");
        replaceJavascriptAndCss("${env.dist_rapid_suite}/grails-app/views/layouts/_layoutHeader.gsp", "_layoutHeader_${buildNo}.js", "_layoutHeader_${buildNo}.css")

        replaceGrailsLinks("${env.dist_rapid_suite}/web-app/designer.gsp")
        ant.java(fork: "true", classname: "com.ifountain.comp.utils.JsCssCombiner") {
            ant.arg(value: "-file");
            ant.arg(value: "$env.dist_rapid_suite/web-app/designer.gsp");
            ant.arg(value: "-applicationPath");
            ant.arg(value: "${env.dist_rapid_suite}/web-app");
            ant.arg(value: "-target");
            ant.arg(value: "${env.dist_rapid_suite}/web-app");
            ant.arg(value: "-suffix");
            ant.arg(value: "_${buildNo}");
            ant.classpath() {
                ant.pathelement(location: "${env.dist_rapid_suite_lib}/comp.jar");
                ant.pathelement(location: "${env.dist_rapid_server_lib}/commons-cli-1.0.jar");
                ant.pathelement(location: "${env.dist_rapid_server_lib}/commons-io-1.4.jar");
                ant.pathelement(location: "${env.dist_rapid_server_lib}/log4j-1.2.15.jar");
            }
        }
        replaceJavascriptAndCss("${env.dist_rapid_suite}/web-app/designer.gsp", "designer_${buildNo}.js", "designer_${buildNo}.css")
        
        ant.move(file: "${env.dist_rapid_server}/licenses/RapidCMDB_license.txt", toFile: "${env.dist_rapid_server}/licenses/RapidInsightCommunityLicense.txt");
        def dbViews = ["databaseConnection", "databaseDatasource", "singleTableDatabaseDatasource"];
        dbViews.each {
            ant.copy(file: "${env.dist_rapid_suite}/web-app/dbDatasources.gsp", toFile: "${env.dist_rapid_suite}/grails-app/views/${it}/list.gsp", overwrite: true);
        }
        def channelViews= ["emailConnector","jabberConnector","smsConnector","aolConnector","sametimeConnector"];
        channelViews.each {
            ant.copy(file: "${env.dist_rapid_suite}/web-app/notificationChannels.gsp", toFile: "${env.dist_rapid_suite}/grails-app/views/${it}/list.gsp", overwrite: true);
        }
        
        if (ZIP_OPT) {
            def zipFileName = "${env.distribution}/RI_Unix$versionDate" + ".zip"
            ant.zip(destfile: zipFileName) {
                ant.zipfileset(dir: "$env.distribution/RapidServer", prefix: "RapidServer", excludes:"**/*.vmoptions,**/*.exe,**/*.bat");
            }
        }
    }

    def prepareRCMDB() {
        def rapidCMDBBuild = new RapidCmdbBuild();
        rapidCMDBBuild.setOptions(buildOptions);
        rapidCMDBBuild.build();
        //        ant.delete(dir: env.dist_rapid_server);
        //
        //        def rapidCmdb = listFiles(new File(env.distribution), "RapidCMDB");
        //        ant.unzip(src: rapidCmdb.absolutePath, dest: env.distribution);
        //        if(willDeleteModeler){
        //            ant.delete(dir: env.dist_modeler);
        //        }

        createRapidUiPlugin();
        createRapidInsightPlugin();
        //        createPlugin(env.rapid_ui,[]);
        //        def rapidUiPlugin = listFiles(new File(env.distribution), "grails-rapid-ui");
        //        installPlugin(rapidUiPlugin, env.dist_rapid_suite, [Ant: ant], [:]);
        //
        //        createPlugin(env.rapid_insight, ["applications/**", "operations/**", "rs.exe"]);
        //        def rapidInsightPlugin = listFiles(new File(env.distribution), "grails-rapid-insight");
        //        installPlugin(rapidInsightPlugin, env.dist_rapid_suite, [Ant: ant], [:]);
    }

    def buildIntegrationPlugins() {
        def distDir = env.dist_modules;
        if (TEST_OPT) distDir = env.dist_rapid_server;
        if (SMARTS_OPT) new SmartsModuleBuild().build(distDir);
        if (NETCOOL_OPT) new NetcoolModuleBuild().build(distDir);
        if (HYPERIC_OPT) new HypericBuild().build(distDir);
        if (APG_OPT) new ApgBuild().build(distDir);
        if (OPENNMS_OPT) new OpenNmsBuild().build(distDir);
        if (JIRA_OPT) new JiraPluginBuild().build(distDir);
    }

    def makeWindowsEnterprise() {
        def versionDate = getVersionWithDate();
        ant.unzip(src: "${env.distribution}/RI_Windows$versionDate" + ".zip", dest: env.distribution + "/WEnt");
        ant.delete(file: env.distribution + "/WEnt/RapidServer/licenses/RapidInsightCommunityLicense.txt");
        ant.copy(file: "$env.rapid_cmdb_cvs/licenses/IFountain End User License Agreement.pdf", toDir: env.distribution + "/WEnt/RapidServer/licenses", overwrite: true);
        ant.zip(destfile: "${env.distribution}/RIE_Windows$versionDate" + ".zip") {
            ant.zipfileset(dir: "$env.distribution/WEnt")
        }
    }

    def makeUnixEnterprise() {
        def versionDate = getVersionWithDate();
        ant.unzip(src: "${env.distribution}/RI_Unix$versionDate" + ".zip", dest: env.distribution + "/UEnt");
        ant.delete(file: env.distribution + "/UEnt/RapidServer/licenses/RapidInsightCommunityLicense.txt");
        ant.copy(file: "$env.rapid_cmdb_cvs/licenses/IFountain End User License Agreement.pdf", toDir: env.distribution + "/UEnt/RapidServer/licenses", overwrite: true);
        ant.zip(destfile: "${env.distribution}/RIE_Unix$versionDate" + ".zip") {
            ant.zipfileset(dir: "$env.distribution/UEnt")
        }
    }

    def listFiles(File rootDir, String regexp)
    {
        File file = null;
        rootDir.listFiles().each {File f ->
            if (f.name.startsWith(regexp))
            {
                file = f;
                return;
            }
        }
        return file;
    }

    def createRapidUiPlugin() {
        ant.copy(todir: "$env.dist_rapid_ui/grails-app",overwrite:true) {
            ant.fileset(dir: "$env.rapid_ui/grails-app") {
                ant.include(name: "domain/**/*")
                ant.include(name: "controllers/**/*")
                ant.include(name: "taglib/**/*")
                ant.include(name: "utils/**/*")
            };
        }
        ant.copy(todir: "$env.dist_rapid_suite/grails-app/templates") {
            ant.fileset(dir: "$env.rapid_ui/grails-app/templates")
        }
        ant.copy(todir: "$env.dist_rapid_suite/operations") {
            ant.fileset(dir: "$env.rapid_ui/operations")
        }
        ant.copy(todir: "$env.dist_rapid_ui/scripts") {
            ant.fileset(dir: "$env.rapid_ui/scripts")
        }
        ant.copy(todir: "$env.dist_rapid_ui/src/groovy") {
            ant.fileset(dir: "$env.rapid_ui/src/groovy")
        }
        ant.copy(file: "$env.rapid_ui/application.properties", toDir: "$env.dist_rapid_ui");
        ant.copy(file: "$env.rapid_ui/RapidUiGrailsPlugin.groovy", toDir: "$env.dist_rapid_ui");
        ant.copy(file: "$env.rapid_ui/plugin.xml", toDir: "$env.dist_rapid_ui");

        ant.copy(toDir: "$env.dist_rapid_suite/web-app",overwrite:true) {
            ant.fileset(dir: "$env.rapid_ui/web-app") {
                ant.exclude(name: "**/test/**")
            }
        }
        ant.copy(todir: "$env.dist_rapid_suite/grails-app",overwrite:true) {
            ant.fileset(dir: "$env.rapid_ui/grails-app") {
                ant.include(name: "views/**/*")
            };
        }
        ant.copy(toDir: "$env.dist_rapid_suite/grails-app/i18n",overwrite:true) {
            ant.fileset(dir: "$env.rapid_ui/grails-app/i18n")
        }

        if (TEST) {
            ant.copy(todir: "${env.dist_rapid_suite}/test") {
                ant.fileset(dir: "$env.rapid_ui/test")
            }
        }
    }
    def createRapidInsightPlugin() {
        ant.copy(todir: "$env.dist_rapid_insight/grails-app") {
            ant.fileset(dir: "$env.rapid_insight/grails-app") {
                ant.include(name: "taglib/**/*")
            };
        }
        ant.copy(file: "$env.rapid_insight/application.properties", toDir: "$env.dist_rapid_insight");
        ant.copy(file: "$env.rapid_insight/RapidInsightGrailsPlugin.groovy", toDir: "$env.dist_rapid_insight");
        ant.copy(file: "$env.rapid_insight/plugin.xml", toDir: "$env.dist_rapid_insight");

        ant.copy(toDir: "${env.dist_rapid_suite}/grails-app/conf",overwrite:true) {
            ant.fileset(dir: "${env.rapid_insight}/grails-app/conf");
        }
        ant.copy(toDir: "${env.dist_rapid_suite}/grails-app/views/layouts",overwrite:true) {
            ant.fileset(dir: "${env.rapid_insight}/grails-app/views/layouts");
        }

        ant.copy(toDir: "${env.dist_rapid_suite}/scripts",overwrite:true) {
            ant.fileset(file: "${env.rapid_insight}/scripts/**") {
                ant.exclude(name: "_Install.groovy")
                ant.exclude(name: "_Upgrade.groovy")
                ant.exclude(name: "*Test.groovy")
            }
        }
        ant.copy(toDir: "${env.dist_rapid_suite}/web-app",overwrite:true) {
            ant.fileset(file: "${env.rapid_insight}/web-app/**");
        }
        ant.copy(toDir: "${env.dist_rapid_suite}/grails-app/controllers",overwrite:true) {
            ant.fileset(file: "${env.rapid_insight}/grails-app/controllers/**");
        }

        ant.copy(toDir: "${env.dist_rapid_suite}/grails-app/views",overwrite:true) {
            ant.fileset(dir: "${env.rapid_insight}/grails-app/views")
        }
        ant.mkdir(dir: "${env.dist_rapid_suite}/generatedModels/grails-app/domain");
        ant.copy(toDir: "${env.dist_rapid_suite}/generatedModels/grails-app/domain",overwrite:true) {
            ant.fileset(file: "${env.rapid_insight}/grails-app/domain/**");
        }
        ant.copy(toDir: "${env.dist_rapid_suite}/grails-app/domain",overwrite:true) {
            ant.fileset(file: "${env.rapid_insight}/grails-app/domain/**");
        }
        ant.copy(toDir: "${env.dist_rapid_suite}/grails-app/templates",overwrite:true) {
            ant.fileset(file: "${env.rapid_insight}/grails-app/templates/**");
        }
        ant.copy(toDir: "${env.dist_rapid_suite}/operations",overwrite:true) {
            ant.fileset(file: "${env.rapid_insight}/operations/**");
        }
        ant.copy(toDir: "${env.dist_rapid_suite}/../solutions",overwrite:true) {
            ant.fileset(file: "${env.rapid_insight}/solutions/**");
        }
        ant.copy(file: "${env.rapid_insight}/rs.exe", toDir: "${env.dist_rapid_suite}",overwrite:true)
        
        if (TEST) {
            ant.copy(todir: "${env.dist_rapid_suite}/test") {
                ant.fileset(dir: "$env.rapid_insight/test")
            }
            ant.copy(todir: "${env.dist_rapid_suite}/grails-app/domain",overwrite:true) {
                ant.fileset(dir: "$env.rapid_insight/solutions/inMaintenance/grails-app/domain")
            }
            ant.copy(todir: "${env.dist_rapid_suite}/grails-app/domain",overwrite:true) {
                ant.fileset(dir: "$env.rapid_insight/solutions/heartbeat/grails-app/domain")
            }
        }
    }

    //    def getPluginName(prefix){
    //        def path = new File(env.distribution);
    //        def PName;
    //        path.eachFile{
    //            if(it.name.indexOf(prefix) != -1) PName = it.name;
    //        }
    //        return PName;
    //    }

    //    def testBuild() {
    //        TEST = true;
    //        createDirectories();
    //
    //        smartsBuild.build();
    //        def SPName = getPluginName("SmartsPlugin");
    //        ant.unzip(src:"$env.distribution/$SPName", dest:env.dist_rapid_server);
    //
    //        netcoolBuild.build();
    //        def NPName = getPluginName("NetcoolPlugin");
    //        ant.unzip(src:"$env.distribution/$NPName", dest:env.dist_rapid_server);
    //
    //        hypericBuild.build();
    //        def HPName = getPluginName("HypericPlugin");
    //        ant.unzip(src:"$env.distribution/$HPName", dest:env.dist_rapid_server);
    //
    //        apgBuild.build();
    //        def APGPName = getPluginName("ApgPlugin");
    //        ant.unzip(src:"$env.distribution/$APGPName", dest:env.dist_rapid_server);
    //
    //        openNmsBuild.build();
    //        def OPPName = getPluginName("OpenNmsPlugin");
    //        ant.unzip(src:"$env.distribution/$OPPName", dest:env.dist_rapid_server);
    //
    //        ant.copy(tofile: "$env.dist_rapid_suite/../conf/groovy-starter.conf", file:"${env.dev_docs}/groovy-starter-for-tests.conf", overwrite:true)
    //        ant.copy(todir: "$env.dist_rapid_suite/grails-app/domain") {
    //            ant.fileset(dir: "$env.rapid_cmdb_cvs/grails-app/domain") {
    //                ant.include(name: "*.groovy")
    //                ant.include(name: "test/*")
    //            }
    //        }
    //    }
}