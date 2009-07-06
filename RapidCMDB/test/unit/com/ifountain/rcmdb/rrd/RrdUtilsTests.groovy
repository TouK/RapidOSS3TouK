package com.ifountain.rcmdb.rrd
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase

/**
* Created by IntelliJ IDEA.
* User: ifountain
* Date: Jun 17, 2009
* Time: 9:38:15 AM
* To change this template use File | Settings | File Templates.
*/
class RrdUtilsTests extends RapidCmdbWithCompassTestCase {

    String fileDirectory = "rrdFiles"
    String rrdFileName = "testRrd.rrd";
    String testImageFile = "testImage.png"
    def classes=[:];

    public void setUp() {
        super.setUp();
        clearMetaClasses();
        classes.RrdVariable=loadClass("RrdVariable");
        classes.RrdArchive=loadClass("RrdArchive");
        classes.RrdGraphTemplate=loadClass("RrdGraphTemplate");

        initialize([classes.RrdVariable,classes.RrdArchive,classes.RrdGraphTemplate], []);
        CompassForTests.addOperationSupport(classes.RrdVariable, loadClass("RrdVariableOperations"));

        new File(fileDirectory).mkdirs()
        new File(fileDirectory + "/" + rrdFileName).delete();
        new File(fileDirectory + "/" + testImageFile).delete();
    }

    public void tearDown() {
        new File(fileDirectory + "/" + rrdFileName).delete();
        new File(fileDirectory + "/" + testImageFile).delete();
        deleteDirectory(new File(fileDirectory))
        clearMetaClasses();
        super.tearDown();
    }

    private void deleteDirectory(File directory){
        if( directory.exists() ) {
            File[] files = directory.listFiles()
            files.each{
                if(it.isDirectory())
                    deleteDirectory(it)
                else
                    it.delete()
            }
         }
         directory.delete()
    }

    private void clearMetaClasses(){
         ExpandoMetaClass.disableGlobally();
         GroovySystem.metaClassRegistry.removeMetaClass(Grapher);
         ExpandoMetaClass.enableGlobally();
    }
    def loadClass(className) {
        return this.class.classLoader.loadClass(className);
    }

    public void testMultipleDatasourceGraphDatasourcesSuccessfully() throws Exception{
        Map config = [:]
        config[DbUtils.DATABASE_NAME] = rrdFileName
        config[DbUtils.DATASOURCE] = [
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

        config[DbUtils.ARCHIVE] = [
                                        [
                                            function:"AVERAGE",
                                            xff:0.5,
                                            steps:1,
                                            rows:24,
                                        ]
                                   ]
        config[DbUtils.START_TIME] = 978300900000;
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

        def archive1 = classes.RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:24)

        classes.RrdVariable.add(name:"testDs1", resource:"resource",
                           type:"COUNTER", heartbeat:600, file: rrdFileName,
                           startTime:978300900000, archives: [archive1])

        classes.RrdVariable.add(name:"testDs2", resource:"resource",
                           type:"GAUGE", heartbeat:600, file: rrdFileName,
                           startTime:978300900000, archives: [archive1])

        def rrdList = [
                            [rrdVariable:"testDs1", color:"123456", description:"cpu"],
                            [rrdVariable:"testDs2", color:"aabb22", description:"memory", function:"AVERAGE"]
                        ];
        Map map = [:];
        map[RrdUtils.RRD_VARIABLES] = rrdList;
        map[Grapher.START_TIME] = 978301200000
        map[Grapher.END_TIME] = 978303900000;
        map["destination"]="testMultipleDatasourceGraphDatasourcesSuccessfully.png";

        byte[] bytes = RrdUtils.graph(map);

        assertTrue("Grapher returns no graph info",bytes!=null);
        File f=new File(fileDirectory + "/" + map['destination']);
        assertTrue(f.exists());
    }

