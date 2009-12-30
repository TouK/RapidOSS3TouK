package build
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Dec 28, 2009
 * Time: 5:44:26 PM
 */
class RivermuseBuild extends Build{
    def version = "$env.rapid_rivermuse/ROSSRivermuseVersion.txt";

    static void main(String[] args) {
        RivermuseBuild rivermuseBuild = new RivermuseBuild();
        rivermuseBuild.build();
    }

    def clean(distDir) {
        if (distDir.equals(env.dist_modules)) {
            ant.delete(dir: env.dist_modules);
            ant.mkdir(dir: env.dist_modules);
        }
    }

    def build() {
        build(env.dist_modules);
    }

    def build(distDir) {
        def rapidSuiteDir = "${distDir}/RapidSuite";
        def versionInBuild = "${rapidSuiteDir}/ROSSRivermuseVersion.txt";
        clean(distDir);
        ant.copy(file: version, tofile: versionInBuild);
        setVersionAndBuildNumber(versionInBuild);
        def versionDate = getVersionWithDate();
        ant.copy(todir: "${rapidSuiteDir}/grails-app") {
            ant.fileset(dir: "$env.rapid_rivermuse/grails-app")
        }
        ant.copy(todir: "${rapidSuiteDir}/operations") {
            ant.fileset(dir: "$env.rapid_rivermuse/operations")
        }
        ant.copy(todir: "${rapidSuiteDir}/src/groovy") {
            ant.fileset(dir: "$env.rapid_rivermuse/src/groovy") {
                if (!TEST) {
                    ant.exclude(name: "**/test/**")
                }
            }
        }
        if (TEST) {
            ant.copy(todir: "${rapidSuiteDir}/test") {
                ant.fileset(dir: "$env.rapid_rivermuse/test")
            }
        }
        ant.copy(toDir: "${rapidSuiteDir}/generatedModels/grails-app/domain") {
            ant.fileset(file: "${env.rapid_rivermuse}/applications/RapidInsight/grails-app/domain/*.groovy");
        }
        ant.copy(todir: rapidSuiteDir) {
            ant.fileset(dir: "$env.rapid_rivermuse/applications/RapidInsight")
        }


        if (distDir.equals(env.dist_modules)) {
            ant.zip(destfile: "$env.distribution/RivermusePlugin$versionDate" + ".zip") {
                ant.zipfileset(dir: "$env.dist_modules")
            }
        }
        println "Rivermuse Build Done";
    }
}