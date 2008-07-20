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
    int numberOfUnFinishedTransactions = 0;
    int numberOfExecutedTransactions = 0;
    CompassTransaction transaction;
    public RapidCompassTransaction(CompassTransaction tr)
    {
        transaction = tr;
    }

    public void commit() throws CompassException {
        commit(false);
    }

    public synchronized  void startTransaction()
    {
        numberOfUnFinishedTransactions++;
        numberOfExecutedTransactions++;
    }

    public synchronized  int getNumberOfUnfinishedTransactions()
    {
        return numberOfUnFinishedTransactions;        
    }

    public synchronized  int getNumberOfExecutedTransactions()
    {
        return numberOfExecutedTransactions;
    }

    public synchronized  void commit(boolean finishTr) throws CompassException {
        numberOfUnFinishedTransactions--;
        if(finishTr)
        {
            transaction.commit();
        }
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
}
