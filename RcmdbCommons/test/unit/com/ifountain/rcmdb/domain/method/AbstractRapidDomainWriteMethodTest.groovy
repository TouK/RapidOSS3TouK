package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import com.ifountain.rcmdb.domain.DomainLockManager
import org.apache.log4j.Logger
import com.ifountain.comp.utils.LoggerUtils
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.comp.test.util.logging.TestLogUtils
import com.ifountain.rcmdb.domain.DomainMethodExecutor
import com.ifountain.rcmdb.test.util.ClosureWaitAction
import junit.framework.TestSuite
import junit.framework.Assert
import org.apache.commons.transaction.locking.LockException
import com.ifountain.session.SessionManager
import com.ifountain.session.Session
import com.ifountain.compass.search.FilterManager

/**
 * Created by IntelliJ IDEA.
 * User: mustafa seker
 * Date: 06.Ara.2008
 * Time: 17:03:26
 * To change this template use File | Settings | File Templates.
 */

public class AbstractRapidDomainWriteMethodTest extends RapidCmdbTestCase
{
    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        DomainMethodExecutor.setMaxNumberOfRetries (1);
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }
    public void testWriteMethodReturnsBackToTheirPreviousStateEvenIfExceptionIsThrown()
    {
        Class modelClass = createModels()[0];
        AbstractRapidDomainWriteMethodImpl impl = new AbstractRapidDomainWriteMethodImpl(modelClass.metaClass);
        def isClosureExecuted = false;
        impl.lockKeyClosure = {domainObject, args->
            return "key"
        }
        Exception expectedException = new Exception("this is a exception")
        impl.closureToBeInvoked = {domainObject, arguments->
            isClosureExecuted = true;
            Session session = SessionManager.getInstance().getSession();
            assertFalse(FilterManager.isFiltersEnabled());
            throw expectedException;
        }
        try{
            impl.invoke (modelClass, null);
        }catch(Exception e)
        {
            assertSame (expectedException, e)
        }
        assertTrue (isClosureExecuted);
        assertTrue(FilterManager.isFiltersEnabled());
    }
    public void testWriteMethodDisablesSearchFilters()
    {
        Class modelClass = createModels()[0];
        AbstractRapidDomainWriteMethodImpl impl = new AbstractRapidDomainWriteMethodImpl(modelClass.metaClass);
        def isClosureExecuted = false;
        impl.lockKeyClosure = {domainObject, args->
            return "key"            
        }
        impl.closureToBeInvoked = {domainObject, arguments->
            isClosureExecuted = true;
            Session session = SessionManager.getInstance().getSession();
            assertFalse(FilterManager.isFiltersEnabled());
        }
        impl.invoke (modelClass, null);
        assertTrue (isClosureExecuted);
        assertTrue(FilterManager.isFiltersEnabled());

        //after finishing its job write methdo should return filter state to previous state
        isClosureExecuted = false;
        FilterManager.setFiltersEnabled(false);
        impl.closureToBeInvoked = {domainObject, arguments->
            isClosureExecuted = true;
            Session session = SessionManager.getInstance().getSession();
            assertFalse(FilterManager.isFiltersEnabled());
        }
        impl.invoke (modelClass, null);
        assertTrue (isClosureExecuted);
        assertFalse(FilterManager.isFiltersEnabled());

    }
    public void testSynchronization()
    {
        _testSynchronization(AbstractRapidDomainWriteMethodImpl);
    }


    public void testThrowsExceptionReleasesLockIfExceptionOccursInActions()
    {
        _testThrowsExceptionReleasesLockIfExceptionOccursInActions(AbstractRapidDomainWriteMethodImpl);
    }



    public void testSynchronizationWillNotBeAppliedToMethodsReturningLockKeyAsNull()
    {
        _testSynchronizationWillNotBeAppliedToMethodsReturningLockKeyAsNull(AbstractRapidDomainWriteMethodImpl);
    }


    public void testSynchronizationWithAThreadRequestingSameLock()
    {
        _testSynchronizationWithAThreadRequestingSameLock(AbstractRapidDomainWriteMethodImpl);
    }


    public void testTimeoutMechanismWithoutDeadLock()
    {
        _testTimeoutMechanismWithoutDeadLock(AbstractRapidDomainWriteMethodImpl);
    }


    public void testDeadLockDetection()
    {
        DomainMethodExecutor.setMaxNumberOfRetries (10);
        _testDeadLockDetection(AbstractRapidDomainWriteMethodImpl);
    }

    public void testWriteWillSuccessfullyProcessTwoSubsequentRequests(){
        Class modelClass = createModels()[0];
        def impl1 = AbstractRapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);
        def instance1 = modelClass.newInstance();
        def instance2 = modelClass.newInstance();
        instance1["keyProp"] = "keyvalue1"
        instance2["keyProp"] = "keyvalue2"
        int thread1State = 0;
        def t1 = Thread.start {
            thread1State = 1;
            impl1.invoke(instance1, null)
            thread1State = 2;
        }
        CommonTestUtils.waitFor (new ClosureWaitAction(){
            assertEquals(2, thread1State);
        });

        int thread2State = 0;
        def t2 = Thread.start {
            thread2State = 1;
            impl1.invoke(instance2, null)
            thread2State = 2;
        }

        CommonTestUtils.waitFor (new ClosureWaitAction(){
            assertEquals(2, thread2State);
        });
    }
    public void testWriteBelongsToDifferentObjectsWillNotBeBlocked(){
        Object waitLock = new Object();
        Class modelClass = createModels()[0];
        AbstractRapidDomainWriteMethodImpl impl1 = AbstractRapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);
        impl1.closureToBeInvoked ={domainObject, arguments->
            synchronized (waitLock)
            {
                waitLock.wait();
            }
        }
        def impl2 = AbstractRapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);
        def instance1 = modelClass.newInstance();
        def instance2 = modelClass.newInstance();
        instance1["keyProp"] = "keyvalue1"
        instance2["keyProp"] = "keyvalue2"
        int thread1State = 0;
        def t1 = Thread.start {
            thread1State = 1;
            impl1.invoke(instance1, null)
            thread1State = 2;
        }
        CommonTestUtils.waitFor (new ClosureWaitAction(){
            assertEquals(1, thread1State);
        });

        int thread2State = 0;
        def t2 = Thread.start {
            thread2State = 1;
            impl2.invoke(instance2, null)
            thread2State = 2;
        }

        CommonTestUtils.waitFor (new ClosureWaitAction(){
            assertEquals(2, thread2State);
        });
        synchronized (waitLock)
        {
            waitLock.notifyAll();
        }
        CommonTestUtils.waitFor (new ClosureWaitAction(){
            assertEquals(2, thread1State);
        });

        t1.join ();
        t2.join ();
    }

    public void testThrowsDeadLockDetectionIfItExceededMaxNumberOfRetries()
    {
        DomainLockManager.initialize(2000, TestLogUtils.log);
        DomainMethodExecutor.setMaxNumberOfRetries (1);
        Object waitLock1 = new Object();
        Object waitLock2 = new Object();
        Class modelClass = createModels()[0];
        def instance1Request1 = AbstractRapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);
        def instance1Request2 = AbstractRapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);
        def instance2Request1 = AbstractRapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);
        def instance2Request2 = AbstractRapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);

        boolean willBlock1 = true;
        boolean willBlock2 = true;
        def instance1 = modelClass.newInstance();
        def instance1Clone = modelClass.newInstance();
        def instance2 = modelClass.newInstance();
        def instance2Clone = modelClass.newInstance();
        instance1["keyProp"] = "keyvalue1"
        instance1Clone["keyProp"] = "keyvalue1"
        instance2["keyProp"] = "keyvalue2"
        instance2Clone["keyProp"] = "keyvalue2"
        int numberOfExecutionOfInvoke = 0;
        instance1Request1.closureToBeInvoked ={domainObject, arguments->
            println "REQUEST1 EXECUTING"
            numberOfExecutionOfInvoke++;
            if(willBlock1)
            {
                synchronized (waitLock1)
                {
                    waitLock1.wait();
                }
            }
            println "REQUEST1 EXECUTING REQUEST2"
            instance2Request2.invoke(instance2Clone, null)
        }
        instance2Request1.closureToBeInvoked ={domainObject, arguments->
            println "REQUEST2 EXECUTING"
            if(willBlock2)
            {
                synchronized (waitLock2)
                {
                    waitLock2.wait();
                }
            }
            println "REQUEST2 EXECUTING REQUEST1"
            instance1Request2.invoke(instance1Clone, null)
        }



        def thread1State = 0;
        def t1 = Thread.start {
            thread1State = 1;
            try
            {
                instance1Request1.invoke(instance1, null)
                thread1State = 2;
            }
            catch(Exception e)
            {
                thread1State = 3;
            }
        }
        int thread2State = 0;
        def t2 = Thread.start {
            thread2State = 1;
            try
            {
                instance2Request1.invoke(instance2, null)
                thread2State = 2;
            }
            catch(org.apache.commons.transaction.locking.LockException ex)
            {
                thread2State = 3;
            }
        }
        Thread.sleep(300);
        assertEquals(1, thread1State);
        assertEquals(1, thread2State);
        synchronized (waitLock1)
        {
            waitLock1.notifyAll();
        }
        willBlock1 = false;
        Thread.sleep(100);

        synchronized (waitLock2)
        {
            waitLock2.notifyAll();
        }
        Thread.sleep(500);
        assertTrue(thread1State == 2 && thread2State == 3 || thread1State == 3 && thread2State == 2 );
        t1.join ();
        t2.join ();
    }

    public void testGetLockName()
    {
        def modelName = "ParentModel";
        def keyProp = [name:"keyProp", type:ModelGenerator.STRING_TYPE, blank:false];

        def modelMetaProps = [name:modelName]
        def modelProps = [keyProp];
        def keyPropList = [keyProp];
        String modelString = ModelGenerationTestUtils.getModelText(modelMetaProps, modelProps, keyPropList, [])
        GroovyClassLoader gcl = new GroovyClassLoader();
        gcl.parseClass(modelString);
        Class modelClass = gcl.loadClass(modelName);

        modelClass.metaClass.keySet = {
            return [new RapidDomainClassProperty(name:"keyProp")];
        }
        DummyAbstractRapidDomainMethodImpl impl = new DummyAbstractRapidDomainMethodImpl(modelClass.metaClass);
        def instance1 = modelClass.newInstance();
        instance1["keyProp"] = "keyProp1";
        String lockName = impl.getLockName (instance1)
        assertEquals(modelClass.name+"keyProp1", lockName);

        def keyProp2 = [name:"keyProp2", type:ModelGenerator.STRING_TYPE, blank:false];
        keyPropList.add(keyProp2);
        modelProps.add(keyProp2)
        String childModelName = "ChildModel"
        Map childModelMetaProps = [name:childModelName, parentModel:modelName]
        modelString = ModelGenerationTestUtils.getModelText(modelMetaProps, modelProps, keyPropList, [])
        String childModelString = ModelGenerationTestUtils.getModelText(childModelMetaProps, [], [], [])
        gcl = new GroovyClassLoader();
        gcl.parseClass(modelString+childModelString);
        modelClass = gcl.loadClass(modelName);
        Class childModelClass = gcl.loadClass(childModelName);
        childModelClass.metaClass.keySet = {
            return [new RapidDomainClassProperty(name:"keyProp"), new RapidDomainClassProperty(name:"keyProp2")];
        }

        impl = new DummyAbstractRapidDomainMethodImpl(childModelClass.metaClass);
        def childInstance1 = childModelClass.newInstance();
        childInstance1["keyProp"] = "keyProp1";
        childInstance1["keyProp2"] = "keyProp2";
        String childLockName = impl.getLockName (childInstance1);
        assertEquals(modelClass.name+"keyProp1"+"keyProp2", childLockName);

        childModelClass.metaClass.keySet = {
            return [];
        }

        childInstance1 = childModelClass.newInstance();
        childInstance1["id"] = 1000;
        childInstance1["keyProp"] = "keyProp1";
        childInstance1["keyProp2"] = "keyProp2";
        childLockName = impl.getLockName (childInstance1);
        assertEquals(modelClass.name+"1000", childLockName);
    }


    public static void _testSynchronization(Class rapidDomainWriteMethodImpl)
    {
        Object waitLock = new Object();
        Class modelClass = createModels()[0];
        def impl1 = rapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);
        impl1.closureToBeInvoked ={domainObject, arguments->
            synchronized (waitLock)
            {
                waitLock.wait();
            }
        }
        def impl2 = rapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);

        def instance1 = modelClass.newInstance();
        def instance2 = modelClass.newInstance();
        instance1["keyProp"] = "keyvalue1"
        instance2["keyProp"] = "keyvalue1"
        int thread1State = 0;
        def t1 = Thread.start {
            thread1State = 1;
            impl1.invoke(instance1, null)
            thread1State = 2;
        }
        Thread.sleep(300);
        assertEquals(1, thread1State)

        int thread2State = 0;
        def t2 = Thread.start {
            thread2State = 1;
            impl2.invoke(instance2, null)
            thread2State = 2;
        }

        Thread.sleep(400);
        assertEquals(1, thread2State);
        synchronized (waitLock)
        {
            waitLock.notifyAll();
        }
        Thread.sleep(400);
        assertEquals(2, thread2State);
        assertEquals(2, thread1State);
        t1.join ();
        t2.join ();
    }
    public static void _testThrowsExceptionReleasesLockIfExceptionOccursInActions(Class rapidDomainWriteMethodImpl)
    {
        Object waitLock = new Object();
        Class modelClass = createModels()[0];
        def impl1 = rapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);
        impl1.closureToBeInvoked ={domainObject, arguments->
            synchronized (waitLock)
            {
                waitLock.wait();
            }
            throw new RuntimeException("An exception");
        }
        def impl2 = rapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);

        def instance1 = modelClass.newInstance();
        def instance2 = modelClass.newInstance();
        instance1["keyProp"] = "keyvalue1"
        instance2["keyProp"] = "keyvalue1"
        int thread1State = 0;
        def t1 = Thread.start {
            thread1State = 1;
            try
            {
                impl1.invoke(instance1, null)
                thread1State = 2;
            }
            catch(Exception e)
            {
                thread1State = 3;
            }
        }
        Thread.sleep(300);
        assertEquals(1, thread1State)

        int thread2State = 0;
        def t2 = Thread.start {
            thread2State = 1;
            impl2.invoke(instance2, null)
            thread2State = 2;
        }

        Thread.sleep(400);
        assertEquals(1, thread2State);
        synchronized (waitLock)
        {
            waitLock.notifyAll();
        }
        Thread.sleep(400);
        assertEquals(2, thread2State);
        assertEquals(3, thread1State);
        t1.join ();
        t2.join ();
    }
    public static void _testSynchronizationWillNotBeAppliedToMethodsReturningLockKeyAsNull(Class rapidDomainWriteMethodImpl)
    {
        Object waitLock = new Object();
        Class modelClass = createModels()[0];
        def impl1 = rapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);
        impl1.closureToBeInvoked ={domainObject, arguments->
            synchronized (waitLock)
            {
                waitLock.wait();
            }
        }
        def impl2 = rapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);
        impl2.lockKeyClosure = {object, args->
            return null;
        }

        def instance1 = modelClass.newInstance();
        def instance2 = modelClass.newInstance();
        instance1["keyProp"] = "keyvalue1"
        instance2["keyProp"] = "keyvalue1"
        int thread1State = 0;
        def t1 = Thread.start {
            thread1State = 1;
            impl1.invoke(instance1, null)
            thread1State = 2;
        }
        Thread.sleep(300);
        assertEquals(1, thread1State)

        int thread2State = 0;
        def t2 = Thread.start {
            thread2State = 1;
            impl2.invoke(instance2, null)
            thread2State = 2;
        }

        Thread.sleep(400);
        assertEquals(2, thread2State);
        synchronized (waitLock)
        {
            waitLock.notifyAll();
        }
        t1.join ();
        t2.join ();
    }
    public static void _testSynchronizationWithAThreadRequestingSameLock(Class rapidDomainWriteMethodImpl)
    {
        Object waitLock = new Object();
        Class modelClass = createModels()[0];
        def impl1 = rapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);

        def impl2 = rapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);

        def subRequest = rapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);
        impl1.closureToBeInvoked ={domainObject, arguments->
            subRequest.invoke(domainObject, arguments); //lock should not be released before this method returned
            synchronized (waitLock)
            {
                waitLock.wait();
            }
        }

        def instance1 = modelClass.newInstance();
        def instance2 = modelClass.newInstance();
        instance1["keyProp"] = "keyvalue1"
        instance2["keyProp"] = "keyvalue1"
        int thread1State = 0;
        def t1 = Thread.start {
            thread1State = 1;
            impl1.invoke(instance1, null)
            thread1State = 2;
        }
        Thread.sleep(300);
        assertEquals(1, thread1State)

        int thread2State = 0;
        def t2 = Thread.start {
            thread2State = 1;
            impl2.invoke(instance2, null)
            thread2State = 2;
        }

        Thread.sleep(400);
        assertEquals(1, thread1State);
        assertEquals(1, thread2State);

        synchronized (waitLock)
        {
            waitLock.notifyAll();
        }
        Thread.sleep(400);

        assertEquals(2, thread1State);
        assertEquals(2, thread2State);
        t1.join ();
        t2.join ();
    }
    public static void _testTimeoutMechanismWithoutDeadLock(Class rapidDomainWriteMethodImpl)
    {
        DomainLockManager.initialize(2000, Logger.getRootLogger());

        Object waitLock = new Object();
        Class modelClass = createModels()[0];
        def impl1 = rapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);
        impl1.closureToBeInvoked ={domainObject, arguments->
            synchronized (waitLock)
            {
                waitLock.wait();
            }
        }

        def impl2 = rapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);


        def instance1 = modelClass.newInstance();
        def instance2 = modelClass.newInstance();
        instance1["keyProp"] = "keyvalue1"
        instance2["keyProp"] = "keyvalue1"
        int thread1State = 0;
        def t1 = Thread.start {
            thread1State = 1;
            impl1.invoke(instance1, null)
            thread1State = 2;
        }
        Thread.sleep(300);
        assertEquals(1, thread1State)

        int thread2State = 0;
        def t2 = Thread.start {
            thread2State = 1;
            try
            {
                impl2.invoke(instance2, null)
                thread2State = 2;
            }catch(org.apache.commons.transaction.locking.LockException ex)
            {
                thread2State = 3;
            }

        }

        Thread.sleep(3000);
        assertEquals(1, thread1State);
        assertEquals(3, thread2State);
        synchronized (waitLock)
        {
            waitLock.notifyAll();
        }
        t1.join ();
        t2.join ();
    }
    public static void _testDeadLockDetection(Class rapidDomainWriteMethodImpl)
    {
        DomainLockManager.initialize(1000000, TestLogUtils.log);
        Object waitLock1 = new Object();
        Object waitLock2 = new Object();
        Class modelClass = createModels()[0];
        def instance1Request1 = rapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);
        def instance1Request2 = rapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);
        def instance2Request1 = rapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);
        def instance2Request2 = rapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);

        boolean willBlock1 = true;
        boolean willBlock2 = true;
        def instance1 = modelClass.newInstance();
        def instance1Clone = modelClass.newInstance();
        def instance2 = modelClass.newInstance();
        def instance2Clone = modelClass.newInstance();
        instance1["keyProp"] = "keyvalue1"
        instance1Clone["keyProp"] = "keyvalue1"
        instance2["keyProp"] = "keyvalue2"
        instance2Clone["keyProp"] = "keyvalue2"
        int numberOfExecutionOfInvokeOfInstance1 = 0;
        instance1Request1.closureToBeInvoked ={domainObject, arguments->
            println "REQUEST1 EXECUTING"
            numberOfExecutionOfInvokeOfInstance1++;
            if(willBlock1)
            {
                synchronized (waitLock1)
                {
                    waitLock1.wait();
                    willBlock1 = false;
                }
            }
            println "REQUEST1 EXECUTING REQUEST2"
            instance2Request2.invoke(instance2Clone, null)
            println "REQUEST1 EXECUTED REQUEST2"
            println "REQUEST1 EXECUTED"
        }
        int numberOfExecutionOfInvokeOfInstance2 = 0;
        instance2Request1.closureToBeInvoked ={domainObject, arguments->
            println "REQUEST2 EXECUTING"
            numberOfExecutionOfInvokeOfInstance2++;
            if(willBlock2)
            {
                synchronized (waitLock2)
                {
                    waitLock2.wait();
                    waitLock2 = false;
                }
            }
            println "REQUEST2 EXECUTING REQUEST1"
            instance1Request2.invoke(instance1Clone, null)
            println "REQUEST2 EXECUTED REQUEST1"
            println "REQUEST2 EXECUTED"
        }



        def thread1State = 0;
        def t1 = Thread.start {
            thread1State = 1;
            try
            {
                instance1Request1.invoke(instance1, null)
                println "finished t1"
                thread1State = 2;
            }
            catch(Exception e)
            {

                println "t1 ${((LockException)e).getCode()} ${((LockException)e).getReason()}"
                thread1State = 3;
            }
        }
        CommonTestUtils.waitFor (new ClosureWaitAction(){
            Assert.assertEquals(1, numberOfExecutionOfInvokeOfInstance1);
        }, 100);
        int thread2State = 0;
        def t2 = Thread.start {
            thread2State = 1;
            try
            {
                instance2Request1.invoke(instance2, null)
                thread2State = 2;
                println "finished t2"
            }
            catch(org.apache.commons.transaction.locking.LockException ex)
            {
                println "t2 ${ex.getCode()} ${ex.getReason()}"
                thread2State = 3;
            }
        }
        CommonTestUtils.waitFor (new ClosureWaitAction(){
            Assert.assertEquals(1, numberOfExecutionOfInvokeOfInstance2);
        }, 100);
        Thread.sleep(300);
        assertEquals(1, thread1State);
        assertEquals(1, thread2State);
        synchronized (waitLock1)
        {
            waitLock1.notifyAll();
        }

        Thread.sleep(300);

        assertEquals(1, thread1State);
        assertEquals(1, thread2State);
        synchronized (waitLock2)
        {
            waitLock2.notifyAll();
        }

        CommonTestUtils.waitFor (new ClosureWaitAction(){
            Assert.assertEquals(2, thread1State);
            Assert.assertTrue("""One of the threads should be selecetd a deadlock victim and should
                be executed twice but thread1 execution times:${numberOfExecutionOfInvokeOfInstance1}
                thread2 execution times:${numberOfExecutionOfInvokeOfInstance2}
        """, (numberOfExecutionOfInvokeOfInstance1 == 2 && numberOfExecutionOfInvokeOfInstance2 == 1) || (numberOfExecutionOfInvokeOfInstance1 == 1 && numberOfExecutionOfInvokeOfInstance2 == 2) );
            Assert.assertEquals(2, thread2State);
        }, 100);

    }

    public static List createModels()
    {
        def modelName = "ParentModel";
        def keyProp = [name:"keyProp", type:ModelGenerator.STRING_TYPE, blank:false];

        def modelMetaProps = [name:modelName]
        def modelProps = [keyProp];
        def keyPropList = [keyProp];
        String modelString = ModelGenerationTestUtils.getModelText(modelMetaProps, modelProps, keyPropList, [])
        GroovyClassLoader gcl = new GroovyClassLoader();
        gcl.parseClass(modelString);
        Class modelClass = gcl.loadClass(modelName);
        return [modelClass];
    }
}
class DummyAbstractRapidDomainMethodImpl extends AbstractRapidDomainWriteMethod
{

    public DummyAbstractRapidDomainMethodImpl(MetaClass mc) {
        super(mc); //To change body of overridden methods use File | Settings | File Templates.
    }

    protected Object _invoke(Object domainObject, Object[] arguments) {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }
}

class AbstractRapidDomainWriteMethodImpl extends AbstractRapidDomainWriteMethod
{
    def closureToBeInvoked;
    def lockKeyClosure= {domainObject, args->
        return domainObject["keyProp"];
    }
    def AbstractRapidDomainWriteMethodImpl(mc) {
        super(mc);
    }

    public String getLockName(Object domainObject, Object[] arguments)
    {
        return lockKeyClosure(domainObject, arguments)
    }
    protected Object _invoke(Object domainObject, Object[] arguments) {
        def res = null;
        if(closureToBeInvoked)
        res = closureToBeInvoked(domainObject, arguments);
        return res;
    }

}


