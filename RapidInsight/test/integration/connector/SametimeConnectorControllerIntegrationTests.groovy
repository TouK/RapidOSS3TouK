package connector

import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import connection.SametimeConnection
import datasource.BaseDatasource
import datasource.SametimeDatasource
import com.ifountain.rcmdb.test.util.IntegrationTestUtils
import connection.Connection
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.rcmdb.sametime.connection.SametimeConnectionImpl
import com.ifountain.rcmdb.test.util.ConnectionTestUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 12, 2009
* Time: 6:14:12 PM
* To change this template use File | Settings | File Templates.
*/
class SametimeConnectorControllerIntegrationTests  extends RapidCmdbIntegrationTestCase{
    static transactional = false;
    def connectorParams=[:]
    public void setUp() {
        super.setUp();
        Connection.removeAll();
        BaseDatasource.removeAll();
        SametimeConnector.removeAll();
        SametimeConnection.removeAll();
        SametimeDatasource.removeAll();
        connectorParams.clear();
        connectorParams["name"] = "testConnector";
        connectorParams["host"] ="sametimehost";
        connectorParams["username"] = "testaccount";
        connectorParams["userPassword"] = "3600";
        connectorParams["community"] = "a.com";
    }

    public void tearDown() {
        super.tearDown();

    }

    public void testAddSuccessfuly()
    {
        def params=[:]
        params.putAll(connectorParams)


        def controller = new SametimeConnectorController();
        params.each{ key , val ->
            controller.params[key] = val;
        }

        controller.save();

        def sametimeConnectors = SametimeConnector.list();
        assertEquals(1, sametimeConnectors.size());
        SametimeConnector sametimeConnector = sametimeConnectors[0]
        assertEquals(params.name, sametimeConnector.name);

        assertEquals(String.valueOf(controller.response.redirectedUrl),"/sametimeConnector/show/${sametimeConnector.id}");



        def sametimeConnections=SametimeConnection.list();
        assertEquals(1,sametimeConnections.size());
        def sametimeConnection=sametimeConnections[0];

        assertEquals(SametimeConnector.getSametimeConnectionName(sametimeConnector.name),sametimeConnection.name);
        assertEquals(sametimeConnection.id,sametimeConnector.ds.connection.id);
        assertEquals (1,sametimeConnection.sametimeDatasources.size());

        def paramsToCheck=[:]
        paramsToCheck.putAll(params)
        paramsToCheck.remove("name");

        paramsToCheck.each{ key , val ->
            assertEquals(val,sametimeConnection[key])
        }

        def sametimeDatasources = SametimeDatasource.list();
        assertEquals (1, sametimeDatasources.size());
        SametimeDatasource sametimeDatasource = sametimeDatasources[0];

        assertEquals (SametimeConnector.getSametimeDatasourceName(sametimeConnector.name),sametimeDatasource.name);
        assertEquals (sametimeDatasource.id, sametimeConnector.ds.id);
        assertEquals (sametimeConnection.id,sametimeDatasource.connection.id);
        assertEquals (0, sametimeDatasource.reconnectInterval);
        assertEquals (sametimeConnection.sametimeDatasources[0].id, sametimeDatasource.id);
    }
    void testAddRollsBackIfAnyModelHasErrors()
    {
        def params=[:]
        params.putAll(connectorParams)

        def datasource = BaseDatasource.add(name:SametimeConnector.getSametimeDatasourceName(params.name))
        assertFalse(datasource.hasErrors())

        def controller = new SametimeConnectorController();
            params.each{ key , val ->
            controller.params[key] = val;
        }

        controller.save();
        def connector=SametimeConnector.list()[0]
        assertEquals(0, SametimeConnector.count())
        assertEquals(0, SametimeConnection.count())
        assertEquals(0, SametimeDatasource.count())


        def model = controller.modelAndView.model;
        def ds = model.sametimeDatasource;
        assertTrue(ds.hasErrors())
        assertEquals("rapidcmdb.instance.already.exist", ds.errors.allErrors[0].code)
    }

