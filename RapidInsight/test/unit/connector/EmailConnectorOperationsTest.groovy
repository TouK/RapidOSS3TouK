package connector

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import datasource.EmailDatasource
import connection.EmailConnection
import com.ifountain.rcmdb.test.util.CompassForTests
import datasource.BaseDatasource
import connection.Connection

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Apr 9, 2009
* Time: 9:22:35 AM
*/
class EmailConnectorOperationsTest extends RapidCmdbWithCompassTestCase {
    def connectorSaveParams;
    def connectorUpdateParams;
    public void setUp() {
        super.setUp();
        initializeForAddAndUpdate();
        connectorSaveParams = [name: "testConnector", smtpHost: "192.168.1.100", smtpPort: 25,
                username: "testaccount", userPassword: "3600", protocol: EmailConnection.SMTP];

        connectorUpdateParams = [name: "testConnector2", smtpHost: "192.168.1.101", smtpPort: 40,
                username: "newtestaccount", userPassword: "3601", protocol: EmailConnection.SMTPS]
    }

    public void tearDown() {
        super.tearDown();
    }

    def initializeForAddAndUpdate()
    {
        initialize([EmailConnector, EmailDatasource, EmailConnection, BaseDatasource, Connection], [], false);
        CompassForTests.addOperationSupport(EmailConnector, EmailConnectorOperations);
    }

    public void testSuccessfulSave()
    {
        def createdObjects = EmailConnectorOperations.addConnector(connectorSaveParams)

        def emailConnectors = EmailConnector.list();
        assertEquals(1, emailConnectors.size());
        EmailConnector emailConnector = emailConnectors[0]
        assertNotNull(createdObjects["emailConnector"])
        assertEquals(emailConnector.id,createdObjects["emailConnector"].id)
        assertEquals(connectorSaveParams.name, emailConnector.name);


        def emailConnections = EmailConnection.list();
        assertEquals(1, emailConnections.size());
        def emailConnection = emailConnections[0];
        assertNotNull(createdObjects["emailConnection"])
        assertEquals(emailConnection.id,createdObjects["emailConnection"].id);

        assertEquals(EmailConnector.getEmailConnectionName(emailConnector.name),emailConnection.name);
        assertEquals(connectorSaveParams["smtpHost"], emailConnection.smtpHost)
        assertEquals(connectorSaveParams["smtpPort"], emailConnection.smtpPort)
        assertEquals(connectorSaveParams["username"], emailConnection.username)
        assertEquals(connectorSaveParams["userPassword"], emailConnection.userPassword)
        assertEquals(connectorSaveParams["protocol"], emailConnection.protocol)
        assertEquals(emailConnection.id,emailConnector.ds.connection.id);
        assertEquals(1,emailConnection.emailDatasources.size());

        def emailDatasources = EmailDatasource.list();
        assertEquals(1, emailDatasources.size());
        EmailDatasource emailDatasource = emailDatasources[0];
        assertNotNull(createdObjects["emailDatasource"])
        assertEquals(emailDatasource.id,createdObjects["emailDatasource"].id)
        assertEquals(EmailConnector.getEmailDatasourceName(emailConnector.name),emailDatasource.name);
        assertEquals(emailDatasource.id, emailConnector.ds.id);
        assertEquals(emailConnection.id,emailDatasource.connection.id);
        assertEquals(0, emailDatasource.reconnectInterval);
        assertEquals(emailConnection.emailDatasources[0].id, emailDatasource.id);
    }

    void testAddRollsBackIfConnectorHasErrors() {

        def existingConnector=EmailConnector.add(connectorSaveParams);
        assertFalse(existingConnector.hasErrors());

        def createdObjects = EmailConnectorOperations.addConnector(connectorSaveParams)
        assertTrue(createdObjects.emailConnector.hasErrors())
        assertEquals(1, EmailConnector.count())
        assertEquals(0, EmailConnection.count())
        assertEquals(0, EmailDatasource.count())
    }

    void testAddRollsBackIfConnectionHasErrors() {
        def existingConnection=Connection.add(name:EmailConnectorOperations.getEmailConnectionName(connectorSaveParams.name));
        assertFalse(existingConnection.hasErrors());

        def createdObjects = EmailConnectorOperations.addConnector(connectorSaveParams)
        assertTrue(createdObjects.emailConnection.hasErrors())
        assertEquals(0, EmailConnector.count())
        assertEquals(0, EmailConnection.count())
        assertEquals(0, EmailDatasource.count())


    }

    void testAddRollsBackIfDatasourceHasErrors() {
        def datasource = BaseDatasource.add(name: EmailConnector.getEmailDatasourceName(connectorSaveParams.name))
        assertFalse(datasource.hasErrors())

        def createdObjects = EmailConnectorOperations.addConnector(connectorSaveParams)
        def connector = EmailConnector.list()[0]
        assertEquals(0, EmailConnector.count())
        assertEquals(0, EmailConnection.count())
        assertEquals(0, EmailDatasource.count())

        def ds = createdObjects.emailDatasource;
        assertTrue(ds.hasErrors())
    }



    public void testSuccessfulUpdate()
    {
        def createdObjects = EmailConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, EmailConnector.count());
        EmailConnector oldEmailConnector = EmailConnector.get(name:connectorSaveParams.name);

