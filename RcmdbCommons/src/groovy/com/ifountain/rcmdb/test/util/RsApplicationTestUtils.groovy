package com.ifountain.rcmdb.test.util
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: May 11, 2009
 * Time: 5:46:24 PM
 * To change this template use File | Settings | File Templates.
 */
class RsApplicationTestUtils {
    static def utilityPaths=[:];
   static def loadedUtilities=[:];

   static clearProcessors()
   {
       getRsApplication().getUtility("EventProcessor").beforeProcessors.clear();
       getRsApplication().getUtility("EventProcessor").afterProcessors.clear();
       getRsApplication().getUtility("ObjectProcessor").beforeProcessors.clear();
       getRsApplication().getUtility("ObjectProcessor").afterProcessors.clear();
   }
   static setToDefaultProcessors()
   {
       getRsApplication().getUtility("EventProcessor").beforeProcessors.clear();
       getRsApplication().getUtility("EventProcessor").afterProcessors.clear();
       getRsApplication().getUtility("ObjectProcessor").beforeProcessors.clear();
       getRsApplication().getUtility("ObjectProcessor").afterProcessors.clear();
   }
   static def getRsApplication()
   {
        return RsApplicationTestUtils.class.classLoader.loadClass("application.RsApplication");
   }
   static initializeRsApplicationOperations(domainClass)
   {
       CompassForTests.addOperationSupport(domainClass,RsApplicationOperationsMock);
   }
   static def clearUtilityPaths()
   {
       utilityPaths.clear();
       loadedUtilities.clear();
   }
   static def loadUtility(utilityName)
   {
        if(RsApplicationTestUtils.utilityPaths.containsKey(utilityName))
        {
             if(!loadedUtilities.containsKey(utilityName))
             {
                GroovyClassLoader loader = new GroovyClassLoader();
                loadedUtilities[utilityName]=loader.parseClass(RsApplicationTestUtils.utilityPaths[utilityName]);
             }
             return  loadedUtilities[utilityName];
        }
        return Thread.currentThread().contextClassLoader.loadClass (utilityName);
   }
}