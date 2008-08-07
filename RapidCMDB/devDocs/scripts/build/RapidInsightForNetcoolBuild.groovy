package build

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 18, 2008
 * Time: 1:44:17 PM
 * To change this template use File | Settings | File Templates.
 */
class RapidInsightForNetcoolBuild extends Build{
	def version = "$env.rapid_netcool/RI4NCVersion.txt"; 
	def versionInBuild = "$env.dist_rapid_suite/RI4NCVersion.txt";
	def rapidCMDBBuild = new RapidCmdbBuild();
//    def rapidCmdbBuild;
//    public RapidInsightForNetcoolBuild(rapidCmdbBuildP)
//    {
//        this.rapidCmdbBuild = rapidCmdbBuildP;
//    }
    public RapidInsightForNetcoolBuild()
    {
        this(null);
    }

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
    	buildWindows();
//    	 save the zip file
    	ant.copy(todir: env.save) {
	        ant.fileset(dir: env.distribution) {
	            ant.include(name: "RapidCMDB*.zip")
	            ant.include(name: "RapidInsight*.zip")
	        }
	    }    	
    	buildUnix();
        //bring back windows zips to distribution
		ant.copy(todir: env.distribution) {
		    ant.fileset(dir: env.save) {
		        ant.include(name: "RapidCMDB*.zip")
		        ant.include(name: "RapidInsight*.zip")
		    }
		}
    }

    def buildWindows(){
    	rapidCMDBBuild.buildWindows();
    	buildPerOS("Windows");
    }
    
    def buildUnix(){
    	rapidCMDBBuild.buildUnix();
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



    def clean() {
        ant.delete(dir: env.distribution);
        ant.delete(dir: "$env.basedir/build");
    }
}