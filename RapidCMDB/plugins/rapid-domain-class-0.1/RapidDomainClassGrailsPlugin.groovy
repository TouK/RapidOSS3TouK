import org.codehaus.groovy.grails.commons.GrailsClassUtils as GCU
import org.hibernate.SessionFactory
import com.ifountain.comp.utils.CaseInsensitiveMap
import datasource.BaseDatasource
import org.codehaus.groovy.grails.commons.GrailsDomainClass

class RapidDomainClassGrailsPlugin {
    def watchedResources = ["file:./grails-app/scripts/*.groovy"]
    def version = 0.1
    def dependsOn = [:]
    def loadAfter = ['hibernate']
    def doWithSpring = {
    }

    def doWithApplicationContext = { applicationContext ->

    }

    def doWithWebDescriptor = { xml ->
    }

    def doWithDynamicMethods = { ctx ->
        SessionFactory sessionFactory = ctx.sessionFactory
        for (dc in application.domainClasses) {
            MetaClass mc = dc.metaClass
            registerDynamicMethods(dc, application, ctx)
            for (subClass in dc.subClasses) {
                registerDynamicMethods(subClass, application, ctx)
            }
            configureFederation(log, dc);
            MetaClass emc = GroovySystem.metaClassRegistry.getMetaClass(dc.clazz)
        }
    }

    def onChange = { event ->
    }

    def onApplicationChange = { event ->
    }


    def registerDynamicMethods(dc, application, ctx)
    {
        def mc = dc.metaClass;
        mc.'static'.get = {Map keys->
            def sampleBean = mc.getTheClass().newInstance();
            keys.each{key,value->
                sampleBean.setProperty (key, value);
            }
            return mc.invokeStaticMethod(delegate, "find", sampleBean)
        }

        mc.'static'.add = {Map props->
            def sampleBean = mc.getTheClass().newInstance();
            props.each{key,value->
                sampleBean.setProperty (key, value);
            }
            def returnedBean = sampleBean.save(flush:true);
            if(!returnedBean)
            {
                return sampleBean;
            }
            else
            {
                return returnedBean;
            }
        }
        mc.'static'.create = {Map props->
            def sampleBean = delegate.newInstance();
            props.each{key,value->
                sampleBean.setProperty (key, value);
            }
            return sampleBean;
        }

    }


    def configureFederation(log, dc)
    {
        log.info("Configuring federation");
        MetaClass mc = dc.metaClass
        def propConfigCache = new PropertyConfigurationCache(dc);
        def dsConfigCache = new DatasourceConfigurationCache(dc);
        mc.setProperty = {String name, Object value->
            def prop = null;
            if(((GrailsDomainClass)dc).hasProperty(name))
            {
                prop = ((GrailsDomainClass)dc).getPropertyByName (name);
            }
            if(prop && prop.isOneToOne())
            {
                def reverseProp = prop.getOtherSide();
                if(reverseProp)
                {
                    def reverseName = reverseProp.name;
                    def oldValue = delegate.getProperty(name);
                    if(value)
                    {
                        if(oldValue != value)
                        {
                            if(oldValue != null)
                            {
                                oldValue.setProperty(reverseName, null);
                            }
                            mc.getMetaProperty(name).setProperty(delegate, value);
                            value.setProperty(reverseName, delegate);
                        }
                    }
                    else
                    {
                        mc.getMetaProperty(name).setProperty(delegate, value);
                        if(oldValue)
                        {
                            oldValue.setProperty(reverseName, value);
                        }
                    }
                }
                else
                {
                    mc.getMetaProperty(name).setProperty(delegate, value);
                }
            }
            else
            {
                mc.getMetaProperty(name).setProperty(delegate, value);
            }
        };
        if(dsConfigCache.hasDatasources() && propConfigCache.hasPropertyConfiguration())
        {
            mc.addMetaBeanProperty(new DatasourceProperty("isPropertiesLoaded", Object.class));
//            mc.setProperty = {String name, Object value->
//                def propertyConfig = propConfigCache.getPropertyConfigByName(name);
//                mc.getMetaProperty(name).setProperty(delegate, value);
//            };

            mc.getProperty = {String name->
                def propertyConfig = propConfigCache.getPropertyConfigByName(name);
                def domainObject = delegate;
                def isPropertiesLoaded = mc.getMetaProperty("isPropertiesLoaded").getProperty(domainObject);
                if(!propertyConfig || propertyConfig.datasource == RapidCMDBConstants.RCMDB)
                {
                    def metaProp = mc.getMetaProperty(name);
                    if(metaProp)
                    {
                        return mc.getMetaProperty(name).getProperty(domainObject);
                    }
                    return null;
                }
                else
                {
                    if(!propertyConfig.lazy)
                    {

                        def isPropLoadedMap = mc.getMetaProperty("isPropertiesLoaded").getProperty(domainObject);
                        if(isPropLoadedMap[propConfigCache.getDatasourceName(domainObject, name)] == true)
                        {
                            return mc.getMetaProperty(name).getProperty(domainObject);
                        }
                    }
                    return getFederatedProperty(mc, domainObject, name, propConfigCache, dsConfigCache);
                }
            };

        }
    }

