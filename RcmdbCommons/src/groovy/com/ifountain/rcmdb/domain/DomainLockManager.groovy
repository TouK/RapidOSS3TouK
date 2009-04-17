package com.ifountain.rcmdb.domain

import org.apache.commons.transaction.locking.GenericLockManager
import org.apache.commons.transaction.locking.LockManager
import org.apache.commons.transaction.util.PrintWriterLogger
import org.apache.commons.transaction.locking.GenericLock
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
    public static int WRITE_LOCK = 3;
    public static int BULK_INDEX_LOCK = 4;
    public static int BULK_INDEX_CHECK_LOCK = 5;
    private static long lockTimeout = 10000;
    private static GenericLockManager lockManager;
    public static void getWriteLock(Object owner, String lockName)
    {
        lockManager.lock(owner, lockName, WRITE_LOCK, GenericLock.COMPATIBILITY_REENTRANT, false, lockTimeout);
    }

    public static void getLock(int type, Object owner, String lockName)
    {
        switch (type)
        {
            case WRITE_LOCK:
                getWriteLock(owner, lockName)
                break;
            case BULK_INDEX_LOCK:
                getBulkIndexLock(owner, lockName)
                break;
            case BULK_INDEX_CHECK_LOCK:
                getBulkIndexCheckLock(owner, lockName)
                break;
            default:
                throw new Exception("Invalid lock type "+ type);
        }
    }
    public static void getBulkIndexCheckLock(Object owner, String lockName)
    {
        lockManager.lock(owner, lockName, BULK_INDEX_CHECK_LOCK, GenericLock.COMPATIBILITY_REENTRANT_AND_SUPPORT, false, lockTimeout);
    }

    public static void getBulkIndexLock(Object owner, String lockName)
    {
        lockManager.lock(owner, lockName, BULK_INDEX_LOCK, GenericLock.COMPATIBILITY_REENTRANT, true, lockTimeout);
    }

    public static void releaseAllLocks(Object owner)
    {
        lockManager.releaseAll (owner);
    }
    public static void releaseLock(Object owner, String lockName)
    {
        lockManager.release(owner, lockName)
    }

    public static Set getLocks(Object owner)
    {
        return lockManager.getAll(owner)
    }

    public static boolean hasLock(Object owner, String lockName, int lockLevel)
    {
        return lockManager.hasLock(owner, lockName, lockLevel);
    }

    public static void initialize(long lockTimeout, Logger logger)
    {
        DomainLockManager.lockTimeout = lockTimeout;
        lockManager = new GenericLockManager(BULK_INDEX_CHECK_LOCK, new Log4jLogger(logger));
    }

    public static void destroy()
    {
    }

}
