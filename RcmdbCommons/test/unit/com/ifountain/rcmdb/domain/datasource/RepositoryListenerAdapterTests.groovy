package com.ifountain.rcmdb.domain.datasource

import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.comp.test.util.logging.TestLogUtils
import com.ifountain.core.connection.ConnectionManager
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.core.test.util.DatasourceTestUtils
import com.ifountain.rcmdb.domain.ObjectProcessor
import com.ifountain.rcmdb.domain.connection.RepositoryConnectionImpl
import com.ifountain.rcmdb.domain.method.EventTriggeringUtils
import com.ifountain.rcmdb.test.util.ClosureWaitAction
import com.ifountain.rcmdb.test.util.RapidCmdbMockTestCase
import connection.Connection
import datasource.BaseDatasource
import datasource.BaseListeningDatasource

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: May 12, 2009
* Time: 4:47:40 PM
*/
class RepositoryListenerAdapterTests extends RapidCmdbMockTestCase {
    public static final String REPOSITORY_TEST_CONNECTION_NAME = "RepoConn"
    RepositoryListenerAdapter adapter;
    public void setUp() {
        super.setUp();
        initialize([Connection, BaseDatasource, BaseListeningDatasource], [gcl.loadClass("RapidDomainClassGrailsPlugin")])
        ConnectionManager.initialize(TestLogUtils.log, DatasourceTestUtils.getParamSupplier(), Thread.currentThread().getContextClassLoader(), 1000);
        DatasourceTestUtils.getParamSupplier().setParam(getConnectionParam());
        def subscriptionFilters = [:]
        subscriptionFilters.put(Connection.class.name, []);
        adapter = new RepositoryListenerAdapter(REPOSITORY_TEST_CONNECTION_NAME, TestLogUtils.log, subscriptionFilters);

    }
    public void tearDown() {
        if (adapter != null) {
            adapter.unsubscribe();
        }
        ConnectionManager.destroy();
        ObjectProcessor.getInstance().deleteObservers();
        super.tearDown();
    }

    public void testSubscribe() throws Exception {
        adapter.subscribe();
        assertTrue(adapter.isSubscribed())
        assertNotNull(adapter.changeProcessorThread);
        assertEquals(1, ObjectProcessor.getInstance().countObservers());
    }

    public void testSubscribeThrowsExceptionIfDomainClassCannotBeFound() throws Exception{
       def subscriptionFilters = ["dummy":[]]
        adapter = new RepositoryListenerAdapter(REPOSITORY_TEST_CONNECTION_NAME, TestLogUtils.log, subscriptionFilters);
        try{
           adapter.subscribe();
           fail("should throw exception")
        }
        catch(e){
        }
    }

    public void testUnsubscribe() throws Exception {
        adapter.subscribe();
        assertEquals(1, ObjectProcessor.getInstance().countObservers());
        adapter.unsubscribe();
        assertFalse(adapter.isSubscribed());
        assertFalse(adapter.changeProcessorThread.isAlive());
        assertEquals(0, ObjectProcessor.getInstance().countObservers());
    }

