package connector

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import datasource.EmailDatasource
import connection.EmailConnection
import com.ifountain.rcmdb.test.util.CompassForTests
import datasource.BaseDatasource
import connection.Connection
import script.CmdbScript
import script.CmdbScriptOperations
import com.ifountain.rcmdb.scripting.ScriptManager
import com.ifountain.rcmdb.scripting.ScriptScheduler
import org.apache.log4j.Level
import message.RsMessageRule
import message.RsMessageRuleOperations
import application.RsApplication
import application.RsApplicationOperations
import auth.RsUser
import auth.RsUserOperations

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Dec 14, 2009
 * Time: 4:56:19 PM
 * To change this template use File | Settings | File Templates.
 */
class NotificationConnectorOperationsTest extends RapidCmdbWithCompassTestCase {
    def connectorSaveParams;
    def connectorUpdateParams;
    def type="Email";

    public void setUp() {
        super.setUp();
        initializeForAddAndUpdate();
        connectorSaveParams = [name: "testConnector", smtpHost: "192.168.1.100", smtpPort: 25,
                username: "testaccount", userPassword: "3600", protocol: EmailConnection.SMTP,logLevel:Level.INFO.toString(),scriptFile:"script1",type:type];

        connectorUpdateParams = [name: "testConnector2", smtpHost: "192.168.1.101", smtpPort: 40,showAsDestination:false,
                username: "newtestaccount", userPassword: "3601", protocol: EmailConnection.SMTPS,logLevel:Level.DEBUG.toString(),scriptFile:"script2",type:type]
    }

    public void tearDown() {
        super.tearDown();
    }
    public void clearMetaClasses()
    {
        ScriptManager.destroyInstance();
        ScriptScheduler.destroyInstance();
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(ScriptScheduler)
        GroovySystem.metaClassRegistry.removeMetaClass(ScriptManager)
        ExpandoMetaClass.enableGlobally();

    }
    def initializeForAddAndUpdate()
    {

        initialize([NotificationConnector, EmailDatasource, EmailConnection, BaseDatasource, Connection,CmdbScript,RsMessageRule], [], false);
        CompassForTests.addOperationSupport(NotificationConnector, NotificationConnectorOperations);
        CompassForTests.addOperationSupport(CmdbScript, CmdbScriptOperations);
        CompassForTests.addOperationSupport(RsMessageRule,RsMessageRuleOperations);
        CompassForTests.addOperationSupport(RsApplication,RsApplicationOperations);
        CompassForTests.addOperationSupport(RsUser,RsUserOperations);
        RsMessageRule.setConfiguredDestinationNames([]);




        initializeScriptManager();
    }
    void initializeScriptManager()
    {
        ScriptManager.metaClass.checkScript = {String script ->}
        ScriptManager.metaClass.addScript = {String script ->}
        ScriptManager.metaClass.removeScript = {String script ->}
        ScriptScheduler.metaClass.scheduleScript = {String scriptName, long startDelay, long period ->}
        ScriptScheduler.metaClass.unscheduleScript = {String scriptName ->}
        ScriptScheduler.metaClass.isScheduled = {String scriptName, long startDelay, long period ->}
        ScriptScheduler.metaClass.isScheduled = {String scriptName, long startDelay, String cronExp ->}
    }
    public void testGetDefaultScriptFile()
    {
        assertEquals("emailSender",NotificationConnectorOperations.getDefaultScriptFile("Email"));
        assertEquals("emailSender",NotificationConnectorOperations.getDefaultScriptFile("email"));
        assertEquals("jabberSender",NotificationConnectorOperations.getDefaultScriptFile("Jabber"));
        assertEquals("jJabberSender",NotificationConnectorOperations.getDefaultScriptFile("JJabber"));
    }
    public void testSuccessfulSave()
    {
        assertEquals([],RsMessageRule.getConfiguredDestinationNames());

        def createdObjects = NotificationConnectorOperations.addConnector(connectorSaveParams)
        assertEquals(4,createdObjects.size());

        def connectors = NotificationConnector.list();
        assertEquals(1, connectors.size());
        NotificationConnector connector = connectors[0]
        assertNotNull(createdObjects["connector"])
        assertEquals(connector.id,createdObjects["connector"].id)
        assertEquals(connectorSaveParams.name, connector.name);
        assertEquals(true,connector.showAsDestination);
        assertEquals(type,connector.type);

        def connections = EmailConnection.list();
        assertEquals(1, connections.size());
        def connection = connections[0];
        assertNotNull(createdObjects["connection"])
        assertEquals(connection.id,createdObjects["connection"].id);

        assertEquals(NotificationConnector.getConnectionName(connector.name),connection.name);
        assertEquals(connectorSaveParams["smtpHost"], connection.smtpHost)
        assertEquals(connectorSaveParams["smtpPort"], connection.smtpPort)
        assertEquals(connectorSaveParams["username"], connection.username)
        assertEquals(connectorSaveParams["userPassword"], connection.userPassword)
        assertEquals(connectorSaveParams["protocol"], connection.protocol)
        assertEquals(connection.id,connector.ds.connection.id);
        assertEquals(1,connection.emailDatasources.size());

        def datasources = EmailDatasource.list();
        assertEquals(1, datasources.size());
        EmailDatasource datasource = datasources[0];
        assertNotNull(createdObjects["datasource"])
        assertEquals(datasource.id,createdObjects["datasource"].id)
        assertEquals(NotificationConnector.getDatasourceName(connector.name),datasource.name);
        assertEquals(datasource.id, connector.ds.id);
        assertEquals(connection.id,datasource.connection.id);
        assertEquals(0, datasource.reconnectInterval);
        assertEquals(connection.emailDatasources[0].id, datasource.id);

        def scripts=CmdbScript.list();
        assertEquals(1, scripts.size());
        CmdbScript script = scripts[0];
        assertNotNull(createdObjects["script"])
        assertEquals(script.id,createdObjects["script"].id)
        assertEquals(NotificationConnector.getScriptName(connector.name),script.name);
        assertEquals(CmdbScript.SCHEDULED, script.type);
        assertEquals(60, script.period);
        assertEquals(CmdbScript.PERIODIC, script.scheduleType);
        assertEquals(connectorSaveParams.scriptFile, script.scriptFile);
        assertEquals(connectorSaveParams.logLevel, script.logLevel);
        assertEquals(false, script.enabled);
        assertEquals(true, script.logFileOwn);
        
        assertEquals([connectorSaveParams.name],RsMessageRule.getConfiguredDestinationNames());

    }


