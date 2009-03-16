package connector

import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import connection.SnmpConnection
import datasource.SnmpDatasource
import script.CmdbScript
import org.apache.log4j.Level
import com.ifountain.rcmdb.test.util.IntegrationTestUtils
import com.ifountain.rcmdb.datasource.ListeningAdapterManager;


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
    def connectorSaveParams=[:];
    def connectorUpdateParams=[:];

    public void setUp() {
        super.setUp();
        SnmpConnector.removeAll();
        SnmpConnection.removeAll();
        SnmpDatasource.removeAll();
        CmdbScript.list().each {
            CmdbScript.deleteScript(it);
        }

        
        connectorSaveParams["host"] = "0.0.0.0";
        connectorSaveParams["port"] = "162";
        connectorSaveParams["logLevel"] = Level.DEBUG.toString();
        connectorSaveParams["scriptFile"]="sampleSnmpScript"


        connectorUpdateParams["host"]="192.168.1.1";
        connectorUpdateParams["port"] ="50";
        connectorUpdateParams["logLevel"]=Level.WARN.toString();
        connectorUpdateParams["scriptFile"]="sampleSnmpScriptForUpdate"
    }

    public void tearDown() {
        super.tearDown();
    }
    def createScriptFile(String scriptFileName)
    {
        def file = new File("${System.getProperty("base.dir")}/scripts/${scriptFileName}.groovy");
        file.setText ("""
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
    def getSnmpControllerForSave(String connectorName,params)
    {
        createScriptFile(params.scriptFile);
        def controller = new SnmpConnectorController();
        controller.params["name"]=connectorName;        
        controller.params.putAll(params);
        return controller;
    }
    def getSnmpControllerForUpdate(Long connectorId,String connectorName,params)
    {
        createScriptFile(params.scriptFile);
        
        def controller = new SnmpConnectorController();
        controller.params["id"]=""+connectorId;
        controller.params["name"]=connectorName;
        controller.params.putAll(params);
        return controller;
    }
    
    public void testSuccessfulSave()
    {   
        String connectorName = "snmpTestConnector";

        def controller = getSnmpControllerForSave(connectorName,connectorSaveParams);
        controller.save();

        List snmpConnectors = SnmpConnector.list();
        assertEquals(1, snmpConnectors.size());
        SnmpConnector snmpConnector = snmpConnectors[0]
        assertEquals(connectorName, snmpConnector.name);
     
        assertEquals ("/snmpConnector/show/${snmpConnector.id}", controller.response.redirectedUrl);


        List snmpConnections = SnmpConnection.list();
        assertEquals(1, snmpConnections.size());
        SnmpConnection snmpConnection = snmpConnections[0]
        assertEquals(snmpConnector.getConnectionName(snmpConnector.name), snmpConnection.name);
        assertEquals(connectorSaveParams.host, snmpConnection.host);
        assertEquals(connectorSaveParams.port.toInteger(), snmpConnection.port);
        assertEquals(snmpConnector.connection.id,snmpConnection.id);


        List snmpDatasources = SnmpDatasource.list();
        assertEquals(1, snmpDatasources.size());
        SnmpDatasource snmpDatasource = snmpDatasources[0]
        assertEquals(snmpConnector.getDatasourceName(snmpConnector.name), snmpDatasource.name);
        assertEquals(snmpConnection.id, snmpDatasource.connection.id);        
        assertTrue(snmpDatasource.isStartable());

        def scripts = CmdbScript.list();
        assertEquals(1, scripts.size());
        CmdbScript script = scripts[0]
        assertEquals(connectorName, script.name);
        assertEquals(connectorSaveParams.scriptFile, script.scriptFile);
        assertEquals(connectorSaveParams.logLevel, script.logLevel);
        assertEquals(true, script.logFileOwn);
        assertEquals(CmdbScript.LISTENING, script.type);
        assertEquals("", script.staticParam);
        assertEquals(snmpConnector.script.id,script.id);
        
    }

    public void testSuccessfulUpdate()
    {
        String connectorName = "snmpTestConnector";

        def controller = getSnmpControllerForSave(connectorName,connectorSaveParams);
        controller.save();

        List oldSnmpConnectors = SnmpConnector.list();
        assertEquals(1, oldSnmpConnectors.size());
        SnmpConnector oldSnmpConnector = oldSnmpConnectors[0]
        assertEquals(connectorName, oldSnmpConnector.name);

        assertEquals ("/snmpConnector/show/${oldSnmpConnector.id}", controller.response.redirectedUrl);
        
        
        IntegrationTestUtils.resetController (controller);

        controller = getSnmpControllerForUpdate(oldSnmpConnector.id,connectorName,connectorUpdateParams);
        controller.update();

        def snmpConnectors = SnmpConnector.list();
        assertEquals(1, snmpConnectors.size());
        def snmpConnector = snmpConnectors[0]
        assertEquals(connectorName, snmpConnector.name);

        assertEquals ("/snmpConnector/show/${snmpConnector.id}", controller.response.redirectedUrl);

        List snmpConnections = SnmpConnection.list();
        assertEquals(1, snmpConnections.size());
        SnmpConnection snmpConnection = snmpConnections[0]
        assertEquals(snmpConnector.getConnectionName(snmpConnector.name), snmpConnection.name);
        assertEquals(connectorUpdateParams.host, snmpConnection.host);
        assertEquals(connectorUpdateParams.port.toInteger(), snmpConnection.port);
        assertEquals(snmpConnector.connection.id,snmpConnection.id);


        List snmpDatasources = SnmpDatasource.list();
        assertEquals(1, snmpDatasources.size());
        SnmpDatasource snmpDatasource = snmpDatasources[0]
        assertEquals(snmpConnector.getDatasourceName(snmpConnector.name), snmpDatasource.name);
        assertEquals(snmpConnection.id, snmpDatasource.connection.id);
        assertTrue(snmpDatasource.isStartable());

        def scripts = CmdbScript.list();
        assertEquals(1, scripts.size());
        CmdbScript script = scripts[0]
        assertEquals(connectorName, script.name);
        assertEquals(connectorUpdateParams.scriptFile, script.scriptFile);
        assertEquals(connectorUpdateParams.logLevel, script.logLevel);
        assertEquals(true, script.logFileOwn);
        assertEquals(CmdbScript.LISTENING, script.type);
        assertEquals("", script.staticParam);
        assertEquals(snmpConnector.script.id,script.id);

    }



    public void testUpdatingConnectorDoesNotChangeListeningStateOfRunningConnector()
    {
          assertTrue(_testUpdatingConnectorDoesNotChangeListeningState(true));
    }
    public void testUpdatingConnectorDoesNotChangeListeningStateOfUnstartedConnector()
    {
          assertFalse(_testUpdatingConnectorDoesNotChangeListeningState(false));
    }
    public boolean _testUpdatingConnectorDoesNotChangeListeningState(runConnector)
    {
        
        if(runConnector)
        {
            ListeningAdapterManager.destroyInstance();
            ListeningAdapterManager.getInstance().initialize();
        }
        try
        {
            String connectorName = "snmpTestConnector";

            def controller = getSnmpControllerForSave(connectorName,connectorSaveParams);
            controller.save();

            List oldSnmpConnectors = SnmpConnector.list();
            assertEquals(1, oldSnmpConnectors.size());
            SnmpConnector oldSnmpConnector = oldSnmpConnectors[0]
            assertEquals(connectorName, oldSnmpConnector.name);

            assertEquals ("/snmpConnector/show/${oldSnmpConnector.id}", controller.response.redirectedUrl);
            def ds= SnmpDatasource.list()[0];
            assertTrue(ds.isStartable());

            def lastDsStateChangeTime=ListeningAdapterManager.getInstance().getLastStateChangeTime(ds);

            if(runConnector)
            {
                IntegrationTestUtils.resetController (controller);

                controller.params.id=oldSnmpConnector.id;
                controller.startConnector();
                assertFalse(ds.isStartable());
                lastDsStateChangeTime=ListeningAdapterManager.getInstance().getLastStateChangeTime(ds);

                assertEquals ("/snmpConnector/list", controller.response.redirectedUrl);
                assertTrue(controller.flash.message.indexOf("successfully started")>0)
                assertEquals(0,controller.flash.errors.size());
            }

            IntegrationTestUtils.resetController (controller);

            controller = getSnmpControllerForUpdate(oldSnmpConnector.id,connectorName,connectorUpdateParams);
            controller.update();

            def snmpConnectors = SnmpConnector.list();
            assertEquals(1, snmpConnectors.size());
            def snmpConnector = snmpConnectors[0]
            assertEquals(connectorName, snmpConnector.name);

            assertEquals ("/snmpConnector/show/${snmpConnector.id}", controller.response.redirectedUrl);

            if(runConnector)
            {
                assertFalse(ds.isStartable());
            }
            else
            {
                assertTrue(ds.isStartable());
            }
            assertEquals(0,lastDsStateChangeTime.compareTo(ListeningAdapterManager.getInstance().getLastStateChangeTime(ds)));

            if(runConnector)
            {
                IntegrationTestUtils.resetController (controller);
                controller.params.id=snmpConnector.id;
                controller.stopConnector();
                assertTrue(controller.flash.message.indexOf("successfully stopped")>0)
                assertEquals(0,controller.flash.errors.size());

                assertTrue(ds.isStartable());

                println controller.flash.message
                println controller.flash.errors
            }

        }
        catch(e)
        {
            e.printStackTrace();
            fail("should not throw exception");
        }
        if(runConnector)
        {               
            ListeningAdapterManager.destroyInstance();
            ListeningAdapterManager.getInstance().initialize();
        }

        return runConnector;
    }
    public void testSaveRollBacksIfConnectorHasErrors()
    {
        String connectorName = "snmpTestConnector";

        def controller = getSnmpControllerForSave(connectorName,connectorSaveParams);
        controller.params.remove("name");
        controller.save();

        assertTrue(controller.modelAndView.model.snmpConnector.hasErrors());
        assertNull(controller.modelAndView.model.snmpConnection.id);
        assertNull(controller.modelAndView.model.script.id);
        

        assertEquals(0, SnmpConnector.list().size());
        assertEquals(0, SnmpConnection.list().size());
        assertEquals(0, SnmpDatasource.list().size());
        assertEquals(0, CmdbScript.list().size());

    }

    /*
    public void testSaveRollBacksIfConnectionHasErrors()
    {
        String connectorName = "snmpTestConnector";
        def controller = getSnmpControllerForSave(connectorName,connectorSaveParams);
        controller.params.port="xxyy";
        controller.save();

        assertNull(controller.modelAndView.model.snmpConnector.hasErrors());
        assertTrue(controller.modelAndView.model.snmpConnection.hasErrors());
        assertNull(controller.modelAndView.model.script.id);


        assertEquals(0, SnmpConnector.list().size());
        assertEquals(0, SnmpConnection.list().size());
        assertEquals(0, SnmpDatasource.list().size());
        assertEquals(0, CmdbScript.list().size());


    }
    */
     /*
    public void testRollbackDataInUpdate()
    {
        String scriptFileName = "file1";
        long reconnectInterval = 123123213;
        def file = new File("${System.getProperty("base.dir")}/scripts/${scriptFileName}.groovy");
        file.setText ("return;")
        SmartsConnectionData smartsConnectionData = SmartsConnectionData.add(name:"con1", broker:connectionParams.broker, username:connectionParams.username, password:connectionParams.password);
        assertFalse(smartsConnectionData.hasErrors());



        def controller = getTopologyController(smartsConnectionData, scriptFileName)
        controller.params.reconnectInterval = ""+reconnectInterval;
        String connectorName = controller.params.name;
        controller.save();
        List smartsConnectors = SmartsConnector.list();
        assertEquals(1, smartsConnectors.size());
        SmartsListeningTopologyConnector smartsConnector = smartsConnectors[0]


        assertEquals ("/smartsConnector/show/${smartsConnector.id}", controller.response.redirectedUrl);


        IntegrationTestUtils.resetController (controller);

        //test if script has error will rollback connector data and datasource data
        String scriptFileNameForUpdate = "file2";
        long updatedReconnectInterval = 123123213;
        file = new File("${System.getProperty("base.dir")}/scripts/${scriptFileNameForUpdate}.groovy");
        file.delete();
        SmartsConnectionData smartsConnectionDataForUpdate = SmartsConnectionData.add(name:"con2", broker:"newBroker", username:"newUsername", password:"newPassword");
        assertFalse(smartsConnectionData.hasErrors());
        controller = getTopologyController(smartsConnectionDataForUpdate)
        controller.params.scriptFile = scriptFileNameForUpdate;
        controller.params.reconnectInterval = ""+updatedReconnectInterval;
        controller.params.id = ""+smartsConnector.id;
        controller.update();

        def model = controller.modelAndView.model;
        assertFalse (model.smartsConnector.hasErrors());
        assertFalse (model.smartsConnection.hasErrors());
        assertFalse (model.datasource.hasErrors());
        assertTrue (model.listeningScript.hasErrors());
        assertEquals("script.compilation.error", model.listeningScript.errors.allErrors[0].code);
        assertEquals (scriptFileName, smartsConnector.get(id:smartsConnector.id).scriptFile);
        assertEquals (reconnectInterval, smartsConnector.get(id:smartsConnector.id).ds.reconnectInterval);
        assertEquals (scriptFileName, smartsConnector.get(id:smartsConnector.id).ds.listeningScript.scriptFile);

        //test if datasource has error will rollback connector data and script data
        file.setText ("return;");
        controller = getTopologyController(smartsConnectionDataForUpdate, scriptFileNameForUpdate)
        controller.params.reconnectInterval = "asdasdasdsadasdsadsad";
        controller.params.id = ""+smartsConnector.id;
        controller.update();

        model = controller.modelAndView.model;


        assertFalse (model.smartsConnector.hasErrors());
        assertFalse (model.smartsConnection.hasErrors());
        assertTrue (model.datasource.hasErrors());
        assertFalse (model.listeningScript.hasErrors());
        assertEquals (scriptFileName, smartsConnector.get(id:smartsConnector.id).scriptFile);
        assertEquals (scriptFileName, smartsConnector.get(id:smartsConnector.id).ds.listeningScript.scriptFile);
        assertEquals (reconnectInterval, smartsConnector.get(id:smartsConnector.id).ds.reconnectInterval);


        //test if connection has error will rollback connector, script and datasource data
        file.setText ("return;");
        controller = getTopologyController(smartsConnectionDataForUpdate, scriptFileNameForUpdate)
        controller.params.reconnectInterval = updatedReconnectInterval;
        controller.params.domainType = "INVALID_DOMAIN_TYPE";
        controller.params.id = ""+smartsConnector.id;
        controller.update();

        model = controller.modelAndView.model;
        assertFalse (model.smartsConnector.hasErrors());
        assertTrue (model.smartsConnection.hasErrors());
        assertFalse (model.datasource.hasErrors());
        assertFalse (model.listeningScript.hasErrors());
        assertEquals (scriptFileName, smartsConnector.get(id:smartsConnector.id).scriptFile);
        assertEquals (scriptFileName, smartsConnector.get(id:smartsConnector.id).ds.listeningScript.scriptFile);
        assertEquals (reconnectInterval, smartsConnector.get(id:smartsConnector.id).ds.reconnectInterval);
        assertEquals (smartsConnector.ds.connection.domainType, smartsConnector.get(id:smartsConnector.id).ds.connection.domainType);
        //TODO:we should make sure that CmdbScript.update is called for script rollback but how?

    }



    public void testIfScriptHasErrorsDoesnotCreateDatasourceAndRemoveCreatedConnectorAndConnection()
    {
        SmartsConnectionData smartsConnectionData = SmartsConnectionData.add(name:"con1", broker:connectionParams.broker, username:connectionParams.username, password:connectionParams.password);
        assertFalse(smartsConnectionData.hasErrors());
        def controller = getTopologyController(smartsConnectionData);
        String connectorName = controller.params.name;
        def previouslyAddedScript = CmdbScript.addScript(name:SmartsListeningTopologyConnector.getScriptName(connectorName), scriptFile:SmartsListeningTopologyConnector.DEFAULT_CONNECTOR_SCRIPT);
        assertFalse (previouslyAddedScript.hasErrors());

        controller.save();

        assertFalse (controller.modelAndView.model.listeningScript.hasErrors());
        assertFalse(controller.modelAndView.model.smartsConnector.hasErrors());
        assertFalse(controller.modelAndView.model.smartsConnection.hasErrors());
        assertNull(controller.modelAndView.model.datasource.id);
        assertEquals (1, controller.flash.errors.getAllErrors().size());
        assertEquals ("script.already.exist", controller.flash.errors.getAllErrors()[0].code);

        assertEquals(0, SmartsConnector.list().size());
        assertEquals(0, SmartsConnection.list().size());
        assertEquals(0, SmartsTopologyDatasource.list().size());
        assertEquals(1, CmdbScript.list().size());

    }

    public void testIfDatasourceAlreadyExistsRemoveCreatedConnectorAndConnectionAdnScript()
    {
        SmartsConnectionData smartsConnectionData = SmartsConnectionData.add(name:"con1", broker:connectionParams.broker, username:connectionParams.username, password:connectionParams.password);
        assertFalse(smartsConnectionData.hasErrors());

        def controller = getTopologyController(smartsConnectionData);
        String connectorName = controller.params.name;
        SmartsConnection conn = SmartsConnection.add(name:"con1", username:smartsConnectionData.username, userPassword:smartsConnectionData.password, domainType:SmartsConnection.AM, domain:connectionParams.domain);
        assertFalse (conn.hasErrors());
        SmartsTopologyDatasource previouslyAddedDatasource = SmartsTopologyDatasource.add(name:SmartsListeningTopologyConnector.getDatasourceName(connectorName), connection:conn);
        assertFalse (previouslyAddedDatasource.hasErrors());

        controller.save();

        assertFalse (controller.modelAndView.model.listeningScript.hasErrors());
        assertFalse(controller.modelAndView.model.smartsConnector.hasErrors());
        assertFalse(controller.modelAndView.model.smartsConnection.hasErrors());
        assertNull(controller.modelAndView.model.datasource.id);
        assertEquals (1, controller.flash.errors.getAllErrors().size());
        assertEquals ("datasource.already.exist", controller.flash.errors.getAllErrors()[0].code);

        assertEquals(0, SmartsConnector.list().size());

        assertEquals(1, SmartsConnection.list().size());
        def connectionAfterSave = SmartsConnection.list()[0]
        assertEquals(conn.name, connectionAfterSave.name)
        assertEquals(1, SmartsTopologyDatasource.list().size());
        def datasource = SmartsTopologyDatasource.list()[0];
        assertEquals(conn.id, datasource.connection.id);
        assertEquals(0, CmdbScript.list().size());
    }

    public void testIfConnectionAlreadyExistsDoesnotCreateDatasourceAndScriptRemoveCreatedConnector()
    {
        SmartsConnectionData smartsConnectionData = SmartsConnectionData.add(name:"con1", broker:connectionParams.broker, username:connectionParams.username, password:connectionParams.password);
        assertFalse(smartsConnectionData.hasErrors());

        def controller = getTopologyController(smartsConnectionData);
        String connectorName = controller.params.name;
        SmartsConnection conn = SmartsConnection.add(name:SmartsListeningTopologyConnector.getConnectionName(connectorName), username:smartsConnectionData.username, userPassword:smartsConnectionData.password, domainType:SmartsConnection.AM, domain:connectionParams.domain);
        assertFalse (conn.hasErrors());

        controller.save();

        assertFalse (controller.modelAndView.model.listeningScript.hasErrors());
        assertFalse(controller.modelAndView.model.smartsConnector.hasErrors());
        assertFalse(controller.modelAndView.model.smartsConnection.hasErrors());
        assertNull(controller.modelAndView.model.datasource.id);
        assertEquals (1, controller.flash.errors.getAllErrors().size());
        assertEquals ("connection.already.exist", controller.flash.errors.getAllErrors()[0].code);

        assertEquals(0, SmartsConnector.list().size());
        assertEquals(1, SmartsConnection.list().size());
        def connectionAfterSave = SmartsConnection.list()[0]
        assertEquals(conn.name, connectionAfterSave.name)
        assertEquals(0, SmartsTopologyDatasource.list().size());
        assertEquals(0, CmdbScript.list().size());

    }

    public void testIfDatasourceHasErrorsRemoveCreatedConnectorAndConnectionAdnScript()
    {
        SmartsConnectionData smartsConnectionData = SmartsConnectionData.add(name:"con1", broker:connectionParams.broker, username:connectionParams.username, password:connectionParams.password);
        assertFalse(smartsConnectionData.hasErrors());

        def controller = getTopologyController(smartsConnectionData);
        controller.params.remove("reconnectInterval");
        String connectorName = controller.params.name;
        controller.save();

        assertFalse (controller.modelAndView.model.listeningScript.hasErrors());
        assertFalse(String.valueOf(controller.modelAndView.model.smartsConnector.errors), controller.modelAndView.model.smartsConnector.hasErrors());
        assertFalse(controller.modelAndView.model.smartsConnection.hasErrors());
        assertNull(controller.modelAndView.model.datasource.id);

        assertEquals(0, SmartsConnector.list().size());
        assertEquals(0, SmartsConnection.list().size());
        assertEquals(0, SmartsTopologyDatasource.list().size());
        assertEquals(0, CmdbScript.list().size());

    }

    
    def getNotificationController(SmartsConnectionData smartsConnectionData, String scriptFileName = SmartsListeningNotificationConnector.DEFAULT_CONNECTOR_SCRIPT)
    {
        String connectorName = "notificationConnector";
        def controller = new SmartsConnectorController();
        controller.params["name"] = connectorName;
        controller.params["type"] = SmartsConnectorController.NOTIFICATION_CONNECTOR_TYPE;
        controller.params["domainType"] = SmartsConnection.SAM;
        controller.params["domain"] = connectionParams.domain;
        controller.params["reconnectInterval"] = 0;
        controller.params["logLevel"] = Level.DEBUG.toString();
        controller.params["tailMode"] = true;
        controller.params["scriptFile"] = scriptFileName;
        controller.params["notificationList"] = "nl";
        controller.params["propertiesToSubscribe"] = "prop1,prop2";
        controller.params["connectionData.id"] = "${smartsConnectionData.id}";
        return controller;
    }
    */

}