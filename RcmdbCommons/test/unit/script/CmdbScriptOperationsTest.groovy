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
        clearMetaClasses();
    }
     protected void tearDown() {
        super.tearDown()


     }
     private void clearMetaClasses()
     {

        ListeningAdapterManager.destroyInstance();
        ScriptScheduler.destroyInstance();
        ScriptManager.destroyInstance();
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(ListeningAdapterManager)
        GroovySystem.metaClassRegistry.removeMetaClass(ScriptScheduler)
        GroovySystem.metaClassRegistry.removeMetaClass(ScriptManager)
        GroovySystem.metaClassRegistry.removeMetaClass(CmdbScript)
        ExpandoMetaClass.enableGlobally();
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
         ScriptManager manager = ScriptManager.getInstance();
        if(new File(base_directory).exists())
        {
            FileUtils.deleteDirectory (new File(base_directory));
        }
        manager.initialize(this.class.getClassLoader(), base_directory, []);
        new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY").mkdirs();
        
         createSimpleScript (simpleScriptFile,expectedScriptMessage);
         
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
     void testAddScriptDoesNotGenerateExceptionIfFromController()
     {
         initializeForCmdbScript();
         CmdbScript.metaClass.hasErrors = {  return true;}
         def params=[name:"CmdbScriptOperationsTestScript",type:CmdbScript.ONDEMAND,scriptFile:simpleScriptFile]
         def scriptToAdd=new CmdbScript();
         params.each{ key , val ->
            scriptToAdd[key]=val
         }
         CompassForTests.addOperationData.setObjectsWillBeReturned([scriptToAdd]);
         
         try{
            def script=CmdbScript.addScript(params,true);
            assertTrue(script.hasErrors());
         }
         catch(e)
         {
             println e
             fail("should not throw exception")
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

     void testAddScriptCallsSchedulerForScheduledScripts()
     {
        initializeForCmdbScript();


        def schedulerMap=[:];

        ScriptScheduler.metaClass.scheduleScript= { String scriptName, long startDelay, long period ->
            schedulerMap.scriptName=scriptName
            schedulerMap.startDelay=startDelay
            schedulerMap.period=period
        }

        ScriptScheduler.metaClass.scheduleScript= { String scriptName, long startDelay, String cronExp ->
            schedulerMap.scriptName=scriptName
            schedulerMap.startDelay=startDelay
            schedulerMap.cronExp=cronExp
        }
        

        //testing for periodic script
        def params=[name:"myscript",type:CmdbScript.SCHEDULED,scheduleType:CmdbScript.PERIODIC,scriptFile:simpleScriptFile,enabled:true,startDelay:10,period:20]
        def scriptToAdd=new CmdbScript();
        params.each{ key , val ->
            scriptToAdd[key]=val
        }
        CompassForTests.addOperationData.setObjectsWillBeReturned([scriptToAdd]);

        def script=CmdbScript.addScript(params)
        println schedulerMap
        assertEquals(params.name,schedulerMap.scriptName)
        assertEquals(params.startDelay,schedulerMap.startDelay)
        assertEquals(params.period,schedulerMap.period)

        //testing for cron script
        schedulerMap=[:];
        params=[name:"myscript",type:CmdbScript.SCHEDULED,scheduleType:CmdbScript.CRON,scriptFile:simpleScriptFile,enabled:true,startDelay:3,cronExpression : "* * * * * ?"]
        scriptToAdd=new CmdbScript();
        params.each{ key , val ->
            scriptToAdd[key]=val
        }
        CompassForTests.addOperationData.setObjectsWillBeReturned([scriptToAdd]);

        script=CmdbScript.addScript(params)
        println schedulerMap
        assertEquals(params.name,schedulerMap.scriptName)
        assertEquals(params.startDelay,schedulerMap.startDelay)
        assertEquals(params.cronExpression,schedulerMap.cronExp)
     }
     
    public void testUpdateScript()
    {
        initializeForCmdbScript();
        def logLevel=Level.DEBUG;
        def logParams=[:]
        logParams["logLevel"]=logLevel.toString();
        logParams["logFileOwn"]=true;

        
        def updateParams=[name:"myscript333",type:CmdbScript.ONDEMAND,scriptFile:simpleScriptFile,logLevel:logParams.logLevel,logFileOwn:logParams.logFileOwn]
        updateParams.logFile=updateParams.name
        def params=[name:"myscript",type:CmdbScript.ONDEMAND,scriptFile:simpleScriptFile]
        def scriptToUpdate=new CmdbScript();
        params.each{ key , val ->
            scriptToUpdate[key]=val
        }

        def unscheduleScriptName=null;
        ScriptScheduler.metaClass.unscheduleScript={ String scriptName ->
            unscheduleScriptName=scriptName
        }
         //test a logger is not configured before for the new script name
        def scriptForLogCheck=new CmdbScript();
        updateParams.each{ key , val ->
            scriptForLogCheck[key]=val
        }
        def logger=CmdbScript.getScriptLogger(scriptForLogCheck);        
        assertNull(logger.getLevel());
        assertFalse(logger.getAllAppenders().hasMoreElements());
        
        def script=CmdbScript.updateScript(scriptToUpdate,updateParams,false);
        assertEquals(script.name,updateParams.name)
        assertEquals(unscheduleScriptName,params.name)

        logger=CmdbScript.getScriptLogger(script);
        assertEquals(logger.getLevel(),logLevel);
        assertTrue(logger.getAllAppenders().hasMoreElements());
        

    }
    void testReloadScript()
    {
        initializeForCmdbScript();
        def params=[name:"myscript",type:CmdbScript.ONDEMAND,scriptFile:simpleScriptFile]
        def scriptToAdd=new CmdbScript();
        params.each{ key , val ->
            scriptToAdd[key]=val
        }
        CompassForTests.addOperationData.setObjectsWillBeReturned([scriptToAdd]);

        def script=CmdbScript.addScript(params);
        

        //tests the script file is really loaded and can be runned
        def scriptClass=ScriptManager.getInstance().getScript(script.scriptFile);
        def scriptObject = scriptClass.newInstance();
        assertEquals (expectedScriptMessage, scriptObject.run())

        def newScriptMessage="new_script_message"
        createSimpleScript(simpleScriptFile,newScriptMessage)

        //we overwrited the script file test that the old one is working, because no reload done
        scriptClass=ScriptManager.getInstance().getScript(script.scriptFile);
        scriptObject = scriptClass.newInstance();
        assertEquals (expectedScriptMessage, scriptObject.run())

        //now we reload the script and test that the new one is working
        script.reload()
        scriptClass=ScriptManager.getInstance().getScript(script.scriptFile);
        scriptObject = scriptClass.newInstance();
        assertEquals (newScriptMessage, scriptObject.run())
        
                        


    }
     void testUpdateScriptLoadsNewScriptFileAndRemovesOldIfNotUsedAndDoesNotRemoveOldIfUsed()
     {

        initializeForCmdbScript();
        ScriptScheduler.metaClass.unscheduleScript={ String scriptName ->

        }

        ScriptManager.getInstance().addScript(simpleScriptFile);
        assertNotNull(ScriptManager.getInstance().getScript(simpleScriptFile));

        def newScriptFile="CmdbScriptOperationsTestScriptFile2.groovy";
        def newScriptMessage="new script message"
        createSimpleScript (newScriptFile,newScriptMessage);

        def params=[name:"myscript",type:CmdbScript.ONDEMAND,scriptFile:simpleScriptFile]
        def script=new CmdbScript();
        params.each{ key , val ->
            script[key]=val
        }
        def updateParams=[name:"myscript2",type:CmdbScript.ONDEMAND,scriptFile:newScriptFile]

         //we test that old script is removed from ScriptManager
        CompassForTests.countHitsValue=0

        def updatedScript=CmdbScript.updateScript(script,updateParams,false);
        assertEquals(updatedScript.name,updateParams.name)
        assertEquals(updatedScript.scriptFile,updateParams.scriptFile)

        assertNull(ScriptManager.getInstance().getScript(params.scriptFile));
        def scriptClass=ScriptManager.getInstance().getScript(updateParams.scriptFile)
        assertNotNull(scriptClass);

        //tests the new script file is really loaded and can be runned
        def scriptObject = scriptClass.newInstance();
        assertEquals (newScriptMessage, scriptObject.run())

        //we test that old script is not removed from ScriptManager
        ScriptManager.getInstance().addScript(simpleScriptFile);
        ScriptManager.getInstance().removeScript(newScriptFile);
        assertNotNull(ScriptManager.getInstance().getScript(simpleScriptFile));
        assertNull(ScriptManager.getInstance().getScript(newScriptFile));
        params.each{ key , val ->
            script[key]=val
        }
        CompassForTests.countHitsValue=5

        updatedScript=CmdbScript.updateScript(script,updateParams,false);
        assertEquals(updatedScript.name,updateParams.name)
        assertEquals(updatedScript.scriptFile,updateParams.scriptFile)

        assertNotNull(ScriptManager.getInstance().getScript(params.scriptFile));
        scriptClass=ScriptManager.getInstance().getScript(updateParams.scriptFile)
        assertNotNull(scriptClass);

        //tests the new script file is really loaded and can be runned
        scriptObject = scriptClass.newInstance();
        assertEquals (newScriptMessage, scriptObject.run())
        
     }    
     void testUpdateScriptsGeneratesExceptionWhenErrorOccurs()
     {
         initializeForCmdbScript();
         CmdbScript.metaClass.hasErrors = {  return true;}

         def sampleBean = CmdbScript.newInstance()
         Errors errors = new BeanPropertyBindingResult(sampleBean, sampleBean.getClass().getName());
         CmdbScript.metaClass.errors = errors


         def params=[name:"CmdbScriptOperationsTestScript",type:CmdbScript.ONDEMAND,scriptFile:simpleScriptFile]
         def scriptToUpdate=new CmdbScript();
         params.each{ key , val ->
            scriptToUpdate[key]=val
         }


         //def messageServiceClass=ClassLoader.getSystemClassLoader().loadClass("MessageService");
         def messageServiceClass=this.class.classLoader.loadClass("MessageService");
         messageServiceClass.metaClass.getMessage = { param1 -> return "injectedTestMessage"}

         scriptToUpdate.messageService=messageServiceClass.newInstance()
         GroovySystem.metaClassRegistry.removeMetaClass(messageServiceClass)


         try{
            CmdbScript.updateScript(scriptToUpdate,params,false);
            fail("should throw exception")
         }
         catch(e)
         {
             assertEquals(e.getMessage(),"injectedTestMessage");
         }


     }
     void testUpdateScriptDoesNotGenerateExceptionIfFromController()
     {
         initializeForCmdbScript();
         CmdbScript.metaClass.hasErrors = {  return true;}
         def params=[name:"CmdbScriptOperationsTestScript",type:CmdbScript.ONDEMAND,scriptFile:simpleScriptFile]
         def scriptToUpdate=new CmdbScript();
         params.each{ key , val ->
            scriptToUpdate[key]=val
         }


         try{
            def script=CmdbScript.updateScript(scriptToUpdate,params,true);
            assertTrue(script.hasErrors());
         }
         catch(e)
         {
             println e
             fail("should not throw exception")
         }
     }
     void testUpdateScriptCallsSchedulerForScheduledScripts()
     {
        initializeForCmdbScript();
        ScriptScheduler.metaClass.unscheduleScript={ String scriptName ->

        }

        def schedulerMap=[:];

        ScriptScheduler.metaClass.scheduleScript= { String scriptName, long startDelay, long period ->
            schedulerMap.scriptName=scriptName
            schedulerMap.startDelay=startDelay
            schedulerMap.period=period
        }

        ScriptScheduler.metaClass.scheduleScript= { String scriptName, long startDelay, String cronExp ->
            schedulerMap.scriptName=scriptName
            schedulerMap.startDelay=startDelay
            schedulerMap.cronExp=cronExp
        }


        //testing for periodic script
        def updateParams=[name:"myscript",type:CmdbScript.SCHEDULED,scheduleType:CmdbScript.PERIODIC,scriptFile:simpleScriptFile,enabled:true,startDelay:10,period:20]
        def params=[name:"myscript",type:CmdbScript.ONDEMAND,scriptFile:simpleScriptFile]
        def scriptToUpdate=new CmdbScript();
        params.each{ key , val ->
            scriptToUpdate[key]=val
        }


        def script=CmdbScript.updateScript(scriptToUpdate,updateParams,false)
        println schedulerMap
        assertEquals(updateParams.name,schedulerMap.scriptName)
        assertEquals(updateParams.startDelay,schedulerMap.startDelay)
        assertEquals(updateParams.period,schedulerMap.period)

        //testing for cron script
        schedulerMap=[:];
        updateParams=[name:"myscript",type:CmdbScript.SCHEDULED,scheduleType:CmdbScript.CRON,scriptFile:simpleScriptFile,enabled:true,startDelay:10,cronExpression : "* * * * * ?"]
        params=[name:"myscript",type:CmdbScript.ONDEMAND,scriptFile:simpleScriptFile]
        scriptToUpdate=new CmdbScript();
        params.each{ key , val ->
            scriptToUpdate[key]=val
        }

         script=CmdbScript.updateScript(scriptToUpdate,updateParams,false)
         println schedulerMap
         assertEquals(updateParams.name,schedulerMap.scriptName)
         assertEquals(updateParams.startDelay,schedulerMap.startDelay)
         assertEquals(updateParams.period,schedulerMap.period)



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

     void testRunScriptPassesStaticParamAndStaticParamMapToScript()
     {
         initializeForCmdbScript();

         def scriptFile="mytestscriptfile.groovy"
         def scriptContent="return [staticParam:staticParam,staticParamMap:staticParamMap]"
         createScript(scriptFile,scriptContent);
         ScriptManager.getInstance().addScript (scriptFile)
         
         def onDemandScript=new CmdbScript(name:"testscript",type:CmdbScript.ONDEMAND,scriptFile:scriptFile,staticParam:"x:5,y:6");
         def params=[:]

         def result=CmdbScript.runScript(onDemandScript,params)
         assertEquals(result.staticParam,onDemandScript.staticParam)
         assertEquals(result.staticParamMap.x,"5")
         assertEquals(result.staticParamMap.y,"6")

         def scheduledScript=new CmdbScript(name:"testscript",type:CmdbScript.SCHEDULED,scriptFile:scriptFile,staticParam:"x:7,y:8");
         result=CmdbScript.runScript(scheduledScript,params)
         assertEquals(result.staticParam,scheduledScript.staticParam)
         assertEquals(result.staticParamMap.x,"7")
         assertEquals(result.staticParamMap.y,"8")

         def listeningScript=new CmdbScript(name:"testscript",type:CmdbScript.LISTENING,scriptFile:scriptFile,staticParam:"x:10,y:11");
         result=CmdbScript.runScript(listeningScript,params)
         assertEquals(result.staticParam,listeningScript.staticParam)
         assertEquals(result.staticParamMap.x,"10")
         assertEquals(result.staticParamMap.y,"11")


     }
     void testRunScriptPassesParametersToScriptManager()
     {

         def managerParams=[:]
         ScriptManager.metaClass.runScript={ scriptPath, bindings,scriptLogger ->            
            managerParams.scriptPath=scriptPath
            managerParams.bindings=bindings
            managerParams.scriptLogger=scriptLogger
            return "myrunscript";
         }

         initializeForCmdbScript();

         ScriptManager.getInstance().addScript (simpleScriptFile);

         def script=new CmdbScript(name:"testscript",type:CmdbScript.ONDEMAND,scriptFile:simpleScriptFile);
         def params=["param1":"1","param2":"a"]
         def oldParams=[:]
         oldParams.putAll(params);
         

         def result=CmdbScript.runScript(script,params)
         assertEquals(result,"myrunscript")
         assertEquals(managerParams.scriptPath,script.scriptFile)
         assertEquals(managerParams.scriptLogger,CmdbScript.getScriptLogger(script))
         assertEquals(managerParams.bindings.staticParam,script.staticParam)
         assertEquals(managerParams.bindings.staticParamMap,CmdbScript.getStaticParamMap(script))
         assertEquals(managerParams.bindings.size(),oldParams.size()+2)
         oldParams.each{  key , val ->
             assertEquals(val,managerParams.bindings[key])
         }
         
     }
     def createSimpleScript(scriptName,scriptMessage)
    {
        def scriptFile = new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write ("""return "$scriptMessage" """);
    }
    def createScript(scriptName,scriptContent)
    {
        def scriptFile = new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY/$scriptName");
        scriptFile.write (scriptContent);
    }

}


