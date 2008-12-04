package build
/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 4, 2008
 * Time: 10:35:09 AM
 * To change this template use File | Settings | File Templates.
 */
class OpenNmsBuild extends Build{
	def version = "$env.rapid_opennms/RIOpenNmsVersion.txt";
	def versionInBuild = "$env.dist_modules_rapid_suite/RIOpenNmsVersion.txt"; 
	
   static void main(String[] args) {
        OpenNmsBuild openNmsBuild = new OpenNmsBuild();
        openNmsBuild.run(args);
    }

    def clean() {
        ant.delete(dir: env.dist_modules);
        ant.mkdir(dir: env.dist_modules);
    }
    def build() {
        clean();
        ant.copy(file: version, tofile: versionInBuild);
        setVersionAndBuildNumber(versionInBuild);
        def versionDate = getVersionWithDate();
        ant.copy(todir: "$env.dist_modules_rapid_suite/grails-app") {
            ant.fileset(dir: "$env.rapid_opennms/grails-app")
        }
        ant.copy(todir: "$env.dist_modules_rapid_suite/operations") {
            ant.fileset(dir: "$env.rapid_opennms/operations")
        }
        ant.copy(todir: "$env.dist_modules_rapid_suite/src/groovy") {
            ant.fileset(dir: "$env.rapid_opennms/src/groovy")
        }
        ant.copy(toDir: "${env.dist_modules_rapid_suite}/generatedModels/grails-app/domain") {
            ant.fileset(file: "${env.rapid_opennms}/applications/RapidInsight/grails-app/domain/*.groovy");
        }
        ant.copy(todir: "$env.dist_modules_rapid_suite") {
            ant.fileset(dir: "$env.rapid_opennms/applications/RapidInsight")
        }

        ant.zip(destfile: "$env.distribution/OpenNmsPlugin_$versionDate" + ".zip") {
            ant.zipfileset(dir: "$env.dist_modules")
        }
        ant.zip(destfile: "${env.distribution}/opennms-RI-plugin_$versionDate" + ".zip"){
            ant.zipfileset(dir:"${env.rapid_opennms}/integration")
        }
    }
}