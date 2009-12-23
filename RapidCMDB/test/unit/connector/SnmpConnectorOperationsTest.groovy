package connector

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import datasource.BaseListeningDatasource
import org.apache.log4j.Level
import connection.SnmpConnection
import datasource.SnmpDatasource
import datasource.SnmpDatasourceOperations
import script.CmdbScript
import com.ifountain.rcmdb.scripting.ScriptManager
import com.ifountain.rcmdb.datasource.ListeningAdapterManager
import com.ifountain.rcmdb.scripting.ScriptScheduler
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.exception.MessageSourceException

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Apr 8, 2009
* Time: 2:42:41 PM
*/
class SnmpConnectorOperationsTest extends RapidCmdbWithCompassTestCase {

    def connectorSaveParams;
    def connectorUpdateParams;
    public void setUp() {
        super.setUp();
        clearMetaClasses();
        initializeForAddAndUpdate();
        connectorSaveParams = [name: "topo", host: "0.0.0.0", port: 162, logLevel: Level.WARN.toString(), scriptFile: "sampleFile1"];
        connectorUpdateParams = [name: "topo", host: "192.168.1.1", port: 50, logLevel: Level.DEBUG.toString(), scriptFile: "sampleFile2"];
    }

    public void tearDown() {
        clearMetaClasses();
        super.tearDown();
    }

    public void clearMetaClasses()
    {
        ScriptManager.destroyInstance();
        ListeningAdapterManager.destroyInstance();
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(CmdbScript)
        GroovySystem.metaClassRegistry.removeMetaClass(ScriptManager)
        GroovySystem.metaClassRegistry.removeMetaClass(ScriptScheduler)
        GroovySystem.metaClassRegistry.removeMetaClass(ListeningAdapterManager)
        ExpandoMetaClass.enableGlobally();
    }

    def initializeForAddAndUpdate()
    {
        initialize([SnmpConnector, SnmpConnection, CmdbScript, BaseListeningDatasource, SnmpDatasource], [], false);
        CmdbScript.metaClass.'static'.runScript = {String scriptName, Map params ->}

        ScriptManager.metaClass.checkScript = {String script ->}
        ScriptManager.metaClass.addScript = {String script ->}
        ScriptManager.metaClass.removeScript = {String script ->}
        ScriptScheduler.metaClass.scheduleScript = {String scriptName, long startDelay, long period ->}
        ScriptScheduler.metaClass.unscheduleScript = {String scriptName ->}
        ScriptScheduler.metaClass.isScheduled = {String scriptName, long startDelay, long period ->}
        ScriptScheduler.metaClass.isScheduled = {String scriptName, long startDelay, String cronExp ->}

        ListeningAdapterManager.metaClass.startAdapter = {BaseListeningDatasource listeningDatasource ->}
        ListeningAdapterManager.metaClass.stopAdapter = {BaseListeningDatasource listeningDatasource ->}
        ListeningAdapterManager.metaClass.isFree = {BaseListeningDatasource listeningDatasource -> return true;}

        CompassForTests.addOperationSupport(CmdbScript, script.CmdbScriptOperations);
        CompassForTests.addOperationSupport(SnmpDatasource, SnmpDatasourceOperations);
    }

