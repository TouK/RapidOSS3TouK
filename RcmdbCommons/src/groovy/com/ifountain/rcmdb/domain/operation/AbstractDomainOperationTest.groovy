package com.ifountain.rcmdb.domain.operation

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.util.DataStore

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
        
        AbstractDomainOperationTestImpl domainOpr = new AbstractDomainOperationTestImpl();
        domainOpr.domainObject = domainInstance;
        assertSame(domainInstance, domainOpr.getProperty ("domainObject"));
        assertEquals (AbstractDomainOperationTestImpl.class.name, domainOpr.getClass().name);
        assertEquals (AbstractDomainOperationTestImpl.class.name, domainOpr.getMetaClass().theClass.name);
        assertEquals (domainInstance.prop1, domainOpr.getProperty("prop1"));

        //test set property
        def updatedProp1Val = "prop1UpdatedValue";
        domainOpr.setProperty ("prop1", updatedProp1Val);
        assertEquals (updatedProp1Val, domainOpr.getProperty("prop1"));
        assertEquals (updatedProp1Val, domainInstance.getProperty("prop1"));


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

        AbstractDomainOperationTestImpl domainOpr = new AbstractDomainOperationTestImpl();
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
}


class AbstractDomainOperationTestImpl extends AbstractDomainOperation
{
    
}