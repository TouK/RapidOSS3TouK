/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
//
// This script is executed by Grails after plugin was installed to project.
// This script is a Gant script so you can use all special variables provided
// by Gant (such as 'baseDir' which points on project base dir). You can
// use 'Ant' to access a global instance of AntBuilder
//
// For example you can create directory under project tree:
// Ant.mkdir(dir:"D:\Workspace\RapidModules/RapidUI/grails-app/jobs")
//

Ant.property(environment:"env")
grailsHome = Ant.antProject.properties."env.GRAILS_HOME"
def baseDir = System.getProperty("base.dir");
Ant.move(toDir:"$baseDir/web-app")
{
    Ant.fileset(dir: "$baseDir/plugins/rapid-ui-0.1/web-app")
    {
        Ant.exclude(name: "**/test/**")        
    }

}
Ant.move(toDir:"$baseDir/grails-app/i18n")
{
    Ant.fileset(dir: "$baseDir/plugins/rapid-ui-0.1/grails-app/i18n")
}
if(new File("$baseDir/plugins/rapid-ui-0.1/src").exists())
{
    Ant.move(toDir:"$baseDir/src")
    {
        Ant.fileset(dir: "$baseDir/plugins/rapid-ui-0.1/src")
    }
}

