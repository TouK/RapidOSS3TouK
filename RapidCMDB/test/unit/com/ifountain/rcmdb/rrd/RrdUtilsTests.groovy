package com.ifountain.rcmdb.rrd

import com.ifountain.core.test.util.RapidCoreTestCase
import com.ifountain.comp.test.util.file.TestFile
import org.jrobin.core.FetchRequest
import org.jrobin.core.FetchData
import org.jrobin.core.RrdDb
import java.text.DecimalFormat
import org.jrobin.core.Datasource
import org.apache.commons.io.FileUtils
import org.jrobin.core.DsDef

/**
* Created by IntelliJ IDEA.
* User: ifountain
* Date: Jun 17, 2009
* Time: 9:38:15 AM
* To change this template use File | Settings | File Templates.
*/
class RrdUtilsTests extends RapidCoreTestCase {

    String rrdFileName = TestFile.TESTOUTPUT_DIR + "/testRrd.rrd";

    protected void setUp() {
        super.setUp();

        new File(rrdFileName).delete();
    }

    protected void tearDown() {
        super.tearDown();
    }


    public void testSuccessfullCreateDatabase() throws Exception{

        Map config = [:]


        config[RrdUtils.DATABASE_NAME] = rrdFileName

        config[RrdUtils.DATASOURCE] = [
                                            [
                                                name:"testDs",
                                                type:"GAUGE",
                                                heartbeat:600,
                                                max:10,
                                                min:2
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
                                            steps:6,
                                            rows:10,
                                        ],
                                        [
                                            function:"MIN",
                                            xff:0.5,
                                            steps:3,
                                            rows:7,
                                        ]
                                   ]

        RrdUtils.createDatabase(config)

        assertTrue(new File(rrdFileName).exists());

        Map map = RrdUtils.getDatabaseInfo(rrdFileName);
        assertEquals(config[RrdUtils.DATABASE_NAME],map[RrdUtils.DATABASE_NAME]);
        checkArchives(config[RrdUtils.ARCHIVE], map[RrdUtils.ARCHIVE]);
        checkDatasources(config[RrdUtils.DATASOURCE], map[RrdUtils.DATASOURCE]);
        assertTrue(new File(rrdFileName).exists());
    }

    public void testSuccessfullCreateDatabaseWithStartTimeByNumber() throws Exception{

        Map config = [:]


        config[RrdUtils.DATABASE_NAME] = rrdFileName

        config[RrdUtils.DATASOURCE] = [
                                            [
                                                name:"testDs",
                                                type:"GAUGE",
                                                heartbeat:600,
                                                max:1,
                                                min:0
                                            ],
                                            [
                                                name:"testDs2",
                                                type:"GAUGE",
                                                heartbeat:600,
                                                max:2,
                                                min:0
                                            ]
                                      ]

        config[RrdUtils.ARCHIVE] = [
                                        [
                                            function:"AVERAGE",
                                            xff:0.5,
                                            steps:6,
                                            rows:10,
                                        ],
                                        [
                                            function:"MIN",
                                            xff:0.5,
                                            steps:1,
                                            rows:5,
                                        ]
                                   ]

        config[RrdUtils.START_TIME] = 920804400L;

        RrdUtils.createDatabase(config);

        Map map = RrdUtils.getDatabaseInfo(rrdFileName);
        assertEquals(config[RrdUtils.DATABASE_NAME],map[RrdUtils.DATABASE_NAME]);
        checkDatasources(config[RrdUtils.DATASOURCE], map[RrdUtils.DATASOURCE]);
        checkArchives(config[RrdUtils.ARCHIVE], map[RrdUtils.ARCHIVE]);
        assertEquals(config[RrdUtils.START_TIME],map[RrdUtils.START_TIME]);

        assertTrue(new File(rrdFileName).exists());
    }

