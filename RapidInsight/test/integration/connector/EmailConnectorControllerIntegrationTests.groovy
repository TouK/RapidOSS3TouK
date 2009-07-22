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

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 12, 2009
* Time: 6:14:12 PM
* To change this template use File | Settings | File Templates.
*/
class EmailConnectorControllerIntegrationTests  extends RapidCmdbIntegrationTestCase{
    static transactional = false;
    def connectorParams=[:]
    public void setUp() {
        super.setUp();
        Connection.removeAll();
        BaseDatasource.removeAll();
        EmailConnector.removeAll();
        EmailConnection.removeAll();
        EmailDatasource.removeAll();
        connectorParams.clear();
        connectorParams["name"] = "testConnector";
        connectorParams["smtpHost"] ="192.168.1.100";
        connectorParams["smtpPort"] = 25;
        connectorParams["username"] = "testaccount";
        connectorParams["userPassword"] = "3600";
        connectorParams["protocol"] = EmailConnection.SMTP;
    }

    public void tearDown() {
        super.tearDown();
        
    }

    public void testAddSuccessfuly()
    {
        def params=[:]
        params.putAll(connectorParams)


        def controller = new EmailConnectorController();
        params.each{ key , val ->
            controller.params[key] = val;                
        }
        
        controller.save();

        def emailConnectors = EmailConnector.list();
        assertEquals(1, emailConnectors.size());
        EmailConnector emailConnector = emailConnectors[0]
        assertEquals(params.name, emailConnector.name);

        assertEquals(String.valueOf(controller.response.redirectedUrl),"/emailConnector/show/${emailConnector.id}");

        

        def emailConnections=EmailConnection.list();
        assertEquals(1,emailConnections.size());
        def emailConnection=emailConnections[0];

        assertEquals(EmailConnector.getEmailConnectionName(emailConnector.name),emailConnection.name);
        assertEquals(emailConnection.id,emailConnector.ds.connection.id);
        assertEquals (1,emailConnection.emailDatasources.size());

        def paramsToCheck=[:]
        paramsToCheck.putAll(params)
        paramsToCheck.remove("name");
        
        paramsToCheck.each{ key , val ->
            assertEquals(val,emailConnection[key])
        }

        def emailDatasources = EmailDatasource.list();
        assertEquals (1, emailDatasources.size());
        EmailDatasource emailDatasource = emailDatasources[0];

        assertEquals (EmailConnector.getEmailDatasourceName(emailConnector.name),emailDatasource.name);
        assertEquals (emailDatasource.id, emailConnector.ds.id);
        assertEquals (emailConnection.id,emailDatasource.connection.id);
        assertEquals (0, emailDatasource.reconnectInterval);
        assertEquals (emailConnection.emailDatasources[0].id, emailDatasource.id);
    }
    void testAddRollsBackIfAnyModelHasErrors()
    {
        def params=[:]
        params.putAll(connectorParams)

        def datasource = BaseDatasource.add(name:EmailConnector.getEmailDatasourceName(params.name))
        assertFalse(datasource.hasErrors())

        def controller = new EmailConnectorController();
            params.each{ key , val ->
            controller.params[key] = val;
        }

        controller.save();
        def connector=EmailConnector.list()[0]
        assertEquals(0, EmailConnector.count())
        assertEquals(0, EmailConnection.count())
        assertEquals(0, EmailDatasource.count())


        def model = controller.modelAndView.model;
        def ds = model.emailDatasource;
        assertTrue(ds.hasErrors())
        assertEquals("rapidcmdb.instance.already.exist", ds.errors.allErrors[0].code)
    }

     public void testUpdateSuccessfully()
    {
        def oldParams=[:]
        oldParams.putAll(connectorParams)

        def controller = new EmailConnectorController();
        oldParams.each{ key , val ->
            controller.params[key] = val;
        }

        controller.save();
        
        def oldEmailConnectors = EmailConnector.list();
        assertEquals(1, oldEmailConnectors.size());
        EmailConnector oldEmailConnector = oldEmailConnectors[0]
        assertEquals(oldParams.name, oldEmailConnector.name);

        assertEquals(String.valueOf(controller.response.redirectedUrl),"/emailConnector/show/${oldEmailConnector.id}")


        
        IntegrationTestUtils.resetController (controller);

        def updateParams=[:]
        updateParams["name"] = "testConnector2";
        updateParams["smtpHost"] ="192.168.1.101";
        updateParams["smtpPort"] = 26;
        updateParams["username"] = "testaccoun2t";
        updateParams["userPassword"] = "13600";
        updateParams["protocol"] = EmailConnection.SMTPS;
        updateParams["id"]=oldEmailConnector.id

        controller = new EmailConnectorController();
        updateParams.each{ key , val ->
            controller.params[key] = val;
        }
        controller.update();

        def emailConnectors = EmailConnector.list();
        assertEquals(1, emailConnectors.size());
        EmailConnector emailConnector = emailConnectors[0]
        assertEquals(updateParams.name, emailConnector.name);

        assertEquals(String.valueOf(controller.response.redirectedUrl),"/emailConnector/show/${emailConnector.id}")


        
        def emailConnections=EmailConnection.list();
        assertEquals(1,emailConnections.size());
        def emailConnection=emailConnections[0];

        assertEquals(EmailConnector.getEmailConnectionName(emailConnector.name),emailConnection.name);
        assertEquals(emailConnection.id,emailConnector.ds.connection.id);
        assertEquals ( 1,emailConnection.emailDatasources.size());

        def paramsToCheck=[:]
        paramsToCheck.putAll(updateParams)
        paramsToCheck.remove("id");
        
        paramsToCheck.each{ key , val ->
            assertEquals(val,emailConnection[key])    
        }

        def emailDatasources = EmailDatasource.list();
        assertEquals (1, emailDatasources.size());
        EmailDatasource emailDatasource = emailDatasources[0];

        assertEquals (EmailConnector.getEmailDatasourceName(emailConnector.name),emailDatasource.name);
        assertEquals (emailDatasource.id, emailConnector.ds.id);
        assertEquals (emailConnection.id,emailDatasource.connection.id);
        assertEquals (0, emailDatasource.reconnectInterval);
        assertEquals (emailConnection.emailDatasources[0].id, emailDatasource.id);
        

    }
    public void testUpdateGeneratesErrorMessageWhenEmailConnectorNotFound()
    {
        def controller=new EmailConnectorController();
        
        def objectId="noobject"
        controller.params["id"]=objectId;

        controller.update();

        assertEquals("EmailConnector not found with id ${objectId}",controller.flash.message);
        assertEquals("/emailConnector/edit/${objectId}", controller.response.redirectedUrl);

    }


