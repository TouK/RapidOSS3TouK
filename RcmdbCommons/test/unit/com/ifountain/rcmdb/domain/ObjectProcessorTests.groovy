package com.ifountain.rcmdb.domain

import com.ifountain.rcmdb.domain.method.EventTriggeringUtils
import com.ifountain.rcmdb.test.util.RapidCmdbMockTestCase
import connection.Connection
import org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin
import java.util.Collections.UnmodifiableMap


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
        proc.repositoryChanged(EventTriggeringUtils.BEFORE_INSERT_EVENT, conn);
        assertEquals(1, observer.repositoryChanges.size());

        Map repositoryChange = observer.repositoryChanges[0];
        assertEquals(2, repositoryChange.size());
        assertEquals(EventTriggeringUtils.BEFORE_INSERT_EVENT, repositoryChange[ObjectProcessor.EVENT_NAME]);
        def domainObject = repositoryChange[ObjectProcessor.DOMAIN_OBJECT]
        assertNotSame(conn, domainObject);
        domainObject.getNonFederatedPropertyList().each {p ->
            assertEquals(conn[p.name], domainObject[p.name]);
        };

        def updateParams = ["prop1":"prop1Value1", "prop2":"prop2Value2"]
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
        updatedProperties.each{key, value ->
            assertEquals(updateParams[key], value);
        }
        try{
            updatedProperties.put("some", "value");
            fail("should throw exception")
        }
        catch(UnsupportedOperationException e){}
    }

}
class MockObjectProcessorObserver implements Observer {
    List repositoryChanges = [];
    public void update(Observable o, Object arg) {
        repositoryChanges.add(arg);
    }
}