    private void checkDatasources(dlist1, rrdDslist2) throws Exception {
        int size1 = dlist1.size();
        int size2 = rrdDslist2.size();
        assertEquals(size1, size2);

        for(int i=0; i<rrdDslist2.size(); i++){
            double max, min;

            max = ((double)dlist1[i][RrdUtils.MAX]==null)?Double.NaN:(double)dlist1[i][RrdUtils.MAX];
            min = ((double)dlist1[i][RrdUtils.MIN]==null)?Double.NaN:(double)dlist1[i][RrdUtils.MIN];

            assertEquals( dlist1[i][RrdUtils.NAME],rrdDslist2[i][RrdUtils.NAME]);
            assertEquals( dlist1[i][RrdUtils.TYPE],rrdDslist2[i][RrdUtils.TYPE]);
            assertEquals( dlist1[i][RrdUtils.HEARTBEAT],rrdDslist2[i][RrdUtils.HEARTBEAT]);
            assertEquals(max ,(double)rrdDslist2[i][RrdUtils.MAX]);
            assertEquals(min,(double)rrdDslist2[i][RrdUtils.MIN]);

//            assertEquals( dlist1[i][RrdUtils.NAME],rrdDslist2[i].getDsName());
//            assertEquals( dlist1[i][RrdUtils.TYPE],rrdDslist2[i].getDsType());
//            assertEquals( dlist1[i][RrdUtils.HEARTBEAT],rrdDslist2[i].getHeartbeat());
//            assertEquals(max ,(double)rrdDslist2[i].getMaxValue());
//            assertEquals(min,(double)rrdDslist2[i].getMinValue());
        }
    }

    private void checkArchives(dlist1, rrdDslist2) throws Exception {
        int size1 = dlist1.size();
        int size2 = rrdDslist2.size();
        assertEquals(size1, size2);

        for(int i=0; i<rrdDslist2.size(); i++){
            assertEquals( dlist1[i][RrdUtils.FUNCTION],rrdDslist2[i][RrdUtils.FUNCTION]);
            assertEquals( dlist1[i][RrdUtils.STEPS],rrdDslist2[i][RrdUtils.STEPS]);
            assertEquals( dlist1[i][RrdUtils.ROWS],rrdDslist2[i][RrdUtils.ROWS]);
            assertEquals( (double)dlist1[i][RrdUtils.XFF],(double)rrdDslist2[i][RrdUtils.XFF]);


//            assertEquals( dlist1[i][RrdUtils.FUNCTION],rrdDslist2[i].getConsolFun());
//            assertEquals( dlist1[i][RrdUtils.STEPS],rrdDslist2[i].getSteps());
//            assertEquals( dlist1[i][RrdUtils.ROWS],rrdDslist2[i].getRows());
//            assertEquals( (double)dlist1[i][RrdUtils.XFF],(double)rrdDslist2[i].getXff());
        }
    }

    public void testCreateDatabaseThrowsExceptionIfConfigMissesProperty() throws Exception {
        Map config =[:];
        try{
            RrdUtils.createDatabase(config)
            fail("should throw exception because Database name is not specified")
        }
        catch(Exception e){
            assertEquals("Database name is not specified", e.getMessage());
        }


        config[RrdUtils.DATABASE_NAME] = rrdFileName

        try{
            RrdUtils.createDatabase(config)
            fail("should throw exception because there is no datasource specified")
        }
        catch(Exception e){
            assertEquals("No Datasource specified", e.getMessage());
        }


        config[RrdUtils.DATASOURCE] = [
                                            [
                                                name:"testDs",
                                                type:"GAUGE",
                                                heartbeat:600,
                                                max:1,
                                                min:0
                                            ]
                                       ]
        try{
            RrdUtils.createDatabase(config)
            fail("should throw exception because there is no archive specified")
        }
        catch(Exception e){
            assertEquals("No archive specified", e.getMessage());
        }
    }
    
