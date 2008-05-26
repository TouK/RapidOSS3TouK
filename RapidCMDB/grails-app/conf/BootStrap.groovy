import datasource.RCMDBDatasource
import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.ifountain.rcmdb.scripting.ScriptManager
import org.jsecurity.crypto.hash.Sha1Hash
import auth.Role
import auth.RsUser
import auth.UserRoleRel
import org.apache.commons.beanutils.ConvertUtils
import com.ifountain.rcmdb.domain.converter.DateConverter
import com.ifountain.rcmdb.domain.converter.LongConverter
import datasource.SnmpDatasource
import com.ifountain.rcmdb.domain.converter.RapidConvertUtils
import model.PropertyShouldBeCleared
import model.ChangedModel
import org.codehaus.groovy.grails.commons.ApplicationHolder
import model.PropertyAction
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass

class BootStrap {

    def init = {servletContext ->
        RapidConvertUtils.getInstance().register (new DateConverter("yyyy-dd-MM HH:mm:ss"), Date.class)
        RapidConvertUtils.getInstance().register (new LongConverter(), Long.class)
    	def adminRole = Role.findByName("Administrator");
    	if(!adminRole){
	    	adminRole = new Role(name: "Administrator");
	    	adminRole.save();	
	    }
	    def userRole = Role.findByName("User");
    	if(!userRole){
	    	userRole = new Role(name: "User");
	    	userRole.save();	
	    }
	    def adminUser = RsUser.findByUsername("rsadmin");
	    if(!adminUser){
			adminUser = new RsUser(username: "rsadmin", passwordHash: new Sha1Hash("changeme").toHex());
			adminUser.save();    
		}
        new UserRoleRel(rsUser: adminUser, role: adminRole).save()
        def rcmdbDatasource = RCMDBDatasource.findByName(RapidCMDBConstants.RCMDB);
        if (rcmdbDatasource == null) {
            new RCMDBDatasource(name: RapidCMDBConstants.RCMDB).save();
        }
        ScriptManager.getInstance().initialize();
        def changedModelProperties = [:]

        
        PropertyAction.list().each{PropertyAction propAction->
            DefaultGrailsDomainClass currentDomainObject = ApplicationHolder.application.getDomainClass(propAction.modelName);
            def modelProps = changedModelProperties[propAction.modelName];
            if(modelProps == null)
            {
                modelProps = [:]
                changedModelProperties[propAction.modelName] = modelProps;
            }
            propAction.defaultValue = currentDomainObject.clazz.newInstance()[propAction.propName];
            propAction.propType = currentDomainObject.getPropertyByName(propAction.propName).type;
            modelProps[propAction.propName] = propAction;
        }
        println "MODEL PROPS:${changedModelProperties}"
        int batch = 1000;

        changedModelProperties.each{String modelName, Map modelProps->
            DefaultGrailsDomainClass currentDomainObject = ApplicationHolder.application.getDomainClass(modelName);
            if(currentDomainObject)
            {
                println "REINDEXING ${modelName}"
                Class currentModelClass = currentDomainObject.clazz;
                int index = 0;
                while(true)
                {
                    def res = currentModelClass.metaClass.invokeStaticMethod (currentModelClass, "search", ["id:[0 TO *]",[max:batch, offset:index]] as Object[]);
                    println "RES ${res}"
                    boolean isDelete = false;
                    res.results.each{modelInstance->
                        if(!isDelete)
                        {
                            modelProps.each{propName, PropertyAction action->
                                def propVal = modelInstance[propName];

                                if(action.action ==  PropertyAction.CLEAR_RELATION)
                                {
                                    println "CLEARING REL ${modelName}"
                                    if(propVal instanceof Collection)
                                    {
                                        propVal.clear();
                                    }
                                    else
                                    {
                                        modelInstance[propName] = null;
                                    }
                                }
                                else if(action.action ==  PropertyAction.SET_DEFAULT_VALUE)
                                {
                                    println "SETTING DEFAULT VALUE ${action.defaultValue} ${action.propType}"
                                    modelInstance[propName] = getDefaultValue(action.defaultValue,action.propType)
                                }
                                else if(action.action ==  PropertyAction.DELETE_ALL_INSTANCES)
                                {
                                    isDelete = true;
                                    return;
                                }
                            }
                        }
                        if(isDelete)
                        {
                            println "UNINDEXED"
                            modelInstance.unindex();
                        }
                        else
                        {
                            println "REUNINDEXED"
                            modelInstance.reindex();
                        }
                    }
                    index += batch;
                    if(res.total < index)
                    {
                        break;
                    }
                }
                println "REINDEXING ${modelName} FINISHED"
            }
            //PropertyAction.findByModelName(modelName)*.delete();
        }          
    }

    def getDefaultValue(defaultValue, newPropType)
    {
        if(defaultValue) return defaultValue;
        if(String.isAssignableFrom(newPropType))
        {
            return "RCMDB_Default"
        }
        else if(Number.isAssignableFrom(newPropType))
        {
            return -1111;
        }
        else if(Date.isAssignableFrom(newPropType))
        {
            return new Date(0);
        }
        return null;
    }

    def destroy = {
        SnmpDatasource.list().each{
            it.close();
        }
        ScriptManager.getInstance().destroy();
    }

    
}