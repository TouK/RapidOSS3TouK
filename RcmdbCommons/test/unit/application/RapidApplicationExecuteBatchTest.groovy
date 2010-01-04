package application

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.compass.CompassTestObject
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.rcmdb.test.util.ClosureWaitAction
import com.ifountain.rcmdb.test.util.ClosureRunnerThread
import com.ifountain.rcmdb.domain.ObjectProcessor
import com.ifountain.rcmdb.domain.MockObjectProcessorObserver

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Oct 11, 2009
* Time: 10:50:08 PM
* To change this template use File | Settings | File Templates.
*/
class RapidApplicationExecuteBatchTest extends RapidCmdbWithCompassTestCase {
    def models;
    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        initialize();
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testExecuteBatchWithModelWhichHasNoKeys()
    {
        def executed = false
        long t = System.currentTimeMillis();
        2.times {
            println it;
            RapidApplication.executeBatch {
                300.times {
                    models.Model2.add(prop1: "prop1val" + it);
                }
                assertEquals(300, models.Model2.count());
                assertNotNull(models.Model2.search("prop1:prop1val0"));
                models.Model2.list()*.remove()
                assertEquals(0, models.Model2.count());
                executed = true;
            }
        }
        assertTrue(executed)
        println "${System.currentTimeMillis() - t}"
    }

    public void testExecuteBatchWithModelWhichHasKeys()
    {
        def executed = false
        long t = System.currentTimeMillis();
        2.times {
            println it;
            RapidApplication.executeBatch {
                300.times {
                    models.Model1.add(prop1: "prop1val" + it);
                }
                assertEquals(300, models.Model1.count());
                assertNotNull(models.Model1.search("prop1:prop1val0"));
                models.Model1.list()*.remove()
                assertEquals(0, models.Model1.count());
                executed = true;
            }
        }
        assertTrue(executed)
        println "${System.currentTimeMillis() - t}"
    }

    public void testUpdateInExecuteBatchWithModelWhichHasKeys()
    {
        def executed = false
        long t = System.currentTimeMillis();
        RapidApplication.executeBatch {
            300.times {
                models.Model1.add(prop1: "prop1val" + it);
            }
            assertEquals(300, models.Model1.count());
            assertEquals(0, models.Model1.search("prop2:prop2Val").total);

            300.times {
                models.Model1.add(prop1: "prop1val" + it, prop2: "prop2Val");
            }
            assertEquals(300, models.Model1.search("prop2:prop2Val").total);
            executed = true;
        }
        assertTrue(executed)
        println "${System.currentTimeMillis() - t}"
    }

