package com.ifountain.rcmdb.rrd

import com.ifountain.core.test.util.RapidCoreTestCase
import com.ifountain.comp.test.util.file.TestFile
import org.jrobin.core.FetchRequest
import org.jrobin.core.FetchData
import org.jrobin.core.RrdDb
import java.text.DecimalFormat
import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.test.util.CompassForTests

import com.ifountain.comp.test.util.file.TestFile
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase

/**
* Created by IntelliJ IDEA.
* User: ifountain
* Date: Jun 17, 2009
* Time: 9:38:15 AM
* To change this template use File | Settings | File Templates.
*/
class RrdUtilsTests extends RapidCmdbWithCompassTestCase {

    String rrdFileName = TestFile.TESTOUTPUT_DIR + "/testRrd.rrd";
    String testImageFile = TestFile.TESTOUTPUT_DIR + "/testImage.png" 
    def classes=[:];

    public void setUp() {
        super.setUp();
        clearMetaClasses();
        classes.RrdVariable=loadClass("RrdVariable");
        classes.RrdArchive=loadClass("RrdArchive");
        classes.RrdGraphTemplate=loadClass("RrdGraphTemplate");

        initialize([classes.RrdVariable,classes.RrdArchive,classes.RrdGraphTemplate], []);
        CompassForTests.addOperationSupport(classes.RrdVariable, loadClass("RrdVariableOperations"));

        new File(rrdFileName).delete();
        new File(testImageFile).delete();
    }

    public void tearDown() {
        new File(rrdFileName).delete();
        new File(testImageFile).delete();
        clearMetaClasses();
        super.tearDown();
    }

    private void clearMetaClasses()
    {
         ExpandoMetaClass.disableGlobally();
         GroovySystem.metaClassRegistry.removeMetaClass(Grapher);
         ExpandoMetaClass.enableGlobally();
    }
    def loadClass(className) {
        return this.class.classLoader.loadClass(className);
    }

