import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.comp.test.util.file.TestFile
import com.ifountain.rcmdb.rrd.RrdUtils;

/**
* User: ifountain
* Date: Jun 22, 2009
* Time: 5:00:47 PM
*/
class RrdVariableOperationsTest extends RapidCmdbWithCompassTestCase {

    String fileName = "variable.rrd";
    String fileNameExt = "variable2.rrd";
    String imageFileName = "imageOutput.png"
    String fileDirectory = "rrdFiles"
    String rrdFileName = "testRrd.rrd";
    String testImageFile = "testImage.png"

    public void setUp() {
        super.setUp();
        initialize([RrdVariable,RrdArchive, RrdGraphTemplate], []);
        CompassForTests.addOperationSupport(RrdVariable, RrdVariableOperations);
        def rrdFile = new File(fileDirectory);
        rrdFile.mkdirs();
        new File(fileDirectory + "/" + fileName).delete()
        new File(fileDirectory + "/" + imageFileName).delete()
    }

    public void tearDown() {
//        new File(fileDirectory + "/" + fileName).delete()
//        new File(fileDirectory + "/" + fileNameExt).delete()
//        new File(fileDirectory + "/" + imageFileName).delete()
//        new File(fileDirectory).delete()
        super.tearDown();
    }
//
//    public void testAddRrdVariableSuccessfully() {
//        def archive = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:10)
//        def variable = RrdVariable.add(name:"variable", resource:"resource",
//                                       type:"GAUGE", heartbeat:300,
//                                       startTime:9000L, step:300L, archives: archive);
//        variable = RrdVariable.get(name:"variable");
//        println new File(fileName).getAbsolutePath();
//        assertEquals("Rrd file path is not proper",
//                RrdVariableOperations.RRD_FOLDER+"vartest.rrd",variable.getFilePath());
//    }

    public void testCreateDBConfigSuccessfulWithOneArchive() {

        def archive = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:10)

        assertFalse(archive.errors.toString(), archive.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource",
                                       type:"GAUGE", heartbeat:300,
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
                                       type:"GAUGE", heartbeat:300,
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
                                       type:"GAUGE", heartbeat:300,
                                       startTime:9000, step:300, archives: archive)
        assertFalse(variable.errors.toString(), variable.hasErrors())


        variable.createDB()

        def dbConfig = RrdUtils.getDatabaseInfo("variable.rrd")

        assertEquals("rrdFiles/variable.rrd", dbConfig["databaseName"])
        assertEquals(300L, dbConfig["step"])

        def datapointList = []
        def datapointConfig = [:]
        datapointConfig["name"] = "variable"
        datapointConfig["type"] = "GAUGE"
        datapointConfig["heartbeat"] = 300L
        datapointConfig["max"] = Double.NaN
        datapointConfig["min"] = Double.NaN
        datapointList.add(datapointConfig)

        assertEquals(datapointList, dbConfig["datasource"])


        def archiveList = []
        def archiveConfig = [:]
        archiveConfig["function"] = "AVERAGE"
        archiveConfig["steps"] = 1
        archiveConfig["rows"] = 10
        archiveConfig["xff"] = 0.5D
        archiveList.add(archiveConfig)
        dbConfig["archive"].get(0).remove("startTime")
        
        assertEquals(archiveList, dbConfig["archive"])

