package build
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Dec 3, 2008
 * Time: 2:00:12 PM
 */
class ApgBuild extends Build{
   static void main(String[] args) {
        ApgBuild apgBuild = new ApgBuild();
        apgBuild.run(args);
    }

    def clean() {
        ant.delete(dir: env.dist_modules);
        ant.mkdir(dir: env.dist_modules);
    }
    def build() {
        clean();

        ant.copy(todir: "$env.dist_modules_rapid_suite/grails-app") {
            ant.fileset(dir: "$env.rapid_apg/grails-app")
        }
        ant.copy(todir: "$env.dist_modules_rapid_suite/operations") {
            ant.fileset(dir: "$env.rapid_apg/operations")
        }
        ant.copy(todir: "$env.dist_modules_rapid_suite/src/groovy") {
            ant.fileset(dir: "$env.rapid_apg/src/groovy")
        }
//        ant.copy(toDir: "${env.dist_modules_rapid_suite}/generatedModels/grails-app/domain") {
//            ant.fileset(file: "${env.rapid_apg}/applications/RapidInsight/grails-app/domain/*.groovy");
//        }
        ant.copy(todir: "$env.dist_modules_rapid_suite") {
            ant.fileset(dir: "$env.rapid_apg/applications/RapidInsight")
        }

        ant.zip(destfile: "$env.distribution/ApgPlugin.zip") {
            ant.zipfileset(dir: "$env.dist_modules")
        }
    }
}