    void testAddRollsBackIfConnectorHasErrors() {

        def existingConnector=NotificationConnector.add(connectorSaveParams);
        assertFalse(existingConnector.hasErrors());

        def createdObjects =NotificationConnectorOperations.addConnector(connectorSaveParams)
        assertTrue(createdObjects.connector.hasErrors())
        assertEquals(4,createdObjects.size());

        assertEquals(1, NotificationConnector.count())
        assertEquals(0, EmailConnection.count())
        assertEquals(0, EmailDatasource.count())
        assertEquals(0, CmdbScript.count())
        
        assertEquals([connectorSaveParams.name],RsMessageRule.getConfiguredDestinationNames());
    }

    void testAddRollsBackIfConnectionHasErrors() {
        def existingConnection=Connection.add(name:NotificationConnectorOperations.getConnectionName(connectorSaveParams.name));
        assertFalse(existingConnection.hasErrors());


        def createdObjects = NotificationConnectorOperations.addConnector(connectorSaveParams)
        assertTrue(createdObjects.connection.hasErrors())
        assertEquals(4,createdObjects.size());

        assertEquals(1, Connection.count())
        assertEquals(0, NotificationConnector.count())
        assertEquals(0, EmailConnection.count())
        assertEquals(0, EmailDatasource.count())
        assertEquals(0, CmdbScript.count())

        assertEquals([],RsMessageRule.getConfiguredDestinationNames());


    }

    void testAddRollsBackIfDatasourceHasErrors() {
        def datasource = BaseDatasource.add(name: NotificationConnector.getDatasourceName(connectorSaveParams.name))
        assertFalse(datasource.hasErrors())

        def createdObjects = NotificationConnectorOperations.addConnector(connectorSaveParams)
        assertTrue(createdObjects.datasource.hasErrors())
        assertEquals(4,createdObjects.size());

        assertEquals(1, BaseDatasource.count())
        assertEquals(0, NotificationConnector.count())
        assertEquals(0, EmailConnection.count())
        assertEquals(0, EmailDatasource.count())
        assertEquals(0, CmdbScript.count())

        assertEquals([],RsMessageRule.getConfiguredDestinationNames());

    }

