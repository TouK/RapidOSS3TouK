package connector

import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import connection.EmailConnection
import datasource.BaseDatasource
import datasource.EmailDatasource
import com.ifountain.rcmdb.test.util.IntegrationTestUtils
import connection.Connection
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

    public void testSuccessfulSave()
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
        assertEquals(emailConnection.name,EmailConnector.getEmailConnectionName(emailConnector.name));
        assertEquals(emailConnection.id,emailConnector.emailConnection.id);
        assertEquals (emailConnection.emailDatasources.size(), 1);

        def paramsToCheck=[:]
        paramsToCheck.putAll(params)
        paramsToCheck.remove("name");
        
        paramsToCheck.each{ key , val ->
            assertEquals(val,emailConnection[key])
        }

        def emailDatasources = EmailDatasource.list();
        assertEquals (1, emailDatasources.size());
        EmailDatasource emailDatasource = emailDatasources[0];
        assertEquals (emailDatasource.name, EmailConnector.getEmailDatasourceName(emailConnector.name));
        assertEquals (emailDatasource.id, emailConnector.emailDatasource.id);
        assertEquals (emailDatasource.connection.id, emailConnection.id);
        assertEquals (0, emailDatasource.reconnectInterval);
        assertEquals (emailConnection.emailDatasources[0].id, emailDatasource.id);

    }
    void testIfConnectorHasErrorsConnectorIsNotAdded(){
        def params=[:]
        params.putAll(connectorParams)
        params["name"] ="";

        def controller = new EmailConnectorController();
        params.each{ key , val ->
            controller.params[key] = val;
        }

        controller.save();

        assertEquals(0, EmailConnector.count())
        assertEquals(0, EmailConnection.count())
        assertEquals(0, EmailDatasource.count())

        def model = controller.modelAndView.model;
        def conn = model.emailConnector;
        assertTrue(conn.hasErrors())
        assertEquals("blank", conn.errors.allErrors[0].code)
    }
    void testIfConnectionHasErrorsConnectorIsNotAdded(){
        def params=[:]
        params.putAll(connectorParams)
        params["smtpHost"] ="";

        def controller = new EmailConnectorController();
        params.each{ key , val ->
            controller.params[key] = val;
        }

        controller.save();

        assertEquals(0, EmailConnector.count())
        assertEquals(0, EmailConnection.count())
        assertEquals(0, EmailDatasource.count())

        def model = controller.modelAndView.model;
        def conn = model.emailConnection;
        assertTrue(conn.hasErrors())
        assertEquals("blank", conn.errors.allErrors[0].code)
    }
    void testIfDatasourceHasErrorsConnectorIsNotAdded(){
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

     public void testSuccessfulUpdate()
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

        def params=[:]
        params["name"] = "testConnector2";
        params["smtpHost"] ="192.168.1.101";
        params["smtpPort"] = 26;
        params["username"] = "testaccoun2t";
        params["userPassword"] = "13600";
        params["protocol"] = EmailConnection.SMTPS;
        params["id"]=oldEmailConnector.id

        controller = new EmailConnectorController();
        params.each{ key , val ->
            controller.params[key] = val;
        }
        controller.update();

        def emailConnectors = EmailConnector.list();
        assertEquals(1, emailConnectors.size());
        EmailConnector emailConnector = emailConnectors[0]
        assertEquals(params.name, emailConnector.name);

        assertEquals(String.valueOf(controller.response.redirectedUrl),"/emailConnector/show/${emailConnector.id}")


        
        def emailConnections=EmailConnection.list();
        assertEquals(1,emailConnections.size());
        def emailConnection=emailConnections[0];
        assertEquals(emailConnection.name,EmailConnector.getEmailConnectionName(emailConnector.name));
        assertEquals(emailConnection.id,emailConnector.emailConnection.id);
        assertEquals (emailConnection.emailDatasources.size(), 1);

        def paramsToCheck=[:]
        paramsToCheck.putAll(params)
        paramsToCheck.remove("id");
        
        paramsToCheck.each{ key , val ->
            assertEquals(val,emailConnection[key])    
        }

        def emailDatasources = EmailDatasource.list();
        assertEquals (1, emailDatasources.size());
        EmailDatasource emailDatasource = emailDatasources[0];
        assertEquals (emailDatasource.name, EmailConnector.getEmailDatasourceName(emailConnector.name));
        assertEquals (emailDatasource.id, emailConnector.emailDatasource.id);
        assertEquals (emailDatasource.connection.id, emailConnection.id);
        assertEquals (0, emailDatasource.reconnectInterval);
        assertEquals (emailConnection.emailDatasources[0].id, emailDatasource.id);
        

    }
     void testIfConnectionRenameFailsUpdateIsNotExecuted(){
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
        updateParams["id"]=EmailConnector.list()[0].id

        def baseConn = Connection.add(name:updateParams.name)
        assertFalse(baseConn.hasErrors())


        IntegrationTestUtils.resetController(controller);
        updateParams.each{ key , val ->
            controller.params[key] = val;
        }
        controller.update();

        def model = controller.modelAndView.model;
        def conn = model.emailConnection;

        assertTrue(conn.hasErrors())
        assertEquals("default.not.unique.message", conn.errors.allErrors[0].code)

        assertEquals(1, EmailConnector.count())
        assertEquals(1, EmailConnection.count())
        assertEquals(1, EmailDatasource.count())


        assertNull(EmailConnector.get(name:updateParams.name))
        def emailConnector = EmailConnector.get(name:params.name);
        assertNotNull(emailConnector)
        assertEquals(params.smtpHost, emailConnector.emailConnection.smtpHost)
        
    }

    void testIfDatasourceRenameFailsUpdateIsNotExecuted(){
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
        assertEquals(params.smtpHost, emailConnector.emailConnection.smtpHost)
        assertEquals(EmailConnector.getEmailDatasourceName(params.name), emailConnector.emailDatasource.name)

    }
     void testIfConnectorHasErrorsConnectorIsNotUpdated(){
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
        updateParams["smtpHost"] ="192.168.1.101";
        updateParams["smtpPort"] = 26;
        updateParams["username"] = "testaccoun2t";
        updateParams["userPassword"] = "13600";
        updateParams["protocol"] = EmailConnection.SMTPS;        
        updateParams["name"]=""
        updateParams["id"]=EmailConnector.list()[0].id

        IntegrationTestUtils.resetController(controller);
        updateParams.each{ key , val ->
            controller.params[key] = val;
        }

        controller.update();
        
        assertEquals(1, EmailConnector.count())
        assertEquals(1, EmailConnection.count())
        assertEquals(1, EmailDatasource.count())


        def model = controller.modelAndView.model;
        def modelItem = model.emailConnector;
        assertTrue(modelItem.hasErrors())
        assertEquals("blank", modelItem.errors.allErrors[0].code)

        def emailConnector=EmailConnector.list()[0]
        assertEquals(emailConnector.name,params.name)
        assertEquals(emailConnector.emailConnection.name,EmailConnector.getEmailConnectionName(emailConnector.name));
        assertEquals(emailConnector.emailDatasource.name,EmailConnector.getEmailDatasourceName(emailConnector.name));
        
        def paramsToCheck=[:]
        paramsToCheck.putAll(params)
        paramsToCheck.remove("name");

        paramsToCheck.each{ key , val ->
            assertEquals(val,emailConnector.emailConnection[key])
        }
        
    }