    public void testSuccessfullCreateDatabase() throws Exception{
        Map config = [:]
        config[DbUtils.DATABASE_NAME] = rrdFileName
        config[DbUtils.DATASOURCE] = [
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

        config[DbUtils.ARCHIVE] = [
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

        DbUtils.createDatabase(config)

        assertTrue(new File(rrdFileName).exists());

        Map map = DbUtils.getDatabaseInfo(rrdFileName);
        assertEquals(config[DbUtils.DATABASE_NAME],map[DbUtils.DATABASE_NAME]);
        checkArchives(config[DbUtils.ARCHIVE], map[DbUtils.ARCHIVE]);
        checkDatasources(config[DbUtils.DATASOURCE], map[DbUtils.DATASOURCE]);
        assertTrue(new File(rrdFileName).exists());
    }

    public void testSuccessfullCreateDatabaseWithStartTimeByNumber() throws Exception{
        Map config = [:]
        config[DbUtils.DATABASE_NAME] = rrdFileName
        config[DbUtils.DATASOURCE] = [
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

        config[DbUtils.ARCHIVE] = [
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

        config[DbUtils.START_TIME] = 920804400000L;

        DbUtils.createDatabase(config);

        Map map = DbUtils.getDatabaseInfo(rrdFileName);
        assertEquals(config[DbUtils.DATABASE_NAME],map[DbUtils.DATABASE_NAME]);
        checkDatasources(config[DbUtils.DATASOURCE], map[DbUtils.DATASOURCE]);
        checkArchives(config[DbUtils.ARCHIVE], map[DbUtils.ARCHIVE]);
        //todo: will be handled later 
//        assertEquals(config[DbUtils.START_TIME],map[DbUtils.START_TIME]);

        assertTrue(new File(rrdFileName).exists());
    }

    public void testRemoveDatabaseSuccessful() throws Exception {
        Map config = [:]
        config[DbUtils.DATABASE_NAME] = rrdFileName
        config[DbUtils.DATASOURCE] = [
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

        config[DbUtils.ARCHIVE] = [
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

        config[DbUtils.START_TIME] = 920804400000L;

        DbUtils.createDatabase(config);

        assertTrue(new File(rrdFileName).exists());

        DbUtils.removeDatabase(rrdFileName)

        assertTrue(!(new File(rrdFileName).exists()));

    }

    public void testRemoveDatabaseThrowsExceptionIfDBNotExists() throws Exception {
       try{
           DbUtils.removeDatabase (rrdFileName)
           fail("should throw exception because there is no such file")
       }
       catch(Exception e)
       {
           assertEquals(("File does not exists : " + rrdFileName), e.getMessage())
       }
    }

    public void testIsDatabaseExistsSuccessful() throws Exception {
        Map config = [:]
        config[DbUtils.DATABASE_NAME] = rrdFileName
        config[DbUtils.DATASOURCE] = [
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

        config[DbUtils.ARCHIVE] = [
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

        config[DbUtils.START_TIME] = 920804400000L;

        assertTrue(!(DbUtils.isDatabaseExists(rrdFileName)))

        DbUtils.createDatabase(config);

        assertTrue(DbUtils.isDatabaseExists(rrdFileName))

    }

    private void checkDatasources(dlist1, rrdDslist2) throws Exception {
        int size1 = dlist1.size();
        int size2 = rrdDslist2.size();
        assertEquals(size1, size2);

        for(int i=0; i<rrdDslist2.size(); i++){
            double max, min;

            max = ((double)dlist1[i][DbUtils.MAX]==null)?Double.NaN:(double)dlist1[i][DbUtils.MAX];
            min = ((double)dlist1[i][DbUtils.MIN]==null)?Double.NaN:(double)dlist1[i][DbUtils.MIN];

            assertEquals( dlist1[i][DbUtils.NAME],rrdDslist2[i][DbUtils.NAME]);
            assertEquals( dlist1[i][DbUtils.TYPE],rrdDslist2[i][DbUtils.TYPE]);
            assertEquals( dlist1[i][DbUtils.HEARTBEAT],rrdDslist2[i][DbUtils.HEARTBEAT]);
            assertEquals(max ,(double)rrdDslist2[i][DbUtils.MAX]);
            assertEquals(min,(double)rrdDslist2[i][DbUtils.MIN]);
        }
    }

    private void checkArchives(dlist1, rrdDslist2) throws Exception {
        int size1 = dlist1.size();
        int size2 = rrdDslist2.size();
        assertEquals(size1, size2);

        for(int i=0; i<rrdDslist2.size(); i++){
            assertEquals( dlist1[i][DbUtils.FUNCTION],rrdDslist2[i][DbUtils.FUNCTION]);
            assertEquals( dlist1[i][DbUtils.STEPS],rrdDslist2[i][DbUtils.STEPS]);
            assertEquals( dlist1[i][DbUtils.ROWS],rrdDslist2[i][DbUtils.ROWS]);
            assertEquals( (double)dlist1[i][DbUtils.XFF],(double)rrdDslist2[i][DbUtils.XFF]);
        }
    }

    public void testCreateDatabaseThrowsExceptionIfConfigMissesProperty() throws Exception {
        Map config =[:];
        try{
            DbUtils.createDatabase(config)
            fail("should throw exception because Database name is not specified")
        }
        catch(Exception e){
            assertEquals("Database name is not specified", e.getMessage());
        }

        config[DbUtils.DATABASE_NAME] = rrdFileName

        try{
            DbUtils.createDatabase(config)
            fail("should throw exception because there is no datasource specified")
        }
        catch(Exception e){
            assertEquals("No Datasource specified", e.getMessage());
        }


        config[DbUtils.DATASOURCE] = [
                                            [
                                                name:"testDs",
                                                type:"GAUGE",
                                                heartbeat:600,
                                                max:1,
                                                min:0
                                            ]
                                       ]
        try{
            DbUtils.createDatabase(config)
            fail("should throw exception because there is no archive specified")
        }
        catch(Exception e){
            assertEquals("No archive specified", e.getMessage());
        }
    }

    public void testCreateDatabaseThrowsExceptionIfStartTimeIsNotValid() throws Exception{
        Map config = [:]
        config[DbUtils.DATABASE_NAME] = rrdFileName
        config[DbUtils.DATASOURCE] = [
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

        config[DbUtils.ARCHIVE] = [
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

         config[DbUtils.START_TIME] = "notnumber";

        try{
            DbUtils.createDatabase(config)
            fail("should throw exception because start time is not number")
        }
        catch(Exception e){
            assertEquals("Start time is not valid", e.getMessage());
        }
    }

    public void testCreateDatabaseThrowsExceptionIfStepIsNotNumber() throws Exception{
        Map config = [:]
        config[DbUtils.DATABASE_NAME] = rrdFileName
        config[DbUtils.DATASOURCE] = [
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

        config[DbUtils.ARCHIVE] = [
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

         config[DbUtils.STEP] = "notnumber";

         try{
            DbUtils.createDatabase(config)
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

        def object = DbUtils.getDsDefs(datasource)

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

        def object = DbUtils.getArcDefs(archives)

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

        RrdDb rrdDb = new RrdDb(rrdFileName);
        /* note that Jrobin works with time in seconds not milliseconds as java do */
        FetchRequest fetchRequest = rrdDb.createFetchRequest("AVERAGE", 978301200, 978304200);
        FetchData fetchData = fetchRequest.fetchData();

         def values = fetchData.getValues("a");
         rrdDb.close();

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

    public void testFetchDataThrowsException() throws Exception{
        Map config = [:]
        config[DbUtils.DATABASE_NAME] = rrdFileName;
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
        DbUtils.createDatabase(config);

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

        try{
            DbUtils.fetchData(rrdFileName,"c");
            fail("should throw exception because datasource is not number");
        }catch (Exception e){
            assertEquals("data source not found",e.getMessage());
        }
    }
    
    public void testFetchDataSuccessfully() throws Exception{
        Map config = [:]
        config[DbUtils.DATABASE_NAME] = rrdFileName;
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
                                            rows:30,
                                        ]
                                   ]
        config[DbUtils.START_TIME] = 978300900000;
        DbUtils.createDatabase(config);

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

        DbUtils.fetchData(rrdFileName,"a");
        DbUtils.fetchData(rrdFileName,"b");
    }

    public void testFetchDataByDatabaseNameOnly() throws Exception{
        Map config = [:]
        config[DbUtils.DATABASE_NAME] = rrdFileName;
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
                                            rows:30,
                                        ]
                                   ]
        config[DbUtils.START_TIME] = 978300900000;
        DbUtils.createDatabase(config);

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

        DbUtils.fetchAllData(rrdFileName);
    }

    public void testFetchDataByDatabaseNameOnlyForOneDatapoint() throws Exception{
        Map config = [:]
        config[DbUtils.DATABASE_NAME] = rrdFileName;
        config[DbUtils.DATASOURCE] = [
                                            [
                                                name:"a",
                                                type:"GAUGE",
                                                heartbeat:600,
                                            ]
                                      ]

        config[DbUtils.ARCHIVE] = [
                                        [
                                            function:"AVERAGE",
                                            xff:0.5,
                                            steps:1,
                                            rows:30,
                                        ]
                                   ]
        config[DbUtils.START_TIME] = 978300900000;

        DbUtils.createDatabase(config);

        DbUtils.updateData(rrdFileName,"978301200000:200");
        DbUtils.updateData(rrdFileName,"978301500000:400");
        DbUtils.updateData(rrdFileName,"978301800000:900");
        DbUtils.updateData(rrdFileName,"978302100000:1200");
        DbUtils.updateData(rrdFileName,"978302400000:1400");
        DbUtils.updateData(rrdFileName,"978302700000:1900");
        DbUtils.updateData(rrdFileName,"978303000000:2100");
        DbUtils.updateData(rrdFileName,"978303300000:2400");
        DbUtils.updateData(rrdFileName,"978303600000:2900");
        DbUtils.updateData(rrdFileName,"978303900000:3300");
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

        byte[] bytes = RrdUtils.graph(map);
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(testImageFile) );
        dos.write(bytes);
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

        byte[] bytes = RrdUtils.graph(map);
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(testImageFile) );
        dos.write(bytes);
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
        map['destination']="${TestFile.TESTOUTPUT_DIR}/testDs2.png";


        byte[] bytes = RrdUtils.graph(map);
        
        File f=new File(map['destination']);
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
        //Optional properties:


        byte[] bytes = RrdUtils.graph(map);
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(testImageFile) );
        dos.write(bytes);
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


        byte[] bytes = RrdUtils.graph(map);
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(testImageFile) );
        dos.write(bytes);
    }
    public void testGraphThrowsExceptionIfNoWebResponseIsDefined()
    {
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

        byte[] bytes = RrdUtils.graph(map);
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(testImageFile) );
        dos.write(bytes);
    }



}