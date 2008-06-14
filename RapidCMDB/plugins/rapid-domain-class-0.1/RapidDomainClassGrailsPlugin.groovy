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
import com.ifountain.rcmdb.domain.util.DatasourceConfigurationCache
import com.ifountain.rcmdb.domain.AbstractDomainOperation
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import com.ifountain.rcmdb.domain.IdGenerator
import com.ifountain.rcmdb.domain.IdGeneratorStrategyImpl
import com.ifountain.rcmdb.domain.method.CompassMethodInvoker
import com.ifountain.rcmdb.domain.generation.ModelUtils
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import com.ifountain.rcmdb.domain.constraints.KeyConstraint
import org.apache.commons.lang.StringUtils

class RapidDomainClassGrailsPlugin {
    private static final Map EXCLUDED_PROPERTIES = ["id":"id", "version":"version", "errors":"errors"]
    def logger = Logger.getLogger("grails.app.plugins.RapidDomainClass")
    def version = 0.1
    def dependsOn = [searchable:"0.5-SNAPSHOT"]
    def loadAfter = ['searchable']
    def domainClassMap;
    def doWithSpring = {
        ConstrainedProperty.registerNewConstraint(KeyConstraint.KEY_CONSTRAINT, KeyConstraint);
    }

    def doWithApplicationContext = { applicationContext ->
    }

    def doWithWebDescriptor = { xml ->
    }