    public void testCreateDatabaseThrowsExceptionIfStartTimeIsNotValid() throws Exception{

        Map config = [:]


        config[RrdUtils.DATABASE_NAME] = rrdFileName

        config[RrdUtils.DATASOURCE] = [
                                            [
                                                name:"testDs",
                                                type:"GAUGE",
                                                heartbeat:600,
                                                max:1,
                                                min:0
                                            ],
                                            [
                                                name:"testDs2",
                                                type:"GAUGE",
                                                heartbeat:600,
                                                max:5,
                                                min:2
                                            ]
                                      ]

        config[RrdUtils.ARCHIVE] = [
                                        [
                                            function:"AVERAGE",
                                            xff:0.5,
                                            steps:6,
                                            rows:10,
                                        ],
                                        [
                                            function:"MIN",
                                            xff:0.5,
                                            steps:1,
                                            rows:5,
                                        ]
                                   ]

         config[RrdUtils.START_TIME] = "notnumber";

        try{
            RrdUtils.createDatabase(config)
            fail("should throw exception because start time is not number")
        }
        catch(Exception e){
            assertEquals("Start time is not valid", e.getMessage());
        }
    }

    public void testCreateDatabaseThrowsExceptionIfStepIsNotNumber() throws Exception{

        Map config = [:]


        config[RrdUtils.DATABASE_NAME] = rrdFileName

        config[RrdUtils.DATASOURCE] = [
                                            [
                                                name:"testDs",
                                                type:"GAUGE",
                                                heartbeat:600,
                                                max:2,
                                                min:1
                                            ],
                                            [
                                                name:"testDs2",
                                                type:"GAUGE",
                                                heartbeat:600,
                                                max:3,
                                                min:0
                                            ]
                                      ]

        config[RrdUtils.ARCHIVE] = [
                                        [
                                            function:"AVERAGE",
                                            xff:0.5,
                                            steps:6,
                                            rows:10,
                                        ],
                                        [
                                            function:"MIN",
                                            xff:0.5,
                                            steps:1,
                                            rows:5,
                                        ]
                                   ]

         config[RrdUtils.STEP] = "notnumber";

         try{
            RrdUtils.createDatabase(config)
            fail("should throw exception because step is not number")
         }
         catch(Exception e){
             assertEquals("Step is not valid", e.getMessage());
         }

    }

    public void testGetDsDefSuccessful() throws Exception {
       def datasource = [
                            [
                                name:"testDs",
                                type:"GAUGE",
                                heartbeat:600,
                                max:10,
                                min:0
                            ],
                            [
                                name:"testDs2",
                                type:"COUNTER",
                                heartbeat:600
                            ]
                        ]

        def object = RrdUtils.getDsDefs(datasource)

        assertEquals(2, object.length);

        assertEquals("testDs",object[0].getDsName())
        assertEquals("GAUGE", object[0].getDsType())
        assertEquals(600, object[0].getHeartbeat())
        assertEquals(10.0D, object[0].getMaxValue())
        assertEquals(0.0D, object[0].getMinValue())

        assertEquals("testDs2", object[1].getDsName())
        assertEquals("COUNTER", object[1].getDsType())
        assertEquals(600, object[1].getHeartbeat())
        assertEquals(Double.NaN, object[1].getMaxValue())
        assertEquals(Double.NaN, object[1].getMinValue())

    }

    public void testGetArcDefSuccessful() throws Exception {
       def archives = [
                            [
                                function:"AVERAGE",
                                xff:0.5,
                                steps:6,
                                rows:10,
                            ],
                            [
                                function:"MIN",
                                xff:0.5,
                                steps:1,
                                rows:5,
                            ]
                     ]

        def object = RrdUtils.getArcDefs(archives)

        assertEquals(2, object.length);

        assertEquals("AVERAGE",object[0].getConsolFun())
        assertEquals(0.5D, object[0].getXff())
        assertEquals(6, object[0].getSteps())
        assertEquals(10, object[0].getRows())

        assertEquals("MIN",object[1].getConsolFun())
        assertEquals(0.5D, object[1].getXff())
        assertEquals(1, object[1].getSteps())
        assertEquals(5, object[1].getRows())

    }

