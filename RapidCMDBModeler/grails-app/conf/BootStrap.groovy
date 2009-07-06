import auth.Group
import auth.Role
import auth.RsUser
import com.ifountain.compass.search.FilterSessionListener
import com.ifountain.rcmdb.converter.*
import com.ifountain.rcmdb.converter.datasource.DatasourceConversionUtils
import com.ifountain.rcmdb.domain.DomainLockManager
import com.ifountain.rcmdb.domain.DomainMethodExecutor
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.rcmdb.scripting.ScriptManager
import com.ifountain.rcmdb.scripting.ScriptScheduler
import com.ifountain.rcmdb.scripting.ScriptingUtils
import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.ifountain.rcmdb.util.RapidDateUtilities
import com.ifountain.rcmdb.util.RapidStringUtilities
import com.ifountain.session.SessionManager
import model.DatasourceName
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.springframework.web.context.support.WebApplicationContextUtils
import script.CmdbScript
import com.ifountain.rcmdb.methods.WithSessionDefaultMethod
import com.ifountain.rcmdb.methods.MethodFactory
import com.ifountain.rcmdb.domain.cache.IdCache
import com.ifountain.rcmdb.auth.SegmentQueryHelper

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
class BootStrap {
    def init = {servletContext ->
        initializeCaches();
        registerDatasourceConverters();
        initializeSessionManager();
        initializeLockManager();
        registerUtilities();
        registerDefaultConverters();
        initializeModelGenerator();
        initializeSegmentHelper();
        registerDefaultUsers();
        registerDefaultDatasourceNames();
        initializeScripting();
    }
    def initializeSegmentHelper(){
        SegmentQueryHelper.getInstance().initialize(ApplicationHolder.application.domainClasses.clazz.findAll{it.name.indexOf(".") < 0})
    }
    def initializeCaches()
    {
        IdCache.initialize(100000);
    }
    def initializeSessionManager()
    {
        SessionManager.getInstance().addSessionListener (new FilterSessionListener());
    }
    def registerDatasourceConverters()
    {
        DatasourceConversionUtils.registerDefaultConverters();
    }
    def initializeLockManager()
    {
        DomainLockManager.initialize(30000, Logger.getLogger(DomainLockManager.class));
        DomainMethodExecutor.setMaxNumberOfRetries(20);
    }
    def registerUtilities()
    {
        RapidStringUtilities.registerStringUtils();
        RapidDateUtilities.registerDateUtils();
    }

    def initializeModelGenerator()
    {
        String baseDirectory = ApplicationHolder.application.config.toProperties()["rapidCMDB.base.dir"];
        String tempDirectory = ApplicationHolder.application.config.toProperties()["rapidCMDB.temp.dir"];
        def invalidNames = [];
        def invalidNameFile = new File("invalidNames.txt");
        if (invalidNameFile.exists())
        {
            def invalidNameFileLines = invalidNameFile.readLines();
            invalidNameFileLines.each {
                invalidNames.add(it.trim())
            }
        }
        ModelGenerator.getInstance().initialize(baseDirectory, tempDirectory, System.getProperty("base.dir"));
        ModelGenerator.getInstance().setInvalidNames (invalidNames);
    }

    def initializeScripting()
    {
        def defaultMethods = [
            "${MethodFactory.WITH_SESSION_METHOD}":MethodFactory.createMethod(MethodFactory.WITH_SESSION_METHOD)
        ]
        def baseDir = System.getProperty("base.dir");
        def startupScripts = ScriptingUtils.getStartupScriptList(baseDir, ApplicationHolder.application.getClassLoader());
        ScriptManager.getInstance().initialize(ApplicationHolder.application.classLoader, System.getProperty("base.dir"), startupScripts, defaultMethods);
        def quartzScheduler = ServletContextHolder.servletContext.getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT).getBean("quartzScheduler")
        ScriptScheduler.getInstance().initialize(quartzScheduler);
        CmdbScript.searchEvery("type:${CmdbScript.SCHEDULED} AND enabled:true").each {
            try {
                if (it.scheduleType == CmdbScript.PERIODIC) {
                    ScriptScheduler.getInstance().scheduleScript(it.name, it.startDelay, it.period)
                }
                else {
                    ScriptScheduler.getInstance().scheduleScript(it.name, it.startDelay, it.cronExpression)
                }
            }
            catch (e) {
                log.warn("Error scheduling script ${it.name}: ${e.getMessage()}");
            }

        }
    }


    def registerDefaultDatasourceNames()
    {
        DatasourceName.add(name: RapidCMDBConstants.RCMDB)
    }

    def registerDefaultConverters()
    {
        def dateFormat = ConfigurationHolder.getConfig().toProperties()["rapidcmdb.date.format"];
        RapidConvertUtils.getInstance().register(new StringConverter(dateFormat), GString.class)
        RapidConvertUtils.getInstance().register(new StringConverter(dateFormat), String.class)
        RapidConvertUtils.getInstance().register(new DateConverter(dateFormat), Date.class)
        RapidConvertUtils.getInstance().register(new LongConverter(), Long.class)
        RapidConvertUtils.getInstance().register(new DoubleConverter(), Double.class)
        RapidConvertUtils.getInstance().register(new BooleanConverter(), Boolean.class)
    }

    def registerDefaultUsers()
    {
        def userRole = Role.add(name: Role.USER);
        def adminRole = Role.add(name: Role.ADMINISTRATOR);
        def adminGroup = Group.add(name: RsUser.RSADMIN, role: adminRole);
        def adminUser = RsUser.get(username: RsUser.RSADMIN);
        if (!adminUser) {
            adminUser=RsUser.addUser(username:RsUser.RSADMIN,password:"changeme",groups:[adminGroup])
        }

    }


    def destroy = {
        ScriptManager.destroyInstance();
        SessionManager.destroyInstance();
        def servletCtx = ServletContextHolder.getServletContext()
        def webAppCtx = WebApplicationContextUtils.getWebApplicationContext(servletCtx)
        def compass = webAppCtx.getBean("compass")
        if (compass)
        {
            compass.close();
        }
    }

}