    public void testMultipleDatasourcesGraphWithDifferentDrawingTypeSuccessfully() throws Exception{
        Map config = [:]
        config[DbUtils.DATABASE_NAME] = rrdFileName
        config[DbUtils.DATASOURCE] = [
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

        config[DbUtils.ARCHIVE] = [
                                        [
                                            function:"AVERAGE",
                                            xff:0.5,
                                            steps:1,
                                            rows:24,
                                        ]
                                   ]
        config[DbUtils.START_TIME] = 978300900000;
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

        def archive1 = classes.RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:24)

        classes.RrdVariable.add(name:"testDs1", resource:"resource",
                           type:"COUNTER", heartbeat:600, file: rrdFileName,
                           startTime:978300900000, archives: [archive1])

        classes.RrdVariable.add(name:"testDs2", resource:"resource",
                           type:"GAUGE", heartbeat:600, file: rrdFileName,
                           startTime:978300900000, archives: [archive1])

        def rrdList = [
                            [rrdVariable:"testDs1", color:"123456", description:"cpu", rpn:"testDs1,2,*"],
                            [rrdVariable:"testDs2", color:"aabb22", description:"memory", function:"AVERAGE", type:"area"]
                        ];
        Map map = [:];
        map[RrdUtils.RRD_VARIABLES] = rrdList;
        map[Grapher.START_TIME] = 978301200000
        map[Grapher.END_TIME] = 978303900000;

        map[Grapher.TITLE] = "Rrd Graph Utilities";
        map[Grapher.VERTICAL_LABEL] = "rate";
        map['destination']="testMultipleDatasourcesGraphWithDifferentDrawingTypeSuccessfully.png";

        byte[] bytes = RrdUtils.graph(map);
        assertTrue("Grapher returns no graph info",bytes!=null);
        File f=new File(fileDirectory + "/" + map['destination']);
        assertTrue(f.exists());
    }

    public void testOneDatasourceGraphSuccessfully() throws Exception{
        Map config = [:]
        config[DbUtils.DATABASE_NAME] = rrdFileName;
        config[DbUtils.DATASOURCE] = [
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

        config[DbUtils.ARCHIVE] = [
                                        [
                                            function:"AVERAGE",
                                            xff:0.5,
                                            steps:1,
                                            rows:24,
                                        ]
                                   ]
        config[DbUtils.START_TIME] = 978300900000;
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

        def archive1 = classes.RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:24)

        classes.RrdVariable.add(name:"testDs2", resource:"resource",
                           type:"GAUGE", heartbeat:600, file: rrdFileName,
                           startTime:978300900000, archives: [archive1])

        def map = [:];
        map[Grapher.START_TIME] = 978301200000;
        map[RrdUtils.RRD_VARIABLE] = "testDs2";
        //Optional properties:
        map[Grapher.END_TIME] = 978303900000;
        map[Grapher.DESCRIPTION] = "cpu util";
        map[Grapher.TYPE] = "area";
        map[Grapher.COLOR] = "5566ff";
        map[Grapher.MAX] = 10;
        map['destination']="testOneDatasourceGraphSuccessfully.png";


        byte[] bytes = RrdUtils.graph(map);

        assertTrue("Grapher returns no graph info",bytes!=null);
        
        File f=new File(fileDirectory + "/" + map['destination']);
        assertTrue(f.exists());
    }
    public void testOneDatasourceWithOneParameterGraphSuccessfully() throws Exception{
        Map config = [:]
        config[DbUtils.DATABASE_NAME] = rrdFileName;
        config[DbUtils.DATASOURCE] = [
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

        config[DbUtils.ARCHIVE] = [
                                        [
                                            function:"AVERAGE",
                                            xff:0.5,
                                            steps:1,
                                            rows:24,
                                        ]
                                   ]
        config[DbUtils.START_TIME] = 978300900000;
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

        def archive1 = classes.RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:24)

        classes.RrdVariable.add(name:"testDs2", resource:"resource",
                           type:"GAUGE", heartbeat:600, file: rrdFileName,
                           startTime:978300900000, archives: [archive1])

        def map = [:];
        map[RrdUtils.RRD_VARIABLE] = "testDs2";
        map['destination']="testOneDatasourceWithOneParameterGraphSuccessfully.png";
        //Optional properties:


        byte[] bytes = RrdUtils.graph(map);

        assertTrue("Grapher returns no graph info",bytes!=null);
        File f=new File(fileDirectory + "/" + map['destination']);
        assertTrue(f.exists());
    }