        def updatedObjects = EmailConnectorOperations.updateConnector(oldEmailConnector, connectorUpdateParams);

        def emailConnectors = EmailConnector.list();
        assertEquals(1, emailConnectors.size());
        EmailConnector emailConnector = emailConnectors[0]
        assertNotNull(updatedObjects["emailConnector"])
        assertEquals(emailConnector.id,updatedObjects["emailConnector"].id)
        assertEquals(connectorUpdateParams.name, emailConnector.name);


        def emailConnections = EmailConnection.list();
        assertEquals(1, emailConnections.size());
        def emailConnection = emailConnections[0];
        assertNotNull(updatedObjects["emailConnection"])
        assertEquals(emailConnection.id,updatedObjects["emailConnection"].id);
        assertEquals(EmailConnector.getEmailConnectionName(emailConnector.name),emailConnection.name);
        assertEquals(emailConnection.id, emailConnector.ds.connection.id);
        assertEquals(1,emailConnection.emailDatasources.size());
        assertEquals(connectorUpdateParams["smtpHost"], emailConnection.smtpHost)
        assertEquals(connectorUpdateParams["smtpPort"], emailConnection.smtpPort)
        assertEquals(connectorUpdateParams["username"], emailConnection.username)
        assertEquals(connectorUpdateParams["userPassword"], emailConnection.userPassword)
        assertEquals(connectorUpdateParams["protocol"], emailConnection.protocol)


        def emailDatasources = EmailDatasource.list();
        assertEquals(1, emailDatasources.size());
        EmailDatasource emailDatasource = emailDatasources[0];
        assertNotNull(updatedObjects["emailDatasource"])
        assertEquals(emailDatasource.id,updatedObjects["emailDatasource"].id)
        assertEquals(EmailConnector.getEmailDatasourceName(emailConnector.name),emailDatasource.name);
        assertEquals(emailDatasource.id, emailConnector.ds.id);
        assertEquals(emailConnection.id,emailDatasource.connection.id);
        assertEquals(0, emailDatasource.reconnectInterval);
        assertEquals(emailConnection.emailDatasources[0].id, emailDatasource.id);
    }

    void testUpdateRollsbackIfConnectionHasErrors() {
        def createdObjects = EmailConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, EmailConnector.count())
        assertEquals(1, EmailConnection.count())
        assertEquals(1, EmailDatasource.count())

        def baseConn = Connection.add(name: connectorUpdateParams.name)
        assertFalse(baseConn.hasErrors())


        def updatedObjects = EmailConnectorOperations.updateConnector(createdObjects["emailConnector"], connectorUpdateParams)

        def conn = updatedObjects.emailConnection;

        assertTrue(conn.hasErrors())
        assertEquals("default.not.unique.message", conn.errors.allErrors[0].code)

        assertEquals(1, EmailConnector.count())
        assertEquals(1, EmailConnection.count())
        assertEquals(1, EmailDatasource.count())

        checkObjectsInRepoAreNotChanged();
    }

    void testUpdateRollsbackIfDatasourceHasErrors() {
        def createdObjects = EmailConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, EmailConnector.count())
        assertEquals(1, EmailConnection.count())
        assertEquals(1, EmailDatasource.count())

        def baseDs = BaseDatasource.add(name: EmailConnector.getEmailDatasourceName(connectorUpdateParams.name))
        assertFalse(baseDs.hasErrors())

        def updatedObjects = EmailConnectorOperations.updateConnector(createdObjects["emailConnector"], connectorUpdateParams);
        def datasource = updatedObjects.emailDatasource;

        assertTrue(datasource.hasErrors())
        assertEquals("default.not.unique.message", datasource.errors.allErrors[0].code)

        assertEquals(1, EmailConnector.count())
        assertEquals(1, EmailConnection.count())
        assertEquals(1, EmailDatasource.count())


        checkObjectsInRepoAreNotChanged();
    }

    void testUpdateRollsbackIfConnectorHasErrors() {
        def createdObjects = EmailConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, EmailConnector.count())
        assertEquals(1, EmailConnection.count())
        assertEquals(1, EmailDatasource.count())

        connectorUpdateParams["name"] = null;
        def updatedObjects = EmailConnectorOperations.updateConnector(createdObjects["emailConnector"], connectorUpdateParams);


        assertTrue(updatedObjects.emailConnector.hasErrors())

        checkObjectsInRepoAreNotChanged();

    }
    private def checkObjectsInRepoAreNotChanged()
    {
        assertNull(EmailConnector.get(name: connectorUpdateParams.name))
        def emailConnector = EmailConnector.get(name: connectorSaveParams.name);
        assertNotNull(emailConnector)

        assertEquals(connectorSaveParams.smtpHost, emailConnector.ds.connection.smtpHost)
        assertEquals(EmailConnector.getEmailDatasourceName(connectorSaveParams.name), emailConnector.ds.name)

    }

     void testSuccessfullDelete() {
        def createdObjects = EmailConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, EmailConnector.count());
        assertEquals(1, EmailConnection.count());
        assertEquals(1, EmailDatasource.count());

        EmailConnectorOperations.deleteConnector(createdObjects["emailConnector"])

        assertEquals(0, EmailConnector.count());
        assertEquals(0, EmailConnection.count());
        assertEquals(0, EmailDatasource.count());
    }

}