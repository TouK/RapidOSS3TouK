package connector

import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import connection.SmsConnection
import datasource.BaseDatasource
import datasource.SmsDatasource
import com.ifountain.rcmdb.test.util.IntegrationTestUtils
import connection.Connection
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.core.connection.ConnectionParam

import com.ifountain.rcmdb.test.util.ConnectionTestUtils
import com.ifountain.rcmdb.sms.connection.SmsConnectionImpl

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 12, 2009
* Time: 6:14:12 PM
* To change this template use File | Settings | File Templates.
*/
class SmsConnectorControllerIntegrationTests  extends RapidCmdbIntegrationTestCase{
    static transactional = false;
    def connectorParams=[:]
    public void setUp() {
        super.setUp();
        Connection.removeAll();
        BaseDatasource.removeAll();
        SmsConnector.removeAll();
        SmsConnection.removeAll();
        SmsDatasource.removeAll();
        connectorParams.clear();
        connectorParams["name"] = "testConnector";
        connectorParams["host"] ="smshost";
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


        def controller = new SmsConnectorController();
        params.each{ key , val ->
            controller.params[key] = val;
        }

        controller.save();

        def smsConnectors = SmsConnector.list();
        assertEquals(1, smsConnectors.size());
        SmsConnector smsConnector = smsConnectors[0]
        assertEquals(params.name, smsConnector.name);

        assertEquals(String.valueOf(controller.response.redirectedUrl),"/smsConnector/show/${smsConnector.id}");



        def smsConnections=SmsConnection.list();
        assertEquals(1,smsConnections.size());
        def smsConnection=smsConnections[0];

        assertEquals(SmsConnector.getSmsConnectionName(smsConnector.name),smsConnection.name);
        assertEquals(smsConnection.id,smsConnector.ds.connection.id);
        assertEquals (1,smsConnection.smsDatasources.size());

        def paramsToCheck=[:]
        paramsToCheck.putAll(params)
        paramsToCheck.remove("name");

        paramsToCheck.each{ key , val ->
            assertEquals(val,smsConnection[key])
        }

        def smsDatasources = SmsDatasource.list();
        assertEquals (1, smsDatasources.size());
        SmsDatasource smsDatasource = smsDatasources[0];

        assertEquals (SmsConnector.getSmsDatasourceName(smsConnector.name),smsDatasource.name);
        assertEquals (smsDatasource.id, smsConnector.ds.id);
        assertEquals (smsConnection.id,smsDatasource.connection.id);
        assertEquals (0, smsDatasource.reconnectInterval);
        assertEquals (smsConnection.smsDatasources[0].id, smsDatasource.id);
    }
    void testAddRollsBackIfAnyModelHasErrors()
    {
        def params=[:]
        params.putAll(connectorParams)

        def datasource = BaseDatasource.add(name:SmsConnector.getSmsDatasourceName(params.name))
        assertFalse(datasource.hasErrors())

        def controller = new SmsConnectorController();
            params.each{ key , val ->
            controller.params[key] = val;
        }

        controller.save();
        def connector=SmsConnector.list()[0]
        assertEquals(0, SmsConnector.count())
        assertEquals(0, SmsConnection.count())
        assertEquals(0, SmsDatasource.count())


        def model = controller.modelAndView.model;
        def ds = model.smsDatasource;
        assertTrue(ds.hasErrors())
        assertEquals("rapidcmdb.instance.already.exist", ds.errors.allErrors[0].code)
    }

     public void testUpdateSuccessfully()
    {
        def oldParams=[:]
        oldParams.putAll(connectorParams)

        def controller = new SmsConnectorController();
        oldParams.each{ key , val ->
            controller.params[key] = val;
        }

        controller.save();

        def oldSmsConnectors = SmsConnector.list();
        assertEquals(1, oldSmsConnectors.size());
        SmsConnector oldSmsConnector = oldSmsConnectors[0]
        assertEquals(oldParams.name, oldSmsConnector.name);

        assertEquals(String.valueOf(controller.response.redirectedUrl),"/smsConnector/show/${oldSmsConnector.id}")



        IntegrationTestUtils.resetController (controller);

        def updateParams=[:]
        updateParams["name"] = "testConnector2";
        updateParams["host"] ="smshost2";
        updateParams["port"] = 5000;
        updateParams["username"] = "testaccoun2t";
        updateParams["userPassword"] = "13600";
        updateParams["id"]=oldSmsConnector.id

        controller = new SmsConnectorController();
        updateParams.each{ key , val ->
            controller.params[key] = val;
        }
        controller.update();

        def smsConnectors = SmsConnector.list();
        assertEquals(1, smsConnectors.size());
        SmsConnector smsConnector = smsConnectors[0]
        assertEquals(updateParams.name, smsConnector.name);

        assertEquals(String.valueOf(controller.response.redirectedUrl),"/smsConnector/show/${smsConnector.id}")



        def smsConnections=SmsConnection.list();
        assertEquals(1,smsConnections.size());
        def smsConnection=smsConnections[0];

        assertEquals(SmsConnector.getSmsConnectionName(smsConnector.name),smsConnection.name);
        assertEquals(smsConnection.id,smsConnector.ds.connection.id);
        assertEquals ( 1,smsConnection.smsDatasources.size());

        def paramsToCheck=[:]
        paramsToCheck.putAll(updateParams)
        paramsToCheck.remove("id");

        paramsToCheck.each{ key , val ->
            assertEquals(val,smsConnection[key])
        }

        def smsDatasources = SmsDatasource.list();
        assertEquals (1, smsDatasources.size());
        SmsDatasource smsDatasource = smsDatasources[0];

        assertEquals (SmsConnector.getSmsDatasourceName(smsConnector.name),smsDatasource.name);
        assertEquals (smsDatasource.id, smsConnector.ds.id);
        assertEquals (smsConnection.id,smsDatasource.connection.id);
        assertEquals (0, smsDatasource.reconnectInterval);
        assertEquals (smsConnection.smsDatasources[0].id, smsDatasource.id);


    }
    public void testUpdateGeneratesErrorMessageWhenSmsConnectorNotFound()
    {
        def controller=new SmsConnectorController();

        def objectId="noobject"
        controller.params["id"]=objectId;

        controller.update();

        assertEquals("SmsConnector not found with id ${objectId}",controller.flash.message);
        assertEquals("/smsConnector/edit/${objectId}", controller.response.redirectedUrl);

    }


