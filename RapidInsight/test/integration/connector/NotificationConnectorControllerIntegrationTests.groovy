package connector

import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import connection.EmailConnection
import datasource.BaseDatasource
import datasource.EmailDatasource
import com.ifountain.rcmdb.test.util.IntegrationTestUtils
import connection.Connection
import com.ifountain.rcmdb.test.util.EmailConnectionImplTestUtils
import com.ifountain.comp.test.util.CommonTestUtils
import connection.EmailConnectionImpl
import org.apache.log4j.Level
import script.CmdbScript

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 12, 2009
* Time: 6:14:12 PM
* To change this template use File | Settings | File Templates.
*/
class NotificationConnectorControllerIntegrationTests  extends RapidCmdbIntegrationTestCase{
    static transactional = false;
    def connectorParams=[:]
    public void setUp() {
        super.setUp();
        //NotificationConnector.reloadOperations();
        Connection.removeAll();
        BaseDatasource.removeAll();
        NotificationConnector.removeAll();
        EmailConnection.removeAll();
        EmailDatasource.removeAll();
        CmdbScript.removeAll();
        connectorParams.clear();
        connectorParams["name"] = "testConnector";
        connectorParams["smtpHost"] ="192.168.1.100";
        connectorParams["smtpPort"] = 25;
        connectorParams["username"] = "testaccount";
        connectorParams["userPassword"] = "3600";
        connectorParams["protocol"] = EmailConnection.SMTP;
        connectorParams["logLevel"] = Level.INFO.toString();
        connectorParams["scriptFile"] = "emailSender";
        connectorParams["type"] = "Email";
        connectorParams["period"] = 70;

    }

    public void tearDown() {
        super.tearDown();

    }

    public void testAddSuccessfuly()
    {
        def params=[:]
        params.putAll(connectorParams)


        def controller = new NotificationConnectorController();
        params.each{ key , val ->
            controller.params[key] = val;
        }

        controller.save();

        def connectors = NotificationConnector.list();
        assertEquals(1, connectors.size());
        NotificationConnector connector = connectors[0]
        assertEquals(params.name, connector.name);
        assertEquals(params.type, connector.type);

        assertEquals(String.valueOf(controller.response.redirectedUrl),"/notificationConnector/show/${connector.id}");



        def connections=EmailConnection.list();
        assertEquals(1,connections.size());
        def connection=connections[0];

        assertEquals(NotificationConnector.getConnectionName(connector.name),connection.name);
        assertEquals(connection.id,connector.ds.connection.id);
        assertEquals (1,connection.emailDatasources.size());

        def paramsToCheck=[:]
        paramsToCheck.putAll(params)
        paramsToCheck.remove("name");
        paramsToCheck.remove("logLevel");
        paramsToCheck.remove("scriptFile");
        paramsToCheck.remove("type");

        paramsToCheck.each{ key , val ->
            assertEquals(val,connection[key])
        }

        def datasources = EmailDatasource.list();
        assertEquals (1, datasources.size());
        def datasource = datasources[0];

        assertEquals (NotificationConnector.getDatasourceName(connector.name),datasource.name);
        assertEquals (datasource.id, connector.ds.id);
        assertEquals (connection.id,datasource.connection.id);
        assertEquals (0, datasource.reconnectInterval);
        assertEquals (connection.emailDatasources[0].id, datasource.id);


        assertEquals(1,CmdbScript.countHits("name:${NotificationConnector.getScriptName(connector.name)}"));
    }
    void testAddRollsBackIfAnyModelHasErrors()
    {
        def params=[:]
        params.putAll(connectorParams)

        def datasource = BaseDatasource.add(name:NotificationConnector.getDatasourceName(params.name))
        assertFalse(datasource.hasErrors())

        def controller = new NotificationConnectorController();
            params.each{ key , val ->
            controller.params[key] = val;
        }

        controller.save();

        assertEquals(0, NotificationConnector.count())
        assertEquals(0, EmailConnection.count())
        assertEquals(0, EmailDatasource.count())
        assertEquals(0, CmdbScript.count())


        def model = controller.modelAndView.model;
        def ds = model.datasource;
        assertTrue(ds.hasErrors())
        assertEquals("rapidcmdb.instance.already.exist", ds.errors.allErrors[0].code)
    }

