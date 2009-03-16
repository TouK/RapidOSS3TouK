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

/**
 * Created by IntelliJ IDEA.
 * User: mustafa seker
 * Date: 06.Ara.2008
 * Time: 17:03:26
 * To change this template use File | Settings | File Templates.
 */

public class AbstractDomainMethodTest extends RapidCmdbTestCase
{
    public void testSynchronization()
    {
        Object waitLock = new Object();
        Class modelClass = createModels()[0];
        AbstractRapidDomainMethodImpl impl1 = new AbstractRapidDomainMethodImpl(modelClass.metaClass);
        impl1.closureToBeInvoked ={domainObject, arguments->
            synchronized (waitLock)
            {
                waitLock.wait();                 
            }
        }
        AbstractRapidDomainMethodImpl impl2 = new AbstractRapidDomainMethodImpl(modelClass.metaClass);

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
    }

    public void testThrowsExceptionReleasesLockIfExceptionOccursInActions()
    {
        Object waitLock = new Object();
        Class modelClass = createModels()[0];
        AbstractRapidDomainMethodImpl impl1 = new AbstractRapidDomainMethodImpl(modelClass.metaClass);
        impl1.closureToBeInvoked ={domainObject, arguments->
            synchronized (waitLock)
            {
                waitLock.wait();
            }
            throw new RuntimeException("An exception");
        }
        AbstractRapidDomainMethodImpl impl2 = new AbstractRapidDomainMethodImpl(modelClass.metaClass);

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
    }

    public void testSynchronizationWillNotBeAppliedToReadOperations()
    {
        Object waitLock = new Object();
        Class modelClass = createModels()[0];
        AbstractRapidDomainMethodImpl impl1 = new AbstractRapidDomainMethodImpl(modelClass.metaClass);
        impl1.closureToBeInvoked ={domainObject, arguments->
            synchronized (waitLock)
            {
                waitLock.wait();
            }
        }
        AbstractRapidDomainMethodImpl impl2 = new AbstractRapidDomainMethodImpl(modelClass.metaClass);
        impl2.isWriteOperation = false;

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
    }


