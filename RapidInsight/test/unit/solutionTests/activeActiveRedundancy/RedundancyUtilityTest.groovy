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
class RedundancyUtilityTest extends RapidCmdbWithCompassTestCase{
     def RsMessageRule;
     def RsMessageRuleOperations;
     def MapGroup;
     def MapGroupOperations;
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


     def static solutionPath;


     public void setUp() {
        super.setUp();
        clearMetaClasses();
        solutionPath = getWorkspacePath() + "/RapidModules/RapidInsight/solutions/ActiveActiveRedundancy"

        generateModelFilesForTest();

        gcl.addClasspath("${temp_model_directory}/domain/".toString());
        RsMessageRule=gcl.loadClass("message.RsMessageRuleForRedundancy") ;
        DeletedObjects=gcl.loadClass("DeletedObjects");


        RsMessageRuleOperations=gcl.parseClass(new File("${solutionPath}/operations/message/RsMessageRuleOperations.groovy"));

        redundancyUtility=gcl.parseClass(new File("${solutionPath}/operations/RedundancyUtility.groovy"));

        initialize([RsMessageRule,DeletedObjects], []);

        CompassForTests.addOperationSupport(RsMessageRule, RsMessageRuleOperations);

        RapidApplicationTestUtils.initializeRapidApplicationOperations(RapidApplication);
        RapidApplicationTestUtils.utilityPaths = ["RedundancyUtility": new File("${solutionPath}/operations/RedundancyUtility.groovy")];
    }
     //Copy the models from solution and replate model names with modelNameForRedundancy
     //If renaming not done test fail in hudson or when multiple tests runned together.
     protected void generateModelFilesForTest()
     {
        println "Will generate models";
        deleteGeneratedModelsDirectory();
        new File(temp_model_directory).mkdirs();
        
        FileUtils.copyFileToDirectory(new File("${solutionPath}/grails-app/domain/message/RsMessageRule.groovy"),new File("${temp_model_directory}/domain/message"));
        FileUtils.copyFileToDirectory(new File("${solutionPath}/grails-app/domain/DeletedObjects.groovy"),new File("${temp_model_directory}/domain/"));
        convertModelFileFromTempDir("RsMessageRule","${temp_model_directory}/domain/message/");
     }
     protected void deleteGeneratedModelsDirectory()
     {
        if (new File(temp_model_directory).exists())
        {
            FileUtils.deleteDirectory(new File(temp_model_directory));
        }
     }
    private void convertModelFileFromTempDir(String modelName,String directoryPath)
    {
        def modelFile=new File(directoryPath+"/${modelName}.groovy");
        def modelText=modelFile.getText().replace("class "+modelName,"class "+modelName+"ForRedundancy");        
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
            assertEquals("message.RsMessageRuleForRedundancy",deletedObject1.modelName);
            assertEquals(redundancyUtility.getKeySearchQueryForObject("message.RsMessageRuleForRedundancy",rule1),deletedObject1.searchQuery);
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

        assertEquals("""destinationType:"(email)" searchQueryId:1 userId:2 """,redundancyUtility.getKeySearchQueryForObject("message.RsMessageRuleForRedundancy",rule1));

   }
   

   
}