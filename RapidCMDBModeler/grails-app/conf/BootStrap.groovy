import auth.Role
import auth.RsUser
import auth.UserRoleRel
import com.ifountain.rcmdb.domain.converter.DateConverter
import com.ifountain.rcmdb.domain.converter.DoubleConverter
import com.ifountain.rcmdb.domain.converter.LongConverter
import com.ifountain.rcmdb.scripting.ScriptManager
import com.ifountain.rcmdb.scripting.ScriptScheduler
import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.ifountain.rcmdb.util.RapidStringUtilities
import model.DatasourceName
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.jsecurity.crypto.hash.Sha1Hash
import script.CmdbScript
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import com.ifountain.rcmdb.domain.converter.RapidConvertUtils
import com.ifountain.compass.SingleCompassSessionManager
import com.ifountain.rcmdb.domain.converter.BooleanConverter

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
        Date.metaClass.'static'.now = {
            return (long)(System.currentTimeMillis()/1000);
        }
    }

    def initializeModelGenerator()
    {
        String baseDirectory = ApplicationHolder.application.config.toProperties()["rapidCMDB.base.dir"];
        String tempDirectory = ApplicationHolder.application.config.toProperties()["rapidCMDB.temp.dir"];
        ModelGenerator.getInstance().initialize (baseDirectory, tempDirectory, System.getProperty("base.dir"));
    }

    def initializeScripting()
    {
        ScriptManager.getInstance().initialize(ApplicationHolder.application.classLoader, System.getProperty("base.dir"), new StartupScriptsConfig().scripts);
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
        def adminRole = Role.findByName("Administrator");
        if (!adminRole) {
            adminRole = Role.add(name: "Administrator");
        }
        def userRole = Role.findByName("User");
        if (!userRole) {
            userRole = Role.add(name: "User");
        }
        def adminUser = RsUser.findByUsername("rsadmin");
        if (!adminUser) {
            adminUser = RsUser.add(username: "rsadmin", passwordHash: new Sha1Hash("changeme").toHex());
        }
        UserRoleRel.add(rsUser: adminUser, role: adminRole)
        
    }


    def destroy = {
        ScriptManager.getInstance().destroy();
        SingleCompassSessionManager.destroy();
    }

}