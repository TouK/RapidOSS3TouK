package com.ifountain.rcmdb.rrd

import com.ifountain.core.test.util.RapidCoreTestCase
import com.ifountain.comp.test.util.file.TestFile

/**
* Created by IntelliJ IDEA.
* User: ifountain
* Date: Jun 18, 2009
* Time: 9:25:20 AM
*/
class GrapherTest extends RapidCoreTestCase {

    public static final String GRAPH_LINE_FILE = TestFile.TESTOUTPUT_DIR + "/expectedLineRrdGraph.gif";
    public static final String GRAPH_AREA_FILE = TestFile.TESTOUTPUT_DIR + "/expectedAreaRrdGraph.gif"

    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }

     public static void testGraphWithArea() throws Exception{
        Map config = [:]

        String fileName = TestFile.TESTOUTPUT_DIR + "/testRrd.rrd";

        config[Grapher.DATASOURCE] = [
                                            [
                                                name:"myspeed",
                                                dbname: fileName,
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

     public static void testGraphWithLine() throws Exception{
        Map config = [:]

        String fileName = TestFile.TESTOUTPUT_DIR + "/testRrd.rrd";

        config[Grapher.DATASOURCE] = [
                                            [
                                                name:"myspeed",
                                                dbname: fileName,
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

     public static void testGraphThrowsExceptionIfColorIsNotValid () throws Exception{

        Map config = [:]

        String fileName = TestFile.TESTOUTPUT_DIR + "/testRrd.rrd";

        config[Grapher.DATASOURCE] = [
                                            [
                                                name:"myspeed",
                                                dbname: fileName,
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

     public static void testGraphThrowsExceptionIfDBNotExistent() throws Exception{
        Map config = [:]

        String fileName = TestFile.TESTOUTPUT_DIR + "/thereisnosuchdatabase";

        config[Grapher.DATASOURCE] = [
                                            [
                                                name:"myspeed",
                                                dbname: fileName,
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

     public static void testGraphThrowsExceptionIfConfigMissesProperty() throws Exception{

       Map config = [:]

        String fileName = TestFile.TESTOUTPUT_DIR + "/testRrd.rrd";

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
                                                dbname: fileName,
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

     public static void testGraphThrowsExceptionIfStartTimeIsNotValid() throws Exception{

       Map config = [:]

        String fileName = TestFile.TESTOUTPUT_DIR + "/testRrd.rrd";

        config[Grapher.DATASOURCE] = [
                                            [
                                                name:"myspeed",
                                                dbname: fileName,
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

     public static void testGraphThrowsExceptionIfEndTimeIsNotValid() throws Exception{

       Map config = [:]

        String fileName = TestFile.TESTOUTPUT_DIR + "/testRrd.rrd";

        config[Grapher.DATASOURCE] = [
                                            [
                                                name:"myspeed",
                                                dbname: fileName,
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

     public static void testAddDataSourceThrowsExceptionIfNameIsNotSpecified() throws Exception{
        Map config = [:]

        String fileName = TestFile.TESTOUTPUT_DIR + "/testRrd.rrd";

        config[Grapher.START_TIME] = 978301200L;
        config[Grapher.END_TIME] = 978303900L;



        config[Grapher.DATASOURCE] = [
                                            [
                                                name:"myspeed",
                                                dbname: fileName,
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

     public static void testAddDataSourceThrowsExceptionIfRpnIsNotSpecified() throws Exception{
        Map config = [:]

        String fileName = TestFile.TESTOUTPUT_DIR + "/testRrd.rrd";

        config[Grapher.START_TIME] = 978301200L;
        config[Grapher.END_TIME] = 978303900L;



        config[Grapher.DATASOURCE] = [
                                            [
                                                name:"myspeed",
                                                dbname: fileName,
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

     public static void testAddDataSourceThrowsExceptionIfNoDBDatasourceSelected() throws Exception{
        Map config = [:]

        String fileName = TestFile.TESTOUTPUT_DIR + "/testRrd.rrd";

        config[Grapher.START_TIME] = 978301200L;
        config[Grapher.END_TIME] = 978303900L;

        config[Grapher.DATASOURCE] = [
                                            /*
                                                There should be at least one
                                                db datasource to work on it

                                                like:
                                                [
                                                    name:"myspeed",
                                                    dbname: fileName,
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

     
}