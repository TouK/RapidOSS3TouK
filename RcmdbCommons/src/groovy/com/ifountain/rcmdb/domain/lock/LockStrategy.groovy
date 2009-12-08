package com.ifountain.rcmdb.domain.lock
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Dec 2, 2009
 * Time: 6:15:35 PM
 */
interface LockStrategy {
    public static final int NO_LOCK = 0;
    public static final int SHARED = 1;
    public static final int EXCLUSIVE = 2;
    public void lock(Object owner, String lockname) throws Exception;
    public void release(Object owner, String lockname);
}