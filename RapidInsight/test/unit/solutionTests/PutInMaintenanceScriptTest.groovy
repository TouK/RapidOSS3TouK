package solutionTests

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.test.util.scripting.ScriptManagerForTest

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jun 26, 2009
* Time: 1:22:07 PM
* To change this template use File | Settings | File Templates.
*/
class PutInMaintenanceScriptTest extends RapidCmdbWithCompassTestCase {

    def RsInMaintenance;
    def RsInMaintenanceSchedule;
    def RsEvent;
    def RsHistoricalInMaintenance;


    def RsInMaintenanceOperations;
    def RsInMaintenanceScheduleOperations;

    public void setUp() {
        super.setUp();
        GroovyClassLoader loader = new GroovyClassLoader();
        def op_base_directory = getWorkspacePath() + "/RapidModules/RapidInsight/solutions/inMaintenance/operations"

        ["RsInMaintenance", "RsInMaintenanceSchedule", "RsHistoricalInMaintenance", "RsEvent"].each {className ->
            setProperty(className, gcl.loadClass(className));
        }

        ["RsInMaintenanceOperations", "RsInMaintenanceScheduleOperations"].each {className ->
            setProperty(className, loader.parseClass(new File("${op_base_directory}/${className}.groovy")));
        }

        initializeScriptManager();

        initialize([RsInMaintenance, RsInMaintenanceSchedule, RsHistoricalInMaintenance, RsEvent], []);
        CompassForTests.addOperationSupport(RsInMaintenance, RsInMaintenanceOperations);
        CompassForTests.addOperationSupport(RsInMaintenanceSchedule, RsInMaintenanceScheduleOperations);
    }

    public void tearDown() {
        super.tearDown();
    }

    void initializeScriptManager()
    {
        def base_directory = getWorkspacePath() + "/RapidModules/RapidInsight/solutions/inMaintenance/scripts"
        println "base path is :" + new File(base_directory).getCanonicalPath();
        ScriptManagerForTest.initialize(gcl, base_directory);
        ScriptManagerForTest.addScript("putInMaintenance");

    }

    public void testPutInMaintenanceWithoutMinutesAndThenTakeObjectOutOfMaintenance()
    {
        assertEquals(0, RsInMaintenance.count());
        def params = [maintenanceType: "maintenance", objectName: "testobj", inMaintenance: "true", info: "manualMaintenance"];
        def webParams = [session: [username: "testuser"]];

        def startTimeForMaintenance = System.currentTimeMillis();

        runScript(params, webParams);
        assertEquals(1, RsInMaintenance.count());
        def maintObject = RsInMaintenance.list()[0];

        assertEquals(params.objectName, maintObject.objectName);
        assertEquals(params.info, maintObject.info);
        assertEquals("User ${webParams.session.username}", maintObject.source);
        assertEquals(new Date(0), maintObject.ending);
        assertTrue(maintObject.starting.getTime() >= startTimeForMaintenance);


        //we remove inMaintenance key, and it will be taken out of maintenance
        params = [maintenanceType: "maintenance", objectName: "testobj", info: "manualMaintenance"];
        runScript(params, webParams);
        assertEquals(0, RsInMaintenance.count());
    }
    public void testPutInMaintenanceWithMinutes()
    {
        assertEquals(0, RsInMaintenance.count());
        def params = [maintenanceType: "maintenance", objectName: "testobj", inMaintenance: "true", info: "manualMaintenance", minutes: "10"];
        def webParams = [session: [username: "testuser"]];

        def startTimeForMaintenance = System.currentTimeMillis();

        runScript(params, webParams);
        assertEquals(1, RsInMaintenance.count());
        def maintObject = RsInMaintenance.list()[0];


        assertEquals(params.objectName, maintObject.objectName);
        assertEquals(params.info, maintObject.info);
        assertEquals("User ${webParams.session.username}", maintObject.source);
        //10 minutes make 600 seconds
        def timeDiff = (maintObject.ending.getTime() - Date.now()) / 1000;
        assertTrue(timeDiff > 599);
        assertTrue(timeDiff < 601);
        assertTrue(maintObject.starting.getTime() >= startTimeForMaintenance);
    }
    public void testPutInMaintenanceWithMinutesThrowsExceptionIfMinutesIsNotAValidInteger()
    {

        def params = [maintenanceType: "maintenance", objectName: "testobj", inMaintenance: "true", info: "manualMaintenance", minutes: "sdsadasdasd"];
        def webParams = [session: [username: "testuser"]];

        try {
            runScript(params, webParams);
            fail("should throw : Minutes should be an integer")
        }
        catch (e)
        {
            assertEquals("Wrong Exception ${e}", e.getMessage().toString(), "Minutes should be an integer")
        }

    }

    private void runScript(params, webParams)
    {
        ScriptManagerForTest.runScript("putInMaintenance", ["params": params, "web": webParams])
    }

}