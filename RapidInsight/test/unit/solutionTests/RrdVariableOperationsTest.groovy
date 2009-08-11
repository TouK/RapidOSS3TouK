package solutionTests

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.rrd.RrdUtils
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Aug 11, 2009
 * Time: 5:16:39 PM
 * To change this template use File | Settings | File Templates.
 */
class RrdVariableOperationsTest extends RapidCmdbWithCompassTestCase {
    
    String fileName = "variable.rrd";
    String fileNameExt = "variable2.rrd";
    String imageFileName = "imageOutput.png"
    String fileDirectory = "rrdFiles"
    String rrdFileName = "testRrd.rrd";
    String testImageFile = "testImage.png"

    def RrdVariable;
    def RrdArchive;
    def RrdGraphTemplate;

    def RrdVariableOperations;
    def RrdArchiveOperations;
    
    public void setUp() {
        super.setUp();
        GroovyClassLoader loader = new GroovyClassLoader();
        
        def op_base_directory = getWorkspacePath()+"/RapidModules/RapidInsight/solutions/timeSeries/operations"

        ["RrdVariable","RrdArchive","RrdGraphTemplate"].each{ className ->
            setProperty(className,gcl.loadClass(className));
        }
        ["RrdVariableOperations","RrdArchiveOperations"].each{ className ->
            setProperty(className,loader.parseClass(new File("${op_base_directory}/${className}.groovy")));
        }
        
        initialize([RrdVariable,RrdArchive, RrdGraphTemplate], []);
        CompassForTests.addOperationSupport(RrdVariable, RrdVariableOperations);
        CompassForTests.addOperationSupport(RrdArchive, RrdArchiveOperations);
        RrdVariable.removeAll()
        RrdArchive.removeAll()
        def rrdFile = new File(fileDirectory);
        rrdFile.mkdirs();
        new File(fileDirectory + "/" + fileName).delete()
        new File(fileDirectory + "/" + imageFileName).delete()
    }

    public void tearDown() {
        new File(fileDirectory + "/" + fileName).delete()
        new File(fileDirectory + "/" + fileNameExt).delete()
        new File(fileDirectory + "/" + imageFileName).delete()
        new File(fileDirectory).delete()
        RrdVariable.removeAll()
        RrdArchive.removeAll()
        super.tearDown();
    }

