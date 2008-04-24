import org.codehaus.groovy.grails.commons.GrailsClassUtils as GCU
import org.hibernate.SessionFactory
import com.ifountain.comp.utils.CaseInsensitiveMap
import datasource.BaseDatasource
import org.codehaus.groovy.grails.plugins.orm.hibernate.HibernateGrailsPlugin
import org.apache.log4j.Logger
import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.ifountain.rcmdb.domain.method.AddMethod
import com.ifountain.rcmdb.domain.method.RemoveMethod
import com.ifountain.rcmdb.domain.method.UpdateMethod
import com.ifountain.rcmdb.domain.method.AddRelationMethod
import com.ifountain.rcmdb.domain.method.RemoveRelationMethod
import com.ifountain.rcmdb.domain.method.AsMapMethod
import com.ifountain.rcmdb.domain.util.DomainClassUtils
import com.ifountain.rcmdb.domain.util.Relation
import com.ifountain.rcmdb.domain.method.GetMethod
import com.ifountain.rcmdb.domain.util.PropertyConfigurationCache
import com.ifountain.rcmdb.domain.util.DatasourceConfigurationCache;
class RapidDomainClassGrailsPlugin {
    def logger = Logger.getLogger("grails.app.plugins.RapidDomainClass")
    def watchedResources = ["file:./grails-app/scripts/*.groovy"]
    def version = 0.1
    def dependsOn = [:]
    def loadAfter = ['hibernate']
    def doWithSpring = {
    }

    def doWithApplicationContext = { applicationContext ->
        HibernateGrailsPlugin
    }

    def doWithWebDescriptor = { xml ->
        def contextParam = xml."context-param"
        contextParam[contextParam.size()-1]+{
            'filter' {
                'filter-name'('hibernateFilter')
                'filter-class'('org.codehaus.groovy.grails.orm.hibernate.support.GrailsOpenSessionInViewFilter')
            }
        }

        def filter = xml."filter"
        filter[filter.size()-1]+{
            'filter-mapping'{
                'filter-name'('hibernateFilter')
                'url-pattern'("/*")
            }
        }
    }

    def doWithDynamicMethods = { ctx ->
        SessionFactory sessionFactory = ctx.sessionFactory
        def domainClassMap = [:];
        for (dc in application.domainClasses) {
            MetaClass mc = dc.metaClass
            domainClassMap[mc.getTheClass().name] = mc.getTheClass().name
        }
        def domainClassesToBeCreated = [];
        for (dc in application.domainClasses) {
            MetaClass mc = dc.metaClass
            if(!domainClassMap.containsKey(mc.getTheClass().getSuperclass().getName()))
            {
                domainClassesToBeCreated += dc;
            }
        }
        for (dc in domainClassesToBeCreated) {
            MetaClass mc = dc.metaClass
            registerDynamicMethods(dc, application, ctx);
        }

    }

    def onChange = { event ->
    }

    def onApplicationChange = { event ->
    }

    def addBasicPersistenceMethods(dc, application, ctx)
    {
        def mc = dc.metaClass;
        def addMethod = new AddMethod(mc, dc);
        def removeMethod = new RemoveMethod(mc, dc);
        def updateMethod = new UpdateMethod(mc, dc);
        def addRelationMethod = new AddRelationMethod(mc, dc);
        def removeRelationMethod = new RemoveRelationMethod(mc, dc);
        try
        {
            dc.metaClass.getTheClass().newInstance().delete();
        }
        catch(t)
        {
            logger.debug("Delete method injection didnot performed by hibernate plugin.", t);
        }
        mc.update = {Map props->
            delegate.update(props, true)
        }
        mc.update = {Map props, Boolean flush->
            return updateMethod.invoke(delegate,  [props, flush] as Object[])    
        }
        mc.addRelation = {Map props->
            return delegate.addRelation(props, true);
        }
        mc.addRelation = {Map props, Boolean flush->
          return addRelationMethod.invoke(delegate,  [props, flush] as Object[])
        }
        mc.removeRelation = {Map props->
            return delegate.removeRelation(props, true);
        }
        mc.removeRelation = {Map props, Boolean flush->
            return removeRelationMethod.invoke(delegate,  [props, flush] as Object[])  
        }
        mc.remove = {->
            delegate.remove(true);
        }
        mc.remove = {Boolean flush->
            return removeMethod.invoke(delegate, [flush] as Object[]);
        }
        mc.'static'.add = {Map props->
            return delegate.add(props, true);
        }
        mc.'static'.add = {Map props, Boolean flush->
            return addMethod.invoke(mc.theClass, [props, flush] as Object[]);
        }
    }

