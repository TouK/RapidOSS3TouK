import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import script.CmdbScript

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 20, 2009
* Time: 5:35:46 PM
* To change this template use File | Settings | File Templates.
*/
class ManualTestScriptIntegrationTests extends RapidCmdbIntegrationTestCase
{
    static transactional = false;

    public void setUp() {
        super.setUp();
    }

    public void tearDown() {
        super.tearDown();

    }
    void copyScript(scriptName)
    {

        def scriptPath= "test/manualTestScripts/scripts/${scriptName}.groovy";

        def ant=new AntBuilder();

        ant.copy(file: scriptPath, toDir: "scripts",overwrite:true);
        
    }

    public void testAllDatasourceTest()
    {
        def scriptName="AllDatasourceTest";
        copyScript(scriptName);
        def script=CmdbScript.addScript([name:scriptName,type: CmdbScript.ONDEMAND],true)        
        assertFalse(script.errors.toString(),script.hasErrors());

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
