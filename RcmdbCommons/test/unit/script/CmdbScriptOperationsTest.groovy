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
import com.ifountain.rcmdb.datasource.ListeningAdapterManager
import com.ifountain.rcmdb.scripting.ScriptManager
import com.ifountain.rcmdb.scripting.ScriptScheduler
import com.ifountain.rcmdb.test.util.CompassForTests
import org.apache.commons.io.FileUtils
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors

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
        ExpandoMetaClass.enableGlobally();
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(ListeningAdapterManager)
        GroovySystem.metaClassRegistry.removeMetaClass(ScriptScheduler)
        GroovySystem.metaClassRegistry.removeMetaClass(ScriptManager)
        GroovySystem.metaClassRegistry.removeMetaClass(CmdbScript)
        ExpandoMetaClass.enableGlobally();
     }


     void initializeForCmdbScript(){
         ScriptManager manager = ScriptManager.getInstance();
        if(new File(base_directory).exists())
        {
            FileUtils.deleteDirectory (new File(base_directory));
        }
        manager.initialize(this.class.getClassLoader(), base_directory, [], [:]);
        new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY").mkdirs();
        
         createSimpleScript (simpleScriptFile,expectedScriptMessage);
         
         CompassForTests.initialize([CmdbScript]);
         CompassForTests.addOperationSupport (CmdbScript, CmdbScriptOperations);
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