    void testUpdateRollsBackIfAnyModelHasErrors(){
        def params=[:]
        params.putAll(connectorParams)


        def controller = new SmsConnectorController();
        params.each{ key , val ->
            controller.params[key] = val;
        }

        controller.save();

        assertEquals(1, SmsConnector.count())
        assertEquals(1, SmsConnection.count())
        assertEquals(1, SmsDatasource.count())

        def updateParams=[:]
        updateParams.putAll(params)
        updateParams["name"]="newSmsConnector"
        updateParams["host"] ="192.168.1.101";
        updateParams["id"]=SmsConnector.list()[0].id

        def baseDs = BaseDatasource.add(name:SmsConnector.getSmsDatasourceName(updateParams.name))
        assertFalse(baseDs.hasErrors())


        IntegrationTestUtils.resetController(controller);
        updateParams.each{ key , val ->
            controller.params[key] = val;
        }
        controller.update();

        def model = controller.modelAndView.model;
        def datasource = model.smsDatasource;

        assertTrue(datasource.hasErrors())
        assertEquals("default.not.unique.message", datasource.errors.allErrors[0].code)

        assertEquals(1, SmsConnector.count())
        assertEquals(1, SmsConnection.count())
        assertEquals(1, SmsDatasource.count())


        assertNull(SmsConnector.get(name:updateParams.name))
        def smsConnector = SmsConnector.get(name:params.name);
        assertNotNull(smsConnector)
        assertEquals(params.host, smsConnector.ds.connection.host)
        assertEquals(SmsConnector.getSmsDatasourceName(params.name), smsConnector.ds.name)

    }

    public void testDeleteSuccessfully()
    {
        def params=[:]
        params.putAll(connectorParams)


        def controller = new SmsConnectorController();
        params.each{ key , val ->
           controller.params[key] = val;
        }

        controller.save();

        assertEquals(1, SmsConnector.count())
        assertEquals(1, SmsConnection.count())
        assertEquals(1, SmsDatasource.count())


        IntegrationTestUtils.resetController (controller);

        def existingConnector=SmsConnector.list()[0];

        controller.params["id"]=existingConnector.id.toString();

        controller.delete();

        assertEquals("/smsConnector/list", controller.response.redirectedUrl);


        assertEquals(0, SmsConnector.count())
        assertEquals(0, SmsConnection.count())
        assertEquals(0, SmsDatasource.count())

    }

    public void testDeleteGeneratesErrorMessageWhenGroupNotFound()
    {
        def controller=new SmsConnectorController();
        def objectId="noobject"
        controller.params["id"]=controller;

        controller.delete();

        assertEquals("SmsConnector not found with id ${controller}",controller.flash.message);
        assertEquals("/smsConnector/list", controller.response.redirectedUrl);
    }

    private def addConnectorForTestConnection()
    {
        CommonTestUtils.initializeFromFile("RCMDBTest.properties");
        ConnectionParam connParam = ConnectionTestUtils.getSmsConnectionParam();
        def params=connParam.otherParams;

        def connectorSaveParams = [name: "testConnector", host: params[SmsConnectionImpl.HOST], port: params[SmsConnectionImpl.PORT],
                username: params[SmsConnectionImpl.USERNAME], userPassword: params[SmsConnectionImpl.PASSWORD]];

        def createdObjects=SmsConnector.addConnector(connectorSaveParams);
        createdObjects.each{ objectName,object ->
                println object.errors
        };

        assertEquals(1, SmsConnector.count())
        assertEquals(1, SmsConnection.count())
        assertEquals(1, SmsDatasource.count())

        return createdObjects.smsConnector;
    }
    public void testTestConnectionSuccessfullyAndWithException()
    {
        def connector=addConnectorForTestConnection();

        def controller=new SmsConnectorController();

        controller.params["id"]=connector.id.toString();
        controller.testConnection();

        assertEquals("Successfully connected to server.",controller.flash.message);
        assertEquals("/smsConnector/list", controller.response.redirectedUrl);


        //with exception

        connector.ds.connection.update(username:"ssssssssssssss");
        IntegrationTestUtils.resetController (controller);

        controller.params["id"]=connector.id.toString();
        controller.testConnection();

        assertEquals(1,controller.flash.errors.getAllErrors().size())
        assertEquals("connection.test.exception",controller.flash.errors.getAllErrors()[0].code);
        assertEquals("/smsConnector/list", controller.response.redirectedUrl);
    }

}
