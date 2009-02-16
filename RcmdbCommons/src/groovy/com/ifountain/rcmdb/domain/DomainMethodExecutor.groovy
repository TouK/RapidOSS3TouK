package com.ifountain.rcmdb.domain

import org.apache.commons.transaction.locking.LockException
import org.apache.log4j.Logger

/**
 * Created by IntelliJ IDEA.
 * User: mustafa seker
 * Date: 07.Ara.2008
 * Time: 04:33:39
 * To change this template use File | Settings | File Templates.
 */

public class DomainMethodExecutor
{
    public static int MAX_NUMBER_OF_RETRIES_AFTER_DEAD_LOCK = 15;
    static Logger logger = Logger.getLogger(DomainMethodExecutor.class);
    public static Object executeAction(Object owner, String lockName, Closure action)
    {
        if(lockName == null) return action();
        int numberOfRetries = 0;
        while(true)
        {
            numberOfRetries++;
            LockException exception = null;
            boolean hasLockPreviously = DomainLockManager.hasLock(owner, lockName);
            try
            {
                if (!hasLockPreviously) {
                    DomainLockManager.getLock(owner, lockName)
                }
                Object res = action();
                if(numberOfRetries > 1)
                {
                    println "Successfully executed action after deadlock detection for " + owner + " with lock " +lockName
                    logger.warn("Successfully executed action after deadlock detection for " + owner + " with lock " +lockName);
                }
                return res;
            }
            catch(org.apache.commons.transaction.locking.LockException ex)
            {
                exception =ex;
            }
            finally
            {
                if (!hasLockPreviously) {
                    DomainLockManager.releaseLock(owner, lockName)
                }
            }
            if(exception.getCode() != LockException.CODE_DEADLOCK_VICTIM || DomainLockManager.getLocks(owner).size() != 0 || numberOfRetries >= MAX_NUMBER_OF_RETRIES_AFTER_DEAD_LOCK)
            {
                throw exception;
            }
            else
            {
                if(numberOfRetries == 1)
                {
                    println "Deadlock detected and will retry to execute action for " + owner + " with lock " +lockName+". Number of retries :"+numberOfRetries
                    logger.warn("Deadlock detected and will retry to execute action for " + owner + " with lock " +lockName+". Number of retries :"+numberOfRetries);
                }
            }
        }
    }

    public static void setMaxNumberOfRetries(int maxNumberOfRetries)
    {
        MAX_NUMBER_OF_RETRIES_AFTER_DEAD_LOCK = maxNumberOfRetries;        
    }
}