    public void testOneDatasourceGraphSuccessfullyWithTemplate() throws Exception{
        Map config = [:]
        config[DbUtils.DATABASE_NAME] = rrdFileName
        config[DbUtils.DATASOURCE] = [
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

        config[DbUtils.ARCHIVE] = [
                                        [
                                            function:"AVERAGE",
                                            xff:0.5,
                                            steps:1,
                                            rows:24,
                                        ]
                                   ]
        config[DbUtils.START_TIME] = 978300900000;
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

        def archive1 = classes.RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:24)

        classes.RrdVariable.add(name:"testDs2", resource:"resource",
                           type:"GAUGE", heartbeat:600, file: rrdFileName,
                           startTime:978300900000, archives: [archive1])
        classes.RrdGraphTemplate.add(["name":"tName", "description":"desc",
                    "title":"title", "verticalLabel":"kmh", "width":100,
                    "type":"area","color":"234231"]);


        def map = [:];
        map[Grapher.START_TIME] = 978301200000;
        map[RrdUtils.RRD_VARIABLE] = "testDs2";
        //Optional properties:
        map[Grapher.END_TIME] = 978303900000;
        map[RrdUtils.GRAPH_TEMPLATE] = "tName";
        map['destination']="testOneDatasourceGraphSuccessfullyWithTemplate.png";


        byte[] bytes = RrdUtils.graph(map);

        assertTrue("Grapher returns no graph info",bytes!=null);
        File f=new File(fileDirectory + "/" + map['destination']);
        assertTrue(f.exists());
    }
    public void testGraphThrowsExceptionIfNoWebResponseIsDefined(){
        def graphCallConfig=null;
        Grapher.metaClass.'static'.graph={ Map config ->
             graphCallConfig=config;
        }

        def graphConfig=[destination:'web'];

        try{
            RrdUtils.graph(graphConfig);
            fail("should throw 'Web response is not avaliable' Exception");
        }
        catch(e)
        {
            assertTrue("wrong exception ${e}",e.getMessage().indexOf("Web response is not avaliable")>=0)
        }
        finally
        {
            assertSame(graphConfig,graphCallConfig);
        }
    }

    public void testOneDatasourceGraphWithRpnSuccessfully() throws Exception{
        Map config = [:]
        config[DbUtils.DATABASE_NAME] = rrdFileName
        config[DbUtils.DATASOURCE] = [
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

        config[DbUtils.ARCHIVE] = [
                                        [
                                            function:"AVERAGE",
                                            xff:0.5,
                                            steps:1,
                                            rows:24,
                                        ]
                                   ]
        config[DbUtils.START_TIME] = 978300900000;
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

        def archive1 = classes.RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:24)

        classes.RrdVariable.add(name:"testDs2", resource:"resource",
                           type:"GAUGE", heartbeat:600, file: rrdFileName,
                           startTime:978300900000, archives: [archive1])

        def map = [:];
        map[Grapher.START_TIME] = 978301200000;
        map[RrdUtils.RRD_VARIABLE] = "testDs2";
        //Optional properties:
        map[Grapher.END_TIME] = 978303900000;
        map[Grapher.DESCRIPTION] = "my graph description";
        map[Grapher.TYPE] = "area";
        map[Grapher.RPN] = "testDs2,2,*";

        map[Grapher.TITLE] = "Rrd Graph Utilities";
        map[Grapher.VERTICAL_LABEL] = "rate";
        map['destination']="testOneDatasourceGraphWithRpnSuccessfully.png";

        byte[] bytes = RrdUtils.graph(map);
        assertTrue("Grapher returns no graph info",bytes!=null);
        File f=new File(fileDirectory + "/" + map['destination']);
        assertTrue(f.exists());
    }

}