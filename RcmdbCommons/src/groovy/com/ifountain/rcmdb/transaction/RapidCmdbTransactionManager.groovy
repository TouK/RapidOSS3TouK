package com.ifountain.rcmdb.transaction
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
}