package com.ifountain.rcmdb.test.util
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Apr 22, 2009
 * Time: 9:31:40 AM
 * To change this template use File | Settings | File Templates.
 */

class RsUtilityOperationsMock  extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation{
    public static def getUtility(utilityName)
    {
        return Thread.currentThread().contextClassLoader.loadClass (utilityName);
    }
}