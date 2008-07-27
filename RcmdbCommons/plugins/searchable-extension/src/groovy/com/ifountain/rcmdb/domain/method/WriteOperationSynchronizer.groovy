package com.ifountain.rcmdb.domain.method
/**
 * Created by IntelliJ IDEA.
 * User: mustafa seker
 * Date: Jul 27, 2008
 * Time: 4:00:50 PM
 * To change this template use File | Settings | File Templates.
 */
class WriteOperationSynchronizer {
    public static Object writeOperationLock = new Object();
}