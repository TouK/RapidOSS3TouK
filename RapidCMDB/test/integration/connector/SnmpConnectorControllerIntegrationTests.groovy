package connector

import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import connection.SnmpConnection
import datasource.SnmpDatasource
import script.CmdbScript
import org.apache.log4j.Level
import com.ifountain.rcmdb.test.util.IntegrationTestUtils
import com.ifountain.rcmdb.datasource.ListeningAdapterManager
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.rcmdb.test.util.ClosureWaitAction
import com.ifountain.rcmdb.datasource.ListeningAdapterRunner;


/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 7, 2009
* Time: 4:55:08 PM
* To change this template use File | Settings | File Templates.
*/
class SnmpConnectorControllerIntegrationTests extends RapidCmdbIntegrationTestCase
{
    static transactional = false;
    def connectorSaveParams = [:];
    def connectorUpdateParams = [:];
    static int portNumber = 15555;
    public void setUp() {
        super.setUp();
        SnmpConnector.removeAll();
        SnmpConnection.removeAll();
        SnmpDatasource.removeAll();
        CmdbScript.list().each {
            CmdbScript.deleteScript(it);
        }


        connectorSaveParams["host"] = "0.0.0.0";
        connectorSaveParams["port"] = ""+portNumber;
        connectorSaveParams["logLevel"] = Level.DEBUG.toString();
        connectorSaveParams["scriptFile"] = "sampleSnmpScript"


        connectorUpdateParams["host"] = "192.168.1.1";
        connectorUpdateParams["port"] = ""+(portNumber+1);
        connectorUpdateParams["logLevel"] = Level.WARN.toString();
        connectorUpdateParams["scriptFile"] = "sampleSnmpScriptForUpdate"
//        portNumber = portNumber+2;
    }

    public void tearDown() {
        super.tearDown();
    }
    def createScriptFile(String scriptFileName)
    {
        def file = new File("${System.getProperty("base.dir")}/scripts/${scriptFileName}.groovy");
        file.setText("""
            def getParameters(){
               return [:]
            }

            def init(){

            }

            def cleanUp(){

            }

            def update(eventTrap){

            }
        """);
    }


    def getSnmpControllerForSave(String connectorName, params)
    {
        createScriptFile(params.scriptFile);
        def controller = new SnmpConnectorController();
        controller.params["name"] = connectorName;
        controller.params.putAll(params);
        return controller;
    }
    def getSnmpControllerForUpdate(Long connectorId, String connectorName, params)
    {
        createScriptFile(params.scriptFile);

        def controller = new SnmpConnectorController();
        controller.params["id"] = "" + connectorId;
        controller.params["name"] = connectorName;
        controller.params.putAll(params);
        return controller;
    }

    public void testSuccessfulSave()
    {
        String connectorName = "snmpTestConnector";

        def controller = getSnmpControllerForSave(connectorName, connectorSaveParams);
        controller.save();

        List snmpConnectors = SnmpConnector.list();
        assertEquals(1, snmpConnectors.size());
        SnmpConnector snmpConnector = snmpConnectors[0]
        assertEquals(connectorName, snmpConnector.name);

        assertEquals("/snmpConnector/show/${snmpConnector.id}", controller.response.redirectedUrl);


        List snmpConnections = SnmpConnection.list();
        assertEquals(1, snmpConnections.size());
        SnmpConnection snmpConnection = snmpConnections[0]
        assertEquals(snmpConnector.getConnectionName(snmpConnector.name), snmpConnection.name);
        assertEquals(connectorSaveParams.host, snmpConnection.host);
        assertEquals(connectorSaveParams.port.toInteger(), snmpConnection.port);
        assertEquals(snmpConnector.connection.id, snmpConnection.id);


        List snmpDatasources = SnmpDatasource.list();
        assertEquals(1, snmpDatasources.size());
        SnmpDatasource snmpDatasource = snmpDatasources[0]
        assertEquals(snmpConnector.getDatasourceName(snmpConnector.name), snmpDatasource.name);
        assertEquals(snmpConnection.id, snmpDatasource.connection.id);
        assertTrue(snmpDatasource.isFree());

        def scripts = CmdbScript.list();
        assertEquals(1, scripts.size());
        CmdbScript script = scripts[0]
        assertEquals(connectorName, script.name);
        assertEquals(connectorSaveParams.scriptFile, script.scriptFile);
        assertEquals(connectorSaveParams.logLevel, script.logLevel);
        assertEquals(true, script.logFileOwn);
        assertEquals(CmdbScript.LISTENING, script.type);
        assertEquals("", script.staticParam);
        assertEquals(snmpConnector.script.id, script.id);

    }