    def doWithDynamicMethods = { ctx ->
        IdGenerator.initialize (new IdGeneratorStrategyImpl());
        domainClassMap = [:];
        for (dc in application.domainClasses) {
            MetaClass mc = dc.metaClass
            domainClassMap[mc.getTheClass().name] = dc
        }
        def domainClassesToBeCreated = [];
        for (dc in application.domainClasses) {
            MetaClass mc = dc.metaClass
            if(isSearchable(mc))
            {
                if(!domainClassMap.containsKey(mc.getTheClass().getSuperclass().getName()))
                {
                    domainClassesToBeCreated += dc;
                }
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


    def addOperationsSupport(GrailsDomainClass dc, application, ctx)
    {
        def mc = dc.metaClass;
        DomainOperationProperty operationProperty = new DomainOperationProperty()
        mc.addMetaBeanProperty (operationProperty)
        def operationClassName = dc.name + ModelUtils.OPERATIONS_CLASS_EXTENSION;
        mc.methodMissing =  {String name, args ->
            def oprInstance = operationProperty.getProperty(delegate);
            if(oprInstance)
            {
                try {
                    return oprInstance.invokeMethod(name, args)
                } catch (MissingMethodException e) {
                }
            }
            throw new MissingMethodException (name,  delegate.class, args);
        }
        mc.'static'.reloadOperations = {
            mc.invokeStaticMethod (dc.clazz, "reloadOperations", true);
        }
        mc.'static'.reloadOperations = {reloadSubclasses->
            def opClassLoader = new GroovyClassLoader(dc.clazz.classLoader);
            opClassLoader.addClasspath ("${System.getProperty("base.dir")}/operations");
            try
            {
                def oprClass = opClassLoader.loadClass (operationClassName);
                if(reloadSubclasses != false )
                {
                    def lastObjectNeedsToBeReloaded = null;
                    try
                    {
                        dc.getSubClasses().each{subDomainObject->
                            lastObjectNeedsToBeReloaded = subDomainObject.name;
                            subDomainObject.metaClass.invokeStaticMethod (subDomainObject.clazz, "reloadOperations", false);
                        }
                    }
                    catch(t)
                    {
                        logger.info("Operations of child model ${lastObjectNeedsToBeReloaded} could not reloaded. Please fix the problem an retry reloading.", t);
                        throw new RuntimeException("Operations of child model ${lastObjectNeedsToBeReloaded} could not reloaded. Please fix the problem an retry reloading. Reason:${t.toString()}", t)
                    }
                }
                operationProperty.operationClass = oprClass;
            }
            catch(java.lang.ClassNotFoundException classNotFound)
            {
                logger.info("${operationClassName} file does not exist.");
                throw new FileNotFoundException("${System.getProperty("base.dir")}/operations/${operationClassName}.groovy", null);
            }
            catch(t)
            {
                logger.info("Error occurred while reloading operation ${operationClassName}", t);
                throw t;
            }
        }

        try
        {
            mc.invokeStaticMethod (dc.clazz, "reloadOperations", false);
        }
        catch(t)
        {
        }
    }

    private boolean isSearchable(mc)
    {
        def metaProp = mc.getMetaProperty("searchable");
        return metaProp != null;
    }

    def addBasicPersistenceMethods(dc, application, ctx)
    {
        def mc = dc.metaClass;
        def relations = DomainClassUtils.getRelations(dc, domainClassMap);
        dc.refreshConstraints();
        def keys = DomainClassUtils.getKeys(dc);
        def addMethod = new AddMethod(mc, dc.validator, relations, keys);
        def removeMethod = new RemoveMethod(mc, relations);
        def updateMethod = new UpdateMethod(mc, dc.validator, relations);
        def addRelationMethod = new AddRelationMethod(mc, relations);
        def removeRelationMethod = new RemoveRelationMethod(mc, relations);
        mc.update = {Map props->
            return updateMethod.invoke(delegate,  [props] as Object[])
        }
        mc.addRelation = {Map props->
          return addRelationMethod.invoke(delegate,  [props] as Object[])
        }
        mc.removeRelation = {Map props->
            return removeRelationMethod.invoke(delegate,  [props] as Object[])
        }
        mc.remove = {->
            return removeMethod.invoke(delegate, null);
        }
        mc.'static'.add = {Map props->
            return addMethod.invoke(mc.theClass, [props] as Object[]);
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
        def keys = DomainClassUtils.getKeys(dc);
        def getMethod = new GetMethod(mc, keys);
        mc.'static'.get = {Map searchParams->
            return getMethod.invoke(mc.theClass, [searchParams] as Object[])
        }
        mc.'static'.get = {Long searchParams->
            return getMethod.invoke(mc.theClass, [searchParams] as Object[])
        }


        mc.'static'.list = {->
            return CompassMethodInvoker.searchEvery(mc, "id:[0 TO *]");
        }

        mc.'static'.list = {Map options->
            return CompassMethodInvoker.search(mc, "id:[0 TO *]", options).results;
        }
    }
    def registerDynamicMethods(dc, application, ctx)
    {
        def mc = dc.clazz.metaClass;
        try
        {
            dc.metaClass.getTheClass().newInstance().delete();
        }
        catch(t)
        {
            logger.debug("Delete method injection didnot performed by hibernate plugin.", t);
        }
        addOperationsSupport(dc, application, ctx)
        addBasicPersistenceMethods(dc, application, ctx)
        addQueryMethods (dc, application, ctx)
        addUtilityMetods (dc, application, ctx)
        addPropertyGetAndSetMethods(dc);
        mc.'static'.methodMissing = {String methodName, args ->
            if(methodName.startsWith("findBy"))
            {
                def searchKeyMap = [:]
                def propName = StringUtils.substringAfter(methodName, "findBy");
                propName = propName.substring(0,1).toLowerCase() + propName.substring(1,propName.length());
                searchKeyMap[propName] = args[0];
                return CompassMethodInvoker.search(mc, searchKeyMap).results[0];
            }
            else if(methodName.startsWith("findAllBy"))
            {
                def searchKeyMap = [:]
                def propName = StringUtils.substringAfter(methodName, "findAllBy");
                propName = propName.substring(0,1).toLowerCase() + propName.substring(1,propName.length());
                searchKeyMap[propName] = args[0];
                return CompassMethodInvoker.search(mc, searchKeyMap).results;
            }
            return null;
        }
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
        def relations = DomainClassUtils.getRelations(dc, domainClassMap);
        MetaClass mc = dc.metaClass
        def propConfigCache = new PropertyConfigurationCache(dc);
        def dsConfigCache = new DatasourceConfigurationCache(dc);
        if(dsConfigCache.hasDatasources() && propConfigCache.hasPropertyConfiguration())
        {
            mc.addMetaBeanProperty(new DatasourceProperty("isPropertiesLoaded", Object.class));
        }
        mc.setProperty = {String name, Object value->
            delegate.setProperty(name, value, true);    
        }

        mc.setProperty = {String name, Object value, boolean flush->
            def operation = delegate.__InternalGetProperty__(DomainOperationProperty.PROP_NAME);
            if(!operation)
            {
                delegate.__InternalSetProperty__(name, value);
            }
            else
            {
                try
                {
                    operation.invokeMethod (GrailsClassUtils.getSetterName(name), [value] as Object[]);
                }
                catch(groovy.lang.MissingMethodException m)
                {
                    operation.setProperty (name, value);
                }
            }
            if(flush && !EXCLUDED_PROPERTIES.containsKey(name))
            {
                delegate.reindex();    
            }
        }
        mc.getProperty = {String name->
            def operation = delegate.__InternalGetProperty__(DomainOperationProperty.PROP_NAME);
            if(!operation)
            {
                return delegate.__InternalGetProperty__(name);
            }
            else
            {
                try
                {
                    return operation.invokeMethod (GrailsClassUtils.getGetterName(name), [] as Object[]);
                }
                catch(groovy.lang.MissingMethodException m)
                {
                    return operation.getProperty (name);
                }
            }
        }
        mc.__InternalSetProperty__ = {String name, Object value->
            def metaProp = mc.getMetaProperty(name);
            if(!metaProp)
            {
                throw new MissingPropertyException(name, mc.theClass);
            }
            metaProp.setProperty(delegate, value);
        }
        mc.__InternalGetProperty__ = {String name->
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
            if(relation && (relation.isOneToMany() || relation.isManyToMany()) && !currentValue )
            {
                currentValue = [];
                domainObject.__InternalSetProperty__(name, currentValue);
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
                    def federatedValue = getFederatedProperty(mc, domainObject, name, propConfigCache, dsConfigCache);
                    if(federatedValue != null)
                    {
                        return federatedValue;
                    }
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
                                currentDomainObject.__InternalSetProperty__(requestedPropConfig.name, value);
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

        return null;
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

class DomainOperationProperty extends MetaBeanProperty
{
    WeakHashMap map = new WeakHashMap()
    Class operationClass;
    public static final String PROP_NAME = "__operation_class__";
    public DomainOperationProperty() {
        super(PROP_NAME, AbstractDomainOperation,null,null); //To change body of overridden methods use File | Settings | File Templates.
    }


    public Object getProperty(Object o) {
        def operation = map[o];
        if(!operation && operationClass)
        {
            operation = operationClass.newInstance() ;
            operationClass.metaClass.getMetaProperty("domainObject").setProperty(operation, o);
            map[o] = operation;
        }

        return operation;
    }

    public void setProperty(Object o, Object o1)
    {
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
