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
import connector.NotificationConnector
import connector.NotificationConnectorOperations
import script.CmdbScript
import script.CmdbScriptOperations
import com.ifountain.rcmdb.scripting.ScriptManager
import com.ifountain.rcmdb.scripting.ScriptScheduler

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 17, 2010
* Time: 1:58:23 PM
* To change this template use File | Settings | File Templates.
*/
class RedundancyMasterCheckerScriptTest extends RapidCmdbWithCompassTestCase{
     def RsLookup;
     def RedundancyLookup;
     def redundancyUtility;

     def static temp_model_directory = "../testoutput/ActiveActive/grails-app";

     def static solutionPath;


     public void setUp() {
        super.setUp();
        clearMetaClasses();
        solutionPath = getWorkspacePath() + "/RapidModules/RapidInsight/solutions/ActiveActiveRedundancy"

        generateTemporaryModelsDirectory();


        gcl.addClasspath("${temp_model_directory}/domain/".toString());
        RsLookup=gcl.loadClass("RsLookup");
        RedundancyLookup=gcl.loadClass("RedundancyLookup");

        redundancyUtility=gcl.parseClass(new File("${solutionPath}/operations/RedundancyUtility.groovy"));

        initialize([RedundancyLookup,HttpDatasource,RsLookup,NotificationConnector,CmdbScript], []);

        ScriptManager.metaClass.checkScript = {String script ->}
        ScriptManager.metaClass.addScript = {String script ->}
        ScriptManager.metaClass.removeScript = {String script ->}
        ScriptScheduler.metaClass.scheduleScript = {String scriptName, long startDelay, long period ->}
        ScriptScheduler.metaClass.unscheduleScript = {String scriptName ->}
        ScriptScheduler.metaClass.isScheduled = {String scriptName, long startDelay, long period ->}
        ScriptScheduler.metaClass.isScheduled = {String scriptName, long startDelay, String cronExp ->}


        CompassForTests.addOperationSupport(NotificationConnector, NotificationConnectorOperations);
        CompassForTests.addOperationSupport(CmdbScript, CmdbScriptOperations);

        RapidApplicationTestUtils.initializeRapidApplicationOperations(RapidApplication);
        RapidApplicationTestUtils.utilityPaths = ["RedundancyUtility": new File("${solutionPath}/operations/RedundancyUtility.groovy")];

        initializeScriptManager();
    }
     protected void generateTemporaryModelsDirectory()
     {
        if (new File(temp_model_directory).exists())
        {
            FileUtils.deleteDirectory(new File(temp_model_directory));
        }
        new File(temp_model_directory).mkdirs();
        FileUtils.copyFileToDirectory(new File("${solutionPath}/grails-app/domain/RedundancyLookup.groovy"),new File("${temp_model_directory}/domain"));
     }
    public void tearDown() {
        clearMetaClasses();
        super.tearDown();
    }
    public static void clearMetaClasses()
    {
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(HttpDatasource);
        GroovySystem.metaClassRegistry.removeMetaClass(ScriptManager);
        GroovySystem.metaClassRegistry.removeMetaClass(ScriptScheduler);
        GroovySystem.metaClassRegistry.removeMetaClass(CmdbScript);
        ExpandoMetaClass.enableGlobally();
    }
    void initializeScriptManager()
    {
        def temp_scripts_directory="${solutionPath}/scripts";
        println "base path is :" + new File(temp_scripts_directory).getCanonicalPath();
        ScriptManagerForTest.initialize(gcl, temp_scripts_directory);
    }

