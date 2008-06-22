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