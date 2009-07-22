package connector

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import datasource.JabberDatasource
import connection.JabberConnection
import com.ifountain.rcmdb.test.util.CompassForTests
import datasource.BaseDatasource
import connection.Connection

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Apr 9, 2009
* Time: 9:22:35 AM
*/
class JabberConnectorOperationsTest extends RapidCmdbWithCompassTestCase {
    def connectorSaveParams;
    def connectorUpdateParams;
    public void setUp() {
        super.setUp();
        initializeForAddAndUpdate();
        connectorSaveParams = [name: "testConnector", host: "jabberHost", port: 5222,
                username: "testaccount", userPassword: "3600",serviceName:"a.com"];

        connectorUpdateParams = [name: "testConnector2", host: "jabberHost2", port: 500,
                username: "newtestaccount", userPassword: "3601",serviceName:"b.com"]
    }

    public void tearDown() {
        super.tearDown();
    }

    def initializeForAddAndUpdate()
    {
        initialize([JabberConnector, JabberDatasource, JabberConnection, BaseDatasource, Connection], [], false);
        CompassForTests.addOperationSupport(JabberConnector, JabberConnectorOperations);
    }

    public void testSuccessfulSave()
    {
        def createdObjects = JabberConnectorOperations.addConnector(connectorSaveParams)

        def jabberConnectors = JabberConnector.list();
        assertEquals(1, jabberConnectors.size());
        JabberConnector jabberConnector = jabberConnectors[0]
        assertNotNull(createdObjects["jabberConnector"])
        assertEquals(jabberConnector.id,createdObjects["jabberConnector"].id)
        assertEquals(connectorSaveParams.name, jabberConnector.name);


        def jabberConnections = JabberConnection.list();
        assertEquals(1, jabberConnections.size());
        def jabberConnection = jabberConnections[0];
        assertNotNull(createdObjects["jabberConnection"])
        assertEquals(jabberConnection.id,createdObjects["jabberConnection"].id);

        assertEquals(JabberConnector.getJabberConnectionName(jabberConnector.name),jabberConnection.name);
        assertEquals(connectorSaveParams["host"], jabberConnection.host)
        assertEquals(connectorSaveParams["port"], jabberConnection.port)
        assertEquals(connectorSaveParams["username"], jabberConnection.username)
        assertEquals(connectorSaveParams["userPassword"], jabberConnection.userPassword)
        assertEquals(connectorSaveParams["serviceName"], jabberConnection.serviceName)
        assertEquals(jabberConnection.id,jabberConnector.ds.connection.id);
        assertEquals(1,jabberConnection.jabberDatasources.size());

        def jabberDatasources = JabberDatasource.list();
        assertEquals(1, jabberDatasources.size());
        JabberDatasource jabberDatasource = jabberDatasources[0];
        assertNotNull(createdObjects["jabberDatasource"])
        assertEquals(jabberDatasource.id,createdObjects["jabberDatasource"].id)
        assertEquals(JabberConnector.getJabberDatasourceName(jabberConnector.name),jabberDatasource.name);
        assertEquals(jabberDatasource.id, jabberConnector.ds.id);
        assertEquals(jabberConnection.id,jabberDatasource.connection.id);
        assertEquals(0, jabberDatasource.reconnectInterval);
        assertEquals(jabberConnection.jabberDatasources[0].id, jabberDatasource.id);
    }

    void testAddRollsBackIfConnectorHasErrors() {

        def existingConnector=JabberConnector.add(connectorSaveParams);
        assertFalse(existingConnector.hasErrors());

        def createdObjects = JabberConnectorOperations.addConnector(connectorSaveParams)
        assertTrue(createdObjects.jabberConnector.hasErrors())
        assertEquals(1, JabberConnector.count())
        assertEquals(0, JabberConnection.count())
        assertEquals(0, JabberDatasource.count())
    }

    void testAddRollsBackIfConnectionHasErrors() {
        def existingConnection=Connection.add(name:JabberConnectorOperations.getJabberConnectionName(connectorSaveParams.name));
        assertFalse(existingConnection.hasErrors());

        def createdObjects = JabberConnectorOperations.addConnector(connectorSaveParams)
        assertTrue(createdObjects.jabberConnection.hasErrors())
        assertEquals(0, JabberConnector.count())
        assertEquals(0, JabberConnection.count())
        assertEquals(0, JabberDatasource.count())


    }

    void testAddRollsBackIfDatasourceHasErrors() {
        def datasource = BaseDatasource.add(name: JabberConnector.getJabberDatasourceName(connectorSaveParams.name))
        assertFalse(datasource.hasErrors())

        def createdObjects = JabberConnectorOperations.addConnector(connectorSaveParams)
        def connector = JabberConnector.list()[0]
        assertEquals(0, JabberConnector.count())
        assertEquals(0, JabberConnection.count())
        assertEquals(0, JabberDatasource.count())

        def ds = createdObjects.jabberDatasource;
        assertTrue(ds.hasErrors())
    }



    public void testSuccessfulUpdate()
    {
        def createdObjects = JabberConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, JabberConnector.count());
        JabberConnector oldJabberConnector = JabberConnector.get(name:connectorSaveParams.name);

        def updatedObjects = JabberConnectorOperations.updateConnector(oldJabberConnector, connectorUpdateParams);

