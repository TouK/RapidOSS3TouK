package connector

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import datasource.SametimeDatasource
import connection.SametimeConnection
import com.ifountain.rcmdb.test.util.CompassForTests
import datasource.BaseDatasource
import connection.Connection

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Apr 9, 2009
* Time: 9:22:35 AM
*/
class SametimeConnectorOperationsTest extends RapidCmdbWithCompassTestCase {
    def connectorSaveParams;
    def connectorUpdateParams;
    public void setUp() {
        super.setUp();
        initializeForAddAndUpdate();
        connectorSaveParams = [name: "testConnector", host: "sametimeHost",
                username: "testaccount", userPassword: "3600",community:"a.com"];

        connectorUpdateParams = [name: "testConnector2", host: "sametimeHost2",
                username: "newtestaccount", userPassword: "3601",community:"b.com"]
    }

    public void tearDown() {
        super.tearDown();
    }

    def initializeForAddAndUpdate()
    {
        initialize([SametimeConnector, SametimeDatasource, SametimeConnection, BaseDatasource, Connection], [], false);
        CompassForTests.addOperationSupport(SametimeConnector, SametimeConnectorOperations);
    }

    public void testSuccessfulSave()
    {
        def createdObjects = SametimeConnectorOperations.addConnector(connectorSaveParams)

        def sametimeConnectors = SametimeConnector.list();
        assertEquals(1, sametimeConnectors.size());
        SametimeConnector sametimeConnector = sametimeConnectors[0]
        assertNotNull(createdObjects["sametimeConnector"])
        assertEquals(sametimeConnector.id,createdObjects["sametimeConnector"].id)
        assertEquals(connectorSaveParams.name, sametimeConnector.name);


        def sametimeConnections = SametimeConnection.list();
        assertEquals(1, sametimeConnections.size());
        def sametimeConnection = sametimeConnections[0];
        assertNotNull(createdObjects["sametimeConnection"])
        assertEquals(sametimeConnection.id,createdObjects["sametimeConnection"].id);

        assertEquals(SametimeConnector.getSametimeConnectionName(sametimeConnector.name),sametimeConnection.name);
        assertEquals(connectorSaveParams["host"], sametimeConnection.host)
        assertEquals(connectorSaveParams["username"], sametimeConnection.username)
        assertEquals(connectorSaveParams["userPassword"], sametimeConnection.userPassword)
        assertEquals(connectorSaveParams["community"], sametimeConnection.community)
        assertEquals(sametimeConnection.id,sametimeConnector.ds.connection.id);
        assertEquals(1,sametimeConnection.sametimeDatasources.size());

        def sametimeDatasources = SametimeDatasource.list();
        assertEquals(1, sametimeDatasources.size());
        SametimeDatasource sametimeDatasource = sametimeDatasources[0];
        assertNotNull(createdObjects["sametimeDatasource"])
        assertEquals(sametimeDatasource.id,createdObjects["sametimeDatasource"].id)
        assertEquals(SametimeConnector.getSametimeDatasourceName(sametimeConnector.name),sametimeDatasource.name);
        assertEquals(sametimeDatasource.id, sametimeConnector.ds.id);
        assertEquals(sametimeConnection.id,sametimeDatasource.connection.id);
        assertEquals(0, sametimeDatasource.reconnectInterval);
        assertEquals(sametimeConnection.sametimeDatasources[0].id, sametimeDatasource.id);
    }

    void testAddRollsBackIfConnectorHasErrors() {

        def existingConnector=SametimeConnector.add(connectorSaveParams);
        assertFalse(existingConnector.hasErrors());

        def createdObjects = SametimeConnectorOperations.addConnector(connectorSaveParams)
        assertTrue(createdObjects.sametimeConnector.hasErrors())
        assertEquals(1, SametimeConnector.count())
        assertEquals(0, SametimeConnection.count())
        assertEquals(0, SametimeDatasource.count())
    }

    void testAddRollsBackIfConnectionHasErrors() {
        def existingConnection=Connection.add(name:SametimeConnectorOperations.getSametimeConnectionName(connectorSaveParams.name));
        assertFalse(existingConnection.hasErrors());

        def createdObjects = SametimeConnectorOperations.addConnector(connectorSaveParams)
        assertTrue(createdObjects.sametimeConnection.hasErrors())
        assertEquals(0, SametimeConnector.count())
        assertEquals(0, SametimeConnection.count())
        assertEquals(0, SametimeDatasource.count())


    }

    void testAddRollsBackIfDatasourceHasErrors() {
        def datasource = BaseDatasource.add(name: SametimeConnector.getSametimeDatasourceName(connectorSaveParams.name))
        assertFalse(datasource.hasErrors())

        def createdObjects = SametimeConnectorOperations.addConnector(connectorSaveParams)
        def connector = SametimeConnector.list()[0]
        assertEquals(0, SametimeConnector.count())
        assertEquals(0, SametimeConnection.count())
        assertEquals(0, SametimeDatasource.count())

        def ds = createdObjects.sametimeDatasource;
        assertTrue(ds.hasErrors())
    }



    public void testSuccessfulUpdate()
    {
        def createdObjects = SametimeConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, SametimeConnector.count());
        SametimeConnector oldSametimeConnector = SametimeConnector.get(name:connectorSaveParams.name);

        def updatedObjects = SametimeConnectorOperations.updateConnector(oldSametimeConnector, connectorUpdateParams);

