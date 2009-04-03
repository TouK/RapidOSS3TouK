package com.ifountain.rcmdb.transaction

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Apr 2, 2009
* Time: 6:21:16 PM
* To change this template use File | Settings | File Templates.
*/
class RapidCmdbTransactionManagerTest extends RapidCmdbTestCase{
    public void testStartTransaction()
    {
        TransactionFactoryImpl impl = new TransactionFactoryImpl();
        ITransaction tr = new TransactionImpl();
        impl.trToBeReturned = tr;
        RapidCmdbTransactionManager.initializeTransactionManager (impl);
        assertSame(tr, RapidCmdbTransactionManager.startTransaction());
    }

    public void testStartGlobalTransaction()
    {
        TransactionFactoryImpl impl = new TransactionFactoryImpl();
        AbstractGlobalTransaction globalTransaction = new GlobalTransactionImpl();
        impl.globalTrToBeReturned = globalTransaction;
        RapidCmdbTransactionManager.initializeTransactionManager (impl);
        assertSame(globalTransaction, RapidCmdbTransactionManager.startGlobalTransaction());

        AbstractGlobalTransaction globalTransaction2 = new GlobalTransactionImpl();
        ITransaction tr2 = new TransactionImpl();
        impl.trToBeReturned = tr2;
        impl.globalTrToBeReturned = globalTransaction2;
        assertSame("Global transaction will be returned till its removal", globalTransaction, RapidCmdbTransactionManager.startGlobalTransaction());
        RapidCmdbTransactionManager.endTransaction(globalTransaction);
        assertSame("A new global tr will be returned after its removal", globalTransaction2, RapidCmdbTransactionManager.startGlobalTransaction());
    }
    

    public void testStartTransactionWillReturnGlobalTransactionIfAnyExist()
    {
        TransactionFactoryImpl impl = new TransactionFactoryImpl();
        AbstractGlobalTransaction globalTransaction = new GlobalTransactionImpl();
        ITransaction tr = new TransactionImpl();
        impl.trToBeReturned = tr;
        impl.globalTrToBeReturned = globalTransaction;
        RapidCmdbTransactionManager.initializeTransactionManager (impl);
        assertSame(globalTransaction, RapidCmdbTransactionManager.startGlobalTransaction());

        AbstractGlobalTransaction globalTransaction2 = new GlobalTransactionImpl();
        ITransaction tr2 = new TransactionImpl();
        impl.trToBeReturned = tr2;
        impl.globalTrToBeReturned = globalTransaction2;
        assertSame(globalTransaction, RapidCmdbTransactionManager.startTransaction());
    }

    public void testGlobalTransactionIsThreadSpecific()
    {
        TransactionFactoryImpl impl = new TransactionFactoryImpl();
        AbstractGlobalTransaction tr = new GlobalTransactionImpl();
        impl.globalTrToBeReturned = tr;
        RapidCmdbTransactionManager.initializeTransactionManager (impl);
        def thread1Global = null;
        def thread2Global = null;
        Thread t1 = Thread.start{
            thread1Global = RapidCmdbTransactionManager.startGlobalTransaction();
        }

        t1.join ();

        AbstractGlobalTransaction tr2 = new GlobalTransactionImpl();
        impl.globalTrToBeReturned = tr2;
        Thread t2 = Thread.start{
            thread2Global = RapidCmdbTransactionManager.startGlobalTransaction();
        }
        t2.join ();
        assertNotSame (thread1Global, thread2Global);
        assertSame (tr, thread1Global);
        assertSame (tr2, thread2Global);
    }
}

class TransactionFactoryImpl implements ITransactionFactory{
    public ITransaction trToBeReturned;
    public AbstractGlobalTransaction globalTrToBeReturned;
    public ITransaction createTransaction() {
        return trToBeReturned; //To change body of implemented methods use File | Settings | File Templates.
    }

    public AbstractGlobalTransaction createGlobalTransaction() {
        return globalTrToBeReturned; //To change body of implemented methods use File | Settings | File Templates.
    }




}

class GlobalTransactionImpl extends AbstractGlobalTransaction
{
    def methodCalls = [];
    public void commitGlobalTransaction() {
        methodCalls.add("commitGlobalTransaction");
    }

    public void rollbackGlobalTransaction() {
        methodCalls.add("rollbackGlobalTransaction");
    }
}
class TransactionImpl implements ITransaction
{

    public void commit() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void rollback() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
    
}