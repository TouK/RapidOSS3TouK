package com.ifountain.compass.transaction

import com.ifountain.rcmdb.transaction.AbstractGlobalTransaction

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Apr 3, 2009
* Time: 9:26:47 AM
* To change this template use File | Settings | File Templates.
*/
class CompassGlobalTransaction extends AbstractGlobalTransaction implements ICompassTransaction{
    private CompassTransaction tr;
    public CompassGlobalTransaction(org.compass.core.CompassTransaction compassTr)
    {
        tr = new CompassTransaction(compassTr);
    }

    public void commitGlobalTransaction() {
        tr.commit();
    }

    public void rollbackGlobalTransaction() {
        tr.rollback()
    }

    public org.compass.core.CompassSession getSession()
    {
        return tr.getSession();
    }

}