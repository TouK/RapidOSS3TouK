package solutionTests.activeActiveRedundancy


import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.RapidApplicationTestUtils
import application.RapidApplication
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.util.ExecutionContextManagerUtils
import com.ifountain.rcmdb.test.util.scripting.ScriptManagerForTest
import auth.RsUser
import datasource.HttpDatasource
import com.ifountain.comp.test.util.logging.TestLogUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 17, 2010
* Time: 1:58:23 PM
* To change this template use File | Settings | File Templates.
*/
class ActiveActiveRedundancyTest extends RapidCmdbWithCompassTestCase{
     def RsMessageRule;
     def RsMessageRuleOperations;
     def MapGroup;
     def MapGroupOperations;
     def SearchQuery;
     def SearchQueryOperations;
     def RsUserInformation;
     def RsUserInformationOperations;
     def RsLookup;
     
     def DeletedObjects;
     def redundancyUtility;

     public void setUp() {
        super.setUp();
        def solutionPath = getWorkspacePath() + "/RapidModules/RapidInsight/solutions/ActiveActiveRedundancy"

        RsLookup=gcl.parseClass(new File("${getWorkspacePath()}/RapidModules/RapidInsight/grails-app/domain/RsLookup.groovy"));
        
        RsMessageRule=gcl.parseClass(new File("${solutionPath}/grails-app/domain/message/RsMessageRule.groovy"));
        MapGroup=gcl.parseClass(new File("${solutionPath}/grails-app/domain/ui/map/MapGroup.groovy"));
        SearchQuery=gcl.parseClass(new File("${solutionPath}/plugins/searchable-extension/grails-app/domain/search/SearchQuery.groovy"));
        RsUserInformation=gcl.parseClass(new File("${solutionPath}/plugins/jsecurity-0.2.1/grails-app/domain/auth/RsUserInformation.groovy"));

        DeletedObjects=gcl.parseClass(new File("${solutionPath}/grails-app/domain/DeletedObjects.groovy"));

        RsMessageRuleOperations=gcl.parseClass(new File("${solutionPath}/operations/message/RsMessageRuleOperations.groovy"));
        MapGroupOperations=gcl.parseClass(new File("${solutionPath}/operations/ui/map/MapGroupOperations.groovy"));
        SearchQueryOperations=gcl.parseClass(new File("${solutionPath}/operations/search/SearchQueryOperations.groovy"));
        RsUserInformationOperations=gcl.parseClass(new File("${solutionPath}/operations/auth/RsUserInformationOperations.groovy"));

        redundancyUtility=gcl.parseClass(new File("${solutionPath}/operations/RedundancyUtility.groovy"));

        initialize([RsMessageRule,MapGroup,SearchQuery,RsUser,RsUserInformation,DeletedObjects,HttpDatasource,RsLookup], []);

        CompassForTests.addOperationSupport(RsMessageRule, RsMessageRuleOperations);
        CompassForTests.addOperationSupport(MapGroup, MapGroupOperations);
        CompassForTests.addOperationSupport(SearchQuery, SearchQueryOperations);
        CompassForTests.addOperationSupport(RsUserInformation, RsUserInformationOperations);

        RapidApplicationTestUtils.initializeRapidApplicationOperations(RapidApplication);
        RapidApplicationTestUtils.utilityPaths = ["RedundancyUtility": new File("${solutionPath}/operations/RedundancyUtility.groovy")];
    }

    public void tearDown() {
           super.tearDown();
    }

    public void testRedundancyUtility_SetsIsLocalPropertyAccordingToExecutionContext(){
        def rule1=RsMessageRule.add(searchQueryId:1,destinationType:"email",userId:2);
        assertFalse(rule1.hasErrors());
        assertEquals(true,rule1.isLocal);

        ExecutionContextManagerUtils.executeInContext ([:])
        {
            ExecutionContextManagerUtils.addObjectToCurrentContext("isRemote",true);
            def rule2=null;
            try{

                rule2=RsMessageRule.add(searchQueryId:2,destinationType:"email3",userId:2);
                assertFalse(rule2.hasErrors());
                assertEquals(false,rule2.isLocal);

                rule1.update(enabled:!(rule1.enabled));
                assertFalse(rule1.hasErrors());
                assertEquals(false,rule1.isLocal);
            }
            finally{
               ExecutionContextManagerUtils.removeObjectFromCurrentContext ("isRemote");
            }

            rule1.update(enabled:!(rule1.enabled));
            assertFalse(rule1.hasErrors());
            assertEquals(true,rule1.isLocal);

            rule2.update(enabled:!(rule2.enabled));
            assertFalse(rule2.hasErrors());
            assertEquals(true,rule1.isLocal);
        }

   }

