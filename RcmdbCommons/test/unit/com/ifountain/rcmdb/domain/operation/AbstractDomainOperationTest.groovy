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

    public void testAdd()
    {
        def gcl = new GroovyClassLoader();
        def domainClassStr = """
        class DomainClass1{
            String prop1;
            def static _add(Map props)
            {
                ${DataStore.name}.put("_addProps",props);
                return ${DataStore.name}.get("objectToBeReturned");
            }
        }
        """
        def domainClass = gcl.parseClass (domainClassStr);

        def domainInstance = domainClass.newInstance();

        def propMap = [prop1:"prop1Value"];
        def objectToBeReturned = new Object();
        DataStore.put ("objectToBeReturned", objectToBeReturned)
        def returnedObject = AbstractDomainOperation.add(domainClass, propMap);
        assertSame (propMap, DataStore.get("_addProps"));
        assertEquals (propMap, DataStore.get("_addProps"));
        assertSame(objectToBeReturned, returnedObject);

    }
    public void testAddUnique()
    {
        def gcl = new GroovyClassLoader();
        def domainClassStr = """
        class DomainClass1{
            String prop1;
            def static _addUnique(Map props)
            {
                ${DataStore.name}.put("_addUniqueProps",props);
                return ${DataStore.name}.get("objectToBeReturned");
            }
        }
        """
        def domainClass = gcl.parseClass (domainClassStr);

        def domainInstance = domainClass.newInstance();

        def propMap = [prop1:"prop1Value"];
        def objectToBeReturned = new Object();
        DataStore.put ("objectToBeReturned", objectToBeReturned)
        def returnedObject = AbstractDomainOperation.addUnique(domainClass, propMap);
        assertSame (propMap, DataStore.get("_addUniqueProps"));
        assertEquals (propMap, DataStore.get("_addUniqueProps"));
        assertSame(objectToBeReturned, returnedObject);

    }

    
    public void testRemoveAll()
    {
        def gcl = new GroovyClassLoader();
        def domainClassStr = """
        class DomainClass1{
            String prop1;
            def static _removeAll(String query)
            {
                ${DataStore.name}.put("_removeAllQuery",query);
                return ${DataStore.name}.get("objectToBeReturned");
            }
        }
        """
        def domainClass = gcl.parseClass (domainClassStr);

        def domainInstance = domainClass.newInstance();

        def propMap = [prop1:"prop1Value"];
        def objectToBeReturned = new Object();
        DataStore.put ("objectToBeReturned", objectToBeReturned)
        def returnedObject = AbstractDomainOperation.removeAll(domainClass);
        assertEquals ("alias:*", DataStore.get("_removeAllQuery"));
        assertSame(objectToBeReturned, returnedObject);

    }
    public void testRemoveAllWithQuery()
    {
        def gcl = new GroovyClassLoader();
        def domainClassStr = """
        class DomainClass1{
            String prop1;
            def static _removeAll(String query)
            {
                ${DataStore.name}.put("_removeAllQuery",query);
                return ${DataStore.name}.get("objectToBeReturned");
            }
        }
        """
        def domainClass = gcl.parseClass (domainClassStr);

        def domainInstance = domainClass.newInstance();
        def query = "query1"
        def propMap = [prop1:"prop1Value"];
        def objectToBeReturned = new Object();
        DataStore.put ("objectToBeReturned", objectToBeReturned)
        def returnedObject = AbstractDomainOperation.removeAll(domainClass, query);
        assertEquals(query, DataStore.get("_removeAllQuery"));
        assertSame(objectToBeReturned, returnedObject);

    }
    public void testUpdate()
    {
        def gcl = new GroovyClassLoader();
        def domainClassStr = """
        class DomainClass1{
            String prop1;
            def  _update(Map props)
            {
                ${DataStore.name}.put("_updateProps",props);
                return ${DataStore.name}.get("objectToBeReturned");
            }
        }
        """
        def domainClass = gcl.parseClass (domainClassStr);

        def domainInstance = domainClass.newInstance();

        AbstractDomainOperation domainOpr = new AbstractDomainOperation();
        domainOpr.domainObject = domainInstance;
        def propMap = [prop1:"prop1Value"];
        def objectToBeReturned = new Object();
        DataStore.put ("objectToBeReturned", objectToBeReturned)
        def returnedObject = domainOpr.update(propMap);
        assertSame (propMap, DataStore.get("_updateProps"));
        assertEquals (propMap, DataStore.get("_updateProps"));
        assertSame(objectToBeReturned, returnedObject);
    }
    public void testRemove()
    {
        def gcl = new GroovyClassLoader();
        def domainClassStr = """
        class DomainClass1{
            String prop1;
            def  _remove()
            {
                ${DataStore.name}.put("is_removeCalled",true);
                return ${DataStore.name}.get("objectToBeReturned");
            }
        }
        """
        def domainClass = gcl.parseClass (domainClassStr);

        def domainInstance = domainClass.newInstance();

        AbstractDomainOperation domainOpr = new AbstractDomainOperation();
        domainOpr.domainObject = domainInstance;
        def objectToBeReturned = new Object();
        DataStore.put ("objectToBeReturned", objectToBeReturned)
        def returnedObject = domainOpr.remove();
        assertSame (true, DataStore.get("is_removeCalled"));
        assertEquals (true, DataStore.get("is_removeCalled"));
        assertSame(objectToBeReturned, returnedObject);
    }
    public void testAddRelation()
    {
        def gcl = new GroovyClassLoader();
        def domainClassStr = """
        class DomainClass1{
            String prop1;
            def  _addRelation(Map relations, String source)
            {
                ${DataStore.name}.put("_addRelationProps",relations);
                ${DataStore.name}.put("_addRelationSource",source);
                return ${DataStore.name}.get("objectToBeReturned");
            }
        }
        """
        def domainClass = gcl.parseClass (domainClassStr);

        def domainInstance = domainClass.newInstance();

        AbstractDomainOperation domainOpr = new AbstractDomainOperation();
        domainOpr.domainObject = domainInstance;
        def relsToBeRemoved = [rel1:new Object()]
        def objectToBeReturned = new Object();
        DataStore.put ("objectToBeReturned", objectToBeReturned)
        def returnedObject = domainOpr.addRelation(relsToBeRemoved);
        assertSame (relsToBeRemoved, DataStore.get("_addRelationProps"));
        assertEquals (relsToBeRemoved, DataStore.get("_addRelationProps"));
        assertEquals (null, DataStore.get("_addRelationSource"));
        assertSame(objectToBeReturned, returnedObject);
    }
    public void testAddRelationWithSource()
    {
        def gcl = new GroovyClassLoader();
        def domainClassStr = """
        class DomainClass1{
            String prop1;
            def  _addRelation(Map relations, String source)
            {
                ${DataStore.name}.put("_addRelationProps",relations);
                ${DataStore.name}.put("_addRelationSource",source);
                return ${DataStore.name}.get("objectToBeReturned");
            }
        }
        """
        def domainClass = gcl.parseClass (domainClassStr);

        def domainInstance = domainClass.newInstance();

        AbstractDomainOperation domainOpr = new AbstractDomainOperation();
        domainOpr.domainObject = domainInstance;
        def relsToBeRemoved = [rel1:new Object()]
        def objectToBeReturned = new Object();
        def source = "src1";
        DataStore.put ("objectToBeReturned", objectToBeReturned)
        def returnedObject = domainOpr.addRelation(relsToBeRemoved, source);
        assertSame (relsToBeRemoved, DataStore.get("_addRelationProps"));
        assertEquals (relsToBeRemoved, DataStore.get("_addRelationProps"));
        assertEquals (source, DataStore.get("_addRelationSource"));
        assertSame(objectToBeReturned, returnedObject);
    }
    
    public void testRemoveRelation()
    {
        def gcl = new GroovyClassLoader();
        def domainClassStr = """
        class DomainClass1{
            String prop1;
            def  _removeRelation(Map relations, String source)
            {
                ${DataStore.name}.put("_removeRelationProps",relations);
                ${DataStore.name}.put("_removeRelationSource",source);
                return ${DataStore.name}.get("objectToBeReturned");
            }
        }
        """
        def domainClass = gcl.parseClass (domainClassStr);

        def domainInstance = domainClass.newInstance();

        AbstractDomainOperation domainOpr = new AbstractDomainOperation();
        domainOpr.domainObject = domainInstance;
        def relsToBeRemoved = [rel1:new Object()]
        def objectToBeReturned = new Object();
        DataStore.put ("objectToBeReturned", objectToBeReturned)
        def returnedObject = domainOpr.removeRelation(relsToBeRemoved);
        assertSame (relsToBeRemoved, DataStore.get("_removeRelationProps"));
        assertEquals (relsToBeRemoved, DataStore.get("_removeRelationProps"));
        assertEquals (null, DataStore.get("_removeRelationSource"));
        assertSame(objectToBeReturned, returnedObject);
    }
    public void testRemoveRelationWithSource()
    {
        def gcl = new GroovyClassLoader();
        def domainClassStr = """
        class DomainClass1{
            String prop1;
            def  _removeRelation(Map relations, String source)
            {
                ${DataStore.name}.put("_removeRelationProps",relations);
                ${DataStore.name}.put("_removeRelationSource",source);
                return ${DataStore.name}.get("objectToBeReturned");
            }
        }
        """
        def domainClass = gcl.parseClass (domainClassStr);

        def domainInstance = domainClass.newInstance();

        AbstractDomainOperation domainOpr = new AbstractDomainOperation();
        domainOpr.domainObject = domainInstance;
        def relsToBeRemoved = [rel1:new Object()]
        def objectToBeReturned = new Object();
        def source = "src1";
        DataStore.put ("objectToBeReturned", objectToBeReturned)
        def returnedObject = domainOpr.removeRelation(relsToBeRemoved, source);
        assertSame (relsToBeRemoved, DataStore.get("_removeRelationProps"));
        assertEquals (relsToBeRemoved, DataStore.get("_removeRelationProps"));
        assertEquals (source, DataStore.get("_removeRelationSource"));
        assertSame(objectToBeReturned, returnedObject);
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