    public void testBatchGetsDirectoryLockAndOtherBatchesWait() {
        def t1state = 0;
        def t2state = 0;
        Object waitLock = new Object();
        def t1 = Thread.start {
            RapidApplication.executeBatch {
                models.Model1.add(prop1: "prop1value1");
                t1state = 1;
                synchronized (waitLock) {
                    waitLock.wait();
                }
            }
            t1state = 2;
        }

        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(1, t1state)
        }))
        def t2 = Thread.start {
            t2state = 1
            RapidApplication.executeBatch {
                models.Model1.add(prop1: "prop1value2");
            }
            t2state = 2;
        }
        Thread.sleep(1000);
        assertEquals(1, t2state);
        synchronized (waitLock) {
            waitLock.notifyAll();
        }
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(2, t1state)
            assertEquals(2, t2state)
        }))
        t1.join();
        t2.join();
    }

    public void testBatchGetsDirectoryLockAndSimpleWriteOperationsWait() {
        def t1state = 0;
        def t2state = 0;
        Object waitLock = new Object();
        def t1 = Thread.start {
            RapidApplication.executeBatch {
                models.Model1.add(prop1: "prop1value1");
                t1state = 1;
                synchronized (waitLock) {
                    waitLock.wait();
                }
            }
            t1state = 2;
        }

        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(1, t1state)
        }))
        def t2 = Thread.start {
            t2state = 1
            models.Model1.add(prop1: "prop1value2");
            t2state = 2;
        }
        Thread.sleep(1000);
        assertEquals(1, t2state);
        synchronized (waitLock) {
            waitLock.notifyAll();
        }
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(2, t1state)
            assertEquals(2, t2state)
        }))
        t1.join();
        t2.join();
    }

    public void testAComplexScenarioWithBatchAndSimpleWriteActions() {
        Object waitLock = new Object();
        def threads = [];
        def batchClosure = {
            synchronized (waitLock) {
                waitLock.wait();
            }
            RapidApplication.executeBatch {
                100.times {
                    def propNumber = (int) (Math.random() * 10)
                    models.Model1.add(prop1: "value${propNumber}")
                }
            }
        }
        def addClosure = {
            synchronized (waitLock) {
                waitLock.wait();
            }
            100.times {
                def propNumber = (int) (Math.random() * 10)
                models.Model1.add(prop1: "value${propNumber}")
            }
        }
        10.times {
            ClosureRunnerThread t = new ClosureRunnerThread(closure: batchClosure);
            t.setName("batch${it}")
            threads.add(t);
            t.start();
        }
        10.times {
            ClosureRunnerThread t = new ClosureRunnerThread(closure: addClosure);
            t.setName("simple${it}")
            threads.add(t);
            t.start();
        }
        CommonTestUtils.waitFor(new ClosureWaitAction({
            threads.each {
                assertTrue(it.isStarted);
            }
        }))
        synchronized (waitLock) {
            waitLock.notifyAll();
        }
        CommonTestUtils.waitFor(new ClosureWaitAction({
            threads.each {
                assertTrue(it.isFinished);
                assertNull(it.exception.toString(), it.exception);
            }
        }), 500)
    }

    public void testRepositoryChangesCannotBeListenedUntilBatchFinishes() {
        ObjectProcessor proc = ObjectProcessor.getInstance();
        MockObjectProcessorObserver observer = new MockObjectProcessorObserver();
        proc.addObserver(observer);
        
        def t1state = 0;
        def t2state = 0;
        Object waitLock = new Object();
        def t1 = Thread.start {
            RapidApplication.executeBatch {
                models.Model1.add(prop1: "prop1value1");
                t1state = 1;
                synchronized (waitLock) {
                    waitLock.wait();
                }
            }
        }

        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(1, t1state)
        }))

        assertEquals(0, observer.repositoryChanges.size());

        synchronized(waitLock){
            waitLock.notifyAll();
        }
        t1.join();
        assertEquals(1, observer.repositoryChanges.size());
        def domainObject = observer.repositoryChanges[0][ObjectProcessor.DOMAIN_OBJECT];
        assertEquals("prop1value1", domainObject.prop1);
    }

    public void testEventTriggersAreNotFiredUntilBatchFinishes() {
        def numberOfAfterAddCalls =0 
        models.Model1.metaClass.afterInsertWrapper = {
            numberOfAfterAddCalls ++;
        }
        def t1state = 0;
        def t2state = 0;
        Object waitLock = new Object();
        def t1 = Thread.start {
            RapidApplication.executeBatch {
                models.Model1.add(prop1: "prop1value1");
                t1state = 1;
                synchronized (waitLock) {
                    waitLock.wait();
                }
            }
        }

        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(1, t1state)
        }))

        assertEquals(0, numberOfAfterAddCalls);

        synchronized(waitLock){
            waitLock.notifyAll();
        }
        t1.join();
        assertEquals(1, numberOfAfterAddCalls);
    }

    def initialize()
    {
        models = [:];
        def model1Name = "Model1";
        def model2Name = "Model2";
        def prop1 = [name: "prop1", type: ModelGenerator.STRING_TYPE];
        def prop2 = [name: "prop2", type: ModelGenerator.STRING_TYPE];
        def model1MetaProps = [name: model1Name]
        def model2MetaProps = [name: model2Name]

        def modelProps = [prop1, prop2];
        def keyPropList = [prop1];


        def model1Text = ModelGenerationTestUtils.getModelText(model1MetaProps, modelProps, keyPropList, []);
        def model2Text = ModelGenerationTestUtils.getModelText(model2MetaProps, modelProps, [], []);
        gcl.parseClass(model1Text)
        gcl.parseClass(model2Text)
        def model1Class = gcl.loadClass(model1Name)
        def model2Class = gcl.loadClass(model2Name)
        models.Model1 = model1Class
        models.Model2 = model2Class
        initialize([RapidApplication, model1Class, model2Class], []);
        CompassForTests.addOperationSupport(RapidApplication, RapidApplicationOperations);
    }
}