   public void testEnableLocalMasterScript()
   {
       ScriptManagerForTest.addScript("enableLocalMaster");

       def messageGeneratorScript=CmdbScript.add(name:"messageGenerator",scriptFile:"a",enabled:false);
       assertFalse(messageGeneratorScript.errors.toString(),messageGeneratorScript.hasErrors());
       assertFalse(messageGeneratorScript.enabled);

       def notificationConnector=NotificationConnector.add(name:"con1",type:"email");
       assertFalse(notificationConnector.hasErrors());
       def connectorScript=CmdbScript.add(name:NotificationConnector.getScriptName("con1"),scriptFile:"a",enabled:false);
       assertFalse(connectorScript.hasErrors());
       assertFalse(connectorScript.enabled);

       ScriptManagerForTest.runScript("enableLocalMaster",[:]);


       assertEquals(1,RedundancyLookup.count());
       def redundancyLookup=RedundancyLookup.get(name:"isMaster");
       assertEquals("true",redundancyLookup.value);

       messageGeneratorScript=CmdbScript.get(id:messageGeneratorScript.id);
       assertTrue(messageGeneratorScript.enabled);

       connectorScript=CmdbScript.get(id:connectorScript.id);
       assertTrue(connectorScript.enabled);

   }

   public void testDisableLocalMasterScript()
   {
       ScriptManagerForTest.addScript("disableLocalMaster");

       def messageGeneratorScript=CmdbScript.add(name:"messageGenerator",scriptFile:"a",enabled:true);
       assertFalse(messageGeneratorScript.errors.toString(),messageGeneratorScript.hasErrors());
       assertTrue(messageGeneratorScript.enabled);

       def notificationConnector=NotificationConnector.add(name:"con1",type:"email");
       assertFalse(notificationConnector.hasErrors());
       def connectorScript=CmdbScript.add(name:NotificationConnector.getScriptName("con1"),scriptFile:"a",enabled:true);
       assertFalse(connectorScript.hasErrors());
       assertTrue(connectorScript.enabled);

       ScriptManagerForTest.runScript("disableLocalMaster",[:]);


       assertEquals(1,RedundancyLookup.count());
       def redundancyLookup=RedundancyLookup.get(name:"isMaster");
       assertEquals("false",redundancyLookup.value);

       messageGeneratorScript=CmdbScript.get(id:messageGeneratorScript.id);
       assertFalse(messageGeneratorScript.enabled);

       connectorScript=CmdbScript.get(id:connectorScript.id);
       assertFalse(connectorScript.enabled);

   }

   public void testRedundancyMasterSwitcherDoesNotChangeAnythingWhenNoRedundancyServerDefined()
   {
      ScriptManagerForTest.addScript("redundancyMasterSwitcher");

      def scriptResult=ScriptManagerForTest.runScript("redundancyMasterSwitcher",[:]);
      assertTrue(scriptResult.indexOf("Error")>=0);
      assertTrue(scriptResult.indexOf("no redundancy server is defined")>=0);

      assertEquals(0,RedundancyLookup.count());
   }

   public void testRedundancyMasterSwitcherDoesNotChangeAntthingWhenLocalServerIsMasterAlready()
   {
      ScriptManagerForTest.addScript("redundancyMasterSwitcher");

      def oldLookup=RedundancyLookup.add(name:"isMaster",value:"true");
      assertFalse(oldLookup.hasErrors());
      
      def scriptResult=ScriptManagerForTest.runScript("redundancyMasterSwitcher",[:]);
      assertFalse(scriptResult.indexOf("Error")>=0);
      assertTrue(scriptResult.indexOf("Local is master : will not check remote servers")>=0);

      //RedundancyLookup not updated
      assertEquals(1,RedundancyLookup.count());
      assertEquals(oldLookup.rsInsertedAt,RedundancyLookup.get(name:"isMaster").rsInsertedAt);
   }

