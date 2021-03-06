package solutionTests.activeActiveRedundancy


import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.RapidApplicationTestUtils
import application.RapidApplication
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.util.ExecutionContextManagerUtils
import message.RsMessageRule

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 17, 2010
* Time: 1:58:23 PM
* To change this template use File | Settings | File Templates.
*/
class RedundancyUtilityTest extends RapidCmdbWithCompassTestCase{

     def RsMessageRuleOperations;
     def MapGroupOperations;
     def SearchQueryOperations;
     def SearchQueryGroupOperations;
     def RsUserInformationOperations;


     def DeletedObjects;
     def UpdatedObjects;
     def redundancyUtility;


     def static solutionPath;


     public void setUp() {
        super.setUp();
        clearMetaClasses();
        solutionPath = getWorkspacePath() + "/RapidModules/RapidInsight/solutions/ActiveActiveRedundancy"



        gcl.addClasspath("${solutionPath}/grails-app/domain/".toString());

        DeletedObjects=gcl.loadClass("DeletedObjects");
        UpdatedObjects=gcl.loadClass("UpdatedObjects");


        RsMessageRuleOperations=gcl.parseClass(new File("${solutionPath}/operations/message/RsMessageRuleOperations.groovy"));

        redundancyUtility=gcl.parseClass(new File("${solutionPath}/operations/RedundancyUtility.groovy"));

        initialize([RsMessageRule,DeletedObjects,UpdatedObjects], []);

        CompassForTests.addOperationSupport(RsMessageRule, RsMessageRuleOperations);

        RapidApplicationTestUtils.initializeRapidApplicationOperations(RapidApplication);
        RapidApplicationTestUtils.utilityPaths = ["RedundancyUtility": new File("${solutionPath}/operations/RedundancyUtility.groovy")];
    }

    public void tearDown() {
        clearMetaClasses();
        super.tearDown();
    }
    public static void clearMetaClasses()
    {
        ExpandoMetaClass.disableGlobally();
        //GroovySystem.metaClassRegistry.removeMetaClass(HttpDatasource);
        ExpandoMetaClass.enableGlobally();
    }
    public void testRedundancyUtility_SavesAndDeletesUpdatedObjectsAccordingToExecutionContext(){
        def rule1=RsMessageRule.add(searchQueryId:1,destinationType:"email",users:"testuser");
        assertFalse(rule1.hasErrors());
        assertEquals(1,UpdatedObjects.count());
        assertEquals(1,UpdatedObjects.countHits("modelName:message.RsMessageRule AND objectId:${rule1.id}"));
        

        ExecutionContextManagerUtils.executeInContext ([:])
        {
            ExecutionContextManagerUtils.addObjectToCurrentContext("isRemote",true);
            def rule2=null;
            try{
                rule2=RsMessageRule.add(searchQueryId:2,destinationType:"email3",users:"testuser");
                assertFalse(rule2.hasErrors());
                assertEquals("UpdatedObjects not deleted, because other object is updated",1,UpdatedObjects.count());
                assertEquals("UpdatedObjects not deleted, because other object is updated",1,UpdatedObjects.countHits("modelName:message.RsMessageRule AND objectId:${rule1.id}"));

                rule1.update(enabled:!(rule1.enabled));
                assertFalse(rule1.hasErrors());
                assertEquals("UpdatedObjects are deleted, because of remote update",0,UpdatedObjects.count());
            }
            finally{
               ExecutionContextManagerUtils.removeObjectFromCurrentContext ("isRemote");
            }
            UpdatedObjects.removeAll();

            rule1.update(enabled:!(rule1.enabled));
            assertFalse(rule1.hasErrors());
            assertEquals(1,UpdatedObjects.countHits("modelName:message.RsMessageRule AND objectId:${rule1.id}"));

            UpdatedObjects.removeAll();

            rule2.update(enabled:!(rule2.enabled));
            assertFalse(rule2.hasErrors());
            assertEquals(1,UpdatedObjects.countHits("modelName:message.RsMessageRule AND objectId:${rule2.id}"));
        }

   }
   public void testRedundancyUtility_UpdatesUpdatedObjectsRecordWhenObjectUpdatedMultipleTimes()
   {
        def rule1=RsMessageRule.add(searchQueryId:1,destinationType:"email",users:"testuser",enabled:false);
        assertFalse(rule1.hasErrors());
        assertEquals(1,UpdatedObjects.count());
        assertEquals(1,UpdatedObjects.countHits("modelName:message.RsMessageRule AND objectId:${rule1.id}"));
        def updatedObjectRecord=UpdatedObjects.list()[0];

        
        rule1.update(enabled:true);
        assertFalse(rule1.hasErrors());
        assertEquals(1,UpdatedObjects.count());
        assertEquals(1,UpdatedObjects.countHits("modelName:message.RsMessageRule AND objectId:${rule1.id}"));
        def newUpdatedObjectRecord=UpdatedObjects.list()[0];

        assertTrue(newUpdatedObjectRecord.rsUpdatedAt>updatedObjectRecord.rsUpdatedAt);
        
   }
    public void testRedundancyUtility_AddsADeletedObjectRecordWhenKeyPropssAreChanged()
   {
        def rule1=RsMessageRule.add(searchQueryId:1,destinationType:"email",users:"testuser");
        assertFalse(rule1.hasErrors());
        assertEquals(0,DeletedObjects.count());
        assertEquals(1,UpdatedObjects.count());
        assertEquals(1,UpdatedObjects.countHits("modelName:message.RsMessageRule AND objectId:${rule1.id}"));
        def updatedObjectRecord=UpdatedObjects.list()[0];

        def searchQueryForRule=application.RapidApplication.getUtility("RedundancyUtility").getKeySearchQueryForObject("message.RsMessageRule",rule1);
        
        rule1.update(users:"testuser2");
        assertFalse(rule1.hasErrors());


        def searchQueryForNewRule=application.RapidApplication.getUtility("RedundancyUtility").getKeySearchQueryForObject("message.RsMessageRule",rule1);
        assertTrue(searchQueryForRule.compareTo(searchQueryForNewRule)!=0);
        
        assertEquals(1,DeletedObjects.count());
        assertEquals(1,DeletedObjects.countHits("modelName:message.RsMessageRule AND searchQuery:${searchQueryForRule.exactQuery()}"));

        assertEquals(1,UpdatedObjects.count());
        assertEquals(1,UpdatedObjects.countHits("modelName:message.RsMessageRule AND objectId:${rule1.id}"));
        
        def newUpdatedObjectRecord=UpdatedObjects.list()[0];
        assertTrue(newUpdatedObjectRecord.rsUpdatedAt>updatedObjectRecord.rsUpdatedAt);



   }
   public void testRemoveDeletesUpdatedObjectsRecordsAndUpdateDeletesDeletedObjectsRecordForTheSameObject()
   {
       def rule1=RsMessageRule.add(searchQueryId:1,destinationType:"email",users:"testuser");
       assertFalse(rule1.hasErrors());
       assertEquals(1,UpdatedObjects.count());
       assertEquals(1,UpdatedObjects.countHits("modelName:message.RsMessageRule AND objectId:${rule1.id}"));

       //
       rule1.remove();
       assertEquals(0,RsMessageRule.count());
       assertEquals(0,UpdatedObjects.count());
       def searchQueryForRule2=application.RapidApplication.getUtility("RedundancyUtility").getKeySearchQueryForObject("message.RsMessageRule",rule1);
       assertEquals(1,DeletedObjects.count());
       assertEquals(1,DeletedObjects.countHits("modelName:message.RsMessageRule AND searchQuery:${searchQueryForRule2.exactQuery()}"));


       def newRule1=RsMessageRule.add(searchQueryId:1,destinationType:"email",users:"testuser");
       assertFalse(newRule1.id == rule1.id);
       assertEquals(1,UpdatedObjects.count());
       assertEquals(1,UpdatedObjects.countHits("modelName:message.RsMessageRule AND objectId:${newRule1.id}"));
       assertEquals(0,DeletedObjects.count());
   }

