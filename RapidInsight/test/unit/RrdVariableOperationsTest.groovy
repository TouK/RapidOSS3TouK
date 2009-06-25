import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.comp.test.util.file.TestFile
import com.ifountain.rcmdb.rrd.RrdUtils;
import com.ifountain.rcmdb.rrd.Grapher;

/**
* User: ifountain
* Date: Jun 22, 2009
* Time: 5:00:47 PM
*/
class RrdVariableOperationsTest extends RapidCmdbWithCompassTestCase {

    String fileName = TestFile.TESTOUTPUT_DIR + "/vartest.rrd";
    String fileNameExt = TestFile.TESTOUTPUT_DIR + "/vartest2.rrd";
    String imageFileName = TestFile.TESTOUTPUT_DIR + "/imageOutput.png"

    public void setUp() {
        super.setUp();
        initialize([RrdVariable,RrdArchive], []);
        CompassForTests.addOperationSupport(RrdVariable, RrdVariableOperations);
        def rrdFile = new File(fileName);
        def imageFile = new File(imageFileName)
        assertTrue(rrdFile.mkdirs());
        rrdFile.delete();
        imageFile.delete();
    }

    public void tearDown() {
        new File(fileName).delete();
        new File(fileNameExt).delete();
        //new File(imageFileName).delete();
        super.tearDown();
    }

    public void testCreateDBConfigSuccessfulWithOneArchive() {

        def archive = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:10)

        assertFalse(archive.errors.toString(), archive.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource",
                                       type:"GAUGE", heartbeat:300, file:fileName,
                                       startTime:9000L, step:300L, archives: archive)
        assertFalse(variable.errors.toString(), variable.hasErrors())

        def config = variable.createDBConfig()

        assertEquals(fileName, config[RrdUtils.DATABASE_NAME])
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
        archiveConfig[RrdUtils.STEPS] = 1L
        archiveConfig[RrdUtils.ROWS] = 10L
        archiveConfig[RrdUtils.XFF] = 0.5D
        archiveList.add(archiveConfig)

