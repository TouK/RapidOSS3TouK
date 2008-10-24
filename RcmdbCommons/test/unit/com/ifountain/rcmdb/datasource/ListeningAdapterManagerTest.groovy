package com.ifountain.rcmdb.datasource
import datasource.*;
import script.*;
import org.apache.log4j.Level
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.scripting.ScriptManager


/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Oct 23, 2008
 * Time: 4:03:10 PM
 * To change this template use File | Settings | File Templates.
 */
class ListeningAdapterManagerTest extends GroovyTestCase{
      void testDummy(){
          
      }
//    static def scriptMap=[:]
//    void testStartAdapterThrowsExceptionWhenNoListeningScriptIsDefined(){
//        def datasource=new BaseListeningDatasource();
//        try {
//
//            ListeningAdapterManager.getInstance().startAdapter(datasource);
//            fail("Should throw exception");
//        }
//        catch(Exception e)
//        {
//            println e;
//        }
//
//        datasource.listeningScript=new CmdbScript(name:"dummysc",type:CmdbScript.ONDEMAND);
//        try {
//
//            ListeningAdapterManager.getInstance().startAdapter(datasource);
//            fail("Should throw exception");
//        }
//        catch(Exception e)
//        {
//            println e;
//        }
//    }
//    void testAdapterCreatesScriptLogger()
//    {
//        def logFile="ListeningAdapterManagerTestScript";
//        def logLevel=Level.DEBUG;
//
//        ScriptManager.getInstance().initialize(this.class.getClassLoader(), System.getProperty("base.dir"), []);
//        CompassForTests.addOperationSupport (CmdbScript, CmdbScriptOperations);
//
//        CompassForTests.initialize([CmdbScript]);
//        CompassForTests.setAddObjects([new CmdbScript(name:"dummysc",type:CmdbScript.LISTENING,scriptFile:"ListeningAdapterManagerTestScript",logFile:logFile,logLevel:logLevel.toString())]);
//        def script=CmdbScript.addScript(name:"dummysc",type:CmdbScript.LISTENING,scriptFile:"ListeningAdapterManagerTestScript");
//
//        def datasource=new BaseListeningDatasource();
//        datasource.listeningScript=script;
//
//        ListeningAdapterManager.getInstance().startAdapter(datasource);
//
//        def logger=CmdbScript.getScriptLogger(datasource.listeningScript);
//        assertEquals(logger.getLevel(),Level.DEBUG);
//
//    }
//    void testAdapterRunsTheScript()
//    {
//
//        ScriptManager.getInstance().initialize(this.class.getClassLoader(), System.getProperty("base.dir"), []);
//        CompassForTests.addOperationSupport (CmdbScript, CmdbScriptOperations);
//        CompassForTests.addOperationSupport (BaseListeningDatasource, BaseListeningDatasourceOperations);
//        CompassForTests.addOperationSupport (SmartsNotificationDatasource, SmartsNotificationDatasourceOperations);
//
//        CompassForTests.initialize([CmdbScript]);
//        CompassForTests.setAddObjects([new CmdbScript(name:"dummysc",type:CmdbScript.LISTENING,scriptFile:"ListeningAdapterManagerTestScript")]);
//        def script=CmdbScript.addScript(name:"dummysc",type:CmdbScript.LISTENING,scriptFile:"ListeningAdapterManagerTestScript");
//
//        def datasource=new BaseListeningDatasource();
//        datasource.listeningScript=script;
//
//        ListeningAdapterManager.getInstance().startAdapter(datasource);
//
//
//
//        assertEquals(scriptMap.logger,CmdbScript.getScriptLogger(script));
//        assertEquals(scriptMap.datasource,datasource);
//        assertEquals(scriptMap.staticParam,script.staticParam);
//
//        assertEquals(scriptMap.scriptRunStarted,true);
//        assertEquals(scriptMap.scriptRunEnded,true);
//
//        assertEquals(scriptMap.scriptInitInvoked,true);
//    }
}