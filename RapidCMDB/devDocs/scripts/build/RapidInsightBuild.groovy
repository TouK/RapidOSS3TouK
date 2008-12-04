package build
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Nov 12, 2008
 * Time: 9:41:55 AM
 */
class RapidInsightBuild extends Build {
    def version = "$env.rapid_insight/RIVersion.txt";
    def versionInBuild = "$env.dist_rapid_suite/RIVersion.txt";
    def rapidCMDBBuild = new RapidCmdbBuild();
    def smartsBuild = new SmartsModuleBuild();
    def netcoolBuild = new NetcoolModuleBuild();
    def hypericBuild = new HypericBuild();
    def apgBuild = new ApgBuild();
    def openNmsBuild = new OpenNmsBuild();

    static void main(String[] args) {
        RapidInsightBuild rapidInsightBuilder = new RapidInsightBuild();
        rapidInsightBuilder.run(args);
    }

    def String getExcludedClasses() {
        if (!TEST) {
            return "**/*Test*, **/*Mock*, **/test/**";
        }
        return "";
    }

    def build() {
        buildUnix();
        addJreOnTopOfUnixAndZip();
        makeEnterprise();
    }
    
    def getSmartsPluginName(){
        def path = new File(env.distribution);
        def SPName;
        path.eachFile{
            if(it.name.indexOf("SmartsPlugin") != -1) SPName = it.name;
        }
        return SPName;
    }
    
    def testBuild() {
        TEST = true;
        createDirectories("Unix", false);
        smartsBuild.build();
        def SPName = getSmartsPluginName();
        ant.unzip(src:"$env.distribution/$SPName", dest:env.dist_rapid_server);
        ant.copy(tofile: "$env.dist_rapid_suite/../conf/groovy-starter.conf", file:"${env.dev_docs}/groovy-starter-for-tests.conf", overwrite:"true")
        ant.copy(todir: "$env.dist_rapid_suite/grails-app/domain") {
            ant.fileset(dir: "$env.rapid_cmdb_cvs/grails-app/domain") {
                ant.include(name: "*.groovy")
                ant.include(name: "test/*")
            }
        }
    }

    def addJreOnTopOfUnixAndZip() {
        ant.copy(todir: "$env.dist_rapid_server/jre") {
            ant.fileset(dir: "$env.jreDir")
        }
        def versionDate = getVersionWithDate();
        def zipFileName = "$env.distribution/RI_Windows$versionDate" + ".zip"
        ant.zip(destfile: zipFileName) {
            ant.zipfileset(dir: "$env.distribution/RapidServer", prefix: "RapidServer")
        }
    }

    def buildWindows() {
        buildPerOS("Windows");
    }

    def buildUnix() {
        buildPerOS("Unix");
    }

    def createDirectories(osType){
       createDirectories(osType, true);
    }
    def createDirectories(osType, willDeleteModeler) {
        if(osType == "Windows"){
            rapidCMDBBuild.buildWindowsWithPlugins();    
        }
        else{
            rapidCMDBBuild.buildUnixWithPlugins();
        }
        ant.delete(dir: env.dist_rapid_server);    

        def rapidCmdb = listFiles(new File(env.distribution), "RapidCMDB");
        ant.unzip(src: rapidCmdb.absolutePath, dest: env.distribution);
        if(willDeleteModeler){
            ant.delete(dir: env.dist_modeler);
        }

        def rapidUiPlugin = listFiles(new File(env.distribution), "grails-rapid-ui");
        installPlugin(rapidUiPlugin, env.dist_rapid_suite, [Ant: ant], [:]);

        def rapidInsightPlugin = listFiles(new File(env.distribution), "grails-rapid-insight");
        installPlugin(rapidInsightPlugin, env.dist_rapid_suite, [Ant: ant], [:]);
    }

    def buildPerOS(osType) {
        createDirectories(osType)
        ant.copy(file: version, tofile: versionInBuild);
        setVersionAndBuildNumber(versionInBuild);
        def versionDate = getVersionWithDate();

        ant.java(fork: "true", classname: "com.ifountain.comp.utils.JsCssCombiner") {
            ant.arg(value: "-file");
            ant.arg(value: "${env.dist_rapid_suite}/grails-app/views/layouts/indexLayout.gsp");
            ant.arg(value: "-applicationPath");
            ant.arg(value: "${env.dist_rapid_suite}/web-app");
            ant.arg(value: "-target");
            ant.arg(value: "${env.dist_rapid_suite}/web-app");
            ant.arg(value: "-suffix");
            ant.arg(value: "${versionDate}");
            ant.classpath() {
                ant.pathelement(location: "${env.dist_rapid_suite_lib}/comp.jar");
                ant.pathelement(location: "${env.dist_rapid_server_lib}/commons-cli-1.0.jar");
                ant.pathelement(location: "${env.dist_rapid_server_lib}/commons-io-1.4.jar");
                ant.pathelement(location: "${env.dist_rapid_server_lib}/log4j-1.2.15.jar");
            }
        }
        ant.move(file: "${env.dist_rapid_suite}/web-app/indexLayout.gsp", todir: "${env.dist_rapid_suite}/grails-app/views/layouts");
        ant.move(file: "${env.dist_rapid_server}/licenses/RapidCMDB_license.txt", toFile: "${env.dist_rapid_server}/licenses/RapidInsightCommunityLicense.txt");
        def dbViews = ["databaseConnection", "databaseDatasource", "singleTableDatabaseDatasource"];
        dbViews.each{
           ant.copy(file: "${env.dist_rapid_suite}/web-app/dbDatasources.gsp", toFile: "${env.dist_rapid_suite}/grails-app/views/${it}/list.gsp", overwrite: true); 
        }
        def zipFileName = "${env.distribution}/RI_$osType$versionDate" + ".zip"
        ant.zip(destfile: zipFileName) {
            ant.zipfileset(dir: "$env.distribution/RapidServer", prefix: "RapidServer")
        }
        //netcoolBuild.versionNo = versionNo;
        //netcoolBuild.buildNo = buildNo;
        netcoolBuild.build();
        //smartsBuild.versionNo = versionNo;
        //smartsBuild.buildNo = buildNo
        smartsBuild.build();
        
    	hypericBuild.run([]);
    	apgBuild.run([]);
    	openNmsBuild.run([]);
    }
    
    def makeEnterprise(){
    	def versionDate = getVersionWithDate();
    	ant.unzip(src: "${env.distribution}/RI_Windows$versionDate" + ".zip", dest: env.distribution + "/WEnt");
    	ant.unzip(src: "${env.distribution}/RI_Unix$versionDate" + ".zip", dest: env.distribution + "/UEnt");
    	
    	ant.delete(file: env.distribution + "/WEnt/RapidServer/licenses/RapidInsightCommunityLicense.txt");
    	ant.delete(file: env.distribution + "/UEnt/RapidServer/licenses/RapidInsightCommunityLicense.txt");
    	ant.copy(file: "$env.rapid_cmdb_cvs/licenses/IFountain End User License Agreement.pdf", toDir: env.distribution + "/WEnt/RapidServer/licenses", overwrite: true);
    	ant.copy(file: "$env.rapid_cmdb_cvs/licenses/IFountain End User License Agreement.pdf", toDir: env.distribution + "/UEnt/RapidServer/licenses", overwrite: true);
    	
        ant.zip(destfile: "${env.distribution}/RIE_Windows$versionDate" + ".zip") {
            ant.zipfileset(dir: "$env.distribution/WEnt")
        }
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

}