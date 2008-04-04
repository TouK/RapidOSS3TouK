import org.codehaus.groovy.grails.commons.spring.RuntimeSpringConfiguration
import org.codehaus.groovy.grails.commons.spring.DefaultRuntimeSpringConfiguration
import org.codehaus.groovy.grails.commons.spring.GrailsRuntimeConfigurator
import org.apache.log4j.Logger;
class RapidScriptingGrailsPlugin
{
    def logger = Logger.getLogger("grails.app.plugins.RapidScripting")
    def watchedResources = ["file:./grails-app/scripts/*.groovy"]
    def version = 0.1
    def dependsOn = [:]
    def loadAfter = ['hibernate']
    def doWithSpring = {
    }

    def doWithApplicationContext = { applicationContext ->

    }

    def doWithWebDescriptor = { xml ->
    }

    def doWithDynamicMethods = { ctx ->
    }

    def onChange = { event ->
        if(event.source.getSuperclass() == Script)
        {
            logger.info ("Reloading script ${event.source.name}");
            RuntimeSpringConfiguration springConfig = event.ctx != null ? new DefaultRuntimeSpringConfiguration(event.ctx) : new DefaultRuntimeSpringConfiguration();
            GrailsRuntimeConfigurator.loadSpringGroovyResourcesIntoContext(springConfig, application.classLoader, event.ctx)
        }
    }

    def onApplicationChange = { event ->
    }


}