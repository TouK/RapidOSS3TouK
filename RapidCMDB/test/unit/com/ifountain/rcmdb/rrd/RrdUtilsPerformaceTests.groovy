package com.ifountain.rcmdb.rrd

import com.ifountain.core.test.util.RapidCoreTestCase
import com.ifountain.comp.test.util.file.TestFile
import junit.framework.TestCase
import org.jrobin.core.FetchRequest
import org.jrobin.core.FetchData
import org.jrobin.core.RrdDb

/**
* Created by IntelliJ IDEA.
* User: ifountain
* Date: Jun 18, 2009
* Time: 2:27:02 PM
*/
class RrdUtilsPerformaceTests extends TestCase{
    private static long start, end;

    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }
    public static void setStartTime(){
        Calendar calendar = Calendar.getInstance();
        start = calendar.getTimeInMillis();
    }
    public static void setEndTime(){
        Calendar calendar = Calendar.getInstance();
        end = calendar.getTimeInMillis();
    }

    public static void testPerformanceUpdateData(){
        Map config = [:]

        String fileName = TestFile.TESTOUTPUT_DIR + "/testRrd.rrd";
        config[RrdUtils.DATABASE_NAME] = fileName

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

        long startTime = 978300900;
        int numberOfUpdatedData = 10000;

        config[RrdUtils.START_TIME] = startTime;
        RrdUtils.createDatabase(config)
        long timeStamp = startTime+300;
        setStartTime();
        for(int i=0; i<numberOfUpdatedData; i++){
             timeStamp+=300;
             RrdUtils.updateData(fileName,timeStamp+":0:0");
        }
        setEndTime();
        long difference = end-start;
        println """Update takes ${difference} milliseconds with ${numberOfUpdatedData}  updates if everyime database is opened and closed. (start:${start} end: ${end})""";


        timeStamp += 300;
        String[] updateEntries = new String[numberOfUpdatedData];
        for(int i=0; i<numberOfUpdatedData; i++){
             timeStamp+=300;
             updateEntries[i] = timeStamp.toString()+":0:0";
        }
        setStartTime();
        RrdUtils.updateData(fileName,updateEntries);
        setEndTime();

        difference = end-start;
        println """Update takes ${difference} milliseconds with ${numberOfUpdatedData}  updates if all the data inserted at once. (start:${start} end: ${end})""";
    }
    public static void testPerformanceDatabaseCreation() throws Exception{
        setStartTime();

        Map config = [:]

        String fileName = TestFile.TESTOUTPUT_DIR + "/testRrd.rrd";
        config[RrdUtils.DATABASE_NAME] = fileName

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
        setEndTime();
    }
}