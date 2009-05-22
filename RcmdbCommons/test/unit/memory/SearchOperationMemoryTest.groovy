package memory

import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.compass.CompassTestObject
import com.ifountain.compass.CompositeDirectoryWrapperProvider
import com.ifountain.rcmdb.test.util.AbstractSearchableCompassTests
import com.ifountain.rcmdb.test.util.ClosureWaitAction
import com.ifountain.rcmdb.test.util.compass.TestCompassFactory
import com.ifountain.rcmdb.test.util.compass.TestCompassUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.compass.core.Compass
import org.compass.core.CompassHit
import org.compass.core.CompassQuery
import org.compass.core.CompassQueryBuilder

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 16, 2009
* Time: 11:39:03 AM
* To change this template use File | Settings | File Templates.
*/
class SearchOperationMemoryTest extends AbstractSearchableCompassTests {

    Compass compass;

    public void setUp() {
        super.setUp()
    }

    public void tearDown() {
        super.tearDown();
        if (compass)
        {
            compass.close();
        }
    }
    public void testMemoryLeakWithAThreadFinished()
    {
        def id = 0;
        def instancesToBeSaved = [
                new CompassTestObject(id: id++, prop1: "propertytoken1 propertytoken2 propertytoken3 propertytoken4"),
                new CompassTestObject(id: id++, prop1: "Propertytoken1 propertytoken2 propertytoken3 propertytoken4"),
                new CompassTestObject(id: id++, prop1: "PROPertytoken1 propertytoken2 propertytoken3 propertytoken4"),
                new CompassTestObject(id: id++, prop1: "propertytoken2 propertytoken3 propertytoken4"),
                new CompassTestObject(id: id++, prop1: "propertytoken3 propertytoken4"),
                new CompassTestObject(id: id++, prop1: "propertytoken4"),
                new CompassTestObject(id: id++, prop1: "")
        ];

        def closureToRun = {
            for (int i = 0; i < 1000; i++)
            {
                TestCompassUtils.withCompassQueryBuilder(compass, {CompassQueryBuilder builder ->
                    CompassQuery query = builder.queryString("alias:*").toQuery();
                    def hits = query.hits();
                    assertEquals(instancesToBeSaved.size(), hits.length());
                    hits.iterator().each {CompassHit hit ->
                        assertNotNull(hit.getData());
                    }
                })
            }
        }
        _testMemoryWithThreadWithThread(instancesToBeSaved, closureToRun, false);

    }


    public void testMemoryLeakWithAThreadNotFinished()
    {
        def id = 0;
        def instancesToBeSaved = [
                new CompassTestObject(id: id++, prop1: "propertytoken1 propertytoken2 propertytoken3 propertytoken4"),
                new CompassTestObject(id: id++, prop1: "Propertytoken1 propertytoken2 propertytoken3 propertytoken4"),
                new CompassTestObject(id: id++, prop1: "PROPertytoken1 propertytoken2 propertytoken3 propertytoken4"),
                new CompassTestObject(id: id++, prop1: "propertytoken2 propertytoken3 propertytoken4"),
                new CompassTestObject(id: id++, prop1: "propertytoken3 propertytoken4"),
                new CompassTestObject(id: id++, prop1: "propertytoken4"),
                new CompassTestObject(id: id++, prop1: "")
        ];

        def closureToRun = {
            for (int i = 0; i < 1000; i++)
            {
                TestCompassUtils.withCompassQueryBuilder(compass, {CompassQueryBuilder builder ->
                    CompassQuery query = builder.queryString("alias:*").toQuery();
                    def hits = query.hits();
                    assertEquals(instancesToBeSaved.size(), hits.length());
                    hits.iterator().each {CompassHit hit ->
                        assertNotNull(hit.getData());
                    }
                })
            }
        }
        _testMemoryWithThreadWithThread(instancesToBeSaved, closureToRun, true);
    }


    public void testMemoryLeakWithAThreadNotFinishedAndHugeNumberOfObjects()
    {
        GrailsApplication application = TestCompassFactory.getGrailsApplication([CompassTestObject])
        ApplicationHolder.application = application;
        CompositeDirectoryWrapperProvider provider = new CompositeDirectoryWrapperProvider();
        Map mappings = [:];

        compass = TestCompassFactory.getCompass(application, null, false);

        def id = 0;

        for(int j=0; j < 600; j++)
        {
            def instancesToBeSaved = [];
            for(int i=0; i < 10; i++)
            {
                        instancesToBeSaved.add(new CompassTestObject(id: id++, prop1: "propertytoken1 propertytoken2 propertytoken3 propertytoken4"+i));
            }
            TestCompassUtils.saveToCompass (compass, instancesToBeSaved as Object[]);
        }

        def closureToRun = {
            for (int i = 0; i < 10; i++)
            {
                TestCompassUtils.withCompassQueryBuilder(compass, {CompassQueryBuilder builder ->
                    CompassQuery query = builder.queryString("alias:*").toQuery();
                    def hits = query.hits();
                    assertEquals(6000, hits.length());
                    for(int j = 0; j <200; j++){
                        def hit = hits.hit(j);
                        assertNotNull(hit.getData());
                    }
                })
            }
        }
        _testMemoryWithThreadWithThread(compass, closureToRun, true);
    }




    public void _testMemoryWithThreadWithThread(List instancesToBeSaved, Closure closureToRun, boolean willThreadWait)
    {
        GrailsApplication application = TestCompassFactory.getGrailsApplication([CompassTestObject])
        ApplicationHolder.application = application;
        CompositeDirectoryWrapperProvider provider = new CompositeDirectoryWrapperProvider();
        Map mappings = [:];

        compass = TestCompassFactory.getCompass(application, null, false);
        TestCompassUtils.saveToCompass(compass, instancesToBeSaved);
        _testMemoryWithThreadWithThread(compass, closureToRun, willThreadWait);
    }
    public void _testMemoryWithThreadWithThread(Compass compass, Closure closureToRun, boolean willThreadWait)
    {
        Object lock = new Object();
        boolean isFinished = false;
        def usedMem = 0;
        Thread t = Thread.start {
            Runtime.getRuntime().gc();
            4.times {
                Runtime.getRuntime().gc();
                Thread.sleep(100);
            }
            usedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            closureToRun();
            isFinished = true;
            if(willThreadWait)
            {
                synchronized (lock)
                {
                    lock.wait();
                }
            }
        }
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertTrue(isFinished);
        }), 200)
        4.times {
            Runtime.getRuntime().gc();
            Thread.sleep(100);
        }
        try {
            def usedMemAfterSearchOperations = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            assertTrue("Memory increased ${usedMemAfterSearchOperations-usedMem} number of bytes", usedMemAfterSearchOperations <= usedMem + 600);
        } finally {
            if(willThreadWait)
            {
                synchronized (lock)
                {
                    lock.notifyAll();
                }
            }
        }
    }
}