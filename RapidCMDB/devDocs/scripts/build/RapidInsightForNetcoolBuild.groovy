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
        def zipFileName = "$env.distribution/RapidInsightForNetcool_Windows$versionDate" + ".zip"
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
        
//        def osType = "Unix";
//        if (rapidCmdb.getName().indexOf("Windows") > -1) osType = "Windows"
        def zipFileName = "${env.distribution}/RapidInsightForNetcool_$osType$versionDate" + ".zip"
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