    public void testRedundancyUtility_OnlySavesADeletedObjectOnLocalDelete(){

        ExecutionContextManagerUtils.executeInContext ([:])
        {
            def rule1=RsMessageRule.add(searchQueryId:1,destinationType:"email",userId:2);
            assertFalse(rule1.hasErrors());
            rule1.remove();

            assertEquals(0,RsMessageRule.count());
            assertEquals(1,DeletedObjects.count());

            def deletedObject1=DeletedObjects.list()[0];
            assertEquals("message.RsMessageRule",deletedObject1.modelName);
            assertEquals(redundancyUtility.getKeySearchQueryForObject("message.RsMessageRule",rule1),deletedObject1.searchQuery);
            deletedObject1.remove();
            assertEquals(0,DeletedObjects.count());

            ExecutionContextManagerUtils.addObjectToCurrentContext("isRemote",true);
            def rule2=null;
            try{

                rule2=RsMessageRule.add(searchQueryId:2,destinationType:"email3",userId:2);
                assertFalse(rule2.hasErrors());
                rule2.remove();
                assertEquals(0,RsMessageRule.count());

                assertEquals(0,DeletedObjects.count());
            }
            finally{
               ExecutionContextManagerUtils.removeObjectFromCurrentContext ("isRemote");
            }
        }

   }

   public void testRedundancyUtility_GetKeySearchQueryForObject()
   {
        def rule1=RsMessageRule.add(searchQueryId:1,destinationType:"email",userId:2);
        assertFalse(rule1.hasErrors());

        assertEquals("""destinationType:"(email)" searchQueryId:1 userId:2 """,redundancyUtility.getKeySearchQueryForObject("message.RsMessageRule",rule1));

        def mapGroup1=MapGroup.add(username:"user1",groupName:"group1");
        assertFalse(mapGroup1.hasErrors());

        assertEquals("""groupName:"(group1)" username:"(user1)" """,redundancyUtility.getKeySearchQueryForObject("ui.map.MapGroup",mapGroup1));
        
   }
   void initializeScriptManager()
    {
        def base_directory = getWorkspacePath() + "/RapidModules/RapidInsight/solutions/ActiveActiveRedundancy/scripts"
        println "base path is :" + new File(base_directory).getCanonicalPath();
        ScriptManagerForTest.initialize(gcl, base_directory);
    }
    
