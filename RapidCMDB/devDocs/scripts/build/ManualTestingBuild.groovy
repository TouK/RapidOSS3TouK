package build
/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 16, 2008
 * Time: 4:24:38 PM
 * To change this template use File | Settings | File Templates.
 */
class ManualTestingBuild extends Build{

    static void main(String[] args) {
        ManualTestingBuild manualTestingBuild = new ManualTestingBuild();
        manualTestingBuild.run(args);
    }
    def build() {
        ant.zip(destfile: "$env.distribution/ManualTesting" + ".zip") {
            ant.zipfileset(dir: "$env.rapid_cmdb_cvs/devDocs/test/manualTesting")
        }
    }
}