package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.comp.test.util.logging.TestLogUtils
import com.ifountain.rcmdb.transaction.RapidCmdbTransactionManager
import com.ifountain.rcmdb.transaction.TransactionFactoryImpl
import com.ifountain.rcmdb.domain.DomainLockManager
import com.ifountain.rcmdb.transaction.TransactionImpl
import com.ifountain.rcmdb.transaction.GlobalTransactionImpl

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Apr 2, 2009
* Time: 10:25:18 AM
* To change this template use File | Settings | File Templates.
*/
class AbstractRapidDomainBulkWriteMethodTest extends RapidCmdbTestCase
{
    TransactionFactoryImpl impl
    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        impl = new TransactionFactoryImpl();
        impl.globalTrToBeReturned = new GlobalTransactionImpl();
        RapidCmdbTransactionManager.initializeTransactionManager(impl)
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }


    public void testSynchronization()
    {
        AbstractRapidDomainWriteMethodTest._testSynchronization (AbstractRapidDomainBulkWriteMethodImpl);         
    }


    public void testThrowsExceptionReleasesLockIfExceptionOccursInActions()
    {
        AbstractRapidDomainWriteMethodTest._testThrowsExceptionReleasesLockIfExceptionOccursInActions(AbstractRapidDomainBulkWriteMethodImpl);
    }



    public void testSynchronizationWillNotBeAppliedToMethodsReturningLockKeyAsNull()
    {
        AbstractRapidDomainWriteMethodTest._testSynchronizationWillNotBeAppliedToMethodsReturningLockKeyAsNull(AbstractRapidDomainBulkWriteMethodImpl);
    }


    public void testSynchronizationWithAThreadRequestingSameLock()
    {
        AbstractRapidDomainWriteMethodTest._testSynchronizationWithAThreadRequestingSameLock(AbstractRapidDomainBulkWriteMethodImpl);
    }


    public void testTimeoutMechanismWithoutDeadLock()
    {
        AbstractRapidDomainWriteMethodTest._testTimeoutMechanismWithoutDeadLock(AbstractRapidDomainBulkWriteMethodImpl);
    }


    public void testDeadLockDetection()
    {
        AbstractRapidDomainWriteMethodTest._testDeadLockDetection(AbstractRapidDomainBulkWriteMethodImpl);
    }

    public void testBulkWriteStartsAGlobalTransaction()
    {
        impl.globalTrToBeReturned = new GlobalTransactionImpl();
        DomainLockManager.initialize(2000, TestLogUtils.log);
        Class modelClass = AbstractRapidDomainWriteMethodTest.createModels()[0];
        AbstractRapidDomainBulkWriteMethodImpl impl1 = new AbstractRapidDomainBulkWriteMethodImpl(modelClass.metaClass);
        GlobalTransactionImpl tr1 = null;
        GlobalTransactionImpl tr2 = null;
        impl1.closureToBeInvoked = {Object domainObject, Object[] arguments->
            impl.trToBeReturned = new TransactionImpl();
            tr1 = RapidCmdbTransactionManager.startTransaction();
            impl.trToBeReturned = new TransactionImpl();
            tr2 = RapidCmdbTransactionManager.startTransaction();
        }

        impl1.invoke (modelClass.newInstance(), null);
        assertNotNull (tr1);
        assertSame("Since global transaction is started all requested trs from this thread will return same transaction", tr1, tr2);
        assertEquals (1, tr1.methodCalls.size());
        assertEquals ("commitGlobalTransaction", tr1.methodCalls[0]);
        def trAfterInvoke = RapidCmdbTransactionManager.startTransaction();
        assertNotSame ("A new transaction should be created after invoke operation", tr1, trAfterInvoke);
        RapidCmdbTransactionManager.endTransaction (trAfterInvoke);
    }

    public void testBulkWriteEndsGlobalTransactionIfAnyExceptionOccurs()
    {
        impl.globalTrToBeReturned = new GlobalTransactionImpl();
        DomainLockManager.initialize(2000, TestLogUtils.log);
        Class modelClass = AbstractRapidDomainWriteMethodTest.createModels()[0];
        AbstractRapidDomainBulkWriteMethodImpl impl1 = new AbstractRapidDomainBulkWriteMethodImpl(modelClass.metaClass);
        def tr1 = null;
        def exceptionMessage = "An exception occurred";
        impl1.closureToBeInvoked = {Object domainObject, Object[] arguments->
            impl.trToBeReturned = new TransactionImpl();
            tr1 = RapidCmdbTransactionManager.startTransaction();
            throw new Exception(exceptionMessage);
        }
        try{
            impl1.invoke (modelClass.newInstance(), null);
            fail("Should throw exception");
        }catch(Exception e)
        {
            assertEquals (exceptionMessage, e.getMessage());
        }
        assertNotNull (tr1);
        assertEquals (1, tr1.methodCalls.size());
        assertEquals ("commitGlobalTransaction", tr1.methodCalls[0]);
        def trAfterInvoke = RapidCmdbTransactionManager.startTransaction();
        assertNotSame ("A new transaction should be created after invoke operation", tr1, trAfterInvoke);
        RapidCmdbTransactionManager.endTransaction (trAfterInvoke);
    }
}

class AbstractRapidDomainBulkWriteMethodImpl extends AbstractRapidDomainBulkWriteMethod
{
    def closureToBeInvoked;
    def lockKeyClosure= {domainObject->
        return domainObject["keyProp"];
    }
    def AbstractRapidDomainBulkWriteMethodImpl(mc) {
        super(mc);
    }

    public String getLockName(Object domainObject, Object[] arguments)
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