        assertEquals(archiveList, config[RrdUtils.ARCHIVE])
    }

    public void testCreateDBConfigSuccessfulWithMultipleArchive() {

        def archive1 = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:10)
        assertFalse(archive1.errors.toString(), archive1.hasErrors())

        def archive2 = RrdArchive.add(name:"archive2", function:"MAX", xff:0.2, step:6, row:5)
        assertFalse(archive2.errors.toString(), archive2.hasErrors())

        def archive3 = RrdArchive.add(name:"archive3", function:"MIN", xff:0.7, step:3, row:15)
        assertFalse(archive3.errors.toString(), archive3.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource",
                                       type:"GAUGE", heartbeat:300, file:fileName,
                                       startTime:9000L, step:300L,
                                       archives: [archive1, archive2, archive3])
        assertFalse(variable.errors.toString(), variable.hasErrors())

        def config = variable.createDBConfig()

        assertEquals(fileName, config[RrdUtils.DATABASE_NAME])
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

    public void testCreateUpdateDataSingleTimeandValue() {

        def variable = RrdVariable.add(name:"variable")
        
        Map props = [time:10000L, value:5]

        String data = variable.createUpdateData(props)

        assertEquals("10000:5", data)
    }

    public void testCreateDBSuccessful() {

        def archive = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:10)
        assertFalse(archive.errors.toString(), archive.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource",
                                       type:"GAUGE", heartbeat:300, file: fileName,
                                       startTime:9000, step:300, archives: archive)
        assertFalse(variable.errors.toString(), variable.hasErrors())

        variable.createDB()

        def dbConfig = RrdUtils.getDatabaseInfo(fileName)

        def config = [:]

        config[RrdUtils.DATABASE_NAME] = fileName
        config[RrdUtils.START_TIME] = 9000L
        config[RrdUtils.STEP] = 300L

        def datapointList = []

        def datapointConfig = [:]
        datapointConfig[RrdUtils.NAME] = "variable"
        datapointConfig[RrdUtils.TYPE] = "GAUGE"
        datapointConfig[RrdUtils.HEARTBEAT] = 300L
        datapointConfig[RrdUtils.MAX] = Double.NaN
        datapointConfig[RrdUtils.MIN] = Double.NaN
        datapointList.add(datapointConfig)

        config[RrdUtils.DATASOURCE] = datapointList

        def archiveList = []

        def archiveConfig = [:]
        archiveConfig[RrdUtils.FUNCTION] = "AVERAGE"
        archiveConfig[RrdUtils.XFF] = 0.5D
        archiveConfig[RrdUtils.STEPS] = 1
        archiveConfig[RrdUtils.ROWS] = 10


        dbConfig[RrdUtils.ARCHIVE].get(0).remove(RrdUtils.START_TIME)

        archiveList.add(archiveConfig)

        config[RrdUtils.ARCHIVE] = archiveList

        assertEquals(config, dbConfig)

    }

    public void testRemoveDBSuccessful() {

        def archive = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:10)
        assertFalse(archive.errors.toString(), archive.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource",
                                       type:"GAUGE", heartbeat:300, file: fileName,
                                       startTime:9000, step:300, archives: archive)
        assertFalse(variable.errors.toString(), variable.hasErrors())

        variable.createDB()

        assertTrue(RrdUtils.isDatabaseExists(fileName))

        variable.removeDB()

        assertFalse(RrdUtils.isDatabaseExists(fileName))

    }

    public void testUpdateSingleTimeandValue() {

        def archive = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:10)

        assertFalse(archive.errors.toString(), archive.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource",
                                       type:"GAUGE", heartbeat:300, file: fileName,
                                       startTime:9000, step:300, archives: archive)
        assertFalse(variable.errors.toString(), variable.hasErrors())

        variable.createDB()

        variable.updateDB(time:9300, value:500)

        assertEquals(500D, RrdUtils.fetchData(fileName, "variable", "AVERAGE", 9300L, 9300L)[0])
    }

    public void testUpdateOnlyValue() {

        def archive = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:10)
        assertFalse(archive.errors.toString(), archive.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource",
                                       type:"GAUGE", heartbeat:300, file: fileName,
                                       startTime:9000, step:300, archives: archive)
        assertFalse(variable.errors.toString(), variable.hasErrors())

        variable.createDB()

        //how to test date 'now'
        variable.updateDB(value:500)
    }

    public void testUpdateMultipleTimeandValue() {

        def archive = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:10)
        assertFalse(archive.errors.toString(), archive.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource",
                                       type:"GAUGE", heartbeat:300, file: fileName,
                                       startTime:9000, step:300, archives: archive)
        assertFalse(variable.errors.toString(), variable.hasErrors())

        variable.createDB()

        variable.updateDB( [[time:9300L, value:5], [time:9600L, value:10], [time:9900L, value:15]] )

        assertEquals([5D,10D,15D], RrdUtils.fetchData(fileName, "variable", "AVERAGE", 9300, 9900)[0,1,2])
    }

    public void testGraphWithoutTemplate() {

        def archive1 = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:24)
        assertFalse(archive1.errors.toString(), archive1.hasErrors())

        def archive2 = RrdArchive.add(name:"archive2", function:"AVERAGE", xff:0.5, step:6, row:10)
        assertFalse(archive2.errors.toString(), archive2.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource",
                                       type:"COUNTER", heartbeat:600, file: fileName,
                                       startTime:920804400L, archives: [archive1, archive2])
        assertFalse(variable.errors.toString(), variable.hasErrors())

        variable.createDB()

        variable.updateDB(time:920804700L, value:12345)
        variable.updateDB(time:920805000L, value:12357)
        variable.updateDB(time:920805300L, value:12363)
        variable.updateDB(time:920805600L, value:12363)
        variable.updateDB(time:920805900L, value:12363)
        variable.updateDB(time:920806200L, value:12373)
        variable.updateDB(time:920806500L, value:12383)
        variable.updateDB(time:920806800L, value:12393)
        variable.updateDB(time:920807100L, value:12399)
        variable.updateDB(time:920807400L, value:12405)
        variable.updateDB(time:920807700L, value:12411)
        variable.updateDB(time:920808000L, value:12415)
        variable.updateDB(time:920808300L, value:12420)
        variable.updateDB(time:920808600L, value:12422)
        variable.updateDB(time:920808900L, value:12423)

        byte[] data = variable.graph(title:"Graph Without Template", startTime:920804400L, endTime:920808000L,
                       vLabel:"vlabel", color:"FF0000", type:"area", description:"Red Line")
        
        DataOutputStream outputStream = new DataOutputStream( new FileOutputStream(imageFileName))
        outputStream.write(data)
    }

    public void testGraphWithDefaultProperties() {

        def archive1 = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:24)
        assertFalse(archive1.errors.toString(), archive1.hasErrors())

        def archive2 = RrdArchive.add(name:"archive2", function:"AVERAGE", xff:0.5, step:6, row:10)
        assertFalse(archive2.errors.toString(), archive2.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource",
                                       type:"COUNTER", heartbeat:600, file: fileName,
                                       startTime:920804400L, archives: [archive1, archive2])
        assertFalse(variable.errors.toString(), variable.hasErrors())

        variable.createDB()

        variable.updateDB(time:920804700L, value:12345)
        variable.updateDB(time:920805000L, value:12357)
        variable.updateDB(time:920805300L, value:12363)
        variable.updateDB(time:920805600L, value:12363)
        variable.updateDB(time:920805900L, value:12363)
        variable.updateDB(time:920806200L, value:12373)
        variable.updateDB(time:920806500L, value:12383)
        variable.updateDB(time:920806800L, value:12393)
        variable.updateDB(time:920807100L, value:12399)
        variable.updateDB(time:920807400L, value:12405)
        variable.updateDB(time:920807700L, value:12411)
        variable.updateDB(time:920808000L, value:12415)
        variable.updateDB(time:920808300L, value:12420)
        variable.updateDB(time:920808600L, value:12422)
        variable.updateDB(time:920808900L, value:12423)

        byte[] data = variable.graph(title:"Graph Without Template", startTime:920804400L, endTime:920808000L)

        DataOutputStream outputStream = new DataOutputStream( new FileOutputStream(imageFileName))
        outputStream.write(data)
    }
    /*
    public void testGraphWithRPNSource() {

        def archive1 = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:24)
        assertFalse(archive1.errors.toString(), archive1.hasErrors())

        def archive2 = RrdArchive.add(name:"archive2", function:"AVERAGE", xff:0.5, step:6, row:10)
        assertFalse(archive2.errors.toString(), archive2.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource",
                                       type:"COUNTER", heartbeat:600, file: fileName,
                                       startTime:920804400L, archives: [archive1, archive2])
        assertFalse(variable.errors.toString(), variable.hasErrors())

        variable.createDB()

        variable.updateDB(time:920804700L, value:12345)
        variable.updateDB(time:920805000L, value:12357)
        variable.updateDB(time:920805300L, value:12363)
        variable.updateDB(time:920805600L, value:12363)
        variable.updateDB(time:920805900L, value:12363)
        variable.updateDB(time:920806200L, value:12373)
        variable.updateDB(time:920806500L, value:12383)
        variable.updateDB(time:920806800L, value:12393)
        variable.updateDB(time:920807100L, value:12399)
        variable.updateDB(time:920807400L, value:12405)
        variable.updateDB(time:920807700L, value:12411)
        variable.updateDB(time:920808000L, value:12415)
        variable.updateDB(time:920808300L, value:12420)
        variable.updateDB(time:920808600L, value:12422)
        variable.updateDB(time:920808900L, value:12423)

        byte[] data = variable.graph(title:"Graph With RPN Source", startTime:920804400L, endTime:920808000L,
                       rpn:"variable,1000,*")

        DataOutputStream outputStream = new DataOutputStream( new FileOutputStream(imageFileName))
        outputStream.write(data)

        //TODO: test graph output

    }
    */
    public void testGraphWithMultipleSource() {
  
        def archive = RrdArchive.add(name:"archive", function:"AVERAGE", xff:0.5, step:1, row:10)
        assertFalse(archive.errors.toString(), archive.hasErrors())

        def variable1 = RrdVariable.add(name:"variable1", resource:"resource",
                                       type:"GAUGE", heartbeat:600, file: fileName,
                                       startTime:978300900L, archives: [archive])
       assertFalse(variable1.errors.toString(), variable1.hasErrors())

        def variable2 = RrdVariable.add(name:"variable2", resource:"resource",
                                        type:"COUNTER", heartbeat:600, file:fileNameExt,
                                        startTime:978300900L, archives: [archive])
        assertFalse(variable2.errors.toString(), variable2.hasErrors())

        variable1.createDB()
        variable2.createDB()

        variable1.updateDB(time:978301200, value:1)
        variable1.updateDB(time:978301500, value:3)
        variable1.updateDB(time:978301800, value:5)
        variable1.updateDB(time:978302100, value:3)
        variable1.updateDB(time:978302400, value:1)
        variable1.updateDB(time:978302700, value:2)
        variable1.updateDB(time:978303000, value:4)
        variable1.updateDB(time:978303300, value:6)
        variable1.updateDB(time:978303600, value:4)
        variable1.updateDB(time:978303900, value:2)

        variable2.updateDB(time:978301200, value:300)
        variable2.updateDB(time:978301500, value:600)
        variable2.updateDB(time:978301800, value:900)
        variable2.updateDB(time:978302100, value:1200)
        variable2.updateDB(time:978302400, value:1500)
        variable2.updateDB(time:978302700, value:1800)
        variable2.updateDB(time:978303000, value:2100)
        variable2.updateDB(time:978303300, value:2400)
        variable2.updateDB(time:978303600, value:2700)
        variable2.updateDB(time:978303900, value:3000)

        def config = [:]
        config[Grapher.TITLE] = "Graph With Multiple Source"
        config[Grapher.START_TIME] = 978300600L
        config[Grapher.END_TIME] = 978304200L

        config[Grapher.RRD_VARIABLES] = []
        config[Grapher.RRD_VARIABLES].add([rrdVariable:"variable1", color:"FF0000", type:"line", description:"Variable 1"])
        config[Grapher.RRD_VARIABLES].add([rrdVariable:"variable2", color:"00FF00", type:"line", description:"Variable 2"])

        byte[] data = RrdUtils.graph(config)

        DataOutputStream outputStream = new DataOutputStream( new FileOutputStream(imageFileName))
        outputStream.write(data)
    }

}