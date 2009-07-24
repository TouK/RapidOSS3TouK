package com.ifountain.rcmdb.datasource

import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.rcmdb.test.util.ClosureWaitAction
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import datasource.BaseListeningDatasource
import com.ifountain.rcmdb.test.util.LoggerForTest

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Mar 12, 2009
 * Time: 4:40:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class RunnerObjectTest extends RapidCmdbWithCompassTestCase {
    RunnerObject rObj;
    public void setUp() {
        super.setUp();
        rObj = new RunnerObject(2)
    }
    public void tearDown() {
        if (rObj != null) {
            try {
                rObj.stop();
            }
            catch (e) {}

        }
        super.tearDown();
    }

    public void testStartAdapter() {
        def ds = new RunnerBaseListeningDatasourceMock(id: 1);
        ListeningAdapterRunner runner = new MockListeningAdapterRunner(ds.id);
        ListeningAdapterRunnerFactory.setRunner(runner);
        rObj.start(ds);
        waitForState(rObj, AdapterStateProvider.STARTED);
    }

    public void testStartThrowsExceptionIfAdapterIsNotInOneOfStopStates()
    {
        def ds = new RunnerBaseListeningDatasourceMock(id: 1);
        ListeningAdapterRunner runner = new MockListeningAdapterRunner(ds.id);
        ListeningAdapterRunnerFactory.setRunner(runner);
        rObj.runner = runner;
        runner.setState(AdapterStateProvider.INITIALIZING);
        try {
            rObj.start(ds);
            fail("Should throw exception since it is already started");
        }
        catch (ListeningAdapterException e) {
            assertEquals(ListeningAdapterException.adapterAlreadyStartedException(rObj.datasourceId).getMessage(), e.getMessage());
        }
        runner.setState(AdapterStateProvider.INITIALIZED);
        try
        {
            rObj.start(ds);
            fail("Should throw exception since it is already started");
        } catch (ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.adapterAlreadyStartedException(rObj.datasourceId).getMessage(), e.getMessage());
        }

        runner.setState(AdapterStateProvider.STARTED);
        try
        {
            rObj.start(ds);
            fail("Should throw exception since it is already started");
        } catch (ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.adapterAlreadyStartedException(rObj.datasourceId).getMessage(), e.getMessage());
        }

        runner.setState(AdapterStateProvider.STOPPING);
        try
        {
            rObj.start(ds);
            fail("Should throw exception ");
        } catch (ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.stoppingStateException(rObj.datasourceId, "start").getMessage(), e.getMessage());
        }

        runner.setState(AdapterStateProvider.NOT_STARTED);
        try
        {
            rObj.start(ds);
            waitForState(rObj, AdapterStateProvider.STARTED);
            rObj.stop();
        }
        catch (e) {
            fail("Should not throw exception");
        }
        runner.setState(AdapterStateProvider.STOPPED_WITH_EXCEPTION);
        try
        {
            rObj.start(ds);
            waitForState(rObj, AdapterStateProvider.STARTED);
            rObj.stop();
        }
        catch (e) {
            fail("Should not throw exception");
        }
        runner.setState(AdapterStateProvider.STOPPED);
        try
        {
            rObj.start(ds);
            waitForState(rObj, AdapterStateProvider.STARTED);
            rObj.stop();
        }
        catch (e) {
            fail("Should not throw exception");
        }

    }

    public void testStartAdapterWithAdapterRunnerThrowingException() {
        def ds = new RunnerBaseListeningDatasourceMock(id: 1);
        MockListeningAdapterRunner runner = new MockListeningAdapterRunner(ds.id);
        runner.setStartException(ListeningAdapterException.couldNotSubscribed(runner.datasourceId, new Exception("")), AdapterStateProvider.STOPPED_WITH_EXCEPTION);
        def testLogger=new LoggerForTest();
        runner.logger=testLogger;
        
        ListeningAdapterRunnerFactory.setRunner(runner);
        rObj.start(ds);
        waitForState(rObj, AdapterStateProvider.STOPPED_WITH_EXCEPTION);

        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(1,testLogger.logHistory.WARN.size())
            assertTrue(testLogger.logHistory.WARN[0].message.indexOf("Exception occurred while starting adapter with datasource id ${ds.id}")>=0);
        }));
    }

    public void testStopAdapter() {
        def ds = new RunnerBaseListeningDatasourceMock(id: 1);
        ListeningAdapterRunner runner = new MockListeningAdapterRunner(ds.id);
        ListeningAdapterRunnerFactory.setRunner(runner);
        rObj.start(ds);
        waitForState(rObj, AdapterStateProvider.STARTED);
        Thread.sleep(300);
        rObj.stop();
        assertEquals(AdapterStateProvider.STOPPED, rObj.getState());
        assertTrue(runner.cleanUpCalled)
    }

    public void testStopThrowsExceptionIfAdapterIsNotInOneOfStartStates()
    {
        def ds = new RunnerBaseListeningDatasourceMock(id: 1);
        def runner = new MockListeningAdapterRunner(ds.id);
        ListeningAdapterRunnerFactory.setRunner(runner);
        rObj.runner = runner;
        runner.setState(AdapterStateProvider.NOT_STARTED);
        try
        {
            rObj.stop();
            fail("Should throw exception since it is already stpped");
        } catch (ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.adapterAlreadyStoppedException(rObj.datasourceId).getMessage(), e.getMessage());
        }

        runner.setState(AdapterStateProvider.STOPPED);
        try
        {
            rObj.stop();
            fail("Should throw exception since it is already stpped");
        } catch (ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.adapterAlreadyStoppedException(rObj.datasourceId).getMessage(), e.getMessage());
        }
        runner.setState(AdapterStateProvider.STOPPING);
        try
        {
            rObj.stop();
            fail("Should throw exception");
        } catch (ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.stoppingStateException(rObj.datasourceId, "stop").getMessage(), e.getMessage());
        }

        runner.setState(AdapterStateProvider.STOPPED_WITH_EXCEPTION);
        try
        {
            rObj.stop();
            fail("Should throw exception since it is already stpped");
        } catch (ListeningAdapterException e)
        {
            assertEquals(ListeningAdapterException.adapterAlreadyStoppedException(rObj.datasourceId).getMessage(), e.getMessage());
        }
        runner.setState(AdapterStateProvider.NOT_STARTED);
        rObj.start(ds);
        waitForState(rObj, AdapterStateProvider.STARTED);
        //Following states are valid stop states
        runner.setState(AdapterStateProvider.INITIALIZING);
        try
        {
            rObj.stop();
        }
        catch (e) {
            fail("Should not throw exception");
        }
        runner.setState(AdapterStateProvider.NOT_STARTED);
        rObj.start(ds);
        runner.setState(AdapterStateProvider.INITIALIZED);
        try
        {
            rObj.stop();
        }
        catch (e) {
            fail("Should not throw exception");
        }
        runner.setState(AdapterStateProvider.NOT_STARTED);
        rObj.start(ds);
        waitForState(rObj, AdapterStateProvider.STARTED);
        try
        {
            rObj.stop();
        }
        catch (e) {
            fail("Should not throw exception");
        }
    }

    def waitForState(RunnerObject runnerObj, String state) {
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(state, runnerObj.getState());
        }));
    }

}

class MockListeningAdapterRunner extends ListeningAdapterRunner {
    ListeningAdapterException startException;
    String stateWillBeSet = AdapterStateProvider.NOT_STARTED;
    boolean cleanUpCalled = false;
    public MockListeningAdapterRunner(Long datasourceId) {
        super(datasourceId);
    }
    public void stop() {
        setState(AdapterStateProvider.STOPPED)
    }

    public void cleanUp() {
        cleanUpCalled = true;
    }


    public void start(BaseListeningDatasource listeningDatasource) {
        if (startException != null) {
            setState(stateWillBeSet);
            throw startException;
        }
        if (!isStopCalled()) {
            setState(AdapterStateProvider.STARTED);
        }

    }

    public void setStartException(ListeningAdapterException ex, String state) {
        startException = ex;
        stateWillBeSet = state;
    }
}
