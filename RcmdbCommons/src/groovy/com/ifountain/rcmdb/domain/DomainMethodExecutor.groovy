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
    public static Object executeActionWithRetry(Object owner, DomainMethodExecutorAction methodExecutorAction)
    {
        int numberOfRetries = 0;
        while(true)
        {
            numberOfRetries++;
            LockException exception = null;
            try{
                def res = executeAction(owner, methodExecutorAction);
                if(numberOfRetries > 1)
                {
                    logger.warn("Successfully executed action after deadlock detection for " + owner + " with lock " +methodExecutorAction.getLockName());
                }
                return res;
            }
            catch(org.apache.commons.transaction.locking.LockException ex)
            {
                exception = ex;
            }
            if(exception.getCode() != LockException.CODE_DEADLOCK_VICTIM || DomainLockManager.getLocks(owner).size() != 0 || numberOfRetries >= MAX_NUMBER_OF_RETRIES_AFTER_DEAD_LOCK)
            {
                throw exception;
            }
            else
            {
                if(numberOfRetries == 1)
                {
                    logger.warn("Deadlock detected and will retry to execute action for " + owner + " with lock " +methodExecutorAction.getLockName()+". Number of retries :"+numberOfRetries);
                }
            }
        }
    }
    public static Object executeAction(Object owner, DomainMethodExecutorAction methodExecutorAction)
    {
        if(!methodExecutorAction.willBeLocked()) return methodExecutorAction.action();

        boolean hasLockPreviously = DomainLockManager.hasLock(owner, methodExecutorAction.getLockName(), methodExecutorAction.lockLevel);
        try
        {
            if (!hasLockPreviously) {
                DomainLockManager.getLock(methodExecutorAction.lockLevel, owner, methodExecutorAction.getLockName())
            }
            return methodExecutorAction.action();
        }
        finally
        {
            if (!hasLockPreviously) {
                DomainLockManager.releaseLock(owner, methodExecutorAction.getLockName())
            }
        }
    }

    public static void setMaxNumberOfRetries(int maxNumberOfRetries)
    {
        MAX_NUMBER_OF_RETRIES_AFTER_DEAD_LOCK = maxNumberOfRetries;        
    }
}
