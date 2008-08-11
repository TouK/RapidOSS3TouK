import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
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
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import java.lang.reflect.Field
import org.codehaus.groovy.grails.commons.GrailsClass
import groovy.xml.MarkupBuilder
import org.springframework.validation.FieldError
import org.codehaus.groovy.grails.commons.ControllerArtefactHandler
import com.ifountain.rcmdb.domain.operation.DomainOperationManager
import com.ifountain.rcmdb.domain.method.ReloadOperationsMethod
import com.ifountain.rcmdb.domain.property.DomainClassPropertyInterceptorFactoryBean
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class RapidDomainClassGrailsPlugin {
    private static final Map EXCLUDED_PROPERTIES = ["id":"id", "version":"version", "errors":"errors"]
    def logger = Logger.getLogger("grails.app.plugins.RapidDomainClass")
    def version = 0.1
    def loadAfter = ['searchable-extension']
    def observe = ["controllers"]
    def domainClassMap;
    def doWithSpring = {
        ConstrainedProperty.registerNewConstraint(KeyConstraint.KEY_CONSTRAINT, KeyConstraint);
        domainPropertyInterceptor(DomainClassPropertyInterceptorFactoryBean) { bean ->
            propertyInterceptorClassName = ConfigurationHolder.getConfig().flatten().get("domain.property.interceptor.class");
            classLoader = application.getClassLoader()
        }
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
            DomainOperationManager manager = new DomainOperationManager(dc.clazz, "${System.getProperty("base.dir")}/operations".toString());

            ReloadOperationsMethod method = new ReloadOperationsMethod(dc.metaClass, DomainClassUtils.getSubClasses(dc), manager, logger);
            mc.methodMissing =  {String name, args ->
                if(manager.operationClass != null)
                {
                    if(manager.operationClassMethods.containsKey(name))
                    {
                        def oprInstance = delegate[RapidCMDBConstants.OPERATION_PROPERTY_NAME];
                        if(oprInstance == null)
                        {
                            oprInstance = manager.operationClass.newInstance() ;
                            manager.operationClass.metaClass.getMetaProperty("domainObject").setProperty(oprInstance, delegate);
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
                method.invoke(mc.theClass, [reloadSubclasses] as Object[]);
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
                if(!propertyConfig || propertyConfig.datasource == RapidCMDBConstants.RCMDB)
                {
                    return currentValue;
                }
                else
                {
                    if(!propertyConfig.lazy)
                    {

                        def isPropLoadedMap = mc.getMetaProperty(RapidCMDBConstants.IS_FEDERATED_PROPERTIES_LOADED).getProperty(domainObject);
                        if(isPropLoadedMap != null && isPropLoadedMap[propConfigCache.getDatasourceName(domainObject, name)] == true)
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
        def isPropsLoadedMap = currentDomainObject[RapidCMDBConstants.IS_FEDERATED_PROPERTIES_LOADED];
        if(isPropsLoadedMap == null)
        {
            isPropsLoadedMap = [:];
            currentDomainObject.setProperty(RapidCMDBConstants.IS_FEDERATED_PROPERTIES_LOADED, isPropsLoadedMap);
        }
        def propertyDatasource = propCache.getDatasource(currentDomainObject, propertyName);
        if(propertyDatasource)
        {
            def keys = dsCache.getKeys(currentDomainObject, propertyDatasource.name);
            if(keys != null && keys.size() > 0)
            {
                def isPropsLoaded = isPropsLoadedMap[propertyDatasource.name];
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
                        isPropsLoadedMap[propertyDatasource.name] = true;
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
