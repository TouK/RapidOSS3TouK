package com.ifountain.rcmdb.domain
/**
 * Created by IntelliJ IDEA.
 * User: mustafa seker
 * Date: 07.Ara.2008
 * Time: 04:33:39
 * To change this template use File | Settings | File Templates.
 */

public class DomainMethodExecutor
{
    public static Object executeAction(Object owner, String lockName, Closure action)
    {
        boolean hasLockPreviously = DomainLockManager.hasLock(owner, lockName);
        try
        {
            if (!hasLockPreviously) {
                DomainLockManager.getLock(owner, lockName)
            }
            return action();
        }
        finally
        {
            if (!hasLockPreviously) {
                DomainLockManager.releaseLock(owner, lockName)
            }
        }
    }
}
