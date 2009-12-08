package com.ifountain.rcmdb.domain.method

import com.ifountain.compass.search.FilterManager
import com.ifountain.rcmdb.domain.DomainLockManager

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
        String directoryLockName = getDirectoryLockName(domainObject, argumentsp);
        def isFiltersEnabledBeforeExecutingThisMethod = FilterManager.isFiltersEnabled();
        FilterManager.setFiltersEnabled(false);
        try {
            def triggersMap;
            DomainLockManager.getInstance().lockDirectory(directoryLockName)
            try {
                DomainLockManager.getInstance().lockInstance(lockName)
                try {
                    triggersMap = _invoke(domainObject, argumentsp);
                }
                finally {
                    DomainLockManager.getInstance().releaseInstance(lockName)
                }
            }
            finally {
                DomainLockManager.getInstance().releaseDirectory(directoryLockName)
            }
            return executeAfterTriggers(triggersMap);
        }
        finally {
            FilterManager.setFiltersEnabled(isFiltersEnabledBeforeExecutingThisMethod);
        }
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
    public String getDirectoryLockName(Object domainObject, Object[] arguments) {
        return domainObject.class.name;
    }

    protected abstract Map _invoke(Object domainObject, Object[] arguments);
    protected abstract Object executeAfterTriggers(Map triggersMap);

}