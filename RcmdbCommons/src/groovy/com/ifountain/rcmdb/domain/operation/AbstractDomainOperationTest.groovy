package com.ifountain.rcmdb.domain.operation

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.util.DataStore
import org.apache.log4j.Logger
import com.ifountain.rcmdb.util.ExecutionContextManagerUtils
import com.ifountain.rcmdb.execution.ExecutionContextManager
import auth.RsUser

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

    public void testGetLogger()
    {
        ExecutionContextManager.destroy();
        AbstractDomainOperationTestImpl domainOpr = new AbstractDomainOperationTestImpl();
        assertEquals("If there is no execution context should return logger belonging to operation", Logger.getLogger(AbstractDomainOperation.name), AbstractDomainOperationTestImpl.getLogger());
        assertEquals("We should be able to access logger as property", Logger.getLogger(AbstractDomainOperation.name), domainOpr.logger);
        ExecutionContextManagerUtils.executeInContext ([:])
        {
            def logger = AbstractDomainOperationTestImpl.getLogger();
            assertEquals ("If there is no logger in execution context should return logger belonging to operation", Logger.getLogger(AbstractDomainOperation.name), logger);
            logger = AbstractDomainOperationTestImpl.logger
            assertEquals ("If there is no logger in execution context should return logger belonging to operation", Logger.getLogger(AbstractDomainOperation.name), logger);
            logger = domainOpr.logger
            assertEquals ("If there is no logger in execution context should return logger belonging to operation", Logger.getLogger(AbstractDomainOperation.name), logger);
        }


        Logger loggerToBeAddedtoContext = Logger.getLogger("logger1");
        ExecutionContextManagerUtils.executeInContext ([:])
        {
            ExecutionContextManagerUtils.addLoggerToCurrentContext (loggerToBeAddedtoContext)
            assertEquals(loggerToBeAddedtoContext, AbstractDomainOperationTestImpl.getLogger());
            assertEquals(loggerToBeAddedtoContext, AbstractDomainOperationTestImpl.logger);
            assertEquals(loggerToBeAddedtoContext, domainOpr.logger);
        }
    }
    public void testGetCurrentUserName()
    {
        ExecutionContextManager.destroy();
        AbstractDomainOperationTestImpl domainOpr = new AbstractDomainOperationTestImpl();
        assertEquals("If there is no execution context should return RsUser.RSADMIN", RsUser.RSADMIN, AbstractDomainOperationTestImpl.getCurrentUserName());
        assertEquals("We should be able to access currentUserName as property", RsUser.RSADMIN, domainOpr.currentUserName);
        ExecutionContextManagerUtils.executeInContext ([:])
        {
            def currentUserName = AbstractDomainOperationTestImpl.getCurrentUserName();
            assertEquals ("If there is no logger in execution context should return RsUser.RSADMIN", RsUser.RSADMIN, currentUserName);
            currentUserName = AbstractDomainOperationTestImpl.currentUserName
            assertEquals ("If there is no logger in execution context should return RsUser.RSADMIN", RsUser.RSADMIN, currentUserName);
            currentUserName = domainOpr.currentUserName
            assertEquals ("If there is no logger in execution context should return RsUser.RSADMIN", RsUser.RSADMIN, currentUserName);
        }


        String currentUserNameToBeAddedtoContext = "testuser";
        ExecutionContextManagerUtils.executeInContext ([:])
        {
            ExecutionContextManagerUtils.addUsernameToCurrentContext (currentUserNameToBeAddedtoContext);
            assertEquals(currentUserNameToBeAddedtoContext, AbstractDomainOperationTestImpl.getCurrentUserName());
            assertEquals(currentUserNameToBeAddedtoContext, AbstractDomainOperationTestImpl.currentUserName);
            assertEquals(currentUserNameToBeAddedtoContext, domainOpr.currentUserName);
        }
    }
}


class AbstractDomainOperationTestImpl extends AbstractDomainOperation
{
    
}