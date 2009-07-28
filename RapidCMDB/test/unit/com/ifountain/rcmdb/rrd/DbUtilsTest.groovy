package com.ifountain.rcmdb.rrd

import com.ifountain.core.test.util.RapidCoreTestCase
import com.ifountain.comp.test.util.file.TestFile
import org.jrobin.core.FetchRequest
import org.jrobin.core.FetchData
import org.jrobin.core.RrdDb
import java.text.DecimalFormat
import org.jrobin.core.RrdException

/**
* Created by IntelliJ IDEA.
* User: ifountain
* Date: Jun 17, 2009
* Time: 9:38:15 AM
* To change this template use File | Settings | File Templates.
*/
class DbUtilsTests extends RapidCoreTestCase {

    String rrdFileName = TestFile.TESTOUTPUT_DIR + "/testRrd.rrd";

    protected void setUp() {
        super.setUp();
        new File(rrdFileName).delete();
    }

    protected void tearDown() {
        new File(rrdFileName).delete();
        new File(rrdFileName+".xml").delete();
        super.tearDown();
    }

    public void testSuccessfullCreateDatabase() throws Exception {
        Map config = [:]
        config[DbUtils.DATABASE_NAME] = rrdFileName
        config[DbUtils.DATASOURCE] = [  [
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
                                         ] ]
        config[DbUtils.ARCHIVE] = [ [
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
                                    ] ]
        DbUtils.createDatabase(config)

        assertTrue(new File(rrdFileName).exists());

        Map map = DbUtils.getDatabaseInfo(rrdFileName);
        assertEquals(config[DbUtils.DATABASE_NAME],map[DbUtils.DATABASE_NAME]);
        checkArchives(config[DbUtils.ARCHIVE], map[DbUtils.ARCHIVE]);
        checkDatasources(config[DbUtils.DATASOURCE], map[DbUtils.DATASOURCE]);
        assertTrue(new File(rrdFileName).exists());
    }

    public void testSuccessfullCreateDatabaseWithStartTimeByNumber() throws Exception {
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

        config[DbUtils.DATASOURCE] = [ [
                                          name:"testDs",
                                          type:"GAUGE",
                                          heartbeat:600,
                                          max:1,
                                          min:0
                                       ] ]
        try{
            DbUtils.createDatabase(config)
            fail("should throw exception because there is no archive specified")
        }
        catch(Exception e){
            assertEquals("No archive specified", e.getMessage());
        }
    }