    public void testCreateDBConfigSuccessfulWithOneArchive() {

        def archive = RrdArchive.add(function:"AVERAGE", xff:0.5, step:1, numberOfDatapoints:10)
        assertFalse(archive.errors.toString(), archive.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource", type:"GAUGE", heartbeat:300,
                                       startTime:9000L, frequency:300L, archives: archive)
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

        def archive1 = RrdArchive.add(function:"AVERAGE", xff:0.5, step:1, numberOfDatapoints:10)
        assertFalse(archive1.errors.toString(), archive1.hasErrors())

        def archive2 = RrdArchive.add(function:"MAX", xff:0.2, step:6, numberOfDatapoints:5)
        assertFalse(archive2.errors.toString(), archive2.hasErrors())

        def archive3 = RrdArchive.add(function:"MIN", xff:0.7, step:3, numberOfDatapoints:15)
        assertFalse(archive3.errors.toString(), archive3.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource", type:"GAUGE", heartbeat:300,
                                       startTime:9000L, frequency:300L, archives: [archive1, archive2, archive3])
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

    public void testCreateDBSuccessful() {

        def archive = RrdArchive.add(function:"AVERAGE", xff:0.5, step:1, numberOfDatapoints:10)
        assertFalse(archive.errors.toString(), archive.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource", type:"GAUGE", heartbeat:300,
                                       startTime:9000, frequency:300)
        assertFalse(variable.errors.toString(), variable.hasErrors())
        variable.addArchive(archive.getMap());
        variable.createDB()

        def dbConfig = RrdUtils.getDatabaseInfo(fileName)

        assertEquals(fileDirectory + "/" + fileName, dbConfig["databaseName"])
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

//        assertEquals(archiveList, dbConfig["archive"])
    }

    public void testRemoveDBSuccessful() {

        def archive = RrdArchive.add(function:"AVERAGE", xff:0.5, step:1, numberOfDatapoints:10)
        assertFalse(archive.errors.toString(), archive.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource", type:"GAUGE", heartbeat:300,
                                       startTime:9000, frequency:300)
        assertFalse(variable.errors.toString(), variable.hasErrors())
        variable.addArchive(archive.getMap())

        variable.createDB()

        assertTrue(RrdUtils.isDatabaseExists(fileName))

        variable.removeDB()

        assertFalse(RrdUtils.isDatabaseExists(fileName))
    }

    public void testUpdateSingleTimeandValue() {

        def archive = RrdArchive.add(function:"AVERAGE", xff:0.5, step:1, numberOfDatapoints:10)
        assertFalse(archive.errors.toString(), archive.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource", type:"GAUGE", heartbeat:300,
                                       startTime:9000000, frequency:300)
        assertFalse(variable.errors.toString(), variable.hasErrors())
        variable.addArchive(archive.getMap())

        variable.createDB()
        variable.updateDB(500, 9300000)

        assertEquals(500D, RrdUtils.fetchDataAsMap(fileName, "variable", "AVERAGE", 9300000L, 9300000L)['variable']['9300'])
    }

    public void testUpdateDiscardsBadUpdateTimes() {

        def archive = RrdArchive.add(function:"AVERAGE", xff:0.5, step:1, numberOfDatapoints:10)
        assertFalse(archive.errors.toString(), archive.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource", type:"GAUGE", heartbeat:300,
                                       startTime:9000000, frequency:300)
        assertFalse(variable.errors.toString(), variable.hasErrors())
        variable.addArchive(archive.getMap())

        variable.createDB()
        variable.updateDB(500, 9300000)
        variable.updateDB(600, 9200000)

        assertEquals(500D, RrdUtils.fetchDataAsMap(fileName, "variable", "AVERAGE", 9300000L, 9300000L)['variable']['9300'])
        assertEquals(null, RrdUtils.fetchDataAsMap(fileName, "variable", "AVERAGE", 9200000L, 9200000L)['variable']['9200'])
    }

    public void testUpdateOnlyValue() {

        def archive = RrdArchive.add(function:"AVERAGE", xff:0.5, step:1, numberOfDatapoints:10)
        assertFalse(archive.errors.toString(), archive.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource", type:"GAUGE", heartbeat:300,
                                       startTime:9000, frequency:300)
        assertFalse(variable.errors.toString(), variable.hasErrors())
        variable.addArchive(archive.getMap())

        variable.createDB()

        //how to test date 'now'
        variable.updateDB(500)
    }

    public void testUpdateMultipleTimeandValue() {

        def archive = RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, numberOfDatapoints:10)
        assertFalse(archive.errors.toString(), archive.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource", type:"GAUGE", heartbeat:300,
                                       startTime:9000000, frequency:300)
        assertFalse(variable.errors.toString(), variable.hasErrors())
        variable.addArchive(archive.getMap())

        variable.createDB()

        RrdVariable.updateDB(fileName, [5, 10, 15], [9300000L, 9600000L, 9900000L] )

        assertEquals(['9300':5D,'9600':10D,'9900':15D], RrdUtils.fetchDataAsMap(fileName, "variable", "AVERAGE", 9300000, 9900000)['variable'])
    }

