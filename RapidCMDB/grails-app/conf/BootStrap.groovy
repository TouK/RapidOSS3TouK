import auth.Role
import auth.RsUser
import auth.UserRoleRel
import com.ifountain.rcmdb.domain.converter.DateConverter
import com.ifountain.rcmdb.domain.converter.DoubleConverter
import com.ifountain.rcmdb.domain.converter.LongConverter
import com.ifountain.rcmdb.domain.converter.RapidConvertUtils
import com.ifountain.rcmdb.scripting.ScriptManager
import com.ifountain.rcmdb.scripting.ScriptScheduler
import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.ifountain.rcmdb.util.RapidStringUtilities
import datasource.RCMDBDatasource
import datasource.SnmpDatasource
import model.ModelAction
import model.PropertyAction
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.jsecurity.crypto.hash.Sha1Hash
import script.CmdbScript
import com.ifountain.rcmdb.domain.generation.ModelGenerator

class BootStrap {
    def quartzScheduler;
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
        ModelGenerator.getInstance().initialize (baseDirectory, tempDirectory, System.getProperty("base.dir"));
    }

    def registerUtilities()
    {
        RapidStringUtilities.registerStringUtils();
    }

    def initializeScripting()
    {
        ScriptManager.getInstance().initialize(ApplicationHolder.application.classLoader, System.getProperty("base.dir"), new StartupScriptsConfig().scripts);
        ScriptScheduler.getInstance().initialize(quartzScheduler);
        CmdbScript.searchEvery("scheduled:true AND enabled:true").each {
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

    def corrrectModelData()
    {
        def changedModelProperties = [:]
        PropertyAction.list().each {PropertyAction propAction ->
            if (!propAction.willBeDeleted)
            {
                DefaultGrailsDomainClass currentDomainObject = ApplicationHolder.application.getDomainClass(propAction.modelName);
                def modelProps = changedModelProperties[propAction.modelName];
                if (modelProps == null)
                {
                    modelProps = [:]
                    changedModelProperties[propAction.modelName] = modelProps;
                }
                propAction.defaultValue = currentDomainObject.clazz.newInstance()[propAction.propName];
                propAction.propType = currentDomainObject.getPropertyByName(propAction.propName).type;
                modelProps[propAction.propName] = propAction;
            }
        }
        int batch = 1000;
        ModelAction.list().each{ModelAction modelAction->
            if(modelAction.action == ModelAction.DELETE_ALL_INSTANCES)
            {
                DefaultGrailsDomainClass currentDomainObject =ApplicationHolder.application.getDomainClass(modelAction.modelName);
                if(currentDomainObject)
                {
                    currentDomainObject.clazz.metaClass.invokeStaticMethod (currentDomainObject.clazz, "unindex", [] as Object[]);
                }
            }
            modelAction.remove();
        }

        changedModelProperties.each {String modelName, Map modelProps ->
            DefaultGrailsDomainClass currentDomainObject = ApplicationHolder.application.getDomainClass(modelName);
            if (currentDomainObject)
            {
                Class currentModelClass = currentDomainObject.clazz;
                int index = 0;
                while (true)
                {
                    def res = currentModelClass.metaClass.invokeStaticMethod(currentModelClass, "search", ["id:[0 TO *]", [max: batch, offset: index]] as Object[]);
                    res.results.each {modelInstance ->
                        modelProps.each {propName, PropertyAction action ->
                            def propVal = modelInstance[propName];

                            if (action.action == PropertyAction.CLEAR_RELATION)
                            {
                                if (propVal instanceof Collection)
                                {
                                    propVal.clear();
                                }
                                else
                                {
                                    modelInstance[propName] = null;
                                }
                            }
                            else if (action.action == PropertyAction.SET_DEFAULT_VALUE)
                            {
                                modelInstance[propName] = action.defaultValue;
                            }
                            action.remove();
                        }
                        modelInstance.reindex();
                    }
                    index += batch;
                    if (res.total < index)
                    {
                        break;
                    }
                }
            }
        }
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

    def registerDefaultConverters()
    {
        def dateFormat = ConfigurationHolder.getConfig().toProperties()["rapidcmdb.date.format"];
        RapidConvertUtils.getInstance().register(new DateConverter(dateFormat), Date.class)
        RapidConvertUtils.getInstance().register(new LongConverter(), Long.class)
        RapidConvertUtils.getInstance().register(new DoubleConverter(), Double.class)
    }


    def destroy = {
        SnmpDatasource.list().each {
            it.close();
        }
        ScriptManager.getInstance().destroy();
    }

}