    public void testCreateDatabaseThrowsExceptionIfStartTimeIsNotValid() throws Exception {
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

    public void testCreateDatabaseThrowsExceptionIfStepIsNotNumber() throws Exception {
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

    public void testUpdateDataSuccessful() throws Exception {
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
                                            rows:30,
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
        rrdDb.exportXml (rrdFileName+".xml");
        // note that Jrobin works with time in seconds not milliseconds as java do
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

    public void testFetchDataThrowsException() throws Exception {
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
            DbUtils.fetchAllDataAsMap(rrdFileName, ['c'], 'AVERAGE');
            fail("should throw exception because datasource is not number");
        }catch (Exception e){
            assertEquals("Unknown datasource name: c",e.getMessage());
        }
    }

    public void testFetchDataSuccessfully() throws Exception {
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

        def result =  DbUtils.fetchAllDataAsMap(rrdFileName, ['a', 'b'], 'AVERAGE');

        assertTrue('result should include value whose key is \'a\'', result.containsKey('a'))
        assertTrue('result should include value whose key is \'b\'', result.containsKey('b'))
        assertEquals('values retrived for source b is incorrect', 1.0D, result['b']['978302400'])
        assertEquals('values retrived for source b is incorrect', 2.0D, result['b']['978303900'])
    }

    public void testSynchronizedUpdateAndFetch() {
        def initialTime = 900000000000 - 60000

        Map config = [:]
        config[DbUtils.DATABASE_NAME] = rrdFileName;
        config[DbUtils.DATASOURCE] = [ [ name:"threadSource",
                                         type:"GAUGE",
                                         heartbeat:120
                                       ] ]
        config[DbUtils.ARCHIVE] = [ [ function:"AVERAGE",
                                      xff:0.5,
                                      steps:1,
                                      rows: 50
                                    ] ]
        config[DbUtils.START_TIME] = initialTime
        config[DbUtils.STEP] = 60

        DbUtils.createDatabase(config)

        def updateTime = initialTime + 60000
        def threadList = []
        def exceptionCount = 0;
        50.times{
            def index = it
            Runnable write = {
                                try {
                                    DbUtils.updateData(rrdFileName, updateTime + ":" + index)
                                }
                                catch(RrdException e) {
                                    synchronized(this){ exceptionCount++ }
                                }
                             }
            Runnable read = {
                                Map result = DbUtils.fetchAllDataAsMap (rrdFileName,['threadSource'],'AVERAGE')['threadSource'];
                                assertTrue("Result map is not true ", (result["900000000"] >= 0
                                                && result["900000000"] < 50) || result["900000000"] == Double.NaN
                                                || result["900000000"] == null);
                             }

            Thread thread = new Thread(index%2==0?read:write)
            thread.start()
            threadList.add(thread)
        }
        threadList.each{ it.join()}

        assertEquals("There is 24 exception required to be caught", 24, exceptionCount)

        Map result = DbUtils.fetchAllDataAsMap (rrdFileName,['threadSource'],'AVERAGE')['threadSource'];
        assertTrue("Result map is not true ", result["900000000"] >= 0 && result["900000000"] < 50);
    }

    public void testSuccessfullSynchronizedUpdateAndFetch() {
        def step = 60000
        def initialTime = 900000000000 - step

        Map config = [:]
        config[DbUtils.DATABASE_NAME] = rrdFileName;
        config[DbUtils.DATASOURCE] = [ [ name:"threadSource",
                                         type:"GAUGE",
                                         heartbeat:120
                                       ] ]
        config[DbUtils.ARCHIVE] = [ [ function:"AVERAGE",
                                      xff:0.5,
                                      steps:1,
                                      rows: 50
                                    ] ]
        config[DbUtils.START_TIME] = initialTime
        config[DbUtils.STEP] = 60

        DbUtils.createDatabase(config)

        def updateTime = initialTime + step
        def threadList = []
        def updatedData = [:]
        def erroredData = [:]
        50.times{
            def index = it
            Runnable write = {
                                try {
                                    DbUtils.updateData(rrdFileName, updateTime + ":" + index)
                                    synchronized(this) {
                                        updatedData[updateTime] = index
                                        updateTime = updateTime + step
                                    }
                                }
                                catch(RrdException e) {
                                    synchronized(this) { erroredData[updateTime] = index }
                                }
                             }
            Thread thread = new Thread(write)
            thread.start()
            threadList.add(thread)
        }
        threadList.each{it.join()}

        Map result = DbUtils.fetchAllDataAsMap (rrdFileName,['threadSource'],'AVERAGE')['threadSource'];

        for( e in updatedData ) {
            assertEquals('Inserted value is not retrived', (double)e.value, result.get(((long)(e.key/1000)).toString()))
        }
        for( e in erroredData ) {
            assertFalse('Data, whose insertion failed, is retrived', (double)e.value == result.get(((long)(e.key/1000)).toString()) )
        }
    }

    /*
     * Creates a sample rrd database to be used in tests
     */
    private void createDatabase() {

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
    }

    /*
     * to check retrived map. used in some tests
     */
    private void assertMap(Map data) {
        assertEquals("Map values are not equal","1.0",data.get("978301200").toString());
        assertEquals("Map values are not equal","4.0",data.get("978301500").toString());
        assertEquals("Map values are not equal","5.0",data.get("978301800").toString());
        assertEquals("Map values are not equal","3.0",data.get("978302100").toString());
        assertEquals("Map values are not equal","1.0",data.get("978302400").toString());
        assertEquals("Map values are not equal","2.0",data.get("978302700").toString());
        assertEquals("Map values are not equal","4.0",data.get("978303000").toString());
        assertEquals("Map values are not equal","6.0",data.get("978303300").toString());
        assertEquals("Map values are not equal","4.0",data.get("978303600").toString());
        assertEquals("Map values are not equal","2.0",data.get("978303900").toString());
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

}