package com.ifountain.rcmdb.test.util
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Apr 21, 2009
 * Time: 3:14:57 PM
 * To change this template use File | Settings | File Templates.
 */
class RsUtilityTestUtils {
   static def utilityPaths=[:];
   static def loadedUtilities=[:];

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
   static initializeRsUtilityOperations(domainClass)
   {
       CompassForTests.addOperationSupport(domainClass,RsUtilityOperationsMock);
   }
   static def clearUtilityPaths()
   {
       utilityPaths.clear();
       loadedUtilities.clear();
   }
   static def loadUtility(utilityName)
   {
        if(RsUtilityTestUtils.utilityPaths.containsKey(utilityName))
        {
             if(!loadedUtilities.containsKey(utilityName))
             {
                GroovyClassLoader loader = new GroovyClassLoader();
                loadedUtilities[utilityName]=loader.parseClass(RsUtilityTestUtils.utilityPaths[utilityName]);
             }
             return  loadedUtilities[utilityName];
        }
        return Thread.currentThread().contextClassLoader.loadClass (utilityName);
   }
}