   public void testUpdatedObjectsScriptReturnsObjectsAndRelationsAndIdRelationsAsXml()
   {
        initializeScriptManager();
        ScriptManagerForTest.addScript("updatedObjects");

        def requestParams=[:];
        requestParams.format="xml";
        requestParams.sort="rsUpdatedAt";
        requestParams.order="asc";
        requestParams.searchIn="auth.RsUserInformation";
        requestParams.max="2";
        requestParams.query="alias:*";
        requestParams.offset="1";


        def user=RsUser.add(username:"user1",passwordHash:"");
        assertFalse(user.errors.toString(),user.hasErrors());

        2.times{
             def info=RsUserInformation.add(rsUser:user,userId:user.id,type:"email${it}");
             assertFalse(info.hasErrors());
        }

        2.times{
             def info=RsUserInformation.add(userId:user.id+5000,type:"email2_${it}");
             assertFalse(info.hasErrors());
        }
        assertEquals(4,RsUserInformation.count());

        //TEST WITH RELATIONS OFF
        def scriptResult=ScriptManagerForTest.runScript("updatedObjects",[params:requestParams]);
        def data=getUpdatedObjectScriptResultAsMap(scriptResult);
        def objects=data.objects;

        assertEquals("4",data.topRow.total);
        assertEquals("1",data.topRow.offset);
        //check max parameter, and ordering, and offset
        assertEquals(2,objects.size());
        assertTrue(Integer.parseInt(objects[1].id)>Integer.parseInt(objects[0].id))
        assertEquals("email1",objects[0].type);

        //check row data, with IdRelatedObject
        def info2=RsUserInformation.searchEvery("type:email1")[0];

        assertEquals("email1",objects[0].type);
        assertEquals("auth.RsUserInformation",objects[0].alias);
        assertEquals("true",objects[0].isLocal);
        assertEquals(info2.id.toString(),objects[0].id);
        assertEquals(info2.rsInsertedAt.toString(),objects[0].rsInsertedAt);
        assertEquals(info2.rsUpdatedAt.toString(),objects[0].rsUpdatedAt);
        assertEquals(user.id.toString(),objects[0].userId);

        assertEquals(1,objects[0].IdRelatedObjects.size());
        assertEquals("auth.RsUser",objects[0].IdRelatedObjects[0].alias);
        assertEquals(redundancyUtility.getKeySearchQueryForObject("auth.RsUser",user),objects[0].IdRelatedObjects[0].searchQuery);
        assertEquals("userId",objects[0].IdRelatedObjects[0].relationName);

        assertEquals(0,objects[0].RelatedObjects.size());

        //check another row, witohout IdRelatedObject
        assertEquals("email2_0",objects[1].type);
        assertEquals("auth.RsUserInformation",objects[1].alias);
        assertEquals("true",objects[1].isLocal);
        assertEquals((user.id+5000).toString(),objects[1].userId);

        assertEquals(0,objects[1].IdRelatedObjects.size());
        assertEquals(0,objects[1].RelatedObjects.size());

        //TEST WITH RELATIONS ON
        requestParams.withRelations=true;
        scriptResult=ScriptManagerForTest.runScript("updatedObjects",[params:requestParams]);
        data=getUpdatedObjectScriptResultAsMap(scriptResult);
        objects=data.objects;

        assertEquals("4",data.topRow.total);
        assertEquals("1",data.topRow.offset);

        assertEquals("email1",objects[0].type);
        assertEquals("auth.RsUserInformation",objects[0].alias);

        assertEquals(1,objects[0].IdRelatedObjects.size());
        assertEquals("auth.RsUser",objects[0].IdRelatedObjects[0].alias);
        assertEquals(redundancyUtility.getKeySearchQueryForObject("auth.RsUser",user),objects[0].IdRelatedObjects[0].searchQuery);
        assertEquals("userId",objects[0].IdRelatedObjects[0].relationName);

        assertEquals(1,objects[0].RelatedObjects.size());
        assertEquals("auth.RsUser",objects[0].RelatedObjects[0].alias);
        assertEquals(redundancyUtility.getKeySearchQueryForObject("auth.RsUser",user),objects[0].IdRelatedObjects[0].searchQuery);
        assertEquals("rsUser",objects[0].RelatedObjects[0].relationName);
        
        //check another row, witohout IdRelatedObject ,RelatedObject
        assertEquals("email2_0",objects[1].type);
        assertEquals("auth.RsUserInformation",objects[1].alias);

        assertEquals(0,objects[1].IdRelatedObjects.size());
        assertEquals(0,objects[1].RelatedObjects.size());

   }

   def getUpdatedObjectScriptResultAsMap(scriptResult)
   {
        println scriptResult
        def resultXml = new XmlSlurper().parseText(scriptResult);
        def objects=[];
        resultXml.Object.each{ dataRow->
            def object=dataRow.attributes();
            def idRelatedObjects=[];
            dataRow.IdRelatedObject.each{ subDataRow ->
               idRelatedObjects.add(subDataRow.attributes());
            }
            object.IdRelatedObjects=idRelatedObjects;

            
            def relatedObjects=[];
            dataRow.RelatedObject.each{ subDataRow ->
               relatedObjects.add(subDataRow.attributes());
            }
            object.RelatedObjects=relatedObjects;

            objects.add(object);
        }
        def data=[topRow:resultXml.attributes(),objects:objects];
        return data;
   }


