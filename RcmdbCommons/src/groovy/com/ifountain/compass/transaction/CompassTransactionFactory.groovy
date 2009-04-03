package com.ifountain.compass.transaction

import com.ifountain.rcmdb.transaction.ITransactionFactory
import com.ifountain.rcmdb.transaction.ITransaction
import com.ifountain.rcmdb.transaction.AbstractGlobalTransaction
import com.ifountain.rcmdb.transaction.AbstractGlobalTransaction
import org.compass.core.Compass
import org.compass.core.CompassSession

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Apr 3, 2009
* Time: 8:43:53 AM
* To change this template use File | Settings | File Templates.
*/
public class CompassTransactionFactory implements ITransactionFactory{
    private Compass compass;
    public  CompassTransactionFactory(Compass compass)
    {
        this.compass = compass;        
    }
    public ITransaction createTransaction() {
        CompassSession session = this.compass.openSession()
        return new CompassTransaction(session.beginTransaction()); //To change body of implemented methods use File | Settings | File Templates.
    }

    public AbstractGlobalTransaction createGlobalTransaction() {
        CompassSession session = this.compass.openSession()
        return new CompassGlobalTransaction(session.beginTransaction()); //To change body of implemented methods use File | Settings | File Templates.
    }

}