/*
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
package script
import com.ifountain.core.test.util.RapidCoreTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import org.apache.log4j.Level;
import com.ifountain.rcmdb.datasource.ListeningAdapterManager;
import datasource.BaseListeningDatasource
import com.ifountain.rcmdb.datasource.BaseListeningDatasourceMock;
import com.ifountain.rcmdb.scripting.ScriptManager
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import org.apache.commons.io.FileUtils
import com.ifountain.rcmdb.scripting.ScriptScheduler

/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Oct 27, 2008
 * Time: 1:25:00 PM
 * To change this template use File | Settings | File Templates.
 */
class CmdbScriptOperationsTest extends RapidCoreTestCase{
    def expectedScriptMessage = "script executed successfully";
    def static base_directory = "../testoutput/";
    def simpleScriptFile="CmdbScriptOperationsTestScriptFile.groovy"
    protected void setUp() {
        super.setUp()
        ScriptManager manager = ScriptManager.getInstance();
        if(new File(base_directory).exists())
        {
            FileUtils.deleteDirectory (new File(base_directory));
        }
        manager.initialize(this.class.getClassLoader(), base_directory, []);
        new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY").mkdirs();

     }
     protected void tearDown() {
        super.tearDown()
        GroovySystem.metaClassRegistry.removeMetaClass(ListeningAdapterManager)
        GroovySystem.metaClassRegistry.removeMetaClass(ScriptScheduler)
        ListeningAdapterManager.destroyInstance();
        ScriptScheduler.destroyInstance();
        GroovySystem.metaClassRegistry.removeMetaClass(CmdbScript)
     }

     void testBeforeDelete(){
        CompassForTests.initialize([CmdbScript]);
        CompassForTests.addOperationSupport (CmdbScript, CmdbScriptOperations);


        def ds=new BaseListeningDatasourceMock(name:"myds");
        CmdbScript script=new CmdbScript(id:0,name:"testscript",type:CmdbScript.LISTENING,listeningDatasource:ds);

        CompassForTests.getOperationData.setObjectsWillBeReturned([script]);

        def stoppedDatasource=null;
        ListeningAdapterManager.metaClass.stopAdapter= { BaseListeningDatasource listeningDatasource ->
            stoppedDatasource = listeningDatasource;
        }
        assertNull(stoppedDatasource);
        script.beforeDelete();
        assertEquals(stoppedDatasource,ds);

     }
     void testBeforeUpdate(){
        CompassForTests.initialize([CmdbScript]);
        CompassForTests.addOperationSupport (CmdbScript, CmdbScriptOperations);


        def ds=new BaseListeningDatasourceMock(name:"myds");
        CmdbScript script=new CmdbScript(id:0,name:"testscript",type:CmdbScript.LISTENING,listeningDatasource:ds);

        CompassForTests.getOperationData.setObjectsWillBeReturned([script]);

        def stoppedDatasource=null;
        ListeningAdapterManager.metaClass.stopAdapter= { BaseListeningDatasource listeningDatasource ->
            stoppedDatasource = listeningDatasource;
        }
        assertNull(stoppedDatasource);
        script.beforeUpdate();
        assertEquals(stoppedDatasource,ds);

     }

