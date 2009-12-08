package com.ifountain.rcmdb.domain.lock

import org.apache.commons.transaction.locking.GenericLockManager
import com.ifountain.rcmdb.domain.batch.BatchExecutionContext

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Dec 4, 2009
* Time: 10:42:07 AM
*/
class BatchDirectoryLockStrategyImpl extends LockStrategyImpl implements BatchExecutionContext {
    private List releasedLocks = [];

    public BatchDirectoryLockStrategyImpl(GenericLockManager manager, int level, long timeout) {
        super(manager, level, timeout); 
    }
    public void release(Object owner, String lockname) {
         releasedLocks.add([owner:owner, lockname:lockname])
    }

    public void batchStarted() {
    }
    public void batchFinished() {
        releasedLocks.each {Map releasedLock ->
            Object owner = releasedLock.owner;
            String lockname = releasedLock.lockname;
            super.release(owner, lockname);
        }
        releasedLocks = [];
    }
}