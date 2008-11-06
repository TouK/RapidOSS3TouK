
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
def apps = ["RapidSearchForNetcool"]
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
            Ant.fileset(file:"${baseDir}/plugins/netcool-0.2/applications/RapidSearchForNetcool/grails-app/templates/**");
        }
        Ant.move(toDir:"${baseDir}/grails-app/conf")
        {
            Ant.fileset(file:"${baseDir}/plugins/netcool-0.2/applications/RapidSearchForNetcool/grails-app/conf/**");
        }
        Ant.move(toDir:"${baseDir}/grails-app/i18n")
        {
            Ant.fileset(file:"${baseDir}/plugins/netcool-0.2/applications/RapidSearchForNetcool/grails-app/i18n/**");
        }
        Ant.move(toDir:"${baseDir}/grails-app/views/layouts")
        {
            Ant.fileset(file:"${baseDir}/plugins/netcool-0.2/applications/RapidSearchForNetcool/grails-app/views/layouts/**");
        }
        Ant.move(toDir:"${baseDir}/plugins/netcool-0.2/grails-app")
        {
            Ant.fileset(file:"${baseDir}/plugins/netcool-0.2/applications/RapidSearchForNetcool/grails-app/**");
        }

        Ant.move(toDir:"${baseDir}/plugins/netcool-0.2/src")
        {
            Ant.fileset(file:"${baseDir}/plugins/netcool-0.2/applications/RapidSearchForNetcool/src/**");
        }

        Ant.move(toDir:"${baseDir}/scripts")
        {
            Ant.fileset(file:"${baseDir}/plugins/netcool-0.2/applications/RapidSearchForNetcool/scripts/**");
        }
        Ant.move(toDir:"${baseDir}/web-app")
        {
            Ant.fileset(file:"${baseDir}/plugins/netcool-0.2/applications/RapidSearchForNetcool/web-app/**");
        }
        Ant.move(file:"${baseDir}/plugins/netcool-0.2/applications/RapidSearchForNetcool/rs.exe", toDir:"${baseDir}")
    }
}

Ant.move(toDir:"${baseDir}/generatedModels")
{
    Ant.fileset(dir:"${baseDir}/plugins/netcool-0.2/generatedModels");
}

Ant.move(toDir:"${baseDir}/grails-app/controllers")
{
    Ant.fileset(file:"${baseDir}/plugins/netcool-0.2/grails-app/controllers/NetcoolEventController.groovy");
    Ant.fileset(file:"${baseDir}/plugins/netcool-0.2/grails-app/controllers/NetcoolJournalController.groovy");
}
Ant.move(toDir:"${baseDir}/grails-app/views/netcoolEvent")
{
    Ant.fileset(dir:"${baseDir}/plugins/netcool-0.2/grails-app/views/netcoolEvent");
}

Ant.move(toDir:"${baseDir}/grails-app/views/netcoolJournal")
{
    Ant.fileset(dir:"${baseDir}/plugins/netcool-0.2/grails-app/views/netcoolJournal");
}

Ant.move(toDir:"${baseDir}/grails-app/domain")
{
    Ant.fileset(file:"${baseDir}/plugins/netcool-0.2/grails-app/domain/NetcoolEvent.groovy");
    Ant.fileset(file:"${baseDir}/plugins/netcool-0.2/grails-app/domain/NetcoolJournal.groovy");
}

Ant.move(toDir:"${baseDir}/operations")
{
    Ant.fileset(file:"${baseDir}/plugins/netcool-0.2/operations/NetcoolEventOperations.groovy");
}
