package com.ifountain.rcmdb.transaction

import com.ifountain.compass.transaction.ICompassTransaction

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Apr 2, 2009
* Time: 6:16:11 PM
* To change this template use File | Settings | File Templates.
*/
class RapidCmdbTransactionManager {
    private static ITransactionFactory transactionFactory;
    private static ThreadLocal<AbstractGlobalTransaction> globalTrStorage;
    public static void initializeTransactionManager(ITransactionFactory transactionFactory)
    {
        globalTrStorage = new InheritableThreadLocal<AbstractGlobalTransaction>();
        RapidCmdbTransactionManager.transactionFactory = transactionFactory;
    }

    public static void endTransaction(ITransaction tr)
    {
        if(globalTrStorage.get() == tr)
        {
            globalTrStorage.set(null);
        }
    }
    public static AbstractGlobalTransaction startGlobalTransaction()
    {
        if(globalTrStorage.get() == null)
        {
            def tr = RapidCmdbTransactionManager.transactionFactory.createGlobalTransaction();
            globalTrStorage.set(tr);
        }
        return globalTrStorage.get();
    }
    public static ITransaction startTransaction()
    {
        def tr = globalTrStorage.get();
        if(tr == null)
        {
            tr = RapidCmdbTransactionManager.transactionFactory.createTransaction();
        }
        return tr;

    }

    public static Object executeWithGlobalTransaction(Closure closure){
        AbstractGlobalTransaction tr = (AbstractGlobalTransaction) RapidCmdbTransactionManager.startGlobalTransaction();
        try {
            Object res = closure(tr);
            tr.commitGlobalTransaction();
            return res;
        }
        catch (Throwable e) {
            try{
                tr.rollbackGlobalTransaction();
            }catch(Throwable t)
            {
                //ignored
            }
            throw e;
        }
        finally {
            RapidCmdbTransactionManager.endTransaction (tr);    
        }
    }
    public static Object executeWithTransaction(Closure closure){
        ITransaction tr = RapidCmdbTransactionManager.startTransaction();
        try {
            Object res = closure(tr);
            tr.commit();
            return res;
        }
        catch (Throwable e) {
            try{
                tr.rollback();
            }catch(Throwable t)
            {
                //ignored
            }
            throw e;
        }
    }
}