    void testUpdateRollsBackIfAnyModelHasErrors(){
        def params=[:]
        params.putAll(connectorParams)


        def controller = new EmailConnectorController();
        params.each{ key , val ->
            controller.params[key] = val;
        }

        controller.save();

        assertEquals(1, EmailConnector.count())
        assertEquals(1, EmailConnection.count())
        assertEquals(1, EmailDatasource.count())

        def updateParams=[:]
        updateParams.putAll(params)
        updateParams["name"]="newEmailConnector"
        updateParams["smtpHost"] ="192.168.1.101";
        updateParams["id"]=EmailConnector.list()[0].id

        def baseDs = BaseDatasource.add(name:EmailConnector.getEmailDatasourceName(updateParams.name))
        assertFalse(baseDs.hasErrors())


        IntegrationTestUtils.resetController(controller);
        updateParams.each{ key , val ->
            controller.params[key] = val;
        }
        controller.update();

        def model = controller.modelAndView.model;
        def datasource = model.emailDatasource;

        assertTrue(datasource.hasErrors())
        assertEquals("default.not.unique.message", datasource.errors.allErrors[0].code)

        assertEquals(1, EmailConnector.count())
        assertEquals(1, EmailConnection.count())
        assertEquals(1, EmailDatasource.count())


        assertNull(EmailConnector.get(name:updateParams.name))
        def emailConnector = EmailConnector.get(name:params.name);
        assertNotNull(emailConnector)
        assertEquals(params.smtpHost, emailConnector.ds.connection.smtpHost)
        assertEquals(EmailConnector.getEmailDatasourceName(params.name), emailConnector.ds.name)

    }

    public void testDeleteSuccessfully()
    {
        def params=[:]
        params.putAll(connectorParams)


        def controller = new EmailConnectorController();
        params.each{ key , val ->
           controller.params[key] = val;
        }

        controller.save();

        assertEquals(1, EmailConnector.count())
        assertEquals(1, EmailConnection.count())
        assertEquals(1, EmailDatasource.count())


        IntegrationTestUtils.resetController (controller);

        def existingConnector=EmailConnector.list()[0];

        controller.params["id"]=existingConnector.id.toString();

        controller.delete();

        assertEquals("/emailConnector/list", controller.response.redirectedUrl);
        

        assertEquals(0, EmailConnector.count())
        assertEquals(0, EmailConnection.count())
        assertEquals(0, EmailDatasource.count())

    }

    public void testDeleteGeneratesErrorMessageWhenGroupNotFound()
    {
        def controller=new EmailConnectorController();
        def objectId="noobject"
        controller.params["id"]=controller;

        controller.delete();

        assertEquals("EmailConnector not found with id ${controller}",controller.flash.message);
        assertEquals("/emailConnector/list", controller.response.redirectedUrl);
    }

    private def addConnectorForTestConnection()
    {
        CommonTestUtils.initializeFromFile("RCMDBTest.properties");
        def params = EmailConnectionImplTestUtils.getSmtpConnectionParams("User1");

        def connectorSaveParams = [name: "testConnector", smtpHost: params[EmailConnectionImpl.SMTPHOST], smtpPort: params[EmailConnectionImpl.SMTPPORT],
                username: params[EmailConnectionImpl.USERNAME], userPassword: params[EmailConnectionImpl.PASSWORD], protocol: EmailConnection.SMTP];

        def createdObjects=EmailConnector.addConnector(connectorSaveParams);
        createdObjects.each{ objectName,object ->
                println object.errors 
        };

        assertEquals(1, EmailConnector.count())
        assertEquals(1, EmailConnection.count())
        assertEquals(1, EmailDatasource.count())

        return createdObjects.emailConnector;
    }
    public void testTestConnectionSuccessfullyAndWithException()
    {
        def connector=addConnectorForTestConnection();

        def controller=new EmailConnectorController();

        controller.params["id"]=connector.id.toString();
        controller.testConnection();
        
        assertEquals("Successfully connected to server.",controller.flash.message);
        assertEquals("/emailConnector/list", controller.response.redirectedUrl);


        //with exception

        connector.ds.connection.update(username:"ssssssssssssss");
        IntegrationTestUtils.resetController (controller);

        controller.params["id"]=connector.id.toString();
        controller.testConnection();

        assertEquals(1,controller.flash.errors.getAllErrors().size())
        assertEquals("connection.test.exception",controller.flash.errors.getAllErrors()[0].code);
        assertEquals("/emailConnector/list", controller.response.redirectedUrl);
    }
   
}