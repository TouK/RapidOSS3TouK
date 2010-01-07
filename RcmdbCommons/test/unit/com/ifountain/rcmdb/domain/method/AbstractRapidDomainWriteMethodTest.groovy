package com.ifountain.rcmdb.domain.method

import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.comp.test.util.logging.TestLogUtils
import com.ifountain.compass.search.FilterManager
import com.ifountain.rcmdb.domain.DomainLockManager
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.rcmdb.domain.lock.LockStrategyImpl
import com.ifountain.rcmdb.test.util.ClosureWaitAction
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.session.Session
import com.ifountain.session.SessionManager
import junit.framework.Assert
import org.apache.commons.transaction.locking.LockException
import org.apache.log4j.Logger

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
        DomainLockManager.getInstance().initialize (TestLogUtils.log);
        LockStrategyImpl.setMaxNumberOfRetries(1);

    }

    protected void tearDown() {
        DomainLockManager.destroy();
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }
    public void testWriteMethodReturnsBackToTheirPreviousStateEvenIfExceptionIsThrown()
    {
        Class modelClass = createModels()[0];
        AbstractRapidDomainWriteMethodImpl impl = new AbstractRapidDomainWriteMethodImpl(modelClass.metaClass);
        def isClosureExecuted = false;
        impl.lockKeyClosure = {domainObject, args ->
            return "key"
        }
        Exception expectedException = new Exception("this is a exception")
        impl.closureToBeInvoked = {domainObject, arguments ->
            isClosureExecuted = true;
            Session session = SessionManager.getInstance().getSession();
            assertFalse(FilterManager.isFiltersEnabled());
            throw expectedException;
        }
        try {
            impl.invoke(modelClass, null);
        } catch (Exception e)
        {
            assertSame(expectedException, e)
        }
        assertTrue(isClosureExecuted);
        assertTrue(FilterManager.isFiltersEnabled());
    }
    public void testWriteMethodDisablesSearchFilters()
    {
        Class modelClass = createModels()[0];
        AbstractRapidDomainWriteMethodImpl impl = new AbstractRapidDomainWriteMethodImpl(modelClass.metaClass);
        def isClosureExecuted = false;
        impl.lockKeyClosure = {domainObject, args ->
            return "key"
        }
        impl.closureToBeInvoked = {domainObject, arguments ->
            isClosureExecuted = true;
            Session session = SessionManager.getInstance().getSession();
            assertFalse(FilterManager.isFiltersEnabled());
            return [domainObject: domainObject]
        }
        impl.invoke(modelClass, null);
        assertTrue(isClosureExecuted);
        assertTrue(FilterManager.isFiltersEnabled());

        //after finishing its job write methdo should return filter state to previous state
        isClosureExecuted = false;
        FilterManager.setFiltersEnabled(false);
        impl.closureToBeInvoked = {domainObject, arguments ->
            isClosureExecuted = true;
            Session session = SessionManager.getInstance().getSession();
            assertFalse(FilterManager.isFiltersEnabled());
            return [domainObject: domainObject]
        }
        impl.invoke(modelClass, null);
        assertTrue(isClosureExecuted);
        assertFalse(FilterManager.isFiltersEnabled());

    }
    public void testDirectoryLockingWithBatchExecution() {
        Object waitLock = new Object();
        Class modelClass = createModels()[0];
        def impl1 = AbstractRapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);
        impl1.closureToBeInvoked = {domainObject, arguments ->
            synchronized (waitLock)
            {
                waitLock.wait();
            }
            return [domainObject: domainObject]
        }
        def instance1 = modelClass.newInstance();
        instance1["keyProp"] = "keyvalue1"

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
            DomainLockManager.getInstance().batchStarted();
            DomainLockManager.getInstance().lockDirectory(modelClass.name)
            DomainLockManager.getInstance().releaseDirectory(modelClass.name)
            DomainLockManager.getInstance().batchFinished();
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
        t1.join();
        t2.join();
    }

    public void testDirectoryLockIsShared() {
        Object waitLock = new Object();
        Class modelClass = createModels()[0];
        def impl1 = AbstractRapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);
        impl1.closureToBeInvoked = {domainObject, arguments ->
            synchronized (waitLock)
            {
                waitLock.wait();
            }
            return [domainObject: domainObject]
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
        Thread.sleep(300);
        assertEquals(1, thread1State)

        int thread2State = 0;
        def t2 = Thread.start {
            thread2State = 1;
            impl2.invoke(instance2, null)
            thread2State = 2;
        }

        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(2, thread2State);
        }))
        synchronized (waitLock)
        {
            waitLock.notifyAll();
        }
        Thread.sleep(400);
        assertEquals(2, thread2State);
        assertEquals(2, thread1State);
        t1.join();
        t2.join();
    }

    public void testDirectoryLockIsReleasedIfInvokationThrowsException() {
        Object waitLock = new Object();
        Class modelClass = createModels()[0];
        def impl1 = AbstractRapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);
        impl1.closureToBeInvoked = {domainObject, arguments ->
            synchronized (waitLock)
            {
                waitLock.wait();
            }
            throw new Exception("");
        }
        def instance1 = modelClass.newInstance();
        instance1["keyProp"] = "keyvalue1"

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
            DomainLockManager.getInstance().batchStarted();
            DomainLockManager.getInstance().lockDirectory(modelClass.name)
            DomainLockManager.getInstance().releaseDirectory(modelClass.name)
            DomainLockManager.getInstance().batchFinished();
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
        t1.join();
        t2.join();
    }

    public void testDirectoryLockReleasedIfInstanceLockingThrowsException() {
        DomainLockManager.getInstance().setInstanceBasedLockTimeout(3000);
        Object waitLock = new Object();
        Class modelClass = createModels()[0];
        def impl1 = AbstractRapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);
        def impl2 = AbstractRapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);
        impl1.closureToBeInvoked = {domainObject, arguments ->
            synchronized (waitLock)
            {
                waitLock.wait();
            }
        }
        def instance1 = modelClass.newInstance();
        instance1["keyProp"] = "keyvalue1"

        int thread1State = 0;
        def t1 = Thread.start {
            thread1State = 1;
            impl1.invoke(instance1, null)
            thread1State = 2;
        }
        Thread.sleep(1000);
        assertEquals(1, thread1State)

        int thread2State = 0;
        def t2 = Thread.start {
            thread2State = 1;
            impl2.invoke(instance1, null)
            thread2State = 2;
        }
        Thread.sleep(300);
        assertEquals(1, thread2State)

        int thread3State = 0;
        def t3 = Thread.start {
            thread3State = 1;
            DomainLockManager.getInstance().batchStarted();
            DomainLockManager.getInstance().lockDirectory(modelClass.name)
            DomainLockManager.getInstance().releaseDirectory(modelClass.name)
            DomainLockManager.getInstance().batchFinished();
            thread3State = 2;
        }

        Thread.sleep(300);
        assertEquals(1, thread3State);

        t2.join(10000);

        Thread.sleep(300);
        assertEquals(1, thread3State);

        synchronized (waitLock)
        {
            waitLock.notifyAll();
        }
        t1.join();
        assertEquals(2, thread1State);
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(2, thread3State);
        }))
        t3.join();
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
        LockStrategyImpl.setMaxNumberOfRetries(10);
        _testDeadLockDetection(AbstractRapidDomainWriteMethodImpl);
    }

    public void testWriteWillSuccessfullyProcessTwoSubsequentRequests() {
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
        CommonTestUtils.waitFor(new ClosureWaitAction() {
            assertEquals(2, thread1State);
        });

        int thread2State = 0;
        def t2 = Thread.start {
            thread2State = 1;
            impl1.invoke(instance2, null)
            thread2State = 2;
        }

        CommonTestUtils.waitFor(new ClosureWaitAction() {
            assertEquals(2, thread2State);
        });
    }
    public void testWriteBelongsToDifferentObjectsWillNotBeBlocked() {
        Object waitLock = new Object();
        Class modelClass = createModels()[0];
        AbstractRapidDomainWriteMethodImpl impl1 = AbstractRapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);
        impl1.closureToBeInvoked = {domainObject, arguments ->
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
        CommonTestUtils.waitFor(new ClosureWaitAction() {
            assertEquals(1, thread1State);
        });

        int thread2State = 0;
        def t2 = Thread.start {
            thread2State = 1;
            impl2.invoke(instance2, null)
            thread2State = 2;
        }

        CommonTestUtils.waitFor(new ClosureWaitAction() {
            assertEquals(2, thread2State);
        });
        synchronized (waitLock)
        {
            waitLock.notifyAll();
        }
        CommonTestUtils.waitFor(new ClosureWaitAction() {
            assertEquals(2, thread1State);
        });

        t1.join();
        t2.join();
    }

    public void testThrowsDeadLockDetectionIfItExceededMaxNumberOfRetries()
    {
        DomainLockManager.getInstance().initialize(TestLogUtils.log, 2000);
        LockStrategyImpl.setMaxNumberOfRetries(1);
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
        instance1Request1.closureToBeInvoked = {domainObject, arguments ->
            println "REQUEST1 EXECUTING"
            numberOfExecutionOfInvoke++;
            if (willBlock1)
            {
                synchronized (waitLock1)
                {
                    waitLock1.wait();
                }
            }
            println "REQUEST1 EXECUTING REQUEST2"
            instance2Request2.invoke(instance2Clone, null)
        }
        instance2Request1.closureToBeInvoked = {domainObject, arguments ->
            println "REQUEST2 EXECUTING"
            if (willBlock2)
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
            catch (Exception e)
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
            catch (org.apache.commons.transaction.locking.LockException ex)
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
        assertTrue(thread1State == 2 && thread2State == 3 || thread1State == 3 && thread2State == 2);
        t1.join();
        t2.join();
    }

    public void testGetLockName()
    {
        def modelName = "ParentModel";
        def keyProp = [name: "keyProp", type: ModelGenerator.STRING_TYPE, blank: false];

        def modelMetaProps = [name: modelName]
        def modelProps = [keyProp];
        def keyPropList = [keyProp];
        String modelString = ModelGenerationTestUtils.getModelText(modelMetaProps, modelProps, keyPropList, [])
        GroovyClassLoader gcl = new GroovyClassLoader();
        gcl.parseClass(modelString);
        Class modelClass = gcl.loadClass(modelName);

        modelClass.metaClass.keySet = {
            return [new RapidDomainClassProperty(name: "keyProp")];
        }
        DummyAbstractRapidDomainMethodImpl impl = new DummyAbstractRapidDomainMethodImpl(modelClass.metaClass);
        def instance1 = modelClass.newInstance();
        instance1["keyProp"] = "keyProp1";
        String lockName = impl.getLockName(instance1)
        assertEquals(modelClass.name + "keyProp1", lockName);

        def keyProp2 = [name: "keyProp2", type: ModelGenerator.STRING_TYPE, blank: false];
        keyPropList.add(keyProp2);
        modelProps.add(keyProp2)
        String childModelName = "ChildModel"
        Map childModelMetaProps = [name: childModelName, parentModel: modelName]
        modelString = ModelGenerationTestUtils.getModelText(modelMetaProps, modelProps, keyPropList, [])
        String childModelString = ModelGenerationTestUtils.getModelText(childModelMetaProps, [], [], [])
        gcl = new GroovyClassLoader();
        gcl.parseClass(modelString + childModelString);
        modelClass = gcl.loadClass(modelName);
        Class childModelClass = gcl.loadClass(childModelName);
        childModelClass.metaClass.keySet = {
            return [new RapidDomainClassProperty(name: "keyProp"), new RapidDomainClassProperty(name: "keyProp2")];
        }

        impl = new DummyAbstractRapidDomainMethodImpl(childModelClass.metaClass);
        def childInstance1 = childModelClass.newInstance();
        childInstance1["keyProp"] = "keyProp1";
        childInstance1["keyProp2"] = "keyProp2";
        String childLockName = impl.getLockName(childInstance1);
        assertEquals(modelClass.name + "keyProp1" + "keyProp2", childLockName);

        childModelClass.metaClass.keySet = {
            return [];
        }

        childInstance1 = childModelClass.newInstance();
        childInstance1["id"] = 1000;
        childInstance1["keyProp"] = "keyProp1";
        childInstance1["keyProp2"] = "keyProp2";
        childLockName = impl.getLockName(childInstance1);
        assertEquals(modelClass.name + "1000", childLockName);
    }

    public void testAfterTriggersAreOutsideOfSynchronizationBlock(){
        Object waitLock = new Object();
        Class modelClass = createModels()[0];
        def impl1 = AbstractRapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);
        def impl2 = AbstractRapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);
        def mockImpl = AbstractRapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);

        def instance1 = modelClass.newInstance();
        def instance2 = modelClass.newInstance();
        instance1["keyProp"] = "keyvalue1"
        instance2["keyProp"] = "keyvalue2"
        
        int thread1State = 0;
        int thread2State = 0;
        impl1.closureToBeInvoked = {domainObject, arguments ->
            thread1State = 2;
            synchronized (waitLock)
            {
                waitLock.wait();
            }
            return [domainObject: domainObject]
        }
        impl1.afterTriggersClosure = {triggersMap ->
            thread1State = 3;
            synchronized(waitLock){
                waitLock.wait();
            }
            mockImpl.invoke(instance2)
            return triggersMap?.domainObject
        }
        impl2.closureToBeInvoked = {domainObject, arguments ->
            thread2State = 2;
            mockImpl.invoke(instance1)
            return [domainObject: domainObject]
        }
        def t1 = Thread.start {
            thread1State = 1;
            impl1.invoke(instance1, null)
            thread1State = 4;
        }
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(2, thread1State);
        }))
        def t2 = Thread.start {
            thread2State = 1;
            impl2.invoke(instance2, null)
            thread2State = 3;
        }
         CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(2, thread2State);
        }))

        synchronized(waitLock){
            waitLock.notifyAll();
        }

        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(3, thread2State);
            assertEquals(3, thread1State);
        }))

        synchronized(waitLock){
            waitLock.notifyAll();
        }

        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(4, thread1State);
        }))
        t1.join();
        t2.join();
    }



    public static void _testSynchronization(Class rapidDomainWriteMethodImpl)
    {
        Object waitLock = new Object();
        Class modelClass = createModels()[0];
        def impl1 = rapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);
        impl1.closureToBeInvoked = {domainObject, arguments ->
            synchronized (waitLock)
            {
                waitLock.wait();
            }
            return [domainObject: domainObject]
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
        t1.join();
        t2.join();
    }
    public static void _testThrowsExceptionReleasesLockIfExceptionOccursInActions(Class rapidDomainWriteMethodImpl)
    {
        Object waitLock = new Object();
        Class modelClass = createModels()[0];
        def impl1 = rapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);
        impl1.closureToBeInvoked = {domainObject, arguments ->
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
            catch (Exception e)
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
        t1.join();
        t2.join();
    }
    public static void _testSynchronizationWillNotBeAppliedToMethodsReturningLockKeyAsNull(Class rapidDomainWriteMethodImpl)
    {
        Object waitLock = new Object();
        Class modelClass = createModels()[0];
        def impl1 = rapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);
        impl1.closureToBeInvoked = {domainObject, arguments ->
            synchronized (waitLock)
            {
                waitLock.wait();
            }
        }
        def impl2 = rapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);
        impl2.lockKeyClosure = {object, args ->
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
        t1.join();
        t2.join();
    }
    public static void _testSynchronizationWithAThreadRequestingSameLock(Class rapidDomainWriteMethodImpl)
    {
        Object waitLock = new Object();
        Class modelClass = createModels()[0];
        def impl1 = rapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);

        def impl2 = rapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);

        def subRequest = rapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);
        impl1.closureToBeInvoked = {domainObject, arguments ->
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
        t1.join();
        t2.join();
    }
    public static void _testTimeoutMechanismWithoutDeadLock(Class rapidDomainWriteMethodImpl)
    {
        DomainLockManager.getInstance().initialize(Logger.getRootLogger(), 2000);

        Object waitLock = new Object();
        Class modelClass = createModels()[0];
        def impl1 = rapidDomainWriteMethodImpl.newInstance(modelClass.metaClass);
        impl1.closureToBeInvoked = {domainObject, arguments ->
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
            } catch (e)
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
        t1.join();
        t2.join();
    }
    public static void _testDeadLockDetection(Class rapidDomainWriteMethodImpl)
    {
        DomainLockManager.getInstance().initialize(TestLogUtils.log, 1000000);
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
        instance1Request1.closureToBeInvoked = {domainObject, arguments ->
            println "REQUEST1 EXECUTING"
            numberOfExecutionOfInvokeOfInstance1++;
            if (willBlock1)
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
        instance2Request1.closureToBeInvoked = {domainObject, arguments ->
            println "REQUEST2 EXECUTING"
            numberOfExecutionOfInvokeOfInstance2++;
            if (willBlock2)
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
            catch (Exception e)
            {

                println "t1 ${e.getCause().getCode()} ${e.getCause().getReason()}"
                thread1State = 3;
            }
        }
        CommonTestUtils.waitFor(new ClosureWaitAction() {
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
            catch (org.apache.commons.transaction.locking.LockException ex)
            {
                println "t2 ${ex.getCode()} ${ex.getReason()}"
                thread2State = 3;
            }
        }
        CommonTestUtils.waitFor(new ClosureWaitAction() {
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
        Thread.sleep(3000)
        println thread1State
        println thread2State
        CommonTestUtils.waitFor(new ClosureWaitAction() {
            Assert.assertTrue((thread1State == 2 && thread2State == 3) || (thread1State == 3 && thread2State == 2));
        }, 100);

    }

    public static List createModels()
    {
        def modelName = "ParentModel";
        def model2Name = "Model2";
        def keyProp = [name: "keyProp", type: ModelGenerator.STRING_TYPE, blank: false];

        def modelMetaProps = [name: modelName]
        def modelProps = [keyProp];
        def keyPropList = [keyProp];
        String modelString = ModelGenerationTestUtils.getModelText(modelMetaProps, modelProps, keyPropList, [])
        String model2String = ModelGenerationTestUtils.getModelText([name: model2Name], modelProps, keyPropList, [])
        GroovyClassLoader gcl = new GroovyClassLoader();
        gcl.parseClass(modelString);
        gcl.parseClass(model2String);
        Class modelClass = gcl.loadClass(modelName);
        Class model2Class = gcl.loadClass(model2Name);
        return [modelClass, model2Class];
    }
}
class DummyAbstractRapidDomainMethodImpl extends AbstractRapidDomainWriteMethod
{

    public DummyAbstractRapidDomainMethodImpl(MetaClass mc) {
        super(mc); //To change body of overridden methods use File | Settings | File Templates.
    }

    protected Map _invoke(Object domainObject, Object[] arguments) {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    protected Object executeAfterTriggers(Map triggersMap) {
        return null;
    }

}

class AbstractRapidDomainWriteMethodImpl extends AbstractRapidDomainWriteMethod
{
    def closureToBeInvoked;
    def afterTriggersClosure;
    def lockKeyClosure = {domainObject, args ->
        return domainObject["keyProp"];
    }
    def AbstractRapidDomainWriteMethodImpl(mc) {
        super(mc);
    }

    public String getLockName(Object domainObject, Object[] arguments)
    {
        return lockKeyClosure(domainObject, arguments)
    }
    protected Map _invoke(Object domainObject, Object[] arguments) {
        def res = null;
        if (closureToBeInvoked)
            res = closureToBeInvoked(domainObject, arguments);
        return res;
    }

    protected Object executeAfterTriggers(Map triggersMap) {
        if(afterTriggersClosure)
            return afterTriggersClosure(triggersMap);
        return triggersMap?.domainObject;
    }

}


