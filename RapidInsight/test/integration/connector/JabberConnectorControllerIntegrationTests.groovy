package connector

import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import connection.JabberConnection
import datasource.BaseDatasource
import datasource.JabberDatasource
import com.ifountain.rcmdb.test.util.IntegrationTestUtils
import connection.Connection
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.rcmdb.jabber.connection.JabberConnectionImpl
import com.ifountain.rcmdb.test.util.ConnectionTestUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 12, 2009
* Time: 6:14:12 PM
* To change this template use File | Settings | File Templates.
*/
class JabberConnectorControllerIntegrationTests  extends RapidCmdbIntegrationTestCase{
    static transactional = false;
    def connectorParams=[:]
    public void setUp() {
        super.setUp();
        Connection.removeAll();
        BaseDatasource.removeAll();
        JabberConnector.removeAll();
        JabberConnection.removeAll();
        JabberDatasource.removeAll();
        connectorParams.clear();
        connectorParams["name"] = "testConnector";
        connectorParams["host"] ="jabberhost";
        connectorParams["port"] = 5222;
        connectorParams["username"] = "testaccount";
        connectorParams["userPassword"] = "3600";
        connectorParams["serviceName"] = "a.com";
    }

    public void tearDown() {
        super.tearDown();

    }

    public void testAddSuccessfuly()
    {
        def params=[:]
        params.putAll(connectorParams)


        def controller = new JabberConnectorController();
        params.each{ key , val ->
            controller.params[key] = val;
        }

        controller.save();

        def jabberConnectors = JabberConnector.list();
        assertEquals(1, jabberConnectors.size());
        JabberConnector jabberConnector = jabberConnectors[0]
        assertEquals(params.name, jabberConnector.name);

        assertEquals(String.valueOf(controller.response.redirectedUrl),"/jabberConnector/show/${jabberConnector.id}");



        def jabberConnections=JabberConnection.list();
        assertEquals(1,jabberConnections.size());
        def jabberConnection=jabberConnections[0];

        assertEquals(JabberConnector.getJabberConnectionName(jabberConnector.name),jabberConnection.name);
        assertEquals(jabberConnection.id,jabberConnector.ds.connection.id);
        assertEquals (1,jabberConnection.jabberDatasources.size());

        def paramsToCheck=[:]
        paramsToCheck.putAll(params)
        paramsToCheck.remove("name");

        paramsToCheck.each{ key , val ->
            assertEquals(val,jabberConnection[key])
        }

        def jabberDatasources = JabberDatasource.list();
        assertEquals (1, jabberDatasources.size());
        JabberDatasource jabberDatasource = jabberDatasources[0];

        assertEquals (JabberConnector.getJabberDatasourceName(jabberConnector.name),jabberDatasource.name);
        assertEquals (jabberDatasource.id, jabberConnector.ds.id);
        assertEquals (jabberConnection.id,jabberDatasource.connection.id);
        assertEquals (0, jabberDatasource.reconnectInterval);
        assertEquals (jabberConnection.jabberDatasources[0].id, jabberDatasource.id);
    }
    void testAddRollsBackIfAnyModelHasErrors()
    {
        def params=[:]
        params.putAll(connectorParams)

        def datasource = BaseDatasource.add(name:JabberConnector.getJabberDatasourceName(params.name))
        assertFalse(datasource.hasErrors())

        def controller = new JabberConnectorController();
            params.each{ key , val ->
            controller.params[key] = val;
        }

        controller.save();
        def connector=JabberConnector.list()[0]
        assertEquals(0, JabberConnector.count())
        assertEquals(0, JabberConnection.count())
        assertEquals(0, JabberDatasource.count())


        def model = controller.modelAndView.model;
        def ds = model.jabberDatasource;
        assertTrue(ds.hasErrors())
        assertEquals("rapidcmdb.instance.already.exist", ds.errors.allErrors[0].code)
    }

     public void testUpdateSuccessfully()
    {
        def oldParams=[:]
        oldParams.putAll(connectorParams)

        def controller = new JabberConnectorController();
        oldParams.each{ key , val ->
            controller.params[key] = val;
        }

        controller.save();

        def oldJabberConnectors = JabberConnector.list();
        assertEquals(1, oldJabberConnectors.size());
        JabberConnector oldJabberConnector = oldJabberConnectors[0]
        assertEquals(oldParams.name, oldJabberConnector.name);

        assertEquals(String.valueOf(controller.response.redirectedUrl),"/jabberConnector/show/${oldJabberConnector.id}")



        IntegrationTestUtils.resetController (controller);

        def updateParams=[:]
        updateParams["name"] = "testConnector2";
        updateParams["host"] ="jabberhost2";
        updateParams["port"] = 5000;
        updateParams["username"] = "testaccoun2t";
        updateParams["userPassword"] = "13600";
        updateParams["serviceName"] = "b.com";
        updateParams["id"]=oldJabberConnector.id

        controller = new JabberConnectorController();
        updateParams.each{ key , val ->
            controller.params[key] = val;
        }
        controller.update();

        def jabberConnectors = JabberConnector.list();
        assertEquals(1, jabberConnectors.size());
        JabberConnector jabberConnector = jabberConnectors[0]
        assertEquals(updateParams.name, jabberConnector.name);

        assertEquals(String.valueOf(controller.response.redirectedUrl),"/jabberConnector/show/${jabberConnector.id}")



        def jabberConnections=JabberConnection.list();
        assertEquals(1,jabberConnections.size());
        def jabberConnection=jabberConnections[0];

        assertEquals(JabberConnector.getJabberConnectionName(jabberConnector.name),jabberConnection.name);
        assertEquals(jabberConnection.id,jabberConnector.ds.connection.id);
        assertEquals ( 1,jabberConnection.jabberDatasources.size());

        def paramsToCheck=[:]
        paramsToCheck.putAll(updateParams)
        paramsToCheck.remove("id");

        paramsToCheck.each{ key , val ->
            assertEquals(val,jabberConnection[key])
        }

        def jabberDatasources = JabberDatasource.list();
        assertEquals (1, jabberDatasources.size());
        JabberDatasource jabberDatasource = jabberDatasources[0];

        assertEquals (JabberConnector.getJabberDatasourceName(jabberConnector.name),jabberDatasource.name);
        assertEquals (jabberDatasource.id, jabberConnector.ds.id);
        assertEquals (jabberConnection.id,jabberDatasource.connection.id);
        assertEquals (0, jabberDatasource.reconnectInterval);
        assertEquals (jabberConnection.jabberDatasources[0].id, jabberDatasource.id);


    }
    public void testUpdateGeneratesErrorMessageWhenJabberConnectorNotFound()
    {
        def controller=new JabberConnectorController();

        def objectId="noobject"
        controller.params["id"]=objectId;

        controller.update();

        assertEquals("JabberConnector not found with id ${objectId}",controller.flash.message);
        assertEquals("/jabberConnector/edit/${objectId}", controller.response.redirectedUrl);

    }


