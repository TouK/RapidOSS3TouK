package com.ifountain.rcmdb.domain

import com.ifountain.rcmdb.test.util.RapidCmdbMockTestCase
import com.ifountain.rcmdb.domain.property.DefaultDomainClassPropertyInterceptor
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import com.ifountain.rcmdb.domain.constraints.KeyConstraint
import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.ifountain.rcmdb.domain.operation.DomainOperationManager
import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin
import application.ObjectId

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

    public void testPropertyInterceptingWithRelations()
    {
        String domainClassName2 = "DomainClass2"
        loadedDomainClass = gcl.parseClass("""
            class ${domainClassName}{
                static searchable = {
                    except:["rel1"]
                }
                Long id;
                Long version;
                String prop1;
                List rel1 = [];
                static relations = [rel1:[type:${domainClassName2}, reverseName:"revRel1", isMany:true]]
            }
            class ${domainClassName2}{
                static searchable = {
                    except:["revRel1"]
                }
                Long id;
                Long version;
                String prop1;
                List revRel1 = [];
                static relations = [revRel1:[type:${domainClassName}, reverseName:"rel1", isMany:true]]
            }
        """)
        Class loadedDomainClass2 = gcl.loadClass(domainClassName2);
        def classesTobeLoaded = [loadedDomainClass, loadedDomainClass2, relation.Relation, application.ObjectId];
        configParams[RapidCMDBConstants.PROPERTY_INTERCEPTOR_CLASS_CONFIG_NAME] = DomainPropertyInterceptorDomainClassGrailsPluginImpl.name;
        def pluginsToLoad = [DomainClassGrailsPlugin, gcl.loadClass("SearchableGrailsPlugin"), gcl.loadClass("SearchableExtensionGrailsPlugin"), gcl.loadClass("RapidDomainClassGrailsPlugin")];
        initialize(classesTobeLoaded, pluginsToLoad)


        def domainClass2Instance1 = loadedDomainClass2.metaClass.invokeStaticMethod(loadedDomainClass2, "add", [[prop1:"obj1Prop1Value"]] as Object[]);
        def domainClass1Instance1 = loadedDomainClass.metaClass.invokeStaticMethod(loadedDomainClass, "add", [[prop1:"prop1Value", rel1:domainClass2Instance1]] as Object[]);
        assertEquals ("prop1Value", domainClass1Instance1.prop1);
        assertEquals (domainClass2Instance1.id, domainClass1Instance1.rel1[0].id);
        assertEquals (domainClass1Instance1.id, domainClass2Instance1.revRel1[0].id);
        assertNotSame (domainClass1Instance1.rel1, domainClass1Instance1.rel1);

        domainClass1Instance1.setProperty("rel1", ["nonemptyulist"], false);
        assertSame (domainClass1Instance1.rel1, domainClass1Instance1.rel1);
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
        def undefinedProp3ExceptionMessage = "Undefined property 3 exception"
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
                def getUndefinedProperty3()
                {
                    throw new Exception("${undefinedProp3ExceptionMessage}");
                }

                def setUndefinedProperty3(Object value)
                {
                    throw new Exception("${undefinedProp3ExceptionMessage}");
                }

                def getUndefinedProperty4()
                {
                    throw new MissingMethodException("anotherMethod", this.class, null);
                }

                def setUndefinedProperty4(Object value)
                {
                    throw new MissingMethodException("anotherMethod", this.class, null);
                }

                def getUndefinedProperty5()
                {
                    throw new MissingMethodException("getUndefinedProperty5", Object.class, null);
                }

                def setUndefinedProperty5(Object value)
                {
                    throw new MissingMethodException("setUndefinedProperty5", Object.class, null);
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

        try
        {
            instance["undefinedProperty3"]
            fail("Should throw exception real exception since property found but an exception occurreed in it");
        }
        catch(Exception ex)
        {
            assertEquals (undefinedProp3ExceptionMessage, ex.getMessage());
        }

        try
        {
            instance["undefinedProperty3"] = 5;
            fail("Should throw exception real exception since property found but an exception occurreed in it");
        }
        catch(Exception ex)
        {
            assertEquals (undefinedProp3ExceptionMessage, ex.getMessage());
        }

        try
        {
            instance["undefinedProperty4"]
            fail("Should throw exception real missingmethodexception exception since thios exception throwed from another method");
        }
        catch(MissingMethodException ex)
        {
            assertEquals ("anotherMethod", ex.getMethod());
        }

        try
        {
            instance["undefinedProperty4"] = 5;
            fail("Should throw exception real missingmethodexception exception since thios exception throwed from another method");
        }
        catch(MissingMethodException ex)
        {
            assertEquals ("anotherMethod", ex.getMethod());
        }

        try
        {
            instance["undefinedProperty5"]
            fail("Should throw exception real missingmethodexception exception since thios exception throwed from another method");
        }
        catch(MissingMethodException ex)
        {
            assertEquals ("getUndefinedProperty5", ex.getMethod());
        }

        try
        {
            instance["undefinedProperty5"] = 5;
            fail("Should throw exception real missingmethodexception exception since thios exception throwed from another method");
        }
        catch(MissingMethodException ex)
        {
            assertEquals ("setUndefinedProperty5", ex.getMethod());
        }
    }

    public void testSaveInstanceForEachSetproperty()
    {
        loadedDomainClass = gcl.parseClass("""
            class ${domainClassName}{
                static searchable = {
                    except = ["prop2"]
                }
                Long id;
                Long version;
                String prop1;
                String prop2;
                static transients = ["prop2"]
                public Object methodMissing(String methodName, args)
                {
                    ${RapidDomainClassGrailsPluginTest.class.name}
                }
            }
        """)
        def classesTobeLoaded = [loadedDomainClass, ObjectId];
        configParams[RapidCMDBConstants.PROPERTY_INTERCEPTOR_CLASS_CONFIG_NAME] = DomainPropertyInterceptorDomainClassGrailsPluginImpl.name;
        def pluginsToLoad = [DomainClassGrailsPlugin, gcl.loadClass("SearchableGrailsPlugin"), gcl.loadClass("SearchableExtensionGrailsPlugin"), gcl.loadClass("RapidDomainClassGrailsPlugin")];
        initialize(classesTobeLoaded, pluginsToLoad)

        def instance = loadedDomainClass.metaClass.invokeStaticMethod(loadedDomainClass, "add", [[prop1:"prop1Value"]]  as Object[] )
        instance.prop1 = "updatedValue"
        def reloadedInstance = loadedDomainClass.metaClass.invokeStaticMethod(loadedDomainClass, "get", [[id:instance.id]] as Object[])
        assertEquals ("updatedValue", reloadedInstance.prop1);

        def numberOfCalls = 0;
        instance.metaClass.update = {Map props->
            numberOfCalls ++;
        }
        instance.prop2 = "transientPropvalueShouldNotUpdate"
        assertEquals (0, numberOfCalls);
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