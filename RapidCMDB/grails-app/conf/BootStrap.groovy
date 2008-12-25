import auth.Group
import auth.Role
import auth.RsUser
import com.ifountain.rcmdb.converter.*
import com.ifountain.rcmdb.datasource.ListeningAdapterManager
import com.ifountain.rcmdb.domain.DomainLockManager
import com.ifountain.rcmdb.domain.DomainMethodExecutor
import com.ifountain.rcmdb.domain.generation.DataCorrectionUtilities
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.rcmdb.scripting.ScriptManager
import com.ifountain.rcmdb.scripting.ScriptScheduler
import com.ifountain.rcmdb.scripting.ScriptingUtils
import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.ifountain.rcmdb.util.RapidDateUtilities
import com.ifountain.rcmdb.util.RapidStringUtilities
import datasource.BaseListeningDatasource
import datasource.RCMDBDatasource
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.compass.core.Compass
import org.jsecurity.crypto.hash.Sha1Hash
import org.springframework.web.context.support.WebApplicationContextUtils
import script.CmdbScript

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
    Thread listeningScriptInitializerThread;
    def init = {servletContext ->
        initializeLockManager();
        registerUtilities();
        registerDefaultConverters();
        initializeModelGenerator();
        registerDefaultUsers();
        registerDefaultDatasources();
        corrrectModelData();
        initializeScripting();
    }
    def initializeLockManager()
    {
        DomainLockManager.initialize(30000, Logger.getLogger(DomainLockManager.class));
        DomainMethodExecutor.setMaxNumberOfRetries(20); 
    }
    def initializeModelGenerator()
    {
        String baseDirectory = ApplicationHolder.application.config.toProperties()["rapidCMDB.base.dir"];
        String tempDirectory = ApplicationHolder.application.config.toProperties()["rapidCMDB.temp.dir"];
        ModelGenerator.getInstance().initialize(baseDirectory, tempDirectory, System.getProperty("base.dir"));
    }

    def registerUtilities()
    {
        RapidStringUtilities.registerStringUtils();
        RapidDateUtilities.registerDateUtils();
    }

    def initializeScripting()
    {
        CmdbScript.list().each{
            CmdbScript.configureScriptLogger(it);
        }
        def baseDir = System.getProperty("base.dir");

        //ScriptScheduler and ListeningAdapterManager should be initialized in order for the startup scripts to use them
        def quartzScheduler = ServletContextHolder.servletContext.getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT).getBean("quartzScheduler")
        ScriptScheduler.getInstance().initialize(quartzScheduler);
        ListeningAdapterManager.getInstance().initialize();
        
        def startupScripts = ScriptingUtils.getStartupScriptList(baseDir, ApplicationHolder.application.getClassLoader());
        ScriptManager.getInstance().initialize(ApplicationHolder.application.classLoader, System.getProperty("base.dir"), startupScripts);


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

        listeningScriptInitializerThread = Thread.start{
            BaseListeningDatasource.searchEvery("isSubscribed:true").each {BaseListeningDatasource ds ->
                if (ds.listeningScript) {
                    try {
                        log.debug("Starting listening script ${ds.listeningScript}")
                        CmdbScript.startListening(ds.listeningScript);
                        log.info("Listening script ${ds.listeningScript} successfully started.")
                    }
                    catch (e) {
                        log.warn("Error starting listening script ${ds.listeningScript}. Reason: ${e.getMessage()}");
                    }
                }
            }
        }
    }

    def corrrectModelData()
    {
        DataCorrectionUtilities.dataCorrectionAfterReloadStep();
    }

    def registerDefaultDatasources()
    {
        def rcmdbDatasource = RCMDBDatasource.findByName(RapidCMDBConstants.RCMDB);
        if (rcmdbDatasource == null) {
            RCMDBDatasource.add(name: RapidCMDBConstants.RCMDB);
        }
    }

    def registerDefaultUsers()
    {
        def userRole = Role.add(name: Role.USER);
        def adminRole = Role.add(name: Role.ADMINISTRATOR);
        def adminGroup = Group.add(name: RsUser.RSADMIN, role: adminRole);
        def adminUser = RsUser.add(username: RsUser.RSADMIN, passwordHash: new Sha1Hash("changeme").toHex());
        adminUser.addRelation(groups:adminGroup);

    }

    def registerDefaultConverters()
    {
        def dateFormat = ConfigurationHolder.getConfig().toProperties()["rapidcmdb.date.format"];
        RapidConvertUtils.getInstance().register(new DateConverter(dateFormat), Date.class)
        RapidConvertUtils.getInstance().register(new LongConverter(), Long.class)
        RapidConvertUtils.getInstance().register(new DoubleConverter(), Double.class)
        RapidConvertUtils.getInstance().register(new BooleanConverter(), Boolean.class)
    }


    def destroy = {
        if(listeningScriptInitializerThread != null && listeningScriptInitializerThread.isAlive())
        {
            log.info("Stopping listening script initializer thread");
            listeningScriptInitializerThread.interrupt();
            listeningScriptInitializerThread.join();
            log.info("Stopped listening script initializer thread");
        }
        ListeningAdapterManager.destroyInstance();
        ScriptManager.getInstance().destroy();
        def servletCtx = ServletContextHolder.getServletContext()
        def webAppCtx = WebApplicationContextUtils.getWebApplicationContext(servletCtx)
        Compass compass = webAppCtx.getBean("compass")
        if(compass)
        {
            compass.close();
        }
    }

}