     void initializeForCmdbScript(){
         createSimpleScript (simpleScriptFile);
         
         CompassForTests.initialize([CmdbScript]);
         CompassForTests.addOperationSupport (CmdbScript, CmdbScriptOperations);
     }
     void testAddScriptGeneratesScriptFileParamWhenMissing()
     {
        initializeForCmdbScript();

        def params=[name:"CmdbScriptOperationsTestScript",type:CmdbScript.ONDEMAND]
        def scriptToAdd=new CmdbScript(scriptFile:simpleScriptFile);
        params.each{ key , val ->
            scriptToAdd[key]=val
        }
        CompassForTests.addOperationData.setObjectsWillBeReturned([scriptToAdd]);
        def script=CmdbScript.addScript(params)
        def paramsAdded=CompassForTests.addOperationData.getParams(CmdbScript)[0];
        assertEquals(paramsAdded.scriptFile,params.name)               
                       
     }
     void testAddScriptGeneratesLogFileParamWhenMissing()
     {
        initializeForCmdbScript();

        def params=[name:"CmdbScriptOperationsTestScript",type:CmdbScript.ONDEMAND,scriptFile:simpleScriptFile]
        def scriptToAdd=new CmdbScript(logFile:"mylogfile");
        params.each{ key , val ->
            scriptToAdd[key]=val
        }
        CompassForTests.addOperationData.setObjectsWillBeReturned([scriptToAdd]);
        def script=CmdbScript.addScript(params)
        def paramsAdded=CompassForTests.addOperationData.getParams(CmdbScript)[0];
        assertEquals(paramsAdded.logFile,params.name)

     }
     void testAddScriptsGeneratesExceptionWhenErrorOccurs()
     {
         initializeForCmdbScript();
         CmdbScript.metaClass.hasErrors = {  return true;}

         def sampleBean = CmdbScript.newInstance()
         Errors errors = new BeanPropertyBindingResult(sampleBean, sampleBean.getClass().getName());
         CmdbScript.metaClass.errors = errors
        
         
         def params=[name:"CmdbScriptOperationsTestScript",type:CmdbScript.ONDEMAND,scriptFile:simpleScriptFile]
         def scriptToAdd=new CmdbScript();
         params.each{ key , val ->
            scriptToAdd[key]=val
         }
         
         //def messageServiceClass=ClassLoader.getSystemClassLoader().loadClass("MessageService");
         def messageServiceClass=this.class.classLoader.loadClass("MessageService");
         messageServiceClass.metaClass.getMessage = { param1 -> return "injectedTestMessage"}
         
         scriptToAdd.messageService=messageServiceClass.newInstance()
         GroovySystem.metaClassRegistry.removeMetaClass(messageServiceClass)

         CompassForTests.addOperationData.setObjectsWillBeReturned([scriptToAdd]);
         try{
            CmdbScript.addScript(params);
            fail("should throw exception")
         }
         catch(e)                                               
         {
             assertEquals(e.getMessage(),"injectedTestMessage");
         }


     }

     void testAddScript(){
        initializeForCmdbScript();      

        def logLevel=Level.DEBUG;
        def logParams=[:]
        logParams["logLevel"]=logLevel.toString();
        logParams["logFileOwn"]=true;

        def params=[name:"myscript",type:CmdbScript.ONDEMAND,scriptFile:simpleScriptFile,logLevel:logParams.logLevel,logFileOwn:logParams.logFileOwn]
        def scriptToAdd=new CmdbScript();
        params.each{ key , val ->
            scriptToAdd[key]=val
        }
        CompassForTests.addOperationData.setObjectsWillBeReturned([scriptToAdd]);

        assertEquals(0,CompassForTests.addOperationData.getCallCount(CmdbScript));
        def script=CmdbScript.addScript(params)
        def paramsAdded=CompassForTests.addOperationData.getParams(CmdbScript)[0]

        assertEquals(1,CompassForTests.addOperationData.getCallCount(CmdbScript));

        //tests that ScriptManager.addScript() is called        
        def scriptClass=ScriptManager.getInstance().getScript(script.scriptFile);
        println scriptClass
        assertNotNull(scriptClass);

        //tests the script file is really loaded and can be runned
        def scriptObject = scriptClass.newInstance();
        assertEquals (expectedScriptMessage, scriptObject.run())

        //test the logger is configured
        def logger=CmdbScript.getScriptLogger(script);
        assertEquals(logger.getLevel(),logLevel);
        assertTrue(logger.getAllAppenders().hasMoreElements());


        


     }