    public void testSuccessfulUpdate()
    {
        String connectorName = "snmpTestConnector";

        def controller = getSnmpControllerForSave(connectorName, connectorSaveParams);
        controller.save();

        List oldSnmpConnectors = SnmpConnector.list();
        assertEquals(1, oldSnmpConnectors.size());
        SnmpConnector oldSnmpConnector = oldSnmpConnectors[0]
        assertEquals(connectorName, oldSnmpConnector.name);

        assertEquals("/snmpConnector/show/${oldSnmpConnector.id}", controller.response.redirectedUrl);


        IntegrationTestUtils.resetController(controller);

        controller = getSnmpControllerForUpdate(oldSnmpConnector.id, connectorName, connectorUpdateParams);
        controller.update();

        def snmpConnectors = SnmpConnector.list();
        assertEquals(1, snmpConnectors.size());
        def snmpConnector = snmpConnectors[0]
        assertEquals(connectorName, snmpConnector.name);

        assertEquals("/snmpConnector/show/${snmpConnector.id}", controller.response.redirectedUrl);

        List snmpConnections = SnmpConnection.list();
        assertEquals(1, snmpConnections.size());
        SnmpConnection snmpConnection = snmpConnections[0]
        assertEquals(snmpConnector.getConnectionName(snmpConnector.name), snmpConnection.name);
        assertEquals(connectorUpdateParams.host, snmpConnection.host);
        assertEquals(connectorUpdateParams.port.toInteger(), snmpConnection.port);
        assertEquals(snmpConnector.connection.id, snmpConnection.id);


        List snmpDatasources = SnmpDatasource.list();
        assertEquals(1, snmpDatasources.size());
        SnmpDatasource snmpDatasource = snmpDatasources[0]
        assertEquals(snmpConnector.getDatasourceName(snmpConnector.name), snmpDatasource.name);
        assertEquals(snmpConnection.id, snmpDatasource.connection.id);
        assertTrue(snmpDatasource.isFree());

        def scripts = CmdbScript.list();
        assertEquals(1, scripts.size());
        CmdbScript script = scripts[0]
        assertEquals(connectorName, script.name);
        assertEquals(connectorUpdateParams.scriptFile, script.scriptFile);
        assertEquals(connectorUpdateParams.logLevel, script.logLevel);
        assertEquals(true, script.logFileOwn);
        assertEquals(CmdbScript.LISTENING, script.type);
        assertEquals("", script.staticParam);
        assertEquals(snmpConnector.script.id, script.id);
    }
    public void testSuccessfulDelete()
    {
        String connectorName = "snmpTestConnector";

        def controller = getSnmpControllerForSave(connectorName, connectorSaveParams);
        controller.save();

        SnmpConnector snmpConnector = SnmpConnector.get(name:connectorName);

        assertEquals("/snmpConnector/show/${snmpConnector.id}", controller.response.redirectedUrl);


        assertEquals(1, SnmpConnector.count());
        assertEquals(1, SnmpConnection.count());
        assertEquals(1, SnmpDatasource.count());
        assertEquals(1, CmdbScript.count());

        IntegrationTestUtils.resetController(controller);
        controller = getSnmpControllerForSave(connectorName, connectorSaveParams);
        controller.params.clear();
        controller.params.id=snmpConnector.id;
        controller.delete();

        assertEquals("/snmpConnector/list", controller.response.redirectedUrl);

        println  controller.response.redirectedUrl
        assertEquals(0, SnmpConnector.count());
        assertEquals(0, SnmpConnection.count());
        assertEquals(0, SnmpDatasource.count());
        assertEquals(0, CmdbScript.count());
    }
    public void testSuccessfullStartConnectorAndStopConnector()
    {
        String connectorName = "snmpTestConnector";

        def controller = getSnmpControllerForSave(connectorName, connectorSaveParams);
        controller.save();

        SnmpConnector snmpConnector = SnmpConnector.get(name:connectorName);

        assertEquals("/snmpConnector/show/${snmpConnector.id}", controller.response.redirectedUrl);


        assertEquals(1, SnmpConnector.count());
        assertEquals(1, SnmpConnection.count());
        assertEquals(1, SnmpDatasource.count());
        assertEquals(1, CmdbScript.count());

        def ds = SnmpDatasource.list()[0];
        assertTrue(ds.isFree());

        def lastDsStateChangeTime = ListeningAdapterManager.getInstance().getLastStateChangeTime(ds);

        IntegrationTestUtils.resetController(controller);
        println "starting connector"
        controller.params.id=snmpConnector.id;
        controller.startConnector();
        assertFalse(ds.isFree());
        def lastDsStateChangeTimeAfterStart=ListeningAdapterManager.getInstance().getLastStateChangeTime(ds);

        assertEquals ("/snmpConnector/list", controller.response.redirectedUrl);
        assertTrue(controller.flash.message.indexOf("successfully started")>0)
        assertEquals(0,controller.flash.errors.size());
        assertEquals(1,lastDsStateChangeTimeAfterStart.compareTo(lastDsStateChangeTime));

        IntegrationTestUtils.resetController(controller);
        println "stopping connector"
        controller.params.id=snmpConnector.id;
        controller.stopConnector();
        assertTrue(ds.isFree());
        def lastDsStateChangeTimeAfterStop=ListeningAdapterManager.getInstance().getLastStateChangeTime(ds);

        assertEquals ("/snmpConnector/list", controller.response.redirectedUrl);
        assertTrue("Expected successfully stopped but was ${controller.flash.message}".toString(), controller.flash.message.indexOf("successfully stopped")>0)
        println "succesfully stopped"
        assertEquals(0,controller.flash.errors.size());
        assertEquals(1,lastDsStateChangeTimeAfterStop.compareTo(lastDsStateChangeTimeAfterStart));

    }
    public void testStartConnectorWithStartException()
    {
        String connectorName = "snmpTestConnector";

        def controller = getSnmpControllerForSave(connectorName, connectorSaveParams);
        controller.save();

        SnmpConnector snmpConnector = SnmpConnector.get(name:connectorName);

        assertEquals("/snmpConnector/show/${snmpConnector.id}", controller.response.redirectedUrl);


        assertEquals(1, SnmpConnector.count());
        assertEquals(1, SnmpConnection.count());
        assertEquals(1, SnmpDatasource.count());
        assertEquals(1, CmdbScript.count());

        def ds = SnmpDatasource.list()[0];
        assertTrue(ds.isFree());

        def lastDsStateChangeTime = ListeningAdapterManager.getInstance().getLastStateChangeTime(ds);

        IntegrationTestUtils.resetController(controller);

        CmdbScript.removeAll();
        assertEquals(0, CmdbScript.count());
        controller.params.id=snmpConnector.id;
        controller.startConnector();
        assertTrue(ds.isFree());
        def lastDsStateChangeTimeAfterStart=ListeningAdapterManager.getInstance().getLastStateChangeTime(ds);


        assertEquals ("/snmpConnector/list", controller.response.redirectedUrl);

        assertTrue(controller.flash.errors.hasErrors());
        assertEquals(0,lastDsStateChangeTimeAfterStart.compareTo(lastDsStateChangeTime));
    }
    public void testStopConnectorWithStopException()
    {
        String connectorName = "snmpTestConnector";

        def controller = getSnmpControllerForSave(connectorName, connectorSaveParams);
        controller.save();

        SnmpConnector snmpConnector = SnmpConnector.get(name:connectorName);

        assertEquals("/snmpConnector/show/${snmpConnector.id}", controller.response.redirectedUrl);


        assertEquals(1, SnmpConnector.count());
        assertEquals(1, SnmpConnection.count());
        assertEquals(1, SnmpDatasource.count());
        assertEquals(1, CmdbScript.count());

        def ds = SnmpDatasource.list()[0];
        assertTrue(ds.isFree());

        def lastDsStateChangeTime = ListeningAdapterManager.getInstance().getLastStateChangeTime(ds);

        IntegrationTestUtils.resetController(controller);

        controller.params.id=snmpConnector.id;
        controller.startConnector();
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals (ListeningAdapterRunner.STARTED, ListeningAdapterManager.getInstance().getState(ds));
        }))

        assertFalse(ds.isFree());
        def lastDsStateChangeTimeAfterStart=ListeningAdapterManager.getInstance().getLastStateChangeTime(ds);

        assertEquals ("/snmpConnector/list", controller.response.redirectedUrl);
        assertTrue(controller.flash.message.indexOf("successfully started")>0)
        assertEquals(0,controller.flash.errors.size());
        assertEquals(1,lastDsStateChangeTimeAfterStart.compareTo(lastDsStateChangeTime));

        IntegrationTestUtils.resetController(controller);
        CmdbScript.removeAll();

        controller.params.id=snmpConnector.id;
        controller.stopConnector();
        assertFalse(ds.isFree());
        def lastDsStateChangeTimeAfterStop=ListeningAdapterManager.getInstance().getLastStateChangeTime(ds);

        assertEquals ("/snmpConnector/list", controller.response.redirectedUrl);
        assertTrue(controller.flash.errors.hasErrors());
        assertEquals(0,lastDsStateChangeTimeAfterStop.compareTo(lastDsStateChangeTimeAfterStart));
    }
    public void testUpdatingConnectorDoesNotChangeListeningStateOfUnstartedConnector()
    {
        try
        {
            String connectorName = "snmpTestConnector";

            def controller = getSnmpControllerForSave(connectorName, connectorSaveParams);
            controller.save();

            List oldSnmpConnectors = SnmpConnector.list();
            assertEquals(1, oldSnmpConnectors.size());
            SnmpConnector oldSnmpConnector = oldSnmpConnectors[0]
            assertEquals(connectorName, oldSnmpConnector.name);

            assertEquals("/snmpConnector/show/${oldSnmpConnector.id}", controller.response.redirectedUrl);
            def ds = SnmpDatasource.list()[0];
            assertTrue(ds.isFree());

            def lastDsStateChangeTime = ListeningAdapterManager.getInstance().getLastStateChangeTime(ds);

            IntegrationTestUtils.resetController(controller);

            controller = getSnmpControllerForUpdate(oldSnmpConnector.id, connectorName, connectorUpdateParams);
            controller.update();

            def snmpConnectors = SnmpConnector.list();
            assertEquals(1, snmpConnectors.size());
            def snmpConnector = snmpConnectors[0]
            assertEquals(connectorName, snmpConnector.name);

            assertEquals("/snmpConnector/show/${snmpConnector.id}", controller.response.redirectedUrl);
            assertTrue(ds.isFree());
            assertEquals(0, lastDsStateChangeTime.compareTo(ListeningAdapterManager.getInstance().getLastStateChangeTime(ds)));

        }
        catch (e)
        {
            e.printStackTrace();
            fail("should not throw exception");
        }
    }

    public void testUpdateLogLevelOfARunningConnector(){
        ListeningAdapterManager.destroyInstance();
        ListeningAdapterManager.getInstance().initialize();
        String connectorName = "snmpTestConnector";
        def controller = getSnmpControllerForSave(connectorName, connectorSaveParams);
        controller.save();

        List snmpConnectors = SnmpConnector.list();
        assertEquals(1, snmpConnectors.size());
        SnmpConnector snmpConnector = snmpConnectors[0]
        assertEquals(connectorName, snmpConnector.name);

        def logger = CmdbScript.getScriptLogger(snmpConnector.script);
        assertEquals(logger.getLevel(), Level.DEBUG);

        IntegrationTestUtils.resetController(controller);
        controller.params.id = snmpConnector.id;
        controller.startConnector();
        def ds = SnmpDatasource.list()[0];
        assertFalse(ds.isFree());
        assertTrue(controller.flash.message.indexOf("successfully started") > 0)
        assertEquals(0, controller.flash.errors.size());

        IntegrationTestUtils.resetController(controller);
        controller.params.id = snmpConnector.id;
        controller.params["logLevel"] = Level.ALL.toString();
        controller.updateLogLevel();
        assertEquals("/snmpConnector/list", controller.response.redirectedUrl);
        logger = CmdbScript.getScriptLogger(snmpConnector.script);
        assertEquals(logger.getLevel(), Level.ALL);

        IntegrationTestUtils.resetController(controller);
        controller.params.id = snmpConnector.id;
        controller.stopConnector();
        assertTrue(controller.flash.message.indexOf("successfully stopped") > 0)
        assertEquals(0, controller.flash.errors.size());

        assertTrue(ds.isFree());
        ListeningAdapterManager.destroyInstance();
        ListeningAdapterManager.getInstance().initialize();
    }
    public void testRunningConnectorCanNotBeUpdated() {
        ListeningAdapterManager.destroyInstance();
        ListeningAdapterManager.getInstance().initialize();
        String connectorName = "snmpTestConnector";
        def controller = getSnmpControllerForSave(connectorName, connectorSaveParams);
        controller.save();

        List snmpConnectors = SnmpConnector.list();
        assertEquals(1, snmpConnectors.size());
        SnmpConnector snmpConnector = snmpConnectors[0]
        assertEquals(connectorName, snmpConnector.name);

        IntegrationTestUtils.resetController(controller);
        controller.params.id = snmpConnector.id;
        controller.startConnector();
        def ds = SnmpDatasource.list()[0];
        assertFalse(ds.isFree());
        assertTrue(controller.flash.message.indexOf("successfully started") > 0)
        assertEquals(0, controller.flash.errors.size());

        IntegrationTestUtils.resetController(controller);
        controller.params.id = snmpConnector.id;
        controller.params["host"] = "newhost";
        controller.params["logLevel"] = Level.ALL.toString();
        controller.update();
        assertEquals (1, controller.flash.errors.getAllErrors().size());
        assertEquals ("connector.update.exception", controller.flash.errors.getAllErrors()[0].code);

        IntegrationTestUtils.resetController(controller);
        controller.params.id = snmpConnector.id;
        controller.stopConnector();
        assertTrue(controller.flash.message.indexOf("successfully stopped") > 0)
        assertEquals(0, controller.flash.errors.size());

        assertTrue(ds.isFree());
        ListeningAdapterManager.destroyInstance();
        ListeningAdapterManager.getInstance().initialize();
    }

    public void testSaveRollsBackIfConnectorHasErrors()
    {
        String connectorName = "snmpTestConnector";

        def existingConnector=SnmpConnector.add(name:connectorName);
        assertFalse(existingConnector.hasErrors());

        def controller = getSnmpControllerForSave(connectorName, connectorSaveParams);
        controller.save();

        assertTrue(controller.modelAndView.model.snmpConnector.hasErrors());
        assertNull(controller.modelAndView.model.snmpConnection.id);
        assertNull(controller.modelAndView.model.script.id);
        assertNull(controller.modelAndView.model.datasource.id);

        existingConnector.remove();

        assertEquals(0, SnmpConnector.count());
        assertEquals(0, SnmpConnection.count());
        assertEquals(0, SnmpDatasource.count());
        assertEquals(0, CmdbScript.count());

    }

    public void testSaveRollsBackIfConnectionHasErrors()
    {

        String connectorName = "snmpTestConnector";

        def existingConnection=SnmpConnection.add(name:SnmpConnector.getConnectionName(connectorName));
        assertFalse(existingConnection.hasErrors());

        def controller = getSnmpControllerForSave(connectorName,connectorSaveParams);
        controller.save();

        assertFalse(controller.modelAndView.model.snmpConnector.hasErrors());
        assertTrue(controller.modelAndView.model.snmpConnection.hasErrors());
        assertNull(controller.modelAndView.model.script.id);
        assertNull(controller.modelAndView.model.datasource.id);

        existingConnection.remove();

        assertEquals(0, SnmpConnector.count());
        assertEquals(0, SnmpConnection.count());
        assertEquals(0, SnmpDatasource.count());
        assertEquals(0, CmdbScript.count());

    }
    public void testSaveRollsBackIfScriptHasErrors()
    {
        String connectorName = "snmpTestConnector";

        createScriptFile(connectorName);
        def existingScript=CmdbScript.addScript(name:connectorName);
        assertFalse(existingScript.hasErrors());

        def controller = getSnmpControllerForSave(connectorName, connectorSaveParams);
        controller.save();

        println controller.modelAndView.model.snmpConnector.hasErrors()

        assertFalse(controller.modelAndView.model.snmpConnector.hasErrors());
        assertFalse(controller.modelAndView.model.snmpConnection.hasErrors());
        assertTrue(controller.modelAndView.model.script.hasErrors());
        assertNull(controller.modelAndView.model.datasource.id);

        existingScript.remove();

        assertEquals(0, SnmpConnector.count());
        assertEquals(0, SnmpConnection.count());
        assertEquals(0, SnmpDatasource.count());
        assertEquals(0, CmdbScript.count());

    }
    public void testSaveRollsBackIfDatasourceHasErrors()
    {

        String connectorName = "snmpTestConnector";
        def connectionForDs=SnmpConnection.add(name:"testcon");
        assertFalse(connectionForDs.hasErrors());
        def existingDatasource=SnmpDatasource.add(name:SnmpConnector.getDatasourceName(connectorName),connection:connectionForDs);
        assertFalse(existingDatasource.hasErrors());

        def controller = getSnmpControllerForSave(connectorName, connectorSaveParams);
        controller.save();

        assertFalse(controller.modelAndView.model.snmpConnector.hasErrors());
        assertFalse(controller.modelAndView.model.snmpConnection.hasErrors());
        assertFalse(controller.modelAndView.model.script.hasErrors());
        assertTrue(controller.modelAndView.model.datasource.hasErrors());

        connectionForDs.remove();
        existingDatasource.remove();

        assertEquals(0, SnmpConnector.count());
        assertEquals(0, SnmpConnection.count());
        assertEquals(0, SnmpDatasource.count());
        assertEquals(0, CmdbScript.count());

    }


}