    def addUtilityMetods(dc, application, ctx)
    {
        def mc = dc.metaClass;
        def asMapMethod = new AsMapMethod(mc, dc);
        mc.asMap = {->
            return asMapMethod.invoke(delegate, null);
        };
        mc.asMap = {List properties->
            return asMapMethod.invoke(delegate, properties as Object[]);
        };
        mc.'static'.create = {Map props->
            def sampleBean = delegate.newInstance();
            props.each{key,value->
                sampleBean.setProperty (key, value);
            }
            return sampleBean;
        }
    }
    def addQueryMethods(dc, application, ctx)
    {
        def mc = dc.metaClass;
        def getMethod = new GetMethod(mc, dc);
        mc.'static'.get = {Map searchParams->
            return getMethod.invoke(mc.theClass, [searchParams] as Object[])
        }
    }
    def registerDynamicMethods(dc, application, ctx)
    {
        addBasicPersistenceMethods(dc, application, ctx)
        addQueryMethods (dc, application, ctx)
        addUtilityMetods (dc, application, ctx)
        addPropertyGetAndSetMethods(dc);
        for(subClass in dc.subClasses)
        {
            if(subClass.metaClass.getTheClass().getSuperclass().name == dc.metaClass.getTheClass().name)
            {
                registerDynamicMethods(subClass, application, ctx);
            }
        }
    }

    def addPropertyGetAndSetMethods(dc)
    {
        def relations = DomainClassUtils.getRelations(dc);
        MetaClass mc = dc.metaClass
        def propConfigCache = new PropertyConfigurationCache(dc);
        def dsConfigCache = new DatasourceConfigurationCache(dc);
        if(dsConfigCache.hasDatasources() && propConfigCache.hasPropertyConfiguration())
        {
            mc.addMetaBeanProperty(new DatasourceProperty("isPropertiesLoaded", Object.class));
        }
        mc.getProperty = {String name->
            def domainObject = delegate;
            def currentValue;
            def metaProp = mc.getMetaProperty(name);
            if(metaProp)
            {
                currentValue = mc.getMetaProperty(name).getProperty(domainObject);
            }
            else
            {
                throw new MissingPropertyException(name, mc.getTheClass());
            }
            Relation relation = relations.get(name);
            if(relation && relation.isOneToMany() && !(currentValue instanceof RelationSetList))
            {
                def criteria = relation.otherSideClass.metaClass.invokeStaticMethod(relation.otherSideClass, "createCriteria", [] as Object[]);
                def foundRelatedInstances = new RelationSetList(criteria.listDistinct{
                    "${relation.otherSideName}"{eq("id",domainObject.id)}
                });
                domainObject[relation.name] = foundRelatedInstances;
                return foundRelatedInstances; 
            }
            else if(dsConfigCache.hasDatasources() && propConfigCache.hasPropertyConfiguration())
            {
                def propertyConfig = propConfigCache.getPropertyConfigByName(name);
                def isPropertiesLoaded = mc.getMetaProperty("isPropertiesLoaded").getProperty(domainObject);
                if(!propertyConfig || propertyConfig.datasource == RapidCMDBConstants.RCMDB)
                {
                    return currentValue;
                }
                else
                {
                    if(!propertyConfig.lazy)
                    {

                        def isPropLoadedMap = mc.getMetaProperty("isPropertiesLoaded").getProperty(domainObject);
                        if(isPropLoadedMap[propConfigCache.getDatasourceName(domainObject, name)] == true)
                        {
                            return currentValue
                        }
                    }
                    return getFederatedProperty(mc, domainObject, name, propConfigCache, dsConfigCache);
                }
            }
            return currentValue;
        };
    }


    def getFederatedProperty(domainObjectMetaClass, currentDomainObject, propertyName, propCache, dsCache)
    {
        def propertyDatasource = propCache.getDatasource(currentDomainObject, propertyName);
        if(propertyDatasource)
        {
            def keys = dsCache.getKeys(currentDomainObject, propertyDatasource.name);
            if(keys != null && keys.size() > 0)
            {
                def isPropsLoaded = currentDomainObject.isPropertiesLoaded[propertyDatasource.name];
                def requestedProperties = propCache.getDatasouceProperties(currentDomainObject, propertyName, isPropsLoaded);
                try
                {
                    def returnedProps = propertyDatasource.getProperties (keys, requestedProperties);
                    if(isPropsLoaded != true)
                    {
                        
                        returnedProps.each {key, value->
                            def requestedPropConfig = propCache.getPropertyConfigByNameInDs(key);
                            if(requestedPropConfig)
                            {
                                currentDomainObject.setProperty(requestedPropConfig.name, value);
                            }
                        }
                        currentDomainObject.isPropertiesLoaded[propertyDatasource.name] = true;
                    }
                    return returnedProps[propCache.getNameInDs(propCache.getPropertyConfigByName(propertyName))];
                    
                }
                catch(Throwable e)
                {
                    logger.warn("An exception occurred while getting federated properties ${requestedProperties} from ${propertyDatasource.name}", e);
                }
            }
        }

        return "";
    }
}



class DatasourceProperty extends MetaBeanProperty
{

    public DatasourceProperty(String s, Class aClass) {
        super(s, aClass, null, null); //To change body of overridden methods use File | Settings | File Templates.
    }
    
   WeakHashMap map = new WeakHashMap()
    public Object getProperty(Object o) {
        def object = map[o];
        if(!object)
        {
            object = [:];
            map[o] = object;
        }
        return object;
    }

    public void setProperty(Object o, Object o1) {


    }

}



class RelationSetList extends ArrayList
{

    public RelationSetList(int initialCapacity) {
        super(initialCapacity);
    }

    public RelationSetList() {
        super();
    }

    public RelationSetList(Collection c) {
        super(c);
    }

}
