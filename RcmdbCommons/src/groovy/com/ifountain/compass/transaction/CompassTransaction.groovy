package com.ifountain.compass.transaction

import com.ifountain.rcmdb.transaction.ITransaction
import org.compass.core.CompassSession
import org.compass.core.CompassTransaction

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Apr 3, 2009
* Time: 8:44:28 AM
* To change this template use File | Settings | File Templates.
*/
class CompassTransaction implements ICompassTransaction{
    private org.compass.core.CompassTransaction compassTr;
    public CompassTransaction(org.compass.core.CompassTransaction compassTr)
    {
        this.compassTr = compassTr;        
    }
    public void commit() {
        this.compassTr.commit();
        this.getSession().close();
    }

    public void rollback() {
        this.compassTr.rollback();
        this.getSession().close();
    }

    public CompassSession getSession()
    {
        return compassTr.getSession();
    }

}