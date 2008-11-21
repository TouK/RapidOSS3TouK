package build
/**
 * Created by IntelliJ IDEA.
 * User: deneme
 * Date: Aug 27, 2008
 * Time: 1:29:38 PM
 * To change this template use File | Settings | File Templates.
 */
class HypericBuild extends Build {
    static void main(String[] args) {
        HypericBuild hypericBuild = new HypericBuild();
        hypericBuild.run(args);
    }

    def clean() {
        ant.delete(dir: env.dist_modules);
        ant.mkdir(dir: env.dist_modules);
    }
    def build() {
        clean();

        ant.copy(todir: "$env.dist_modules_rapid_suite/grails-app") {
            ant.fileset(dir: "$env.rapid_hyperic/grails-app")
        }
        ant.copy(todir: "$env.dist_modules_rapid_suite/operations") {
            ant.fileset(dir: "$env.rapid_hyperic/operations")
        }
        ant.copy(todir: "$env.dist_modules_rapid_suite/src/groovy") {
            ant.fileset(dir: "$env.rapid_hyperic/src/groovy")
        }
        ant.copy(toDir: "${env.dist_modules_rapid_suite}/generatedModels/grails-app/domain") {
            ant.fileset(file: "${env.rapid_hyperic}/applications/RapidInsight/grails-app/domain/*.groovy");
        }
        ant.copy(todir: "$env.dist_modules_rapid_suite") {
            ant.fileset(dir: "$env.rapid_hyperic/applications/RapidInsight")
        }
        ant.zip(destfile: "$env.distribution/HypericPlugin.zip") {
            ant.zipfileset(dir: "$env.dist_modules")
        }
        ant.zip(destfile: "${env.distribution}/hyperic-rcmdb-plugin.zip"){
            ant.zipfileset(dir:"${env.rapid_hyperic}/integration/hyperic/plugin")
        }
    }

}