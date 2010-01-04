package com.ifountain.rcmdb.test.util
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: May 11, 2009
 * Time: 5:46:24 PM
 * To change this template use File | Settings | File Templates.
 */
class RapidApplicationTestUtils {
    static def utilityPaths=[:];
   static def loadedUtilities=[:];

   static clearProcessors()
   {
       getRapidApplication().getUtility("EventProcessor").beforeProcessors.clear();
       getRapidApplication().getUtility("EventProcessor").afterProcessors.clear();
       getRapidApplication().getUtility("ObjectProcessor").beforeProcessors.clear();
       getRapidApplication().getUtility("ObjectProcessor").afterProcessors.clear();
   }
   static setToDefaultProcessors()
   {
       getRapidApplication().getUtility("EventProcessor").beforeProcessors.clear();
       getRapidApplication().getUtility("EventProcessor").afterProcessors.clear();
       getRapidApplication().getUtility("ObjectProcessor").beforeProcessors.clear();
       getRapidApplication().getUtility("ObjectProcessor").afterProcessors.clear();
   }
   static def getRapidApplication()
   {
        return RapidApplicationTestUtils.class.classLoader.loadClass("application.RapidApplication");
   }
   static initializeRapidApplicationOperations(domainClass)
   {
       CompassForTests.addOperationSupport(domainClass,RapidApplicationOperationsMock);
   }
   static def clearUtilityPaths()
   {
       utilityPaths.clear();
       loadedUtilities.clear();
   }
   static def loadUtility(utilityName)
   {
        if(RapidApplicationTestUtils.utilityPaths.containsKey(utilityName))
        {
             if(!loadedUtilities.containsKey(utilityName))
             {
                GroovyClassLoader loader = new GroovyClassLoader();
                loadedUtilities[utilityName]=loader.parseClass(RapidApplicationTestUtils.utilityPaths[utilityName]);
             }
             return  loadedUtilities[utilityName];
        }
        return Thread.currentThread().contextClassLoader.loadClass (utilityName);
   }
}