package solutionTests.activeActiveRedundancy


import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.RapidApplicationTestUtils
import application.RapidApplication
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.util.ExecutionContextManagerUtils
import com.ifountain.rcmdb.test.util.scripting.ScriptManagerForTest
import datasource.HttpDatasource
import com.ifountain.comp.test.util.logging.TestLogUtils
import org.apache.commons.io.FileUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 17, 2010
* Time: 1:58:23 PM
* To change this template use File | Settings | File Templates.
*/
class RedundancySynchronizationScriptsTests extends RapidCmdbWithCompassTestCase{

     def SearchQuery;
     def SearchQueryOperations;
     def SearchQueryGroup;
     def SearchQueryGroupOperations;
     def RsUser;
     def RsUserInformation;
     def RsUserInformationOperations;
     def RsLookup;

     
     def DeletedObjects;
     def redundancyUtility;

     def static temp_model_directory = "../testoutput/ActiveActive/grails-app";
     def static temp_scripts_directory = "../testoutput/ActiveActive/scripts";

     def static solutionPath;


     public void setUp() {
        super.setUp();
        clearMetaClasses();
        solutionPath = getWorkspacePath() + "/RapidModules/RapidInsight/solutions/ActiveActiveRedundancy"

        generateModelFilesForTest();

        gcl.addClasspath("${temp_model_directory}/domain/".toString());
        SearchQuery=gcl.loadClass("search.SearchQueryForRedundancy") ;
        SearchQueryGroup=gcl.loadClass("search.SearchQueryGroupForRedundancy") ;
        RsUserInformation=gcl.loadClass("auth.RsUserInformationForRedundancy") ;
        RsUser=gcl.loadClass("auth.RsUserForRedundancy");

        RsLookup=gcl.loadClass("RsLookup");
        DeletedObjects=gcl.loadClass("DeletedObjects");

        SearchQueryOperations=gcl.parseClass(new File("${solutionPath}/operations/search/SearchQueryOperations.groovy"));
        SearchQueryGroupOperations=gcl.parseClass(new File("${solutionPath}/operations/search/SearchQueryGroupOperations.groovy"));
        RsUserInformationOperations=gcl.parseClass(new File("${solutionPath}/operations/auth/RsUserInformationOperations.groovy"));


        redundancyUtility=gcl.parseClass(new File("${solutionPath}/operations/RedundancyUtility.groovy"));

        initialize([SearchQuery,SearchQueryGroup,RsUser,RsUserInformation,DeletedObjects,HttpDatasource,RsLookup], []);

        CompassForTests.addOperationSupport(SearchQuery, SearchQueryOperations);
        CompassForTests.addOperationSupport(SearchQueryGroup, SearchQueryGroupOperations);
        CompassForTests.addOperationSupport(RsUserInformation, RsUserInformationOperations);

        RapidApplicationTestUtils.initializeRapidApplicationOperations(RapidApplication);
        RapidApplicationTestUtils.utilityPaths = ["RedundancyUtility": new File("${solutionPath}/operations/RedundancyUtility.groovy")];
    }
     //Copy the models from solution and replate model names with modelNameForRedundancy
     //If renaming not done test fail in hudson or when multiple tests runned together.
     protected void generateModelFilesForTest()
     {
        def markerFile=new File("${temp_model_directory}/generatedOnce.mark");
        def generatedModels=false;
        if(!markerFile.exists())
        {
            generatedModels=true;
        }
        else
        {
            def lastGenerationTime=Long.parseLong(markerFile.getText().trim());
            //60 seconds timeout
            if(System.currentTimeMillis()-lastGenerationTime>60000)
            {
               generatedModels=true;
            }
            else
            {
                println "will not generate models because generation is fresh, not older than 60 seconds";
            }
        }
        if(generatedModels)
        {
            println "Will generate models";

            deleteGeneratedModelsDirectory();
            new File(temp_model_directory).mkdirs();

            generateScriptFilesForTest();
            
            FileUtils.copyDirectory (new File("${solutionPath}/grails-app"),new File(temp_model_directory));
            FileUtils.copyDirectory (new File("${solutionPath}/plugins/jsecurity-0.2.1/grails-app"),new File(temp_model_directory));
            FileUtils.copyDirectory (new File("${solutionPath}/plugins/searchable-extension/grails-app"),new File(temp_model_directory));
            FileUtils.copyFileToDirectory(new File("${getWorkspacePath()}/RapidModules/RcmdbCommons/plugins/jsecurity-0.2.1/grails-app/domain/auth/RsUser.groovy"),new File("${temp_model_directory}/domain/auth"));



            convertModelFileFromTempDir("SearchQuery","${temp_model_directory}/domain/search/");
            convertModelFileFromTempDir("SearchQueryGroup","${temp_model_directory}/domain/search/");
            convertModelFileFromTempDir("RsUserInformation","${temp_model_directory}/domain/auth/");
            convertModelFileFromTempDir("RsUser","${temp_model_directory}/domain/auth/");
            
            FileUtils.deleteDirectory(new File("${temp_model_directory}/domain/ui/"));
            FileUtils.deleteDirectory(new File("${temp_model_directory}/domain/message/"));

            markerFile.setText(System.currentTimeMillis().toString());
        }
     }
     protected void generateScriptFilesForTest()
     {
         new File(temp_scripts_directory).mkdirs();

         FileUtils.copyDirectory (new File("${solutionPath}/scripts"),new File(temp_scripts_directory));

         def scriptFile=new File("${temp_scripts_directory}/updatedObjects.groovy");
         def scriptText=scriptFile.getText().replaceAll("RsUser;","RsUserForRedundancy;");         
         scriptText=scriptText.replaceAll("RsUserInformation","RsUserInformationForRedundancy");
         scriptFile.setText(scriptText);

     }
     protected void deleteGeneratedModelsDirectory()
     {
        if (new File(temp_model_directory).exists())
        {
            FileUtils.deleteDirectory(new File(temp_model_directory));
            FileUtils.deleteDirectory(new File(temp_scripts_directory));
        }
     }
    private void convertModelFileFromTempDir(String modelName,String directoryPath)
    {
        def modelFile=new File(directoryPath+"/${modelName}.groovy");
        def modelText=modelFile.getText().replace("class "+modelName,"class "+modelName+"ForRedundancy");
        if(modelName == "SearchQuery")
        {
            modelText=modelText.replace("SearchQueryGroup,","SearchQueryGroupForRedundancy,");
        }
        else if(modelName=="SearchQueryGroup")
        {
            modelText=modelText.replace("SearchQuery,","SearchQueryForRedundancy,");
        }
        else if(modelName=="RsUserInformation")
        {
            modelText=modelText.replace("type:RsUser","type:RsUserForRedundancy");
        }
        else if(modelName=="RsUser")
        {
            modelText=modelText.replace("RsUserInformation","RsUserInformationForRedundancy");
        }
        def newModelFile=new File(directoryPath+"/${modelName}ForRedundancy.groovy");
        newModelFile.setText(modelText);
        FileUtils.deleteQuietly (modelFile);
    }   
    public void tearDown() {
        clearMetaClasses();
        super.tearDown();
    }
    public static void clearMetaClasses()
    {
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(HttpDatasource);
        ExpandoMetaClass.enableGlobally();
    }
   
