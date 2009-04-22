package com.ifountain.rcmdb.test.util
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Apr 21, 2009
 * Time: 3:14:57 PM
 * To change this template use File | Settings | File Templates.
 */
class RsUtilityTestUtils {
   static clearProcessors()
   {
       getRsUtility().getUtility("EventProcessor").clearProcessors();
       getRsUtility().getUtility("ObjectProcessor").clearProcessors();
   }
   static setToDefaultProcessors()
   {
       getRsUtility().getUtility("EventProcessor").setToDefaultProcessors();
       getRsUtility().getUtility("ObjectProcessor").setToDefaultProcessors();
   }
   static def getRsUtility()
   {
        return RsUtilityTestUtils.class.classLoader.loadClass("RsUtility");
   }
}

class RsUtilityOperationsMock  extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation{
    public static def getUtility(utilityName)
    {
        return Thread.currentThread().contextClassLoader.loadClass (utilityName);        
    }
}