     public void testUpdateSuccessfully()
    {
        def oldParams=[:]
        oldParams.putAll(connectorParams)

        def controller = new SametimeConnectorController();
        oldParams.each{ key , val ->
            controller.params[key] = val;
        }

        controller.save();

        def oldSametimeConnectors = SametimeConnector.list();
        assertEquals(1, oldSametimeConnectors.size());
        SametimeConnector oldSametimeConnector = oldSametimeConnectors[0]
        assertEquals(oldParams.name, oldSametimeConnector.name);

        assertEquals(String.valueOf(controller.response.redirectedUrl),"/sametimeConnector/show/${oldSametimeConnector.id}")



        IntegrationTestUtils.resetController (controller);

        def updateParams=[:]
        updateParams["name"] = "testConnector2";
        updateParams["host"] ="sametimehost2";
        updateParams["username"] = "testaccoun2t";
        updateParams["userPassword"] = "13600";
        updateParams["community"] = "b.com";
        updateParams["id"]=oldSametimeConnector.id

        controller = new SametimeConnectorController();
        updateParams.each{ key , val ->
            controller.params[key] = val;
        }
        controller.update();

        def sametimeConnectors = SametimeConnector.list();
        assertEquals(1, sametimeConnectors.size());
        SametimeConnector sametimeConnector = sametimeConnectors[0]
        assertEquals(updateParams.name, sametimeConnector.name);

        assertEquals(String.valueOf(controller.response.redirectedUrl),"/sametimeConnector/show/${sametimeConnector.id}")



        def sametimeConnections=SametimeConnection.list();
        assertEquals(1,sametimeConnections.size());
        def sametimeConnection=sametimeConnections[0];

        assertEquals(SametimeConnector.getSametimeConnectionName(sametimeConnector.name),sametimeConnection.name);
        assertEquals(sametimeConnection.id,sametimeConnector.ds.connection.id);
        assertEquals ( 1,sametimeConnection.sametimeDatasources.size());

        def paramsToCheck=[:]
        paramsToCheck.putAll(updateParams)
        paramsToCheck.remove("id");

        paramsToCheck.each{ key , val ->
            assertEquals(val,sametimeConnection[key])
        }

        def sametimeDatasources = SametimeDatasource.list();
        assertEquals (1, sametimeDatasources.size());
        SametimeDatasource sametimeDatasource = sametimeDatasources[0];

        assertEquals (SametimeConnector.getSametimeDatasourceName(sametimeConnector.name),sametimeDatasource.name);
        assertEquals (sametimeDatasource.id, sametimeConnector.ds.id);
        assertEquals (sametimeConnection.id,sametimeDatasource.connection.id);
        assertEquals (0, sametimeDatasource.reconnectInterval);
        assertEquals (sametimeConnection.sametimeDatasources[0].id, sametimeDatasource.id);


    }
    public void testUpdateGeneratesErrorMessageWhenSametimeConnectorNotFound()
    {
        def controller=new SametimeConnectorController();

        def objectId="noobject"
        controller.params["id"]=objectId;

        controller.update();

        assertEquals("SametimeConnector not found with id ${objectId}",controller.flash.message);
        assertEquals("/sametimeConnector/edit/${objectId}", controller.response.redirectedUrl);

    }


