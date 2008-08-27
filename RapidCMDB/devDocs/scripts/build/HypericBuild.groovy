package build
/**
 * Created by IntelliJ IDEA.
 * User: deneme
 * Date: Aug 27, 2008
 * Time: 1:29:38 PM
 * To change this template use File | Settings | File Templates.
 */
class HypericBuild {
    def version = "$env.hyperic/HypVersion.txt";
    def versionInBuild = "$env.dist_rapid_suite/HypVersion.txt";
    def rapidCMDBBuild = new RapidCmdbBuild();

    static void main(String[] args) {
        HypericBuild hypericBuilder = new HypericBuild();
        hypericBuilder.run(args);
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
        def zipFileName = "$env.distribution/Hyperic_Windows$versionDate" + ".zip"
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

        def hypericPlugin = listFiles(new File(env.distribution), "grails-hyperic");
        installPlugin(hypericPlugin, env.dist_rapid_suite, [Ant:ant], ["hyperic_applications":"1"]);

        def rapidUiPlugin = listFiles(new File(env.distribution), "grails-rapid-ui");
        installPlugin(rapidUiPlugin, env.dist_rapid_suite, [Ant:ant], [:]);

        ant.copy(file : version, tofile : versionInBuild );
        setVersionAndBuildNumber(versionInBuild);
        def versionDate = getVersionWithDate();

//        def osType = "Unix";
//        if (rapidCmdb.getName().indexOf("Windows") > -1) osType = "Windows"
        def zipFileName = "${env.distribution}/Hyperic_$osType$versionDate" + ".zip"
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