    public void testGraphWithoutTemplate() {

        def archive1 = RrdArchive.add(function:"AVERAGE", xff:0.5, step:1, numberOfDatapoints:24)
        assertFalse(archive1.errors.toString(), archive1.hasErrors())

        def archive2 = RrdArchive.add(function:"AVERAGE", xff:0.5, step:6, numberOfDatapoints:10)
        assertFalse(archive2.errors.toString(), archive2.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource", type:"COUNTER", heartbeat:600,
                                       startTime:920804400000L)
        assertFalse(variable.errors.toString(), variable.hasErrors())
        variable.addArchive(archive1.getMap())
        variable.addArchive(archive2.getMap())

        variable.createDB()

        variable.updateDB(12345,920804700000L)
        variable.updateDB(12357,920805000000L)
        variable.updateDB(12363,920805300000L)
        variable.updateDB(12363,920805600000L)
        variable.updateDB(12363,920805900000L)
        variable.updateDB(12373,920806200000L)
        variable.updateDB(12383,920806500000L)
        variable.updateDB(12393,920806800000L)
        variable.updateDB(12399,920807100000L)
        variable.updateDB(12405,920807400000L)
        variable.updateDB(12411,920807700000L)
        variable.updateDB(12415,920808000000L)
        variable.updateDB(12420,920808300000L)
        variable.updateDB(12422,920808600000L)
        variable.updateDB(12423,920808900000L)

        variable.graph(title:"Graph Without Template", startTime:920804400000L, endTime:920808000000L, type:"line",
                       vlabel:"Vertical Label", color:"FF0000", description:"Red Line", destination: imageFileName)

        assertTrue(new File(fileDirectory + "/" + imageFileName).exists())
    }

    public void testGraphWithDefaultProperties() {

        def archive1 = RrdArchive.add(function:"AVERAGE", xff:0.5, step:1, numberOfDatapoints:24)
        assertFalse(archive1.errors.toString(), archive1.hasErrors())

        def archive2 = RrdArchive.add(function:"AVERAGE", xff:0.5, step:6, numberOfDatapoints:10)
        assertFalse(archive2.errors.toString(), archive2.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource", type:"COUNTER", heartbeat:600,
                                       startTime:920804400000L)
        assertFalse(variable.errors.toString(), variable.hasErrors())
        variable.addArchive(archive1.getMap())
        variable.addArchive(archive2.getMap())

        variable.createDB()

        variable.updateDB(12345,920804700000L)
        variable.updateDB(12357,920805000000L)
        variable.updateDB(12363,920805300000L)
        variable.updateDB(12363,920805600000L)
        variable.updateDB(12363,920805900000L)
        variable.updateDB(12373,920806200000L)
        variable.updateDB(12383,920806500000L)
        variable.updateDB(12393,920806800000L)
        variable.updateDB(12399,920807100000L)
        variable.updateDB(12405,920807400000L)
        variable.updateDB(12411,920807700000L)
        variable.updateDB(12415,920808000000L)
        variable.updateDB(12420,920808300000L)
        variable.updateDB(12422,920808600000L)
        variable.updateDB(12423,920808900000L)

        variable.graph(title:"Graph Without Template", startTime:920804400000L,
                       endTime:920808000000L, destination:imageFileName)

        assertTrue(new File(fileDirectory + "/" + imageFileName).exists())
    }

    public void testGraphWithRPNSource() {

        def archive1 = RrdArchive.add(function:"AVERAGE", xff:0.5, step:1, numberOfDatapoints:24)
        assertFalse(archive1.errors.toString(), archive1.hasErrors())

        def archive2 = RrdArchive.add(function:"AVERAGE", xff:0.5, step:6, numberOfDatapoints:10)
        assertFalse(archive2.errors.toString(), archive2.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource", type:"COUNTER", heartbeat:600,
                                       startTime:920804400000L, frequency:300)
        assertFalse(variable.errors.toString(), variable.hasErrors())
        variable.addArchive(archive1.getMap())
        variable.addArchive(archive2.getMap())

        variable.createDB()

        variable.updateDB(12345,920804700000L)
        variable.updateDB(12357,920805000000L)
        variable.updateDB(12363,920805300000L)
        variable.updateDB(12363,920805600000L)
        variable.updateDB(12363,920805900000L)
        variable.updateDB(12373,920806200000L)
        variable.updateDB(12383,920806500000L)
        variable.updateDB(12393,920806800000L)
        variable.updateDB(12399,920807100000L)
        variable.updateDB(12405,920807400000L)
        variable.updateDB(12411,920807700000L)
        variable.updateDB(12415,920808000000L)
        variable.updateDB(12420,920808300000L)
        variable.updateDB(12422,920808600000L)
        variable.updateDB(12423,920808900000L)

        variable.graph(title:"Graph With RPN Source", color: "0000FF", startTime:920804400000L, endTime:920808000000L,
                       rpn:"variable,1000,*", destination:imageFileName)

        assertTrue(new File(fileDirectory + "/" + imageFileName).exists())
    }

