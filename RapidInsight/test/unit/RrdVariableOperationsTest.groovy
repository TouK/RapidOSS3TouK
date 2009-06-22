import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.rrd.RrdUtils;

/**
* User: ifountain
* Date: Jun 22, 2009
* Time: 5:00:47 PM
*/
class RrdVariableOperationsTest extends RapidCmdbWithCompassTestCase {

    public void setUp() {
        super.setUp();
        initialize([RrdVariable,RrdArchive], []);
        CompassForTests.addOperationSupport(RrdVariable, RrdVariableOperations);

    }

    public void tearDown() {
        super.tearDown();
    }

    public void testCreateDBConfigSuccessfulWithOneArchive(){
        def archive1 = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:10)
        def variable = RrdVariable.add(name:"variable", resource:"resource",
                                       type:"GAUGE", heartbeat:300, file:"filename",
                                       startTime:9000L, step:300L, archives: archive1)


        assertFalse(variable.errors.toString(), variable.hasErrors())

        def config = variable.createDBConfig()

        assertEquals("filename", config[RrdUtils.DATABASE_NAME])
        assertEquals(9000L, config[RrdUtils.START_TIME])
        assertEquals(300L, config[RrdUtils.STEP])

        def datapointList = []

        def datapointConfig = [:]
        datapointConfig[RrdUtils.NAME] = "variable"
        datapointConfig[RrdUtils.TYPE] = "GAUGE"
        datapointConfig[RrdUtils.HEARTBEAT] = 300L
        datapointConfig[RrdUtils.MAX] = Double.NaN
        datapointConfig[RrdUtils.MIN] = Double.NaN
        datapointList.add(datapointConfig)

        assertEquals(datapointList, config[RrdUtils.DATASOURCE])

        def archiveList = []

        def archiveConfig = [:]
        archiveConfig[RrdUtils.FUNCTION] = "AVERAGE"
        archiveConfig[RrdUtils.XFF] = 0.5D
        archiveConfig[RrdUtils.STEPS] = 1L
        archiveConfig[RrdUtils.ROWS] = 10L
        archiveList.add(archiveConfig)

        assertEquals(archiveList, config[RrdUtils.ARCHIVE])
    }

    public void testCreateDBConfigSuccessfulWithMultipleArchive(){
        def archive1 = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:10)
        def archive2 = RrdArchive.add(name:"archive2", function:"MAX", xff:0.2, step:6, row:5)
        def archive3 = RrdArchive.add(name:"archive3", function:"MIN", xff:0.7, step:3, row:15)

        def variable = RrdVariable.add(name:"variable", resource:"resource",
                                       type:"GAUGE", heartbeat:300, file:"filename",
                                       startTime:9000L, step:300L,
                                       archives: [archive1, archive2, archive3])


        assertFalse(variable.errors.toString(), variable.hasErrors())

        def config = variable.createDBConfig()

        assertEquals("filename", config[RrdUtils.DATABASE_NAME])
        assertEquals(9000L, config[RrdUtils.START_TIME])
        assertEquals(300L, config[RrdUtils.STEP])

        def datapointList = []

        def datapointConfig = [:]
        datapointConfig[RrdUtils.NAME] = "variable"
        datapointConfig[RrdUtils.TYPE] = "GAUGE"
        datapointConfig[RrdUtils.HEARTBEAT] = 300L
        datapointConfig[RrdUtils.MAX] = Double.NaN
        datapointConfig[RrdUtils.MIN] = Double.NaN
        datapointList.add(datapointConfig)

        assertEquals(datapointList, config[RrdUtils.DATASOURCE])

        def archiveList = []

        def archiveConfig1 = [:]
        archiveConfig1[RrdUtils.FUNCTION] = "AVERAGE"
        archiveConfig1[RrdUtils.XFF] = 0.5D
        archiveConfig1[RrdUtils.STEPS] = 1L
        archiveConfig1[RrdUtils.ROWS] = 10L
        archiveList.add(archiveConfig1)

        def archiveConfig2 = [:]
        archiveConfig2[RrdUtils.FUNCTION] = "MAX"
        archiveConfig2[RrdUtils.XFF] = 0.2D
        archiveConfig2[RrdUtils.STEPS] = 6L
        archiveConfig2[RrdUtils.ROWS] = 5L
        archiveList.add(archiveConfig2)

        def archiveConfig3 = [:]
        archiveConfig3[RrdUtils.FUNCTION] = "MIN"
        archiveConfig3[RrdUtils.XFF] = 0.7D
        archiveConfig3[RrdUtils.STEPS] = 3L
        archiveConfig3[RrdUtils.ROWS] = 15L
        archiveList.add(archiveConfig3)

        assertEquals(archiveList, config[RrdUtils.ARCHIVE])
    }

}