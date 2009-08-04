import com.ifountain.rcmdb.test.util.SeleniumTestCase
import com.ifountain.rcmdb.test.util.SeleniumTestUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Aug 4, 2009
* Time: 9:19:47 AM
* To change this template use File | Settings | File Templates.
*/
class AdminUiSnmpTabTest extends SeleniumTestCase {
    void setUp() throws Exception
    {
        super.setUp("http://${SeleniumTestUtils.getRIHost()}:${SeleniumTestUtils.getRIPort()}/RapidSuite",
                SeleniumTestUtils.getSeleniumBrowser());
        selenium.logout()
        selenium.login("rsadmin", "changeme");
        selenium.stopAllSnmpConnectors();
        selenium.deleteAllSnmpConnectors();
    }
    void tearDown() {
        selenium.stopAllSnmpConnectors();
        super.tearDown();
    }

    void testCreateSnmpConnector() {
        def connectorName = "snmp";
        def host = "0.0.0.0";
        def port = "162"
        def scriptName = "snmpTestListener";
        def scriptContent = """
            def getParameters(){
                return [:]
            }
            def init(){}
            def cleanUp(){}
            def update(trap){}
        """
        SeleniumTestUtils.createScriptFile(scriptName, scriptContent)
        def connectorId = selenium.createSnmpConnector(connectorName, host, port, scriptName, "", "WARN", true)
        selenium.openAndWait("/RapidSuite/snmpConnector/show/" + connectorId);
        assertEquals(connectorName, selenium.getText("name"))
        assertEquals(host, selenium.getText("host"))
        assertEquals(port, selenium.getText("port"))
        assertEquals(scriptName, selenium.getText("scriptFile"))
        assertEquals("WARN", selenium.getText("logLevel"))
        assertEquals("", selenium.getText("staticParam"))
        assertTrue(new File("${SeleniumTestUtils.getRsHome()}/RapidSuite/logs/${connectorName}.log").exists())
    }

    void testStartStopConnector() {
        def connectorName = "snmp";
        def host = "0.0.0.0";
        def port = "162"
        def scriptName = "snmpTestListener";
        def scriptContent = """
            def getParameters(){
                return [:]
            }
            def init(){}
            def cleanUp(){}
            def update(trap){}
        """
        SeleniumTestUtils.createScriptFile(scriptName, scriptContent)
        def connectorId = selenium.createSnmpConnector(connectorName, host, port, scriptName, "", "WARN", true)
        selenium.startSnmpConnectorById(connectorId, true);
        Thread.sleep(1000);
        selenium.stopSnmpConnectorById(connectorId, true);
    }

}