    public void testGraphMultipleTakingTwoMap() {
        def archive = RrdArchive.add(function:"AVERAGE", xff:0.5, step:1, numberOfDatapoints:10)
        assertFalse(archive.errors.toString(), archive.hasErrors())

        def variable1 = RrdVariable.add(name:"variable1", resource:"resource", type:"GAUGE", heartbeat:600,
                                       startTime:978300900000L)
        assertFalse(variable1.errors.toString(), variable1.hasErrors())
        variable1.addArchive(archive.getMap())

        def variable2 = RrdVariable.add(name:"variable2", resource:"resource", type:"COUNTER", heartbeat:600,
                                        startTime:978300900000L)
        assertFalse(variable2.errors.toString(), variable2.hasErrors())
        variable2.addArchive(archive.getMap())

        variable1.createDB()
        variable2.createDB()

        variable1.updateDB(1,978301200000)
        variable1.updateDB(3,978301500000)
        variable1.updateDB(5,978301800000)
        variable1.updateDB(3,978302100000)
        variable1.updateDB(1,978302400000)
        variable1.updateDB(2,978302700000)
        variable1.updateDB(4,978303000000)
        variable1.updateDB(6,978303300000)
        variable1.updateDB(4,978303600000)
        variable1.updateDB(2,978303900000)

        variable2.updateDB(300,978301200000)
        variable2.updateDB(600,978301500000)
        variable2.updateDB(900,978301800000)
        variable2.updateDB(1200,978302100000)
        variable2.updateDB(1500,978302400000)
        variable2.updateDB(1800,978302700000)
        variable2.updateDB(2100,978303000000)
        variable2.updateDB(2400,978303300000)
        variable2.updateDB(2700,978303600000)
        variable2.updateDB(3000,978303900000)

        def varConf = [
                variable1:[ type:"area", description:"Variable 1"],
                variable2:[ type:"stack", description:"Variable 2"],
                ]

        def graphConfig = [ title: "Graph With Multiple Source",
                startTime: 978300600000L,
                endTime: 978304200000L,
                destination: imageFileName,
                ]

        RrdVariable.graphMultiple(varConf, graphConfig);
        assertTrue(new File(fileDirectory + "/" + imageFileName).exists())
    }

    public void testGraphWithMultipleSource() {

        def archive = RrdArchive.add(function:"AVERAGE", xff:0.5, step:1, numberOfDatapoints:10)
        assertFalse(archive.errors.toString(), archive.hasErrors())

        def variable1 = RrdVariable.add(name:"variable1", resource:"resource", type:"GAUGE", heartbeat:600,
                                       startTime:978300900000L)
        assertFalse(variable1.errors.toString(), variable1.hasErrors())
        variable1.addArchive(archive.getMap())

        def variable2 = RrdVariable.add(name:"variable2", resource:"resource", type:"COUNTER", heartbeat:600,
                                        startTime:978300900000L)
        assertFalse(variable2.errors.toString(), variable2.hasErrors())
        variable2.addArchive(archive.getMap())

        variable1.createDB()
        variable2.createDB()

        variable1.updateDB(1,978301200000)
        variable1.updateDB(3,978301500000)
        variable1.updateDB(5,978301800000)
        variable1.updateDB(3,978302100000)
        variable1.updateDB(1,978302400000)
        variable1.updateDB(2,978302700000)
        variable1.updateDB(4,978303000000)
        variable1.updateDB(6,978303300000)
        variable1.updateDB(4,978303600000)
        variable1.updateDB(2,978303900000)

        variable2.updateDB(300,978301200000)
        variable2.updateDB(600,978301500000)
        variable2.updateDB(900,978301800000)
        variable2.updateDB(1200,978302100000)
        variable2.updateDB(1500,978302400000)
        variable2.updateDB(1800,978302700000)
        variable2.updateDB(2100,978303000000)
        variable2.updateDB(2400,978303300000)
        variable2.updateDB(2700,978303600000)
        variable2.updateDB(3000,978303900000)

        def config = [:]
        config["title"] = "Graph With Multiple Source"
        config["startTime"] = 978300600000L
        config["endTime"] = 978304200000L
        config["destination"] = imageFileName

        config["rrdVariables"] = []
        config["rrdVariables"].add([rrdVariable:"variable1", color:"FF0000", type:"line", description:"Variable 1"])
        config["rrdVariables"].add([rrdVariable:"variable2", color:"00FF00", type:"line", description:"Variable 2"])

        RrdVariable.graphMultiple(config)

        assertTrue(new File(fileDirectory + "/" + imageFileName).exists())
    }

