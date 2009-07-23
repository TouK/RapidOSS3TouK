package connector

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import datasource.AolDatasource
import connection.AolConnection
import com.ifountain.rcmdb.test.util.CompassForTests
import datasource.BaseDatasource
import connection.Connection

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Apr 9, 2009
* Time: 9:22:35 AM
*/
class AolConnectorOperationsTest extends RapidCmdbWithCompassTestCase {
    def connectorSaveParams;
    def connectorUpdateParams;
    public void setUp() {
        super.setUp();
        initializeForAddAndUpdate();
        connectorSaveParams = [name: "testConnector", host: "aolHost", port: 5222,
                username: "testaccount", userPassword: "3600"];

        connectorUpdateParams = [name: "testConnector2", host: "aolHost2", port: 500,
                username: "newtestaccount", userPassword: "3601"]
    }

    public void tearDown() {
        super.tearDown();
    }

    def initializeForAddAndUpdate()
    {
        initialize([AolConnector, AolDatasource, AolConnection, BaseDatasource, Connection], [], false);
        CompassForTests.addOperationSupport(AolConnector, AolConnectorOperations);
    }

    public void testSuccessfulSave()
    {
        def createdObjects = AolConnectorOperations.addConnector(connectorSaveParams)

        def aolConnectors = AolConnector.list();
        assertEquals(1, aolConnectors.size());
        AolConnector aolConnector = aolConnectors[0]
        assertNotNull(createdObjects["aolConnector"])
        assertEquals(aolConnector.id,createdObjects["aolConnector"].id)
        assertEquals(connectorSaveParams.name, aolConnector.name);


        def aolConnections = AolConnection.list();
        assertEquals(1, aolConnections.size());
        def aolConnection = aolConnections[0];
        assertNotNull(createdObjects["aolConnection"])
        assertEquals(aolConnection.id,createdObjects["aolConnection"].id);

        assertEquals(AolConnector.getAolConnectionName(aolConnector.name),aolConnection.name);
        assertEquals(connectorSaveParams["host"], aolConnection.host)
        assertEquals(connectorSaveParams["port"], aolConnection.port)
        assertEquals(connectorSaveParams["username"], aolConnection.username)
        assertEquals(connectorSaveParams["userPassword"], aolConnection.userPassword)
        assertEquals(aolConnection.id,aolConnector.ds.connection.id);
        assertEquals(1,aolConnection.aolDatasources.size());

        def aolDatasources = AolDatasource.list();
        assertEquals(1, aolDatasources.size());
        AolDatasource aolDatasource = aolDatasources[0];
        assertNotNull(createdObjects["aolDatasource"])
        assertEquals(aolDatasource.id,createdObjects["aolDatasource"].id)
        assertEquals(AolConnector.getAolDatasourceName(aolConnector.name),aolDatasource.name);
        assertEquals(aolDatasource.id, aolConnector.ds.id);
        assertEquals(aolConnection.id,aolDatasource.connection.id);
        assertEquals(0, aolDatasource.reconnectInterval);
        assertEquals(aolConnection.aolDatasources[0].id, aolDatasource.id);
    }

    void testAddRollsBackIfConnectorHasErrors() {

        def existingConnector=AolConnector.add(connectorSaveParams);
        assertFalse(existingConnector.hasErrors());

        def createdObjects = AolConnectorOperations.addConnector(connectorSaveParams)
        assertTrue(createdObjects.aolConnector.hasErrors())
        assertEquals(1, AolConnector.count())
        assertEquals(0, AolConnection.count())
        assertEquals(0, AolDatasource.count())
    }

    void testAddRollsBackIfConnectionHasErrors() {
        def existingConnection=Connection.add(name:AolConnectorOperations.getAolConnectionName(connectorSaveParams.name));
        assertFalse(existingConnection.hasErrors());

        def createdObjects = AolConnectorOperations.addConnector(connectorSaveParams)
        assertTrue(createdObjects.aolConnection.hasErrors())
        assertEquals(0, AolConnector.count())
        assertEquals(0, AolConnection.count())
        assertEquals(0, AolDatasource.count())


    }

    void testAddRollsBackIfDatasourceHasErrors() {
        def datasource = BaseDatasource.add(name: AolConnector.getAolDatasourceName(connectorSaveParams.name))
        assertFalse(datasource.hasErrors())

        def createdObjects = AolConnectorOperations.addConnector(connectorSaveParams)
        def connector = AolConnector.list()[0]
        assertEquals(0, AolConnector.count())
        assertEquals(0, AolConnection.count())
        assertEquals(0, AolDatasource.count())

        def ds = createdObjects.aolDatasource;
        assertTrue(ds.hasErrors())
    }



    public void testSuccessfulUpdate()
    {
        def createdObjects = AolConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, AolConnector.count());
        AolConnector oldAolConnector = AolConnector.get(name:connectorSaveParams.name);

        def updatedObjects = AolConnectorOperations.updateConnector(oldAolConnector, connectorUpdateParams);

