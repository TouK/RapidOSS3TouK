package build

import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.RegexFileFilter
import org.apache.commons.io.filefilter.FalseFileFilter
import org.apache.commons.lang.StringUtils

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 18, 2008
 * Time: 1:44:17 PM
 * To change this template use File | Settings | File Templates.
 */
class RapidSearchForNetcoolBuild extends Build{
    def rapidCmdbBuild;
    public RapidSearchForNetcoolBuild(runRapidCmdbBuild)
    {
        if(runRapidCmdbBuild)
        {
            rapidCmdbBuild = new RapidCmdbBuild();
        }
    }
    public RapidSearchForNetcoolBuild()
    {
        this(true);
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

        def rapidCmdb = FileUtils.listFiles(new File(env.distribution), new RegexFileFilter("RapidCMDB.*\\.zip"), new FalseFileFilter());
        ant.unzip(src: rapidCmdb[0].absolutePath, dest: env.distribution);
        ant.delete(dir:env.dist_rapid_cmdb_modeler);

        def netcoolPlugin = FileUtils.listFiles(new File(env.distribution), new RegexFileFilter(".*netcool.*\\.zip"), new FalseFileFilter());
        installPlugin(netcoolPlugin[0], env.dist_rapid_cmdb, [Ant:ant], ["netcool_applications":"1"]);

        def rapidUiPlugin = FileUtils.listFiles(new File(env.distribution), new RegexFileFilter(".*rapid-ui.*\\.zip"), new FalseFileFilter());
        installPlugin(rapidUiPlugin[0], env.dist_rapid_cmdb, [Ant:ant], [:]);
        def zipFileName = "${env.distribution}/RapidSearchForNetcool.zip"
        ant.zip(destfile: zipFileName) {
           ant.zipfileset(dir : "$env.distribution/RapidServer", prefix:"RapidServer")
        }
    }



    def clean() {
        ant.delete(dir: env.distribution);
        ant.delete(dir: "$env.basedir/build");
    }
}