    public void testGraphWithMultipleSourceByList() {

        def archive = RrdArchive.add(function:"AVERAGE", xff:0.5, step:1, numberOfDatapoints:10)
        assertFalse(archive.errors.toString(), archive.hasErrors())

        def variable1 = RrdVariable.add(name:"variable1", resource:"resource", type:"GAUGE", heartbeat:600,
                                       startTime:978300900000L)
        assertFalse(variable1.errors.toString(), variable1.hasErrors())
        variable1.addArchive(archive.getMap())

        def variable2 = RrdVariable.add(name:"variable2", resource:"resource", type:"COUNTER", heartbeat:600,
                                        startTime:978300900000L)
        assertFalse(variable2.errors.toString(), variable2.hasErrors())
        variable2.addArchive(archive.getMap())

        variable1.createDB()
        variable2.createDB()

        variable1.updateDB(1,978301200000)
        variable1.updateDB(3,978301500000)
        variable1.updateDB(5,978301800000)
        variable1.updateDB(3,978302100000)
        variable1.updateDB(1,978302400000)
        variable1.updateDB(2,978302700000)
        variable1.updateDB(4,978303000000)
        variable1.updateDB(6,978303300000)
        variable1.updateDB(4,978303600000)
        variable1.updateDB(2,978303900000)

        variable2.updateDB(300,978301200000)
        variable2.updateDB(600,978301500000)
        variable2.updateDB(900,978301800000)
        variable2.updateDB(1200,978302100000)
        variable2.updateDB(1500,978302400000)
        variable2.updateDB(1800,978302700000)
        variable2.updateDB(2100,978303000000)
        variable2.updateDB(2400,978303300000)
        variable2.updateDB(2700,978303600000)
        variable2.updateDB(3000,978303900000)

        def config = [:]
        config["title"] = "Graph With Multiple Source With Variable List"
        config["startTime"] = 978300600000L
        config["endTime"] = 978304200000L
        config["destination"] = imageFileName

        RrdVariable.graphMultiple(["variable1", "variable2"], config)

        assertTrue(new File(fileDirectory + "/" + imageFileName).exists())
    }

    public void testGraphWithMultipleSourceandRPN() {

        def archive1 = RrdArchive.add(function:"AVERAGE", xff:0.5, step:1, numberOfDatapoints:24)
        assertFalse(archive1.errors.toString(), archive1.hasErrors())

        def archive2 = RrdArchive.add(function:"AVERAGE", xff:0.5, step:6, numberOfDatapoints:10)
        assertFalse(archive2.errors.toString(), archive2.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource", type:"COUNTER", heartbeat:600,
                                       startTime:920804400000L, frequency:300)

        assertFalse(variable.errors.toString(), variable.hasErrors())
        variable.addArchive(archive1.getMap())
        variable.addArchive(archive2.getMap())

        variable.createDB()

        variable.updateDB(12345,920804700000L)
        variable.updateDB(12357,920805000000L)
        variable.updateDB(12363,920805300000L)
        variable.updateDB(12363,920805600000L)
        variable.updateDB(12363,920805900000L)
        variable.updateDB(12373,920806200000L)
        variable.updateDB(12383,920806500000L)
        variable.updateDB(12393,920806800000L)
        variable.updateDB(12399,920807100000L)
        variable.updateDB(12405,920807400000L)
        variable.updateDB(12411,920807700000L)
        variable.updateDB(12415,920808000000L)
        variable.updateDB(12420,920808300000L)
        variable.updateDB(12422,920808600000L)
        variable.updateDB(12423,920808900000L)

        def config = [:]
        config["title"] = "Graph With Multiple Source"
        config["startTime"] = 920804400000L
        config["endTime"] = 920808000000L
        config["destination"] = imageFileName

        config["rrdVariables"] = []
        config["rrdVariables"].add([rrdVariable:"variable", color:"000000", type:"line", thickness:4, rpn:"variable,3600,*",description:"km/h"])
        config["rrdVariables"].add([rrdVariable:"variable", color:"FF0000", type:"area", rpn:"variable,3600,*,100,GT,variable,3600,*,0,IF", description:"Fast"])
        config["rrdVariables"].add([rrdVariable:"variable", color:"00FF00", type:"area", rpn:"variable,3600,*,100,GT,0,variable,3600,*,IF", description:"Good"])

        RrdVariable.graphMultiple(config)

        assertTrue(new File(fileDirectory + "/" + imageFileName).exists())
    }

