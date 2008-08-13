package com.ifountain.rcmdb.domain

import com.ifountain.rcmdb.test.util.RapidCmdbMockTestCase
import com.ifountain.rcmdb.domain.property.DefaultDomainClassPropertyInterceptor
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import com.ifountain.rcmdb.domain.constraints.KeyConstraint
import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.ifountain.rcmdb.domain.operation.DomainOperationManager
import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import org.apache.commons.io.FileUtils

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 11, 2008
 * Time: 10:09:19 AM
 * To change this template use File | Settings | File Templates.
 */
class RapidDomainClassGrailsPluginTest extends RapidCmdbMockTestCase
{
    def domainClassName = "DomainClass1"
    Class loadedDomainClass;
    public void setUp() {
        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testDoWithSpring()
    {
        configParams[RapidCMDBConstants.PROPERTY_INTERCEPTOR_CLASS_CONFIG_NAME] = DomainPropertyInterceptorDomainClassGrailsPluginImpl.name;
        def pluginsToLoad = [gcl.loadClass("RapidDomainClassGrailsPlugin")];
        initialize([], pluginsToLoad)
        assertTrue(appCtx.getBean("domainPropertyInterceptor") instanceof DomainPropertyInterceptorDomainClassGrailsPluginImpl);
        assertTrue(ConstrainedProperty.hasRegisteredConstraint(KeyConstraint.KEY_CONSTRAINT));
    }

    public void testPropertyIntercepting()
    {
        loadedDomainClass = gcl.parseClass("""
            class ${domainClassName}{
                Long id;
                Long version;
                String prop1;
                Long prop2;
                public Object methodMissing(String methodName, args)
                {
                    ${RapidDomainClassGrailsPluginTest.class.name}
                }
            }
        """)
        def classesTobeLoaded = [loadedDomainClass];
        configParams[RapidCMDBConstants.PROPERTY_INTERCEPTOR_CLASS_CONFIG_NAME] = DomainPropertyInterceptorDomainClassGrailsPluginImpl.name;
        def pluginsToLoad = [gcl.loadClass("RapidDomainClassGrailsPlugin")];
        initialize(classesTobeLoaded, pluginsToLoad)
        
        def instance = loadedDomainClass.newInstance();
        instance.prop1 = "prop1Value";
        assertEquals ("prop1Value", instance.prop1);
        DomainPropertyInterceptorDomainClassGrailsPluginImpl interceptor = appCtx.getBean("domainPropertyInterceptor");
        assertEquals(1, interceptor.getPropertyList.size());
        assertEquals(1, interceptor.setPropertyList.size());
    }

    public void testPropertyInterceptingThrowsMissingPropertyExceptionInvokesGetterAndSetterMethods()
    {

        def baseDir = "../testOutput"
        FileUtils.deleteDirectory (new File(baseDir));
        System.setProperty("base.dir", baseDir)
        loadedDomainClass = gcl.parseClass("""
            class ${domainClassName}{
                Object ${RapidCMDBConstants.OPERATION_PROPERTY_NAME};
                Long id;
                Long version;
                String prop1;
                Long prop2;
                public Object methodMissing(String methodName, args)
                {
                    ${RapidDomainClassGrailsPluginTest.class.name}
                }
            }
        """)
        def undefinedPropValue = "undefinedPropValue"
        def operationFile = new File(baseDir +"/operations/${domainClassName}${DomainOperationManager.OPERATION_SUFFIX}.groovy");
        operationFile.parentFile.mkdirs();
        operationFile.setText ("""
            class ${domainClassName}${DomainOperationManager.OPERATION_SUFFIX} extends ${AbstractDomainOperation.class.name}{
                def undefinedProp = "${undefinedPropValue}";
                def getUndefinedProperty()
                {
                    return undefinedProp;
                }
                def setUndefinedProperty(Object value)
                {
                    undefinedProp = value;
                }
            }
        """);
        def classesTobeLoaded = [loadedDomainClass];
        configParams[RapidCMDBConstants.PROPERTY_INTERCEPTOR_CLASS_CONFIG_NAME] = DomainPropertyInterceptorDomainClassGrailsPluginImpl.name;
        def pluginsToLoad = [gcl.loadClass("RapidDomainClassGrailsPlugin")];
        initialize(classesTobeLoaded, pluginsToLoad)

        def instance = loadedDomainClass.newInstance();
        DomainPropertyInterceptorDomainClassGrailsPluginImpl interceptor = appCtx.getBean("domainPropertyInterceptor");
        assertEquals (undefinedPropValue, instance["undefinedProperty"]);
        instance["undefinedProperty"] = "updatedPropValue";
        assertEquals ("updatedPropValue", instance["undefinedProperty"]);

        try
        {
            instance["undefinedProperty2"]
            fail("Should throw exception since property not defined in operation and in domain class");
        }
        catch(MissingPropertyException ex)
        {
            assertEquals ("undefinedProperty2", ex.getProperty());
        }

        try
        {
            instance["undefinedProperty2"] = ""
            fail("Should throw exception since property not defined in operation and in domain class");
        }
        catch(MissingPropertyException ex)
        {
            assertEquals ("undefinedProperty2", ex.getProperty());
        }
    }

}

class DomainPropertyInterceptorDomainClassGrailsPluginImpl extends DefaultDomainClassPropertyInterceptor
{
    def setPropertyList = []
    def getPropertyList = []
    public void setDomainClassProperty(Object domainObject, String propertyName, Object value) {
        super.setDomainClassProperty(domainObject, propertyName, value);    //To change body of overridden methods use File | Settings | File Templates.
        setPropertyList += propertyName
    }

    public Object getDomainClassProperty(Object domainObject, String propertyName) {
        getPropertyList += propertyName
        return super.getDomainClassProperty(domainObject, propertyName);    //To change body of overridden methods use File | Settings | File Templates.
    }

}