import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import script.CmdbScript

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Apr 8, 2009
* Time: 2:20:14 PM
* To change this template use File | Settings | File Templates.
*/
class RIManualTestScriptIntegrationTests extends RapidCmdbIntegrationTestCase{
    static transactional = false;
    void setUp() throws Exception {
        super.setUp();
        CmdbScript.list().each {
            CmdbScript.deleteScript(it);
        }
    }
    void tearDown() throws Exception {
        super.tearDown();
    }
    void copyManualTestScript(scriptFolder,scriptName)
    {

        def scriptPath= "../../../RapidModules/RapidInsight/test/manualTestScripts/${scriptFolder}/${scriptName}.groovy";

        def ant=new AntBuilder();

        ant.copy(file: scriptPath, toDir: "scripts",overwrite:true);

    }

     void copyScript(scriptName)
    {

        def scriptPath= "../../../RapidModules/RapidInsight/scripts/${scriptName}.groovy";

        def ant=new AntBuilder();

        ant.copy(file: scriptPath, toDir: "scripts",overwrite:true);

    }
    void copyOverridenOperation(path)
    {
        def ant=new AntBuilder();

        ant.copy(file: path, toDir: "operations",overwrite:true);
    }
    public void testRsTicketOperations()
    {
        def oprPath = "../../../RapidModules/RapidInsight/solutions/jiraTicketing/operations/RsTicketOperations.groovy"
        copyOverridenOperation(oprPath);
        RsTicket.reloadOperations();
        datasource.JiraDatasource.reloadOperations();

        copyManualTestScript("ticketing","RsTicketOperationsTest");

        def script=CmdbScript.addScript([name:"RsTicketOperationsTest",type: CmdbScript.ONDEMAND],true)
        println script.errors
        assertFalse(script.hasErrors());

         try{
            def result=CmdbScript.runScript(script,[:]);
        }
        catch(e)
        {
            e.printStackTrace();
            fail("Error in script. Reason ${e}");

        }

    }


}