    public void testGraphWithSingleSourceandTemplate() {

        def archive1 = RrdArchive.add(function:"AVERAGE", xff:0.5, step:1, numberOfDatapoints:24)
        assertFalse(archive1.errors.toString(), archive1.hasErrors())

        def archive2 = RrdArchive.add(function:"AVERAGE", xff:0.5, step:6, numberOfDatapoints:10)
        assertFalse(archive2.errors.toString(), archive2.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource", type:"COUNTER", heartbeat:600,
                                       startTime:920804400000L, frequency:300)

        assertFalse(variable.errors.toString(), variable.hasErrors())
        variable.addArchive(archive1.getMap())
        variable.addArchive(archive2.getMap())

        variable.createDB()

        variable.updateDB(12345,920804700000L)
        variable.updateDB(12357,920805000000L)
        variable.updateDB(12363,920805300000L)
        variable.updateDB(12363,920805600000L)
        variable.updateDB(12363,920805900000L)
        variable.updateDB(12373,920806200000L)
        variable.updateDB(12383,920806500000L)
        variable.updateDB(12393,920806800000L)
        variable.updateDB(12399,920807100000L)
        variable.updateDB(12405,920807400000L)
        variable.updateDB(12411,920807700000L)
        variable.updateDB(12415,920808000000L)
        variable.updateDB(12420,920808300000L)
        variable.updateDB(12422,920808600000L)
        variable.updateDB(12423,920808900000L)

        def template = RrdGraphTemplate.add(name:"templateSample", title:"Graph With Template")

        def config = [:]
        config["template"] = "templateSample"
        config["startTime"] = 920804400000L
        config["endTime"] = 920808000000L
        config["destination"] = imageFileName

        config["rrdVariables"] = []
        config["rrdVariables"].add([rrdVariable:"variable", color:"000000", type:"line", thickness:4, rpn:"variable,3600,*",description:"km/h"])
        config["rrdVariables"].add([rrdVariable:"variable", color:"FF0000", type:"area", rpn:"variable,3600,*,100,GT,variable,3600,*,0,IF", description:"Fast"])
        config["rrdVariables"].add([rrdVariable:"variable", color:"00FF00", type:"area", rpn:"variable,3600,*,100,GT,0,variable,3600,*,IF", description:"Good"])

        RrdVariable.graphMultiple(config)

        assertTrue(new File(fileDirectory + "/" + imageFileName).exists())
    }

