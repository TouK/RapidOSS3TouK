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
import com.ifountain.rcmdb.domain.property.DefaultDomainClassPropertyInterceptor
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import com.ifountain.rcmdb.util.RapidCMDBConstants
import org.apache.commons.io.FileUtils
import org.compass.core.Compass
import com.ifountain.rcmdb.util.RapidStringUtilities
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.compass.index.WrapperIndexDeletionPolicy
import com.ifountain.rcmdb.domain.property.RapidCmdbDomainPropertyInterceptor
import com.ifountain.rcmdb.converter.RapidConvertUtils
import com.ifountain.rcmdb.domain.util.DomainClassDefaultPropertyValueHolder
import com.ifountain.rcmdb.domain.IdGenerator

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 8, 2008
 * Time: 4:28:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class RapidCmdbMockTestCase extends RapidCmdbTestCase{
    static String rootIndexDir = "../indexFiles";
    static{
        def rootIndexDirFile = new File(rootIndexDir);
        if(rootIndexDirFile.exists())
        {
            FileUtils.deleteDirectory (rootIndexDirFile);
        }
    }
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
//    def previousGrailsApp;
    def indexDir;
    static indexCount = 0;
    public void setUp() {
        super.setUp();
        DomainClassDefaultPropertyValueHolder.destroy();
        RapidConvertUtils.destroyInstance();

        indexCount++;
        indexDir = "${rootIndexDir}/${this.class.simpleName}${indexCount}";
        System.clearProperty("index.dir")
//        previousGrailsApp = ApplicationHolder.application;
        configParams = [:]
        CommonTestUtils.waitFor (new ClosureWaitAction({
            FileUtils.deleteDirectory(new File(indexDir));    
        }));

//        if(previousGrailsApp != null)
//        {
//            println "WITH GRAILS APPLICATION"
//            gcl = new GroovyClassLoader(previousGrailsApp.getClassLoader());
//        }
//        else
//        {
            gcl = new GroovyClassLoader(this.class.classLoader);
//        }
    }

    def initialize(List classesToBeLoaded, List pluginsToLoad)
    {
        initialize(classesToBeLoaded, pluginsToLoad, false)
    }
    def initialize(List classesToBeLoaded, List pluginsToLoad, boolean isPersistant)
    {

        if(isPersistant)
        {
            System.setProperty("index.dir", indexDir);
        }
        else
        {
            System.setProperty("index.dir", "ram://app-index");
        }
        this.loadedClasses = classesToBeLoaded;
        ExpandoMetaClass.enableGlobally()
        RapidStringUtilities.registerStringUtils();
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
        if(!configParams.containsKey(RapidCMDBConstants.PROPERTY_INTERCEPTOR_CLASS_CONFIG_NAME))
        {
            ConfigurationHolder.config.setProperty (RapidCMDBConstants.PROPERTY_INTERCEPTOR_CLASS_CONFIG_NAME, RapidCmdbDomainPropertyInterceptor.class.name);
        }

        if(!configParams.containsKey("rapidcmdb.date.format"))
        {
            ConfigurationHolder.config.setProperty ("rapidcmdb.date.format", "yyyy-dd-MM HH:mm:ss.SSS");
        }
        dependentPlugins*.doWithRuntimeConfiguration(springConfig)


		appCtx = springConfig.getApplicationContext()
		mockManager.applicationContext = appCtx
		servletContext.setAttribute( GrailsApplicationAttributes.APPLICATION_CONTEXT, appCtx)
		dependentPlugins*.doWithDynamicMethods(appCtx)
		dependentPlugins*.doWithApplicationContext(appCtx)
    }

    public void destroy()
    {
        if(appCtx)
        {
            if(appCtx.containsBean("compass"))
            {
                Compass compass = appCtx.getBean("compass");
                compass.getSearchEngineIndexManager().close();
                appCtx.getBean("compass").close()
            }
        }
        servletContext = null
        webRequest = null
        request = null
        response = null
        gcl = new GroovyClassLoader(this.class.classLoader);
        ga = null
        mockManager = null
        ctx = null
        appCtx = null
        springConfig = null
        resolver = null
        originalHandler = null
//        ApplicationHolder.application = previousGrailsApp;
        PluginManagerHolder.pluginManager = null;
        ServletContextHolder.servletContext = null;
        ExpandoMetaClass.disableGlobally()
        this.loadedClasses.each{
            GroovySystem.metaClassRegistry.removeMetaClass (it);
        }
        ExpandoMetaClass.enableGlobally();
        this.loadedClasses = null;
        WrapperIndexDeletionPolicy.clearPolicies();
        DomainClassDefaultPropertyValueHolder.destroy();
        IdGenerator.destroy();

    }

    void tearDown() {
        destroy();
        System.clearProperty("index.dir")
        CompassForTests.destroy();
        //super.tearDown();

    }
}