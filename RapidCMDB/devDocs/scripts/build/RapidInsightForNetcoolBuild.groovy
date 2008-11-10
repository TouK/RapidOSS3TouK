package build

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 18, 2008
 * Time: 1:44:17 PM
 * To change this template use File | Settings | File Templates.
 */
 
//SUPPORTED TARGETS:
//------------------	
//build			: builds RI4NC for Unix AND Windows
//buildUnix		: builds RapidCMDB for Unix
//buildWindows	: builds RapidCMDB for Windows

class RapidInsightForNetcoolBuild extends Build{
	def version = "$env.rapid_netcool/RI4NCVersion.txt"; 
	def versionInBuild = "$env.dist_rapid_suite/RI4NCVersion.txt";
	def rapidCMDBBuild = new RapidCmdbBuild();

    static void main(String[] args) {
        RapidInsightForNetcoolBuild rapidInsightForNetcoolBuilder = new RapidInsightForNetcoolBuild();
        rapidInsightForNetcoolBuilder.run(args);
    }

    def String getExcludedClasses() {
        if (!TEST) {
            return "**/*Test*, **/*Mock*, **/test/**";
        }
        return "";
    }
    
    def build(){
    	buildUnix();
    	addJreOnTopOfUnixAndZip();
    }
    
    def addJreOnTopOfUnixAndZip(){
    	ant.copy(todir: "$env.dist_rapid_server/jre") {
            ant.fileset(dir: "$env.jreDir")
        }
        def versionDate = getVersionWithDate();
        def zipFileName = "$env.distribution/RI4NC_Windows$versionDate" + ".zip"
        ant.zip(destfile: zipFileName) {
            ant.zipfileset(dir: "$env.distribution"){
            	ant.exclude(name:".project");
            	ant.exclude(name:"*.zip");
            }
        }
    }    

    def buildWindows(){
    	rapidCMDBBuild.buildWindowsWithPlugins();
    	buildPerOS("Windows");
    }
    
    def buildUnix(){
    	rapidCMDBBuild.buildUnixWithPlugins();
    	buildPerOS("Unix");
    }

    def buildPerOS(osType) {
        ant.delete(dir:env.dist_rapid_server);

        def rapidCmdb = listFiles(new File(env.distribution), "RapidCMDB");
        ant.unzip(src: rapidCmdb.absolutePath, dest: env.distribution);
        ant.delete(dir:env.dist_modeler);

        def netcoolPlugin = listFiles(new File(env.distribution), "grails-netcool");
        installPlugin(netcoolPlugin, env.dist_rapid_suite, [Ant:ant], ["netcool_applications":"1"]);

        def rapidUiPlugin = listFiles(new File(env.distribution), "grails-rapid-ui");
        installPlugin(rapidUiPlugin, env.dist_rapid_suite, [Ant:ant], [:]);

        ant.copy(file : version, tofile : versionInBuild );
        setVersionAndBuildNumber(versionInBuild);
        def versionDate = getVersionWithDate();

        ant.java(fork : "true", classname : "com.ifountain.comp.utils.JsCssCombiner"){
			ant.arg(value : "-file");
			ant.arg(value : "${env.dist_rapid_suite}/grails-app/views/layouts/indexLayout.gsp");
			ant.arg(value : "-applicationPath");
			ant.arg(value : "${env.dist_rapid_suite}/web-app");
			ant.arg(value : "-target");
			ant.arg(value : "${env.dist_rapid_suite}/web-app");
			ant.arg(value : "-suffix");
			ant.arg(value : "${versionDate}");
			ant.classpath(){
				ant.pathelement(location : "${env.dist_rapid_suite_lib}/comp.jar");
				ant.pathelement(location : "${env.dist_rapid_server_lib}/commons-cli-1.0.jar");
				ant.pathelement(location : "${env.dist_rapid_server_lib}/commons-io-1.4.jar");
				ant.pathelement(location : "${env.dist_rapid_server_lib}/log4j-1.2.15.jar");
			}
		}
		ant.move(file : "${env.dist_rapid_suite}/web-app/indexLayout.gsp", todir : "${env.dist_rapid_suite}/grails-app/views/layouts" );
//        def osType = "Unix";
//        if (rapidCmdb.getName().indexOf("Windows") > -1) osType = "Windows"
        def zipFileName = "${env.distribution}/RI4NC_$osType$versionDate" + ".zip"
        ant.zip(destfile: zipFileName) {
           ant.zipfileset(dir : "$env.distribution/RapidServer", prefix:"RapidServer")
        }
    }

    def listFiles(File rootDir, String regexp)
    {
        File file = null;
        rootDir.listFiles().each{File f->
            if(f.name.startsWith(regexp))
            {
                file = f;
                return;
            }
        }
        return file;
    }

}