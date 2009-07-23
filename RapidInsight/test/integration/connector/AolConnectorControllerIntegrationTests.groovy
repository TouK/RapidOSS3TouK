package connector

import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import connection.AolConnection
import datasource.BaseDatasource
import datasource.AolDatasource
import com.ifountain.rcmdb.test.util.IntegrationTestUtils
import connection.Connection
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.core.connection.ConnectionParam

import com.ifountain.rcmdb.test.util.ConnectionTestUtils
import com.ifountain.rcmdb.aol.connection.AolConnectionImpl

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 12, 2009
* Time: 6:14:12 PM
* To change this template use File | Settings | File Templates.
*/
class AolConnectorControllerIntegrationTests  extends RapidCmdbIntegrationTestCase{
    static transactional = false;
    def connectorParams=[:]
    public void setUp() {
        super.setUp();
        Connection.removeAll();
        BaseDatasource.removeAll();
        AolConnector.removeAll();
        AolConnection.removeAll();
        AolDatasource.removeAll();
        connectorParams.clear();
        connectorParams["name"] = "testConnector";
        connectorParams["host"] ="aolhost";
        connectorParams["port"] = 5222;
        connectorParams["username"] = "testaccount";
        connectorParams["userPassword"] = "3600";
    }

    public void tearDown() {
        super.tearDown();

    }

    public void testAddSuccessfuly()
    {
        def params=[:]
        params.putAll(connectorParams)


        def controller = new AolConnectorController();
        params.each{ key , val ->
            controller.params[key] = val;
        }

        controller.save();

        def aolConnectors = AolConnector.list();
        assertEquals(1, aolConnectors.size());
        AolConnector aolConnector = aolConnectors[0]
        assertEquals(params.name, aolConnector.name);

        assertEquals(String.valueOf(controller.response.redirectedUrl),"/aolConnector/show/${aolConnector.id}");



        def aolConnections=AolConnection.list();
        assertEquals(1,aolConnections.size());
        def aolConnection=aolConnections[0];

        assertEquals(AolConnector.getAolConnectionName(aolConnector.name),aolConnection.name);
        assertEquals(aolConnection.id,aolConnector.ds.connection.id);
        assertEquals (1,aolConnection.aolDatasources.size());

        def paramsToCheck=[:]
        paramsToCheck.putAll(params)
        paramsToCheck.remove("name");

        paramsToCheck.each{ key , val ->
            assertEquals(val,aolConnection[key])
        }

        def aolDatasources = AolDatasource.list();
        assertEquals (1, aolDatasources.size());
        AolDatasource aolDatasource = aolDatasources[0];

        assertEquals (AolConnector.getAolDatasourceName(aolConnector.name),aolDatasource.name);
        assertEquals (aolDatasource.id, aolConnector.ds.id);
        assertEquals (aolConnection.id,aolDatasource.connection.id);
        assertEquals (0, aolDatasource.reconnectInterval);
        assertEquals (aolConnection.aolDatasources[0].id, aolDatasource.id);
    }
    void testAddRollsBackIfAnyModelHasErrors()
    {
        def params=[:]
        params.putAll(connectorParams)

        def datasource = BaseDatasource.add(name:AolConnector.getAolDatasourceName(params.name))
        assertFalse(datasource.hasErrors())

        def controller = new AolConnectorController();
            params.each{ key , val ->
            controller.params[key] = val;
        }

        controller.save();
        def connector=AolConnector.list()[0]
        assertEquals(0, AolConnector.count())
        assertEquals(0, AolConnection.count())
        assertEquals(0, AolDatasource.count())


        def model = controller.modelAndView.model;
        def ds = model.aolDatasource;
        assertTrue(ds.hasErrors())
        assertEquals("rapidcmdb.instance.already.exist", ds.errors.allErrors[0].code)
    }

     public void testUpdateSuccessfully()
    {
        def oldParams=[:]
        oldParams.putAll(connectorParams)

        def controller = new AolConnectorController();
        oldParams.each{ key , val ->
            controller.params[key] = val;
        }

        controller.save();

        def oldAolConnectors = AolConnector.list();
        assertEquals(1, oldAolConnectors.size());
        AolConnector oldAolConnector = oldAolConnectors[0]
        assertEquals(oldParams.name, oldAolConnector.name);

        assertEquals(String.valueOf(controller.response.redirectedUrl),"/aolConnector/show/${oldAolConnector.id}")



        IntegrationTestUtils.resetController (controller);

        def updateParams=[:]
        updateParams["name"] = "testConnector2";
        updateParams["host"] ="aolhost2";
        updateParams["port"] = 5000;
        updateParams["username"] = "testaccoun2t";
        updateParams["userPassword"] = "13600";
        updateParams["id"]=oldAolConnector.id

        controller = new AolConnectorController();
        updateParams.each{ key , val ->
            controller.params[key] = val;
        }
        controller.update();

        def aolConnectors = AolConnector.list();
        assertEquals(1, aolConnectors.size());
        AolConnector aolConnector = aolConnectors[0]
        assertEquals(updateParams.name, aolConnector.name);

        assertEquals(String.valueOf(controller.response.redirectedUrl),"/aolConnector/show/${aolConnector.id}")



        def aolConnections=AolConnection.list();
        assertEquals(1,aolConnections.size());
        def aolConnection=aolConnections[0];

        assertEquals(AolConnector.getAolConnectionName(aolConnector.name),aolConnection.name);
        assertEquals(aolConnection.id,aolConnector.ds.connection.id);
        assertEquals ( 1,aolConnection.aolDatasources.size());

        def paramsToCheck=[:]
        paramsToCheck.putAll(updateParams)
        paramsToCheck.remove("id");

        paramsToCheck.each{ key , val ->
            assertEquals(val,aolConnection[key])
        }

        def aolDatasources = AolDatasource.list();
        assertEquals (1, aolDatasources.size());
        AolDatasource aolDatasource = aolDatasources[0];

        assertEquals (AolConnector.getAolDatasourceName(aolConnector.name),aolDatasource.name);
        assertEquals (aolDatasource.id, aolConnector.ds.id);
        assertEquals (aolConnection.id,aolDatasource.connection.id);
        assertEquals (0, aolDatasource.reconnectInterval);
        assertEquals (aolConnection.aolDatasources[0].id, aolDatasource.id);


    }
    public void testUpdateGeneratesErrorMessageWhenAolConnectorNotFound()
    {
        def controller=new AolConnectorController();

        def objectId="noobject"
        controller.params["id"]=objectId;

        controller.update();

        assertEquals("AolConnector not found with id ${objectId}",controller.flash.message);
        assertEquals("/aolConnector/edit/${objectId}", controller.response.redirectedUrl);

    }