    public void testRedundancyUtility_OnlySavesADeletedObjectOnLocalDelete(){

        ExecutionContextManagerUtils.executeInContext ([:])
        {
            def rule1=RsMessageRule.add(searchQueryId:1,destinationType:"email",users:"testuser");
            assertFalse(rule1.hasErrors());
            assertEquals(1,UpdatedObjects.countHits("modelName:message.RsMessageRule AND objectId:${rule1.id}"));
            
            rule1.remove();

            assertEquals(0,RsMessageRule.count());
            assertEquals("Remove should delete updated object",0,UpdatedObjects.count());
            assertEquals(1,DeletedObjects.count());

            def deletedObject1=DeletedObjects.list()[0];
            assertEquals("message.RsMessageRule",deletedObject1.modelName);
            assertEquals(redundancyUtility.getKeySearchQueryForObject("message.RsMessageRule",rule1),deletedObject1.searchQuery);
            deletedObject1.remove();
            assertEquals(0,DeletedObjects.count());

            ExecutionContextManagerUtils.addObjectToCurrentContext("isRemote",true);
            def rule2=null;
            try{

                rule2=RsMessageRule.add(searchQueryId:2,destinationType:"email3",users:"testuser");
                assertFalse(rule2.hasErrors());
                assertEquals(0,UpdatedObjects.count());
                UpdatedObjects.add(modelName:rule2.class.name,objectId:rule2.id);
                assertEquals(1,UpdatedObjects.count());
                                
                rule2.remove();
                assertEquals(0,RsMessageRule.count());
                assertEquals(0,UpdatedObjects.count());
                assertEquals(0,DeletedObjects.count());
            }
            finally{
               ExecutionContextManagerUtils.removeObjectFromCurrentContext ("isRemote");
            }
        }

   }



   public void testRedundancyUtility_GetKeySearchQueryForObject()
   {
        def rule1=RsMessageRule.add(searchQueryId:1,destinationType:"email",users:"testuser",groups:"group1");
        assertFalse(rule1.hasErrors());

        assertEquals("""destinationType:"(email)" groups:"(group1)" searchQueryId:1 users:"(testuser)" """,redundancyUtility.getKeySearchQueryForObject("message.RsMessageRule",rule1));

   }
   

   
}