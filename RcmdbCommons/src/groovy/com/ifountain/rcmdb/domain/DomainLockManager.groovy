package com.ifountain.rcmdb.domain

import org.apache.commons.transaction.locking.GenericLockManager
import org.apache.commons.transaction.util.Log4jLogger
import org.apache.log4j.Logger
import com.ifountain.rcmdb.domain.lock.LockStrategyImpl
import com.ifountain.rcmdb.domain.lock.LockStrategy
import com.ifountain.rcmdb.domain.lock.BatchDirectoryLockStrategyImpl
import com.ifountain.rcmdb.domain.batch.AbstractBatchExecutionManager
import com.ifountain.rcmdb.domain.batch.BatchExecutionContext

/**
 * Created by IntelliJ IDEA.
 * User: mustafa seker
 * Date: 06.Ara.2008
 * Time: 18:32:36
 * To change this template use File | Settings | File Templates.
 */

public class DomainLockManager extends AbstractBatchExecutionManager
{
    private long instanceBasedLockTimeout = 10000;
    private long directoryBasedLockTimeout = 1800000;
    public GenericLockManager lockManager;
    private DomainLockManager() {
        batchExecutionContextStorage = new ThreadLocal<BatchExecutionContext>()
    }
    private static DomainLockManager instance;
    
    public static DomainLockManager getInstance() {
        if (instance == null) {
            instance = new DomainLockManager();
        }
        return instance;
    }

    public static destroy(){
        instance = null;
    }

    protected BatchExecutionContext makeStorageInstance() {
        return new BatchDirectoryLockStrategyImpl(lockManager, LockStrategyImpl.EXCLUSIVE, directoryBasedLockTimeout)
    }

    public void lockDirectory(String directoryName) throws Exception {
        LockStrategy lockStrategy = getDirectoryLockStrategy();
        lockStrategy.lock(getOwner(), directoryName);
    }
    public void releaseDirectory(String directoryName) {
        LockStrategy lockStrategy = getDirectoryLockStrategy();
        lockStrategy.release(getOwner(), directoryName);
    }

    private LockStrategy getDirectoryLockStrategy() {
        LockStrategy lockStrategy = (BatchDirectoryLockStrategyImpl) batchExecutionContextStorage.get();
        if (lockStrategy == null) {
            lockStrategy = new LockStrategyImpl(lockManager, LockStrategyImpl.SHARED, directoryBasedLockTimeout);
        }
        return lockStrategy
    }

    public void lockInstance(String instanceName) throws Exception {
        new LockStrategyImpl(lockManager, LockStrategyImpl.EXCLUSIVE, instanceBasedLockTimeout).lock(getOwner(), instanceName);
    }
    public void releaseInstance(String instanceName) {
        new LockStrategyImpl(lockManager, LockStrategyImpl.EXCLUSIVE, instanceBasedLockTimeout).release(getOwner(), instanceName);
    }

    private Object getOwner() {
        return Thread.currentThread();
    }
    public void initialize(Logger logger) {
        initialize(logger, 10000, 1800000)
    }
    public void initialize(Logger logger, long instanceLockTimeout) {
        initialize(logger, instanceLockTimeout, 1800000)
    }
    public void initialize(Logger logger, long intanceLockTimeout, long directoryLockTimeout)
    {
        instanceBasedLockTimeout = intanceLockTimeout;
        directoryBasedLockTimeout = directoryLockTimeout;
        lockManager = new GenericLockManager(2, new Log4jLogger(logger));
    }

    public void setInstanceBasedLockTimeout(long newTimeout) {
        instanceBasedLockTimeout = newTimeout;
    }
    public void setDirectoryBasedLockTimeout(long newTimeout) {
        directoryBasedLockTimeout = newTimeout;
    }

}