    void testUpdateRollsBackIfAnyModelHasErrors(){
        def params=[:]
        params.putAll(connectorParams)


        def controller = new AolConnectorController();
        params.each{ key , val ->
            controller.params[key] = val;
        }

        controller.save();

        assertEquals(1, AolConnector.count())
        assertEquals(1, AolConnection.count())
        assertEquals(1, AolDatasource.count())

        def updateParams=[:]
        updateParams.putAll(params)
        updateParams["name"]="newAolConnector"
        updateParams["host"] ="192.168.1.101";
        updateParams["id"]=AolConnector.list()[0].id

        def baseDs = BaseDatasource.add(name:AolConnector.getAolDatasourceName(updateParams.name))
        assertFalse(baseDs.hasErrors())


        IntegrationTestUtils.resetController(controller);
        updateParams.each{ key , val ->
            controller.params[key] = val;
        }
        controller.update();

        def model = controller.modelAndView.model;
        def datasource = model.aolDatasource;

        assertTrue(datasource.hasErrors())
        assertEquals("default.not.unique.message", datasource.errors.allErrors[0].code)

        assertEquals(1, AolConnector.count())
        assertEquals(1, AolConnection.count())
        assertEquals(1, AolDatasource.count())


        assertNull(AolConnector.get(name:updateParams.name))
        def aolConnector = AolConnector.get(name:params.name);
        assertNotNull(aolConnector)
        assertEquals(params.host, aolConnector.ds.connection.host)
        assertEquals(AolConnector.getAolDatasourceName(params.name), aolConnector.ds.name)

    }

    public void testDeleteSuccessfully()
    {
        def params=[:]
        params.putAll(connectorParams)


        def controller = new AolConnectorController();
        params.each{ key , val ->
           controller.params[key] = val;
        }

        controller.save();

        assertEquals(1, AolConnector.count())
        assertEquals(1, AolConnection.count())
        assertEquals(1, AolDatasource.count())


        IntegrationTestUtils.resetController (controller);

        def existingConnector=AolConnector.list()[0];

        controller.params["id"]=existingConnector.id.toString();

        controller.delete();

        assertEquals("/aolConnector/list", controller.response.redirectedUrl);


        assertEquals(0, AolConnector.count())
        assertEquals(0, AolConnection.count())
        assertEquals(0, AolDatasource.count())

    }

    public void testDeleteGeneratesErrorMessageWhenGroupNotFound()
    {
        def controller=new AolConnectorController();
        def objectId="noobject"
        controller.params["id"]=controller;

        controller.delete();

        assertEquals("AolConnector not found with id ${controller}",controller.flash.message);
        assertEquals("/aolConnector/list", controller.response.redirectedUrl);
    }

    private def addConnectorForTestConnection()
    {
        CommonTestUtils.initializeFromFile("RCMDBTest.properties");
        ConnectionParam connParam = ConnectionTestUtils.getAolConnectionParam();
        def params=connParam.otherParams;

        def connectorSaveParams = [name: "testConnector", host: params[AolConnectionImpl.HOST], port: params[AolConnectionImpl.PORT],
                username: params[AolConnectionImpl.USERNAME], userPassword: params[AolConnectionImpl.PASSWORD]];

        def createdObjects=AolConnector.addConnector(connectorSaveParams);
        createdObjects.each{ objectName,object ->
                println object.errors
        };

        assertEquals(1, AolConnector.count())
        assertEquals(1, AolConnection.count())
        assertEquals(1, AolDatasource.count())

        return createdObjects.aolConnector;
    }
    public void testTestConnectionSuccessfullyAndWithException()
    {
        def connector=addConnectorForTestConnection();

        def controller=new AolConnectorController();

        controller.params["id"]=connector.id.toString();
        controller.testConnection();

        assertEquals("Successfully connected to server.",controller.flash.message);
        assertEquals("/aolConnector/list", controller.response.redirectedUrl);


        //with exception

        connector.ds.connection.update(username:"ssssssssssssss");
        IntegrationTestUtils.resetController (controller);

        controller.params["id"]=connector.id.toString();
        controller.testConnection();

        assertEquals(1,controller.flash.errors.getAllErrors().size())
        assertEquals("connection.test.exception",controller.flash.errors.getAllErrors()[0].code);
        assertEquals("/aolConnector/list", controller.response.redirectedUrl);
    }

}
