package connector

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import datasource.SmsDatasource
import connection.SmsConnection
import com.ifountain.rcmdb.test.util.CompassForTests
import datasource.BaseDatasource
import connection.Connection

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Apr 9, 2009
* Time: 9:22:35 AM
*/
class SmsConnectorOperationsTest extends RapidCmdbWithCompassTestCase {
    def connectorSaveParams;
    def connectorUpdateParams;
    public void setUp() {
        super.setUp();
        initializeForAddAndUpdate();
        connectorSaveParams = [name: "testConnector", host: "smsHost", port: 5222,
                username: "testaccount", userPassword: "3600"];

        connectorUpdateParams = [name: "testConnector2", host: "smsHost2", port: 500,
                username: "newtestaccount", userPassword: "3601"]
    }

    public void tearDown() {
        super.tearDown();
    }

    def initializeForAddAndUpdate()
    {
        initialize([SmsConnector, SmsDatasource, SmsConnection, BaseDatasource, Connection], [], false);
        CompassForTests.addOperationSupport(SmsConnector, SmsConnectorOperations);
    }

    public void testSuccessfulSave()
    {
        def createdObjects = SmsConnectorOperations.addConnector(connectorSaveParams)

        def smsConnectors = SmsConnector.list();
        assertEquals(1, smsConnectors.size());
        SmsConnector smsConnector = smsConnectors[0]
        assertNotNull(createdObjects["smsConnector"])
        assertEquals(smsConnector.id,createdObjects["smsConnector"].id)
        assertEquals(connectorSaveParams.name, smsConnector.name);


        def smsConnections = SmsConnection.list();
        assertEquals(1, smsConnections.size());
        def smsConnection = smsConnections[0];
        assertNotNull(createdObjects["smsConnection"])
        assertEquals(smsConnection.id,createdObjects["smsConnection"].id);

        assertEquals(SmsConnector.getSmsConnectionName(smsConnector.name),smsConnection.name);
        assertEquals(connectorSaveParams["host"], smsConnection.host)
        assertEquals(connectorSaveParams["port"], smsConnection.port)
        assertEquals(connectorSaveParams["username"], smsConnection.username)
        assertEquals(connectorSaveParams["userPassword"], smsConnection.userPassword)
        assertEquals(smsConnection.id,smsConnector.ds.connection.id);
        assertEquals(1,smsConnection.smsDatasources.size());

        def smsDatasources = SmsDatasource.list();
        assertEquals(1, smsDatasources.size());
        SmsDatasource smsDatasource = smsDatasources[0];
        assertNotNull(createdObjects["smsDatasource"])
        assertEquals(smsDatasource.id,createdObjects["smsDatasource"].id)
        assertEquals(SmsConnector.getSmsDatasourceName(smsConnector.name),smsDatasource.name);
        assertEquals(smsDatasource.id, smsConnector.ds.id);
        assertEquals(smsConnection.id,smsDatasource.connection.id);
        assertEquals(0, smsDatasource.reconnectInterval);
        assertEquals(smsConnection.smsDatasources[0].id, smsDatasource.id);
    }

    void testAddRollsBackIfConnectorHasErrors() {

        def existingConnector=SmsConnector.add(connectorSaveParams);
        assertFalse(existingConnector.hasErrors());

        def createdObjects = SmsConnectorOperations.addConnector(connectorSaveParams)
        assertTrue(createdObjects.smsConnector.hasErrors())
        assertEquals(1, SmsConnector.count())
        assertEquals(0, SmsConnection.count())
        assertEquals(0, SmsDatasource.count())
    }

    void testAddRollsBackIfConnectionHasErrors() {
        def existingConnection=Connection.add(name:SmsConnectorOperations.getSmsConnectionName(connectorSaveParams.name));
        assertFalse(existingConnection.hasErrors());

        def createdObjects = SmsConnectorOperations.addConnector(connectorSaveParams)
        assertTrue(createdObjects.smsConnection.hasErrors())
        assertEquals(0, SmsConnector.count())
        assertEquals(0, SmsConnection.count())
        assertEquals(0, SmsDatasource.count())


    }

    void testAddRollsBackIfDatasourceHasErrors() {
        def datasource = BaseDatasource.add(name: SmsConnector.getSmsDatasourceName(connectorSaveParams.name))
        assertFalse(datasource.hasErrors())

        def createdObjects = SmsConnectorOperations.addConnector(connectorSaveParams)
        def connector = SmsConnector.list()[0]
        assertEquals(0, SmsConnector.count())
        assertEquals(0, SmsConnection.count())
        assertEquals(0, SmsDatasource.count())

        def ds = createdObjects.smsDatasource;
        assertTrue(ds.hasErrors())
    }



    public void testSuccessfulUpdate()
    {
        def createdObjects = SmsConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, SmsConnector.count());
        SmsConnector oldSmsConnector = SmsConnector.get(name:connectorSaveParams.name);

        def updatedObjects = SmsConnectorOperations.updateConnector(oldSmsConnector, connectorUpdateParams);

