import com.ifountain.rcmdb.test.util.SeleniumTestCase
import com.ifountain.rcmdb.test.util.SeleniumTestUtils
import org.apache.commons.lang.StringUtils


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

    private String newLogValidatorScript()
    {
        newScript();
        selenium.type("name", "logValidator");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        selenium.open("/RapidSuite/script/run/logValidator?file=logs/RapidServer.log");

        // the Hello from cron entry number will be stored in stored
        return selenium.getText("//body");
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
        def oldRsRiEventOperations=""
        def temp
        for (int i = 0; i < arrayListSize; i++){
            temp=  rsRiArrayList.get(i)
            RsRiEventContent = RsRiEventContent + temp+ "\n"

            if(i<arrayListSize-4)
            oldRsRiEventOperations = oldRsRiEventOperations +temp + "\n"
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


}