    public void testUpdate() throws Exception {
        MockRepositoryObserverImpl observer = new MockRepositoryObserverImpl();
        adapter.subscribe();
        adapter.addObserver(observer);

        Connection conn = new Connection();
        Map changeEvent = [:]
        Map updatedProps = [:]
        changeEvent.put(ObjectProcessor.EVENT_NAME, EventTriggeringUtils.AFTER_UPDATE_EVENT)
        changeEvent.put(ObjectProcessor.DOMAIN_OBJECT, conn)
        changeEvent.put(ObjectProcessor.UPDATED_PROPERTIES, updatedProps)
        adapter.update(null, changeEvent)
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(1, observer.receivedObjects.size());
        }))

        Map receivedChangeEvent = observer.receivedObjects[0];
        assertNotSame(receivedChangeEvent, changeEvent);
        assertEquals(3, receivedChangeEvent.size())
        assertEquals(EventTriggeringUtils.AFTER_UPDATE_EVENT, receivedChangeEvent[ObjectProcessor.EVENT_NAME])
        def receivedDomainObject = receivedChangeEvent[ObjectProcessor.DOMAIN_OBJECT];
        assertTrue(receivedDomainObject instanceof Connection);
        assertNotSame(conn, receivedDomainObject);
        conn.getNonFederatedPropertyList().each {p ->
            assertEquals(conn[p.name], receivedDomainObject[p.name])
        }
    }

    public void testQueueProcessorWaitsIfQueueIsEmpty() {
        MockRepositoryObserverImpl observer = new MockRepositoryObserverImpl();
        adapter.subscribe();
        adapter.addObserver(observer);

        Connection conn = new Connection();
        Map changeEvent = [:]
        Map updatedProps = [:]
        changeEvent.put(ObjectProcessor.EVENT_NAME, EventTriggeringUtils.AFTER_INSERT_EVENT)
        changeEvent.put(ObjectProcessor.DOMAIN_OBJECT, conn)
        changeEvent.put(ObjectProcessor.UPDATED_PROPERTIES, updatedProps)
        adapter.update(null, changeEvent)

        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(1, observer.receivedObjects.size());
        }))

        Thread.sleep(3000);
        adapter.update(null, changeEvent)
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(2, observer.receivedObjects.size());
        }))
    }

    public void testClassAndEventBasedFiltering() {
        def subscriptionFilters = [:];
        subscriptionFilters.put(BaseDatasource.class.name, [EventTriggeringUtils.AFTER_INSERT_EVENT])
        subscriptionFilters.put(BaseListeningDatasource.class.name, [])
        adapter = new RepositoryListenerAdapter(REPOSITORY_TEST_CONNECTION_NAME, TestLogUtils.log, subscriptionFilters);

        MockRepositoryObserverImpl observer = new MockRepositoryObserverImpl();
        adapter.subscribe();
        adapter.addObserver(observer);

        BaseDatasource baseDatasource = new BaseDatasource();
        BaseListeningDatasource baseListeningDatasource = new BaseListeningDatasource();

        Map changeEvent = [:]
        changeEvent.put(ObjectProcessor.EVENT_NAME, EventTriggeringUtils.AFTER_INSERT_EVENT)
        changeEvent.put(ObjectProcessor.DOMAIN_OBJECT, baseDatasource);

        adapter.update(null, changeEvent)
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(1, observer.receivedObjects.size());
        }))

        changeEvent = [:]
        changeEvent.put(ObjectProcessor.EVENT_NAME, EventTriggeringUtils.AFTER_INSERT_EVENT)
        changeEvent.put(ObjectProcessor.DOMAIN_OBJECT, baseListeningDatasource);

        adapter.update(null, changeEvent)
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(2, observer.receivedObjects.size());
        }))

        changeEvent = [:]
        changeEvent.put(ObjectProcessor.EVENT_NAME, EventTriggeringUtils.AFTER_DELETE_EVENT)
        changeEvent.put(ObjectProcessor.DOMAIN_OBJECT, baseDatasource);

        adapter.update(null, changeEvent)
        Thread.sleep(1000);
        assertEquals(2, observer.receivedObjects.size());

        changeEvent = [:]
        changeEvent.put(ObjectProcessor.EVENT_NAME, EventTriggeringUtils.AFTER_DELETE_EVENT)
        changeEvent.put(ObjectProcessor.DOMAIN_OBJECT, baseListeningDatasource);

        adapter.update(null, changeEvent)
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(3, observer.receivedObjects.size());
        }))

        changeEvent = [:]
        changeEvent.put(ObjectProcessor.EVENT_NAME, EventTriggeringUtils.AFTER_UPDATE_EVENT)
        changeEvent.put(ObjectProcessor.DOMAIN_OBJECT, baseListeningDatasource);

        adapter.update(null, changeEvent)
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(4, observer.receivedObjects.size());
        }))

        changeEvent = [:]
        changeEvent.put(ObjectProcessor.EVENT_NAME, EventTriggeringUtils.AFTER_UPDATE_EVENT)
        changeEvent.put(ObjectProcessor.DOMAIN_OBJECT, baseDatasource);

        adapter.update(null, changeEvent)
        Thread.sleep(1000);
        assertEquals(4, observer.receivedObjects.size());
    }

    public void testClosureBasedFiltering() {
        def subscriptionFilters = [:];
        subscriptionFilters.put(Connection.class.name, [])

        Closure filterClosure = {changeEvent ->
            def domainObject = changeEvent[ObjectProcessor.DOMAIN_OBJECT];
            def eventName = changeEvent[ObjectProcessor.EVENT_NAME];
            def updatedProps = changeEvent[ObjectProcessor.UPDATED_PROPERTIES];
            if (domainObject instanceof Connection && eventName == EventTriggeringUtils.AFTER_UPDATE_EVENT && updatedProps.containsKey("connectionClass")) {
                return false;
            }
            return true;
        }
        MockRepositoryObserverImpl observer = new MockRepositoryObserverImpl();
        adapter = new RepositoryListenerAdapter(REPOSITORY_TEST_CONNECTION_NAME, TestLogUtils.log, subscriptionFilters, filterClosure);
        adapter.subscribe();
        adapter.addObserver(observer)
        
        Connection conn = new Connection();
        Map changeEvent = [:]
        changeEvent.put(ObjectProcessor.EVENT_NAME, EventTriggeringUtils.AFTER_INSERT_EVENT)
        changeEvent.put(ObjectProcessor.DOMAIN_OBJECT, conn)
        adapter.update(null, changeEvent)

        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(1, observer.receivedObjects.size());
        }))

        changeEvent = [:]
        Map updatedProps = ["connectionClass": RepositoryConnectionImpl.class.name];
        changeEvent.put(ObjectProcessor.EVENT_NAME, EventTriggeringUtils.AFTER_UPDATE_EVENT)
        changeEvent.put(ObjectProcessor.DOMAIN_OBJECT, conn)
        changeEvent.put(ObjectProcessor.UPDATED_PROPERTIES, updatedProps)
        adapter.update(null, changeEvent)

        Thread.sleep(1000);
        assertEquals(1, observer.receivedObjects.size());
    }


    private ConnectionParam getConnectionParam() {
        return new ConnectionParam("RepositoryConnection", REPOSITORY_TEST_CONNECTION_NAME, RepositoryConnectionImpl.class.getName(), [:], 10, 1000, 0);
    }

}

class MockRepositoryObserverImpl implements Observer {
    public List receivedObjects = new ArrayList();

    public void update(Observable o, Object obj) {
        receivedObjects.add(obj);
    }
}