    public void testUpdateDataSuccessful() throws Exception{

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


        FetchRequest fetchRequest = new RrdDb(rrdFileName).createFetchRequest("AVERAGE", 978301200, 978304200);
        FetchData fetchData = fetchRequest.fetchData();
//        fetchData.println();

         def values = fetchData.getValues("a");
         DecimalFormat df = new DecimalFormat("#.##");
         def newValues = [];
         values.each{
             if(it!= Double.NaN){
                newValues.add(df.format(it))
             }
         };
         def expectedValues = ["0.67", "1.67", "1", "0.67", "1.67", "0.67", "1", "1.67", "1.33"];
         assertEquals(expectedValues, newValues);
    }

 /*
//    public void testGetStartTimeSuccessfulByDate() throws Exception{
//
//        String time = "2009-06-17"
//
//        def object = RrdUtils.getFormattedStartTime(time)
//
//        Calendar check = Calendar.getInstance()
//        check.clear()
//        check.set(2009, 6, 17)
//        assertEquals(check, object)
//    }
//
//    public void testGetStartTimeSuccessfulByTime() throws Exception{
//
//        String time = "2009-06-17, 16:39:10"
//
//        def object = RrdUtils.getFormattedStartTime(time)
//
//        Calendar check = Calendar.getInstance()
//        check.clear()
//        check.set(2009, 6, 17, 16, 39, 10)
//        assertEquals(check, object)
//
//    }
//
*/
 /*
    public void testSuccessfullCreateDatabaseWithStartTimeByDate() throws Exception{

        Map config = [:]

        String fileName = TestFile.TESTOUTPUT_DIR + "/testRrd.rrd";
        config[RrdUtils.DATABASE_NAME] = fileName

        config[RrdUtils.DATASOURCE] = [
                                            [
                                                name:"testDs",
                                                type:"GAUGE",
                                                heartbeat:600,
                                                max:3,
                                                min:0
                                            ],
                                            [
                                                name:"testDs2",
                                                type:"GAUGE",
                                                heartbeat:600,
                                                max:67,
                                                min:0
                                            ]
                                      ]

        config[RrdUtils.ARCHIVE] = [
                                        [
                                            function:"AVERAGE",
                                            xff:0.5,
                                            steps:6,
                                            rows:10,
                                        ],
                                        [
                                            function:"MIN",
                                            xff:0.5,
                                            steps:1,
                                            rows:5,
                                        ]
                                   ]

        config[RrdUtils.START_TIME] = "2009-06-17";

        RrdUtils.createDatabase(config)

        assertTrue(new File(fileName).exists());
    }

    public void testSuccessfullCreateDatabaseWithStartTimeByTime() throws Exception{

        Map config = [:]

        String fileName = TestFile.TESTOUTPUT_DIR + "/testRrd.rrd";
        config[RrdUtils.DATABASE_NAME] = fileName

        config[RrdUtils.DATASOURCE] = [
                                            [
                                                name:"testDs",
                                                type:"GAUGE",
                                                heartbeat:600,
                                                max:6,
                                                min:0
                                            ],
                                            [
                                                name:"testDs2",
                                                type:"GAUGE",
                                                heartbeat:600,
                                                max:4,
                                                min:0
                                            ]
                                      ]

        config[RrdUtils.ARCHIVE] = [
                                        [
                                            function:"AVERAGE",
                                            xff:0.5,
                                            steps:6,
                                            rows:10,
                                        ],
                                        [
                                            function:"MIN",
                                            xff:0.5,
                                            steps:1,
                                            rows:5,
                                        ]
                                   ]

        config[RrdUtils.START_TIME] = "2009-06-17, 14:59:55";

        RrdUtils.createDatabase(config)

        assertTrue(new File(fileName).exists());
    }
     */

}