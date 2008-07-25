package com.ifountain.compass;

import org.compass.core.CompassTransaction;
import org.compass.core.CompassException;
import org.compass.core.CompassSession;

/**
 * Created by IntelliJ IDEA.
 * User: mustafa seker
 * Date: Jul 20, 2008
 * Time: 2:33:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class RapidCompassTransaction implements CompassTransaction{
    private static long transactionId = 0;
    CompassTransaction transaction;
    TransactionListener listener;
    long id;
    public RapidCompassTransaction(CompassTransaction tr, TransactionListener listener)
    {
        this.id = getNextTransactionId();
        transaction = tr;
        this.listener = listener;
        this.listener.transactionStarted(this);
    }

    public static synchronized long getNextTransactionId()
    {
        return transactionId++;
    }

    public void commit() throws CompassException {
        this.listener.transactionCommitted(this);
    }

    public CompassSession getSession() {
        return transaction.getSession();
    }

    public void rollback() throws CompassException {
        throw new RuntimeException("Not supported");
    }

    public boolean wasCommitted() throws CompassException {
        return transaction.wasCommitted();
    }

    public boolean wasRolledBack() throws CompassException {
        return transaction.wasRolledBack();
    }

    public boolean equals(Object obj) {
        RapidCompassTransaction other = (RapidCompassTransaction)obj;

        return other.transaction == transaction;    //To change body of overridden methods use File | Settings | File Templates.
    }
}