     public void testUpdateSuccessfully()
    {
        def oldParams=[:]
        oldParams.putAll(connectorParams)

        def controller = new NotificationConnectorController();
        oldParams.each{ key , val ->
            controller.params[key] = val;
        }

        controller.save();

        def oldConnectors = NotificationConnector.list();
        assertEquals(1, oldConnectors.size());
        NotificationConnector oldConnector = oldConnectors[0]
        assertEquals(oldParams.name, oldConnector.name);

        assertEquals(String.valueOf(controller.response.redirectedUrl),"/notificationConnector/show/${oldConnector.id}")

        IntegrationTestUtils.resetController (controller);

        def updateParams=[:]
        updateParams["name"] = "testConnector2";
        updateParams["smtpHost"] ="192.168.1.101";
        updateParams["smtpPort"] = 26;
        updateParams["username"] = "testaccoun2t";
        updateParams["userPassword"] = "13600";
        updateParams["protocol"] = EmailConnection.SMTPS;
        updateParams["id"]=oldConnector.id
        updateParams["logLevel"] = Level.DEBUG.toString();
        updateParams["scriptFile"] = "emailSender";
        updateParams["period"] = 70;


        controller = new NotificationConnectorController();
        updateParams.each{ key , val ->
            controller.params[key] = val;
        }
        controller.update();

        def connectors = NotificationConnector.list();
        assertEquals(1, connectors.size());
        NotificationConnector connector = connectors[0]
        assertEquals(updateParams.name, connector.name);

        assertEquals(String.valueOf(controller.response.redirectedUrl),"/notificationConnector/show/${connector.id}")



        def connections=EmailConnection.list();
        assertEquals(1,connections.size());
        def connection=connections[0];

        assertEquals(NotificationConnector.getConnectionName(connector.name),connection.name);
        assertEquals(connection.id,connector.ds.connection.id);
        assertEquals ( 1,connection.emailDatasources.size());

        def paramsToCheck=[:]
        paramsToCheck.putAll(updateParams)
        paramsToCheck.remove("id");
        paramsToCheck.remove("logLevel");
        paramsToCheck.remove("scriptFile");

        paramsToCheck.each{ key , val ->
            assertEquals(val,connection[key])
        }

        def datasources = EmailDatasource.list();
        assertEquals (1, datasources.size());
        def datasource = datasources[0];

        assertEquals (NotificationConnector.getDatasourceName(connector.name),datasource.name);
        assertEquals (datasource.id, connector.ds.id);
        assertEquals (connection.id,datasource.connection.id);
        assertEquals (0, datasource.reconnectInterval);
        assertEquals (connection.emailDatasources[0].id, datasource.id);

        assertEquals(1,CmdbScript.countHits("name:${NotificationConnector.getScriptName(connector.name)}"));
    }
    public void testUpdateGeneratesErrorMessageWhenConnectorNotFound()
    {
        def controller=new NotificationConnectorController();

        def objectId="noobject"
        controller.params["id"]=objectId;

        controller.update();

        assertEquals("NotificationConnector not found with id ${objectId}",controller.flash.message);
        assertEquals("/notificationConnector/edit/${objectId}", controller.response.redirectedUrl);

    }