   public void testRedundancyMasterSwitcherWillMakeLocalServerSlaveAndSyncronizesMessageGeneratorLookupWhenRemoteServerIsMaster()
   {
        ScriptManagerForTest.addScript("redundancyMasterSwitcher");
        ScriptManagerForTest.addScript("disableLocalMaster");
      
        def doRequestCallParams_ForRedundancyLookup=[:];
        def doRequestResultFromRemoteServer_ForRedundancyLookup="""<Objects total='1' offset='0'>
            <Object alias='RedundancyLookup' id='1028' name='isMaster' value='true' rsInsertedAt='1266925281703' rsUpdatedAt='1266925281703'  />
        </Objects>""";

        def doRequestCallParams_ForRsLookup=[:];
        def doRequestResultFromRemoteServer_ForRsLookup="""<Objects total='2' offset='0'>
             <Object alias='RsLookup' id='1016' name='messageGeneratorMaxEventClearTime' rsInsertedAt='1266925274078' rsUpdatedAt='1266937678343' value='200' />
             <Object alias='RsLookup' id='1015' name='messageGeneratorMaxEventCreateTime' rsInsertedAt='1266924742421' rsUpdatedAt='1266937678359' value='100' />                    
       </Objects>""";
        

        HttpDatasource.metaClass.doRequest= { String url, Map params ->
             if(params.searchIn=="RedundancyLookup")
             {
                 doRequestCallParams_ForRedundancyLookup.url=url;
                 doRequestCallParams_ForRedundancyLookup.params=params;
                 return doRequestResultFromRemoteServer_ForRedundancyLookup;
             }
             else if(params.searchIn=="RsLookup")
             {
                 doRequestCallParams_ForRsLookup.url=url;
                 doRequestCallParams_ForRsLookup.params=params;
                 return doRequestResultFromRemoteServer_ForRsLookup;
             }

        }
        CmdbScript.metaClass.static.runScript = { String scriptName, Map params ->
            ScriptManagerForTest.runScript(scriptName,params);
        }
        def ds=HttpDatasource.add(name:"redundancy1");
        assertFalse(ds.hasErrors());

        def messageGeneratorScript=CmdbScript.add(name:"messageGenerator",scriptFile:"a",enabled:true);
        assertFalse(messageGeneratorScript.errors.toString(),messageGeneratorScript.hasErrors());
        assertTrue(messageGeneratorScript.enabled);

        def scriptResult=ScriptManagerForTest.runScript("redundancyMasterSwitcher",[:]);
        assertFalse(scriptResult.indexOf("Error")>=0);
        assertTrue(scriptResult.indexOf("Found Remote master redundancy1, Local Server will stay as slave")>=0);
        assertEquals(1,RedundancyLookup.count());
        def redundancyLookup=RedundancyLookup.get(name:"isMaster");
        assertEquals("false",redundancyLookup.value);

        assertEquals(2,doRequestCallParams_ForRedundancyLookup.size());
        assertEquals("RedundancyLookup",doRequestCallParams_ForRedundancyLookup.params.searchIn);

        messageGeneratorScript=CmdbScript.get(id:messageGeneratorScript.id);
        assertFalse(messageGeneratorScript.enabled);

        assertEquals(2,doRequestCallParams_ForRsLookup.size());
        assertEquals("RsLookup",doRequestCallParams_ForRsLookup.params.searchIn);

        assertEquals(2,RsLookup.count());
        assertEquals("200",RsLookup.get(name:"messageGeneratorMaxEventClearTime").value)
        assertEquals("100",RsLookup.get(name:"messageGeneratorMaxEventCreateTime").value)
   }

