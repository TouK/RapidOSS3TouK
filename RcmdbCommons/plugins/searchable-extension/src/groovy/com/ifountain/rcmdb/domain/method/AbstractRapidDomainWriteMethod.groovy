package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.domain.DomainLockManager
import com.ifountain.rcmdb.domain.DomainMethodExecutor
import com.ifountain.rcmdb.domain.DomainMethodExecutorAction

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 23, 2009
* Time: 8:51:21 AM
* To change this template use File | Settings | File Templates.
*/
abstract class AbstractRapidDomainWriteMethod extends AbstractRapidDomainMethod {

    public AbstractRapidDomainWriteMethod(MetaClass mc) {
        super(mc);
    }

    public final Object invoke(Object domainObject, Object[] argumentsp) {
        String lockName = getLockName(domainObject, argumentsp);
        def executionClosure = {
            def res = _invoke(domainObject, argumentsp);
            return res;
        }
        def methodExecutorAction = new DomainMethodExecutorAction(DomainLockManager.WRITE_LOCK, lockName, executionClosure);
        return DomainMethodExecutor.executeActionWithRetry(Thread.currentThread(), methodExecutorAction)
    }

    public String getLockName(Object domainObject, Object[] arguments) {
        StringBuffer bf = new StringBuffer(rootParentClass.name);
        def keys = domainObject.'keySet'();
        if (keys.isEmpty())
        {
            bf.append(domainObject["id"])
        }
        else
        {
            keys.each {prop ->
                bf.append(domainObject[prop.name]);
            }
        }
        return bf.toString();
    }

    protected abstract Object _invoke(Object domainObject, Object[] arguments);

}