    public void testSuccessfulSave()
    {
        def createdObjects = SnmpConnectorOperations.addConnector(connectorSaveParams);

        List snmpConnectors = SnmpConnector.list();
        assertEquals(1, snmpConnectors.size());
        SnmpConnector snmpConnector = snmpConnectors[0]
        assertNotNull(createdObjects["snmpConnector"])
        assertEquals(createdObjects["snmpConnector"].id, snmpConnector.id)
        assertEquals(connectorSaveParams.name, snmpConnector.name);

        List snmpConnections = SnmpConnection.list();
        assertEquals(1, snmpConnections.size());
        SnmpConnection snmpConnection = snmpConnections[0]
        assertNotNull(createdObjects["snmpConnection"])
        assertEquals(createdObjects["snmpConnection"].id, snmpConnection.id)
        assertEquals(snmpConnector.getConnectionName(snmpConnector.name), snmpConnection.name);
        assertEquals(connectorSaveParams.host, snmpConnection.host);
        assertEquals(connectorSaveParams.port.toInteger(), snmpConnection.port);
        assertEquals(snmpConnector.connection.id, snmpConnection.id);


        List snmpDatasources = SnmpDatasource.list();
        assertEquals(1, snmpDatasources.size());
        SnmpDatasource snmpDatasource = snmpDatasources[0]
        assertNotNull(createdObjects["datasource"])
        assertEquals(createdObjects["datasource"].id, snmpDatasource.id)
        assertEquals(snmpConnector.getDatasourceName(snmpConnector.name), snmpDatasource.name);
        assertEquals(snmpConnection.id, snmpDatasource.connection.id);

        def scripts = CmdbScript.list();
        assertEquals(1, scripts.size());
        CmdbScript script = scripts[0]
        assertNotNull(createdObjects["script"])
        assertEquals(createdObjects["script"].id, script.id)
        assertEquals(connectorSaveParams["name"], script.name);
        assertEquals(connectorSaveParams.scriptFile, script.scriptFile);
        assertEquals(connectorSaveParams.logLevel, script.logLevel);
        assertEquals(true, script.logFileOwn);
        assertEquals(CmdbScript.LISTENING, script.type);
        assertEquals("", script.staticParam);
        assertEquals(snmpConnector.script.id, script.id);
    }

    public void testSaveRollsBackIfConnectorHasErrors()
    {
        def existingConnector = SnmpConnector.add(name: connectorSaveParams["name"]);
        assertFalse(existingConnector.hasErrors());

        def createdObjects = SnmpConnectorOperations.addConnector(connectorSaveParams);

        assertTrue(createdObjects.snmpConnector.hasErrors());
        assertNull(createdObjects.snmpConnection.id);
        assertNull(createdObjects.script.id);
        assertNull(createdObjects.datasource.id);

        existingConnector.remove();

        assertEquals(0, SnmpConnector.count());
        assertEquals(0, SnmpConnection.count());
        assertEquals(0, SnmpDatasource.count());
        assertEquals(0, CmdbScript.count());
    }

    public void testSaveRollsBackIfConnectionHasErrors()
    {
        def existingConnection = SnmpConnection.add(name: SnmpConnector.getConnectionName(connectorSaveParams["name"]));
        assertFalse(existingConnection.hasErrors());

        def createdObjects = SnmpConnectorOperations.addConnector(connectorSaveParams);

        assertFalse(createdObjects.snmpConnector.hasErrors());
        assertTrue(createdObjects.snmpConnection.hasErrors());
        assertNull(createdObjects.script.id);
        assertNull(createdObjects.datasource.id);

        existingConnection.remove();

        assertEquals(0, SnmpConnector.count());
        assertEquals(0, SnmpConnection.count());
        assertEquals(0, SnmpDatasource.count());
        assertEquals(0, CmdbScript.count());
    }

    public void testSaveRollsBackIfScriptHasErrors()
    {
        def existingScript = CmdbScript.addScript(name: connectorSaveParams["name"]);
        assertFalse(existingScript.hasErrors());

        def createdObjects = SnmpConnectorOperations.addConnector(connectorSaveParams);

        assertFalse(createdObjects.snmpConnector.hasErrors());
        assertFalse(createdObjects.snmpConnection.hasErrors());
        assertTrue(createdObjects.script.hasErrors());
        assertNull(createdObjects.datasource.id);

        existingScript.remove();

        assertEquals(0, SnmpConnector.count());
        assertEquals(0, SnmpConnection.count());
        assertEquals(0, SnmpDatasource.count());
        assertEquals(0, CmdbScript.count());
    }

    public void testSaveRollsBackIfDatasourceHasErrors()
    {
        def connectionForDs = SnmpConnection.add(name: "testcon");
        assertFalse(connectionForDs.hasErrors());
        def existingDatasource = SnmpDatasource.add(name: SnmpConnector.getDatasourceName(connectorSaveParams["name"]), connection: connectionForDs);
        assertFalse(existingDatasource.hasErrors());

        def createdObjects = SnmpConnectorOperations.addConnector(connectorSaveParams);

        assertFalse(createdObjects.snmpConnector.hasErrors());
        assertFalse(createdObjects.snmpConnection.hasErrors());
        assertFalse(createdObjects.script.hasErrors());
        assertTrue(createdObjects.datasource.hasErrors());

        connectionForDs.remove();
        existingDatasource.remove();

        assertEquals(0, SnmpConnector.count());
        assertEquals(0, SnmpConnection.count());
        assertEquals(0, SnmpDatasource.count());
        assertEquals(0, CmdbScript.count());
    }