        def sametimeConnectors = SametimeConnector.list();
        assertEquals(1, sametimeConnectors.size());
        SametimeConnector sametimeConnector = sametimeConnectors[0]
        assertNotNull(updatedObjects["sametimeConnector"])
        assertEquals(sametimeConnector.id,updatedObjects["sametimeConnector"].id)
        assertEquals(connectorUpdateParams.name, sametimeConnector.name);


        def sametimeConnections = SametimeConnection.list();
        assertEquals(1, sametimeConnections.size());
        def sametimeConnection = sametimeConnections[0];
        assertNotNull(updatedObjects["sametimeConnection"])
        assertEquals(sametimeConnection.id,updatedObjects["sametimeConnection"].id);
        assertEquals(SametimeConnector.getSametimeConnectionName(sametimeConnector.name),sametimeConnection.name);
        assertEquals(sametimeConnection.id, sametimeConnector.ds.connection.id);
        assertEquals(1,sametimeConnection.sametimeDatasources.size());
        assertEquals(connectorUpdateParams["host"], sametimeConnection.host)
        assertEquals(connectorUpdateParams["username"], sametimeConnection.username)
        assertEquals(connectorUpdateParams["userPassword"], sametimeConnection.userPassword)
        assertEquals(connectorUpdateParams["community"], sametimeConnection.community)


        def sametimeDatasources = SametimeDatasource.list();
        assertEquals(1, sametimeDatasources.size());
        SametimeDatasource sametimeDatasource = sametimeDatasources[0];
        assertNotNull(updatedObjects["sametimeDatasource"])
        assertEquals(sametimeDatasource.id,updatedObjects["sametimeDatasource"].id)
        assertEquals(SametimeConnector.getSametimeDatasourceName(sametimeConnector.name),sametimeDatasource.name);
        assertEquals(sametimeDatasource.id, sametimeConnector.ds.id);
        assertEquals(sametimeConnection.id,sametimeDatasource.connection.id);
        assertEquals(0, sametimeDatasource.reconnectInterval);
        assertEquals(sametimeConnection.sametimeDatasources[0].id, sametimeDatasource.id);
    }

    void testUpdateRollsbackIfConnectionHasErrors() {
        def createdObjects = SametimeConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, SametimeConnector.count())
        assertEquals(1, SametimeConnection.count())
        assertEquals(1, SametimeDatasource.count())

        def baseConn = Connection.add(name: connectorUpdateParams.name)
        assertFalse(baseConn.hasErrors())


        def updatedObjects = SametimeConnectorOperations.updateConnector(createdObjects["sametimeConnector"], connectorUpdateParams)

        def conn = updatedObjects.sametimeConnection;

        assertTrue(conn.hasErrors())
        assertEquals("default.not.unique.message", conn.errors.allErrors[0].code)

        assertEquals(1, SametimeConnector.count())
        assertEquals(1, SametimeConnection.count())
        assertEquals(1, SametimeDatasource.count())

        checkObjectsInRepoAreNotChanged();
    }

    void testUpdateRollsbackIfDatasourceHasErrors() {
        def createdObjects = SametimeConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, SametimeConnector.count())
        assertEquals(1, SametimeConnection.count())
        assertEquals(1, SametimeDatasource.count())

        def baseDs = BaseDatasource.add(name: SametimeConnector.getSametimeDatasourceName(connectorUpdateParams.name))
        assertFalse(baseDs.hasErrors())

        def updatedObjects = SametimeConnectorOperations.updateConnector(createdObjects["sametimeConnector"], connectorUpdateParams);
        def datasource = updatedObjects.sametimeDatasource;

        assertTrue(datasource.hasErrors())
        assertEquals("default.not.unique.message", datasource.errors.allErrors[0].code)

        assertEquals(1, SametimeConnector.count())
        assertEquals(1, SametimeConnection.count())
        assertEquals(1, SametimeDatasource.count())


        checkObjectsInRepoAreNotChanged();
    }

    void testUpdateRollsbackIfConnectorHasErrors() {
        def createdObjects = SametimeConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, SametimeConnector.count())
        assertEquals(1, SametimeConnection.count())
        assertEquals(1, SametimeDatasource.count())

        connectorUpdateParams["name"] = null;
        def updatedObjects = SametimeConnectorOperations.updateConnector(createdObjects["sametimeConnector"], connectorUpdateParams);


        assertTrue(updatedObjects.sametimeConnector.hasErrors())

        checkObjectsInRepoAreNotChanged();

    }
    private def checkObjectsInRepoAreNotChanged()
    {
        assertNull(SametimeConnector.get(name: connectorUpdateParams.name))
        def sametimeConnector = SametimeConnector.get(name: connectorSaveParams.name);
        assertNotNull(sametimeConnector)

        assertEquals(connectorSaveParams.host, sametimeConnector.ds.connection.host)
        assertEquals(SametimeConnector.getSametimeDatasourceName(connectorSaveParams.name), sametimeConnector.ds.name)

    }

     void testSuccessfullDelete() {
        def createdObjects = SametimeConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, SametimeConnector.count());
        assertEquals(1, SametimeConnection.count());
        assertEquals(1, SametimeDatasource.count());

        SametimeConnectorOperations.deleteConnector(createdObjects["sametimeConnector"])

        assertEquals(0, SametimeConnector.count());
        assertEquals(0, SametimeConnection.count());
        assertEquals(0, SametimeDatasource.count());
    }

}