import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import java.text.SimpleDateFormat
import com.ifountain.rcmdb.test.util.RapidApplicationTestUtils
import application.RapidApplication

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Sep 11, 2009
* Time: 2:47:30 PM
* To change this template use File | Settings | File Templates.
*/
class RsHistoricalEventOperationsTest extends RapidCmdbWithCompassTestCase {
    public void setUp() {
        super.setUp();
        initialize([RsHistoricalEvent], []);
        CompassForTests.addOperationSupport(RsHistoricalEvent, RsHistoricalEventOperations);
        RapidApplicationTestUtils.initializeRapidApplicationOperations(RapidApplication);
        RapidApplicationTestUtils.clearProcessors();
    }

    public void tearDown() {
        super.tearDown();
    }


    public void testBeforeInsertCalculations()
    {
        def createdAt = new Date().getTime();
        def clearedAt = createdAt + 200000;
        def historicalEvent = RsHistoricalEvent.add(name: "event1", createdAt: createdAt, clearedAt: clearedAt);
        assertFalse(historicalEvent.hasErrors());
        RsHistoricalEvent historicalEventFromrepository = RsHistoricalEvent.get(id: historicalEvent.id);
        assertEquals(clearedAt - createdAt, historicalEventFromrepository.duration);

        RsHistoricalEventOperations.formatters.each {propName, SimpleDateFormat formatter ->
            assertEquals(historicalEventFromrepository[propName], formatter.format(new Date(createdAt)));
            assertEquals(historicalEvent[propName], formatter.format(new Date(createdAt)));
        }
    }

    public void testCalculateDuration()
    {
        def createdAt = new Date().getTime();
        def clearedAt = createdAt + 200000;
        def historicalEvent = RsHistoricalEvent.add(name: "event1", createdAt: createdAt, clearedAt: clearedAt);
        assertFalse(historicalEvent.hasErrors());

        assertEquals(clearedAt - createdAt, historicalEvent.calculateDuration());
    }

    public void testHistoricalEventCacheFunctions()
     {
         //test retrieve does not return null
         def historicalEventsCache=RsHistoricalEvent.retrieveHistoricalEventCache();
         assertEquals(0,historicalEventsCache.size());

         //test clear stores a new arrary and returns it
         def clearedHistoricalEventCache=RsHistoricalEvent.clearHistoricalEventCache();
         assertEquals(0,clearedHistoricalEventCache.size());
         assertSame (RsHistoricalEvent.retrieveHistoricalEventCache(),clearedHistoricalEventCache);
         assertNotSame (historicalEventsCache,clearedHistoricalEventCache);

         //test saveHistoricalEventCache
         historicalEventsCache=RsHistoricalEvent.retrieveHistoricalEventCache();
         RsHistoricalEvent.addToHistoricalEventCache(RsHistoricalEvent,[name:"abc"])
         assertEquals(1,historicalEventsCache.size());

         assertEquals(0,RsHistoricalEvent.count());
         RsHistoricalEvent.saveHistoricalEventCache();

         //it does clear so it saves a new empty array to cache
         assertEquals(1,historicalEventsCache.size());
         assertNotSame (historicalEventsCache,RsHistoricalEvent.retrieveHistoricalEventCache());
         assertEquals(0,RsHistoricalEvent.retrieveHistoricalEventCache().size());


         assertEquals(1,RsHistoricalEvent.count());
         assertEquals(1,RsHistoricalEvent.countHits("name:abc"));
     }

     //Test do 100 times clear on each 10 threads, 1000 clears total , measures the performance of average clear time
     public void testHistoricalEventCachePerformanceWithMultipleThreads()
     {
         def threadCount=10;
         def eventCountPerThread=100; // per thread
         def eventInstance=RsEvent.newInstance();

         def threads=[];
         def threadTimes=[];
         def totalStartTime=System.nanoTime();

         threadCount.times{ threadCounter ->
             def thredTimesIndex=threadCounter+0;
             def thread=Thread.start{
                 def threadName =Thread.currentThread().name;
                 try{
                     def startTime=System.nanoTime();
                     eventCountPerThread.times{ eventCounter ->
                         //println "clear ${threadName} ${it.name}"
                         RsHistoricalEvent.addToHistoricalEventCache(RsHistoricalEvent,[name:"${threadName}_${eventCounter}"]);
                     }
                     def endTime=System.nanoTime();
                     threadTimes[thredTimesIndex]=endTime-startTime;
                 }
                 catch(e)
                 {
                     println("Exception in thread ${threadName}. Reason ${e}");
                     e.printStackTrace();
                 }
             };
             threads.add(thread);
         }

         threads.each{
            it.join();
         }
         def totalEndTime=System.nanoTime();

         threadCount.times{ threadIndex ->
             def threadTime=threadTimes[threadIndex];
             def threadTimeMsec=threadTime/1000000l;
             def eventTimeMsec=threadTimeMsec / eventCountPerThread;
             println "Thread ${threadIndex} Total Clear Time ${threadTime} nanos , ${threadTimeMsec} msecs, Per Event Clear Time ${eventTimeMsec} msecs";
             assertTrue("Event Clear Time for each thread should be less then 2 msec",eventTimeMsec < 2);
         }

         def totalTimeMsec=( totalEndTime - totalStartTime) / 1000000l;
         def eventTimeMsecFromTotalTime =  totalTimeMsec / eventCountPerThread / threadCount;

         println "Total Time ${totalTimeMsec } msec , Per Event Clear Time From Total ${eventTimeMsecFromTotalTime} msecs";
         assertTrue("From Total Time: Event Clear Time for each thread should be less then 0.3 msec",eventTimeMsecFromTotalTime < 0.3);

         assertEquals(eventCountPerThread * threadCount,RsHistoricalEvent.retrieveHistoricalEventCache().size());

     }

}