   void initializeScriptManager()
    {                                    
        println "base path is :" + new File(temp_scripts_directory).getCanonicalPath();
        ScriptManagerForTest.initialize(gcl, temp_scripts_directory);
    }
    
   public void testUpdatedObjectsScriptReturnsObjectsAndRelationsAndIdRelationsAsXml()
   {
        initializeScriptManager();
        ScriptManagerForTest.addScript("updatedObjects");

        def requestParams=[:];
        requestParams.format="xml";
        requestParams.sort="rsUpdatedAt";
        requestParams.order="asc";
        requestParams.searchIn="auth.RsUserInformationForRedundancy";
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
        assertEquals("auth.RsUserInformationForRedundancy",objects[0].alias);
        assertEquals("true",objects[0].isLocal);
        assertEquals(info2.id.toString(),objects[0].id);
        assertEquals(info2.rsInsertedAt.toString(),objects[0].rsInsertedAt);
        assertEquals(info2.rsUpdatedAt.toString(),objects[0].rsUpdatedAt);
        assertEquals(user.id.toString(),objects[0].userId);

        assertEquals(1,objects[0].IdRelatedObjects.size());
        assertEquals("auth.RsUserForRedundancy",objects[0].IdRelatedObjects[0].alias);
        assertEquals(redundancyUtility.getKeySearchQueryForObject("auth.RsUserForRedundancy",user),objects[0].IdRelatedObjects[0].searchQuery);
        assertEquals("userId",objects[0].IdRelatedObjects[0].relationName);

        assertEquals(0,objects[0].RelatedObjects.size());

        //check another row, witohout IdRelatedObject
        assertEquals("email2_0",objects[1].type);
        assertEquals("auth.RsUserInformationForRedundancy",objects[1].alias);
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
        assertEquals("auth.RsUserInformationForRedundancy",objects[0].alias);

        assertEquals(1,objects[0].IdRelatedObjects.size());
        assertEquals("auth.RsUserForRedundancy",objects[0].IdRelatedObjects[0].alias);
        assertEquals(redundancyUtility.getKeySearchQueryForObject("auth.RsUserForRedundancy",user),objects[0].IdRelatedObjects[0].searchQuery);
        assertEquals("userId",objects[0].IdRelatedObjects[0].relationName);

        assertEquals(1,objects[0].RelatedObjects.size());
        assertEquals("auth.RsUserForRedundancy",objects[0].RelatedObjects[0].alias);
        assertEquals(redundancyUtility.getKeySearchQueryForObject("auth.RsUserForRedundancy",user),objects[0].IdRelatedObjects[0].searchQuery);
        assertEquals("rsUser",objects[0].RelatedObjects[0].relationName);
        
        //check another row, witohout IdRelatedObject ,RelatedObject
        assertEquals("email2_0",objects[1].type);
        assertEquals("auth.RsUserInformationForRedundancy",objects[1].alias);

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

   public void testSynchorinzeDeletedObjectsScript_RetrievesDeletedObjectDataFromRemoteServerUpdatedObjectsScript()
   {
        initializeScriptManager();
        ScriptManagerForTest.addScript("synchronizeDeletedObjects");

        def doRequestCallParams=[:];
        def doRequestResultFromRemoteServer="""<Objects total='0' offset='0'></Objects>""";

        HttpDatasource.metaClass.doRequest= { String url, Map params ->
             doRequestCallParams.url=url;
             doRequestCallParams.params=params;
             return doRequestResultFromRemoteServer;
        }
        def ds=HttpDatasource.add(name:"redundancy1");
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
   }
   public void testSynchorinzeDeletedObjectsScript_DeletesObjectLocallyWhichAreRemotelyDeleted()
   {
       initializeScriptManager();
        ScriptManagerForTest.addScript("synchronizeDeletedObjects");
        
        def scriptResult=null;

        def doRequestCallParams=[:];
        def doRequestResultFromRemoteServer="""<Objects total='0' offset='0'></Objects>""";

        HttpDatasource.metaClass.doRequest= { String url, Map params ->
             doRequestCallParams.url=url;
             doRequestCallParams.params=params;
             return doRequestResultFromRemoteServer;
        }
        def ds=HttpDatasource.add(name:"redundancy1");
        assertFalse(ds.hasErrors());

        //test with some deleted objects

        //add objects and delete them then get UpdatedObjects xml for DeletedObject in order to mock remote server
        def searchQuery=SearchQuery.add(name:"query1",username:"user1",type:"t1",query:"asd");
        def rsUserInformation=RsUserInformation.add(userId:5,type:"info1");

        assertEquals(1,SearchQuery.count());
        assertEquals(1,RsUserInformation.count());
        assertEquals(0,DeletedObjects.count());

        def lastDeletedObjectUpdatedAt=rsUserInformation.rsUpdatedAt+3600000;

        doRequestResultFromRemoteServer="""
        <Objects total='2' offset='0'>
          <Object alias='DeletedObjects' id='55553' modelName='search.SearchQueryForRedundancy' rsInsertedAt='${lastDeletedObjectUpdatedAt-1000}' rsUpdatedAt='${lastDeletedObjectUpdatedAt-1000}' searchQuery='name:"(query1)" type:"(t1)" username:"(user1)" ' />
          <Object alias='DeletedObjects' id='66664' modelName='auth.RsUserInformationForRedundancy' rsInsertedAt='${lastDeletedObjectUpdatedAt}' rsUpdatedAt='${lastDeletedObjectUpdatedAt}' searchQuery='type:"(info1)" userId:5 ' />
        </Objects>""";
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
        def lookup=RsLookup.get(name:"DeletedObjects_redundancy1_UpdatedAt");
        assertNotNull (lookup);
        assertEquals(lastDeletedObjectUpdatedAt.toString(),lookup.value);

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
        assertEquals("rsUpdatedAt:[${lastDeletedObjectUpdatedAt} TO *] ",doRequestCallParams.params.query);
        assertEquals(0,doRequestCallParams.params.offset);


   }
   public void testSynchronizeDeletedObjectsScript_DoesNotThrowExceptionButLogs()
   {
        initializeScriptManager();
        ScriptManagerForTest.addScript("synchronizeDeletedObjects");

        def ds=HttpDatasource.add(name:"redundancy1");
        assertFalse(ds.hasErrors());

        //test that exception is not thrown, but rather logged
        HttpDatasource.metaClass.doRequest= { String url, Map params ->
             throw new Exception("testexception");
        }

        def scriptResult=ScriptManagerForTest.runScript("synchronizeDeletedObjects",[:]);
        println "synchronizeDeletedObjects result"+scriptResult.replaceAll("<br>","\n");
        assertTrue(scriptResult.indexOf("Error")>=0);
        assertTrue(scriptResult.indexOf("testexception")>=0);
   }


   public void testSynchronizeObjectsScript_RetrievesObjectDataFromRemoteServerUpdatedObjectsScript()
   {
        initializeScriptManager();
        ScriptManagerForTest.addScript("synchronizeObjects");

        def doRequestCallParams=[:];
        def doRequestResultFromRemoteServer="""<Objects total='0' offset='0'></Objects>""";

        HttpDatasource.metaClass.doRequest= { String url, Map params ->
             doRequestCallParams.url=url;
             doRequestCallParams.params=params;
             return doRequestResultFromRemoteServer;
        }
        def ds=HttpDatasource.add(name:"redundancy1");
        assertFalse(ds.hasErrors());


        //test with no object
        assertEquals(0,doRequestCallParams.size());
        def scriptResult=ScriptManagerForTest.runScript("synchronizeObjects",[params:["modelName":"search.SearchQueryForRedundancy","withRelations":"true"]]);
        println "synchronizeObjects result"+scriptResult.replaceAll("<br>","\n");
        assertTrue(scriptResult.indexOf("Error")<0);

        assertEquals(2,doRequestCallParams.size());
        assertEquals("script/run/updatedObjects",doRequestCallParams.url);
        assertEquals("rsadmin",doRequestCallParams.params.login);
        assertEquals("changeme",doRequestCallParams.params.password);
        assertEquals("xml",doRequestCallParams.params.format);
        assertEquals("rsUpdatedAt",doRequestCallParams.params.sort);
        assertEquals("asc",doRequestCallParams.params.order);
        assertEquals("search.SearchQueryForRedundancy",doRequestCallParams.params.searchIn);
        assertEquals(100,doRequestCallParams.params.max);
        assertEquals("rsUpdatedAt:[0 TO *] AND isLocal:true",doRequestCallParams.params.query);
        assertEquals(0,doRequestCallParams.params.offset);
        assertEquals("true",doRequestCallParams.params.withRelations);

        assertEquals(0,RsLookup.count());


        //test with no relations
        doRequestCallParams.clear(); 
        assertEquals(0,doRequestCallParams.size());
        scriptResult=ScriptManagerForTest.runScript("synchronizeObjects",[params:["modelName":"search.SearchQueryForRedundancy"]]);
        println "synchronizeObjects result"+scriptResult.replaceAll("<br>","\n");
        assertTrue(scriptResult.indexOf("Error")<0);

        assertEquals(2,doRequestCallParams.size());
        assertEquals("script/run/updatedObjects",doRequestCallParams.url);
        assertEquals("search.SearchQueryForRedundancy",doRequestCallParams.params.searchIn);
        assertEquals(null,doRequestCallParams.params.withRelations);
   }

   public void testSynchonizeObjectsScript_AddsUpdatesObjectsLocallyWhichAreRemotelyUpdated()
   {
        initializeScriptManager();
        ScriptManagerForTest.addScript("synchronizeObjects");

        def remoteUpdatedAt=Date.now()+3600000;
        def scriptResult;
        
        //new object
        def doRequestResultFromRemoteServer="""<Objects total='0' offset='0'></Objects>""";
        def doRequestCallParams=[:];

        HttpDatasource.metaClass.doRequest= { String url, Map params ->
             doRequestCallParams.url=url;
             doRequestCallParams.params=params;
             return doRequestResultFromRemoteServer;
        }


        def ds=HttpDatasource.add(name:"redundancy1");
        assertFalse(ds.hasErrors());
        
        //TestLogUtils.enableLogger ();
        
        ExecutionContextManagerUtils.executeInContext ([:])
        {
            ExecutionContextManagerUtils.addObjectToCurrentContext("isRemote",true);
            try{
                doRequestResultFromRemoteServer="""
                <Objects total='1' offset='0'>
                  <Object alias='search.SearchQueryGroupForRedundancy' expanded='true' id='66661' isLocal='true' isPublic='false' name='group1' rsInsertedAt='${remoteUpdatedAt}' rsOwner='p' rsUpdatedAt='${remoteUpdatedAt}' type='t1' username='user1'>
                  </Object>
                </Objects>""";
                scriptResult=ScriptManagerForTest.runScript("synchronizeObjects",[params:["modelName":"search.SearchQueryGroupForRedundancy","withRelations":"true"]]);
                println "synchronizeObjects result"+scriptResult.replaceAll("<br>","\n");
                assertTrue(scriptResult.indexOf("Error")<0);
            }
            finally{
                ExecutionContextManagerUtils.removeObjectFromCurrentContext ("isRemote");
            }
        }

        assertEquals(1,SearchQueryGroup.count());
        def queryGroup=SearchQueryGroup.list()[0];
        assertNotNull (queryGroup);
        assertTrue("object id does not come from xml",queryGroup.id<100);
        assertEquals(true,queryGroup.expanded);
        assertEquals(false,queryGroup.isPublic);
        assertEquals("group1",queryGroup.name);
        assertEquals("t1",queryGroup.type);
        assertEquals("user1",queryGroup.username);
        assertEquals(false,queryGroup.isLocal);

        assertEquals("rsUpdatedAt:[0 TO *] AND isLocal:true",doRequestCallParams.params.query);
        

        assertEquals(1,RsLookup.count());
        def lookup=RsLookup.get(name:"search.SearchQueryGroupForRedundancy_redundancy1_UpdatedAt");
        assertEquals(remoteUpdatedAt.toString(),lookup.value);



        //test update
        remoteUpdatedAt=remoteUpdatedAt-1000;
        ExecutionContextManagerUtils.executeInContext ([:])
        {
            ExecutionContextManagerUtils.addObjectToCurrentContext("isRemote",true);
            try{
                doRequestResultFromRemoteServer="""
                <Objects total='1' offset='0'>
                  <Object alias='search.SearchQueryGroupForRedundancy' expanded='false' id='66661' isLocal='true' isPublic='true' name='group1' rsInsertedAt='${remoteUpdatedAt}' rsOwner='p' rsUpdatedAt='${remoteUpdatedAt}' type='t1' username='user1'>
                  </Object>
                </Objects>""";
                scriptResult=ScriptManagerForTest.runScript("synchronizeObjects",[params:["modelName":"search.SearchQueryGroupForRedundancy","withRelations":"true"]]);
                println "synchronizeObjects result"+scriptResult.replaceAll("<br>","\n");
                assertTrue(scriptResult.indexOf("Error")<0);
            }
            finally{
                ExecutionContextManagerUtils.removeObjectFromCurrentContext ("isRemote");
            }
        }

        queryGroup=SearchQueryGroup.list()[0];
        assertNotNull (queryGroup);
        assertEquals(false,queryGroup.expanded);
        assertEquals(true,queryGroup.isPublic);
        assertEquals(false,queryGroup.isLocal);

        assertEquals("rsUpdatedAt:[${lookup.value} TO *] AND isLocal:true",doRequestCallParams.params.query);

        assertEquals(1,RsLookup.count());
        lookup=RsLookup.get(name:"search.SearchQueryGroupForRedundancy_redundancy1_UpdatedAt");
        assertEquals(remoteUpdatedAt.toString(),lookup.value);



        //test do not update if update is not newer
        remoteUpdatedAt=queryGroup.rsUpdatedAt-100000;
        ExecutionContextManagerUtils.executeInContext ([:])
        {
            ExecutionContextManagerUtils.addObjectToCurrentContext("isRemote",true);
            try{
                doRequestResultFromRemoteServer="""
                <Objects total='1' offset='0'>
                  <Object alias='search.SearchQueryGroupForRedundancy' expanded='true' id='66661' isLocal='true' isPublic='false' name='group1' rsInsertedAt='${remoteUpdatedAt}' rsOwner='p' rsUpdatedAt='${remoteUpdatedAt}' type='t1' username='user1'>
                  </Object>
                </Objects>""";
                scriptResult=ScriptManagerForTest.runScript("synchronizeObjects",[params:["modelName":"search.SearchQueryGroupForRedundancy","withRelations":"true"]]);
                println "synchronizeObjects result"+scriptResult.replaceAll("<br>","\n");
                assertTrue(scriptResult.indexOf("Error")<0);
            }
            finally{
                ExecutionContextManagerUtils.removeObjectFromCurrentContext ("isRemote");
            }
        }

        queryGroup=SearchQueryGroup.list()[0];
        assertNotNull (queryGroup);        
        assertEquals(false,queryGroup.expanded);
        assertEquals(true,queryGroup.isPublic);
        assertEquals(false,queryGroup.isLocal);

        assertEquals("rsUpdatedAt:[${lookup.value} TO *] AND isLocal:true",doRequestCallParams.params.query);

        assertEquals(1,RsLookup.count());
        lookup=RsLookup.get(name:"search.SearchQueryGroupForRedundancy_redundancy1_UpdatedAt");
        assertEquals(remoteUpdatedAt.toString(),lookup.value);
        
   }
   public void testSynchonizeObjectsScript_AddsRemovesRelationsLocallyWhichAreRemotelyUpdated()
   {
        initializeScriptManager();
        ScriptManagerForTest.addScript("synchronizeObjects");        

        def scriptResult=null;

        def doRequestResultFromRemoteServer="""<Objects total='0' offset='0'></Objects>""";

        HttpDatasource.metaClass.doRequest= { String url, Map params ->
             return doRequestResultFromRemoteServer;
        }
        def ds=HttpDatasource.add(name:"redundancy1");
        assertFalse(ds.hasErrors());

        //TEST RELATION ADD FOR EXISTING OBJECTS
        def queryGroup=SearchQueryGroup.add(name:"group1",username:"user1",type:"t1");
        assertFalse(queryGroup.hasErrors());

        def remoteUpdatedAt=queryGroup.rsUpdatedAt+3600000;

        def searchQuery=SearchQuery.add(name:"query1",username:"user1",type:"t1",query:"asd");
        assertFalse(searchQuery.hasErrors());

        assertEquals(true,queryGroup.isLocal);
        assertEquals(true,searchQuery.isLocal);

        assertEquals(1,SearchQuery.count());
        assertEquals(1,SearchQueryGroup.count());
        queryGroup=SearchQueryGroup.get(id:queryGroup.id);
        assertEquals(0,queryGroup.queries.size());


        //sync SearchQueryGroup , this will only add relation between searchQuery and queryGroups
        // since searchQuery2 does not exist nothing for that relation and object will be done
        ExecutionContextManagerUtils.executeInContext ([:])
        {
            ExecutionContextManagerUtils.addObjectToCurrentContext("isRemote",true);
            try{
                doRequestResultFromRemoteServer="""
                <Objects total='1' offset='0'>
                  <Object alias='search.SearchQueryGroupForRedundancy' expanded='false' id='66661' isLocal='true' isPublic='false' name='group1' rsInsertedAt='${remoteUpdatedAt}' rsOwner='p' rsUpdatedAt='${remoteUpdatedAt}' type='t1' username='user1'>
                    <RelatedObject relationName='queries' alias='search.SearchQueryForRedundancy' searchQuery='name:"(query1)" type:"(t1)" username:"(user1)" ' />
                    <RelatedObject relationName='queries' alias='search.SearchQueryForRedundancy' searchQuery='name:"(query2)" type:"(t1)" username:"(user1)" ' />
                  </Object>
                </Objects>""";
                scriptResult=ScriptManagerForTest.runScript("synchronizeObjects",[params:["modelName":"search.SearchQueryGroupForRedundancy","withRelations":"true"]]);
                println "synchronizeObjects result"+scriptResult.replaceAll("<br>","\n");
                assertTrue(scriptResult.indexOf("Error")<0);
            }
            finally{
                ExecutionContextManagerUtils.removeObjectFromCurrentContext ("isRemote");
            }
        }

        
        assertEquals(1,SearchQuery.count());
        assertEquals(1,SearchQueryGroup.count());

        //objects still isLocal, they are not updated but addRelation is called.
        def newQueryGroup=SearchQueryGroup.get(id:queryGroup.id);
        assertEquals(1,newQueryGroup.queries.size());
        assertEquals(true,newQueryGroup.isLocal);

        def newSearchQuery=SearchQuery.get(id:searchQuery.id);
        assertEquals(newQueryGroup.id,newSearchQuery.group.id);
        assertEquals(true,newSearchQuery.isLocal);

        //TEST RELATION ADD FOR EXISTING AND NEW OBJECTS
        //sync SearchQuery
        // this will only add relation between searchQuery and queryGroups
        // this will add searchQuery2 and add relation between searchQuery2 and queryGroups
        ExecutionContextManagerUtils.executeInContext ([:])
        {
            ExecutionContextManagerUtils.addObjectToCurrentContext("isRemote",true);
            try{
                doRequestResultFromRemoteServer="""
                <Objects total='2' offset='0'>
                  <Object alias='search.SearchQueryForRedundancy' id='77772' isLocal='true' isPublic='false' name='query1' query='asd' rsInsertedAt='${remoteUpdatedAt}' rsOwner='p' rsUpdatedAt='${remoteUpdatedAt}' searchClass='' sortOrder='asc' sortProperty='' type='t1' username='user1' viewName='default'>
                    <RelatedObject relationName='group' alias='search.SearchQueryGroupForRedundancy' searchQuery='name:"(group1)" type:"(t1)" username:"(user1)" ' />
                  </Object>
                  <Object alias='search.SearchQueryForRedundancy' id='77774' isLocal='true' isPublic='false' name='query2' query='asd' rsInsertedAt='${remoteUpdatedAt}' rsOwner='p' rsUpdatedAt='${remoteUpdatedAt}' searchClass='' sortOrder='asc' sortProperty='' type='t1' username='user1' viewName='default'>
                    <RelatedObject relationName='group' alias='search.SearchQueryGroupForRedundancy' searchQuery='name:"(group1)" type:"(t1)" username:"(user1)" ' />
                  </Object>
                </Objects>""";
                scriptResult=ScriptManagerForTest.runScript("synchronizeObjects",[params:["modelName":"search.SearchQueryForRedundancy","withRelations":"true"]]);
                println "synchronizeObjects result"+scriptResult.replaceAll("<br>","\n");
                assertTrue(scriptResult.indexOf("Error")<0);
            }
            finally{
                ExecutionContextManagerUtils.removeObjectFromCurrentContext ("isRemote");
            }
        }

        assertEquals(2,SearchQuery.count());
        assertEquals(1,SearchQueryGroup.count());

        newQueryGroup=SearchQueryGroup.get(id:queryGroup.id);
        assertEquals(2,newQueryGroup.queries.size());
        assertEquals(true,newQueryGroup.isLocal);

        newSearchQuery=SearchQuery.get(id:searchQuery.id);
        assertEquals(newQueryGroup.id,newSearchQuery.group.id);
        assertEquals(true,newSearchQuery.isLocal);

        def newSearchQuery2=SearchQuery.search("name:query2").results[0];
        assertEquals(newQueryGroup.id,newSearchQuery2.group.id);
        assertEquals(false,newSearchQuery2.isLocal);

        //TEST RELATION REMOVAL FROM MANY-TO-MANY
        //TestLogUtils.enableLogger();
        //sync SearchQueryGroup with groups  relation with query2 removed
        ExecutionContextManagerUtils.executeInContext ([:])
        {
            ExecutionContextManagerUtils.addObjectToCurrentContext("isRemote",true);
            try{
                doRequestResultFromRemoteServer="""
                <Objects total='1' offset='0'>
                  <Object alias='search.SearchQueryGroupForRedundancy' expanded='false' id='66661' isLocal='true' isPublic='false' name='group1' rsInsertedAt='${remoteUpdatedAt}' rsOwner='p' rsUpdatedAt='${remoteUpdatedAt}' type='t1' username='user1'>
                    <RelatedObject relationName='queries' alias='search.SearchQueryForRedundancy' searchQuery='name:"(query1)" type:"(t1)" username:"(user1)" ' />
                  </Object>
                </Objects>""";
                scriptResult=ScriptManagerForTest.runScript("synchronizeObjects",[params:["modelName":"search.SearchQueryGroupForRedundancy","withRelations":"true"]]);
                println "synchronizeObjects result"+scriptResult.replaceAll("<br>","\n");
                assertTrue(scriptResult.indexOf("Error")<0);
            }
            finally{
                ExecutionContextManagerUtils.removeObjectFromCurrentContext ("isRemote");
            }
        }

        newQueryGroup=SearchQueryGroup.get(id:queryGroup.id);
        assertEquals(1,newQueryGroup.queries.size());
        assertEquals(true,newQueryGroup.isLocal);

        newSearchQuery=SearchQuery.get(id:searchQuery.id);
        assertEquals(newQueryGroup.id,newSearchQuery.group.id);
        assertEquals(true,newSearchQuery.isLocal);
        
        newSearchQuery2=SearchQuery.search("name:query2").results[0];
        assertEquals(null,newSearchQuery2.group);
        assertEquals(false,newSearchQuery2.isLocal);
   }
   public void testSynchonizeObjectsScript_ConvertsIdRelatedObjectsToPropertyFromRemote()
   {
        def remoteUpdatedAt=Date.now()+3600000;

        initializeScriptManager();
        ScriptManagerForTest.addScript("synchronizeObjects");

        def scriptResult=null;

        def doRequestResultFromRemoteServer="""<Objects total='0' offset='0'></Objects>""";

        HttpDatasource.metaClass.doRequest= { String url, Map params ->
             return doRequestResultFromRemoteServer;
        }
        def ds=HttpDatasource.add(name:"redundancy1");
        assertFalse(ds.hasErrors());

        def user=RsUser.add(username:"user1",passwordHash:"");
        assertFalse(user.errors.toString(),user.hasErrors());

        ExecutionContextManagerUtils.executeInContext ([:])
        {
            ExecutionContextManagerUtils.addObjectToCurrentContext("isRemote",true);
            try{
                doRequestResultFromRemoteServer="""
                <Objects total='1' offset='0'>
                  <Object alias='auth.RsUserInformationForRedundancy' id='4444441' isLocal='true' rsInsertedAt='1266499889000' rsOwner='p' rsUpdatedAt='1266499889000' type='email0' userId='5555'>
                    <IdRelatedObject relationName='userId' alias='auth.RsUserForRedundancy' searchQuery='username:"(user1)" ' />
                    <RelatedObject relationName='rsUser' alias='auth.RsUserForRedundancy' searchQuery='username:"(user1)" ' />
                  </Object>
                </Objects>""";
                scriptResult=ScriptManagerForTest.runScript("synchronizeObjects",[params:["modelName":"auth.RsUserInformationForRedundancy","withRelations":"true"]]);
                println "synchronizeObjects result"+scriptResult.replaceAll("<br>","\n");
                assertTrue(scriptResult.indexOf("Error")<0);
            }
            finally{
                ExecutionContextManagerUtils.removeObjectFromCurrentContext ("isRemote");
            }
        }

        assertEquals(1,RsUserInformation.count());
        def info=RsUserInformation.list()[0];
        assertEquals("id related userId should be generated",user.id,info.userId);
        assertTrue(info.userId<100); //not from xml
        assertEquals("email0",info.type);
        
        assertEquals("also relations should be generated",user.id,info.rsUser.id);

        
        //remove RsUser and test same info now
        RsUserInformation.removeAll();
        RsUser.removeAll();
        assertEquals(0,RsUser.count());
        assertEquals(0,RsUserInformation.count());

        ExecutionContextManagerUtils.executeInContext ([:])
        {
            ExecutionContextManagerUtils.addObjectToCurrentContext("isRemote",true);
            try{
                doRequestResultFromRemoteServer="""
                <Objects total='1' offset='0'>
                  <Object alias='auth.RsUserInformationForRedundancy' id='4444441' isLocal='true' rsInsertedAt='1266499889000' rsOwner='p' rsUpdatedAt='1266499889000' type='email0' userId='5555'>
                    <IdRelatedObject relationName='userId' alias='auth.RsUserForRedundancy' searchQuery='username:"(user1)" ' />
                  </Object>
                </Objects>""";
                scriptResult=ScriptManagerForTest.runScript("synchronizeObjects",[params:["modelName":"auth.RsUserInformationForRedundancy","withRelations":"true"]]);
                println "synchronizeObjects result"+scriptResult.replaceAll("<br>","\n");
                assertTrue(scriptResult.indexOf("Error")<0);
            }
            finally{
                ExecutionContextManagerUtils.removeObjectFromCurrentContext ("isRemote");
            }
        }

        assertEquals(1,RsUserInformation.count());

   }
}