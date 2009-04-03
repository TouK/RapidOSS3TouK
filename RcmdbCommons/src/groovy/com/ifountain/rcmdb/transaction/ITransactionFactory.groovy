package com.ifountain.rcmdb.transaction
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Apr 2, 2009
 * Time: 6:17:16 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ITransactionFactory {
    public ITransaction createTransaction();
    public AbstractGlobalTransaction createGlobalTransaction();
}