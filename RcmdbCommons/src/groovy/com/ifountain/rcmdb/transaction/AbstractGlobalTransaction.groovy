package com.ifountain.rcmdb.transaction
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Apr 3, 2009
 * Time: 8:59:27 AM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractGlobalTransaction implements ITransaction{

    public void commit() {
    }

    public void rollback() {
    }

    abstract public void commitGlobalTransaction();
    abstract public void rollbackGlobalTransaction();
}