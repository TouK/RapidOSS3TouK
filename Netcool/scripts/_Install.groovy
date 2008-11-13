
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
def choices = System.getProperty("netcool_applications");
def apps = ["RapidInsightForNetcool"]
def pluginName = "netcool-0.2"
if(choices == null)
{
    println "Netcool plugin has following applications. Select one of them to install or skip:"
    for(int i=0; i < apps.size(); i++)
    {
        println "${i+1}. ${apps[i]}"
    }

    def buffReader = new BufferedReader(new InputStreamReader(System.in));
    def choice = buffReader.readLine();
    choice = choice.trim()
    choices = choice.split(",", -1);
}
choices.each{
    if(it == "1")
    {
        Ant.move(toDir:"${baseDir}/grails-app/templates")
        {
            Ant.fileset(file:"${baseDir}/plugins/${pluginName}/applications/RapidInsightForNetcool/grails-app/templates/**");
        }
        Ant.move(toDir:"${baseDir}/grails-app/conf")
        {
            Ant.fileset(file:"${baseDir}/plugins/${pluginName}/applications/RapidInsightForNetcool/grails-app/conf/**");
        }
        Ant.move(toDir:"${baseDir}/grails-app/i18n")
        {
            Ant.fileset(file:"${baseDir}/plugins/${pluginName}/applications/RapidInsightForNetcool/grails-app/i18n/**");
        }
        Ant.move(toDir:"${baseDir}/grails-app/views")
        {
            Ant.fileset(dir:"${baseDir}/plugins/${pluginName}/applications/RapidInsightForNetcool/grails-app/views");
        }
        Ant.mkdir(dir:"${baseDir}/generatedModels/grails-app/domain")
        Ant.copy(toDir:"${baseDir}/generatedModels/grails-app/domain")
        {
            Ant.fileset(file:"${baseDir}/plugins/${pluginName}/applications/RapidInsightForNetcool/grails-app/domain/*.groovy");
        }
        Ant.move(toDir:"${baseDir}/grails-app/controllers")
        {
            Ant.fileset(dir:"${baseDir}/plugins/${pluginName}/applications/RapidInsightForNetcool/grails-app/controllers");
        }
         Ant.move(toDir:"${baseDir}/grails-app/domain")
        {
            Ant.fileset(dir:"${baseDir}/plugins/${pluginName}/applications/RapidInsightForNetcool/grails-app/domain");
        }
        Ant.move(toDir:"${baseDir}/plugins/${pluginName}/src")
        {
            Ant.fileset(file:"${baseDir}/plugins/${pluginName}/applications/RapidInsightForNetcool/src/**");
        }

        Ant.move(toDir:"${baseDir}/scripts")
        {
            Ant.fileset(file:"${baseDir}/plugins/${pluginName}/applications/RapidInsightForNetcool/scripts/**");
        }
        Ant.move(toDir:"${baseDir}/web-app")
        {
            Ant.fileset(file:"${baseDir}/plugins/${pluginName}/applications/RapidInsightForNetcool/web-app/**");
        }
        Ant.move(toDir:"${baseDir}/operations")
        {
            Ant.fileset(file:"${baseDir}/plugins/${pluginName}/applications/RapidInsightForNetcool/operations/**");
        }
    }
}

Ant.move(toDir:"${baseDir}/operations")
{
    Ant.fileset(file:"${baseDir}/plugins/${pluginName}/operations/**");
}