   public void testSynchorinzeDeletedObjectsScript_LikeAcceptanceTest()
   {
        initializeScriptManager();
        ScriptManagerForTest.addScript("synchronizeDeletedObjects");
        ScriptManagerForTest.addScript("updatedObjects");

        def doRequestCallParams=[:];
        def doRequestResultFromRemoteServer="""<Objects total='0' offset='0'></Objects>""";

        HttpDatasource.metaClass.doRequest= { String url, Map params ->
             doRequestCallParams.url=url;
             doRequestCallParams.params=params;
             return doRequestResultFromRemoteServer;
        }
        def ds=HttpDatasource.add(name:"ross1");
        assertFalse(ds.hasErrors());
        //TestLogUtils.enableLogger();

        //test with no deleted object
        assertEquals(0,doRequestCallParams.size());
        def scriptResult=ScriptManagerForTest.runScript("synchronizeDeletedObjects",[:]);
        println "synchronizeDeletedObjects result"+scriptResult.replaceAll("<br>","\n");
        assertTrue(scriptResult.indexOf("Error")<0);

        assertEquals(2,doRequestCallParams.size());
        assertEquals("script/run/updatedObjects",doRequestCallParams.url);
        assertEquals("rsadmin",doRequestCallParams.params.login);
        assertEquals("changeme",doRequestCallParams.params.password);
        assertEquals("xml",doRequestCallParams.params.format);
        assertEquals("rsUpdatedAt",doRequestCallParams.params.sort);
        assertEquals("asc",doRequestCallParams.params.order);
        assertEquals("DeletedObjects",doRequestCallParams.params.searchIn);
        assertEquals(100,doRequestCallParams.params.max);
        assertEquals("rsUpdatedAt:[0 TO *] ",doRequestCallParams.params.query);
        assertEquals(0,doRequestCallParams.params.offset);

        assertEquals(0,RsLookup.count());

        //test with some deleted objects

        //add objects and delete them then get UpdatedObjects xml for DeletedObject in order to mock remote server
        def searchQuery=SearchQuery.add(name:"query1",username:"user1",type:"t1",query:"asd");
        def rsUserInformation=RsUserInformation.add(userId:5,type:"info1");
        
        //remove this objects
        assertEquals(1,SearchQuery.count());
        assertEquals(1,RsUserInformation.count());
        searchQuery.remove();
        rsUserInformation.remove();
        assertEquals(0,SearchQuery.count());
        assertEquals(0,RsUserInformation.count());
        assertEquals(2,DeletedObjects.count());

        //add object again
        searchQuery=SearchQuery.add(name:"query1",username:"user1",type:"t1",query:"asd");
        assertFalse(searchQuery.hasErrors());
        rsUserInformation=RsUserInformation.add(userId:5,type:"info1");
        assertFalse(rsUserInformation.hasErrors());

        assertEquals(1,SearchQuery.count());
        assertEquals(1,RsUserInformation.count());
        assertEquals(2,DeletedObjects.count());

        def lastDeletedObject=DeletedObjects.search("alias:*",[sort:"id",order:"desc"]).results[0];

        def requestParams=[:];
        requestParams.format="xml";
        requestParams.sort="rsUpdatedAt";
        requestParams.order="asc";
        requestParams.searchIn="DeletedObjects";
        requestParams.max="100";
        requestParams.query="alias:*";
        requestParams.offset="0";

        doRequestResultFromRemoteServer=ScriptManagerForTest.runScript("updatedObjects",[params:requestParams]);
        println "updatedObjects xml result from remote : ${doRequestResultFromRemoteServer}";
        DeletedObjects.removeAll();
        assertEquals(0,DeletedObjects.count());

        ExecutionContextManagerUtils.executeInContext ([:])
        {
            ExecutionContextManagerUtils.addObjectToCurrentContext("isRemote",true);
            try{
                doRequestCallParams.clear();
                assertEquals(0,doRequestCallParams.size());
                scriptResult=ScriptManagerForTest.runScript("synchronizeDeletedObjects",[:]);
                println "synchronizeDeletedObjects result"+scriptResult.replaceAll("<br>","\n");
                assertTrue(scriptResult.indexOf("Error")<0);
                assertEquals(2,doRequestCallParams.size());
            }
            finally{
                ExecutionContextManagerUtils.removeObjectFromCurrentContext ("isRemote");
            }
        }

        assertEquals(0,SearchQuery.count());
        assertEquals(0,RsUserInformation.count());
        assertEquals("Delete is remote no DeletedObjects should be added",0,DeletedObjects.count());

        assertEquals(1,RsLookup.count());
        def lookup=RsLookup.get(name:"DeletedObjects_ross1_UpdatedAt");
        assertNotNull (lookup);
        assertEquals(lastDeletedObject.rsUpdatedAt.toString(),lookup.value);

        //calling again will change query
        doRequestCallParams.clear();
        assertEquals(0,doRequestCallParams.size());
        scriptResult=ScriptManagerForTest.runScript("synchronizeDeletedObjects",[:]);
        println "synchronizeDeletedObjects result"+scriptResult.replaceAll("<br>","\n");
        assertTrue(scriptResult.indexOf("Error")<0);
        assertEquals(2,doRequestCallParams.size());

        assertEquals(2,doRequestCallParams.size());
        assertEquals("script/run/updatedObjects",doRequestCallParams.url);
        assertEquals("DeletedObjects",doRequestCallParams.params.searchIn);
        assertEquals("rsUpdatedAt:[${lastDeletedObject.rsUpdatedAt} TO *] ",doRequestCallParams.params.query);
        assertEquals(0,doRequestCallParams.params.offset);
   }
}