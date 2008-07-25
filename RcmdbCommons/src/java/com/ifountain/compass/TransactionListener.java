package com.ifountain.compass;

import org.compass.core.CompassTransaction;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 25, 2008
 * Time: 5:56:06 PM
 * To change this template use File | Settings | File Templates.
 */
public interface TransactionListener {
    public void transactionStarted(RapidCompassTransaction tr);
    public void transactionCommitted(RapidCompassTransaction tr);
    public void transactionRolledback(RapidCompassTransaction tr);
}
