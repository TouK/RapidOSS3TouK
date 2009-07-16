import com.ifountain.rcmdb.test.util.SeleniumTestCase
import com.ifountain.rcmdb.test.util.SeleniumTestUtils

/**
* Created by IntelliJ IDEA.
* User: fadime
* Date: Jun 21, 2009
* Time: 10:00:19 PM
* To change this template use File | Settings | File Templates.
*/
class AdminUiReloadingTest extends SeleniumTestCase
{

    void setUp() throws Exception
    {
        super.setUp("http://${SeleniumTestUtils.getRIHost()}:${SeleniumTestUtils.getRIPort()}/RapidSuite/",
                SeleniumTestUtils.getSeleniumBrowser());
        selenium.login("rsadmin", "changeme");
        selenium.runScriptByName("removeAll");
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        selenium.logout()
    }



    public void testTestOperationReload()
    {
        def operationFile = new File("${SeleniumTestUtils.getRsHome()}/RapidSuite/operations/RsRiEventOperations.groovy");
        def message = "New operation successfully loaded"
        def newOprContent = """
            class RsRiEventOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
            {
                def static newOperation(){
                    return "${message}"
                }
            }
        """
        selenium.login("rsadmin", "changeme")
        def scriptContent = """return RsRiEvent.newOperation();""";
        SeleniumTestUtils.createScriptFile("operationCheck", scriptContent);
        selenium.createOnDemandScript("operationCheck", [:], []);

        def oldRsRiEventOperationContent = operationFile.getText();
        operationFile.setText(newOprContent);
        try {
            //open reload tab
            selenium.openAndWait("/RapidSuite/admin.gsp");
            selenium.clickAndWait("link=Reload");
            selenium.select("domainClassName", "label=RsRiEvent");
            selenium.clickAndWait("_action_run");

            assertTrue(selenium.isTextPresent("Operation reloaded successfully"));

            selenium.runScriptByName("operationCheck");
            assertTrue("Operation is reloaded message should be visible", selenium.isTextPresent(message));
        } finally {
            operationFile.setText(oldRsRiEventOperationContent);
            SeleniumTestUtils.deleteScriptFile("operationCheck");
            selenium.deleteScriptByName("operationCheck");
        }
    }

    public void testTestWebUIReload()
    {
        selenium.login("rsadmin", "changeme");
        assertTrue(selenium.isTextPresent("Ack"));
        assertFalse(selenium.isTextPresent("Acknowledge"));

        def eventsGspFile = new File("${SeleniumTestUtils.getRsHome()}/RapidSuite/web-app/index/events.gsp");
        def oldEventsGspContent = eventsGspFile.getText();
        assertTrue("Ack col does not exist", oldEventsGspContent.indexOf("colLabel=\"Ack\"") >= 0);
        def newContent = oldEventsGspContent.replaceAll("colLabel=\"Ack\"", "colLabel=\"Acknowledge\"")
        eventsGspFile.setText(newContent);
        try {
            //open reload tab
            selenium.openAndWait("/RapidSuite/admin.gsp");
            selenium.clickAndWait("link=Reload");
            selenium.clickAndWait("link=Reload Web UI");
            assertTrue(selenium.isTextPresent("Views and controllers reloaded successfully."));
            selenium.openAndWait("/RapidSuite/index.gsp");
            assertTrue(selenium.isTextPresent("Acknowledge"));
        }
        finally {
            eventsGspFile.setText(oldEventsGspContent);
        }
    }

}