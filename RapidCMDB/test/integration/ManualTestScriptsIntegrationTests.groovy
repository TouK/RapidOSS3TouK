

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.scripting.ScriptManager
import script.CmdbScript
import script.CmdbScriptOperations

import connection.*
import datasource.*
import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase


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
    /*

    def base_directory="";
    public void setUp() {
        super.setUp();
          //to run in Hudson
        base_directory = "../RapidSuite";
        //def canonicalPath=new File(System.getProperty("base.dir", ".")).getCanonicalPath();
        def canonicalPath=new File(".").getCanonicalPath();
        //to run in developer pc
        if(canonicalPath.endsWith("RapidModules"))
        {
            base_directory = "RapidCMDB";
        }
        initializeScriptManager();
    }

    public void tearDown() {
        super.tearDown();

    }
    void initializeScriptManager()
    {
        def script_base_directory= base_directory+"/test/manualTestScripts";
        println "script base path is :"+new File(script_base_directory).getCanonicalPath();

        ScriptManager manager = ScriptManager.getInstance();
        manager.initialize(this.class.getClassLoader(), script_base_directory, [], [:]);
    }

    public void testAllDatasourceTest()
    {
        def script=CmdbScript.addScript([name:"AllDatasourceTest",scriptFile:"AllDatasourceTest.groovy",type: CmdbScript.ONDEMAND],true)
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
   */

}
