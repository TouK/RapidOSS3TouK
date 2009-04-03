package com.ifountain.rcmdb.transaction
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Apr 2, 2009
 * Time: 6:17:50 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ITransaction {
    public void commit();
    public void rollback();
}