        def smsConnectors = SmsConnector.list();
        assertEquals(1, smsConnectors.size());
        SmsConnector smsConnector = smsConnectors[0]
        assertNotNull(updatedObjects["smsConnector"])
        assertEquals(smsConnector.id,updatedObjects["smsConnector"].id)
        assertEquals(connectorUpdateParams.name, smsConnector.name);


        def smsConnections = SmsConnection.list();
        assertEquals(1, smsConnections.size());
        def smsConnection = smsConnections[0];
        assertNotNull(updatedObjects["smsConnection"])
        assertEquals(smsConnection.id,updatedObjects["smsConnection"].id);
        assertEquals(SmsConnector.getSmsConnectionName(smsConnector.name),smsConnection.name);
        assertEquals(smsConnection.id, smsConnector.ds.connection.id);
        assertEquals(1,smsConnection.smsDatasources.size());
        assertEquals(connectorUpdateParams["host"], smsConnection.host)
        assertEquals(connectorUpdateParams["port"], smsConnection.port)
        assertEquals(connectorUpdateParams["username"], smsConnection.username)
        assertEquals(connectorUpdateParams["userPassword"], smsConnection.userPassword)


        def smsDatasources = SmsDatasource.list();
        assertEquals(1, smsDatasources.size());
        SmsDatasource smsDatasource = smsDatasources[0];
        assertNotNull(updatedObjects["smsDatasource"])
        assertEquals(smsDatasource.id,updatedObjects["smsDatasource"].id)
        assertEquals(SmsConnector.getSmsDatasourceName(smsConnector.name),smsDatasource.name);
        assertEquals(smsDatasource.id, smsConnector.ds.id);
        assertEquals(smsConnection.id,smsDatasource.connection.id);
        assertEquals(0, smsDatasource.reconnectInterval);
        assertEquals(smsConnection.smsDatasources[0].id, smsDatasource.id);
    }

    void testUpdateRollsbackIfConnectionHasErrors() {
        def createdObjects = SmsConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, SmsConnector.count())
        assertEquals(1, SmsConnection.count())
        assertEquals(1, SmsDatasource.count())

        def baseConn = Connection.add(name: connectorUpdateParams.name)
        assertFalse(baseConn.hasErrors())


        def updatedObjects = SmsConnectorOperations.updateConnector(createdObjects["smsConnector"], connectorUpdateParams)

        def conn = updatedObjects.smsConnection;

        assertTrue(conn.hasErrors())
        assertEquals("default.not.unique.message", conn.errors.allErrors[0].code)

        assertEquals(1, SmsConnector.count())
        assertEquals(1, SmsConnection.count())
        assertEquals(1, SmsDatasource.count())

        checkObjectsInRepoAreNotChanged();
    }

    void testUpdateRollsbackIfDatasourceHasErrors() {
        def createdObjects = SmsConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, SmsConnector.count())
        assertEquals(1, SmsConnection.count())
        assertEquals(1, SmsDatasource.count())

        def baseDs = BaseDatasource.add(name: SmsConnector.getSmsDatasourceName(connectorUpdateParams.name))
        assertFalse(baseDs.hasErrors())

        def updatedObjects = SmsConnectorOperations.updateConnector(createdObjects["smsConnector"], connectorUpdateParams);
        def datasource = updatedObjects.smsDatasource;

        assertTrue(datasource.hasErrors())
        assertEquals("default.not.unique.message", datasource.errors.allErrors[0].code)

        assertEquals(1, SmsConnector.count())
        assertEquals(1, SmsConnection.count())
        assertEquals(1, SmsDatasource.count())


        checkObjectsInRepoAreNotChanged();
    }

    void testUpdateRollsbackIfConnectorHasErrors() {
        def createdObjects = SmsConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, SmsConnector.count())
        assertEquals(1, SmsConnection.count())
        assertEquals(1, SmsDatasource.count())

        connectorUpdateParams["name"] = null;
        def updatedObjects = SmsConnectorOperations.updateConnector(createdObjects["smsConnector"], connectorUpdateParams);


        assertTrue(updatedObjects.smsConnector.hasErrors())

        checkObjectsInRepoAreNotChanged();

    }
    private def checkObjectsInRepoAreNotChanged()
    {
        assertNull(SmsConnector.get(name: connectorUpdateParams.name))
        def smsConnector = SmsConnector.get(name: connectorSaveParams.name);
        assertNotNull(smsConnector)

        assertEquals(connectorSaveParams.host, smsConnector.ds.connection.host)
        assertEquals(SmsConnector.getSmsDatasourceName(connectorSaveParams.name), smsConnector.ds.name)

    }

     void testSuccessfullDelete() {
        def createdObjects = SmsConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, SmsConnector.count());
        assertEquals(1, SmsConnection.count());
        assertEquals(1, SmsDatasource.count());

        SmsConnectorOperations.deleteConnector(createdObjects["smsConnector"])

        assertEquals(0, SmsConnector.count());
        assertEquals(0, SmsConnection.count());
        assertEquals(0, SmsDatasource.count());
    }

}