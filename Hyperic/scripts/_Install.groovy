
//
// This script is executed by Grails after plugin was installed to project.
// This script is a Gant script so you can use all special variables provided
// by Gant (such as 'baseDir' which points on project base dir). You can
// use 'Ant' to access a global instance of AntBuilder
//
// For example you can create directory under project tree:
// Ant.mkdir(dir:"D:\Workspace\RapidModules/Netcool/grails-app/jobs")
//

Ant.property(environment:"env")
grailsHome = Ant.antProject.properties."env.GRAILS_HOME"
def baseDir = System.getProperty("base.dir");

Ant.move(toDir:"${baseDir}/generatedModels/grails-app/domain")
{
    Ant.fileset(file:"${baseDir}/plugins/hyperic-0.1/generatedModels/*");
}

Ant.move(toDir:"${baseDir}/grails-app/controllers")
{
    Ant.fileset(file:"${baseDir}/plugins/hyperic-0.1/grails-app/controllers/*");
}

Ant.move(toDir:"${baseDir}/grails-app/views/hypericEvent")
{
    Ant.fileset(dir:"${baseDir}/plugins/hyperic-0.1/grails-app/views/hypericEvent");
}

Ant.move(toDir:"${baseDir}/grails-app/views/hypericServer")
{
    Ant.fileset(dir:"${baseDir}/plugins/hyperic-0.1/grails-app/views/hypericServer");
}

Ant.move(toDir:"${baseDir}/grails-app/views/resource")
{
    Ant.fileset(dir:"${baseDir}/plugins/hyperic-0.1/grails-app/views/resource");
}

Ant.move(toDir:"${baseDir}/grails-app/views/platform")
{
    Ant.fileset(dir:"${baseDir}/plugins/hyperic-0.1/grails-app/views/platform");
}

Ant.move(toDir:"${baseDir}/grails-app/views/server")
{
    Ant.fileset(dir:"${baseDir}/plugins/hyperic-0.1/grails-app/views/server");
}

Ant.move(toDir:"${baseDir}/grails-app/views/service")
{
    Ant.fileset(dir:"${baseDir}/plugins/hyperic-0.1/grails-app/views/service");
}

Ant.move(toDir:"${baseDir}/grails-app/domain")
{
    Ant.fileset(file:"${baseDir}/plugins/hyperic-0.1/grails-app/domain/*");
}

Ant.move(toDir:"${baseDir}/scripts")
{
    Ant.fileset(file:"${baseDir}/plugins/hyperic-0.1/scripts/Hyperic*Integration.groovy");
    Ant.fileset(file:"${baseDir}/plugins/hyperic-0.1/scripts/Startup.groovy");
}

Ant.move(toDir:"${baseDir}/grails-app/conf")
{
    Ant.fileset(file:"${baseDir}/plugins/hyperic-0.1/grails-app/conf/StartupScriptsConfig.groovy");
}

