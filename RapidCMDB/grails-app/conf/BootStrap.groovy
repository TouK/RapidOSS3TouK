import auth.Group
import auth.Role
import auth.RsUser
import com.ifountain.rcmdb.datasource.ListeningAdapterManager
import com.ifountain.rcmdb.domain.converter.*
import com.ifountain.rcmdb.domain.generation.DataCorrectionUtilities
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.rcmdb.scripting.ScriptManager
import com.ifountain.rcmdb.scripting.ScriptScheduler
import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.ifountain.rcmdb.util.RapidDateUtilities
import com.ifountain.rcmdb.util.RapidStringUtilities
import datasource.BaseListeningDatasource
import datasource.RCMDBDatasource
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.jsecurity.crypto.hash.Sha1Hash
import script.CmdbScript
import org.compass.core.Compass
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.springframework.web.context.support.WebApplicationContextUtils
import script.CmdbScript
import com.ifountain.rcmdb.scripting.ScriptingUtils;

class BootStrap {
    def quartzScheduler;

    Thread listeningScriptInitializerThread;
    def init = {servletContext ->
        registerUtilities();
        registerDefaultConverters();
        initializeModelGenerator();
        registerDefaultUsers();
        registerDefaultDatasources();
        corrrectModelData();
        initializeScripting();
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
        ListeningAdapterManager.getInstance().destroy();
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