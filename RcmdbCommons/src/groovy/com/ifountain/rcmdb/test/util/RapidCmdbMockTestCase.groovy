package com.ifountain.rcmdb.test.util

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.support.MockApplicationContext
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.codehaus.groovy.grails.commons.DefaultGrailsApplication
import org.codehaus.groovy.grails.plugins.DefaultGrailsPlugin
import org.codehaus.groovy.grails.commons.spring.GrailsRuntimeConfigurator
import org.codehaus.groovy.grails.commons.spring.WebRuntimeSpringConfiguration
import org.springframework.mock.web.MockServletContext
import org.codehaus.groovy.grails.support.MockResourceLoader
import org.codehaus.groovy.grails.plugins.DefaultPluginMetaManager
import org.springframework.core.io.Resource
import com.ifountain.rcmdb.test.util.grails.MockGrailsPluginManager
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.codehaus.groovy.grails.plugins.PluginMetaManager
import org.springframework.context.ApplicationContext
import org.codehaus.groovy.grails.compiler.injection.GrailsAwareClassLoader
import org.codehaus.groovy.grails.compiler.injection.ClassInjector
import org.codehaus.groovy.grails.compiler.injection.DefaultGrailsDomainClassInjector
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.apache.commons.digester.plugins.PluginManager
import org.codehaus.groovy.grails.plugins.PluginManagerHolder

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 8, 2008
 * Time: 4:28:15 PM
 * To change this template use File | Settings | File Templates.
 */
class RapidCmdbMockTestCase extends RapidCmdbTestCase{
    def servletContext
	def webRequest
	def request
	def response
    def configParams;
    GroovyClassLoader gcl
    GrailsApplication ga;
	def mockManager
    MockApplicationContext ctx;
	def originalHandler
	def springConfig
	ApplicationContext appCtx
    def loadedClasses;
    def resolver = new PathMatchingResourcePatternResolver()
    def previousGrailsApp;
    public void setUp() {
        super.setUp();
        previousGrailsApp = ApplicationHolder.application;
        configParams = [:]
        if(previousGrailsApp != null)
        {
            gcl = new GroovyClassLoader(previousGrailsApp.getClassLoader());
        }
        else
        {
            gcl = new GroovyClassLoader(this.class.classLoader);    
        }
    }

    def initialize(List classesToBeLoaded, List pluginsToLoad)
    {
        this.loadedClasses = classesToBeLoaded;
        ExpandoMetaClass.enableGlobally()
//        classesToBeLoaded.addAll(Arrays.asList(gcl.getLoadedClasses()));
        ctx = new MockApplicationContext();
        ga = new DefaultGrailsApplication(classesToBeLoaded as Class[],gcl);
        configParams.each {key,value->
            ga.getConfig().setProperty (key, value);
        }
        mockManager = new MockGrailsPluginManager(ga)
        def dependentPlugins = pluginsToLoad.collect { new DefaultGrailsPlugin(it, ga)}
        dependentPlugins.each{ mockManager.registerMockPlugin(it); it.manager = mockManager }
        mockManager.doArtefactConfiguration();
        ga.initialise()
        ApplicationHolder.application = ga
        ga.setApplicationContext(ctx);
        ctx.registerMockBean(GrailsApplication.APPLICATION_ID, ga);
        ctx.registerMockBean(GrailsRuntimeConfigurator.CLASS_LOADER_BEAN, gcl)
        ctx.registerMockBean(PluginMetaManager.BEAN_ID, new DefaultPluginMetaManager(new Resource[0]));

        ctx.registerMockBean("manager", mockManager )

        def configurator = new GrailsRuntimeConfigurator(ga)
        configurator.pluginManager = mockManager
        ctx.registerMockBean(GrailsRuntimeConfigurator.BEAN_ID, configurator )

        springConfig = new WebRuntimeSpringConfiguration(ctx)
        servletContext = new MockServletContext(new MockResourceLoader())
        springConfig.servletContext = servletContext
        ServletContextHolder.setServletContext(servletContext);

        dependentPlugins*.doWithRuntimeConfiguration(springConfig)


		appCtx = springConfig.getApplicationContext()
		mockManager.applicationContext = appCtx
		servletContext.setAttribute( GrailsApplicationAttributes.APPLICATION_CONTEXT, appCtx)
		dependentPlugins*.doWithDynamicMethods(appCtx)
		dependentPlugins*.doWithApplicationContext(appCtx)
    }

    void tearDown() {
        servletContext = null
		webRequest = null
		request = null
		response = null
		gcl = null
		ga = null
		mockManager = null
		ctx = null
		appCtx = null
    	springConfig = null
    	resolver = null
		ExpandoMetaClass.disableGlobally()
    	originalHandler = null
        ApplicationHolder.application = previousGrailsApp;
        PluginManagerHolder.pluginManager = null;
        this.loadedClasses.each{
            GroovySystem.metaClassRegistry.removeMetaClass (it);    
        }
        this.loadedClasses = null;


    }
}