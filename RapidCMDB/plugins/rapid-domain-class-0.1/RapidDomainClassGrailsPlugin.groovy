import org.codehaus.groovy.grails.commons.GrailsClassUtils as GCU
import org.hibernate.SessionFactory
import com.ifountain.comp.utils.CaseInsensitiveMap
import datasource.BaseDatasource
import org.codehaus.groovy.grails.plugins.orm.hibernate.HibernateGrailsPlugin
import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor as Events
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsClassUtils;
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
        def mappedBy = getMappedBy(dc);
        def mc = dc.metaClass;
        def oneToOneRelationProperties = getOneToOneRelationProperties(dc);
        def oneToManyRelationProperties = getOneToManyRelationProperties(dc);
        def allRelations = getRelationProperties(dc);
        try
        {
            dc.metaClass.getTheClass().newInstance().delete();
        }
        catch(t)
        {
            logger.debug("Delete method injection didnot performed by hibernate plugin.", t);
        }
        mc.hybernateDelete = mc.getMetaMethod("delete", (Object[])[Map.class]).closure;
        mc.hybernateSave1 = mc.getMetaMethod("save", (Object[])[Map.class]).closure;
        mc.hybernateSave2 = mc.getMetaMethod("save", (Object[])[Boolean.class]).closure;
        mc.save = {Boolean validate->
            delegate.hybernateSave2(validate);
        }
        mc.save = {->
            delegate.save(flush:false);
        }


        mc.save = {Map args->
            def domainObject = delegate;
            def res = delegate.hybernateSave1(args);
            if(res && delegate.class.name.indexOf(".") < 0)
            {
                oneToOneRelationProperties.each{relationName, relationProp->
                    def relationValue = domainObject[relationName];
                    def sample = relationProp.type.newInstance();
                    def otherSideName = mappedBy[relationName];
                    def foundObjects = relationProp.type.metaClass.invokeStaticMethod(sample,"findAllBy${getUppercasedRelationName(otherSideName)}", domainObject);
                    foundObjects.each{relatedCls->
                        relatedCls[otherSideName] = null;
                        relatedCls.hybernateSave1(args);
                    }
                    if(relationValue)
                    {
                        foundObjects = domainObject.class.metaClass.invokeStaticMethod(domainObject.class.newInstance(),"findAllBy${getUppercasedRelationName(relationName)}", relationValue);
                        foundObjects.each{relatedCls->
                            if(relatedCls.id != domainObject.id)
                            {
                                relatedCls[relationName] = null;
                                relatedCls.hybernateSave1(args);
                            }
                        }

                        relationValue[otherSideName] = domainObject;
                        relationValue.hybernateSave1(args);
                    }
                }
            }
            return res;
        }
        mc.update = {Map props->
            delegate.update(props, true)
        }
        mc.update = {Map props, Boolean flush->
            def domainObject = delegate;
            props.each{key,value->
                domainObject.setProperty(key, value);
            }
            def res = domainObject.save(flush:flush);
            if(!res)
            {
                return domainObject;
            }
            else
            {
                return res;
            }
        }
        mc.addRelation = {Map props->
            return delegate.addRelation(props, true);
        }
        mc.addRelation = {Map props, Boolean flush->
            def domainObject = delegate;
            props.each{key,value->
                def propMetaData = allRelations.get(key);
                if(propMetaData)
                {
                    if(propMetaData.isOneToOne() || propMetaData.isManyToOne())
                    {
                        domainObject.setProperty(key, value);
                    }
                    else
                    {
                        if(value instanceof Collection)
                        {
                            for(childDomain in value)
                            {
                                if(!propMetaData.isOneToMany())
                                {
                                    domainObject."addTo${getUppercasedRelationName(key)}"(childDomain);
                                }
                                else
                                {
                                    childDomain.setProperty(mappedBy[key], domainObject);
                                }
                            }
                        }
                        else
                        {
                            if(!propMetaData.isOneToMany())
                            {
                                domainObject."addTo${getUppercasedRelationName(key)}"(value);
                            }
                            else
                            {
                                value.setProperty(mappedBy[key], domainObject);
                            }

                        }
                    }
                }
            }
            def res = domainObject.save(flush:flush);
            if(!res)
            {
                return domainObject;
            }
            else
            {
                return res;
            }
        }

        mc.removeRelation = {Map props->
            return delegate.removeRelation(props, true);
        }
        mc.removeRelation = {Map props, Boolean flush->
            def domainObject = delegate;
            props.each{key,value->
                GrailsDomainClassProperty propMetaData = allRelations.get(key);
                if(propMetaData)
                {
                    if(propMetaData.isOneToOne() || propMetaData.isManyToOne())
                    {
                        domainObject.setProperty(key, null);
                    }
                    else
                    {
                        if(value instanceof Collection)
                        {
                            for(childDomain in value)
                            {
                                domainObject."removeFrom${getUppercasedRelationName(key)}"(childDomain);
                            }
                        }
                        else
                        {
                            domainObject."removeFrom${getUppercasedRelationName(key)}"(value);
                        }
                    }
                }
            }
            def res = domainObject.save(flush:flush);
            if(!res)
            {
                return domainObject;
            }
            else
            {
                return res;
            }
        }


        mc.remove = {->
            delegate.remove(true);
        }
        mc.remove = {Boolean flush->
            delegate.delete(flush:flush);
        }
        mc.delete = {->
            delegate.delete(flush:false);
        }
        mc.delete = { Map args ->
            def domainObject = delegate;
            if(domainObject.class.name.indexOf(".") < 0)
            {
                oneToOneRelationProperties.each{relationName, relationProp->
                    def otherObject = domainObject[relationName];
                    if(otherObject)
                    {
                        otherObject[mappedBy[relationName]] = null;
                        otherObject.save();
                    }

                }
                oneToManyRelationProperties.each{relationName, relationProp->
                    def otherObjects = domainObject[relationName];
                    if(otherObjects)
                    {
                        def otherSideName = mappedBy[relationName];
                        for(otherObject in otherObjects)
                        {
                            otherObject[otherSideName] = null;
                            otherObject.save();
                        }
                    }

                }
            }
            domainObject.hybernateDelete(args);
        }

        mc.'static'.add = {Map props->
            return delegate.add(props, true);
        }
        mc.'static'.add = {Map props, Boolean flush->
            def sampleBean = mc.getTheClass().newInstance();
            props.each{key,value->
                sampleBean.setProperty (key, value);
            }
            def returnedBean = sampleBean.save(flush:flush);
            if(!returnedBean)
            {
                return sampleBean;
            }
            else
            {
                return returnedBean;
            }
        }
    }

    def getMappedBy(dc)
    {
        def mappedBy = [:];
        def tempObj = dc.metaClass.getTheClass();
        while(tempObj && tempObj != java.lang.Object.class)
        {
            def tmpMappedBy = GrailsClassUtils.getStaticPropertyValue (tempObj, "mappedBy");
            if(tmpMappedBy)
            {
                mappedBy.putAll(tmpMappedBy);
            }
            tempObj =  tempObj.getSuperclass();
        }
        return mappedBy;
    }

    def getUppercasedRelationName(String relName)
    {
        if(relName.length() == 1)
        {
            return relName.toUpperCase();
        }
        else
        {
            return relName.substring(0,1).toUpperCase()+relName.substring(1);
        }
    }

    def addUtilityMetods(dc, application, ctx)
    {
        def mc = dc.metaClass;
        mc.asMap = {->
            def domainObject = delegate;
            def excludedProps = ['version',
                                         'id',
                                           Events.ONLOAD_EVENT,
                                           Events.BEFORE_DELETE_EVENT,
                                           Events.BEFORE_INSERT_EVENT,
                                           Events.BEFORE_UPDATE_EVENT]
            def props = dc.properties.findAll { !excludedProps.contains(it.name) }
            def propertyMap = [:];
            for(prop in props){
                if(!prop.oneToMany && !prop.manyToMany && !prop.oneToOne && !prop.manyToOne){
                    propertyMap.put(prop.name, domainObject.getProperty(prop.name))
                }
            }

            return propertyMap;
        };
        mc.asMap = {List properties->
            def domainObject = delegate;
            def propertyMap = [:];
            for(prop in properties){
               try{
                    propertyMap.put(prop, domainObject.getProperty(prop));
               }
               catch(e){
                    logger.debug("An exception occurred while converting object to map while getting value of property ${prop}.", e);
               }
            }
            return propertyMap;
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
        mc.'static'.get = {Map keys->
            def sampleBean = mc.getTheClass().newInstance();
            keys.each{key,value->
                sampleBean.setProperty (key, value);
            }
            return mc.invokeStaticMethod(delegate, "find", sampleBean)
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

    def getRelationProperties(dc)
    {
        def relationProperties = [:];
        dc.getProperties().each
        {
            if(it.isAssociation())
            {
                relationProperties[it.name] = it;
            }
        }
        return relationProperties;
    }

    def getOneToOneRelationProperties(dc)
    {
        def oneToOneRelationProperties = [:];
        dc.getProperties().each
        {
            if(it.isOneToOne())
            {
                oneToOneRelationProperties[it.name] = it;
            }
        }
        return oneToOneRelationProperties;
    }

    def getOneToManyRelationProperties(dc)
    {
        def oneToManyRelationProperties = [:];
        dc.getProperties().each
        {
            if(it.isOneToMany())
            {
                oneToManyRelationProperties[it.name] = it;
            }
        }
        return oneToManyRelationProperties;
    }

    def addPropertyGetAndSetMethods(dc)
    {                         
        MetaClass mc = dc.metaClass
        def propConfigCache = new PropertyConfigurationCache(dc);
        def dsConfigCache = new DatasourceConfigurationCache(dc);
//        mc.setProperty = {String name, Object value->
//            mc.getMetaProperty(name).setProperty(delegate, value);
//            if(delegate.id)
//            {
//                delagete.save(flush:true);
//            }
//        };
        if(dsConfigCache.hasDatasources() && propConfigCache.hasPropertyConfiguration())
        {
            mc.addMetaBeanProperty(new DatasourceProperty("isPropertiesLoaded", Object.class));
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