     void testAddScriptCallsSchedulerForPeriodicScript()
     {
        initializeForCmdbScript();

        def params=[name:"myscript",type:CmdbScript.SCHEDULED,scheduleType:CmdbScript.PERIODIC,scriptFile:simpleScriptFile,enabled:true,startDelay:10,period:20]
        def scriptToAdd=new CmdbScript();
        params.each{ key , val ->
            scriptToAdd[key]=val
        }
        CompassForTests.addOperationData.setObjectsWillBeReturned([scriptToAdd]);


        def schedulerMap=[:];

        ScriptScheduler.metaClass.scheduleScript= { String scriptName, long startDelay, long period ->
            schedulerMap.scriptName=scriptName
            schedulerMap.startDelay=startDelay
            schedulerMap.period=period            
        }

        def script=CmdbScript.addScript(params)
        def paramsAdded=CompassForTests.addOperationData.getParams(CmdbScript)[0]
        assertEquals(script.name,schedulerMap.scriptName)
        assertEquals(script.startDelay,schedulerMap.startDelay)
        assertEquals(script.period,schedulerMap.period)

     }
     void testAddScriptCallsSchedulerForCronScript()
     {
        initializeForCmdbScript();

        def params=[name:"myscript",type:CmdbScript.SCHEDULED,scheduleType:CmdbScript.CRON,scriptFile:simpleScriptFile,enabled:true,startDelay:10,cronExpression : "* * * * * ?"]
        def scriptToAdd=new CmdbScript();
        params.each{ key , val ->
            scriptToAdd[key]=val
        }
        CompassForTests.addOperationData.setObjectsWillBeReturned([scriptToAdd]);


        def schedulerMap=[:];

        ScriptScheduler.metaClass.scheduleScript= { String scriptName, long startDelay, String cronExp ->
            schedulerMap.scriptName=scriptName
            schedulerMap.startDelay=startDelay
            schedulerMap.cronExp=cronExp
        }

        def script=CmdbScript.addScript(params)
        def paramsAdded=CompassForTests.addOperationData.getParams(CmdbScript)[0]
        assertEquals(script.name,schedulerMap.scriptName)
        assertEquals(script.startDelay,schedulerMap.startDelay)
        assertEquals(script.cronExpression,schedulerMap.cronExp)

     }

     void testCreateStaticParams(){

         CompassForTests.addOperationSupport (CmdbScript, CmdbScriptOperations);

         CmdbScript script=new CmdbScript(staticParam:"x:5,y:6");
         def mapParams=CmdbScript.getStaticParamMap(script);
         assertEquals(mapParams.x,"5");
         assertEquals(mapParams.y,"6");
         assertEquals(mapParams.size(),2);

         CmdbScript scriptWithNoStaticParam=new CmdbScript();
         def mapParams2=CmdbScript.getStaticParamMap(scriptWithNoStaticParam);
         assertEquals(mapParams2.size(),0);

         CmdbScript scriptWithCustomParam=new CmdbScript(staticParam:"xyz-dsfdfdf");
         def mapParams3=CmdbScript.getStaticParamMap(scriptWithCustomParam);
         assertEquals(mapParams3.size(),0);


     }

     void testConfigureScriptLogger()
     {
          CompassForTests.addOperationSupport (CmdbScript, CmdbScriptOperations);

          def logLevel=Level.DEBUG;
          def scriptParams=[:]
          scriptParams["name"]="testscript";
          scriptParams["logFile"]="testscript";
          scriptParams["logLevel"]=logLevel.toString();
          scriptParams["logFileOwn"]=false;

          CmdbScript script=new CmdbScript(name:scriptParams.name,logFile:scriptParams.logFile,logFileOwn:scriptParams.logFileOwn,logLevel:scriptParams.logLevel);

          def logger=null;

          CmdbScript.configureScriptLogger(script);
          logger=CmdbScript.getScriptLogger(script);
          assertEquals(logger.getLevel(),logLevel);
          assertFalse(logger.getAllAppenders().hasMoreElements());

          script.logFileOwn=true;
          script.logLevel=Level.INFO.toString();
          CmdbScript.configureScriptLogger(script);
          logger=CmdbScript.getScriptLogger(script);
          assertEquals(logger.getLevel(),Level.INFO);
          assertTrue(logger.getAllAppenders().hasMoreElements());



     }

     def createSimpleScript(scriptName)
    {
        def scriptFile = new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write ("""return "$expectedScriptMessage" """);
    }

}

