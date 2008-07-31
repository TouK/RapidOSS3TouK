import com.ifountain.rcmdb.domain.AbstractDomainOperation
import com.ifountain.rcmdb.domain.IdGenerator
import com.ifountain.rcmdb.domain.IdGeneratorStrategyImpl
import com.ifountain.rcmdb.domain.constraints.KeyConstraint
import com.ifountain.rcmdb.domain.method.AsMapMethod
import com.ifountain.rcmdb.domain.util.DatasourceConfigurationCache
import com.ifountain.rcmdb.domain.util.DomainClassUtils
import com.ifountain.rcmdb.domain.util.PropertyConfigurationCache
import com.ifountain.rcmdb.domain.util.Relation
import com.ifountain.rcmdb.util.RapidCMDBConstants
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import org.codehaus.groovy.grails.commons.metaclass.WeakGenericDynamicProperty
import org.codehaus.groovy.grails.commons.metaclass.FunctionCallback

class RapidDomainClassGrailsPlugin {
    private static final Map EXCLUDED_PROPERTIES = ["id":"id", "version":"version", "errors":"errors"]
    def logger = Logger.getLogger("grails.app.plugins.RapidDomainClass")
    def version = 0.1
    def loadAfter = ['searchable-extension']
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

    private boolean isSearchable(mc)
    {
        def metaProp = mc.getMetaProperty("searchable");
        return metaProp != null;
    }


    def addOperationsSupport(GrailsDomainClass dc, application, ctx)
    {
        def mc = dc.metaClass;
        DomainOperationProperty operationProperty = new DomainOperationProperty()
        mc.addMetaBeanProperty (operationProperty)
        def operationClassName = dc.name + "Operations";
        mc.methodMissing =  {String name, args ->
        	delegate.methodMissing(name, args, true);
        }
        mc.methodMissing =  {String name, args, willDelagateToOperation ->
        	if(willDelagateToOperation)
        	{
	            def oprInstance = operationProperty.getProperty(delegate);
	            if(oprInstance)
	            {
	                try {
	                    return oprInstance.invokeMethod(name, args)
	                } catch (MissingMethodException e) {
	                    if(e.getType().name != oprInstance.class.name)
	                    {
	                        throw e;
	                    }
	                }
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

    

    

    def addUtilityMetods(dc, application, ctx)
    {
        def mc = dc.metaClass;
        def asMapMethod = new AsMapMethod(mc, dc, logger);
        mc.asMap = {->
            return asMapMethod.invoke(delegate, null);
        };
        mc.asMap = {List requestedProperties->
            return asMapMethod.invoke(delegate, [requestedProperties] as Object[]);
        };
        mc.'static'.create = {Map props->
            def sampleBean = delegate.newInstance();
            props.each{key,value->
                sampleBean.setProperty (key, value);
            }
            return sampleBean;
        }
    }
    
    def registerDynamicMethods(dc, application, ctx)
    {
        def mc = dc.clazz.metaClass;
        addOperationsSupport(dc, application, ctx)
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

    def addPropertyGetAndSetMethods(GrailsDomainClass dc)
    {
        def relations = DomainClassUtils.getRelations(dc);
        MetaClass mc = dc.metaClass
        def propConfigCache = new PropertyConfigurationCache(dc);
        def dsConfigCache = new DatasourceConfigurationCache(dc);
        def persistantProps = DomainClassUtils.getPersistantProperties(dc, false);
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
            if(flush && !EXCLUDED_PROPERTIES.containsKey(name) && delegate.id != null && persistantProps.containsKey(name))
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
            if(relation && (relation.isOneToMany() || relation.isManyToMany()) && currentValue == null)
            {
                currentValue = new HashSet();
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



class DatasourceProperty extends MetaBeanProperty implements FunctionCallback
{
    WeakGenericDynamicProperty dynamicProp;
    public DatasourceProperty(String s, Class aClass) {
        super(s, aClass, null, null); //To change body of overridden methods use File | Settings | File Templates.
        dynamicProp = new WeakGenericDynamicProperty(s, aClass, this, false)
    }

    public Object getProperty(Object o) {
        return dynamicProp.get(o)
    }

    public Object execute(Object object) {
        return [:];  //To change body of implemented methods use File | Settings | File Templates.
    }


    public void setProperty(Object o, Object o1) {
    }
}

class DomainOperationProperty extends MetaBeanProperty  implements FunctionCallback
{
    WeakGenericDynamicProperty dynamicProp;
    Class operationClass;
    public static final String PROP_NAME = "__operation_class__";
    public DomainOperationProperty() {
        super(PROP_NAME, AbstractDomainOperation,null,null); //To change body of overridden methods use File | Settings | File Templates.
        dynamicProp = new WeakGenericDynamicProperty(PROP_NAME, AbstractDomainOperation, this, false)
    }

    public Object execute(Object object) {
        def operation = operationClass.newInstance() ;
        operationClass.metaClass.getMetaProperty("domainObject").setProperty(operation, object);
        return operation;
    }
    public Object getProperty(Object o) {
        if(operationClass)
        {
            return dynamicProp.get(o)
        }
        else
        {
            return null;
        }
    }

    public void setProperty(Object o, Object o1)
    {
    }

}
