import auth.Group
import auth.Role
import auth.RsUser
import com.ifountain.rcmdb.domain.converter.*
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.rcmdb.scripting.ScriptManager
import com.ifountain.rcmdb.scripting.ScriptScheduler
import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.ifountain.rcmdb.util.RapidStringUtilities
import model.DatasourceName
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.jsecurity.crypto.hash.Sha1Hash
import script.CmdbScript
import com.ifountain.rcmdb.util.RapidDateUtilities
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.springframework.web.context.support.WebApplicationContextUtils
import com.ifountain.rcmdb.scripting.ScriptingUtils

class BootStrap {
    def quartzScheduler;
    def init = {servletContext ->
        registerUtilities();
        registerDefaultConverters();
        initializeModelGenerator();
        registerDefaultUsers();
        registerDefaultDatasourceNames();
        initializeScripting();
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
        ModelGenerator.getInstance().initialize (baseDirectory, tempDirectory, System.getProperty("base.dir"));
    }

    def initializeScripting()
    {
        def startupScripts = ScriptingUtils.getStartupScriptList(baseDir, ApplicationHolder.application.getClassLoader());
        ScriptManager.getInstance().initialize(ApplicationHolder.application.classLoader, System.getProperty("base.dir"), startupScripts);
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
        def adminUser = RsUser.add(username: RsUser.RSADMIN, passwordHash: new Sha1Hash("changeme").toHex());
        adminUser.addRelation(groups:adminGroup);
        
    }


    def destroy = {
        ScriptManager.getInstance().destroy();
        def servletCtx = ServletContextHolder.getServletContext()
        def webAppCtx = WebApplicationContextUtils.getWebApplicationContext(servletCtx)
        def compass = webAppCtx.getBean("compass")
        if(compass)
        {
            compass.close();
        }
    }

}