package utils

import com.thoughtworks.selenium.Selenium
import junit.framework.Assert

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Aug 4, 2009
* Time: 9:24:38 AM
* To change this template use File | Settings | File Templates.
*/
class SnmpUiUtilities {
    public static createSnmpConnector(Selenium selenium, String name, String host, String port, String scriptFile, String staticParameter, String logLevel = "WARN", boolean validate = true)
    {
        selenium.openAndWait("/RapidSuite/snmpConnector/list")
        selenium.clickAndWait("link=New SnmpConnector");
        selenium.type("name", name);
        selenium.type("host", host);
        selenium.type("port", port);
        selenium.type("scriptFile", scriptFile);
        selenium.type("staticParam", staticParameter);
        selenium.select("logLevel", "label=${logLevel}");
        selenium.clickAndWait("//input[@value='Create']");
        def conId = CommonUiTestUtils.getIdFromlocation(selenium.getLocation());
        if (validate)
        {
            CommonUiTestUtils.assertPageMessage(selenium, "SnmpConnector " + conId + " created")
        }
        return conId;
    }

    public static deleteSnmpConnectorById(Selenium selenium, String connectorId, boolean validate = true)
    {
        selenium.openAndWait("/RapidSuite/snmpConnector/show/" + connectorId);
        Assert.assertTrue("Snmp connector ${connectorId} does not exist".toString(), selenium.getLocation().indexOf("/snmpConnector/show") >= 0);
        def connectorName = selenium.getText("id=name");
        selenium.clickAndWait("_action_Delete");
        Assert.assertTrue(selenium.getConfirmation().matches("^Are you sure[\\s\\S]\$"));
        selenium.waitForPageToLoad("30000");
        if (validate)
        {
            Assert.assertTrue("Expected to end with /RapidSuite/snmpConnector/list but was ${selenium.getLocation()}", selenium.getLocation().endsWith("/RapidSuite/snmpConnector/list"));
            CommonUiTestUtils.assertPageMessage(selenium, "SnmpConnector " + connectorName + " deleted")
        }
    }

    public static deleteAllSnmpConnectors(Selenium selenium, boolean validate = true)
    {
        def connectors = CommonUiTestUtils.search(selenium, "connector.SnmpConnector", "alias:*")
        connectors.each {
            deleteSnmpConnectorById(selenium, it.id, validate);
        }
    }

    public static startSnmpConnectorById(Selenium selenium, String connectorId, boolean validate = true){
        selenium.openAndWait("/RapidSuite/snmpConnector/show/" + connectorId);
        Assert.assertTrue("Snmp connector ${connectorId} does not exist".toString(), selenium.getLocation().indexOf("/snmpConnector/show") >= 0);
        def connectorName = selenium.getText("name");
        selenium.clickAndWait("_action_StartConnector");
        if (validate)
        {
            Assert.assertTrue("Expected to end with /RapidSuite/snmpConnector/list but was ${selenium.getLocation()}", selenium.getLocation().endsWith("/RapidSuite/snmpConnector/list"));
            CommonUiTestUtils.assertPageMessage(selenium, "Connector " + connectorName + " successfully started")
        }
    }

    public static stopSnmpConnectorById(Selenium selenium, String connectorId, boolean validate = true){
        selenium.openAndWait("/RapidSuite/snmpConnector/show/" + connectorId);
        Assert.assertTrue("Snmp connector ${connectorId} does not exist".toString(), selenium.getLocation().indexOf("/snmpConnector/show") >= 0);
        def connectorName = selenium.getText("name");
        selenium.clickAndWait("_action_StopConnector");
        if (validate)
        {
            Assert.assertTrue("Expected to end with /RapidSuite/snmpConnector/list but was ${selenium.getLocation()}", selenium.getLocation().endsWith("/RapidSuite/snmpConnector/list"));
            CommonUiTestUtils.assertPageMessage(selenium, "Connector " + connectorName + " successfully stopped")
        }
    }

    public static reloadSnmpConnectorById(Selenium selenium, String connectorId, boolean validate = true){
        selenium.openAndWait("/RapidSuite/snmpConnector/show/" + connectorId);
        Assert.assertTrue("Snmp connector ${connectorId} does not exist".toString(), selenium.getLocation().indexOf("/snmpConnector/show") >= 0);
        selenium.clickAndWait("_action_Reload");
        if (validate)
        {
            Assert.assertTrue("Expected to end with /RapidSuite/snmpConnector/show but was ${selenium.getLocation()}", selenium.getLocation().endsWith("/RapidSuite/snmpConnector/show/${connectorId}"));
            CommonUiTestUtils.assertPageMessage(selenium, "Script reloaded successfully.")
        }
    }

    public static stopAllSnmpConnectors(Selenium selenium){
        def scriptContent = """
            import script.*;
            import connector.*;
            def snmpConnectors = SnmpConnector.list();
            snmpConnectors.each{
                try{
                    CmdbScript.stopListening(it.script);
                }
                catch(e){}
            }
        """
        selenium.executeScript(scriptContent);
    }
}