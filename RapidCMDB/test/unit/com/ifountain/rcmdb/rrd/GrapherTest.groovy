package com.ifountain.rcmdb.rrd


import com.ifountain.comp.test.util.file.TestFile
import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.comp.test.util.file.TestFile

/**
* Created by IntelliJ IDEA.
* User: ifountain
* Date: Jun 18, 2009
* Time: 9:25:20 AM
*/
class GrapherTest extends RapidCmdbWithCompassTestCase {

    final String GRAPH_LINE_FILE =getWorkspacePath() + "/RapidModules/RapidCMDB/test/unit/com/ifountain/rcmdb/rrd/expectedLineRrdGraph.gif";
    final String GRAPH_AREA_FILE = getWorkspacePath() + "/RapidModules/RapidCMDB/test/unit/com/ifountain/rcmdb/rrd/expectedAreaRrdGraph.gif"
    String rrdFileName = TestFile.TESTOUTPUT_DIR + "/testRrd.rrd";
    def classes=[:];
    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        classes.RrdVariable=loadClass("RrdVariable");
        classes.RrdArchive=loadClass("RrdArchive");
        classes.RrdGraphTemplate=loadClass("RrdGraphTemplate");

        initialize([classes.RrdVariable,classes.RrdArchive,classes.RrdGraphTemplate], []);
        CompassForTests.addOperationSupport(classes.RrdVariable, loadClass("RrdVariableOperations"));
        CompassForTests.addOperationSupport(classes.RrdGraphTemplate, loadClass("RrdGraphTemplateOperations"));
        new File(rrdFileName).delete();
    }
    def loadClass(className)
    {
        return this.class.classLoader.loadClass(className);
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }
    public void createDatabase()
    {

        Map config = [:]

        config[RrdUtils.DATABASE_NAME] = rrdFileName

        config[RrdUtils.DATASOURCE] = [
                                            [
                                                name:"a",
                                                type:"COUNTER",
                                                heartbeat:600,
                                            ],
                                            [
                                                name:"b",
                                                type:"GAUGE",
                                                heartbeat:600
                                            ]
                                      ]

        config[RrdUtils.ARCHIVE] = [
                                        [
                                            function:"AVERAGE",
                                            xff:0.5,
                                            steps:1,
                                            rows:100,
                                        ]
                                   ]
        config[RrdUtils.START_TIME] = 978300900;
        RrdUtils.createDatabase(config)

        RrdUtils.updateData(rrdFileName,"978301200:200:1");
        RrdUtils.updateData(rrdFileName,"978301500:400:4");
        RrdUtils.updateData(rrdFileName,"978301800:900:5");
        RrdUtils.updateData(rrdFileName,"978302100:1200:3");
        RrdUtils.updateData(rrdFileName,"978302400:1400:1");
        RrdUtils.updateData(rrdFileName,"978302700:1900:2");
        RrdUtils.updateData(rrdFileName,"978303000:2100:4");
        RrdUtils.updateData(rrdFileName,"978303300:2400:6");
        RrdUtils.updateData(rrdFileName,"978303600:2900:4");
        RrdUtils.updateData(rrdFileName,"978303900:3300:2");
    }
     public void testGraphWithArea() throws Exception{
         createDatabase();

        Map config = [:]

        config[Grapher.DATASOURCE] = [
                                            [
                                                name:"myspeed",
                                                dbname: rrdFileName,
                                                dsname: "b",
                                                function:"AVERAGE"
                                            ],
                                            [
                                                name:"kmh",
                                                rpn: "myspeed,10,*"
                                            ],
                                            [
                                                name:"fast",
                                                rpn:"kmh,100,GT,100,0,IF"
                                            ]
                                      ]

        config[Grapher.AREA] = [
                                        [
                                            name:"kmh",
                                            color: "ff00ff",
                                            description:"My Graph"
                                        ]
                                   ]

       config[Grapher.START_TIME] = 978301200L;
       config[Grapher.END_TIME] = 978303900L;

       byte[] actualBytes = Grapher.graph(config)

       File expectedGraphFile = new File(GRAPH_AREA_FILE)
       DataInputStream dis = new DataInputStream(new java.io.FileInputStream(expectedGraphFile));
       byte[] expectedBytes = new byte[expectedGraphFile.length()]
       dis.read(expectedBytes)

       assertEquals(expectedBytes.length, actualBytes.length)
       for(int i=0; i<expectedBytes.length; i++){
           assertEquals(expectedBytes[i], actualBytes[i])
       }

     }

     public void testGraphWithLine() throws Exception{
        createDatabase();

        Map config = [:]

        config[Grapher.DATASOURCE] = [
                                            [
                                                name:"myspeed",
                                                dbname: rrdFileName,
                                                dsname: "b",
                                                function:"AVERAGE"
                                            ],
                                            [
                                                name:"kmh",
                                                rpn: "myspeed,10,*"
                                            ],
                                            [
                                                name:"fast",
                                                rpn:"kmh,100,GT,100,0,IF"
                                            ]
                                      ]

        config[Grapher.LINE] = [
                                        [
                                            name:"kmh",
                                            color: "ff00ff",
                                            description:"My Graph",
                                            thickness: 3
                                        ]
                               ]

       config[Grapher.START_TIME] = 978301200L;
       config[Grapher.END_TIME] = 978303900L;

       byte[] actualBytes = Grapher.graph(config)


       File expectedGraphFile = new File(GRAPH_LINE_FILE)
       DataInputStream dis = new DataInputStream(new java.io.FileInputStream(expectedGraphFile));
       byte[] expectedBytes = new byte[expectedGraphFile.length()]
       dis.read(expectedBytes)

       assertEquals(expectedBytes.length, actualBytes.length)
       for(int i=0; i<expectedBytes.length; i++){
           assertEquals(expectedBytes[i], actualBytes[i])
       }

     }

     public void testGraphThrowsExceptionIfColorIsNotValid () throws Exception{

        Map config = [:]

        config[Grapher.DATASOURCE] = [
                                            [
                                                name:"myspeed",
                                                dbname: rrdFileName,
                                                dsname: "b",
                                                function:"AVERAGE"
                                            ],
                                            [
                                                name:"kmh",
                                                rpn: "myspeed,10,*"
                                            ],
                                            [
                                                name:"fast",
                                                rpn:"kmh,100,GT,100,0,IF"
                                            ]
                                      ]

        config[Grapher.AREA] = [
                                        [
                                            name:"kmh",
                                            color: "notvalidcolor",
                                            description:"My Graph"
                                        ]
                                   ]

       config[Grapher.START_TIME] = 978301200L;
       config[Grapher.END_TIME] = 978303900L;

       try{
            Grapher.graph(config)
            fail("should throw exception because color is not valid")
       }
       catch(Exception e){
           assertTrue(e.getMessage().indexOf("Invalid color")>=0);

       }

     }

     public void testGraphThrowsExceptionIfDBNotExistent() throws Exception{
        Map config = [:]

        String fileName = TestFile.TESTOUTPUT_DIR + "/thereisnosuchdatabase";

        config[Grapher.DATASOURCE] = [
                                            [
                                                name:"myspeed",
                                                dbname: rrdFileName,
                                                dsname: "b",
                                                function:"AVERAGE"
                                            ],
                                            [
                                                name:"kmh",
                                                rpn: "myspeed,10,*"
                                            ],
                                            [
                                                name:"fast",
                                                rpn:"kmh,100,GT,100,0,IF"
                                            ]
                                      ]

        config[Grapher.AREA] = [
                                        [
                                            name:"kmh",
                                            color: "125678",
                                            description:"My Graph"
                                        ]
                                   ]

       config[Grapher.START_TIME] = 978301200L;
       config[Grapher.END_TIME] = 978303900L;

       try{
            Grapher.graph(config)
            fail("should throw exception because database is not existent")
       }
       catch(FileNotFoundException e){
       }

     }

     public void testGraphThrowsExceptionIfConfigMissesProperty() throws Exception{

       Map config = [:]



       try{
            Grapher.graph(config)
            fail("should throw exception because no datasource specified")
       }
       catch(Exception e){
            assertEquals("No Datasource specified", e.getMessage())
       }

       config[Grapher.DATASOURCE] = [
                                            [
                                                name:"myspeed",
                                                dbname: rrdFileName,
                                                dsname: "b",
                                                function:"AVERAGE"
                                            ],
                                            [
                                                name:"kmh",
                                                rpn: "myspeed,10,*"
                                            ],
                                            [
                                                name:"fast",
                                                rpn:"kmh,100,GT,100,0,IF"
                                            ]
                                    ]

       try{
            Grapher.graph(config)
            fail("should throw exception because no start time specified")
       }
       catch(Exception e){
            assertEquals("Start time is not specified", e.getMessage())
       }

       config[Grapher.START_TIME] = 978301200L;

       try{
            Grapher.graph(config)
            fail("should throw exception because no end time specified")
       }
       catch(Exception e){
            assertEquals("End time is not specified", e.getMessage())
       }

     }

     public void testGraphThrowsExceptionIfStartTimeIsNotValid() throws Exception{

       Map config = [:]



        config[Grapher.DATASOURCE] = [
                                            [
                                                name:"myspeed",
                                                dbname: rrdFileName,
                                                dsname: "b",
                                                function:"AVERAGE"
                                            ],
                                            [
                                                name:"kmh",
                                                rpn: "myspeed,10,*"
                                            ],
                                            [
                                                name:"fast",
                                                rpn:"kmh,100,GT,100,0,IF"
                                            ]
                                      ]

        config[Grapher.AREA] = [
                                        [
                                            name:"kmh",
                                            color: "ff00ff",
                                            description:"My Graph"
                                        ]
                                   ]

       config[Grapher.START_TIME] = -1232L;
       config[Grapher.END_TIME] = 978303900L;

       try{
            Grapher.graph(config)
            fail("should throw exception start time is not valid")
       }
       catch(Exception e)
       {
             assertTrue(e.getMessage().indexOf("Invalid timestamps specified")>=0);
       }
     }

     public void testGraphThrowsExceptionIfEndTimeIsNotValid() throws Exception{

       Map config = [:]



        config[Grapher.DATASOURCE] = [
                                            [
                                                name:"myspeed",
                                                dbname: rrdFileName,
                                                dsname: "b",
                                                function:"AVERAGE"
                                            ],
                                            [
                                                name:"kmh",
                                                rpn: "myspeed,10,*"
                                            ],
                                            [
                                                name:"fast",
                                                rpn:"kmh,100,GT,100,0,IF"
                                            ]
                                      ]

        config[Grapher.AREA] = [
                                        [
                                            name:"kmh",
                                            color: "ff00ff",
                                            description:"My Graph"
                                        ]
                                   ]

       config[Grapher.START_TIME] = 978301200L;
       config[Grapher.END_TIME] = "12312zdfasd";

       try{
            Grapher.graph(config)
            fail("should throw exception end time is not valid")
       }
       catch(Exception e)
       {
             assertTrue(e.getMessage().indexOf("Invalid timestamps specified")>=0);
       }
     }

     public void testAddDataSourceThrowsExceptionIfNameIsNotSpecified() throws Exception{
        Map config = [:]



        config[Grapher.START_TIME] = 978301200L;
        config[Grapher.END_TIME] = 978303900L;



        config[Grapher.DATASOURCE] = [
                                            [
                                                name:"myspeed",
                                                dbname: rrdFileName,
                                                dsname: "b",
                                                function:"AVERAGE"
                                            ],
                                            [
                                                /*
                                                there should be a name for each datasource
                                                */
                                                rpn: "myspeed,10,*"
                                            ],
                                            [
                                                name:"fast",
                                                rpn:"kmh,100,GT,100,0,IF"
                                            ]
                                      ]


        try{
            Grapher.graph(config)
            fail("should throw exception because name of datasource is missing")
        }
        catch(Exception e)
        {
            assertEquals("Datasource distorted: Name of datasource is not specified", e.getMessage()) 
        }

    }

     public void testAddDataSourceThrowsExceptionIfRpnIsNotSpecified() throws Exception{
        Map config = [:]



        config[Grapher.START_TIME] = 978301200L;
        config[Grapher.END_TIME] = 978303900L;



        config[Grapher.DATASOURCE] = [
                                            [
                                                name:"myspeed",
                                                dbname: rrdFileName,
                                                dsname: "b",
                                                function:"AVERAGE"
                                            ],
                                            [
                                                name:"kmh"
                                                /*
                                                there should be a rpn for datasource
                                                */
                                            ],
                                            [
                                                name:"fast",
                                                rpn:"kmh,100,GT,100,0,IF"
                                            ]
                                      ]


        try{
            Grapher.graph(config)
            fail("should throw exception because rpn of datasource is missing")
        }
        catch(Exception e)
        {
            assertEquals("Datasource distorted", e.getMessage())
        }

    }

     public   void testAddDataSourceThrowsExceptionIfNoDBDatasourceSelected() throws Exception{
        Map config = [:]



        config[Grapher.START_TIME] = 978301200L;
        config[Grapher.END_TIME] = 978303900L;

        config[Grapher.DATASOURCE] = [
                                            /*
                                                There should be at least one
                                                db datasource to work on it

                                                like:
                                                [
                                                    name:"myspeed",
                                                    dbname: rrdFileName,
                                                    dsname: "b",
                                                    function:"AVERAGE"
                                                ]
                                            */
                                            [
                                                name:"kmh",
                                                rpn: "myspeed,10,*"
                                            ],
                                            [
                                                name:"fast",
                                                rpn:"kmh,100,GT,100,0,IF"
                                            ]
                                      ]

        try{
            Grapher.graph(config)
            fail("should throw exception because db datasource is missing")
        }
        catch(Exception e)
        {
            assertEquals("There is no database selected", e.getMessage())
        }

    }

    public void testMultipleDatasourceGraphDatasourcesSuccessfully()  throws Exception{
        Map config = [:]

        config[RrdUtils.DATABASE_NAME] = rrdFileName

        config[RrdUtils.DATASOURCE] = [
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

        config[RrdUtils.ARCHIVE] = [
                                        [
                                            function:"AVERAGE",
                                            xff:0.5,
                                            steps:1,
                                            rows:24,
                                        ]
                                   ]
        config[RrdUtils.START_TIME] = 978300900;
        RrdUtils.createDatabase(config)

        RrdUtils.updateData(rrdFileName,"978301200:200:1");
        RrdUtils.updateData(rrdFileName,"978301500:400:4");
        RrdUtils.updateData(rrdFileName,"978301800:900:5");
        RrdUtils.updateData(rrdFileName,"978302100:1200:3");
        RrdUtils.updateData(rrdFileName,"978302400:1400:1");
        RrdUtils.updateData(rrdFileName,"978302700:1900:2");
        RrdUtils.updateData(rrdFileName,"978303000:2100:4");
        RrdUtils.updateData(rrdFileName,"978303300:2400:6");
        RrdUtils.updateData(rrdFileName,"978303600:2900:4");
        RrdUtils.updateData(rrdFileName,"978303900:3300:2");

        def archive1 = classes.RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:24)

        classes.RrdVariable.add(name:"testDs1", resource:"resource",
                           type:"COUNTER", heartbeat:600, file: rrdFileName,
                           startTime:978300900, archives: [archive1])

        classes.RrdVariable.add(name:"testDs2", resource:"resource",
                           type:"GAUGE", heartbeat:600, file: rrdFileName,
                           startTime:978300900, archives: [archive1])

        def rrdList = [
                            [rrdVariable:"testDs1", color:"123456", description:"cpu"],
                            [rrdVariable:"testDs2", color:"aabb22", description:"memory", function:"AVERAGE"]
                        ];
        Map map = [:];
        map[Grapher.RRD_VARIABLES] = rrdList;
        map[Grapher.START_TIME] = 978301200
        map[Grapher.END_TIME] = 978303900;

        byte[] bytes = Grapher.graphMultipleDatasources(map);
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(rrdFileName+".png") );
        dos.write(bytes);
    }
    public void testMultipleDatasourcesGraphWithDifferentDrawingTypeSuccessfully()  throws Exception{
        Map config = [:]

        config[RrdUtils.DATABASE_NAME] = rrdFileName

        config[RrdUtils.DATASOURCE] = [
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

        config[RrdUtils.ARCHIVE] = [
                                        [
                                            function:"AVERAGE",
                                            xff:0.5,
                                            steps:1,
                                            rows:24,
                                        ]
                                   ]
        config[RrdUtils.START_TIME] = 978300900;
        RrdUtils.createDatabase(config)

        RrdUtils.updateData(rrdFileName,"978301200:200:1");
        RrdUtils.updateData(rrdFileName,"978301500:400:4");
        RrdUtils.updateData(rrdFileName,"978301800:900:5");
        RrdUtils.updateData(rrdFileName,"978302100:1200:3");
        RrdUtils.updateData(rrdFileName,"978302400:1400:1");
        RrdUtils.updateData(rrdFileName,"978302700:1900:2");
        RrdUtils.updateData(rrdFileName,"978303000:2100:4");
        RrdUtils.updateData(rrdFileName,"978303300:2400:6");
        RrdUtils.updateData(rrdFileName,"978303600:2900:4");
        RrdUtils.updateData(rrdFileName,"978303900:3300:2");

        def archive1 = classes.RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:24)

        classes.RrdVariable.add(name:"testDs1", resource:"resource",
                           type:"COUNTER", heartbeat:600, file: rrdFileName,
                           startTime:978300900, archives: [archive1])

        classes.RrdVariable.add(name:"testDs2", resource:"resource",
                           type:"GAUGE", heartbeat:600, file: rrdFileName,
                           startTime:978300900, archives: [archive1])

        def rrdList = [
                            [rrdVariable:"testDs1", color:"123456", description:"cpu", rpn:"testDs1,2,*"],
                            [rrdVariable:"testDs2", color:"aabb22", description:"memory", function:"AVERAGE", type:"area"]
                        ];
        Map map = [:];
        map[Grapher.RRD_VARIABLES] = rrdList;
        map[Grapher.START_TIME] = 978301200
        map[Grapher.END_TIME] = 978303900;

        map[Grapher.TITLE] = "Rrd Graph Utilities";
        map[Grapher.VERTICAL_LABEL] = "rate";

        byte[] bytes = Grapher.graphMultipleDatasources(map);
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(rrdFileName+".png") );
        dos.write(bytes);
    }
    public void testOneDatasourceGraphSuccessfully()  throws Exception{
        Map config = [:]

        config[RrdUtils.DATABASE_NAME] = rrdFileName

        config[RrdUtils.DATASOURCE] = [
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

        config[RrdUtils.ARCHIVE] = [
                                        [
                                            function:"AVERAGE",
                                            xff:0.5,
                                            steps:1,
                                            rows:24,
                                        ]
                                   ]
        config[RrdUtils.START_TIME] = 978300900;
        RrdUtils.createDatabase(config)

        RrdUtils.updateData(rrdFileName,"978301200:200:1");
        RrdUtils.updateData(rrdFileName,"978301500:400:4");
        RrdUtils.updateData(rrdFileName,"978301800:900:5");
        RrdUtils.updateData(rrdFileName,"978302100:1200:3");
        RrdUtils.updateData(rrdFileName,"978302400:1400:1");
        RrdUtils.updateData(rrdFileName,"978302700:1900:2");
        RrdUtils.updateData(rrdFileName,"978303000:2100:4");
        RrdUtils.updateData(rrdFileName,"978303300:2400:6");
        RrdUtils.updateData(rrdFileName,"978303600:2900:4");
        RrdUtils.updateData(rrdFileName,"978303900:3300:2");

        def archive1 = classes.RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:24)

        classes.RrdVariable.add(name:"testDs2", resource:"resource",
                           type:"GAUGE", heartbeat:600, file: rrdFileName,
                           startTime:978300900, archives: [archive1])

        def map = [:];
        map[Grapher.START_TIME] = 978301200;
        map[Grapher.RRD_VARIABLE] = "testDs2";
        //Optional properties:
        map[Grapher.END_TIME] = 978303900;
        map[Grapher.DESCRIPTION] = "cpu util";
        map[Grapher.TYPE] = "area";
        map[Grapher.COLOR] = "5566ff";
        map[Grapher.MAX] = 10;


        byte[] bytes = Grapher.graphOneVariable(map);
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(rrdFileName+".png") );
        dos.write(bytes);
    }
    public void testOneDatasourceGraphSuccessfullyWithTemplate()  throws Exception{
        Map config = [:]

        config[RrdUtils.DATABASE_NAME] = rrdFileName

        config[RrdUtils.DATASOURCE] = [
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

        config[RrdUtils.ARCHIVE] = [
                                        [
                                            function:"AVERAGE",
                                            xff:0.5,
                                            steps:1,
                                            rows:24,
                                        ]
                                   ]
        config[RrdUtils.START_TIME] = 978300900;
        RrdUtils.createDatabase(config)

        RrdUtils.updateData(rrdFileName,"978301200:200:1");
        RrdUtils.updateData(rrdFileName,"978301500:400:4");
        RrdUtils.updateData(rrdFileName,"978301800:900:5");
        RrdUtils.updateData(rrdFileName,"978302100:1200:3");
        RrdUtils.updateData(rrdFileName,"978302400:1400:1");
        RrdUtils.updateData(rrdFileName,"978302700:1900:2");
        RrdUtils.updateData(rrdFileName,"978303000:2100:4");
        RrdUtils.updateData(rrdFileName,"978303300:2400:6");
        RrdUtils.updateData(rrdFileName,"978303600:2900:4");
        RrdUtils.updateData(rrdFileName,"978303900:3300:2");

        def archive1 = classes.RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:24)

        classes.RrdVariable.add(name:"testDs2", resource:"resource",
                           type:"GAUGE", heartbeat:600, file: rrdFileName,
                           startTime:978300900, archives: [archive1])
        classes.RrdGraphTemplate.add(["name":"tName", "description":"desc",
                    "title":"title", "type":"line",
                    "verticalLabel":"kmh", "width":100, "description":"cpu util",
                    "type":"area","color":"234231"]);


        def map = [:];
        map[Grapher.START_TIME] = 978301200;
        map[Grapher.RRD_VARIABLE] = "testDs2";
        //Optional properties:
        map[Grapher.END_TIME] = 978303900;
        map[Grapher.GRAPH_TEMPLATE] = "tName";


        byte[] bytes = Grapher.graphOneVariable(map);
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(rrdFileName+".png") );
        dos.write(bytes);
    }
    public void testOneDatasourceGraphWithRpnSuccessfully()  throws Exception{
        Map config = [:]

        config[RrdUtils.DATABASE_NAME] = rrdFileName

        config[RrdUtils.DATASOURCE] = [
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

        config[RrdUtils.ARCHIVE] = [
                                        [
                                            function:"AVERAGE",
                                            xff:0.5,
                                            steps:1,
                                            rows:24,
                                        ]
                                   ]
        config[RrdUtils.START_TIME] = 978300900;
        RrdUtils.createDatabase(config)

        RrdUtils.updateData(rrdFileName,"978301200:200:1");
        RrdUtils.updateData(rrdFileName,"978301500:400:4");
        RrdUtils.updateData(rrdFileName,"978301800:900:5");
        RrdUtils.updateData(rrdFileName,"978302100:1200:3");
        RrdUtils.updateData(rrdFileName,"978302400:1400:1");
        RrdUtils.updateData(rrdFileName,"978302700:1900:2");
        RrdUtils.updateData(rrdFileName,"978303000:2100:4");
        RrdUtils.updateData(rrdFileName,"978303300:2400:6");
        RrdUtils.updateData(rrdFileName,"978303600:2900:4");
        RrdUtils.updateData(rrdFileName,"978303900:3300:2");

        def archive1 = classes.RrdArchive.add(name:"archive1", function:"AVERAGE", xff:0.5, step:1, row:24)

        classes.RrdVariable.add(name:"testDs2", resource:"resource",
                           type:"GAUGE", heartbeat:600, file: rrdFileName,
                           startTime:978300900, archives: [archive1])

        def map = [:];
        map[Grapher.START_TIME] = 978301200;
        map[Grapher.RRD_VARIABLE] = "testDs2";
        //Optional properties:
        map[Grapher.END_TIME] = 978303900;
        map[Grapher.DESCRIPTION] = "my graph description";
        map[Grapher.TYPE] = "area";
        map[Grapher.RPN] = "testDs2,2,*";

        map[Grapher.TITLE] = "Rrd Graph Utilities";
        map[Grapher.VERTICAL_LABEL] = "rate";

        byte[] bytes = Grapher.graphOneVariable(map);
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(rrdFileName+".png") );
        dos.write(bytes);
    }
//    public void testGraphTemplate() throws Exception{
//          Map map = [name:"tName", description:"desc",
//                    min:0, title:"title", type:"line",
//                    verticalLabel:"kmh", width:100]
//          loadClass("RrdGraphTemplate").add(map);
//          Grapher.getGeneralSettingsMap ([startTime:3243426,endTime:4243426,
//                                        templateName:"tName"]);
//    }

}