   public void testRedundancyMasterSwitcherWillMakeLocalServerMasterWhenRemoteServerIsSlave()
   {
        ScriptManagerForTest.addScript("redundancyMasterSwitcher");
        ScriptManagerForTest.addScript("enableLocalMaster");

        def doRequestCallParams_ForRedundancyLookup=[:];
        def doRequestResultFromRemoteServer_ForRedundancyLookup="""<Objects total='1' offset='0'>
            <Object alias='RedundancyLookup' id='1028' name='isMaster' value='false' rsInsertedAt='1266925281703' rsUpdatedAt='1266925281703'  />
        </Objects>""";

        HttpDatasource.metaClass.doRequest= { String url, Map params ->
             if(params.searchIn=="RedundancyLookup")
             {
                 doRequestCallParams_ForRedundancyLookup.url=url;
                 doRequestCallParams_ForRedundancyLookup.params=params;
                 return doRequestResultFromRemoteServer_ForRedundancyLookup;
             }
        }
        CmdbScript.metaClass.static.runScript = { String scriptName, Map params ->
            ScriptManagerForTest.runScript(scriptName,params);
        }
        def ds=HttpDatasource.add(name:"redundancy1");
        assertFalse(ds.hasErrors());

        def messageGeneratorScript=CmdbScript.add(name:"messageGenerator",scriptFile:"a",enabled:false);
        assertFalse(messageGeneratorScript.errors.toString(),messageGeneratorScript.hasErrors());
        assertFalse(messageGeneratorScript.enabled);

        def scriptResult=ScriptManagerForTest.runScript("redundancyMasterSwitcher",[:]);
        assertFalse(scriptResult.indexOf("Error")>=0);
        assertTrue(scriptResult.indexOf("Not found any remote master, will make Local Server master")>=0);
        assertEquals(1,RedundancyLookup.count());
        def redundancyLookup=RedundancyLookup.get(name:"isMaster");
        assertEquals("true",redundancyLookup.value);

        assertEquals(2,doRequestCallParams_ForRedundancyLookup.size());
        assertEquals("RedundancyLookup",doRequestCallParams_ForRedundancyLookup.params.searchIn);

        messageGeneratorScript=CmdbScript.get(id:messageGeneratorScript.id);
        assertTrue(messageGeneratorScript.enabled);
   }

   public void testRedundancyMasterSwitcherWillMakeLocalServerMasterWhenRemoteServerIsNotAccessible()
   {
        ScriptManagerForTest.addScript("redundancyMasterSwitcher");
        ScriptManagerForTest.addScript("enableLocalMaster");

        def doRequestCallParams_ForRedundancyLookup=[:];


        HttpDatasource.metaClass.doRequest= { String url, Map params ->
             if(params.searchIn=="RedundancyLookup")
             {
                 doRequestCallParams_ForRedundancyLookup.url=url;
                 doRequestCallParams_ForRedundancyLookup.params=params;
                 throw new Exception("Remote server is dead");
             }
        }
        CmdbScript.metaClass.static.runScript = { String scriptName, Map params ->
            ScriptManagerForTest.runScript(scriptName,params);
        }
        def ds=HttpDatasource.add(name:"redundancy1");
        assertFalse(ds.hasErrors());

        def messageGeneratorScript=CmdbScript.add(name:"messageGenerator",scriptFile:"a",enabled:false);
        assertFalse(messageGeneratorScript.errors.toString(),messageGeneratorScript.hasErrors());
        assertFalse(messageGeneratorScript.enabled);

        def scriptResult=ScriptManagerForTest.runScript("redundancyMasterSwitcher",[:]);
        println  "scriptResult "+scriptResult;
        assertTrue(scriptResult.indexOf("Error")>=0);
        assertTrue(scriptResult.indexOf("Remote server redundancy1 is not accessible.")>=0);
        assertTrue(scriptResult.indexOf("Remote server is dead")>=0);
        assertTrue(scriptResult.indexOf("Not found any remote master, will make Local Server master")>=0);
        assertEquals(1,RedundancyLookup.count());
        def redundancyLookup=RedundancyLookup.get(name:"isMaster");
        assertEquals("true",redundancyLookup.value);

        assertEquals(2,doRequestCallParams_ForRedundancyLookup.size());
        assertEquals("RedundancyLookup",doRequestCallParams_ForRedundancyLookup.params.searchIn);

        messageGeneratorScript=CmdbScript.get(id:messageGeneratorScript.id);
        assertTrue(messageGeneratorScript.enabled);
   }
}