    public void testSuccessfulUpdate()
    {
        def createdObjects = SnmpConnectorOperations.addConnector(connectorSaveParams);

        List oldSnmpConnectors = SnmpConnector.list();
        assertEquals(1, oldSnmpConnectors.size());
        SnmpConnector oldSnmpConnector = oldSnmpConnectors[0]
        assertEquals(connectorSaveParams["name"], oldSnmpConnector.name);

        def updatedObjects = SnmpConnectorOperations.updateConnector(oldSnmpConnector, connectorUpdateParams);

        def snmpConnectors = SnmpConnector.list();
        assertEquals(1, snmpConnectors.size());
        def snmpConnector = snmpConnectors[0]
        assertNotNull(updatedObjects["snmpConnector"])
        assertEquals(updatedObjects["snmpConnector"].id, snmpConnector.id)
        assertEquals(connectorUpdateParams["name"], snmpConnector.name);


        List snmpConnections = SnmpConnection.list();
        assertEquals(1, snmpConnections.size());
        SnmpConnection snmpConnection = snmpConnections[0]
        assertNotNull(updatedObjects["snmpConnection"])
        assertEquals(updatedObjects["snmpConnection"].id, snmpConnection.id)
        assertEquals(snmpConnector.getConnectionName(snmpConnector.name), snmpConnection.name);
        assertEquals(connectorUpdateParams.host, snmpConnection.host);
        assertEquals(connectorUpdateParams.port.toInteger(), snmpConnection.port);
        assertEquals(snmpConnector.connection.id, snmpConnection.id);


        List snmpDatasources = SnmpDatasource.list();
        assertEquals(1, snmpDatasources.size());
        SnmpDatasource snmpDatasource = snmpDatasources[0]
        assertNotNull(updatedObjects["datasource"])
        assertEquals(updatedObjects["datasource"].id, snmpDatasource.id)
        assertEquals(snmpConnector.getDatasourceName(snmpConnector.name), snmpDatasource.name);
        assertEquals(snmpConnection.id, snmpDatasource.connection.id);
        assertTrue(snmpDatasource.isFree());

        def scripts = CmdbScript.list();
        assertEquals(1, scripts.size());
        CmdbScript script = scripts[0]
        assertNotNull(updatedObjects["script"])
        assertEquals(updatedObjects["script"].id, script.id)
        assertEquals(connectorUpdateParams["name"], script.name);
        assertEquals(connectorUpdateParams.scriptFile, script.scriptFile);
        assertEquals(connectorUpdateParams.logLevel, script.logLevel);
        assertEquals(true, script.logFileOwn);
        assertEquals(CmdbScript.LISTENING, script.type);
        assertEquals("", script.staticParam);
        assertEquals(snmpConnector.script.id, script.id);
    }

    public void testRunningConnectorCanNotBeUpdated() {
        def createdObjects = SnmpConnectorOperations.addConnector(connectorSaveParams);

        List snmpConnectors = SnmpConnector.list();
        assertEquals(1, snmpConnectors.size());
        SnmpConnector snmpConnector = snmpConnectors[0]
        assertEquals(connectorSaveParams["name"], snmpConnector.name);

        ListeningAdapterManager.metaClass.isFree = {BaseListeningDatasource listeningDatasource -> return false;}

        def updatedObjects = SnmpConnectorOperations.updateConnector(snmpConnector, connectorUpdateParams);
        assertTrue(updatedObjects["exception"] instanceof MessageSourceException)
        assertEquals("connector.update.exception", updatedObjects["exception"].code)
        assertEquals(connectorSaveParams["host"], SnmpConnector.get(id: snmpConnector.id).connection.host)
    }

