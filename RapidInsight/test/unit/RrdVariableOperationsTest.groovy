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
        rrdFile.mkdirs();
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

        assertEquals(fileName, config["databaseName"])
        assertEquals(9000L, config["startTime"])
        assertEquals(300L, config["step"])

        def datapointList = []

        def datapointConfig = [:]
        datapointConfig["name"] = "variable"
        datapointConfig["type"] = "GAUGE"
        datapointConfig["heartbeat"] = 300L
        datapointConfig["max"] = Double.NaN
        datapointConfig["min"] = Double.NaN
        datapointList.add(datapointConfig)

        assertEquals(datapointList, config["datasource"])

        def archiveList = []

        def archiveConfig = [:]
        archiveConfig["function"] = "AVERAGE"
        archiveConfig["steps"] = 1L
        archiveConfig["rows"] = 10L
        archiveConfig["xff"] = 0.5D
        archiveList.add(archiveConfig)

        assertEquals(archiveList, config["archive"])
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

        assertEquals(fileName, config["databaseName"])
        assertEquals(9000L, config["startTime"])
        assertEquals(300L, config["step"])

        def datapointList = []

        def datapointConfig = [:]
        datapointConfig["name"] = "variable"
        datapointConfig["type"] = "GAUGE"
        datapointConfig["heartbeat"] = 300L
        datapointConfig["max"] = Double.NaN
        datapointConfig["min"] = Double.NaN
        datapointList.add(datapointConfig)

        assertEquals(datapointList, config["datasource"])

        def archiveList = []

        def archiveConfig1 = [:]
        archiveConfig1["function"] = "AVERAGE"
        archiveConfig1["xff"] = 0.5D
        archiveConfig1["steps"] = 1L
        archiveConfig1["rows"] = 10L
        archiveList.add(archiveConfig1)

        def archiveConfig2 = [:]
        archiveConfig2["function"] = "MAX"
        archiveConfig2["xff"] = 0.2D
        archiveConfig2["steps"] = 6L
        archiveConfig2["rows"] = 5L
        archiveList.add(archiveConfig2)

        def archiveConfig3 = [:]
        archiveConfig3["function"] = "MIN"
        archiveConfig3["xff"] = 0.7D
        archiveConfig3["steps"] = 3L
        archiveConfig3["rows"] = 15L
        archiveList.add(archiveConfig3)

        assertEquals(archiveList, config["archive"])
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

        config["databaseName"] = fileName
        //get databaseinfo does not returns db start time rather archive start time
        //config["startTime"] = 9000L
        config["step"] = 300L

        def datapointList = []

        def datapointConfig = [:]
        datapointConfig["name"] = "variable"
        datapointConfig["type"] = "GAUGE"
        datapointConfig["heartbeat"] = 300L
        datapointConfig["max"] = Double.NaN
        datapointConfig["min"] = Double.NaN
        datapointList.add(datapointConfig)

        config["datasource"] = datapointList

        def archiveList = []

        def archiveConfig = [:]
        archiveConfig["function"] = "AVERAGE"
        archiveConfig["xff"] = 0.5D
        archiveConfig["steps"] = 1
        archiveConfig["rows"] = 10


        dbConfig["archive"].get(0).remove("startTime")
        dbConfig.remove("endTime")
        dbConfig.remove("startTime")

        archiveList.add(archiveConfig)

        config["archive"] = archiveList

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
                                       startTime:9000000, step:300, archives: archive)
        assertFalse(variable.errors.toString(), variable.hasErrors())

        variable.createDB()

        variable.updateDB(time:9300000, value:500)

        assertEquals(500D, RrdUtils.fetchData(fileName, "variable", "AVERAGE", 9300000L, 9300000L)[0])
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
                                       startTime:9000000, step:300, archives: archive)
        assertFalse(variable.errors.toString(), variable.hasErrors())

        variable.createDB()

        variable.updateDB( [[time:9300000L, value:5], [time:9600000L, value:10], [time:9900000L, value:15]] )

        assertEquals([5D,10D,15D], RrdUtils.fetchData(fileName, "variable", "AVERAGE", 9300000, 9900000)[0,1,2])
    }

    public void testGraphWithoutTemplate() {

        def archive1 = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:24)
        assertFalse(archive1.errors.toString(), archive1.hasErrors())

        def archive2 = RrdArchive.add(name:"archive2", function:"AVERAGE", xff:0.5, step:6, row:10)
        assertFalse(archive2.errors.toString(), archive2.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource",
                                       type:"COUNTER", heartbeat:600, file: fileName,
                                       startTime:920804400000L, archives: [archive1, archive2])
        assertFalse(variable.errors.toString(), variable.hasErrors())

        variable.createDB()

        variable.updateDB(time:920804700000L, value:12345)
        variable.updateDB(time:920805000000L, value:12357)
        variable.updateDB(time:920805300000L, value:12363)
        variable.updateDB(time:920805600000L, value:12363)
        variable.updateDB(time:920805900000L, value:12363)
        variable.updateDB(time:920806200000L, value:12373)
        variable.updateDB(time:920806500000L, value:12383)
        variable.updateDB(time:920806800000L, value:12393)
        variable.updateDB(time:920807100000L, value:12399)
        variable.updateDB(time:920807400000L, value:12405)
        variable.updateDB(time:920807700000L, value:12411)
        variable.updateDB(time:920808000000L, value:12415)
        variable.updateDB(time:920808300000L, value:12420)
        variable.updateDB(time:920808600000L, value:12422)
        variable.updateDB(time:920808900000L, value:12423)

        variable.graph(title:"Graph Without Template", startTime:920804400000L, endTime:920808000000L,
                       vlabel:"Vertical Label", color:"FF0000", type:"line", description:"Red Line", destination: imageFileName)

        assertTrue(new File(imageFileName).exists())

        /*
        DataOutputStream outputStream = new DataOutputStream( new FileOutputStream(imageFileName))
        outputStream.write(data)
        */
    }

    public void testGraphWithDefaultProperties() {

        def archive1 = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:24)
        assertFalse(archive1.errors.toString(), archive1.hasErrors())

        def archive2 = RrdArchive.add(name:"archive2", function:"AVERAGE", xff:0.5, step:6, row:10)
        assertFalse(archive2.errors.toString(), archive2.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource",
                                       type:"COUNTER", heartbeat:600, file: fileName,
                                       startTime:920804400000L, archives: [archive1, archive2])
        assertFalse(variable.errors.toString(), variable.hasErrors())

        variable.createDB()

        variable.updateDB(time:920804700000L, value:12345)
        variable.updateDB(time:920805000000L, value:12357)
        variable.updateDB(time:920805300000L, value:12363)
        variable.updateDB(time:920805600000L, value:12363)
        variable.updateDB(time:920805900000L, value:12363)
        variable.updateDB(time:920806200000L, value:12373)
        variable.updateDB(time:920806500000L, value:12383)
        variable.updateDB(time:920806800000L, value:12393)
        variable.updateDB(time:920807100000L, value:12399)
        variable.updateDB(time:920807400000L, value:12405)
        variable.updateDB(time:920807700000L, value:12411)
        variable.updateDB(time:920808000000L, value:12415)
        variable.updateDB(time:920808300000L, value:12420)
        variable.updateDB(time:920808600000L, value:12422)
        variable.updateDB(time:920808900000L, value:12423)

        variable.graph(title:"Graph Without Template", startTime:920804400000L,
                       endTime:920808000000L, destination:imageFileName)

        assertTrue(new File(imageFileName).exists())

        /*
        DataOutputStream outputStream = new DataOutputStream( new FileOutputStream(imageFileName))
        outputStream.write(data)
        */
    }
    
    public void testGraphWithRPNSource() {

        def archive1 = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:24)
        assertFalse(archive1.errors.toString(), archive1.hasErrors())

        def archive2 = RrdArchive.add(name:"archive2", function:"AVERAGE", xff:0.5, step:6, row:10)
        assertFalse(archive2.errors.toString(), archive2.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource",
                                       type:"COUNTER", heartbeat:600, file: fileName,
                                       startTime:920804400000L, step:300, archives: [archive1, archive2])
        assertFalse(variable.errors.toString(), variable.hasErrors())

        variable.createDB()

        variable.updateDB(time:920804700000L, value:12345)
        variable.updateDB(time:920805000000L, value:12357)
        variable.updateDB(time:920805300000L, value:12363)
        variable.updateDB(time:920805600000L, value:12363)
        variable.updateDB(time:920805900000L, value:12363)
        variable.updateDB(time:920806200000L, value:12373)
        variable.updateDB(time:920806500000L, value:12383)
        variable.updateDB(time:920806800000L, value:12393)
        variable.updateDB(time:920807100000L, value:12399)
        variable.updateDB(time:920807400000L, value:12405)
        variable.updateDB(time:920807700000L, value:12411)
        variable.updateDB(time:920808000000L, value:12415)
        variable.updateDB(time:920808300000L, value:12420)
        variable.updateDB(time:920808600000L, value:12422)
        variable.updateDB(time:920808900000L, value:12423)

        variable.graph(title:"Graph With RPN Source", color: "0000FF", startTime:920804400000L, endTime:920808000000L,
                       rpn:"variable,1000,*", destination:imageFileName)

        assertTrue(new File(imageFileName).exists())

        /*
        DataOutputStream outputStream = new DataOutputStream( new FileOutputStream(imageFileName))
        outputStream.write(data)
        */
    }

    public void testGraphWithMultipleSource() {
  
        def archive = RrdArchive.add(name:"archive", function:"AVERAGE", xff:0.5, step:1, row:10)
        assertFalse(archive.errors.toString(), archive.hasErrors())

        def variable1 = RrdVariable.add(name:"variable1", resource:"resource",
                                       type:"GAUGE", heartbeat:600, file: fileName,
                                       startTime:978300900000L, archives: [archive])
       assertFalse(variable1.errors.toString(), variable1.hasErrors())

        def variable2 = RrdVariable.add(name:"variable2", resource:"resource",
                                        type:"COUNTER", heartbeat:600, file:fileNameExt,
                                        startTime:978300900000L, archives: [archive])
        assertFalse(variable2.errors.toString(), variable2.hasErrors())

        variable1.createDB()
        variable2.createDB()

        variable1.updateDB(time:978301200000, value:1)
        variable1.updateDB(time:978301500000, value:3)
        variable1.updateDB(time:978301800000, value:5)
        variable1.updateDB(time:978302100000, value:3)
        variable1.updateDB(time:978302400000, value:1)
        variable1.updateDB(time:978302700000, value:2)
        variable1.updateDB(time:978303000000, value:4)
        variable1.updateDB(time:978303300000, value:6)
        variable1.updateDB(time:978303600000, value:4)
        variable1.updateDB(time:978303900000, value:2)

        variable2.updateDB(time:978301200000, value:300)
        variable2.updateDB(time:978301500000, value:600)
        variable2.updateDB(time:978301800000, value:900)
        variable2.updateDB(time:978302100000, value:1200)
        variable2.updateDB(time:978302400000, value:1500)
        variable2.updateDB(time:978302700000, value:1800)
        variable2.updateDB(time:978303000000, value:2100)
        variable2.updateDB(time:978303300000, value:2400)
        variable2.updateDB(time:978303600000, value:2700)
        variable2.updateDB(time:978303900000, value:3000)

        def config = [:]
        config["title"] = "Graph With Multiple Source"
        config["startTime"] = 978300600000L
        config["endTime"] = 978304200000L
        config["destination"] = imageFileName

        config["rrdVariables"] = []
        config["rrdVariables"].add([rrdVariable:"variable1", color:"FF0000", type:"line", description:"Variable 1"])
        config["rrdVariables"].add([rrdVariable:"variable2", color:"00FF00", type:"line", description:"Variable 2"])

        RrdUtils.graph(config)

        assertTrue(new File(imageFileName).exists())

        /*
        DataOutputStream outputStream = new DataOutputStream( new FileOutputStream(imageFileName))
        outputStream.write(data)
        */
    }

    public void testGraphWithMultipleSourceandRPN() {

        def archive1 = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:24)
        assertFalse(archive1.errors.toString(), archive1.hasErrors())

        def archive2 = RrdArchive.add(name:"archive2", function:"AVERAGE", xff:0.5, step:6, row:10)
        assertFalse(archive2.errors.toString(), archive2.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource",
                                       type:"COUNTER", heartbeat:600, file: fileName,
                                       startTime:920804400000L, step:300, archives: [archive1, archive2])

        assertFalse(variable.errors.toString(), variable.hasErrors())

        variable.createDB()

        variable.updateDB(time:920804700000L, value:12345)
        variable.updateDB(time:920805000000L, value:12357)
        variable.updateDB(time:920805300000L, value:12363)
        variable.updateDB(time:920805600000L, value:12363)
        variable.updateDB(time:920805900000L, value:12363)
        variable.updateDB(time:920806200000L, value:12373)
        variable.updateDB(time:920806500000L, value:12383)
        variable.updateDB(time:920806800000L, value:12393)
        variable.updateDB(time:920807100000L, value:12399)
        variable.updateDB(time:920807400000L, value:12405)
        variable.updateDB(time:920807700000L, value:12411)
        variable.updateDB(time:920808000000L, value:12415)
        variable.updateDB(time:920808300000L, value:12420)
        variable.updateDB(time:920808600000L, value:12422)
        variable.updateDB(time:920808900000L, value:12423)

        def config = [:]
        config["title"] = "Graph With Multiple Source"
        config["startTime"] = 920804400000L
        config["endTime"] = 920808000000L
        config["destination"] = imageFileName

        config["rrdVariables"] = []
        config["rrdVariables"].add([rrdVariable:"variable", color:"000000", type:"line", thickness:4, rpn:"variable,3600,*",description:"km/h"])
        config["rrdVariables"].add([rrdVariable:"variable", color:"FF0000", type:"area", rpn:"variable,3600,*,100,GT,variable,3600,*,0,IF", description:"Fast"])
        config["rrdVariables"].add([rrdVariable:"variable", color:"00FF00", type:"area", rpn:"variable,3600,*,100,GT,0,variable,3600,*,IF", description:"Good"])

        byte[] data = RrdUtils.graph(config)
                                                                    
        assertTrue(new File(imageFileName).exists())
        
        /*
        DataOutputStream outputStream = new DataOutputStream( new FileOutputStream(imageFileName))
        outputStream.write(data)
        */
        
    }

}