    void testUpdateRollsBackIfAnyModelHasErrors(){
        def params=[:]
        params.putAll(connectorParams)


        def controller = new JabberConnectorController();
        params.each{ key , val ->
            controller.params[key] = val;
        }

        controller.save();

        assertEquals(1, JabberConnector.count())
        assertEquals(1, JabberConnection.count())
        assertEquals(1, JabberDatasource.count())

        def updateParams=[:]
        updateParams.putAll(params)
        updateParams["name"]="newJabberConnector"
        updateParams["host"] ="192.168.1.101";
        updateParams["id"]=JabberConnector.list()[0].id

        def baseDs = BaseDatasource.add(name:JabberConnector.getJabberDatasourceName(updateParams.name))
        assertFalse(baseDs.hasErrors())


        IntegrationTestUtils.resetController(controller);
        updateParams.each{ key , val ->
            controller.params[key] = val;
        }
        controller.update();

        def model = controller.modelAndView.model;
        def datasource = model.jabberDatasource;

        assertTrue(datasource.hasErrors())
        assertEquals("default.not.unique.message", datasource.errors.allErrors[0].code)

        assertEquals(1, JabberConnector.count())
        assertEquals(1, JabberConnection.count())
        assertEquals(1, JabberDatasource.count())


        assertNull(JabberConnector.get(name:updateParams.name))
        def jabberConnector = JabberConnector.get(name:params.name);
        assertNotNull(jabberConnector)
        assertEquals(params.host, jabberConnector.ds.connection.host)
        assertEquals(JabberConnector.getJabberDatasourceName(params.name), jabberConnector.ds.name)

    }

    public void testDeleteSuccessfully()
    {
        def params=[:]
        params.putAll(connectorParams)


        def controller = new JabberConnectorController();
        params.each{ key , val ->
           controller.params[key] = val;
        }

        controller.save();

        assertEquals(1, JabberConnector.count())
        assertEquals(1, JabberConnection.count())
        assertEquals(1, JabberDatasource.count())


        IntegrationTestUtils.resetController (controller);

        def existingConnector=JabberConnector.list()[0];

        controller.params["id"]=existingConnector.id.toString();

        controller.delete();

        assertEquals("/jabberConnector/list", controller.response.redirectedUrl);


        assertEquals(0, JabberConnector.count())
        assertEquals(0, JabberConnection.count())
        assertEquals(0, JabberDatasource.count())

    }

    public void testDeleteGeneratesErrorMessageWhenGroupNotFound()
    {
        def controller=new JabberConnectorController();
        def objectId="noobject"
        controller.params["id"]=controller;

        controller.delete();

        assertEquals("JabberConnector not found with id ${controller}",controller.flash.message);
        assertEquals("/jabberConnector/list", controller.response.redirectedUrl);
    }

    private def addConnectorForTestConnection()
    {
        CommonTestUtils.initializeFromFile("RCMDBTest.properties");
        ConnectionParam connParam = ConnectionTestUtils.getJabberConnectionParam();
        def params=connParam.otherParams;

        def connectorSaveParams = [name: "testConnector", host: params[JabberConnectionImpl.HOST], port: params[JabberConnectionImpl.PORT],
                username: params[JabberConnectionImpl.USERNAME], userPassword: params[JabberConnectionImpl.PASSWORD], serviceName: params[JabberConnectionImpl.SERVICENAME]];

        def createdObjects=JabberConnector.addConnector(connectorSaveParams);
        createdObjects.each{ objectName,object ->
                println object.errors
        };

        assertEquals(1, JabberConnector.count())
        assertEquals(1, JabberConnection.count())
        assertEquals(1, JabberDatasource.count())

        return createdObjects.jabberConnector;
    }
    public void testTestConnectionSuccessfullyAndWithException()
    {
        def connector=addConnectorForTestConnection();

        def controller=new JabberConnectorController();

        controller.params["id"]=connector.id.toString();
        controller.testConnection();

        assertEquals("Successfully connected to server.",controller.flash.message);
        assertEquals("/jabberConnector/list", controller.response.redirectedUrl);


        //with exception

        connector.ds.connection.update(username:"ssssssssssssss");
        IntegrationTestUtils.resetController (controller);

        controller.params["id"]=connector.id.toString();
        controller.testConnection();

        assertEquals(1,controller.flash.errors.getAllErrors().size())
        assertEquals("connection.test.exception",controller.flash.errors.getAllErrors()[0].code);
        assertEquals("/jabberConnector/list", controller.response.redirectedUrl);
    }

}
