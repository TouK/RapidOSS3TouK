package com.ifountain.rcmdb.domain.operation

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.util.DataStore
import org.apache.log4j.Logger
import com.ifountain.rcmdb.util.ExecutionContextManagerUtils
import com.ifountain.rcmdb.execution.ExecutionContextManager


/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 23, 2009
* Time: 6:29:38 PM
* To change this template use File | Settings | File Templates.
*/
class AbstractDomainOperationTest extends RapidCmdbTestCase
{

    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        DataStore.clear();
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        DataStore.clear();
    }


    public void testGetProperties()
    {
        def gcl = new GroovyClassLoader();
        def domainClassStr = """
        class DomainClass1{
            String prop1;
        }
        """
        def domainClass = gcl.parseClass (domainClassStr);

        def domainInstance = domainClass.newInstance();
        
        AbstractDomainOperation domainOpr = new AbstractDomainOperation();
        domainOpr.domainObject = domainInstance;
        assertSame(domainInstance, domainOpr.getProperty ("domainObject"));
        assertEquals (AbstractDomainOperation.class.name, domainOpr.getClass().name);
        assertEquals (AbstractDomainOperation.class.name, domainOpr.getMetaClass().theClass.name);
        assertEquals (domainInstance.prop1, domainOpr.getProperty("prop1"));

        //test set property
        def updatedProp1Val = "prop1UpdatedValue";
        domainOpr.setProperty ("prop1", updatedProp1Val);
        assertEquals (updatedProp1Val, domainOpr.getProperty("prop1"));
        assertEquals (updatedProp1Val, domainInstance.getProperty("prop1"));


    }

    public void testInvokeCompassOperation()
    {
        def gcl = new GroovyClassLoader();
        def domainClassStr = """
        class DomainClass1{
            String prop1;
            def _update(Map props)
            {
                ${DataStore.class.name}.put("updateCall", [props]);
                return "updateRes"
            }
        }
        """
        def domainClass = gcl.parseClass (domainClassStr);

        def domainInstance = domainClass.newInstance();

        AbstractDomainOperation domainOpr = new AbstractDomainOperation();
        domainOpr.domainObject = domainInstance;

        def props = [myProp:"myPropValue"]
        def res = domainOpr.invokeCompassOperation ("update", [props]);
        assertEquals ("updateRes", res);
        assertEquals (1, DataStore.get("updateCall").size());
        assertEquals (props, DataStore.get("updateCall")[0]);


        try{
            domainOpr.invokeCompassOperation ("invalid", [props]);
            fail("should throw exception invalid method does not exist");
        }catch(MissingMethodException e)
        {
            assertEquals ("_invalid", e.getMethod());
        }
    }

    public void testThrowsExceptionIfCompassOperationIsInvokedWhileBeforeContinue()
    {
        def gcl = new GroovyClassLoader();
        def domainClassStr = """
        class DomainClass1{
            String prop1;
            def _update(Map props)
            {
                ${DataStore.class.name}.put("updateCall", [props]);
                return "updateRes"
            }
        }
        """
        def domainClass = gcl.parseClass (domainClassStr);

        def domainInstance = domainClass.newInstance();

        AbstractDomainOperation domainOpr = new AbstractDomainOperation();
        domainOpr.domainObject = domainInstance;

        def methodName = "update";
        try{
            domainOpr.invokeBeforeEventTriggerOperation {
                domainOpr.invokeCompassOperation (methodName, [[:]]);
            }
            fail("Should throw exception since we cannot execute update method while before operation continue");
        }catch(RuntimeException e)
        {
            assertEquals ("${methodName} cannot be executed in before triggers", e.getMessage());
        }
    }

    public void testSetPropertiesCallsNonPersistantSetPropertyIfFlagIsSet()
    {
        def gcl = new GroovyClassLoader();
        def domainClassStr = """
        class DomainClass1{
            String prop1;
            public void setProperty(String propName, String propValue, boolean willPersist)
            {
                ${DataStore.class.name}.put("setProperty", [propName, propValue, willPersist]);
            }
        }
        """
        def domainClass = gcl.parseClass (domainClassStr);

        def domainInstance = domainClass.newInstance();

        AbstractDomainOperation domainOpr = new AbstractDomainOperation();
        domainOpr.domainObject = domainInstance;
        assertTrue(domainOpr.rsSetPropertyWillUpdate instanceof ThreadLocal);
        assertTrue(domainOpr.rsSetPropertyWillUpdate.get());
        assertFalse (domainOpr.rsIsBeforeTriggerContinue);

        def updatedProp1Val = "prop1UpdatedValue";
        domainOpr.invokeBeforeEventTriggerOperation{
            assertFalse (domainOpr.rsSetPropertyWillUpdate.get());
            assertTrue (domainOpr.rsIsBeforeTriggerContinue);
            domainOpr.setProperty ("prop1", updatedProp1Val);
        }
        assertTrue(domainOpr.rsSetPropertyWillUpdate.get());
        assertFalse (domainOpr.rsIsBeforeTriggerContinue);
        List params = DataStore.get("setProperty");
        assertEquals ("prop1", params[0]);
        assertEquals (updatedProp1Val, params[1]);
        assertEquals (false, params[2]);
    }

    public void testInvokeBeforeEventTriggerOperationWillRestoreFlagsIfExceptionIsThrown()
    {
        def gcl = new GroovyClassLoader();
        def domainClassStr = """
        class DomainClass1{
            String prop1;
            public void setProperty(String propName, String propValue, boolean willPersist)
            {
                ${DataStore.class.name}.put("setProperty", [propName, propValue, willPersist]);
            }
        }
        """
        def domainClass = gcl.parseClass (domainClassStr);

        def domainInstance = domainClass.newInstance();

        AbstractDomainOperation domainOpr = new AbstractDomainOperation();
        domainOpr.domainObject = domainInstance;
        assertTrue(domainOpr.rsSetPropertyWillUpdate instanceof ThreadLocal);
        assertTrue(domainOpr.rsSetPropertyWillUpdate.get());
        assertFalse (domainOpr.rsIsBeforeTriggerContinue);
        def exception = new Exception("exception");
        try{
            domainOpr.invokeBeforeEventTriggerOperation{
                assertFalse(domainOpr.rsSetPropertyWillUpdate.get());
                assertTrue(domainOpr.rsIsBeforeTriggerContinue);
                throw exception;
            }
            fail("Should throw exception");
        }
        catch(Exception e)
        {
            assertSame (exception, e);
        }
        assertTrue(domainOpr.rsSetPropertyWillUpdate.get());
        assertFalse (domainOpr.rsIsBeforeTriggerContinue);
    }

    public void testBeforeEventWrappersWillCallNonPersistantSetProperty()
    {
        def gcl = new GroovyClassLoader();
        def domainClassStr = """
        class DomainClass1{
            String prop1;
            public void setProperty(String propName, String propValue, boolean willPersist)
            {
                ${DataStore.class.name}.put("setProperty", [propName, propValue, willPersist]);
            }
        }
        """

        def newOprClassStr = """
            class NewOpr extends ${AbstractDomainOperation.class.name}
            {
                def onLoad()
                {
                    assert rsIsBeforeTriggerContinue == false
                    assert rsSetPropertyWillUpdate.get() == true
                    ${DataStore.class.name}.get("methodCalls").add("onLoad")
                }
                def beforeDelete()
                {
                    assert rsIsBeforeTriggerContinue == true
                    assert rsSetPropertyWillUpdate.get() == false
                    ${DataStore.class.name}.get("methodCalls").add("beforeDelete")
                }
                def beforeUpdate(Map props)
                {
                    assert rsIsBeforeTriggerContinue == true
                    assert rsSetPropertyWillUpdate.get() == false
                    ${DataStore.class.name}.get("methodCalls").add("beforeUpdate")
                }
                def beforeInsert()
                {
                    assert rsIsBeforeTriggerContinue == true
                    assert rsSetPropertyWillUpdate.get() == false
                    ${DataStore.class.name}.get("methodCalls").add("beforeInsert")
                }
                def afterDelete()
                {
                    assert rsIsBeforeTriggerContinue == false
                    assert rsSetPropertyWillUpdate.get() == true
                    ${DataStore.class.name}.get("methodCalls").add("afterDelete")
                }
                def afterUpdate(Map props)
                {
                    assert rsIsBeforeTriggerContinue == false
                    assert rsSetPropertyWillUpdate.get() == true
                    ${DataStore.class.name}.get("methodCalls").add("afterUpdate")
                }
                def afterInsert()
                {
                    assert rsIsBeforeTriggerContinue == false
                    assert rsSetPropertyWillUpdate.get() == true
                    ${DataStore.class.name}.get("methodCalls").add("afterInsert")
                }
            }
        """

        def newOprClass = gcl.parseClass(newOprClassStr);
        def domainClass = gcl.parseClass (domainClassStr);

        DataStore.put("methodCalls", [])
        def domainInstance = domainClass.newInstance();

        AbstractDomainOperation domainOpr = newOprClass.newInstance();
        domainOpr.domainObject = domainInstance;
        domainOpr.onLoadWrapper();
        assertEquals(1, DataStore.get("methodCalls").size());
        assertEquals("onLoad", DataStore.get("methodCalls")[0]);

        DataStore.put("methodCalls", [])

        domainOpr.beforeDeleteWrapper();
        assertEquals(1, DataStore.get("methodCalls").size());
        assertEquals("beforeDelete", DataStore.get("methodCalls")[0]);

        DataStore.put("methodCalls", [])

        domainOpr.beforeUpdateWrapper([:]);
        assertEquals(1, DataStore.get("methodCalls").size());
        assertEquals("beforeUpdate", DataStore.get("methodCalls")[0]);

        DataStore.put("methodCalls", [])

        domainOpr.beforeInsertWrapper();
        assertEquals(1, DataStore.get("methodCalls").size());
        assertEquals("beforeInsert", DataStore.get("methodCalls")[0]);

        DataStore.put("methodCalls", [])

        domainOpr.afterDeleteWrapper();
        assertEquals(1, DataStore.get("methodCalls").size());
        assertEquals("afterDelete", DataStore.get("methodCalls")[0]);

        DataStore.put("methodCalls", [])

        domainOpr.afterUpdateWrapper([:]);
        assertEquals(1, DataStore.get("methodCalls").size());
        assertEquals("afterUpdate", DataStore.get("methodCalls")[0]);

        DataStore.put("methodCalls", [])

        domainOpr.afterInsertWrapper();
        assertEquals(1, DataStore.get("methodCalls").size());
        assertEquals("afterInsert", DataStore.get("methodCalls")[0]);
    }

    public void testBeforeUpdateWrapperWillReturnUpdatedPropsList()
    {
        def gcl = new GroovyClassLoader();
        def domainClassStr = """
        class DomainClass1{
            String prop1;
            public void setProperty(String propName, String propValue, boolean willPersist)
            {
                ${DataStore.class.name}.put("setProperty", [propName, propValue, willPersist]);
            }
        }
        """
        DataStore.put("methodCalls", [])
        def newOprClassStr = """
            class NewOpr extends ${AbstractDomainOperation.class.name}
            {
                def beforeUpdate(Map props)
                {
                    ${DataStore.class.name}.get("methodCalls").add("beforeUpdate")
                    prop1 = "prop1UpdatedValue1"
                    prop1 = "prop1UpdatedValue2"
                }
                def afterUpdate(Map props)
                {
                    ${DataStore.class.name}.get("methodCalls").add("afterUpdate");
                }
            }
        """

        def newOprClass = gcl.parseClass(newOprClassStr);
        def domainClass = gcl.parseClass (domainClassStr);

        def prop1ValueBeforeUpdate = "prop1Value";
        def domainInstance = domainClass.newInstance();
        domainInstance.prop1 = prop1ValueBeforeUpdate;
        
        AbstractDomainOperation domainOpr = newOprClass.newInstance();
        domainOpr.domainObject = domainInstance;

        Map updatedProps = domainOpr.beforeUpdateWrapper([:]);

        assertEquals (1, updatedProps.size());
        assertTrue(updatedProps.containsKey("prop1"));
        assertEquals(prop1ValueBeforeUpdate, updatedProps.get("prop1"));
    }

    public void testBeforeUpdateWrapperWillNotReturnNameOfNotUpdatedPropsEvenIfSetPropertyIsCalled()
    {
        def gcl = new GroovyClassLoader();
        def domainClassStr = """
        class DomainClass1{
            String prop1;
            public void setProperty(String propName, String propValue, boolean willPersist)
            {
                ${DataStore.class.name}.put("setProperty", [propName, propValue, willPersist]);
            }
        }
        """
        def prop1Value = "prop1Value"
        DataStore.put("methodCalls", [])
        def newOprClassStr = """
            class NewOpr extends ${AbstractDomainOperation.class.name}
            {
                def beforeUpdate(Map props)
                {
                    ${DataStore.class.name}.get("methodCalls").add("beforeUpdate")
                    prop1 = "${prop1Value}"
                }
                def afterUpdate(Map props)
                {
                    ${DataStore.class.name}.get("methodCalls").add("afterUpdate");
                }
            }
        """

        def newOprClass = gcl.parseClass(newOprClassStr);
        def domainClass = gcl.parseClass (domainClassStr);

        def domainInstance = domainClass.newInstance();
        domainInstance.prop1 = prop1Value;

        AbstractDomainOperation domainOpr = newOprClass.newInstance();
        domainOpr.domainObject = domainInstance;

        Map updatedProps = domainOpr.beforeUpdateWrapper([:]);

        assertEquals ("Since prop1 value didnot changed updated props will not contain property name", 0, updatedProps.size());
    }

    public void testMethodMissing()
    {
        def gcl = new GroovyClassLoader();
        def domainClassStr = """
        class DomainClass1{
            def method1(String prop1, Object prop2)
            {
                ${DataStore.class.name}.put("params", [prop1:prop1, prop2:prop2])
                return ${DataStore.class.name}.get("return");
            }
        }
        """
        def domainClass = gcl.parseClass (domainClassStr);

        def domainInstance = domainClass.newInstance();

        AbstractDomainOperation domainOpr = new AbstractDomainOperation();
        domainOpr.domainObject = domainInstance;

        def objectToBeReturned = new Object();
        DataStore.put("return", objectToBeReturned);
        String param1 = "param1";
        Object param2 = new Object();
        def returnedObject = domainOpr.method1(param1, param2);
        assertEquals (param1, DataStore.get("params").prop1);
        assertSame(param2, DataStore.get("params").prop2);
        assertSame(objectToBeReturned, returnedObject);

        //test with a subclass parameter    
        param2 = "param2";
        returnedObject = domainOpr.method1(param1, param2);
        assertEquals (param1, DataStore.get("params").prop1);
        assertEquals(param2, DataStore.get("params").prop2);
        assertSame(objectToBeReturned, returnedObject);

        //test with null parameters
        param2 = "param2";
        objectToBeReturned = null;
        DataStore.put("return", objectToBeReturned);
        returnedObject = domainOpr.method1(null, null);
        assertEquals (null, DataStore.get("params").prop1);
        assertEquals(null, DataStore.get("params").prop2);
        assertEquals(objectToBeReturned, returnedObject);
    }

    public void testGetLogger()
    {
        ExecutionContextManager.destroy();
        AbstractDomainOperation domainOpr = new AbstractDomainOperation();
        assertEquals("If there is no execution context should return logger belonging to operation", Logger.getLogger(AbstractDomainOperation.name), AbstractDomainOperation.getLogger());
        assertEquals("We should be able to access logger as property", Logger.getLogger(AbstractDomainOperation.name), domainOpr.logger);
        ExecutionContextManagerUtils.executeInContext ([:])
        {
            def logger = AbstractDomainOperation.getLogger();
            assertEquals ("If there is no logger in execution context should return logger belonging to operation", Logger.getLogger(AbstractDomainOperation.name), logger);
            logger = AbstractDomainOperation.logger
            assertEquals ("If there is no logger in execution context should return logger belonging to operation", Logger.getLogger(AbstractDomainOperation.name), logger);
            logger = domainOpr.logger
            assertEquals ("If there is no logger in execution context should return logger belonging to operation", Logger.getLogger(AbstractDomainOperation.name), logger);
        }


        Logger loggerToBeAddedtoContext = Logger.getLogger("logger1");
        ExecutionContextManagerUtils.executeInContext ([:])
        {
            ExecutionContextManagerUtils.addLoggerToCurrentContext (loggerToBeAddedtoContext)
            assertEquals(loggerToBeAddedtoContext, AbstractDomainOperation.getLogger());
            assertEquals(loggerToBeAddedtoContext, AbstractDomainOperation.logger);
            assertEquals(loggerToBeAddedtoContext, domainOpr.logger);
        }
    }
    
}