    public void testGraphWithMultipleSourceandTemplate() {

        def archive1 = RrdArchive.add(function:"AVERAGE", xff:0.5, step:1, numberOfDatapoints:24)
        assertFalse(archive1.errors.toString(), archive1.hasErrors())

        def archive2 = RrdArchive.add(function:"AVERAGE", xff:0.5, step:6, numberOfDatapoints:10)
        assertFalse(archive2.errors.toString(), archive2.hasErrors())

        def variable = RrdVariable.add(name:"variable", resource:"resource", type:"COUNTER", heartbeat:600,
                                       startTime:920804400000L, frequency:300)

        assertFalse(variable.errors.toString(), variable.hasErrors())
        variable.addArchive(archive1.getMap())
        variable.addArchive(archive2.getMap())

        variable.createDB()

        variable.updateDB(12345,920804700000L)
        variable.updateDB(12357,920805000000L)
        variable.updateDB(12363,920805300000L)
        variable.updateDB(12363,920805600000L)
        variable.updateDB(12363,920805900000L)
        variable.updateDB(12373,920806200000L)
        variable.updateDB(12383,920806500000L)
        variable.updateDB(12393,920806800000L)
        variable.updateDB(12399,920807100000L)
        variable.updateDB(12405,920807400000L)
        variable.updateDB(12411,920807700000L)
        variable.updateDB(12415,920808000000L)
        variable.updateDB(12420,920808300000L)
        variable.updateDB(12422,920808600000L)
        variable.updateDB(12423,920808900000L)

        def template = RrdGraphTemplate.add(name:"templateSample", title:"Graph With Template", color:"FF00FF",
                                            verticalLabel:"vertical label", type:"area", description:"inside template")

        def config = [:]
        config["vlabel"] = "overwritten label"
        config["template"] = "templateSample"
        config["startTime"] = 920804400000L
        config["endTime"] = 920808000000L
        config["destination"] = imageFileName

        variable.graph(config)

        assertTrue(new File(fileDirectory + "/" + imageFileName).exists())
    }

    public void testFileSource() {
        def variable1 = RrdVariable.add(name:"variable1")
        def fileName = variable1.fileSource()
        assertEquals("variable1.rrd", fileName)
    }

    private void createVariables() {
        def variable1 = RrdVariable.add(name:"variable1", resource:"resource", type:"GAUGE", heartbeat:600,
                                                startTime:3000000L, frequency:10)
        assertFalse(variable1.errors.toString(), variable1.hasErrors())

        def variable2 = RrdVariable.add(name:"variable2", resource:"resource", type:"COUNTER", heartbeat:600,
                                        startTime:3000000L, frequency:10)
        assertFalse(variable1.errors.toString(), variable1.hasErrors())

        def archiveConfig1 = [function:'AVERAGE', xff:0.5, step:1, numberOfDatapoints:3]
        def archiveConfig2 = [function:'AVERAGE', xff:0.5, step:2, numberOfDatapoints:3]

        variable1.addArchive(archiveConfig1)
        variable1.addArchive(archiveConfig2)
        variable2.addArchive(archiveConfig1)
        variable2.addArchive(archiveConfig2)
        variable1.createDB();
        variable2.createDB();
        RrdVariable.updateDB(variable1.fileSource(), [1,2,3,4,5,6], [3010000L,3020000L,3030000L,3040000L,3050000L,3060000L])
        RrdVariable.updateDB(variable2.fileSource(), [10,20,40,80,160,320], [3010000L,3020000L,3030000L,3040000L,3050000L,3060000L])
    }

    public void testFetchMultipleData() {
        createVariables()
        def result = RrdVariable.fetchAllData(["variable1","variable2"]);
        assertTrue('result should have values from variable1', result.containsKey('variable1'))
        assertTrue('result should have values from variable2', result.containsKey('variable2'))

        assertEquals('wrong values returned',3.5D, result['variable1']['3040'])
        assertEquals('wrong values returned',5D, result['variable1']['3050'])
    }

    public void testFetchMultipleDataWithMoreConfiguration() {
        createVariables()
        def result = RrdVariable.fetchData(['variable1','variable2'], "AVERAGE", 3030000L, 3060000L);
        assertTrue('result should have values from variable1', result.containsKey('variable1'))
        assertTrue('result should have values from variable2', result.containsKey('variable2'))
        assertEquals('wrong values returned',4D, result['variable2']['3040'])
        assertEquals('wrong values returned',8D, result['variable2']['3050'])
    }

    public void testFetchData() {
        def variable1 = RrdVariable.add(name:"variable1", resource:"resource", type:"GAUGE", heartbeat:600,
                                                        startTime:3000000L, frequency:10)
        assertFalse(variable1.errors.toString(), variable1.hasErrors())

        def archiveConfig1 = [function:'AVERAGE', xff:0.5, step:1, numberOfDatapoints:3]

        variable1.addArchive(archiveConfig1)
        variable1.createDB();
        RrdVariable.updateDB(variable1.fileSource(), [1,2,3,4,5,6], [3010000L,3020000L,3030000L,3040000L,3050000L,3060000L])

        def result = variable1.fetchAllData();
        assertTrue('result should have values from variable1', result.containsKey('variable1'))
        assertEquals('wrong values returned',4D, result['variable1']['3040'])
        assertEquals('wrong values returned',5D, result['variable1']['3050'])
        assertEquals('wrong values returned',6D, result['variable1']['3060'])
    }

