package com.ifountain.rcmdb.test.util

import application.RsApplicationOperations

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: May 11, 2009
* Time: 5:46:51 PM
* To change this template use File | Settings | File Templates.
*/
class RsApplicationOperationsMock extends RsApplicationOperations{

    public static def getUtility(utilityName)
    {
       return RsApplicationTestUtils.loadUtility(utilityName).newInstance();
    }
}