        new File(fileDirectory + "/variable.rrd").delete()
    }

    public void testRemoveDBSuccessful() {

        def archive = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:10)
        assertFalse(archive.errors.toString(), archive.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource",
                                       type:"GAUGE", heartbeat:300,
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
                                       type:"GAUGE", heartbeat:300,
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
                                       type:"GAUGE", heartbeat:300,
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
                                       type:"GAUGE", heartbeat:300,
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
                                       type:"COUNTER", heartbeat:600,
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

        assertTrue(new File(fileDirectory + "/" + imageFileName).exists())
    }

    public void testGraphWithDefaultProperties() {

        def archive1 = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:24)
        assertFalse(archive1.errors.toString(), archive1.hasErrors())

        def archive2 = RrdArchive.add(name:"archive2", function:"AVERAGE", xff:0.5, step:6, row:10)
        assertFalse(archive2.errors.toString(), archive2.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource",
                                       type:"COUNTER", heartbeat:600,
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

        assertTrue(new File(fileDirectory + "/" + imageFileName).exists())
    }
    
    public void testGraphWithRPNSource() {

        def archive1 = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:24)
        assertFalse(archive1.errors.toString(), archive1.hasErrors())

        def archive2 = RrdArchive.add(name:"archive2", function:"AVERAGE", xff:0.5, step:6, row:10)
        assertFalse(archive2.errors.toString(), archive2.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource",
                                       type:"COUNTER", heartbeat:600,
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

        assertTrue(new File(fileDirectory + "/" + imageFileName).exists())
    }

    public void testGraphWithMultipleSource() {
  
        def archive = RrdArchive.add(name:"archive", function:"AVERAGE", xff:0.5, step:1, row:10)
        assertFalse(archive.errors.toString(), archive.hasErrors())

        def variable1 = RrdVariable.add(name:"variable1", resource:"resource",
                                       type:"GAUGE", heartbeat:600,
                                       startTime:978300900000L, archives: [archive])
       assertFalse(variable1.errors.toString(), variable1.hasErrors())

        def variable2 = RrdVariable.add(name:"variable2", resource:"resource",
                                        type:"COUNTER", heartbeat:600,
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

        RrdVariableOperations.graphMultiple(config)

        assertTrue(new File(fileDirectory + "/" + imageFileName).exists())
    }

    public void testGraphWithMultipleSourceandRPN() {

        def archive1 = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:24)
        assertFalse(archive1.errors.toString(), archive1.hasErrors())

        def archive2 = RrdArchive.add(name:"archive2", function:"AVERAGE", xff:0.5, step:6, row:10)
        assertFalse(archive2.errors.toString(), archive2.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource",
                                       type:"COUNTER", heartbeat:600,
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

        byte[] data = RrdVariableOperations.graphMultiple(config)
                                                                    
        assertTrue(new File(fileDirectory + "/" + imageFileName).exists())
    }

    public void testGraphWithSingleSourceandTemplate() {

        def archive1 = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:24)
        assertFalse(archive1.errors.toString(), archive1.hasErrors())

        def archive2 = RrdArchive.add(name:"archive2", function:"AVERAGE", xff:0.5, step:6, row:10)
        assertFalse(archive2.errors.toString(), archive2.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource",
                                       type:"COUNTER", heartbeat:600,
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

        def template = RrdGraphTemplate.add(name:"templateSample", title:"Graph With Template")

        config["template"] = "templateSample"
        config["startTime"] = 920804400000L
        config["endTime"] = 920808000000L
        config["destination"] = imageFileName

        config["rrdVariables"] = []
        config["rrdVariables"].add([rrdVariable:"variable", color:"000000", type:"line", thickness:4, rpn:"variable,3600,*",description:"km/h"])
        config["rrdVariables"].add([rrdVariable:"variable", color:"FF0000", type:"area", rpn:"variable,3600,*,100,GT,variable,3600,*,0,IF", description:"Fast"])
        config["rrdVariables"].add([rrdVariable:"variable", color:"00FF00", type:"area", rpn:"variable,3600,*,100,GT,0,variable,3600,*,IF", description:"Good"])

        byte[] data = RrdVariableOperations.graphMultiple(config)

        assertTrue(new File(fileDirectory + "/" + imageFileName).exists())
    }

    public void testGraphWithMultipleSourceandTemplate() {

        def archive1 = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:24)
        assertFalse(archive1.errors.toString(), archive1.hasErrors())

        def archive2 = RrdArchive.add(name:"archive2", function:"AVERAGE", xff:0.5, step:6, row:10)
        assertFalse(archive2.errors.toString(), archive2.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource",
                                       type:"COUNTER", heartbeat:600, startTime:920804400000L,
                                       step:300, archives: [archive1, archive2])

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

        def template = RrdGraphTemplate.add(name:"templateSample", title:"Graph With Template",
                                            verticalLabel:"vertical label", type:"area", description:"inside template",
                                            color:"FF00FF")

        config["vlabel"] = "overwritten label"
        config["template"] = "templateSample"
        config["startTime"] = 920804400000L
        config["endTime"] = 920808000000L
        config["destination"] = imageFileName

        variable.graph(config)

        assertTrue(new File(fileDirectory + "/" + imageFileName).exists())

        new File(fileDirectory + "/variable.rrd").delete()
    }

    public void testFileSource() {

        def variable1 = RrdVariable.add(name:"variable1")
        def fileName = variable1.fileSource()

        assertEquals("variable1.rrd", fileName)

        def variable2 = RrdVariable.add(name:"variable2", file:"explicitDef")
        fileName = variable2.fileSource()
        
        assertEquals(fileNameExt, fileName)

    }


    public void testOneDatasourceGraphSuccessfully() throws Exception{
        Map config = [:]
        config[RrdVariableOperations.DATABASE_NAME] = rrdFileName;
        config[RrdVariableOperations.DATASOURCE] = [
                                            [
                                                name:"testDs1",
                                                type:"COUNTER",
                                                heartbeat:600,
                                            ],
                                            [
                                                name:"testDs2",
                                                type:"GAUGE",
                                                heartbeat:600
                                            ]
                                      ]

        config[RrdVariableOperations.ARCHIVE] = [
                                        [
                                            function:"AVERAGE",
                                            xff:0.5,
                                            steps:1,
                                            rows:24,
                                        ]
                                   ]
        config[RrdVariableOperations.START_TIME] = 978300900000;
        RrdUtils.createDatabase(config)

        RrdUtils.updateData(rrdFileName,"978301200000:200:1");
        RrdUtils.updateData(rrdFileName,"978301500000:400:4");
        RrdUtils.updateData(rrdFileName,"978301800000:900:5");
        RrdUtils.updateData(rrdFileName,"978302100000:1200:3");
        RrdUtils.updateData(rrdFileName,"978302400000:1400:1");
        RrdUtils.updateData(rrdFileName,"978302700000:1900:2");
        RrdUtils.updateData(rrdFileName,"978303000000:2100:4");
        RrdUtils.updateData(rrdFileName,"978303300000:2400:6");
        RrdUtils.updateData(rrdFileName,"978303600000:2900:4");
        RrdUtils.updateData(rrdFileName,"978303900000:3300:2");

        def archive1 = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:24)

        def rrdvariable = RrdVariable.add(name:"testDs2", resource:"resource",
                           type:"GAUGE", heartbeat:600,
                           startTime:978300900000, archives: [archive1])

        rrdvariable.createDB();

        def map = [:];
        map[RrdVariableOperations.START_TIME] = 978301200000;
        //Optional properties:
        map[RrdVariableOperations.END_TIME] = 978303900000;
        map[RrdVariableOperations.DESCRIPTION] = "cpu util";
        map[RrdVariableOperations.TYPE] = "area";
        map[RrdVariableOperations.COLOR] = "5566ff";
        map[RrdVariableOperations.MAX] = 10;
        map['destination']="testOneDatasourceGraphSuccessfully.png";

        def rrdVar = RrdVariable.get(name:"testDs2");

        byte[] bytes = rrdVar.graph(map);

        assertTrue("Grapher returns no graph info",bytes!=null);

        File f=new File(fileDirectory + "/" + map['destination']);
        println f.getAbsolutePath();
        assertTrue("image file does not exists.",f.exists());
    }
    public void testMultipleDatasourceGraphDatasourcesSuccessfully() throws Exception{
        Map config = [:]
        config[RrdVariableOperations.DATABASE_NAME] = rrdFileName
        config[RrdVariableOperations.DATASOURCE] = [
                                            [
                                                name:"testDs1",
                                                type:"COUNTER",
                                                heartbeat:600,
                                            ],
                                            [
                                                name:"testDs2",
                                                type:"GAUGE",
                                                heartbeat:600
                                            ]
                                      ]

        config[RrdVariableOperations.ARCHIVE] = [
                                        [
                                            function:"AVERAGE",
                                            xff:0.5,
                                            steps:1,
                                            rows:24,
                                        ]
                                   ]
        config[RrdVariableOperations.START_TIME] = 978300900000;
        RrdUtils.createDatabase(config)

        RrdUtils.updateData(rrdFileName,"978301200000:200:1");
        RrdUtils.updateData(rrdFileName,"978301500000:400:4");
        RrdUtils.updateData(rrdFileName,"978301800000:900:5");
        RrdUtils.updateData(rrdFileName,"978302100000:1200:3");
        RrdUtils.updateData(rrdFileName,"978302400000:1400:1");
        RrdUtils.updateData(rrdFileName,"978302700000:1900:2");
        RrdUtils.updateData(rrdFileName,"978303000000:2100:4");
        RrdUtils.updateData(rrdFileName,"978303300000:2400:6");
        RrdUtils.updateData(rrdFileName,"978303600000:2900:4");
        RrdUtils.updateData(rrdFileName,"978303900000:3300:2");

        def archive1 = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:24)

        def rrdvariable1 = RrdVariable.add(name:"testDs1", resource:"resource",
                           type:"COUNTER", heartbeat:600,
                           startTime:978300900000, archives: [archive1])

        def rrdvariable2 = RrdVariable.add(name:"testDs2", resource:"resource",
                           type:"GAUGE", heartbeat:600,
                           startTime:978300900000, archives: [archive1])

        rrdvariable1.createDB();
        rrdvariable2.createDB();

        def rrdList = [
                            [rrdVariable:"testDs1", color:"123456", description:"cpu"],
                            [rrdVariable:"testDs2", color:"aabb22", description:"memory", function:"AVERAGE"]
                        ];
        Map map = [:];
        map[RrdVariableOperations.RRD_VARIABLES] = rrdList;
        map[RrdVariableOperations.START_TIME] = 978301200000
        map[RrdVariableOperations.END_TIME] = 978303900000;
        map["destination"]="testMultipleDatasourceGraphDatasourcesSuccessfully.png";

        byte[] bytes = RrdVariableOperations.graphMultiple(map);

        assertTrue("Grapher returns no graph info",bytes!=null);
        File f=new File(fileDirectory + "/" + map['destination']);
        println(f.getAbsolutePath());
        assertTrue(f.exists());
    }

    public void testMultipleDatasourcesGraphWithDifferentDrawingTypeSuccessfully() throws Exception{
        Map config = [:]
        config[RrdVariableOperations.DATABASE_NAME] = rrdFileName
        config[RrdVariableOperations.DATASOURCE] = [
                                            [
                                                name:"testDs1",
                                                type:"COUNTER",
                                                heartbeat:600,
                                            ],
                                            [
                                                name:"testDs2",
                                                type:"GAUGE",
                                                heartbeat:600
                                            ]
                                      ]

        config[RrdVariableOperations.ARCHIVE] = [
                                        [
                                            function:"AVERAGE",
                                            xff:0.5,
                                            steps:1,
                                            rows:24,
                                        ]
                                   ]
        config[RrdVariableOperations.START_TIME] = 978300900000;
        RrdUtils.createDatabase(config)

        RrdUtils.updateData(rrdFileName,"978301200000:200:1");
        RrdUtils.updateData(rrdFileName,"978301500000:400:4");
        RrdUtils.updateData(rrdFileName,"978301800000:900:5");
        RrdUtils.updateData(rrdFileName,"978302100000:1200:3");
        RrdUtils.updateData(rrdFileName,"978302400000:1400:1");
        RrdUtils.updateData(rrdFileName,"978302700000:1900:2");
        RrdUtils.updateData(rrdFileName,"978303000000:2100:4");
        RrdUtils.updateData(rrdFileName,"978303300000:2400:6");
        RrdUtils.updateData(rrdFileName,"978303600000:2900:4");
        RrdUtils.updateData(rrdFileName,"978303900000:3300:2");

        def archive1 = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:24)

        def rrdvariable1 = RrdVariable.add(name:"testDs1", resource:"resource",
                           type:"COUNTER", heartbeat:600,
                           startTime:978300900000, archives: [archive1])

        def rrdvariable2 = RrdVariable.add(name:"testDs2", resource:"resource",
                           type:"GAUGE", heartbeat:600,
                           startTime:978300900000, archives: [archive1])

        rrdvariable1.createDB();
        rrdvariable2.createDB();

        def rrdList = [
                            [rrdVariable:"testDs1", color:"123456", description:"cpu", rpn:"testDs1,2,*"],
                            [rrdVariable:"testDs2", color:"aabb22", description:"memory", function:"AVERAGE", type:"area"]
                        ];
        Map map = [:];
        map[RrdVariableOperations.RRD_VARIABLES] = rrdList;
        map[RrdVariableOperations.START_TIME] = 978301200000
        map[RrdVariableOperations.END_TIME] = 978303900000;

        map[RrdVariableOperations.TITLE] = "Rrd Graph Utilities";
        map[RrdVariableOperations.VERTICAL_LABEL] = "rate";
        map['destination']="testMultipleDatasourcesGraphWithDifferentDrawingTypeSuccessfully.png";

        byte[] bytes = RrdVariableOperations.graphMultiple(map);
        assertTrue("Grapher returns no graph info",bytes!=null);
        File f=new File(fileDirectory + "/" + map['destination']);
        assertTrue(f.exists());
    }

    public void testOneDatasourceWithOneParameterGraphSuccessfully() throws Exception{
        Map config = [:]
        config[RrdVariableOperations.DATABASE_NAME] = rrdFileName;
        config[RrdVariableOperations.DATASOURCE] = [
                                            [
                                                name:"testDs1",
                                                type:"COUNTER",
                                                heartbeat:600,
                                            ],
                                            [
                                                name:"testDs2",
                                                type:"GAUGE",
                                                heartbeat:600
                                            ]
                                      ]

        config[RrdVariableOperations.ARCHIVE] = [
                                        [
                                            function:"AVERAGE",
                                            xff:0.5,
                                            steps:1,
                                            rows:24,
                                        ]
                                   ]
        config[RrdVariableOperations.START_TIME] = 978300900000;
        RrdUtils.createDatabase(config)

        RrdUtils.updateData(rrdFileName,"978301200000:200:1");
        RrdUtils.updateData(rrdFileName,"978301500000:400:4");
        RrdUtils.updateData(rrdFileName,"978301800000:900:5");
        RrdUtils.updateData(rrdFileName,"978302100000:1200:3");
        RrdUtils.updateData(rrdFileName,"978302400000:1400:1");
        RrdUtils.updateData(rrdFileName,"978302700000:1900:2");
        RrdUtils.updateData(rrdFileName,"978303000000:2100:4");
        RrdUtils.updateData(rrdFileName,"978303300000:2400:6");
        RrdUtils.updateData(rrdFileName,"978303600000:2900:4");
        RrdUtils.updateData(rrdFileName,"978303900000:3300:2");

        def archive1 = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:24)

        def rrdvariable = RrdVariable.add(name:"testDs2", resource:"resource",
                           type:"GAUGE", heartbeat:600,
                           startTime:978300900000, archives: [archive1])

        rrdvariable.createDB();

        def map = [:];
        map[RrdVariableOperations.RRD_VARIABLE] = "testDs2";
        map['destination']="testOneDatasourceWithOneParameterGraphSuccessfully.png";
        //Optional properties:

        def rrdVar = RrdVariable.get(name:"testDs2");

        byte[] bytes = rrdVar.graph(map);

        assertTrue("Grapher returns no graph info",bytes!=null);
        File f=new File(fileDirectory + "/" + map['destination']);
        assertTrue(f.exists());
    }

    public void testOneDatasourceGraphSuccessfullyWithTemplate() throws Exception{
        Map config = [:]
        config[RrdVariableOperations.DATABASE_NAME] = rrdFileName
        config[RrdVariableOperations.DATASOURCE] = [
                                            [
                                                name:"testDs1",
                                                type:"COUNTER",
                                                heartbeat:600,
                                            ],
                                            [
                                                name:"testDs2",
                                                type:"GAUGE",
                                                heartbeat:600
                                            ]
                                      ]

        config[RrdVariableOperations.ARCHIVE] = [
                                        [
                                            function:"AVERAGE",
                                            xff:0.5,
                                            steps:1,
                                            rows:24,
                                        ]
                                   ]
        config[RrdVariableOperations.START_TIME] = 978300900000;
        RrdUtils.createDatabase(config)

        RrdUtils.updateData(rrdFileName,"978301200000:200:1");
        RrdUtils.updateData(rrdFileName,"978301500000:400:4");
        RrdUtils.updateData(rrdFileName,"978301800000:900:5");
        RrdUtils.updateData(rrdFileName,"978302100000:1200:3");
        RrdUtils.updateData(rrdFileName,"978302400000:1400:1");
        RrdUtils.updateData(rrdFileName,"978302700000:1900:2");
        RrdUtils.updateData(rrdFileName,"978303000000:2100:4");
        RrdUtils.updateData(rrdFileName,"978303300000:2400:6");
        RrdUtils.updateData(rrdFileName,"978303600000:2900:4");
        RrdUtils.updateData(rrdFileName,"978303900000:3300:2");

        def archive1 = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:24)

        def rrdvariable = RrdVariable.add(name:"testDs2", resource:"resource",
                           type:"GAUGE", heartbeat:600,
                           startTime:978300900000, archives: [archive1])
        RrdGraphTemplate.add(["name":"tName", "description":"desc",
                    "title":"title", "verticalLabel":"kmh", "width":100,
                    "type":"area","color":"234231"]);

        rrdvariable.createDB();

        def map = [:];
        map[RrdVariableOperations.START_TIME] = 978301200000;
        map[RrdVariableOperations.RRD_VARIABLE] = "testDs2";
        //Optional properties:
        map[RrdVariableOperations.END_TIME] = 978303900000;
        map[RrdVariableOperations.GRAPH_TEMPLATE] = "tName";
        map['destination']="testOneDatasourceGraphSuccessfullyWithTemplate.png";


        byte[] bytes = RrdVariable.get(name:"testDs2").graph(map);

        assertTrue("Grapher returns no graph info",bytes!=null);
        File f=new File(fileDirectory + "/" + map['destination']);
        assertTrue(f.exists());
    }

    public void testOneDatasourceGraphWithRpnSuccessfully() throws Exception{
        Map config = [:]
        config[RrdVariableOperations.DATABASE_NAME] = rrdFileName
        config[RrdVariableOperations.DATASOURCE] = [
                                            [
                                                name:"testDs1",
                                                type:"COUNTER",
                                                heartbeat:600,
                                            ],
                                            [
                                                name:"testDs2",
                                                type:"GAUGE",
                                                heartbeat:600
                                            ]
                                      ]

        config[RrdVariableOperations.ARCHIVE] = [
                                        [
                                            function:"AVERAGE",
                                            xff:0.5,
                                            steps:1,
                                            rows:24,
                                        ]
                                   ]
        config[RrdVariableOperations.START_TIME] = 978300900000;
        RrdUtils.createDatabase(config)

        RrdUtils.updateData(rrdFileName,"978301200000:200:1");
        RrdUtils.updateData(rrdFileName,"978301500000:400:4");
        RrdUtils.updateData(rrdFileName,"978301800000:900:5");
        RrdUtils.updateData(rrdFileName,"978302100000:1200:3");
        RrdUtils.updateData(rrdFileName,"978302400000:1400:1");
        RrdUtils.updateData(rrdFileName,"978302700000:1900:2");
        RrdUtils.updateData(rrdFileName,"978303000000:2100:4");
        RrdUtils.updateData(rrdFileName,"978303300000:2400:6");
        RrdUtils.updateData(rrdFileName,"978303600000:2900:4");
        RrdUtils.updateData(rrdFileName,"978303900000:3300:2");

        def archive1 = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:24)

        def rrdvariable = RrdVariable.add(name:"testDs2", resource:"resource",
                           type:"GAUGE", heartbeat:600,
                           startTime:978300900000, archives: [archive1])

        rrdvariable.createDB();

        def map = [:];
        map[RrdVariableOperations.START_TIME] = 978301200000;
        map[RrdVariableOperations.RRD_VARIABLE] = "testDs2";
        //Optional properties:
        map[RrdVariableOperations.END_TIME] = 978303900000;
        map[RrdVariableOperations.DESCRIPTION] = "my graph description";
        map[RrdVariableOperations.TYPE] = "area";
        map[RrdVariableOperations.RPN] = "testDs2,2,*";

        map[RrdVariableOperations.TITLE] = "Rrd Graph Utilities";
        map[RrdVariableOperations.VERTICAL_LABEL] = "rate";
        map['destination']="testOneDatasourceGraphWithRpnSuccessfully.png";

        byte[] bytes = RrdVariable.get(name:"testDs2").graph(map);
        assertTrue("Grapher returns no graph info",bytes!=null);
        File f=new File(fileDirectory + "/" + map['destination']);
        assertTrue(f.exists());
    }

}