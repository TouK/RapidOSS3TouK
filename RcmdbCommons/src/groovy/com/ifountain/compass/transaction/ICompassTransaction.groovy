package com.ifountain.compass.transaction

import com.ifountain.rcmdb.transaction.ITransaction
import org.compass.core.CompassSession

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Apr 3, 2009
* Time: 9:32:37 AM
* To change this template use File | Settings | File Templates.
*/
interface ICompassTransaction extends ITransaction{
    public CompassSession getSession();
}