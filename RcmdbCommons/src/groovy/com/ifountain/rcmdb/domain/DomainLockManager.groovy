package com.ifountain.rcmdb.domain

import org.apache.commons.transaction.locking.GenericLock
import org.apache.commons.transaction.locking.GenericLockManager
import org.apache.commons.transaction.util.Log4jLogger
import org.apache.log4j.Logger

/**
 * Created by IntelliJ IDEA.
 * User: mustafa seker
 * Date: 06.Ara.2008
 * Time: 18:32:36
 * To change this template use File | Settings | File Templates.
 */

public class DomainLockManager
{
    public static int WRITE_LOCK = 1;
    private static long lockTimeout = 10000;
    private static GenericLockManager lockManager;
    private static Map lockAccessObjects;
    private static final Object accessLockObject = new Object();
    public static void setLockTimeout(long timeout)
    {
        lockTimeout = timeout;
    }
    public static void getWriteLock(Object owner, String lockName)
    {
        synchronized (accessLockObject)
        {
            if(!hasLock(owner, lockName))
            {
                def accessCount = getLockAccessCount (lockName);
                accessCount.increase();
            }
        }
        try{
            lockManager.lock(owner, lockName, WRITE_LOCK, GenericLock.COMPATIBILITY_REENTRANT, false, lockTimeout);
        }catch(Throwable t){
            removeLock(owner, lockName);
            throw t;
        }
    }

    private static LockAccessCount getLockAccessCount(String lockName)
    {
        synchronized (accessLockObject)
        {
            LockAccessCount accessCount = lockAccessObjects[lockName];
            if(accessCount == null)
            {
                accessCount = new LockAccessCount();
                lockAccessObjects[lockName] = accessCount;
            }
            return accessCount;
        }
    }

    public static void getLock(int type, Object owner, String lockName)
    {
        switch (type)
        {
            case WRITE_LOCK:
                getWriteLock(owner, lockName)
                break;
            default:
                throw new Exception("Invalid lock type "+ type);
        }
    }
    
    public static void releaseLock(Object owner, String lockName)
    {
        def hasLock = hasLock(owner, lockName);
        if(hasLock)
        {
            lockManager.release(owner, lockName)
            removeLock(owner, lockName);
        }
    }

    private static void removeLock(Object owner, String lockName)
    {
        if(lockManager.getAll(owner).isEmpty())
        {
            lockManager.removeOwner (owner);
        }
        synchronized (accessLockObject)
        {
            def accessCount = getLockAccessCount (lockName);
            accessCount.decrease();
            if(accessCount.getAccessCount() == 0)
            {
                lockManager.removeLock (lockName);
                lockAccessObjects.remove (lockName);
            }
        }
    }

    public static boolean hasLock(Object owner, String lockName)
    {
        return lockManager.getOwner(lockName) == owner;
    }

    public static boolean hasLock(Object owner)
    {
        return !lockManager.getAll(owner).isEmpty();
    }

    public static void initialize(long lockTimeout, Logger logger)
    {
        DomainLockManager.lockTimeout = lockTimeout;
        lockAccessObjects = Collections.synchronizedMap([:]);
        lockManager = new GenericLockManager(WRITE_LOCK, new Log4jLogger(logger));
    }

    public static void destroy()
    {
    }

}

class LockAccessCount{
    int accessCount = 0;
    public synchronized void increase()
    {
        accessCount++;
    }

    public synchronized void decrease()
    {
        accessCount--;
    }

    public synchronized int getAccessCount()
    {
        return accessCount;
    }
}
