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
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import java.lang.reflect.Field
import org.codehaus.groovy.grails.commons.GrailsClass
import groovy.xml.MarkupBuilder
import org.springframework.validation.FieldError

class RapidDomainClassGrailsPlugin {
    private static final Map EXCLUDED_PROPERTIES = ["id":"id", "version":"version", "errors":"errors"]
    def logger = Logger.getLogger("grails.app.plugins.RapidDomainClass")
    def version = 0.1
    def loadAfter = ['searchable-extension']
    def observe = ["controllers"]
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
        for (GrailsClass controller in application.controllerClasses) {
            addErrorsSupportToControllers(controller, ctx);
        }
    }

    def onChange = { event ->
        if (application.isArtefactOfType(ControllerArtefactHandler.TYPE, event.source)) {
            addErrorsSupportToControllers(event.source, event.ctx);   
        }
    }

    def onApplicationChange = { event ->
    }

    private boolean isSearchable(mc)
    {
        def metaProp = mc.getMetaProperty("searchable");
        return metaProp != null;
    }

    def addErrorsSupportToControllers(controller, ctx)
    {
        MetaClass mc = controller.metaClass
        mc.addError = {String messageCode->

            delegate.addError(messageCode, [])

        }
        mc.addError = {String messageCode, List params->

            delegate.addError(messageCode, params, "")

        }
        mc.addError = {String messageCode, List params, String defaultMessage->
            if(!delegate.hasErrors())
            {
                delegate.errors = new RapidBindException(delegate, delegate.class.name);
            }
            delegate.errors.reject(messageCode, params as Object[],defaultMessage)

        }
        def messageSource = ctx.getBean("messageSource");
        mc.errorsToXml = {->
            delegate.errorsToXml(delegate.errors);
        }
        mc.errorsToXml = {errors->
            StringWriter writer = new StringWriter();
            def builder = new MarkupBuilder(writer);
            builder.Errors(){
                errors.getAllErrors().each{error->
                    def message = messageSource.getMessage( error,Locale.ENGLISH);
                    if(error instanceof FieldError)
                    {
                        def field = error.getField();
                        builder.Error(field:field, error:message)
                    }
                    else
                    {
                        builder.Error(error:message)
                    }
                }
            }

            return writer.toString();

        }
    }

    def addErrorsSupport(GrailsDomainClass dc, application, ctx)
    {
        def mc = dc.metaClass;
        try
        {
        	Field errField = mc.theClass.getDeclaredField("errors");
            errField.setAccessible (true);
            mc.hasErrors = {-> delegate.errors?.hasErrors() }
	        mc.getErrors = {->
	            def errors = errField.get(delegate);
	            if(errors == null)
	            {
	            	errors = new BeanPropertyBindingResult(delegate, delegate.getClass().getName());
	            	delegate.setErrors(errors);
	            }
	            return errors;
	       }
	        mc.setErrors = { Errors errors ->
	            errField.set(delegate, errors);
	        }
	        mc.clearErrors = {->
	            delegate.setErrors (new BeanPropertyBindingResult(delegate, delegate.getClass().getName()))
	        }
        }
        catch(java.lang.NoSuchFieldException ex)
        {
        }
    }
    def addOperationsSupport(GrailsDomainClass dc, application, ctx)
    {
        def mc = dc.metaClass;
        try
        {
            mc.theClass.getDeclaredField(RapidCMDBConstants.OPERATION_PROPERTY_NAME);
            def operationClassName = dc.name + "Operations";
            Class operationClass = null;
            def operationMethods = [:]
            mc.methodMissing =  {String name, args ->
                if(operationClass != null)
                {
                    if(operationMethods.containsKey(name))
                    {
                        def oprInstance = delegate[RapidCMDBConstants.OPERATION_PROPERTY_NAME];
                        if(oprInstance == null)
                        {
                            oprInstance = operationClass.newInstance() ;
                            operationClass.metaClass.getMetaProperty("domainObject").setProperty(oprInstance, delegate);
                        }
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
                    operationMethods.clear();
                    operationClass = oprClass;
                    operationClass.metaClass.methods.each{
                        operationMethods[it.name] = it.name;
                    };
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
        catch(NoSuchFieldException  ex)
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
        addErrorsSupport(dc, application, ctx)
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
        def hasOperationProp = GrailsClassUtils.getProperty(mc.theClass, RapidCMDBConstants.OPERATION_PROPERTY_NAME, AbstractDomainOperation) != null
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
            def operation = null
            if(hasOperationProp)
            {
                operation = delegate.__InternalGetProperty__(RapidCMDBConstants.OPERATION_PROPERTY_NAME);
            }
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
            def operation = null
            if(hasOperationProp)
            {
                operation = delegate.__InternalGetProperty__(RapidCMDBConstants.OPERATION_PROPERTY_NAME);
            }
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
