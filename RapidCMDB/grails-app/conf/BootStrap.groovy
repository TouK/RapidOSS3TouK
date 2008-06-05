import datasource.RCMDBDatasource
import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.ifountain.rcmdb.scripting.ScriptManager
import com.ifountain.rcmdb.scripting.ScriptScheduler
import org.jsecurity.crypto.hash.Sha1Hash
import auth.Role
import auth.RsUser
import auth.UserRoleRel
import com.ifountain.rcmdb.domain.converter.DateConverter
import com.ifountain.rcmdb.domain.converter.LongConverter
import datasource.SnmpDatasource
import com.ifountain.rcmdb.domain.converter.RapidConvertUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder
import model.PropertyAction
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import com.ifountain.rcmdb.domain.converter.DoubleConverter
import script.CmdbScript
import org.codehaus.groovy.runtime.StackTraceUtils
import model.DatasourceName
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import model.ModelAction

class BootStrap {
    def quartzScheduler;
    def init = {servletContext ->
        String baseDirectory = ApplicationHolder.application.config.toProperties()["rapidCMDB.base.dir"];
        String tempDirectory = ApplicationHolder.application.config.toProperties()["rapidCMDB.temp.dir"];
        ModelGenerator.getInstance().initialize (baseDirectory, tempDirectory, baseDirectory);
        registerDefaultConverters();

        def adminRole = Role.findByName("Administrator");
        if (!adminRole) {
            adminRole = new Role(name: "Administrator");
            adminRole.save();
        }
        def userRole = Role.findByName("User");
        if (!userRole) {
            userRole = new Role(name: "User");
            userRole.save();
        }
        def adminUser = RsUser.findByUsername("rsadmin");
        if (!adminUser) {
            adminUser = new RsUser(username: "rsadmin", passwordHash: new Sha1Hash("changeme").toHex());
            adminUser.save();
        }
        new UserRoleRel(rsUser: adminUser, role: adminRole).save()


        def rcmdbDatasource = RCMDBDatasource.findByName(RapidCMDBConstants.RCMDB);
        if (rcmdbDatasource == null) {
            new RCMDBDatasource(name: RapidCMDBConstants.RCMDB).save();
        }
        if(System.getProperty("rs.modeler") != null){
            if(DatasourceName.findByName(RapidCMDBConstants.RCMDB) == null){
                new DatasourceName(name: RapidCMDBConstants.RCMDB).save();
            }
        }

        ScriptManager.getInstance().initialize();

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
            if(modelAction.DELETE_ALL_INSTANCES)
            {
                DefaultGrailsDomainClass currentDomainObject =ApplicationHolder.application.getDomainClass(modelAction.modelName);
                if(currentDomainObject)
                {
                    currentDomainObject.clazz.metaClass.invokeStaticMethod (currentDomainObject.clazz, "unindex", [] as Object[]);
                }
            }
            modelAction.willBeDeleted = true;
            modelAction.save();
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
                                modelInstance[propName] = getDefaultValue(action.defaultValue, action.propType)
                            }
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
            PropertyAction.findByModelName(modelName).each {propActionWillBeDeleted ->
                propActionWillBeDeleted.willBeDeleted = true;
                propActionWillBeDeleted.save();
            }
        }
        ScriptScheduler.getInstance().initialize(quartzScheduler);
        CmdbScript.findAllByScheduledAndEnabled(true, true).each {
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

    def registerDefaultConverters()
    {
        def dateFormat = ConfigurationHolder.getConfig().toProperties()["rapidcmdb.date.format"];
        RapidConvertUtils.getInstance().register(new DateConverter(dateFormat), Date.class)
        RapidConvertUtils.getInstance().register(new LongConverter(), Long.class)
        RapidConvertUtils.getInstance().register(new DoubleConverter(), Double.class)
    }

    def getDefaultValue(defaultValue, newPropType)
    {
        if (defaultValue) return defaultValue;
        if (String.isAssignableFrom(newPropType))
        {
            return "RCMDB_Default"
        }
        else if (Number.isAssignableFrom(newPropType))
        {
            return -1111;
        }
        else if (Date.isAssignableFrom(newPropType))
        {
            return new Date(0);
        }
        return null;
    }

    def destroy = {
        SnmpDatasource.list().each {
            it.close();
        }
        ScriptManager.getInstance().destroy();
    }

}