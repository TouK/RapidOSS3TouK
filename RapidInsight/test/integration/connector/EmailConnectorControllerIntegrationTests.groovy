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

        assertEquals(String.valueOf(controller.response.redirectedUrl),"emailConnector")

        def emailConnectors = EmailConnector.list();
        assertEquals(1, emailConnectors.size());
        EmailConnector emailConnector = emailConnectors[0]
        assertEquals(params.name, emailConnector.name);


        def emailConnections=EmailConnection.list();
        assertEquals(1,emailConnections.size());
        def emailConnection=emailConnections[0];
        assertEquals(emailConnection.name,emailConnector.name);
        assertEquals(emailConnection.id,emailConnector.emailConnection.id);
        assertEquals (emailConnection.emailDatasources.size(), 1);
        
        params.each{ key , val ->
            assertEquals(val,emailConnection[key])
        }

        def emailDatasources = EmailDatasource.list();
        assertEquals (1, emailDatasources.size());
        EmailDatasource emailDatasource = emailDatasources[0];
        assertEquals (emailDatasource.name, emailConnector.name+"connectorDs");
        assertEquals (emailDatasource.id, emailConnector.emailDatasource.id);
        assertEquals (emailDatasource.connection.id, emailConnection.id);
        assertEquals (0, emailDatasource.reconnectInterval);
        assertEquals (emailConnection.emailDatasources[0].id, emailDatasource.id);

    }
    void testIfConnectionHasErrorsConnectorIsNotAdded(){
        def params=[:]
        params.putAll(connectorParams)
        params["smtpHost"] =null;

        def controller = new EmailConnectorController();
        params.each{ key , val ->
            controller.params[key] = val;
        }

        controller.save();

        assertEquals(0, EmailConnector.list().size())
        assertEquals(0, EmailConnection.list().size())
        assertEquals(0, EmailDatasource.list().size())

        def model = controller.modelAndView.model;
        def conn = model.emailConnection;
        assertTrue(conn.hasErrors())
        assertEquals("nullable", conn.errors.allErrors[0].code)
    }
    void testIfDatasourceHasErrorsConnectorIsNotAdded(){
        def params=[:]
        params.putAll(connectorParams)
        
        def datasource = BaseDatasource.add(name:"${params.name}connectorDs")
        assertFalse(datasource.hasErrors())

        def controller = new EmailConnectorController();
            params.each{ key , val ->
            controller.params[key] = val;
        }

        controller.save();
        def connector=EmailConnector.list()[0]        
        assertEquals(0, EmailConnector.list().size())
        assertEquals(0, EmailConnection.list().size())
        assertEquals(0, EmailDatasource.list().size())


        def model = controller.modelAndView.model;
        def ds = model.emailDatasource;
        assertTrue(ds.hasErrors())
        assertEquals("rapidcmdb.invalid.instanceof.existing", ds.errors.allErrors[0].code)
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
        assertEquals(String.valueOf(controller.response.redirectedUrl),"emailConnector")

        def oldEmailConnectors = EmailConnector.list();
        assertEquals(1, oldEmailConnectors.size());
        EmailConnector oldEmailConnector = oldEmailConnectors[0]
        assertEquals(oldParams.name, oldEmailConnector.name);
        
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
        assertEquals(String.valueOf(controller.response.redirectedUrl),"emailConnector")

        def emailConnectors = EmailConnector.list();
        assertEquals(1, emailConnectors.size());
        EmailConnector emailConnector = emailConnectors[0]
        assertEquals(params.name, emailConnector.name);
        
        def emailConnections=EmailConnection.list();
        assertEquals(1,emailConnections.size());
        def emailConnection=emailConnections[0];
        assertEquals(emailConnection.name,emailConnector.name);
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
        assertEquals (emailDatasource.name, emailConnector.name+"connectorDs");
        assertEquals (emailDatasource.id, emailConnector.emailDatasource.id);
        assertEquals (emailDatasource.connection.id, emailConnection.id);
        assertEquals (0, emailDatasource.reconnectInterval);
        assertEquals (emailConnection.emailDatasources[0].id, emailDatasource.id);
        

    }
}