    def constructDatasourceProperties(propertyConfiguration)
    {
        def datasourcesProperties = [:]
        propertyConfiguration.each {key,value->
           def dsName = value.datasource;
           if(!dsName)
           {
                dsName = value.datasourceProperty;
           }
           def props = datasourceProperties[dsName];
           if(!props)
           {
               props = [];
               datasourceProperties[dsName] = props;
           }
           value.name = key;
           props += value;
        }
        return datasourcesProperties;
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
                    e.printStackTrace();
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


class PropertyConfigurationCache
{
    def propertiesByName;
    def propertiesByNameInDs;
    def datasourceProperties;
    def domainMetaClass;
    public PropertyConfigurationCache(domainClass)
    {
        domainMetaClass = domainClass.metaClass;
        propertiesByName = [:];
        propertiesByNameInDs = new CaseInsensitiveMap();
        datasourceProperties = [:];
        constructPropertyConfiguration(domainClass);
        propertiesByName.each{key, value->
            value.name = key;
            def nameInDs = getNameInDs(value);
            propertiesByNameInDs[nameInDs] = value;
            def propertyDs = value.datasource;
            if(!propertyDs)
            {
                propertyDs =  value.datasourceProperty;
            }
            if(!value.lazy)
            {
                def dsProps = datasourceProperties[propertyDs];
                if(!dsProps)
                {
                    dsProps = [:];
                    datasourceProperties[propertyDs] = dsProps;
                }
                dsProps[key] = nameInDs;
            }

        }
    }

    def hasPropertyConfiguration()
    {
        return propertiesByName.size() > 0;
    }

    def getDatasourceName(domainObject, propertyName)
    {
        def propertyConfig = propertiesByName[propertyName];
        if(propertyConfig)
        {
            def datasourceName =  propertyConfig.datasource;
            if(!datasourceName)
            {
                def referencedDatasourceName =  propertyConfig.datasourceProperty;
                if(referencedDatasourceName)
                {
                    def metaProp = domainMetaClass.getMetaProperty(referencedDatasourceName);
                    if(metaProp)
                    {
                        datasourceName = metaProp.getProperty(domainObject);
                    }
                }
            }
            return datasourceName;
        }
        return null;

    }


    def constructPropertyConfiguration(domainClass)
    {
        def realClass = domainClass.metaClass.getTheClass();
        def superClass = realClass.getSuperclass();
        if(superClass && superClass != Object.class)
        {
            constructPropertyConfiguration(superClass);
        }
        if(domainClass.metaClass.hasProperty(domainClass, "propertyConfiguration"))
        {
            def propertyConfig = GCU.getStaticPropertyValue (realClass, "propertyConfiguration");
            if(propertyConfig)
            {
                propertiesByName.putAll(propertyConfig);
            }
        }
    }

    def getPropertyConfigByName(propName)
    {
        return propertiesByName[propName];
    }

    def getPropertyConfigByNameInDs(propName)
    {
        return propertiesByNameInDs[propName];
    }

    def getDatasouceProperties(domainObject, propertyName, isPropsLoaded)
    {
        def propConfig = propertiesByName[propertyName];
        if(isPropsLoaded != true)
        {
            def dsName = propConfig.datasource;
            if(!dsName)
            {
                dsName = propConfig.datasourceProperty;
            }
            if(datasourceProperties[dsName])
            {
                def requestedProps = new HashMap(datasourceProperties[dsName]);
                requestedProps[propertyName] = getNameInDs(propConfig);
                return new ArrayList(requestedProps.values());
            }
            else
            {
                return [getNameInDs(propConfig)];   
            }
        }
        else
        {
            return [getNameInDs(propConfig)];    
        }
    }

    def getNameInDs(propertyConfig)
    {
        def nameInDs = propertyConfig.nameInDs;
        if(!nameInDs)
        {
            nameInDs = propertyConfig.name;
        }
        return nameInDs;
    }


    def getDatasource(domainObject, propertyName)
    {
        def datasourceName = getDatasourceName(domainObject, propertyName);
        if(datasourceName)
        {
           return BaseDatasource.findByName(datasourceName)
        }
        return null;
    }
}

class DatasourceConfigurationCache
{
    def datasources;
    def domainMetaClass;
    public DatasourceConfigurationCache(domainClass)
    {
        datasources = [:];
        constructDatasources(domainClass);
        domainMetaClass = domainClass.metaClass;
    }

    def hasDatasources()
    {
        return datasources.size() > 0;
    }

    def constructDatasources(domainClass)
    {
        def realClass = domainClass.metaClass.getTheClass();
        def superClass = realClass.getSuperclass();
        if(superClass && superClass != Object.class)
        {
            constructDatasources(superClass);
        }
        if(domainClass.metaClass.hasProperty(domainClass, "datasources"))
        {
            def dataSources = GCU.getStaticPropertyValue (realClass, "datasources");
            if(dataSources)
            {
                datasources.putAll(dataSources);
            }
        }
    }

    def getKeys(domainObject, datasourceName)
    {
        def datasourceConfig = datasources[datasourceName];
        if(datasourceConfig)
        {
            def keyConfiguration = datasourceConfig.keys;
            def keys = [:];
            def isNull = false;
            keyConfiguration.each{key,value->
                def nameInDs = key;
                if(value && value.nameInDs)
                {
                    nameInDs = value.nameInDs;
                }
                def keyValue =   domainMetaClass.getMetaProperty(key).getProperty(domainObject);
                if(keyValue)
                {
                    keys[nameInDs] = keyValue;
                }
                else
                {
                    isNull = true;
                    return;
                }
            }
            return !isNull?keys:null;
        }
        return null;
    }

    def createPropertyDatasource(propertyConfig, domainObject)
    {
        def datasourceName =  propertyConfig.datasource;
        if(!datasourceName)
        {
            def referencedDatasourceName =  propertyConfig.datasourceProperty;
            if(referencedDatasourceName)
            {
                def metaProp = domainMetaClass.getMetaProperty(referencedDatasourceName);
                if(metaProp)
                {
                    datasourceName = metaProp.getProperty(domainObject);
                }
            }
        }
        if(datasourceName)
        {
           return BaseDatasource.findByName(datasourceName)
        }
    }
}
