package build
/**
 * Created by IntelliJ IDEA.
 * User: mustafa s
 * Date: Aug 22, 2009
 * Time: 4:14:01 PM
 * To change this template use File | Settings | File Templates.
 */
class RemoteDevelopmentBuild extends Build{
    static void main(String[] args) {
        long t = System.currentTimeMillis();
        RemoteDevelopmentBuild remoteDevBuild = new RemoteDevelopmentBuild();
        remoteDevBuild.build();
        println "Build finished in ${(System.currentTimeMillis() - t)/1000.0} secs."
    }

    def build() {
        ant.delete(dir: "${env.dist_rapid_server}/remoteDevelopment");
        ant.copy(todir: "$env.distribution/remoteDevelopment") {
            ant.fileset(dir: "$env.rapid_cmdb_cvs/devDocs/developerUtilities/remoteDevelopmentSolution") {
                ant.exclude(name: ".SVN")
                ant.exclude(name: ".svn")
            };
        }
        def zipFileName = "${env.distribution}/remoteDev" + ".zip"
        ant.zip(destfile: zipFileName) {
            ant.zipfileset(dir: "$env.distribution/remoteDevelopment", prefix: "RemoteDevelopmentSolution");
        }
    }
}