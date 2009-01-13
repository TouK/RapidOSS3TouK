package connector

import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import connection.EmailConnection
import datasource.EmailDatasource
/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 12, 2009
* Time: 6:14:12 PM
* To change this template use File | Settings | File Templates.
*/
class EmailConnectorControllerIntegrationTests  extends RapidCmdbIntegrationTestCase{
    static transactional = false;
    public void setUp() {
        super.setUp();
        EmailConnector.removeAll();
        EmailConnection.removeAll();
        EmailDatasource.removeAll();
    }

    public void tearDown() {
        super.tearDown();
    }

    public void testSuccessfulSave()
    {
        def params=[:]
        params["name"] = "emailConnector";
        params["smtpHost"] ="192.168.1.100";
        params["smtpPort"] = 25;
        params["username"] = "testaccount";
        params["userPassword"] = "3600";
        params["protocol"] = EmailConnection.SMTP;


        def controller = new EmailConnectorController();
        params.each{ key , val ->
            controller.params[key] = val;                
        }
        
        controller.save();         
        assertTrue(String.valueOf(controller.response.redirectedUrl).indexOf("emailConnector")>=0)

        def emailConnectors = EmailConnector.list();
        assertEquals(1, emailConnectors.size());
        EmailConnector emailConnector = emailConnectors[0]
        assertEquals("emailConnector", emailConnector.name);


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
}