    public void testCreateDefaultArchives() {

        def archive = RrdArchive.add(function:"AVERAGE", xff:0.5, step:1, numberOfDatapoints:10)
        assertFalse(archive.errors.toString(), archive.hasErrors())

        def variable1 = RrdVariable.add(name:"variable1", resource:"resource", type:"GAUGE", heartbeat:120, frequency:60,
                                       startTime:978300900000L)
        variable1.createDB();
        assertEquals("Defaults archives are not created", 5, variable1.archives.size());

        Map oneYear = variable1.archives[0].getMap();
        assertEquals(oneYear.get(RrdVariableOperations.FUNCTION),"AVERAGE");
        assertEquals(oneYear.get(RrdVariableOperations.STEP).toString(),"1440");
        assertEquals(oneYear.get(RrdVariableOperations.NUMBER_OF_DATAPOINTS).toString(),"365");
        assertEquals(oneYear.get(RrdVariableOperations.XFF).toString(),"0.5");

        Map oneMonth = variable1.archives[1].getMap();
        assertEquals(oneMonth.get(RrdVariableOperations.FUNCTION),"AVERAGE");
        assertEquals(oneMonth.get(RrdVariableOperations.STEP).toString(),"120");
        assertEquals(oneMonth.get(RrdVariableOperations.NUMBER_OF_DATAPOINTS).toString(),"360");
        assertEquals(oneMonth.get(RrdVariableOperations.XFF).toString(),"0.5");

        Map oneWeek = variable1.archives[2].getMap();
        assertEquals(oneWeek.get(RrdVariableOperations.FUNCTION),"AVERAGE");
        assertEquals(oneWeek.get(RrdVariableOperations.STEP).toString(),"30");
        assertEquals(oneWeek.get(RrdVariableOperations.NUMBER_OF_DATAPOINTS).toString(),"336");
        assertEquals(oneWeek.get(RrdVariableOperations.XFF).toString(),"0.5");

        Map oneDay = variable1.archives[3].getMap();
        assertEquals(oneDay.get(RrdVariableOperations.FUNCTION),"AVERAGE");
        assertEquals(oneDay.get(RrdVariableOperations.STEP).toString(),"4");
        assertEquals(oneDay.get(RrdVariableOperations.NUMBER_OF_DATAPOINTS).toString(),"360");
        assertEquals(oneDay.get(RrdVariableOperations.XFF).toString(),"0.5");

        Map oneHour = variable1.archives[4].getMap();
        assertEquals(oneHour.get(RrdVariableOperations.FUNCTION),"AVERAGE");
        assertEquals(oneHour.get(RrdVariableOperations.STEP).toString(),"1");
        assertEquals(oneHour.get(RrdVariableOperations.NUMBER_OF_DATAPOINTS).toString(),"60");
        assertEquals(oneHour.get(RrdVariableOperations.XFF).toString(),"0.5");
    }

    public void testCreateDefaultArchivesOneLessDefault() {
        def variable1 = RrdVariable.add(name:"variable1", resource:"resource", type:"GAUGE", heartbeat:720, frequency:360,
                                       startTime:978300900000L)
        variable1.createDefaultArchives();

        assertEquals("Defaults archives are not created", 4, variable1.archives.size());
    }

    public void testAddArchive() {
        def variable1 = RrdVariable.add(name:"variable1", resource:"resource", type:"GAUGE", heartbeat:120,
                                        frequency:7200, startTime:978300900000L)

        variable1.addArchive([name:"testArchive",step:5,xff:0.5, numberOfDatapoints:12]);
        variable1.archives.each{
            assertEquals("archive row is not proper",it.numberOfDatapoints.toString(),"12");
            assertEquals("archive xff is not proper",it.xff.toString(),"0.5");
            assertEquals("archive step is not proper",it.step.toString(),"5");
        }
    }
}