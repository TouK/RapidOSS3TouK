package com.ifountain.rcmdb.rrd


import com.ifountain.comp.test.util.file.TestFile
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase

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
    String testImageName = TestFile.TESTOUTPUT_DIR + "/testImage.png"

    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        new File(rrdFileName).delete();
        new File(testImageName).delete();
    }

    public void tearDown() {
        new File(rrdFileName).delete();
        new File(testImageName).delete();
        super.tearDown();
    }

    public void createDatabase() {
        Map config = [:]

        config[DbUtils.DATABASE_NAME] = rrdFileName
        config[DbUtils.DATASOURCE] = [
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

        config[DbUtils.ARCHIVE] = [
                                        [
                                            function:"AVERAGE",
                                            xff:0.5,
                                            steps:1,
                                            rows:100,
                                        ]
                                   ]
        config[DbUtils.START_TIME] = 978300900000;
        DbUtils.createDatabase(config)

        DbUtils.updateData(rrdFileName,"978301200000:200:1");
        DbUtils.updateData(rrdFileName,"978301500000:400:4");
        DbUtils.updateData(rrdFileName,"978301800000:900:5");
        DbUtils.updateData(rrdFileName,"978302100000:1200:3");
        DbUtils.updateData(rrdFileName,"978302400000:1400:1");
        DbUtils.updateData(rrdFileName,"978302700000:1900:2");
        DbUtils.updateData(rrdFileName,"978303000000:2100:4");
        DbUtils.updateData(rrdFileName,"978303300000:2400:6");
        DbUtils.updateData(rrdFileName,"978303600000:2900:4");
        DbUtils.updateData(rrdFileName,"978303900000:3300:2");
    }

    public void testGraphWithArea() throws Exception{
        createDatabase();

        Map config = [:]

        config[Grapher.DATASOURCE] = [
                                            [
                                                name:"myspeed",
                                                databaseName: rrdFileName,
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

       config[Grapher.START_TIME] = 978301200000L;
       config[Grapher.END_TIME] = 978303900000L;

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
                                                databaseName: rrdFileName,
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

       config[Grapher.START_TIME] = 978301200000L;
       config[Grapher.END_TIME] = 978303900000L;

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
                                                databaseName: rrdFileName,
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
                                                databaseName: rrdFileName,
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
                                                databaseName: rrdFileName,
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
                                                databaseName: rrdFileName,
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
                                                databaseName: rrdFileName,
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
                                                databaseName: rrdFileName,
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
        config[Grapher.START_TIME] = 978301200000L;
        config[Grapher.END_TIME] = 978303900000L;
        config[Grapher.DATASOURCE] = [
                                            [
                                                name:"myspeed",
                                                databaseName: rrdFileName,
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

    public void testAddDataSourceThrowsExceptionIfNoDBDatasourceSelected() throws Exception{
        Map config = [:]
        config[Grapher.START_TIME] = 978301200000L;
        config[Grapher.END_TIME] = 978303900000L;
        config[Grapher.DATASOURCE] = [
                                            /*
                                                There should be at least one
                                                db datasource to work on it

                                                like:
                                                [
                                                    name:"myspeed",
                                                    databaseName: rrdFileName,
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

    public void testToFileSuccessfull() throws Exception{
        createDatabase();
        Map config = [:]
        config[Grapher.DATASOURCE] = [
                                            [
                                                name:"myspeed",
                                                databaseName: rrdFileName,
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
       config[Grapher.START_TIME] = 978301200000L;
       config[Grapher.END_TIME] = 978303900000L;

       byte[] bytesToFile = Grapher.graph(config)

       Grapher.toFile(bytesToFile, testImageName)

       File testFile = new File(testImageName)
       DataInputStream dis = new DataInputStream(new FileInputStream(testFile));
       byte[] bytesFromFile = new byte[testFile.length()]
       dis.read(bytesFromFile)

       assertEquals(bytesToFile.length, bytesFromFile.length)
       for(int i=0; i<bytesToFile.length; i++){
           assertEquals(bytesToFile[i], bytesFromFile[i])
       }

     }


}