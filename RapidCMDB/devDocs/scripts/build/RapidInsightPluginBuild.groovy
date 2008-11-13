package build
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Nov 12, 2008
 * Time: 10:32:10 AM
 */
class RapidInsightPluginBuild extends Build{
    static void main(String[] args) {
        RapidInsightPluginBuild rapidInsightPluginBuild = new RapidInsightPluginBuild();
        rapidInsightPluginBuild.run(args);
    }
    def build() {
        createPlugin(env.rapid_insight, ["applications/**", "operations/**", "rs.exe"]);
    }
}