package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.test.util.ClosureRunnerThread

/**
* Created by IntelliJ IDEA.
* User: mustafa seker
* Date: Jul 27, 2008
* Time: 4:10:51 PM
* To change this template use File | Settings | File Templates.
*/
class AbstractRapidDomainMethodSynchronizationTest extends RapidCmdbTestCase{
    public void testAbstractRapidDomainMethodSynchronization()
    {
        AbstractRapidDomainMethodImpl impl1 = new AbstractRapidDomainMethodImpl(willBlock:true);
         AbstractRapidDomainMethodImpl impl2 = new AbstractRapidDomainMethodImpl(willBlock:false);
        def blockingClosure = {
            impl1.invoke (null, null);
        }

        def nonBlockingClosure = {
            impl2.invoke (null, null);
        }
        ClosureRunnerThread thread1 = new ClosureRunnerThread(closure:blockingClosure);
        ClosureRunnerThread thread2 = new ClosureRunnerThread(closure:nonBlockingClosure);
        thread1.start();
        Thread.sleep (200);
        thread2.start();
        Thread.sleep (200);
        assertFalse (thread1.isFinished);
        assertTrue (impl1.isInvokeCalled);
        assertFalse (thread2.isFinished);
        assertFalse (impl2.isInvokeCalled);
        synchronized (AbstractRapidDomainMethodImpl.blockLock)
        {
            AbstractRapidDomainMethodImpl.blockLock.notifyAll();            
        }
        Thread.sleep (100);
        assertTrue (thread1.isFinished);
        assertTrue (impl1.isInvokeCalled);
        assertTrue (thread2.isFinished);
        assertTrue (impl2.isInvokeCalled);

    }

    public void testAbstractRapidDomainStaticMethodSynchronization()
    {
        AbstractRapidDomainStaticMethodImpl impl1 = new AbstractRapidDomainStaticMethodImpl(willBlock:true);
         AbstractRapidDomainStaticMethodImpl impl2 = new AbstractRapidDomainStaticMethodImpl(willBlock:false);
        def blockingClosure = {
            impl1.invoke (null, null);
        }

        def nonBlockingClosure = {
            impl2.invoke (null, null);
        }
        ClosureRunnerThread thread1 = new ClosureRunnerThread(closure:blockingClosure);
        ClosureRunnerThread thread2 = new ClosureRunnerThread(closure:nonBlockingClosure);
        thread1.start();
        Thread.sleep (200);
        thread2.start();
        Thread.sleep (200);
        assertFalse (thread1.isFinished);
        assertTrue (impl1.isInvokeCalled);
        assertFalse (thread2.isFinished);
        assertFalse (impl2.isInvokeCalled);
        synchronized (AbstractRapidDomainStaticMethodImpl.blockLock)
        {
            AbstractRapidDomainStaticMethodImpl.blockLock.notifyAll();
        }
        Thread.sleep (100);
        assertTrue (thread1.isFinished);
        assertTrue (impl1.isInvokeCalled);
        assertTrue (thread2.isFinished);
        assertTrue (impl2.isInvokeCalled);

    }
}

class AbstractRapidDomainMethodImpl extends AbstractRapidDomainMethod
{

    public AbstractRapidDomainMethodImpl(MetaClass mc) {
        super(null); //To change body of overridden methods use File | Settings | File Templates.
    }

    boolean willBlock = false;
    boolean isInvokeCalled = false;
    public static Object blockLock = new Object();
    protected Object _invoke(Object domainObject, Object[] arguments) {
        isInvokeCalled = true;
        if(willBlock)
        {
            synchronized (blockLock)
            {
                blockLock.wait ();
            }
        }
        return null; 
    }

    public boolean isWriteOperation() {
        return true; //To change body of implemented methods use File | Settings | File Templates.
    }
    
}

class AbstractRapidDomainStaticMethodImpl extends AbstractRapidDomainStaticMethod
{

    public AbstractRapidDomainStaticMethodImpl(MetaClass mc) {
        super(null); //To change body of overridden methods use File | Settings | File Templates.
    }

    boolean willBlock = false;
    boolean isInvokeCalled = false;
    public static Object blockLock = new Object();
    protected Object _invoke(Class clazz, Object[] arguments) {
        isInvokeCalled = true;
        if(willBlock)
        {
            synchronized (blockLock)
            {
                blockLock.wait ();
            }
        }
        return null;
    }

    public boolean isWriteOperation() {
        return true; //To change body of implemented methods use File | Settings | File Templates.
    }

}