    void testAddRollsBackIfScriptHasErrors() {
        def script = CmdbScript.add(name: NotificationConnectorOperations.getScriptName(connectorSaveParams.name),scriptFile:"dummy")
        assertFalse(script.hasErrors())

        def createdObjects = NotificationConnectorOperations.addConnector(connectorSaveParams)
        assertTrue(createdObjects.script.hasErrors())
        assertEquals(4,createdObjects.size());

        assertEquals(0, NotificationConnector.count())
        assertEquals(0, EmailConnection.count())
        assertEquals(0, EmailDatasource.count())
        assertEquals(1, CmdbScript.count())

        assertEquals([],RsMessageRule.getConfiguredDestinationNames());
    }


    public void testSuccessfulUpdate()
    {

        assertEquals([],RsMessageRule.getConfiguredDestinationNames());

        def createdObjects = NotificationConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, NotificationConnector.count());
        NotificationConnector oldConnector = NotificationConnector.get(name:connectorSaveParams.name);

        assertEquals([oldConnector.name],RsMessageRule.getConfiguredDestinationNames());

        def updatedObjects = NotificationConnectorOperations.updateConnector(oldConnector, connectorUpdateParams);
        assertEquals(4,updatedObjects.size());

        def connectors = oldConnector.list();
        assertEquals(1, connectors.size());
        NotificationConnector connector = connectors[0]
        assertNotNull(updatedObjects["connector"])
        assertEquals(connector.id,updatedObjects["connector"].id)
        assertEquals(connectorUpdateParams.name, connector.name);
        assertEquals(false,connector.showAsDestination);


        def connections = EmailConnection.list();
        assertEquals(1, connections.size());
        def connection = connections[0];
        assertNotNull(updatedObjects["connection"])
        assertEquals(connection.id,updatedObjects["connection"].id);
        assertEquals(NotificationConnector.getConnectionName(connector.name),connection.name);
        assertEquals(connection.id, connector.ds.connection.id);
        assertEquals(1,connection.emailDatasources.size());
        assertEquals(connectorUpdateParams["smtpHost"], connection.smtpHost)
        assertEquals(connectorUpdateParams["smtpPort"], connection.smtpPort)
        assertEquals(connectorUpdateParams["username"], connection.username)
        assertEquals(connectorUpdateParams["userPassword"], connection.userPassword)
        assertEquals(connectorUpdateParams["protocol"], connection.protocol)


        def datasources = EmailDatasource.list();
        assertEquals(1, datasources.size());
        EmailDatasource datasource = datasources[0];
        assertNotNull(updatedObjects["datasource"])
        assertEquals(datasource.id,updatedObjects["datasource"].id)
        assertEquals(NotificationConnector.getDatasourceName(connector.name),datasource.name);
        assertEquals(datasource.id, connector.ds.id);
        assertEquals(connection.id,datasource.connection.id);
        assertEquals(0, datasource.reconnectInterval);
        assertEquals(connection.emailDatasources[0].id, datasource.id);

        def scripts=CmdbScript.list();
        assertEquals(1, scripts.size());
        CmdbScript script = scripts[0];
        assertNotNull(updatedObjects["script"])
        assertEquals(script.id,updatedObjects["script"].id)
        assertEquals(NotificationConnector.getScriptName(connector.name),script.name);
        assertEquals(CmdbScript.SCHEDULED, script.type);
        assertEquals(60, script.period);
        assertEquals(CmdbScript.PERIODIC, script.scheduleType);
        assertEquals(connectorUpdateParams.scriptFile, script.scriptFile);
        assertEquals(connectorUpdateParams.logLevel, script.logLevel);
        assertEquals(false, script.enabled);
        assertEquals(true, script.logFileOwn);

