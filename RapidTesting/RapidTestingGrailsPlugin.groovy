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
import org.codehaus.groovy.grails.commons.GrailsClass
import org.codehaus.groovy.grails.web.metaclass.RedirectDynamicMethod
import org.codehaus.groovy.grails.web.metaclass.RenderDynamicMethod
import org.springframework.context.ApplicationContext
import org.codehaus.groovy.grails.plugins.web.ControllersGrailsPlugin
import com.ifountain.testing.TestLock

class RapidTestingGrailsPlugin {
    
    def version = 0.1
    def dependsOn = [:]

    def author = ""
    def authorEmail = ""
    def title = "A testing plugin"
    def description = '''A testing plugin'''
    def loadAfter = ['contollers','rapid-domain-class']
    // URL to the plugin's documentation
    def documentation = "http://grails.org/RapidTesting+Plugin"
    def config;
    def doWithSpring = {
        config = getConfiguration(parentCtx)
    }

    def doWithApplicationContext = { applicationContext ->
    }

    def doWithWebDescriptor = { xml ->
    }

    def onConfigChange = { event ->
    }

    def doWithDynamicMethods = { ctx ->
        for (GrailsClass controller in application.controllerClasses) {
            MetaClass mc = controller.metaClass
            if(controller.name != TestController.name)
            {
                registerControllerMethods(mc, ctx)
            }
        }
    }

    def onChange = { event ->
        println event.source
    }


    def registerControllerMethods(MetaClass mc, ApplicationContext ctx) {
    }

    private getConfiguration = { resourceLoader ->
       try {
           return Class.forName('RapidTestingConfiguration', true, Thread.currentThread().contextClassLoader).newInstance()
       } catch (ClassNotFoundException e) {
           return null
       }
    }
}




class RedirectRequests extends MetaBeanProperty
{
    WeakHashMap map = new WeakHashMap()
    public RedirectRequests(String prop) {
        super(prop, List,null,null); //To change body of overridden methods use File | Settings | File Templates.
    }


    public Object getProperty(Object o) {
        def requests = map[o];
        if(!requests)
        {
            requests = []
            map[o] = requests;
        }
        return requests;
    }

    public void setProperty(Object o, Object o1)
    {
    }

}