        def jabberConnectors = JabberConnector.list();
        assertEquals(1, jabberConnectors.size());
        JabberConnector jabberConnector = jabberConnectors[0]
        assertNotNull(updatedObjects["jabberConnector"])
        assertEquals(jabberConnector.id,updatedObjects["jabberConnector"].id)
        assertEquals(connectorUpdateParams.name, jabberConnector.name);


        def jabberConnections = JabberConnection.list();
        assertEquals(1, jabberConnections.size());
        def jabberConnection = jabberConnections[0];
        assertNotNull(updatedObjects["jabberConnection"])
        assertEquals(jabberConnection.id,updatedObjects["jabberConnection"].id);
        assertEquals(JabberConnector.getJabberConnectionName(jabberConnector.name),jabberConnection.name);
        assertEquals(jabberConnection.id, jabberConnector.ds.connection.id);
        assertEquals(1,jabberConnection.jabberDatasources.size());
        assertEquals(connectorUpdateParams["host"], jabberConnection.host)
        assertEquals(connectorUpdateParams["port"], jabberConnection.port)
        assertEquals(connectorUpdateParams["username"], jabberConnection.username)
        assertEquals(connectorUpdateParams["userPassword"], jabberConnection.userPassword)
        assertEquals(connectorUpdateParams["serviceName"], jabberConnection.serviceName)


        def jabberDatasources = JabberDatasource.list();
        assertEquals(1, jabberDatasources.size());
        JabberDatasource jabberDatasource = jabberDatasources[0];
        assertNotNull(updatedObjects["jabberDatasource"])
        assertEquals(jabberDatasource.id,updatedObjects["jabberDatasource"].id)
        assertEquals(JabberConnector.getJabberDatasourceName(jabberConnector.name),jabberDatasource.name);
        assertEquals(jabberDatasource.id, jabberConnector.ds.id);
        assertEquals(jabberConnection.id,jabberDatasource.connection.id);
        assertEquals(0, jabberDatasource.reconnectInterval);
        assertEquals(jabberConnection.jabberDatasources[0].id, jabberDatasource.id);
    }

    void testUpdateRollsbackIfConnectionHasErrors() {
        def createdObjects = JabberConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, JabberConnector.count())
        assertEquals(1, JabberConnection.count())
        assertEquals(1, JabberDatasource.count())

        def baseConn = Connection.add(name: connectorUpdateParams.name)
        assertFalse(baseConn.hasErrors())


        def updatedObjects = JabberConnectorOperations.updateConnector(createdObjects["jabberConnector"], connectorUpdateParams)

        def conn = updatedObjects.jabberConnection;

        assertTrue(conn.hasErrors())
        assertEquals("default.not.unique.message", conn.errors.allErrors[0].code)

        assertEquals(1, JabberConnector.count())
        assertEquals(1, JabberConnection.count())
        assertEquals(1, JabberDatasource.count())

        checkObjectsInRepoAreNotChanged();
    }

    void testUpdateRollsbackIfDatasourceHasErrors() {
        def createdObjects = JabberConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, JabberConnector.count())
        assertEquals(1, JabberConnection.count())
        assertEquals(1, JabberDatasource.count())

        def baseDs = BaseDatasource.add(name: JabberConnector.getJabberDatasourceName(connectorUpdateParams.name))
        assertFalse(baseDs.hasErrors())

        def updatedObjects = JabberConnectorOperations.updateConnector(createdObjects["jabberConnector"], connectorUpdateParams);
        def datasource = updatedObjects.jabberDatasource;

        assertTrue(datasource.hasErrors())
        assertEquals("default.not.unique.message", datasource.errors.allErrors[0].code)

        assertEquals(1, JabberConnector.count())
        assertEquals(1, JabberConnection.count())
        assertEquals(1, JabberDatasource.count())


        checkObjectsInRepoAreNotChanged();
    }

    void testUpdateRollsbackIfConnectorHasErrors() {
        def createdObjects = JabberConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, JabberConnector.count())
        assertEquals(1, JabberConnection.count())
        assertEquals(1, JabberDatasource.count())

        connectorUpdateParams["name"] = null;
        def updatedObjects = JabberConnectorOperations.updateConnector(createdObjects["jabberConnector"], connectorUpdateParams);


        assertTrue(updatedObjects.jabberConnector.hasErrors())

        checkObjectsInRepoAreNotChanged();

    }
    private def checkObjectsInRepoAreNotChanged()
    {
        assertNull(JabberConnector.get(name: connectorUpdateParams.name))
        def jabberConnector = JabberConnector.get(name: connectorSaveParams.name);
        assertNotNull(jabberConnector)

        assertEquals(connectorSaveParams.host, jabberConnector.ds.connection.host)
        assertEquals(JabberConnector.getJabberDatasourceName(connectorSaveParams.name), jabberConnector.ds.name)

    }

     void testSuccessfullDelete() {
        def createdObjects = JabberConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, JabberConnector.count());
        assertEquals(1, JabberConnection.count());
        assertEquals(1, JabberDatasource.count());

        JabberConnectorOperations.deleteConnector(createdObjects["jabberConnector"])

        assertEquals(0, JabberConnector.count());
        assertEquals(0, JabberConnection.count());
        assertEquals(0, JabberDatasource.count());
    }

}