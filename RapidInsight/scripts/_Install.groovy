/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Nov 12, 2008
 * Time: 9:32:54 AM
 */
//
// This script is executed by Grails after plugin was installed to project.
// This script is a Gant script so you can use all special variables provided
// by Gant (such as 'baseDir' which points on project base dir). You can
// use 'Ant' to access a global instance of AntBuilder
//

Ant.property(environment: "env")
def pluginName = "rapid-insight-0.1"
grailsHome = Ant.antProject.properties."env.GRAILS_HOME"
def baseDir = System.getProperty("base.dir");

Ant.move(toDir: "${baseDir}/grails-app/conf") {
    Ant.fileset(dir: "${baseDir}/plugins/${pluginName}/grails-app/conf");
}
Ant.move(toDir: "${baseDir}/grails-app/views/layouts") {
    Ant.fileset(dir: "${baseDir}/plugins/${pluginName}/grails-app/views/layouts");
}

Ant.move(toDir: "${baseDir}/scripts") {
    Ant.fileset(file: "${baseDir}/plugins/${pluginName}/scripts/**"){
        Ant.exclude(name: "_Install.groovy")   
        Ant.exclude(name: "_Upgrade.groovy")   
    }
}
Ant.move(toDir: "${baseDir}/web-app") {
    Ant.fileset(file: "${baseDir}/plugins/${pluginName}/web-app/**");
}
Ant.move(toDir:"${baseDir}/grails-app/controllers")
{
    Ant.fileset(file:"${baseDir}/plugins/${pluginName}/grails-app/controllers/**");
}

Ant.move(toDir:"${baseDir}/grails-app/views")
{
    Ant.fileset(dir:"${baseDir}/plugins/${pluginName}/grails-app/views")
}
Ant.mkdir(dir : "${baseDir}/generatedModels/grails-app/domain");
Ant.copy(toDir:"${baseDir}/generatedModels/grails-app/domain")
{
    Ant.fileset(file:"${baseDir}/plugins/${pluginName}/grails-app/domain/**");
}
Ant.move(toDir:"${baseDir}/grails-app/domain")
{
    Ant.fileset(file:"${baseDir}/plugins/${pluginName}/grails-app/domain/**");
}
Ant.move(toDir:"${baseDir}/operations")
{
    Ant.fileset(file:"${baseDir}/plugins/${pluginName}/operations/**");
}

Ant.move(file:"${baseDir}/plugins/${pluginName}/rs.exe", toDir:"${baseDir}")