        def aolConnectors = AolConnector.list();
        assertEquals(1, aolConnectors.size());
        AolConnector aolConnector = aolConnectors[0]
        assertNotNull(updatedObjects["aolConnector"])
        assertEquals(aolConnector.id,updatedObjects["aolConnector"].id)
        assertEquals(connectorUpdateParams.name, aolConnector.name);


        def aolConnections = AolConnection.list();
        assertEquals(1, aolConnections.size());
        def aolConnection = aolConnections[0];
        assertNotNull(updatedObjects["aolConnection"])
        assertEquals(aolConnection.id,updatedObjects["aolConnection"].id);
        assertEquals(AolConnector.getAolConnectionName(aolConnector.name),aolConnection.name);
        assertEquals(aolConnection.id, aolConnector.ds.connection.id);
        assertEquals(1,aolConnection.aolDatasources.size());
        assertEquals(connectorUpdateParams["host"], aolConnection.host)
        assertEquals(connectorUpdateParams["port"], aolConnection.port)
        assertEquals(connectorUpdateParams["username"], aolConnection.username)
        assertEquals(connectorUpdateParams["userPassword"], aolConnection.userPassword)


        def aolDatasources = AolDatasource.list();
        assertEquals(1, aolDatasources.size());
        AolDatasource aolDatasource = aolDatasources[0];
        assertNotNull(updatedObjects["aolDatasource"])
        assertEquals(aolDatasource.id,updatedObjects["aolDatasource"].id)
        assertEquals(AolConnector.getAolDatasourceName(aolConnector.name),aolDatasource.name);
        assertEquals(aolDatasource.id, aolConnector.ds.id);
        assertEquals(aolConnection.id,aolDatasource.connection.id);
        assertEquals(0, aolDatasource.reconnectInterval);
        assertEquals(aolConnection.aolDatasources[0].id, aolDatasource.id);
    }

    void testUpdateRollsbackIfConnectionHasErrors() {
        def createdObjects = AolConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, AolConnector.count())
        assertEquals(1, AolConnection.count())
        assertEquals(1, AolDatasource.count())

        def baseConn = Connection.add(name: connectorUpdateParams.name)
        assertFalse(baseConn.hasErrors())


        def updatedObjects = AolConnectorOperations.updateConnector(createdObjects["aolConnector"], connectorUpdateParams)

        def conn = updatedObjects.aolConnection;

        assertTrue(conn.hasErrors())
        assertEquals("default.not.unique.message", conn.errors.allErrors[0].code)

        assertEquals(1, AolConnector.count())
        assertEquals(1, AolConnection.count())
        assertEquals(1, AolDatasource.count())

        checkObjectsInRepoAreNotChanged();
    }

    void testUpdateRollsbackIfDatasourceHasErrors() {
        def createdObjects = AolConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, AolConnector.count())
        assertEquals(1, AolConnection.count())
        assertEquals(1, AolDatasource.count())

        def baseDs = BaseDatasource.add(name: AolConnector.getAolDatasourceName(connectorUpdateParams.name))
        assertFalse(baseDs.hasErrors())

        def updatedObjects = AolConnectorOperations.updateConnector(createdObjects["aolConnector"], connectorUpdateParams);
        def datasource = updatedObjects.aolDatasource;

        assertTrue(datasource.hasErrors())
        assertEquals("default.not.unique.message", datasource.errors.allErrors[0].code)

        assertEquals(1, AolConnector.count())
        assertEquals(1, AolConnection.count())
        assertEquals(1, AolDatasource.count())


        checkObjectsInRepoAreNotChanged();
    }

    void testUpdateRollsbackIfConnectorHasErrors() {
        def createdObjects = AolConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, AolConnector.count())
        assertEquals(1, AolConnection.count())
        assertEquals(1, AolDatasource.count())

        connectorUpdateParams["name"] = null;
        def updatedObjects = AolConnectorOperations.updateConnector(createdObjects["aolConnector"], connectorUpdateParams);


        assertTrue(updatedObjects.aolConnector.hasErrors())

        checkObjectsInRepoAreNotChanged();

    }
    private def checkObjectsInRepoAreNotChanged()
    {
        assertNull(AolConnector.get(name: connectorUpdateParams.name))
        def aolConnector = AolConnector.get(name: connectorSaveParams.name);
        assertNotNull(aolConnector)

        assertEquals(connectorSaveParams.host, aolConnector.ds.connection.host)
        assertEquals(AolConnector.getAolDatasourceName(connectorSaveParams.name), aolConnector.ds.name)

    }

     void testSuccessfullDelete() {
        def createdObjects = AolConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, AolConnector.count());
        assertEquals(1, AolConnection.count());
        assertEquals(1, AolDatasource.count());

        AolConnectorOperations.deleteConnector(createdObjects["aolConnector"])

        assertEquals(0, AolConnector.count());
        assertEquals(0, AolConnection.count());
        assertEquals(0, AolDatasource.count());
    }

}