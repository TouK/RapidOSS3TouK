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
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        logout()
    }


    private void logout()
    {
        selenium.click("link=Logout");
    }

    private void login()
    {
        selenium.open("/RapidSuite/auth/login?targetUri=%2Fadmin.gsp&format=html");
        selenium.waitForPageToLoad("30000");
        selenium.type("login", "rsadmin");
        selenium.type("password", "changeme");
        selenium.click("//input[@value='Sign in']");
        selenium.waitForPageToLoad("30000");
    }


    private void newScript()
    {
        selenium.open("/RapidSuite/script/list");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=Scripts");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=New Script");
        selenium.waitForPageToLoad("30000");
    }

    private void createScriptFile(String path, String scriptContent)
    {
        File file = new File(path)
        if (file.exists())
            file.delete();
        SeleniumTestUtils.createScript(path, scriptContent)
    }


    private void deleteScriptFile(String name)
    {
        File file = new File("${SeleniumTestUtils.getRsHome()}/RapidSuite/scripts/" + name)
        if (file.exists())
            file.delete();
    }

    private void deleteScript(String id) {
        selenium.open("/RapidSuite/script/show/" + id);
        selenium.click("_action_Delete");
        assertTrue(selenium.getConfirmation().matches("^Are you sure[\\s\\S]\$"));
    }


    public void testTestOperationReload()
    {

        def rsRiArrayList = new ArrayList();
        def reader = new BufferedReader(new FileReader("${SeleniumTestUtils.getRsHome()}/RapidSuite/operations/RsRiEventOperations.groovy"));

        String line = null;
        while ((line = reader.readLine()) != null)
            rsRiArrayList.add(line)

        rsRiArrayList.trimToSize()
        def arrayListSize = rsRiArrayList.size() - 1

        while (arrayListSize > 0)
            if (rsRiArrayList.get(arrayListSize--) == "}")
                break
            else
                arrayListSize--

        rsRiArrayList.add(arrayListSize++, "static newOperation(){")
        rsRiArrayList.add(arrayListSize++, """ return "New operation successfully loaded" """)
        rsRiArrayList.add(arrayListSize++, "  }")
        rsRiArrayList.add(arrayListSize++, "  }\n")

        def RsRiEventContent = ""
        def oldRsRiEventOperations = ""
        def temp
        for (int i = 0; i < arrayListSize; i++) {
            temp = rsRiArrayList.get(i)
            RsRiEventContent = RsRiEventContent + temp + "\n"

            if (i < arrayListSize - 3)
                oldRsRiEventOperations = oldRsRiEventOperations + temp + "\n"
        }
        oldRsRiEventOperations = oldRsRiEventOperations + "}\n"

        createScriptFile("${SeleniumTestUtils.getRsHome()}/RapidSuite/operations/RsRiEventOperations.groovy", RsRiEventContent);

        def scriptContent = """return RsRiEvent.newOperation();""";
        createScriptFile("${SeleniumTestUtils.getRsHome()}/RapidSuite/scripts/operationCheck.groovy", scriptContent);

        login()
        selenium.click("//li[2]/a/em");
        selenium.waitForPageToLoad("30000");
        selenium.select("domainClassName", "label=RsRiEvent");
        selenium.click("_action_run");
        selenium.waitForPageToLoad("30000");

        verifyTrue(selenium.isTextPresent("Operation reloaded successfully"));

        selenium.click("//em");
        selenium.waitForPageToLoad("30000");
        newScript()
        selenium.type("name", "operationCheck");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        String idValue = selenium.getText("document.getElementById('id')");
        selenium.click("_action_Run");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("New operation successfully loaded"));

        createScriptFile("${SeleniumTestUtils.getRsHome()}/RapidSuite/operations/RsRiEventOperations.groovy", oldRsRiEventOperations);
        deleteScript(idValue)
        deleteScriptFile("operationCheck.groovy")

    }

    public void testTestWebUIReload()
    {

        selenium.open("/RapidSuite/auth/login?targetUri=%2F&format=html");
        selenium.type("login", "rsadmin");
        selenium.type("password", "changeme");
        selenium.click("//input[@value='Sign in']");
        selenium.waitForPageToLoad("30000");
        selenium.click("//div[@id='top']/table/tbody/tr/td[2]/div/ul/li[1]/a/em");
        selenium.waitForPageToLoad("30000");
        verifyEquals("Ack", selenium.getText("//span[@id='elgen-37']/span"));
        verifyFalse(selenium.isTextPresent("Acknowledge"));

        selenium.open("/RapidSuite/admin.gsp");
        selenium.click("//li[2]/a/em");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=Reload Web UI");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Views and controllers reloaded successfully."));

        def eventsFileContent = ""
        def newEventsFileContent = ""
        def isEventIncludesAck = false //checks if  events.gsp includes colLabel="Act"

        // def  br=new BufferedReader(new FileReader(params.file));
        def br = new BufferedReader(new FileReader("${SeleniumTestUtils.getRsHome()}/RapidSuite/web-app/index/events.gsp"));
        String line = null;
        while ((line = br.readLine()) != null)
        {
            if (line.contains("colLabel=\"Ack\"") && line.contains("</rui:sgColumn>"))
            {
                def splitted = new String[3]
                splitted = line.split("\"Ack\"")
                eventsFileContent = eventsFileContent + line + "\n"
                def lastLine = splitted[0] + "\"Acknowledge\"" + splitted[1]
                newEventsFileContent = newEventsFileContent + lastLine + "\n"
                isEventIncludesAck = true
            }
            else
            {eventsFileContent = eventsFileContent + line + "\n"
                newEventsFileContent = newEventsFileContent + line + "\n"
            }
        }

        assertTrue(isEventIncludesAck)

        createScriptFile("${SeleniumTestUtils.getRsHome()}/RapidSuite/web-app/index/events.gsp", newEventsFileContent);

        selenium.open("/RapidSuite/index.gsp");
        verifyTrue(selenium.isTextPresent("Acknowledge"));


        createScriptFile("${SeleniumTestUtils.getRsHome()}/RapidSuite/web-app/index/events.gsp", eventsFileContent);
        selenium.open("/RapidSuite/admin.gsp");
        selenium.click("//li[2]/a/em");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=Reload Web UI");
        selenium.waitForPageToLoad("30000");
    }

}