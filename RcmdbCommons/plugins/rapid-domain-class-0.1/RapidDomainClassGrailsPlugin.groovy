import com.ifountain.rcmdb.domain.IdGenerator
import com.ifountain.rcmdb.domain.IdGeneratorStrategyImpl
import com.ifountain.rcmdb.domain.constraints.KeyConstraint
import com.ifountain.rcmdb.domain.method.*
import com.ifountain.rcmdb.domain.operation.DomainOperationLoadException
import com.ifountain.rcmdb.domain.operation.DomainOperationManager
import com.ifountain.rcmdb.domain.property.*
import com.ifountain.rcmdb.domain.util.DomainClassDefaultPropertyValueHolder
import com.ifountain.rcmdb.domain.util.DomainClassUtils
import com.ifountain.rcmdb.domain.util.InvokeOperationUtils
import com.ifountain.rcmdb.methods.MethodFactory
import com.ifountain.rcmdb.util.RapidCMDBConstants
import groovy.xml.MarkupBuilder
import java.lang.reflect.Field
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.*
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import org.springframework.validation.FieldError
import org.codehaus.groovy.runtime.InvokerHelper
import com.ifountain.rcmdb.domain.util.RelationMetaData

/*
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
class RapidDomainClassGrailsPlugin {
    private static final Map EXCLUDED_PROPERTIES = [:]
    static{
        EXCLUDED_PROPERTIES["id"] = "id";
        EXCLUDED_PROPERTIES["version"] = "version";
        EXCLUDED_PROPERTIES["errors"] = "errors";
        EXCLUDED_PROPERTIES[RapidCMDBConstants.IS_FEDERATED_PROPERTIES_LOADED] = RapidCMDBConstants.IS_FEDERATED_PROPERTIES_LOADED;
        EXCLUDED_PROPERTIES[RapidCMDBConstants.OPERATION_PROPERTY_NAME] = RapidCMDBConstants.OPERATION_PROPERTY_NAME;
    }
    def logger = Logger.getLogger("grails.app.plugins.RapidDomainClass")
    def version = 0.1
    def loadAfter = ['searchableExtension']
    def observe = ["controllers"]
    def domainClassMap;
    def operationClassManagers;
    def doWithSpring = {
        operationClassManagers = [:];
        ConstrainedProperty.registerNewConstraint(KeyConstraint.KEY_CONSTRAINT, KeyConstraint);
        domainPropertyInterceptor(DomainClassPropertyInterceptorFactoryBean) { bean ->
            propertyInterceptorClassName = ConfigurationHolder.getConfig().flatten().get(RapidCMDBConstants.PROPERTY_INTERCEPTOR_CLASS_CONFIG_NAME);
            classLoader = application.getClassLoader()
        }
        propertyDatasourceManager(PropertyDatasourceManagerBean)
        {
        }
        DomainClassDefaultPropertyValueHolder.initialize (application.domainClasses.clazz);
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
            addControllerMethods(controller, ctx);
        }
    }

    def onChange = { event ->
        if (application.isArtefactOfType(ControllerArtefactHandler.TYPE, event.source)) {
            addControllerMethods(event.source, event.ctx);
        }
    }

    def onApplicationChange = { event ->
    }

    def addControllerMethods(controller, ctx)
    {
        MetaClass mc = controller.metaClass
        mc.addError = {String messageCode->

            delegate.addError(messageCode, [])

        }
        mc.addError = {String messageCode, List params->

            delegate.addError(messageCode, params, "")

        }

        mc."${MethodFactory.WITH_SESSION_METHOD}" = MethodFactory.createMethod(MethodFactory.WITH_SESSION_METHOD);

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
            mc.hasErrors = {String fieldName-> delegate.errors?.hasFieldErrors(fieldName) }
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
        FederatedPropertyManager impl = ctx.getBean(PropertyDatasourceManagerBean.BEAN_ID)
        GetPropertiesMethod getPropertiesMethod = new GetPropertiesMethod(dc, impl);
        KeySetMethod keySetMethod = new KeySetMethod(dc, impl);
        GetRootClassMethod getRootClassMethod = new GetRootClassMethod(dc, application.getDomainClasses());
        MetaClass mc = dc.metaClass;
        GetOperationsMethod getOperationsMethod = new GetOperationsMethod(mc);
        mc.static.getRootClass = {->
            return getRootClassMethod.rootClass;
        }
        mc.static.getPropertiesList = {->
            return getPropertiesMethod.getDomainObjectProperties();
        }
        mc.static.getFederatedPropertyList = {->
            return getPropertiesMethod.getFederatedProperties();
        }
        mc.static.getRelationPropertyList = {->
            return getPropertiesMethod.getRelations();
        }
        mc.static.getNonFederatedPropertyList = {->
            return getPropertiesMethod.getNonFederatedProperties();
        }
        mc.static.getOperations = {->
            return getOperationsMethod.getOperations();
        }
        mc.static.keySet= {->
            return keySetMethod.getKeys();
        }
        if(mc.getMetaProperty(RapidCMDBConstants.OPERATION_PROPERTY_NAME) != null)
        {
            DomainOperationManager parentClassManager = operationClassManagers[dc.clazz.superclass.name];
            def defaultOperationsMethods = ["${MethodFactory.WITH_SESSION_METHOD}":[method:MethodFactory.createMethod(MethodFactory.WITH_SESSION_METHOD), isStatic:true]];
            DomainOperationManager manager = new DomainOperationManager(dc.clazz, "${System.getProperty("base.dir")}/operations".toString(), parentClassManager, defaultOperationsMethods, application.classLoader);
            operationClassManagers[dc.clazz.name] = manager;
            ReloadOperationsMethod reloadOperationsMethod = new ReloadOperationsMethod(dc.metaClass, DomainClassUtils.getSubClasses(dc), manager, logger);
            def cls = mc.theClass;
            mc.invokeOperation =  {String name, args ->
                return InvokeOperationUtils.invokeMethod(delegate, name, args, manager.getOperationClass(), manager.getOperationClassMethods());
            }
            mc.propertyMissing = {String name->
                def domainObject = delegate;
                def getterName = GrailsClassUtils.getGetterName(name);
                return convertPropertyMissingException(delegate.class, name, getterName){
                    return InvokeOperationUtils.invokeMethod(domainObject, getterName, InvokerHelper.EMPTY_ARGS, manager.getOperationClass(), manager.getOperationClassMethods());
                }

            }
            mc.methodMissing =  {String name, args ->
                return InvokeOperationUtils.invokeMethod(delegate, name, args, manager.getOperationClass(), manager.getOperationClassMethods());
            }
            mc.'static'.invokeStaticOperation = {String methodName, args ->
                return InvokeOperationUtils.invokeStaticMethod(mc.theClass, methodName, args, manager.getOperationClass(), manager.getOperationClassMethods());
            }
            mc.'static'.methodMissing = {String methodName, args ->
                return InvokeOperationUtils.invokeStaticMethod(cls, methodName, args, manager.getOperationClass(), manager.getOperationClassMethods());
            }
            mc.'static'.reloadOperations = {
                mc.invokeStaticMethod (dc.clazz, "reloadOperations", true);
            }
            mc.'static'.reloadOperations = {reloadSubclasses->
                reloadOperationsMethod.invoke(mc.theClass, [reloadSubclasses] as Object[]);
                getPropertiesMethod.setOperationClass (manager.getOperationClass());
                getOperationsMethod.setOperationClass (manager.getOperationClass());
            }
              
            try
            {
                mc.invokeStaticMethod (dc.clazz, "reloadOperations", false);
            }
            catch(t)
            {
                //the errors is already logged by the reloadOperations method _invoke(), but invoke() exceptions are ignored                
                //logger.warn("[RapidDomainClassGrailsPlugin]: Error in invoke reloadOperations for domain ${dc.clazz.name}. Reason :"+t.toString());
            }
        }
        else
        {
            throw DomainOperationLoadException.operationPropertyIsNotDefined(mc.theClass.name)
        }

    }

    

    

    def addUtilityMetods(dc, application, ctx)
    {
        def mc = dc.metaClass;
        def asMapMethod = new AsMapMethod(mc, dc.clazz, logger);
        mc.asMap = {->
            return asMapMethod.invoke(delegate, null);
        };
        mc.asMap = {List requestedProperties->
            return asMapMethod.invoke(delegate, [requestedProperties] as Object[]);
        };
        mc.cloneObject = {->
            def cloned = dc.clazz.newInstance();
            def domainObject = delegate;
            domainObject.getNonFederatedPropertyList().each{p ->
                cloned.setPropertyWithoutUpdate(p.name, domainObject[p.name])
            }
            def filteredProps = ["version", RapidCMDBConstants.ERRORS_PROPERTY_NAME];
            filteredProps.each{propName ->
                cloned.setPropertyWithoutUpdate(propName, domainObject[propName])
            }
            return cloned;
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
    def convertPropertyMissingException(Class expectedExceptionClass, String propertyName, String expectedMethodName, Closure closureToBeInvoked)
    {
        try
        {
            return closureToBeInvoked();
        }
        catch(MissingMethodException ex)
        {
            if(ex.getType().name == expectedExceptionClass.name && ex.getMethod() == expectedMethodName)
            {
                throw new MissingPropertyException(propertyName, expectedExceptionClass);
            }
            else
            {
                throw ex;
            }
        }
    }
    def addPropertyInterceptors(GrailsDomainClass dc, application, ctx)
    {
        FederatedPropertyManager impl = ctx.getBean(PropertyDatasourceManagerBean.BEAN_ID)
        GetPropertiesMethod getPropertiesMethod = new GetPropertiesMethod(dc, impl);
        def props =dc.getProperties();
        def persistantProps = DomainClassUtils.getPersistantProperties(dc, false);
        def relations = DomainClassUtils.getRelations(dc);
        MetaClass dcMetaCls = dc.metaClass;
        Class dcClass = dc.clazz;
        DomainClassPropertyInterceptor propertyInterceptor = ctx.getBean("domainPropertyInterceptor");
        dc.metaClass.setPropertyWithoutUpdate = {String name, Object value->
            delegate.setProperty(name, value, false);
        }
        dc.metaClass.setProperty = {String name, Object value->
            delegate.setProperty(name, value, true);
        }
        dc.metaClass.setProperty = {String name, Object value, boolean flush->
            try
            {
                if(flush && !EXCLUDED_PROPERTIES.containsKey(name) && persistantProps.containsKey(name) && ((MetaClass)delegate.metaClass).getMetaMethod("update", Map) != null)
                {
                    delegate.invokeCompassOperation("updateForSetProperty", [["$name":value]]);
                }
                else
                {
                    if(relations[name] == null)
                    {
                        propertyInterceptor.setDomainClassProperty (dcMetaCls, dcClass, delegate, name, value);
                    }
                    else
                    {
                        def dynamicPropertyStorage = delegate[RapidCMDBConstants.IS_FEDERATED_PROPERTIES_LOADED];
                        if(dynamicPropertyStorage == null)
                        {
                            dynamicPropertyStorage = [:]
                            delegate[RapidCMDBConstants.IS_FEDERATED_PROPERTIES_LOADED] = dynamicPropertyStorage;
                        }
                        dynamicPropertyStorage[name] = value;
                    }
                }
            }
            catch(MissingPropertyException propEx)
            {

                def setterName = GrailsClassUtils.getSetterName(name);
                convertPropertyMissingException(delegate.class, name, setterName){
                    delegate.methodMissing(GrailsClassUtils.getSetterName(name), [value] as Object[]);
                }
            }

        }

        getPropertiesMethod.getFederatedProperties().name.each{propName->
            dc.metaClass."${GrailsClassUtils.getGetterName(propName)}" = {->
                return propertyInterceptor.getDomainClassProperty (dcMetaCls, dcClass, delegate, propName);
            }
            dc.metaClass."${GrailsClassUtils.getSetterName(propName)}" = {value->
                propertyInterceptor.setDomainClassProperty (dcMetaCls, dcClass, delegate, propName, value);
            }
        }
        relations.each{relName, metaData->
            dc.metaClass."${GrailsClassUtils.getGetterName(relName)}" = {->
                def dynamicPropertyStorage = delegate[RapidCMDBConstants.IS_FEDERATED_PROPERTIES_LOADED];
                if(dynamicPropertyStorage != null && dynamicPropertyStorage.containsKey(relName))
                {
                    return dynamicPropertyStorage.get(relName);
                }
                else
                {
                    return RelationUtils.getRelatedObjects(delegate, metaData);
                }
            }
            dc.metaClass."${GrailsClassUtils.getSetterName(relName)}" = {value->
                delegate.invokeCompassOperation("updateForSetProperty", [["$relName":value]]);
            }
        }
    }
}
