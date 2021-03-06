package com.ifountain.rcmdb.domain.lock
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Dec 2, 2009
 * Time: 6:15:35 PM
 */
interface LockStrategy {
    public void lock(Object owner, String lockname) throws Exception;
    public void release(Object owner, String lockname);
}