package com.ifountain.rcmdb.domain

import com.ifountain.rcmdb.domain.method.EventTriggeringUtils
import com.ifountain.rcmdb.test.util.RapidCmdbMockTestCase
import connection.Connection
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.rcmdb.test.util.ClosureWaitAction

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: May 12, 2009
* Time: 9:13:33 AM
* To change this template use File | Settings | File Templates.
*/
class ObjectProcessorTests extends RapidCmdbMockTestCase {

    public void setUp() {
        super.setUp();
        initialize([Connection], [gcl.loadClass("RapidDomainClassGrailsPlugin")])
    }
    public void tearDown() {
        ObjectProcessor.getInstance().deleteObservers();
        super.tearDown();
    }

    public void testObjectProcessorIsSingleton() {
        ObjectProcessor proc1 = ObjectProcessor.getInstance();
        ObjectProcessor proc2 = ObjectProcessor.getInstance();
        assertSame(proc1, proc2)
    }

    public void testRepositoryChanged() {
        ObjectProcessor proc = ObjectProcessor.getInstance();
        MockObjectProcessorObserver observer = new MockObjectProcessorObserver();
        proc.addObserver(observer);

        Connection conn = new Connection();
        proc.repositoryChanged(EventTriggeringUtils.AFTER_INSERT_EVENT, conn);
        assertEquals(1, observer.repositoryChanges.size());

        Map repositoryChange = observer.repositoryChanges[0];
        assertEquals(2, repositoryChange.size());
        assertEquals(EventTriggeringUtils.AFTER_INSERT_EVENT, repositoryChange[ObjectProcessor.EVENT_NAME]);
        def domainObject = repositoryChange[ObjectProcessor.DOMAIN_OBJECT]
        assertNotSame(conn, domainObject);
        domainObject.getNonFederatedPropertyList().each {p ->
            assertEquals(conn[p.name], domainObject[p.name]);
        };

        def updateParams = ["prop1": "prop1Value1", "prop2": "prop2Value2"]
        Connection conn2 = new Connection();
        proc.repositoryChanged(EventTriggeringUtils.AFTER_UPDATE_EVENT, conn2, updateParams);
        assertEquals(2, observer.repositoryChanges.size());

        repositoryChange = observer.repositoryChanges[1];
        assertEquals(3, repositoryChange.size());
        assertEquals(EventTriggeringUtils.AFTER_UPDATE_EVENT, repositoryChange[ObjectProcessor.EVENT_NAME]);
        domainObject = repositoryChange[ObjectProcessor.DOMAIN_OBJECT]
        assertNotSame(conn2, domainObject);
        domainObject.getNonFederatedPropertyList().each {p ->
            assertEquals(conn[p.name], domainObject[p.name]);
        };
        def updatedProperties = repositoryChange[ObjectProcessor.UPDATED_PROPERTIES];
        assertEquals(updateParams.size(), updatedProperties.size())
        updatedProperties.each {key, value ->
            assertEquals(updateParams[key], value);
        }
        try {
            updatedProperties.put("some", "value");
            fail("should throw exception")
        }
        catch (UnsupportedOperationException e) {}
    }

    public void testBatchExecutionKeepsRepositoryChangesUntilBatchFinishes() {
        ObjectProcessor proc = ObjectProcessor.getInstance();
        MockObjectProcessorObserver observer = new MockObjectProcessorObserver();
        proc.addObserver(observer);
        proc.batchStarted();
        Connection conn = new Connection();
        Connection conn2 = new Connection();
        Connection conn3 = new Connection();
        proc.repositoryChanged(EventTriggeringUtils.AFTER_INSERT_EVENT, conn);
        proc.repositoryChanged(EventTriggeringUtils.AFTER_INSERT_EVENT, conn2);
        proc.repositoryChanged(EventTriggeringUtils.AFTER_INSERT_EVENT, conn3);
        assertEquals(0, observer.repositoryChanges.size());

        proc.batchFinished();
        assertEquals(3, observer.repositoryChanges.size())
    }

    public void testBatchExecutionContextIsThreadLocal() {
        ObjectProcessor proc = ObjectProcessor.getInstance();
        MockObjectProcessorObserver observer = new MockObjectProcessorObserver();
        proc.addObserver(observer);
        proc.batchStarted();
        Connection conn = new Connection(name: "conn1");
        Connection conn2 = new Connection(name: "conn2");

        Object waitLock = new Object();
        def t1state = 0;
        def t1 = Thread.start {
            proc.batchStarted()
            proc.repositoryChanged(EventTriggeringUtils.AFTER_INSERT_EVENT, conn);
            t1state = 1;
            synchronized (waitLock) {
                waitLock.wait();
            }
            proc.batchFinished();
            t1state = 2;
        }

        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(1, t1state);
        }))
        assertEquals(0, observer.repositoryChanges.size());

        def t2 = Thread.start {
            proc.repositoryChanged(EventTriggeringUtils.AFTER_INSERT_EVENT, conn2);
        }
        t2.join();
        assertEquals(1, observer.repositoryChanges.size())
        def domainObject = observer.repositoryChanges[0][ObjectProcessor.DOMAIN_OBJECT]
        assertEquals("conn2", domainObject.name);

        synchronized (waitLock) {
            waitLock.notifyAll();
        }
        t1.join();

        assertEquals(2, observer.repositoryChanges.size())
        domainObject = observer.repositoryChanges[1][ObjectProcessor.DOMAIN_OBJECT]
        assertEquals("conn1", domainObject.name);
    }

}
class MockObjectProcessorObserver implements Observer {
    List repositoryChanges = [];
    public void update(Observable o, Object arg) {
        repositoryChanges.add(arg);
    }
}