    public void testUpdateRollbacksDataIfScriptHasErrors() {
        def createdObjects = SnmpConnectorOperations.addConnector(connectorSaveParams);

        List snmpConnectors = SnmpConnector.list();
        assertEquals(1, snmpConnectors.size());
        SnmpConnector snmpConnector = snmpConnectors[0]
        assertEquals(connectorSaveParams["name"], snmpConnector.name);


        ScriptManager.metaClass.checkScript = {String script -> throw new Exception("");}
        connectorUpdateParams["name"] = "newSnmpConnector";
        def updatedObjects = SnmpConnectorOperations.updateConnector(snmpConnector, connectorUpdateParams);

        assertFalse(updatedObjects.snmpConnector.hasErrors());
        assertFalse(updatedObjects.snmpConnection.hasErrors());
        assertFalse(updatedObjects.datasource.hasErrors());
        assertTrue(updatedObjects.script.hasErrors());
        assertEquals("script.compilation.error", updatedObjects.script.errors.allErrors[0].code);

        //connector rollback
        snmpConnector = SnmpConnector.get(id: snmpConnector.id);
        assertEquals(connectorSaveParams["name"], snmpConnector.name)
        //connection rollback
        assertEquals(connectorSaveParams["host"], snmpConnector.connection.host)
        assertEquals(connectorSaveParams["port"], snmpConnector.connection.port)
    }

    public void testUpdateRollbacksDataIfConnectionHasErrors() {
        def createdObjects = SnmpConnectorOperations.addConnector(connectorSaveParams);

        List snmpConnectors = SnmpConnector.list();
        assertEquals(1, snmpConnectors.size());
        SnmpConnector snmpConnector = snmpConnectors[0]
        assertEquals(connectorSaveParams["name"], snmpConnector.name);


        connectorUpdateParams["name"] = "newSnmpConnector";
        connectorUpdateParams["port"] = "asdfasdf";
        connectorUpdateParams["logLevel"] = Level.ALL.toString();
        def updatedObjects = SnmpConnectorOperations.updateConnector(snmpConnector, connectorUpdateParams);

        assertFalse(updatedObjects.snmpConnector.hasErrors());
        assertTrue(updatedObjects.snmpConnection.hasErrors());
        assertFalse(updatedObjects.datasource.hasErrors());
        assertFalse(updatedObjects.script.hasErrors());

        //connector rollback
        snmpConnector = SnmpConnector.get(id: snmpConnector.id);
        assertEquals(connectorSaveParams["name"], snmpConnector.name)
        //script rollback
        assertEquals(connectorSaveParams["logLevel"], snmpConnector.script.logLevel)
    }

    public void testSuccessfulDelete()
    {
        def createdObjects = SnmpConnectorOperations.addConnector(connectorSaveParams);

        List snmpConnectors = SnmpConnector.list();
        assertEquals(1, snmpConnectors.size());
        SnmpConnector snmpConnector = snmpConnectors[0]
        assertEquals(connectorSaveParams["name"], snmpConnector.name);

        assertEquals(1, SnmpConnector.count());
        assertEquals(1, SnmpConnection.count());
        assertEquals(1, SnmpDatasource.count());
        assertEquals(1, CmdbScript.count());

        SnmpConnectorOperations.deleteConnector(snmpConnector)

        assertEquals(0, SnmpConnector.count());
        assertEquals(0, SnmpConnection.count());
        assertEquals(0, SnmpDatasource.count());
        assertEquals(0, CmdbScript.count());
    }

    public void testStartConnector() {
        def createdObjects = SnmpConnectorOperations.addConnector(connectorSaveParams);
        List snmpConnectors = SnmpConnector.list();
        assertEquals(1, snmpConnectors.size());
        SnmpConnector snmpConnector = snmpConnectors[0]
        assertEquals(connectorSaveParams["name"], snmpConnector.name);
        assertFalse(snmpConnector.script.listeningDatasource.isSubscribed);

        SnmpConnectorOperations.startConnector(snmpConnector);
        assertTrue(snmpConnector.script.listeningDatasource.isSubscribed)
    }

     public void testStopConnector() {
        def createdObjects = SnmpConnectorOperations.addConnector(connectorSaveParams);
        List snmpConnectors = SnmpConnector.list();
        assertEquals(1, snmpConnectors.size());
        SnmpConnector snmpConnector = snmpConnectors[0]
        assertEquals(connectorSaveParams["name"], snmpConnector.name);
        assertFalse(snmpConnector.script.listeningDatasource.isSubscribed);

        SnmpConnectorOperations.startConnector(snmpConnector);
        assertTrue(snmpConnector.script.listeningDatasource.isSubscribed)

        SnmpConnectorOperations.stopConnector(snmpConnector);
        assertFalse(snmpConnector.script.listeningDatasource.isSubscribed)
    }
}