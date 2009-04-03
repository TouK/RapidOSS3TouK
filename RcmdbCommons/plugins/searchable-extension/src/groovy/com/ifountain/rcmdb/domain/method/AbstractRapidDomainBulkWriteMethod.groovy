package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.domain.DomainMethodExecutorAction
import com.ifountain.rcmdb.domain.DomainLockManager
import com.ifountain.rcmdb.domain.DomainMethodExecutor
import com.ifountain.rcmdb.transaction.RapidCmdbTransactionManager
import com.ifountain.rcmdb.transaction.ITransaction
import com.ifountain.rcmdb.transaction.AbstractGlobalTransaction

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Apr 2, 2009
* Time: 8:57:44 AM
* To change this template use File | Settings | File Templates.
*/
abstract class AbstractRapidDomainBulkWriteMethod extends AbstractRapidDomainMethod {

    public AbstractRapidDomainBulkWriteMethod(MetaClass mc) {
        super(mc);
    }

    public final Object invoke(Object domainObject, Object[] arguments) {
        AbstractGlobalTransaction tr = RapidCmdbTransactionManager.startGlobalTransaction();
        try{
            String lockName = getLockName(domainObject, arguments);
            def executionClosure = {
                return _invoke(domainObject, arguments);
            }
            def methodExecutorAction = new DomainMethodExecutorAction(DomainLockManager.BULK_INDEX_LOCK, lockName, executionClosure);
            def res = DomainMethodExecutor.executeActionWithRetry(Thread.currentThread(), methodExecutorAction)
            return res;
        }finally{
            tr.commitGlobalTransaction();
            RapidCmdbTransactionManager.endTransaction(tr);
        }

    }

    public String getLockName(Object object, Object[] arguments) {
        return mc.theClass.name;
    }


}