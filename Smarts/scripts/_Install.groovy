
/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Aug 11, 2008
 * Time: 2:14:41 PM
 */

//
// This script is executed by Grails after plugin was installed to project.
// This script is a Gant script so you can use all special variables provided
// by Gant (such as 'baseDir' which points on project base dir). You can
// use 'Ant' to access a global instance of AntBuilder
//
// For example you can create directory under project tree:
// Ant.mkdir(dir:"D:\Workspace\RapidModules/Smarts/grails-app/jobs")
//

Ant.property(environment:"env")
def pluginName = "smarts-0.1"
grailsHome = Ant.antProject.properties."env.GRAILS_HOME"
def baseDir = System.getProperty("base.dir");
def choices = System.getProperty("smarts_applications");
def apps = ["RapidInsightForSmarts"]
if(choices == null)
{
    println "Smarts plugin has following applications. Select one of them to install or skip:"
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
        Ant.move(toDir:"${baseDir}/grails-app/conf")
        {
            Ant.fileset(dir:"${baseDir}/plugins/${pluginName}/applications/RapidInsightForSmarts/grails-app/conf");
        }
        Ant.move(toDir:"${baseDir}/grails-app/i18n")
        {
            Ant.fileset(file:"${baseDir}/plugins/${pluginName}/applications/RapidInsightForSmarts/grails-app/i18n/**");
        }
        Ant.move(toDir:"${baseDir}/grails-app/views/layouts")
        {
            Ant.fileset(dir:"${baseDir}/plugins/${pluginName}/applications/RapidInsightForSmarts/grails-app/views/layouts");
        }
        Ant.move(toDir:"${baseDir}/plugins/${pluginName}/grails-app")
        {
            Ant.fileset(file:"${baseDir}/plugins/${pluginName}/applications/RapidInsightForSmarts/grails-app/**");
        }

        Ant.move(toDir:"${baseDir}/scripts")
        {
            Ant.fileset(file:"${baseDir}/plugins/${pluginName}/applications/RapidInsightForSmarts/scripts/**");
        }
        Ant.move(toDir:"${baseDir}/web-app")
        {
            Ant.fileset(file:"${baseDir}/plugins/${pluginName}/applications/RapidInsightForSmarts/web-app/**");
        }

        Ant.move(toDir:"${baseDir}/operations")
        {
            Ant.fileset(dir:"${baseDir}/plugins/${pluginName}/applications/RapidInsightForSmarts/operations");
        }
    }
}

Ant.move(toDir:"${baseDir}/grails-app/controllers")
{
    Ant.fileset(file:"${baseDir}/plugins/${pluginName}/grails-app/controllers/Rs*.groovy");
}

Ant.move(toDir:"${baseDir}/generatedModels")
{
    Ant.fileset(dir:"${baseDir}/plugins/${pluginName}/generatedModels");
}
Ant.move(toDir:"${baseDir}/grails-app/views")
{
    Ant.fileset(dir:"${baseDir}/plugins/${pluginName}/grails-app/views"){
        Ant.filename(name:"rs*/**")
    }
}
Ant.move(toDir:"${baseDir}/grails-app/domain")
{
    Ant.fileset(file:"${baseDir}/plugins/${pluginName}/grails-app/domain/Rs*.groovy");
}
Ant.move(toDir:"${baseDir}/operations")
{
    Ant.fileset(file:"${baseDir}/plugins/${pluginName}/operations/**");
}

