import com.ifountain.rcmdb.domain.IdGenerator
import com.ifountain.rcmdb.domain.IdGeneratorStrategyImpl
import com.ifountain.rcmdb.domain.constraints.KeyConstraint
import com.ifountain.rcmdb.domain.method.AsMapMethod
import com.ifountain.rcmdb.domain.method.ReloadOperationsMethod
import com.ifountain.rcmdb.domain.operation.DomainOperationManager
import com.ifountain.rcmdb.domain.property.DomainClassPropertyInterceptor
import com.ifountain.rcmdb.domain.property.DomainClassPropertyInterceptorFactoryBean
import com.ifountain.rcmdb.domain.util.DomainClassUtils
import com.ifountain.rcmdb.util.RapidCMDBConstants
import groovy.xml.MarkupBuilder
import java.lang.reflect.Field
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.commons.ControllerArtefactHandler
import org.codehaus.groovy.grails.commons.GrailsClass
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import org.springframework.validation.FieldError
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import com.ifountain.rcmdb.domain.property.RelationUtils
import com.ifountain.rcmdb.domain.method.GetPropertiesMethod

class RapidDomainClassGrailsPlugin {
    private static final Map EXCLUDED_PROPERTIES = ["id":"id", "version":"version", "errors":"errors", "__is_federated_properties_loaded__":RapidCMDBConstants.IS_FEDERATED_PROPERTIES_LOADED, "__operation_class__":RapidCMDBConstants.OPERATION_PROPERTY_NAME]
    def logger = Logger.getLogger("grails.app.plugins.RapidDomainClass")
    def version = 0.1
    def loadAfter = ['searchableExtension']
    def observe = ["controllers"]
    def domainClassMap;
    def doWithSpring = {
        ConstrainedProperty.registerNewConstraint(KeyConstraint.KEY_CONSTRAINT, KeyConstraint);
        domainPropertyInterceptor(DomainClassPropertyInterceptorFactoryBean) { bean ->
            propertyInterceptorClassName = ConfigurationHolder.getConfig().flatten().get(RapidCMDBConstants.PROPERTY_INTERCEPTOR_CLASS_CONFIG_NAME);
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
            if(!domainClassMap.containsKey(mc.getTheClass().getSuperclass().getName()))
            {
                domainClassesToBeCreated += dc;
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
        GetPropertiesMethod getPropertiesMethod = new GetPropertiesMethod(dc);
        MetaClass mc = dc.metaClass;
        mc.static.getModelProperties = {->
            return getPropertiesMethod.getDomainObjectProperties();
        }
        if(mc.getMetaProperty(RapidCMDBConstants.OPERATION_PROPERTY_NAME) != null)
        {
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
                            delegate[RapidCMDBConstants.OPERATION_PROPERTY_NAME] = oprInstance;
                        }
                        try {
                            return oprInstance.invokeMethod(name, args)
                        } catch (MissingMethodException e) {
                            if(e.getType().name != oprInstance.class.name || e.getMethod() != name)
                            {
                                throw e;
                            }
                        }
                    }
                }
                throw new MissingMethodException (name,  mc.theClass, args);
            }
            mc.'static'._methodMissing = {String methodName, args ->
                if(manager.operationClass != null)
                {
                    if(manager.operationClassMethods.containsKey(methodName))
                    {
                        try {
                            return manager.operationClass.metaClass.invokeStaticMethod(manager.operationClass, methodName, args);
                        } catch (MissingMethodException e) {
                            if(e.getType().name != manager.operationClass.name || e.getMethod() != methodName)
                            {
                                throw e;
                            }
                        }

                    }
                }
                throw new MissingMethodException (methodName,  mc.theClass, args);
            }
            mc.'static'.reloadOperations = {
                mc.invokeStaticMethod (dc.clazz, "reloadOperations", true);
            }
            mc.'static'.reloadOperations = {reloadSubclasses->
                method.invoke(mc.theClass, [reloadSubclasses] as Object[]);
                getPropertiesMethod.setOperationClass (manager.operationClass);
            }

            try
            {
                mc.invokeStaticMethod (dc.clazz, "reloadOperations", false);
            }
            catch(t)
            {
            }
        }

    }

    

    

    def addUtilityMetods(dc, application, ctx)
    {
        def mc = dc.metaClass;
        def asMapMethod = new AsMapMethod(mc, dc, logger, DomainClassUtils.getRelations(dc));
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
    
    def registerDynamicMethods(GrailsDomainClass dc, application, ctx)
    {
        def mc = dc.clazz.metaClass;
        addPropertyInterceptors(dc, application, ctx);
        addErrorsSupport(dc, application, ctx)
        addOperationsSupport(dc, application, ctx)
        addUtilityMetods (dc, application, ctx)
        for(subClass in dc.subClasses)
        {
            if(subClass.metaClass.getTheClass().getSuperclass().name == dc.metaClass.getTheClass().name)
            {
                registerDynamicMethods(subClass, application, ctx);
            }
        }
    }

    def addPropertyInterceptors(GrailsDomainClass dc, application, ctx)
    {
        def props =dc.getProperties();
        def persistantProps = DomainClassUtils.getPersistantProperties(dc, false);
        def relations = DomainClassUtils.getRelations(dc);
        DomainClassPropertyInterceptor propertyInterceptor = ctx.getBean("domainPropertyInterceptor");
        dc.metaClass.setProperty = {String name, Object value->
            delegate.setProperty(name, value, true);
        }
        dc.metaClass.setProperty = {String name, Object value, boolean flush->
            try
            {
                if(flush && !EXCLUDED_PROPERTIES.containsKey(name) && persistantProps.containsKey(name) && ((MetaClass)delegate.metaClass).getMetaMethod("update", Map) != null)
                {
                    delegate.update(["$name":value]);
                }
                else
                {
                    propertyInterceptor.setDomainClassProperty (delegate, name, value);
                }
            }
            catch(MissingPropertyException propEx)
            {
                try
                {
                    delegate.methodMissing(GrailsClassUtils.getSetterName(name), [value] as Object[]);
                }
                catch(Throwable ex)
                {
                    throw propEx
                }
            }
        }

        dc.metaClass.getProperty = {String name->
            try
            {
                def propValue = propertyInterceptor.getDomainClassProperty (delegate, name);
                if(!propValue)
                {
                    def relation = relations[name];
                    if(relation)
                    {
                        return RelationUtils.getRelatedObjects(delegate, relation);
                    }
                }
                return propValue;
            }
            catch(MissingPropertyException propEx)
            {
                try
                {
                    return delegate.methodMissing(GrailsClassUtils.getGetterName(name), null);
                }
                catch(Throwable ex)
                {
                    throw propEx
                }
            }
        }
     
    }
}