    void testUpdateRollsBackIfAnyModelHasErrors(){
        def params=[:]
        params.putAll(connectorParams)


        def controller = new NotificationConnectorController();
        params.each{ key , val ->
            controller.params[key] = val;
        }

        controller.save();

        assertEquals(1, NotificationConnector.count())
        assertEquals(1, EmailConnection.count())
        assertEquals(1, EmailDatasource.count())
        assertEquals(1, CmdbScript.count())

        def updateParams=[:]
        updateParams.putAll(params)
        updateParams["name"]="newConnector"
        updateParams["smtpHost"] ="192.168.1.101";
        updateParams["id"]=NotificationConnector.list()[0].id

        def baseDs = BaseDatasource.add(name:NotificationConnector.getDatasourceName(updateParams.name))
        assertFalse(baseDs.hasErrors())


        IntegrationTestUtils.resetController(controller);
        updateParams.each{ key , val ->
            controller.params[key] = val;
        }
        controller.update();

        def model = controller.modelAndView.model;
        def datasource = model.datasource;

        assertTrue(datasource.hasErrors())
        assertEquals("default.not.unique.message", datasource.errors.allErrors[0].code)

        assertEquals(1, NotificationConnector.count())
        assertEquals(1, EmailConnection.count())
        assertEquals(1, EmailDatasource.count())
        assertEquals(1, CmdbScript.count())


        assertNull(NotificationConnector.get(name:updateParams.name))
        def connector = NotificationConnector.get(name:params.name);
        assertNotNull(connector)
        assertEquals(params.smtpHost, connector.ds.connection.smtpHost)
        assertEquals(NotificationConnector.getDatasourceName(params.name), connector.ds.name)

    }

    public void testDeleteSuccessfully()
    {
        def params=[:]
        params.putAll(connectorParams)


        def controller = new NotificationConnectorController();
        params.each{ key , val ->
           controller.params[key] = val;
        }

        controller.save();

        assertEquals(1, NotificationConnector.count())
        assertEquals(1, EmailConnection.count())
        assertEquals(1, EmailDatasource.count())
        assertEquals(1, CmdbScript.count())


        IntegrationTestUtils.resetController (controller);

        def existingConnector=NotificationConnector.list()[0];

        controller.params["id"]=existingConnector.id.toString();

        controller.delete();

        assertEquals("/notificationConnector/list", controller.response.redirectedUrl);


        assertEquals(0, NotificationConnector.count())
        assertEquals(0, EmailConnection.count())
        assertEquals(0, EmailDatasource.count())
        assertEquals(0, CmdbScript.count())

    }

    public void testDeleteGeneratesErrorMessageWhenGroupNotFound()
    {
        def controller=new NotificationConnectorController();
        def objectId="noobject"
        controller.params["id"]=controller;

        controller.delete();

        assertEquals("NotificationConnector not found with id ${controller}",controller.flash.message);
        assertEquals("/notificationConnector/list", controller.response.redirectedUrl);
    }

    private def addConnectorForTestConnection()
    {
        CommonTestUtils.initializeFromFile("RCMDBTest.properties");
        def params = EmailConnectionImplTestUtils.getSmtpConnectionParams("User1");

        def connectorSaveParams = [name: "testConnector", smtpHost: params[EmailConnectionImpl.SMTPHOST], smtpPort: params[EmailConnectionImpl.SMTPPORT],
                username: params[EmailConnectionImpl.USERNAME], userPassword: params[EmailConnectionImpl.PASSWORD], protocol: EmailConnection.SMTP,
                logLevel : Level.INFO.toString(),scriptFile: "emailSender",type:"Email"];

        def createdObjects=NotificationConnector.addConnector(connectorSaveParams);
        createdObjects.each{ objectName,object ->
                println object.errors
        };

        assertEquals(1, NotificationConnector.count())
        assertEquals(1, EmailConnection.count())
        assertEquals(1, EmailDatasource.count())
        assertEquals(1, CmdbScript.count())

        return createdObjects.connector;
    }
    public void testTestConnectionSuccessfullyAndWithException()
    {
        def connector=addConnectorForTestConnection();

        def controller=new NotificationConnectorController();

        controller.params["id"]=connector.id.toString();
        controller.testConnection();

        assertEquals("Successfully connected to server.",controller.flash.message);
        assertEquals("/notificationConnector/list", controller.response.redirectedUrl);


        //with exception

        connector.ds.connection.update(username:"ssssssssssssss");
        IntegrationTestUtils.resetController (controller);

        controller.params["id"]=connector.id.toString();
        controller.testConnection();

        assertEquals(1,controller.flash.errors.getAllErrors().size())
        assertEquals("connection.test.exception",controller.flash.errors.getAllErrors()[0].code);
        assertEquals("/notificationConnector/list", controller.response.redirectedUrl);
    }



}