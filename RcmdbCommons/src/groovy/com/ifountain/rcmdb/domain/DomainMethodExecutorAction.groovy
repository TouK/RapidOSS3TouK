package com.ifountain.rcmdb.domain
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Apr 1, 2009
 * Time: 4:49:44 PM
 * To change this template use File | Settings | File Templates.
 */
class DomainMethodExecutorAction {
    String lockName;
    int lockLevel;
    Closure action;
    public DomainMethodExecutorAction(int lockLevel, String lockName, Closure action)
    {
        this.lockLevel = lockLevel;
        this.lockName = lockName;
        this.action = action;
    }

    public boolean willBeLocked()
    {
        return this.lockName != null;
    }

    
}