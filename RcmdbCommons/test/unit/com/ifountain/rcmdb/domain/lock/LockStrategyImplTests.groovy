package com.ifountain.rcmdb.domain.lock

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import org.apache.commons.transaction.locking.GenericLockManager
import org.apache.commons.transaction.util.Log4jLogger
import org.apache.log4j.Logger

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Dec 8, 2009
* Time: 11:40:48 AM
* To change this template use File | Settings | File Templates.
*/
class LockStrategyImplTests extends RapidCmdbTestCase {
    GenericLockManager lockManager
    protected void setUp() {
        super.setUp();
        lockManager = new GenericLockManager(2, new Log4jLogger(Logger.getRootLogger()));
    }

    protected void tearDown() {
        LockStrategyImpl.lockAccessObjects.clear();
        LockStrategyImpl.lockOwnerObjects.clear();
        super.tearDown();
    }

    public void testGenericLockManagerMemoryWithLockAndRelease(){
       LockStrategyImpl lockStrategy = new LockStrategyImpl(lockManager, 2, 3000)
       def lockName = "lock1";
       def lockOwner = new Object();
       lockStrategy.lock(lockOwner, lockName);
       lockStrategy.release(lockOwner, lockName);

       assertEquals(0, LockStrategyImpl.lockOwnerObjects.size());
       assertEquals(0, LockStrategyImpl.lockOwnerObjects.size());
       assertEquals(0, lockManager.getLocks().size());
       assertEquals(0, lockManager.globalOwners.size());

       lockStrategy.lock(lockOwner, lockName);
       lockStrategy.lock(lockOwner, lockName);
       lockStrategy.release(lockOwner, lockName);
       lockStrategy.release(lockOwner, lockName);

       assertEquals(0, LockStrategyImpl.lockOwnerObjects.size());
       assertEquals(0, LockStrategyImpl.lockOwnerObjects.size());
       assertEquals(0, lockManager.getLocks().size());
       assertEquals(0, lockManager.globalOwners.size());

       lockStrategy.lock(lockOwner, lockName);
       lockStrategy.lock(lockOwner, lockName);
       lockStrategy.release(lockOwner, lockName);
       lockStrategy.release(lockOwner, lockName);
       lockStrategy.release(lockOwner, lockName);
       lockStrategy.release(lockOwner, lockName);

       assertEquals(0, LockStrategyImpl.lockOwnerObjects.size());
       assertEquals(0, LockStrategyImpl.lockOwnerObjects.size());
       assertEquals(0, lockManager.getLocks().size());
       assertEquals(0, lockManager.globalOwners.size());
    }

}