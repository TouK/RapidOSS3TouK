package com.ifountain.rcmdb.domain.lock

import org.apache.commons.transaction.locking.GenericLockManager
import org.apache.commons.transaction.locking.LockException
import org.apache.log4j.Logger

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Dec 2, 2009
* Time: 6:22:24 PM
*/
class LockStrategyImpl implements LockStrategy {
    public static final int NO_LOCK = 0;
    public static final int SHARED = 1;
    public static final int EXCLUSIVE = 2;
    public static int MAX_NUMBER_OF_RETRIES_AFTER_DEAD_LOCK = 15;
    private int lockLevel = EXCLUSIVE;
    private GenericLockManager lockManager
    private int lockTimeout;
    private static Map lockAccessObjects = Collections.synchronizedMap([:]);;
    private static Map lockOwnerObjects = Collections.synchronizedMap([:]);;
    private static final Object accessLockObject = new Object();
    private static final Object ownerLockObject = new Object();
    Logger logger = Logger.getLogger(LockStrategyImpl.class);
    public LockStrategyImpl(GenericLockManager manager, int level, long timeout) {
        lockLevel = level;
        lockManager = manager;
        lockTimeout = timeout
    }
    public void lock(Object owner, String lockname) throws Exception {
        if (lockname != null) {
            int numberOfRetries = 0;
            while (true)
            {
                numberOfRetries++;
                LockException exception = null;
                increaseAccessCount(lockname);
                try {
                    lockManager.lock(owner, lockname, lockLevel, true, lockTimeout)
                    increaseOwnerCount(owner, lockname);
                    return;
                    if (numberOfRetries > 1)
                    {
                        logger.warn("Successfully get lock after deadlock detection for " + owner + " with lock " + lockname);
                    }
                }
                catch (LockException ex)
                {
                    exception = ex;

                }
                if (exception.getCode() != LockException.CODE_DEADLOCK_VICTIM || numberOfRetries >= MAX_NUMBER_OF_RETRIES_AFTER_DEAD_LOCK)
                {
                    removeLock(lockname);
                    throw exception;
                }
                else
                {
                    if (numberOfRetries == 1)
                    {
                        logger.warn("Deadlock detected and will retry to get lock for " + owner + " with name " + lockname + ". Number of retries :" + numberOfRetries);
                    }
                }
            }
        }
    }

    public void release(Object owner, String lockname) {
        if (lockname != null) {
            def willRelease = false
            synchronized (ownerLockObject) {
                def ownerCount = getLockOwnerCount(owner, lockname);
                ownerCount.decrease();
                if(ownerCount.getCount() <= 0){
                    lockOwnerObjects.remove(owner);
                    willRelease = true;
                }
            }
            if (willRelease) {
                lockManager.release(owner, lockname);
                if (lockManager.getAll(owner).isEmpty())
                {
                    lockManager.removeOwner(owner);
                }
                removeLock(lockname);
            }
        }
    }

    private void increaseAccessCount(String lockname) {
        synchronized (accessLockObject)
        {
            def accessCount = getLockAccessCount(lockname);
            accessCount.increase();
        }
    }
    private void increaseOwnerCount(Object owner, String lockname) {
        synchronized (ownerLockObject) {
            def ownerCount = getLockOwnerCount(owner, lockname);
            ownerCount.increase();
        }
    }

    private void removeLock(String lockname) {
        synchronized (accessLockObject)
        {
            def accessCount = getLockAccessCount(lockname);
            accessCount.decrease();
            if (accessCount.getCount() <= 0)
            {
                lockManager.removeLock(lockname);
                lockAccessObjects.remove(lockname);
            }
        }
    }

    private LockCount getLockAccessCount(String lockName)
    {
        synchronized (accessLockObject)
        {
            LockCount accessCount = lockAccessObjects[lockName];
            if (accessCount == null)
            {
                accessCount = new LockCount();
                lockAccessObjects[lockName] = accessCount;
            }
            return accessCount;
        }
    }
    private LockCount getLockOwnerCount(Object owner, String lockName)
    {
        synchronized (ownerLockObject)
        {
            Map ownedLocks = lockOwnerObjects[owner];
            if (ownedLocks == null) {
                ownedLocks = [:]
                lockOwnerObjects[owner] = ownedLocks
            }
            LockCount ownerCount = ownedLocks[lockName]
            if (ownerCount == null)
            {
                ownerCount = new LockCount();
                ownedLocks[lockName] = ownerCount;
            }
            return ownerCount;
        }
    }
    public static void setMaxNumberOfRetries(int maxNumberOfRetries)
    {
        MAX_NUMBER_OF_RETRIES_AFTER_DEAD_LOCK = maxNumberOfRetries;
    }
}

class LockCount {
    int count = 0;
    public synchronized void increase()
    {
        count++;
    }

    public synchronized void decrease()
    {
        count--;
    }

    public synchronized int getCount()
    {
        return count;
    }
}