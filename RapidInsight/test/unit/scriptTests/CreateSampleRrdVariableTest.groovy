package scriptTests

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.scripting.ScriptManagerForTest
import com.ifountain.rcmdb.test.util.CompassForTests

/**
* User: ifountain
* Date: Jul 3, 2009
* Time: 10:39:03 AM
*/
class CreateSampleRrdVariableTest extends RapidCmdbWithCompassTestCase {

    def rrdVariable;
      
    public void setUp() {
        super.setUp();
        def rrdArchive = this.class.classLoader.loadClass("RrdArchive");
        rrdVariable = this.class.classLoader.loadClass("RrdVariable");
        def rrdVariableOperations = this.class.classLoader.loadClass("RrdVariableOperations")
        initialize([rrdArchive, rrdVariable], []);
        CompassForTests.addOperationSupport(rrdVariable, rrdVariableOperations);
        initializeScriptManager();
    }

    public void tearDown() {
        new File("sampleRrdDB.rrd").delete();
        super.tearDown();
    }

    void initializeScriptManager() {
        def base_directory = getWorkspacePath()+"/RapidModules/RapidInsight/scripts"
        println "base path is :"+new File(base_directory).getCanonicalPath();
        ScriptManagerForTest.initialize(gcl,base_directory);
        ScriptManagerForTest.addScript('createDefaultRrdArchives');
        ScriptManagerForTest.addScript('createSampleRrdVariable');
    }

    public void testVariableCreatedSuccessfully(){
        //default archives must be available in RI to create default rrd variable
        def archiveResult = ScriptManagerForTest.runScript("createDefaultRrdArchives",[:]);

        def result = ScriptManagerForTest.runScript("createSampleRrdVariable",[:]);

        def variable = rrdVariable.get(name:"sampleRrdVariable");

        assertTrue("Sample rrd variable is not created", variable!=null);
        assertTrue("Sample rrd db file is not created", new File("sampleRrdDB.rrd").exists());
    }

}