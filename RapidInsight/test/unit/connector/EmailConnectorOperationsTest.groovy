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
        assertEquals(createdObjects["emailConnector"].id, emailConnector.id)
        assertEquals(connectorSaveParams.name, emailConnector.name);


        def emailConnections = EmailConnection.list();
        assertEquals(1, emailConnections.size());
        def emailConnection = emailConnections[0];
        assertNotNull(createdObjects["emailConnection"])
        assertEquals(createdObjects["emailConnection"].id, emailConnection.id)
        assertEquals(emailConnection.name, EmailConnector.getEmailConnectionName(emailConnector.name));
        assertEquals(connectorSaveParams["smtpHost"], emailConnection.smtpHost)
        assertEquals(connectorSaveParams["smtpPort"], emailConnection.smtpPort)
        assertEquals(connectorSaveParams["username"], emailConnection.username)
        assertEquals(connectorSaveParams["userPassword"], emailConnection.userPassword)
        assertEquals(connectorSaveParams["protocol"], emailConnection.protocol)
        assertEquals(emailConnection.id, emailConnector.emailConnection.id);
        assertEquals(emailConnection.emailDatasources.size(), 1);

        def emailDatasources = EmailDatasource.list();
        assertEquals(1, emailDatasources.size());
        EmailDatasource emailDatasource = emailDatasources[0];
        assertNotNull(createdObjects["emailDatasource"])
        assertEquals(createdObjects["emailDatasource"].id, emailDatasource.id)
        assertEquals(emailDatasource.name, EmailConnector.getEmailDatasourceName(emailConnector.name));
        assertEquals(emailDatasource.id, emailConnector.emailDatasource.id);
        assertEquals(emailDatasource.connection.id, emailConnection.id);
        assertEquals(0, emailDatasource.reconnectInterval);
        assertEquals(emailConnection.emailDatasources[0].id, emailDatasource.id);
    }

    void testIfConnectorHasErrorsNothingIsAdded() {

        def existingConnector=EmailConnector.add(connectorSaveParams);
        assertFalse(existingConnector.hasErrors());

        def createdObjects = EmailConnectorOperations.addConnector(connectorSaveParams)
        assertTrue(createdObjects.emailConnector.hasErrors())
        assertEquals(1, EmailConnector.list().size())
        assertEquals(0, EmailConnection.list().size())
        assertEquals(0, EmailDatasource.list().size())
    }

    void testIfConnectionHasErrorsConnectorIsNotAdded() {
        def connectionSaveParams=[:];
        connectionSaveParams.name=EmailConnectorOperations.getEmailConnectionName(connectorSaveParams.name);
        connectionSaveParams.smtpHost=connectorSaveParams.smtpHost;
        connectionSaveParams.smtpPort=connectorSaveParams.smtpPort;
        def existingConnection=EmailConnection.add(connectionSaveParams);
        assertFalse(existingConnection.hasErrors());

        def createdObjects = EmailConnectorOperations.addConnector(connectorSaveParams)
        assertTrue(createdObjects.emailConnection.hasErrors())
        assertEquals(0, EmailConnector.list().size())
        assertEquals(1, EmailConnection.list().size())
        assertEquals(0, EmailDatasource.list().size())


    }

    void testIfDatasourceHasErrorsConnectorIsNotAdded() {
        def datasource = BaseDatasource.add(name: EmailConnector.getEmailDatasourceName(connectorSaveParams.name))
        assertFalse(datasource.hasErrors())

        def createdObjects = EmailConnectorOperations.addConnector(connectorSaveParams)
        def connector = EmailConnector.list()[0]
        assertEquals(0, EmailConnector.list().size())
        assertEquals(0, EmailConnection.list().size())
        assertEquals(0, EmailDatasource.list().size())

        def ds = createdObjects.emailDatasource;
        assertTrue(ds.hasErrors())
    }

    void testSuccessfullDelete() {
        def createdObjects = EmailConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, EmailConnector.list().size());
        assertEquals(1, EmailConnection.list().size());
        assertEquals(1, EmailDatasource.list().size());

        EmailConnectorOperations.deleteConnector(createdObjects["emailConnector"])

        assertEquals(0, EmailConnector.list().size());
        assertEquals(0, EmailConnection.list().size());
        assertEquals(0, EmailDatasource.list().size());
    }

    public void testSuccessfulUpdate()
    {
        def createdObjects = EmailConnectorOperations.addConnector(connectorSaveParams)

        def oldEmailConnectors = EmailConnector.list();
        assertEquals(1, oldEmailConnectors.size());
        EmailConnector oldEmailConnector = oldEmailConnectors[0]
        assertEquals(connectorSaveParams.name, oldEmailConnector.name);

        def updatedObjects = EmailConnectorOperations.updateConnector(oldEmailConnector, connectorUpdateParams);

        def emailConnectors = EmailConnector.list();
        assertEquals(1, emailConnectors.size());
        EmailConnector emailConnector = emailConnectors[0]
        assertNotNull(updatedObjects["emailConnector"])
        assertEquals(updatedObjects["emailConnector"].id, emailConnector.id)
        assertEquals(connectorUpdateParams.name, emailConnector.name);


        def emailConnections = EmailConnection.list();
        assertEquals(1, emailConnections.size());
        def emailConnection = emailConnections[0];
        assertNotNull(updatedObjects["emailConnection"])
        assertEquals(updatedObjects["emailConnection"].id, emailConnection.id)
        assertEquals(emailConnection.name, EmailConnector.getEmailConnectionName(emailConnector.name));
        assertEquals(emailConnection.id, emailConnector.emailConnection.id);
        assertEquals(emailConnection.emailDatasources.size(), 1);
        assertEquals(connectorUpdateParams["smtpHost"], emailConnection.smtpHost)
        assertEquals(connectorUpdateParams["smtpPort"], emailConnection.smtpPort)
        assertEquals(connectorUpdateParams["username"], emailConnection.username)
        assertEquals(connectorUpdateParams["userPassword"], emailConnection.userPassword)
        assertEquals(connectorUpdateParams["protocol"], emailConnection.protocol)


        def emailDatasources = EmailDatasource.list();
        assertEquals(1, emailDatasources.size());
        EmailDatasource emailDatasource = emailDatasources[0];
        assertNotNull(updatedObjects["emailDatasource"])
        assertEquals(updatedObjects["emailDatasource"].id, emailDatasource.id)
        assertEquals(emailDatasource.name, EmailConnector.getEmailDatasourceName(emailConnector.name));
        assertEquals(emailDatasource.id, emailConnector.emailDatasource.id);
        assertEquals(emailDatasource.connection.id, emailConnection.id);
        assertEquals(0, emailDatasource.reconnectInterval);
        assertEquals(emailConnection.emailDatasources[0].id, emailDatasource.id);
    }

    void testIfConnectionUpdateFailsUpdateIsRollbacked() {
        def createdObjects = EmailConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, EmailConnector.list().size())
        assertEquals(1, EmailConnection.list().size())
        assertEquals(1, EmailDatasource.list().size())

        def baseConn = Connection.add(name: connectorUpdateParams.name)
        assertFalse(baseConn.hasErrors())


        def updatedObjects = EmailConnectorOperations.updateConnector(createdObjects["emailConnector"], connectorUpdateParams)

        def conn = updatedObjects.emailConnection;

        assertTrue(conn.hasErrors())
        assertEquals("default.not.unique.message", conn.errors.allErrors[0].code)

        assertEquals(1, EmailConnector.list().size())
        assertEquals(1, EmailConnection.list().size())
        assertEquals(1, EmailDatasource.list().size())


        assertNull(EmailConnector.get(name: connectorUpdateParams.name))
        def emailConnector = EmailConnector.get(name: connectorSaveParams.name);
        assertNotNull(emailConnector)
        assertEquals(connectorSaveParams.smtpHost, emailConnector.emailConnection.smtpHost)
    }

    void testIfDatasourceUpdateFailsUpdateIsRollbacked() {
        def createdObjects = EmailConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, EmailConnector.list().size())
        assertEquals(1, EmailConnection.list().size())
        assertEquals(1, EmailDatasource.list().size())

        def baseDs = BaseDatasource.add(name: EmailConnector.getEmailDatasourceName(connectorUpdateParams.name))
        assertFalse(baseDs.hasErrors())

        def updatedObjects = EmailConnectorOperations.updateConnector(createdObjects["emailConnector"], connectorUpdateParams);
        def datasource = updatedObjects.emailDatasource;

        assertTrue(datasource.hasErrors())
        assertEquals("default.not.unique.message", datasource.errors.allErrors[0].code)

        assertEquals(1, EmailConnector.list().size())
        assertEquals(1, EmailConnection.list().size())
        assertEquals(1, EmailDatasource.list().size())


        assertNull(EmailConnector.get(name: connectorUpdateParams.name))
        def emailConnector = EmailConnector.get(name: connectorSaveParams.name);
        assertNotNull(emailConnector)
        assertEquals(connectorSaveParams.smtpHost, emailConnector.emailConnection.smtpHost)
        assertEquals(EmailConnector.getEmailDatasourceName(connectorSaveParams.name), emailConnector.emailDatasource.name)
    }

    void testIfConnectorUpdateFailsNothingIsUpdated() {
        def createdObjects = EmailConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, EmailConnector.list().size())
        assertEquals(1, EmailConnection.list().size())
        assertEquals(1, EmailDatasource.list().size())

        connectorUpdateParams["name"] = null;
        def updatedObjects = EmailConnectorOperations.updateConnector(createdObjects["emailConnector"], connectorUpdateParams);

        def emailConnector = updatedObjects.emailConnector;
        assertTrue(emailConnector.hasErrors())

        emailConnector = EmailConnector.list()[0]
        def emailConnection = emailConnector.emailConnection;
        assertEquals(emailConnector.name, connectorSaveParams.name)
        assertEquals(emailConnection.name, EmailConnector.getEmailConnectionName(emailConnector.name));
        assertEquals(emailConnector.emailDatasource.name, EmailConnector.getEmailDatasourceName(emailConnector.name));

        assertEquals(connectorSaveParams["smtpHost"], emailConnection.smtpHost)
        assertEquals(connectorSaveParams["smtpPort"], emailConnection.smtpPort)
        assertEquals(connectorSaveParams["username"], emailConnection.username)
        assertEquals(connectorSaveParams["userPassword"], emailConnection.userPassword)
        assertEquals(connectorSaveParams["protocol"], emailConnection.protocol)

    }

}