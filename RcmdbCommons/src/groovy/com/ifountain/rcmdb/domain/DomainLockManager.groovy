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
    public static int WRITE_LOCK = 1;
    private static long lockTimeout = 10000;
    private static GenericLockManager lockManager;
    public static void getLock(Object owner, String lockName)
    {
        lockManager.lock(owner, lockName, WRITE_LOCK, true, lockTimeout);
    }

    public static void releaseLock(Object owner, String lockName)
    {
        lockManager.release(owner, lockName)
    }

    public static boolean hasLock(Object owner, String lockName)
    {
        return lockManager.hasLock(owner, lockName, WRITE_LOCK);
    }

    public static void initialize(long lockTimeout, Logger logger)
    {
        DomainLockManager.lockTimeout = lockTimeout;
        lockManager = new GenericLockManager(WRITE_LOCK, new Log4jLogger(logger));
    }

    public static void destroy()
    {
    }

    
}