        assertEquals([],RsMessageRule.getConfiguredDestinationNames());
    }


    void testUpdateRollsbackIfConnectionHasErrors() {
        def createdObjects = NotificationConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, NotificationConnector.count())
        assertEquals(1, EmailConnection.count())
        assertEquals(1, EmailDatasource.count())
        assertEquals(1, CmdbScript.count())

        def baseConn = Connection.add(name: connectorUpdateParams.name)
        assertFalse(baseConn.hasErrors())


        def updatedObjects = NotificationConnectorOperations.updateConnector(createdObjects["connector"], connectorUpdateParams)

        def conn = updatedObjects.connection;

        assertTrue(conn.hasErrors())
        assertEquals("default.not.unique.message", conn.errors.allErrors[0].code)

        assertEquals(1, NotificationConnector.count())
        assertEquals(1, EmailConnection.count())
        assertEquals(1, EmailDatasource.count())
        assertEquals(1, CmdbScript.count())

        checkObjectsInRepoAreNotChanged();

    }

    void testUpdateRollsbackIfDatasourceHasErrors() {
        def createdObjects = NotificationConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, NotificationConnector.count())
        assertEquals(1, EmailConnection.count())
        assertEquals(1, EmailDatasource.count())
        assertEquals(1, CmdbScript.count())

        def baseDs = BaseDatasource.add(name: NotificationConnector.getDatasourceName(connectorUpdateParams.name))
        assertFalse(baseDs.hasErrors())

        def updatedObjects = NotificationConnectorOperations.updateConnector(createdObjects["connector"], connectorUpdateParams);
        def datasource = updatedObjects.datasource;

        assertTrue(datasource.hasErrors())
        assertEquals("default.not.unique.message", datasource.errors.allErrors[0].code)

        assertEquals(1, NotificationConnector.count())
        assertEquals(1, EmailConnection.count())
        assertEquals(1, EmailDatasource.count())


        checkObjectsInRepoAreNotChanged();
    }
    void testUpdateRollsbackIfScriptHasErrors() {
        def createdObjects = NotificationConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, NotificationConnector.count())
        assertEquals(1, EmailConnection.count())
        assertEquals(1, EmailDatasource.count())
        assertEquals(1, CmdbScript.count())

        connectorUpdateParams.scriptFile=null;

        def updatedObjects = NotificationConnectorOperations.updateConnector(createdObjects["connector"], connectorUpdateParams);
        def script = updatedObjects.script;

        assertTrue(script.hasErrors())
        assertEquals("nullable", script.errors.allErrors[0].code)

        assertEquals(1, NotificationConnector.count())
        assertEquals(1, EmailConnection.count())
        assertEquals(1, EmailDatasource.count())
        assertEquals(1, CmdbScript.count())


        checkObjectsInRepoAreNotChanged();
    }

    void testUpdateRollsbackIfConnectorHasErrors() {
        def createdObjects = NotificationConnectorOperations.addConnector(connectorSaveParams)

        assertEquals(1, NotificationConnector.count())
        assertEquals(1, EmailConnection.count())
        assertEquals(1, EmailDatasource.count())
        assertEquals(1, CmdbScript.count())

        connectorUpdateParams["name"] = null;
        def updatedObjects = NotificationConnectorOperations.updateConnector(createdObjects["connector"], connectorUpdateParams);


        assertTrue(updatedObjects.connector.hasErrors())

        checkObjectsInRepoAreNotChanged();

    }
    private def checkObjectsInRepoAreNotChanged()
    {
        assertNull(NotificationConnector.get(name: connectorUpdateParams.name))
        def connector = NotificationConnector.get(name: connectorSaveParams.name);
        assertNotNull(connector)

        assertEquals(connectorSaveParams.smtpHost, connector.ds.connection.smtpHost)
        assertEquals(NotificationConnector.getDatasourceName(connectorSaveParams.name), connector.ds.name)
        assertEquals(NotificationConnector.getScriptName(connectorSaveParams.name),CmdbScript.list()[0].name)

        assertEquals([connectorSaveParams.name],RsMessageRule.getConfiguredDestinationNames());

    }

     void testSuccessfullDelete() {
        def createdObjects = NotificationConnectorOperations.addConnector(connectorSaveParams)


        assertEquals(1, NotificationConnector.count());
        assertEquals(1, EmailConnection.count());
        assertEquals(1, EmailDatasource.count());
        assertEquals(1, CmdbScript.count());

        assertEquals([connectorSaveParams.name],RsMessageRule.getConfiguredDestinationNames());

        NotificationConnectorOperations.deleteConnector(createdObjects["connector"])

        assertEquals(0, NotificationConnector.count());
        assertEquals(0, EmailConnection.count());
        assertEquals(0, EmailDatasource.count());
        assertEquals(0, CmdbScript.count());

        assertEquals([],RsMessageRule.getConfiguredDestinationNames());
    }

}