//    void testIfConnectionHasErrorsConnectorIsNotUpdated(){
//        fail("Needs implementation of pre-validation of parameters. can not be tested otherwise")
//       def params=[:]
//        params.putAll(connectorParams)
//
//        def controller = new EmailConnectorController();
//        params.each{ key , val ->
//            controller.params[key] = val;
//        }
//
//        controller.save();
//
//        assertEquals(1, EmailConnector.count())
//        assertEquals(1, EmailConnection.count())
//        assertEquals(1, EmailDatasource.count())
//
//
//
//        def updateParams=[:]
//        updateParams.putAll(params)
//        updateParams["name"] = "testConnectorrrx";
//        updateParams["smtpPort"] = 26;
//        updateParams["username"] = "testaccoun2t";
//        updateParams["userPassword"] = "13600";
//        updateParams["protocol"] = EmailConnection.SMTPS;
//        updateParams["smtpHost"] =null;
//        updateParams["id"]=EmailConnector.list()[0].id
//
//        IntegrationTestUtils.resetController(controller);
//        updateParams.each{ key , val ->
//            controller.params[key] = val;
//        }
//
//        controller.update();
//
//
//        def model = controller.modelAndView.model;
//        def modelItem = model.emailConnection;
//        assertTrue(modelItem.hasErrors())
//        assertEquals("nullable", modelItem.errors.allErrors[0].code)
//
//        def emailConnector=EmailConnector.list()[0]
//        assertEquals(emailConnector.name,params.name)
//        assertEquals(emailConnector.emailConnection.name,EmailConnector.getEmailConnectionName(emailConnector.name));
//        assertEquals(emailConnector.emailDatasource.name,EmailConnector.getEmailDatasourceName(emailConnector.name));
//
//        def paramsToCheck=[:]
//        paramsToCheck.putAll(params)
//        paramsToCheck.remove("name");
//
//        paramsToCheck.each{ key , val ->
//            assertEquals(val,emailConnector.emailConnection[key])
//        }
//    }
}