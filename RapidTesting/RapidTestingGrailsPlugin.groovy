import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.GrailsClass
import org.codehaus.groovy.grails.plugins.web.ControllersGrailsPlugin
import org.springframework.context.ApplicationContext
import org.codehaus.groovy.grails.web.metaclass.RedirectDynamicMethod
import org.codehaus.groovy.grails.web.metaclass.RenderDynamicMethod
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

    def doWithSpring = {

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
        def redirect = new RedirectDynamicMethod(ctx)
        def render = new RenderDynamicMethod()
        // the redirect dynamic method

        RedirectRequests redirectRequests = new RedirectRequests("__redirect_requests__")
        mc.addMetaBeanProperty (redirectRequests)
        RedirectRequests renderRequests = new RedirectRequests("__render_requests__")
        mc.addMetaBeanProperty (renderRequests)

        mc.redirect = {Map args ->
            renderRequests.getProperty(delegate).add(args);
        }
        // the render method
        mc.render = {Object o ->
            renderRequests.getProperty(delegate).add(o?.inspect());
        }

        mc.render = {String txt ->
            renderRequests.getProperty(delegate).add(txt);
        }
        mc.render = {Map args ->
            renderRequests.getProperty(delegate).add(args);
        }
        mc.render = {Closure c ->
            renderRequests.getProperty(delegate).add(c);
        }
        mc.render = {Map args, Closure c ->
            renderRequests.getProperty(delegate).add(args);
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