package build

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 18, 2008
 * Time: 1:44:17 PM
 * To change this template use File | Settings | File Templates.
 */
class RapidSearchForNetcoolBuild extends Build{
    def rapidCmdbBuild;
    public RapidSearchForNetcoolBuild(rapidCmdbBuild)
    {
        rapidCmdbBuild = rapidCmdbBuild;
    }
    public RapidSearchForNetcoolBuild()
    {
        this(null);
    }

    static void main(String[] args) {
        RapidSearchForNetcoolBuild rapidSearchForNetcoolBuilder = new RapidSearchForNetcoolBuild();
        rapidSearchForNetcoolBuilder.run(args);
    }

    def String getExcludedClasses() {
        if (!TEST) {
            return "**/*Test*, **/*Mock*, **/test/**";
        }
        return "";
    }


    def build() {
        if(this.rapidCmdbBuild != null )
        {
            clean();
            rapidCmdbBuild.build();
        }
        ant.delete(dir:env.dist_rapid_server);

        def rapidCmdb = listFiles(new File(env.distribution), "RapidCMDB.*\\.zip");
        ant.unzip(src: rapidCmdb.absolutePath, dest: env.distribution);
        ant.delete(dir:env.dist_rapid_cmdb_modeler);

        def netcoolPlugin = listFiles(new File(env.distribution), ".*netcool.*\\.zip");
        installPlugin(netcoolPlugin, env.dist_rapid_cmdb, [Ant:ant], ["netcool_applications":"1"]);

        def rapidUiPlugin = listFiles(new File(env.distribution), ".*rapid-ui.*\\.zip");
        installPlugin(rapidUiPlugin, env.dist_rapid_cmdb, [Ant:ant], [:]);
        def zipFileName = "${env.distribution}/RapidSearchForNetcool.zip"
        ant.zip(destfile: zipFileName) {
           ant.zipfileset(dir : "$env.distribution/RapidServer", prefix:"RapidServer")
        }
    }

    def listFiles(File rootDir, String regexp)
    {
        File file = null;
        rootDir.listFiles().each{File f->
            if(f.absolutePath.matches(regexp))
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