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
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.codehaus.groovy.grails.exceptions.InvalidPropertyException;
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
        def relations = getRelations(dc);
        def mc = dc.metaClass;
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
            def domainObject = delegate;
            def relationMap = [:]
            props.each{key,value->
                if(!relations.containsKey(key))
                {

                    domainObject.setProperty (key, value);
                }
                else
                {
                    def relationsToBeRemoved = [:];
                    relationsToBeRemoved[key] = domainObject[key]; 
                    domainObject.removeRelation(relationsToBeRemoved, false);
                    if(value)
                    {
                        relationMap[key] = value;
                    }
                }
            }
            domainObject.addRelation(relationMap, false);
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
                Relation relation = relations.get(key);
                if(relation)
                {
                    if(relation.isOneToOne())
                    {
                        checkInstanceOf(relation, value);
                        setOneToOne(domainObject, relation, value);

                    }
                    else if(relation.isManyToOne())
                    {
                        if(value)
                        {
                            def relationToBeAdded = [:]
                            relationToBeAdded[relation.otherSideName] = domainObject;
                            value.addRelation(relationToBeAdded, flush);
                        }
                    }
                    else if(relation.isOneToMany())
                    {
                        if(value instanceof Collection)
                        {
                            for(childDomain in value)
                            {
                                checkInstanceOf(relation, childDomain);
                                childDomain.setProperty(relation.otherSideName, domainObject);
                                childDomain.save();
                                domainObject."addTo${relation.upperCasedName}"(childDomain)
                            }
                        }
                        else
                        {
                            checkInstanceOf(relation, value);
                            value.setProperty(relation.otherSideName, domainObject);
                            value.save();
                            domainObject."addTo${relation.upperCasedName}"(value)
                        }
                    }
                    else
                    {
                        if(value instanceof Collection)
                        {
                            for(childDomain in value)
                            {
                                checkInstanceOf(relation, childDomain);
                                domainObject."addTo${relation.upperCasedName}"(childDomain);
                                childDomain."addTo${relation.upperCasedOtherSideName}"(domainObject);
                            }
                        }
                        else
                        {
                            checkInstanceOf(relation, value);
                            domainObject."addTo${relation.upperCasedName}"(value);
                            value."addTo${relation.upperCasedOtherSideName}"(domainObject);
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

                if(value)
                {
                    Relation relation = relations.get(key);
                    
                    if(relation)
                    {
                        if(relation.isOneToOne())
                        {
                            checkInstanceOf(relation, value);
                            setOneToOne(domainObject, relation, null);
                        }
                        else if(relation.isManyToOne())
                        {
                            def relationToBeRemoved = [:]
                            relationToBeRemoved[relation.otherSideName] = domainObject;
                            value.removeRelation(relationToBeRemoved, flush);
                        }
                        else if(relation.isOneToMany())
                        {
                            if(value instanceof Collection)
                            {
                                def childDomains = new ArrayList(value);

                                for(childDomain in childDomains)
                                {
                                    checkInstanceOf(relation, childDomain);
                                    childDomain.setProperty(relation.otherSideName, null);
                                    childDomain.save();
                                    domainObject."removeFrom${relation.upperCasedName}"(childDomain);
                                }
                            }
                            else
                            {
                                checkInstanceOf(relation, value);
                                value.setProperty(relation.otherSideName, null);
                                value.save();
                                domainObject."removeFrom${relation.upperCasedName}"(value);
                            }
                        }
                        else
                        {
                            if(value instanceof Collection)
                            {
                                def childDomains = new ArrayList(value);
                                for(childDomain in childDomains)
                                {
                                    checkInstanceOf(relation, childDomain);
                                    domainObject."removeFrom${relation.upperCasedName}"(childDomain);
                                    childDomain."removeFrom${relation.upperCasedOtherSideName}"(domainObject);
                                }
                            }
                            else
                            {
                                checkInstanceOf(relation, value);
                                domainObject."removeFrom${relation.upperCasedName}"(value);
                                value."removeFrom${relation.upperCasedOtherSideName}"(domainObject);
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


        mc.remove = {->
            delegate.remove(true);
        }
        mc.remove = {Boolean flush->
            def domainObject = delegate;
            def relationsToBeRemoved = [:];
            relations.each{relationName, Relation relation->
                relationsToBeRemoved[relationName] = domainObject[relationName];
            }
            domainObject.removeRelation(relationsToBeRemoved, false);
            domainObject.delete("flush":flush);
        }

        mc.'static'.add = {Map props->
            return delegate.add(props, true);
        }
        mc.'static'.add = {Map props, Boolean flush->
            def sampleBean = mc.getTheClass().newInstance();
            def relationMap = [:]
            props.each{key,value->
                if(!relations.containsKey(key))
                {
                    sampleBean.setProperty (key, value);
                }
                else
                {
                    relationMap[key] = value;
                }
            }
            def returnedBean = sampleBean.save(flush:flush);
            if(returnedBean && !relationMap.isEmpty())
            {
                returnedBean.addRelation(relationMap, false);
                returnedBean = returnedBean.save(flush:flush);
            }
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

    def setOneToOne(domainObject, Relation relation, relationValue)
    {
        def previousValue = domainObject[relation.name];
        if(previousValue && relationValue && relationValue.id == previousValue.id)
        {
            return;
        }

        if(previousValue)
        {
            previousValue[relation.otherSideName] = null;
            previousValue.save();
        }
        if(relationValue)
        {
            if(relationValue[relation.otherSideName])
            {
                relationValue[relation.otherSideName][relation.name] = null;
                relationValue[relation.otherSideName].save();
            }
            relationValue[relation.otherSideName] = domainObject;
            relationValue.save();
        }
        domainObject[relation.name] = relationValue;
    }

    def getStaticVariable(dc, variableName)
    {
        def variableMap = [:];
        def tempObj = dc.metaClass.getTheClass();
        while(tempObj && tempObj != java.lang.Object.class)
        {
            def tmpVariableMap = GrailsClassUtils.getStaticPropertyValue (tempObj, variableName);
            if(tmpVariableMap)
            {
                variableMap.putAll(tmpVariableMap);
            }
            tempObj =  tempObj.getSuperclass();
        }
        return variableMap;
    }

    def checkInstanceOf(relation, value)
    {
        if(value && !relation.otherSideClass.isInstance(value))
        {
            throw new InvalidPropertyException ("Invalid relation value for ${relation.name} expected ${relation.otherSideClass.getName()} got ${value.class.name}");
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

    def getRelations(GrailsDomainClass dc)
    {
        def allRelations = [:];
        def hasMany = getStaticVariable(dc, "hasMany");
        def mappedBy = getStaticVariable(dc, "mappedBy");
        mappedBy.each{relationName, otherSideName->
            def otherSideClass = hasMany[relationName];
            if(!otherSideClass)
            {
                otherSideClass = dc.getPropertyByName (relationName).getType();
            }
            allRelations[relationName] = new Relation(relationName, otherSideName, dc.getClazz(), otherSideClass);
        }
        return allRelations;
    }

    def addPropertyGetAndSetMethods(dc)
    {
        def relations = getRelations(dc);
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

            Relation relation = relations[name]
            if(relation && relation.isOneToMany() && currentValue.isEmpty())
            {
                def foundRelatedInstances = relation.otherSideClass.metaClass.invokeStaticMethod(relation.otherSideClass, "findAllBy${relation.upperCasedOtherSideName}", [domainObject] as Object[]);
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

class Relation
{
    public static int ONE_TO_ONE = 0;
    public static int ONE_TO_MANY = 1;
    public static int MANY_TO_MANY = 2;
    public static int MANY_TO_ONE = 3;
    String name;
    String otherSideName;
    String upperCasedName;
    String upperCasedOtherSideName;
    Class otherSideClass;

    int type;
    public Relation(String name, String otherSideName, Class cls, Class otherClass)
    {
        this.name = name;
        this.otherSideName = otherSideName;
        this.otherSideClass = otherClass;
        this.upperCasedName = getUppercasedRelationName(name);
        this.upperCasedOtherSideName = getUppercasedRelationName(otherSideName);
        def relationPropClass = cls.metaClass.getMetaProperty(name).getType();
        def otherSidePropClass = otherClass.metaClass.getMetaProperty(otherSideName).getType();
        def isSelfCollection = Collection.isAssignableFrom(relationPropClass);
        def isOtherCollection = Collection.isAssignableFrom(otherSidePropClass);
        if(isSelfCollection && isOtherCollection)
        {
            this.type = MANY_TO_MANY;
        }
        else if(isSelfCollection && !isOtherCollection)
        {
            this.type = ONE_TO_MANY;
        }
        else if(!isSelfCollection && isOtherCollection)
        {
            this.type = MANY_TO_ONE;
        }
        else
        {
            this.type = ONE_TO_ONE;
        }
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

    def isOneToOne()
    {
        return type == ONE_TO_ONE;
    }

    def isOneToMany()
    {
        return type == ONE_TO_MANY;
    }

    def isManyToOne()
    {
        return type == MANY_TO_ONE;
    }

    def isManyToMany()
    {
        return type == MANY_TO_MANY;
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
