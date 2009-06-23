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
package plugintests;

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
import com.ifountain.rcmdb.domain.util.DomainClassDefaultPropertyValueHolder;
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import com.ifountain.rcmdb.domain.IdGenerator
import com.ifountain.rcmdb.domain.operation.DomainOperationLoadException
import org.springframework.validation.BindingResult
import org.springframework.validation.BeanPropertyBindingResult
import com.ifountain.rcmdb.domain.generation.ModelGenerator

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
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void tearDown() {
        IdGenerator.destroy();
        super.tearDown();
    }



    public void testDoWithSpring()
    {
        configParams[RapidCMDBConstants.PROPERTY_INTERCEPTOR_CLASS_CONFIG_NAME] = DomainPropertyInterceptorDomainClassGrailsPluginImpl.name;
        def pluginsToLoad = [gcl.loadClass("RapidDomainClassGrailsPlugin")];
        initialize([], pluginsToLoad)
        assertTrue(appCtx.getBean("domainPropertyInterceptor") instanceof DomainPropertyInterceptorDomainClassGrailsPluginImpl);
        assertTrue(ConstrainedProperty.hasRegisteredConstraint(KeyConstraint.KEY_CONSTRAINT));
    }

    public void testGetrootClass()
    {
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
        def classesTobeLoaded = [loadedDomainClass];
        configParams[RapidCMDBConstants.PROPERTY_INTERCEPTOR_CLASS_CONFIG_NAME] = DomainPropertyInterceptorDomainClassGrailsPluginImpl.name;
        def pluginsToLoad = [gcl.loadClass("RapidDomainClassGrailsPlugin")];
        initialize(classesTobeLoaded, pluginsToLoad)
        assertEquals (loadedDomainClass, loadedDomainClass.getRootClass());
    }
    public void testPropertyIntercepting()
    {
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
        def classesTobeLoaded = [loadedDomainClass];
        configParams[RapidCMDBConstants.PROPERTY_INTERCEPTOR_CLASS_CONFIG_NAME] = DomainPropertyInterceptorDomainClassGrailsPluginImpl.name;
        def pluginsToLoad = [gcl.loadClass("RapidDomainClassGrailsPlugin")];
        initialize(classesTobeLoaded, pluginsToLoad)

        def instance = loadedDomainClass.newInstance();
        instance.prop1 = "prop1Value";
        assertEquals("prop1Value", instance.prop1);
        def interceptor = appCtx.getBean("domainPropertyInterceptor");
        assertEquals(1, interceptor.getPropertyList.size());
        assertEquals(1, interceptor.setPropertyList.size());
    }

    public void testCloneObject() {
        loadedDomainClass = gcl.parseClass("""
            class ${domainClassName}{
                Object ${RapidCMDBConstants.OPERATION_PROPERTY_NAME};
                org.springframework.validation.Errors ${RapidCMDBConstants.ERRORS_PROPERTY_NAME};
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
        def pluginsToLoad = [gcl.loadClass("RapidDomainClassGrailsPlugin")];
        initialize(classesTobeLoaded, pluginsToLoad)

        def instance = loadedDomainClass.newInstance();
        instance.id = 1;
        instance.version = 2;
        instance.prop1 = "prop1Value"
        instance.prop2 = 2;
        instance.errors = new BeanPropertyBindingResult("obj1", "prop1");
        def cloned = instance.cloneObject();
        assertEquals(instance.id, cloned.id)
        assertEquals(instance.version, cloned.version)
        assertEquals(instance.prop1, cloned.prop1)
        assertEquals(instance.prop2, cloned.prop2)
        assertSame(instance.errors, cloned.errors)
    }

    public void testPropertyInterceptingWithRelations()
    {
        String domainClassName2 = "DomainClass2"
        loadedDomainClass = gcl.parseClass("""
            class ${domainClassName}{
                Object ${RapidCMDBConstants.OPERATION_PROPERTY_NAME};
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
                Object ${RapidCMDBConstants.OPERATION_PROPERTY_NAME};
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


        def domainClass2Instance1 = loadedDomainClass2.metaClass.invokeStaticMethod(loadedDomainClass2, "add", [[prop1: "obj1Prop1Value"]] as Object[]);
        def domainClass1Instance1 = loadedDomainClass.metaClass.invokeStaticMethod(loadedDomainClass, "add", [[prop1: "prop1Value", rel1: domainClass2Instance1]] as Object[]);
        assertEquals("prop1Value", domainClass1Instance1.prop1);
        assertEquals(domainClass2Instance1.id, domainClass1Instance1.rel1[0].id);
        assertEquals(domainClass1Instance1.id, domainClass2Instance1.revRel1[0].id);
        assertNotSame(domainClass1Instance1.rel1, domainClass1Instance1.rel1);

        //When we need to hold relation data temporarily such as in ControllerUtils we will set
        //relation data to object and this data will be given to requester directly
        def realPropValue = ["nonemptyulist"];
        domainClass1Instance1.setProperty("rel1", realPropValue, false);
        assertSame(realPropValue, domainClass1Instance1.getRealPropertyValue("rel1"));
    }

    public void testInitializesDefaultPropertyManager()
    {
        def prop1DefaultValue = "defaultValue"
        loadedDomainClass = gcl.parseClass("""
            class ${domainClassName}{
                Object ${RapidCMDBConstants.OPERATION_PROPERTY_NAME};
                static searchable = {
                    except:[]
                }
                Long id;
                Long version;
                String prop1 = "${prop1DefaultValue}";
                static relations = [:]
            }
        """)
        def classesTobeLoaded = [loadedDomainClass, relation.Relation, application.ObjectId];
        configParams[RapidCMDBConstants.PROPERTY_INTERCEPTOR_CLASS_CONFIG_NAME] = DomainPropertyInterceptorDomainClassGrailsPluginImpl.name;
        def pluginsToLoad = [DomainClassGrailsPlugin, gcl.loadClass("SearchableGrailsPlugin"), gcl.loadClass("SearchableExtensionGrailsPlugin"), gcl.loadClass("RapidDomainClassGrailsPlugin")];
        initialize(classesTobeLoaded, pluginsToLoad)

        assertEquals(prop1DefaultValue, DomainClassDefaultPropertyValueHolder.getDefaultPropery(domainClassName, "prop1"));

        destroy();
        gcl = new GroovyClassLoader(this.class.classLoader);
        def domainClassName2 = "DomainClass2"
        prop1DefaultValue = "defaultValue"
        loadedDomainClass = gcl.parseClass("""
            class ${domainClassName2}{
                Object ${RapidCMDBConstants.OPERATION_PROPERTY_NAME};
                static searchable = {
                    except:[]
                }
                Long id;
                Long version;
                String prop1 = "${prop1DefaultValue}";
                static relations = [:]
            }
        """)
        classesTobeLoaded = [loadedDomainClass, relation.Relation, application.ObjectId];
        configParams[RapidCMDBConstants.PROPERTY_INTERCEPTOR_CLASS_CONFIG_NAME] = DomainPropertyInterceptorDomainClassGrailsPluginImpl.name;
        pluginsToLoad = [DomainClassGrailsPlugin, gcl.loadClass("SearchableGrailsPlugin"), gcl.loadClass("SearchableExtensionGrailsPlugin"), gcl.loadClass("RapidDomainClassGrailsPlugin")];
        initialize(classesTobeLoaded, pluginsToLoad)

        assertEquals(prop1DefaultValue, DomainClassDefaultPropertyValueHolder.getDefaultPropery(domainClassName2, "prop1"));
        try
        {
            DomainClassDefaultPropertyValueHolder.getDefaultPropery(domainClassName, "prop1");
            fail("Should throw exception");
        } catch (Exception e)
        {
            assertEquals(DomainClassDefaultPropertyValueHolder.DOMAIN_NOT_FOUND_EXCEPTION_MESSAGE, e.getMessage());
        }

    }

    public void testPropertyInterceptingThrowsMissingPropertyExceptionInvokesGetterAndSetterMethods()
    {

        def baseDir = "../testOutput"
        FileUtils.deleteDirectory(new File(baseDir));
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
        def operationFile = new File(baseDir + "/operations/${domainClassName}${DomainOperationManager.OPERATION_SUFFIX}.groovy");
        operationFile.parentFile.mkdirs();
        operationFile.setText("""
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
        def interceptor = appCtx.getBean("domainPropertyInterceptor");
        assertEquals(undefinedPropValue, instance["undefinedProperty"]);
        instance["undefinedProperty"] = "updatedPropValue";
        assertEquals("updatedPropValue", instance["undefinedProperty"]);

        try
        {
            instance["undefinedProperty2"]
            fail("Should throw exception since property not defined in operation and in domain class");
        }
        catch (MissingPropertyException ex)
        {
            assertEquals("undefinedProperty2", ex.getProperty());
        }

        try
        {
            instance["undefinedProperty2"] = ""
            fail("Should throw exception since property not defined in operation and in domain class");
        }
        catch (MissingPropertyException ex)
        {
            assertEquals("undefinedProperty2", ex.getProperty());
        }

        try
        {
            instance["undefinedProperty3"]
            fail("Should throw exception real exception since property found but an exception occurreed in it");
        }
        catch (Exception ex)
        {
            assertEquals(undefinedProp3ExceptionMessage, ex.getMessage());
        }

        try
        {
            instance["undefinedProperty3"] = 5;
            fail("Should throw exception real exception since property found but an exception occurreed in it");
        }
        catch (Exception ex)
        {
            assertEquals(undefinedProp3ExceptionMessage, ex.getMessage());
        }

        try
        {
            instance["undefinedProperty4"]
            fail("Should throw exception real missingmethodexception exception since thios exception throwed from another method");
        }
        catch (MissingMethodException ex)
        {
            assertEquals("anotherMethod", ex.getMethod());
        }

        try
        {
            instance["undefinedProperty4"] = 5;
            fail("Should throw exception real missingmethodexception exception since thios exception throwed from another method");
        }
        catch (MissingMethodException ex)
        {
            assertEquals("anotherMethod", ex.getMethod());
        }

        try
        {
            instance["undefinedProperty5"]
            fail("Should throw exception real missingmethodexception exception since thios exception throwed from another method");
        }
        catch (MissingMethodException ex)
        {
            assertEquals("getUndefinedProperty5", ex.getMethod());
        }

        try
        {
            instance["undefinedProperty5"] = 5;
            fail("Should throw exception real missingmethodexception exception since thios exception throwed from another method");
        }
        catch (MissingMethodException ex)
        {
            assertEquals("setUndefinedProperty5", ex.getMethod());
        }
    }

    public void testSaveInstanceForEachSetproperty()
    {
        loadedDomainClass = gcl.parseClass("""
            class ${domainClassName}{
                Object ${RapidCMDBConstants.OPERATION_PROPERTY_NAME};
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

        def instance = loadedDomainClass.add(prop1: "prop1Value", version: 4)
        instance.prop1 = "updatedValue"
        def reloadedInstance = loadedDomainClass.get(id: instance.id)
        assertEquals("updatedValue", reloadedInstance.prop1);

        //check excluded properties will not be persisted
        def newIdAndVersion = 15555555;
        reloadedInstance.id = newIdAndVersion;
        reloadedInstance.version = newIdAndVersion;
        assertEquals(1, loadedDomainClass.list().size());
        assertNull(loadedDomainClass.get(id: newIdAndVersion));

        reloadedInstance = loadedDomainClass.get(id: instance.id)
        assertEquals(instance.id, reloadedInstance.id);
        assertEquals(instance.version, reloadedInstance.version);


        def numberOfCalls = 0;
        instance.metaClass.update = {Map props ->
            numberOfCalls++;
        }
        instance.prop2 = "transientPropvalueShouldNotUpdate"
        assertEquals(0, numberOfCalls);

        //test setPropertyWithoutUpdate
        instance.setPropertyWithoutUpdate("prop1", "prop1SetWithoutUpdate");
        assertEquals(0, numberOfCalls);
    }

    public void testThrowsExceptioNifOperationPropertyIsNotDefinedInModel()
    {
        loadedDomainClass = gcl.parseClass("""
            class ${domainClassName}{
                static searchable = {
                    except:[]
                }
                Long id;
                Long version;
                static relations = [:]
            }
        """)
        def classesTobeLoaded = [loadedDomainClass, relation.Relation, application.ObjectId];
        configParams[RapidCMDBConstants.PROPERTY_INTERCEPTOR_CLASS_CONFIG_NAME] = DomainPropertyInterceptorDomainClassGrailsPluginImpl.name;
        def pluginsToLoad = [DomainClassGrailsPlugin, gcl.loadClass("SearchableGrailsPlugin"), gcl.loadClass("SearchableExtensionGrailsPlugin"), gcl.loadClass("RapidDomainClassGrailsPlugin")];
        try{
            initialize(classesTobeLoaded, pluginsToLoad)
            fail("Should throw exception since operation property is not defined in model");
        }catch(com.ifountain.rcmdb.domain.operation.DomainOperationLoadException exception)
        {
            assertEquals (DomainOperationLoadException.operationPropertyIsNotDefined(domainClassName).getMessage(), exception.getMessage());
        }
    }
    public void testOperation()
    {

        String baseDir = "../testoutput";
        FileUtils.deleteDirectory(new File(baseDir));
        System.setProperty("base.dir", baseDir);
        loadedDomainClass = gcl.parseClass("""
            class ${domainClassName}{
                def ${RapidCMDBConstants.OPERATION_PROPERTY_NAME};
                static searchable = {
                    except = ["prop2", "${RapidCMDBConstants.OPERATION_PROPERTY_NAME}"]
                }
                Long id;
                Long version;
                String prop1;
                String prop2;
                static transients = ["prop2", "${RapidCMDBConstants.OPERATION_PROPERTY_NAME}"]
            }
        """)
        def operationFile = new File("${System.getProperty("base.dir")}/operations/${domainClassName}Operations.groovy");
        operationFile.parentFile.mkdirs();
        operationFile.setText("""
            class ${domainClassName}Operations extends ${AbstractDomainOperation.class.name}{
                def method1(arg1, arg2)
                {
                    return arg1+arg2;
                }

                def static method2(arg1, arg2, arg3)
                {
                    return arg1+arg2+arg3;
                }
            }
        """)
        def classesTobeLoaded = [loadedDomainClass, ObjectId];
        def pluginsToLoad = [DomainClassGrailsPlugin, gcl.loadClass("SearchableGrailsPlugin"), gcl.loadClass("SearchableExtensionGrailsPlugin"), gcl.loadClass("RapidDomainClassGrailsPlugin")];
        initialize(classesTobeLoaded, pluginsToLoad)

        def instance = loadedDomainClass.add(prop1: "prop1Value");

        //test calling a non static operation
        def args = ["arg1", "arg2"]
        def res = instance.method1(args[0], args[1]);
        assertEquals(args.join(""), res);
        //test calling a static operation
        args = ["arg1", "arg2", "arg3"]
        res = loadedDomainClass.method2(args[0], args[1], args[2]);
        assertEquals(args.join(""), res);

        //We will reload operation and see operations
        operationFile.setText("""
            class ${domainClassName}Operations extends ${AbstractDomainOperation.class.name}{
                def method3(arg1, arg2)
                {
                    return arg1+arg2;
                }

                def static method4(arg1, arg2, arg3)
                {
                    return arg1+arg2+arg3;
                }
            }
        """)
        loadedDomainClass.reloadOperations();

        //We have to reload instance from repository to get new operation since old instance is cached in instance 
        instance = loadedDomainClass.get(id: instance.id);
        try
        {
            instance.method1(args[0], args[1]);
            fail("Should throw exception");
        }
        catch (groovy.lang.MissingMethodException e)
        {
            assertEquals("method1", e.getMethod());
            assertEquals(loadedDomainClass, e.getType());
        }

        args = ["arg1", "arg2"]
        res = instance.method3(args[0], args[1]);
        assertEquals(args.join(""), res);
        args = ["arg1", "arg2", "arg3"]
        res = loadedDomainClass.method4(args[0], args[1], args[2]);
        assertEquals(args.join(""), res);
    }

    public void testGetPropertiesListMethods()
    {
        def model1Name = "Model1";
        def model2Name = "Model2";
        def datasource = [name:"ds1", keys:[[propertyName:"prop1"]]]
        def prop1 = [name: "prop1", type: ModelGenerator.STRING_TYPE];
        def prop2 = [name: "prop2", type: ModelGenerator.DATE_TYPE];
        def prop3 = [name: "prop3", type: ModelGenerator.NUMBER_TYPE, datasource:datasource.name];
        def rel1 = [name:"rel1",  reverseName:"revrel1", toModel:model2Name, cardinality:ModelGenerator.RELATION_TYPE_MANY, reverseCardinality:ModelGenerator.RELATION_TYPE_MANY, isOwner:true];
        def revRel1 = [name:"revrel1",  reverseName:"rel1", toModel:model1Name, cardinality:ModelGenerator.RELATION_TYPE_MANY, reverseCardinality:ModelGenerator.RELATION_TYPE_MANY, isOwner:false];
        def model1MetaProps = [name: model1Name]
        def model2MetaProps = [name: model2Name]

        def modelProps = [prop1, prop2, prop3];
        def keyPropList = [prop1];


        def model1Text = ModelGenerationTestUtils.getModelText(model1MetaProps, [datasource], modelProps, keyPropList, [rel1]);
        def model2Text = ModelGenerationTestUtils.getModelText(model2MetaProps, [datasource], modelProps, keyPropList, [revRel1]);

        String baseDir = "../testoutput";
        FileUtils.deleteDirectory(new File(baseDir));
        System.setProperty("base.dir", baseDir);
        gcl.parseClass(model1Text+model2Text)
        def model1Class = gcl.loadClass(model1Name)
        def model2Class = gcl.loadClass(model2Name)
        def operationFile = new File("${System.getProperty("base.dir")}/operations/${model1Class.name}Operations.groovy");
        operationFile.parentFile.mkdirs();
        operationFile.setText("""
            class ${model1Class.name}Operations extends ${AbstractDomainOperation.class.name}{
                def declaredProp1;
            }
        """)
        def classesTobeLoaded = [model1Class, model2Class, ObjectId];
        def pluginsToLoad = [DomainClassGrailsPlugin, gcl.loadClass("SearchableGrailsPlugin"), gcl.loadClass("SearchableExtensionGrailsPlugin"), gcl.loadClass("RapidDomainClassGrailsPlugin")];
        initialize(classesTobeLoaded, pluginsToLoad)

        def allProperties = model1Class.getPropertiesList();
        assertEquals (6, allProperties.size());
        def expectedProps = ["declaredProp1", "id", "prop1", "prop2", "prop3", "rel1"]
        for(int i=0; i < allProperties.size();i++){
            assertEquals (expectedProps[i], allProperties[i].name);            
        }

        def federatedProperties = model1Class.getFederatedPropertiesList();
        assertEquals (1, federatedProperties.size());
        expectedProps = [ "prop3"]
        for(int i=0; i < federatedProperties.size();i++){
            assertEquals (expectedProps[i], federatedProperties[i].name);
        }

        def relations = model1Class.getRelationPropertiesList();
        assertEquals (1, relations.size());
        expectedProps = [ "rel1"]
        for(int i=0; i < relations.size();i++){
            assertEquals (expectedProps[i], relations[i].name);
        }


        def nonFederatedProperties = model1Class.getNonFederatedPropertiesList();
        assertEquals (3, nonFederatedProperties.size());
        expectedProps = ["id", "prop1", "prop2"]
        for(int i=0; i < nonFederatedProperties.size();i++){
            assertEquals (expectedProps[i], nonFederatedProperties[i].name);
        }
    }

}

class DomainPropertyInterceptorDomainClassGrailsPluginImpl extends DefaultDomainClassPropertyInterceptor
{
    def setPropertyList = []
    def getPropertyList = []
    public void setDomainClassProperty(Object domainObject, String propertyName, Object value) {
        super.setDomainClassProperty(domainObject, propertyName, value); //To change body of overridden methods use File | Settings | File Templates.
        setPropertyList += propertyName
    }

    public Object getDomainClassProperty(Object domainObject, String propertyName) {
        getPropertyList += propertyName
        return super.getDomainClassProperty(domainObject, propertyName); //To change body of overridden methods use File | Settings | File Templates.
    }

}