    void testUpdateRollsBackIfAnyModelHasErrors(){
        def params=[:]
        params.putAll(connectorParams)


        def controller = new SametimeConnectorController();
        params.each{ key , val ->
            controller.params[key] = val;
        }

        controller.save();

        assertEquals(1, SametimeConnector.count())
        assertEquals(1, SametimeConnection.count())
        assertEquals(1, SametimeDatasource.count())

        def updateParams=[:]
        updateParams.putAll(params)
        updateParams["name"]="newSametimeConnector"
        updateParams["host"] ="192.168.1.101";
        updateParams["id"]=SametimeConnector.list()[0].id

        def baseDs = BaseDatasource.add(name:SametimeConnector.getSametimeDatasourceName(updateParams.name))
        assertFalse(baseDs.hasErrors())


        IntegrationTestUtils.resetController(controller);
        updateParams.each{ key , val ->
            controller.params[key] = val;
        }
        controller.update();

        def model = controller.modelAndView.model;
        def datasource = model.sametimeDatasource;

        assertTrue(datasource.hasErrors())
        assertEquals("default.not.unique.message", datasource.errors.allErrors[0].code)

        assertEquals(1, SametimeConnector.count())
        assertEquals(1, SametimeConnection.count())
        assertEquals(1, SametimeDatasource.count())


        assertNull(SametimeConnector.get(name:updateParams.name))
        def sametimeConnector = SametimeConnector.get(name:params.name);
        assertNotNull(sametimeConnector)
        assertEquals(params.host, sametimeConnector.ds.connection.host)
        assertEquals(SametimeConnector.getSametimeDatasourceName(params.name), sametimeConnector.ds.name)

    }

    public void testDeleteSuccessfully()
    {
        def params=[:]
        params.putAll(connectorParams)


        def controller = new SametimeConnectorController();
        params.each{ key , val ->
           controller.params[key] = val;
        }

        controller.save();

        assertEquals(1, SametimeConnector.count())
        assertEquals(1, SametimeConnection.count())
        assertEquals(1, SametimeDatasource.count())


        IntegrationTestUtils.resetController (controller);

        def existingConnector=SametimeConnector.list()[0];

        controller.params["id"]=existingConnector.id.toString();

        controller.delete();

        assertEquals("/sametimeConnector/list", controller.response.redirectedUrl);


        assertEquals(0, SametimeConnector.count())
        assertEquals(0, SametimeConnection.count())
        assertEquals(0, SametimeDatasource.count())

    }

    public void testDeleteGeneratesErrorMessageWhenGroupNotFound()
    {
        def controller=new SametimeConnectorController();
        def objectId="noobject"
        controller.params["id"]=controller;

        controller.delete();

        assertEquals("SametimeConnector not found with id ${controller}",controller.flash.message);
        assertEquals("/sametimeConnector/list", controller.response.redirectedUrl);
    }

    private def addConnectorForTestConnection()
    {
        CommonTestUtils.initializeFromFile("RCMDBTest.properties");
        ConnectionParam connParam = ConnectionTestUtils.getSametimeConnectionParam();
        def params=connParam.otherParams;

        def connectorSaveParams = [name: "testConnector", host: params[SametimeConnectionImpl.HOST], 
                username: params[SametimeConnectionImpl.USERNAME], userPassword: params[SametimeConnectionImpl.PASSWORD], community: params[SametimeConnectionImpl.COMMUNITY]];

        def createdObjects=SametimeConnector.addConnector(connectorSaveParams);
        createdObjects.each{ objectName,object ->
                println object.errors
        };

        assertEquals(1, SametimeConnector.count())
        assertEquals(1, SametimeConnection.count())
        assertEquals(1, SametimeDatasource.count())

        return createdObjects.sametimeConnector;
    }
    public void testTestConnectionSuccessfullyAndWithException()
    {
        def connector=addConnectorForTestConnection();

        def controller=new SametimeConnectorController();

        controller.params["id"]=connector.id.toString();
        controller.testConnection();

        assertEquals("Successfully connected to server.",controller.flash.message);
        assertEquals("/sametimeConnector/list", controller.response.redirectedUrl);


        //with exception

        connector.ds.connection.update(username:"ssssssssssssss");
        IntegrationTestUtils.resetController (controller);

        controller.params["id"]=connector.id.toString();
        controller.testConnection();

        assertEquals(1,controller.flash.errors.getAllErrors().size())
        assertEquals("connection.test.exception",controller.flash.errors.getAllErrors()[0].code);
        assertEquals("/sametimeConnector/list", controller.response.redirectedUrl);
    }

}
