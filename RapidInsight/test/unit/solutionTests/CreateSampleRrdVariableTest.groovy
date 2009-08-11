package solutionTests


import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.scripting.ScriptManagerForTest
import com.ifountain.rcmdb.test.util.CompassForTests

class CreateSampleRrdVariableTest extends RapidCmdbWithCompassTestCase {

    def RrdVariable;
    def RrdArchive;
    def RrdVariableOperations;

    public void setUp() {
        super.setUp();

        GroovyClassLoader loader = new GroovyClassLoader();

        def op_base_directory = getWorkspacePath()+"/RapidModules/RapidInsight/solutions/timeSeries/operations"

        ["RrdVariable","RrdArchive"].each{ className ->
            setProperty(className,gcl.loadClass(className));
        }
        ["RrdVariableOperations",].each{ className ->
            setProperty(className,loader.parseClass(new File("${op_base_directory}/${className}.groovy")));
        }
        

        initialize([RrdVariable, RrdArchive], []);
        CompassForTests.addOperationSupport(RrdVariable, RrdVariableOperations);
        initializeScriptManager();
    }

    public void tearDown() {
        new File("rrdFiles/sampleRrdDB.rrd").delete();
        super.tearDown();
    }

    void initializeScriptManager() {
        def base_directory = getWorkspacePath()+"/RapidModules/RapidInsight/solutions/timeSeries/scripts"
        println "base path is :"+new File(base_directory).getCanonicalPath();
        ScriptManagerForTest.initialize(gcl,base_directory);
        ScriptManagerForTest.addScript('createSampleRrdVariable');
    }

    public void testVariableCreatedSuccessfully(){
        //default archives must be available in RI to create default rrd variable
        def result = ScriptManagerForTest.runScript("createSampleRrdVariable",[:]);

        def variable = RrdVariable.get(name:"sampleRrdVariable");

        assertTrue("Sample rrd variable is not created", variable!=null);
        assertTrue("Sample rrd db file is not created", new File("rrdFiles/sampleRrdVariable.rrd").exists());
    }

}