    public void testSynchronizationWillNotBeAppliedToMethodsReturningLockKeyAsNull()
    {
        Object waitLock = new Object();
        Class modelClass = createModels()[0];
        AbstractRapidDomainMethodImpl impl1 = new AbstractRapidDomainMethodImpl(modelClass.metaClass);
        impl1.closureToBeInvoked ={domainObject, arguments->
            synchronized (waitLock)
            {
                waitLock.wait();
            }
        }
        AbstractRapidDomainMethodImpl impl2 = new AbstractRapidDomainMethodImpl(modelClass.metaClass);
        impl2.lockKeyClosure = {
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
    }

    public void testSynchronizationWithAThreadRequestingSameLock()
    {
        Object waitLock = new Object();
        Class modelClass = createModels()[0];
        AbstractRapidDomainMethodImpl impl1 = new AbstractRapidDomainMethodImpl(modelClass.metaClass);

        AbstractRapidDomainMethodImpl impl2 = new AbstractRapidDomainMethodImpl(modelClass.metaClass);

        AbstractRapidDomainMethodImpl subRequest = new AbstractRapidDomainMethodImpl(modelClass.metaClass);
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
    }

    public void testTimeoutMechanismWithoutDeadLock()
    {
        DomainLockManager.initialize(2000, Logger.getRootLogger());

        Object waitLock = new Object();
        Class modelClass = createModels()[0];
        AbstractRapidDomainMethodImpl impl1 = new AbstractRapidDomainMethodImpl(modelClass.metaClass);
        impl1.closureToBeInvoked ={domainObject, arguments->
            synchronized (waitLock)
            {
                waitLock.wait();
            }
        }

        AbstractRapidDomainMethodImpl impl2 = new AbstractRapidDomainMethodImpl(modelClass.metaClass);


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
    }

    public void testDeadLockDetection()
    {
        TestLogUtils.enableLogger(Logger.getRootLogger());
        DomainLockManager.initialize(2000, TestLogUtils.log);
        Object waitLock1 = new Object();
        Object waitLock2 = new Object();
        Class modelClass = createModels()[0];
        AbstractRapidDomainMethodImpl instance1Request1 = new AbstractRapidDomainMethodImpl(modelClass.metaClass);
        AbstractRapidDomainMethodImpl instance1Request2 = new AbstractRapidDomainMethodImpl(modelClass.metaClass);
        AbstractRapidDomainMethodImpl instance2Request1 = new AbstractRapidDomainMethodImpl(modelClass.metaClass);
        AbstractRapidDomainMethodImpl instance2Request2 = new AbstractRapidDomainMethodImpl(modelClass.metaClass);

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
            numberOfExecutionOfInvoke++;
            if(willBlock1)
            {
                synchronized (waitLock1)
                {
                    waitLock1.wait();
                }
            }
            instance2Request2.invoke(instance2Clone, null)
        }
        instance2Request1.closureToBeInvoked ={domainObject, arguments->
            if(willBlock2)
            {
                synchronized (waitLock2)
                {
                    waitLock2.wait();
                }
            }
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
        assertEquals(2, thread1State);
        assertEquals(2, numberOfExecutionOfInvoke);
        assertEquals(2, thread2State);
    }


    public void testThrowsDeadLockDetectionIfItExceededMaxNumberOfRetries()
    {
        DomainLockManager.initialize(2000, TestLogUtils.log);
        Object waitLock1 = new Object();
        Object waitLock2 = new Object();
        Class modelClass = createModels()[0];
        AbstractRapidDomainMethodImpl instance1Request1 = new AbstractRapidDomainMethodImpl(modelClass.metaClass);
        AbstractRapidDomainMethodImpl instance1Request2 = new AbstractRapidDomainMethodImpl(modelClass.metaClass);
        AbstractRapidDomainMethodImpl instance2Request1 = new AbstractRapidDomainMethodImpl(modelClass.metaClass);
        AbstractRapidDomainMethodImpl instance2Request2 = new AbstractRapidDomainMethodImpl(modelClass.metaClass);

        def instance1 = modelClass.newInstance();
        def instance1Clone = modelClass.newInstance();
        def instance2 = modelClass.newInstance();
        def instance2Clone = modelClass.newInstance();
        instance1["keyProp"] = "testThrowsDeadLockDetectionIfItExceededMaxNumberOfRetrieskeyvalue1"
        instance1Clone["keyProp"] = "testThrowsDeadLockDetectionIfItExceededMaxNumberOfRetrieskeyvalue1"
        instance2["keyProp"] = "testThrowsDeadLockDetectionIfItExceededMaxNumberOfRetrieskeyvalue2"
        instance2Clone["keyProp"] = "testThrowsDeadLockDetectionIfItExceededMaxNumberOfRetrieskeyvalue2"
        int numberOfExecutionOfInvoke = 0;
        instance1Request1.closureToBeInvoked ={domainObject, arguments->
            numberOfExecutionOfInvoke++;
            synchronized (waitLock1)
            {
                waitLock1.wait();
            }
            instance2Request2.invoke(instance2Clone, null)
        }
        instance2Request1.closureToBeInvoked ={domainObject, arguments->
            synchronized (waitLock2)
            {
                waitLock2.wait();
            }
            instance1Request2.invoke(instance1Clone, null)
        }

        int maxRetry = 3;
        DomainMethodExecutor.setMaxNumberOfRetries(maxRetry);

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
        println "STARTED THREAD "+ t1



        for(int i=0; i < maxRetry; i++)
        {
            println "NUMBEROF RETRY:"+i;
            int numberOfRetriesToFindNumberOfExecution = 0;
            while(numberOfExecutionOfInvoke != i+1 && numberOfRetriesToFindNumberOfExecution < 1000)
            {
                Thread.sleep (1);
                numberOfRetriesToFindNumberOfExecution++;
            }
            assertEquals (i+1, numberOfExecutionOfInvoke);
            int thread2State = 0;
            def t2 = Thread.start {
                thread2State = 1;
                try
                {
                    instance2Request1.invoke(instance2, null)
                    thread2State = 2;
                }
                catch(Throwable ex)
                {
                    thread2State = 3;
                }
            }
            println "STARTED THREAD "+ t2
            Thread.sleep(300);
            assertEquals(1, thread1State);
            assertEquals(1, thread2State);
            synchronized (waitLock1)
            {
                waitLock1.notifyAll();
            }
            Thread.sleep(200);

            synchronized (waitLock2)
            {
                waitLock2.notifyAll();
            }
            Thread.sleep(1800);
            if(i != maxRetry-1)
            {
                assertEquals(1, thread1State);
            }
            else
            {
                assertEquals(3, thread1State);    
            }
            int thread2StateCheckCount = 0;
            while(thread2StateCheckCount < 20)
            {
                thread2StateCheckCount++;
                synchronized (waitLock2)
                {
                    waitLock2.notifyAll();
                }
                Thread.sleep(300);
                if(thread2State == 2)
                {
                    break;
                }
            }

            assertTrue("Thread2 state should be 2 after deadlock detection", thread2StateCheckCount < 8);
            t2.join ();
        }
        Thread.sleep(500);
        assertEquals(3, thread1State);
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
    private List createModels()
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
class DummyAbstractRapidDomainMethodImpl extends AbstractRapidDomainMethod
{

    public DummyAbstractRapidDomainMethodImpl(MetaClass mc) {
        super(mc); //To change body of overridden methods use File | Settings | File Templates.
    }

    public boolean isWriteOperation() {
        return false; //To change body of implemented methods use File | Settings | File Templates.
    }

    protected Object _invoke(Object domainObject, Object[] arguments) {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

}
class AbstractRapidDomainMethodImpl extends AbstractRapidDomainMethod
{

    public boolean isWriteOperation = true;
    def closureToBeInvoked;
    def lockKeyClosure= {domainObject->
        return domainObject["keyProp"];        
    }
    def AbstractRapidDomainMethodImpl(mc) {
        super(mc);
    }


    public boolean isWriteOperation() {
        return isWriteOperation;  //To change body of implemented methods use File | Settings | File Templates.
    }
    public String getLockName(Object domainObject)
    {
        return lockKeyClosure(domainObject)
    }
    protected Object _invoke(Object domainObject, Object[] arguments) {
        def res = null;
        if(closureToBeInvoked)
        res = closureToBeInvoked(domainObject, arguments);
        return res;
    }

}
