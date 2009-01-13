package message

import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import script.CmdbScript

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 13, 2009
 * Time: 3:12:46 PM
 * To change this template use File | Settings | File Templates.
 */
class EmailSenderScriptIntegrationTests extends RapidCmdbIntegrationTestCase {
    static transactional = false;
    
    def destination="abdurrahim"
    void setUp() throws Exception {
        super.setUp();
        CmdbScript.list().each{
            it.remove();
        }
    }

    void tearDown() throws Exception {
        super.tearDown();
    }

    void testSenderRecievesConnectorNameFromStaticParam(){
         assertEquals(CmdbScript.list().size(),0)
         
         def createScript=CmdbScript.addScript([name:"createDefaults"])
         CmdbScript.runScript(createScript.name);
         
         assertTrue(CmdbScript.list().size()>1)

         def senderScript=CmdbScript.get(name:"emailSender")
         assertEquals(senderScript.staticParam,"connectorName:emailConnector")

         def newScriptFileName="emailSenderStaticParamTest.groovy"
         File newScriptFile=new File(System.getProperty("base.dir")+"/scripts/"+newScriptFileName)
         newScriptFile.write("return [staticParam:staticParam,staticParamMap:staticParamMap]")

         CmdbScript.updateScript(senderScript,[scriptFile:newScriptFileName,name:senderScript.name],false)
         assertFalse(senderScript.hasErrors())
         assertEquals(senderScript.scriptFile,newScriptFileName)
         

         senderScript.reload();
         
         def result=CmdbScript.runScript(senderScript.name);
         assertEquals(result.staticParam,senderScript.staticParam)
         assertEquals(result.staticParamMap.connectorName,"emailConnector")
         
    }

}