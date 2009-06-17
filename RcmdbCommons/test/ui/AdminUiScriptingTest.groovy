import com.ifountain.rcmdb.test.util.SeleniumTestCase
import com.ifountain.rcmdb.test.util.SeleniumTestUtils

/**
* Created by IntelliJ IDEA.
* User: fadime
* Date: Jun 8, 2009
* Time: 2:24:02 AM
* To change this template use File | Settings | File Templates.
*/

class AdminUiScriptingTest extends SeleniumTestCase
{

    void setUp() throws Exception
    {
        super.setUp("http://${SeleniumTestUtils.getRIHost()}:${SeleniumTestUtils.getRIPort()}/RapidSuite/", SeleniumTestUtils.getSeleniumBrowser());
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
        String str = "RapidServer.log";
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



    public void testCreateAnOnDemandScriptByScriptFileName()
    {
        def scriptContent = """import script.*
             def resp ="";
             res = CmdbScript.search("scriptFile:aScript")
             res.results.each{
            resp = resp+"scriptName:\${it.name} loglevel: \${it.logLevel} useOwnLogger: \${it.logFileOwn} staticParameter:\${it.staticParam}"
            }
             logger.warn(resp)
             return resp """  ;

        isScriptExists("${SeleniumTestUtils.base_Dir}aScript.groovy",scriptContent);
        

        login();
        newScript();
        selenium.type("name", "ondemand2");
        selenium.type("scriptFile", "aScript");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Script created"));
        verifyEquals("ondemand2", selenium.getText("name"));
        verifyEquals("aScript", selenium.getText("scriptFile"));
        verifyEquals("WARN", selenium.getText("logLevel"));
        verifyEquals("false", selenium.getText("identifier=logFileOwn"));
        verifyEquals("OnDemand", selenium.getText("identifier=type"));
    }

    private void   isScriptExists(String path,String scriptContent)
    {
        File file = new File(path)
        if(file.exists())
            file.delete();
        SeleniumTestUtils.createScript(path,scriptContent)
    }

    public void atestCreateAnOnDemandscriptByName()
    {
        String scriptContent = """import script.*
             def resp ="";
             res = CmdbScript.search("scriptFile:aScript")
             res.results.each{
            resp = resp+"scriptName:\${it.name} loglevel: \${it.logLevel} useOwnLogger: \${it.logFileOwn} staticParameter:\${it.staticParam}"
            }
             logger.warn(resp)
             return resp """  ;

        isScriptExists("${SeleniumTestUtils.getSeleniumRIPath()}aScript.groovy",scriptContent);

        login();
        newScript();
        selenium.type("name", "aScript");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Script created"));
        verifyEquals("aScript", selenium.getText("name"));
        verifyEquals("aScript", selenium.getText("scriptFile"));
        verifyEquals("WARN", selenium.getText("logLevel"));
        verifyEquals("false", selenium.getText("logFileOwn"));
        verifyEquals("", selenium.getText("identifier=staticParam"));
        verifyEquals("OnDemand", selenium.getText("identifier=type"));
    }


    public void atestTestAScheduledCronScriptFilesSelTest()
    {

        String scriptContent = """ import script.*
             import org.apache.commons.lang.StringUtils
              def logFile = new File(params.file);
              def log = logFile.getText();
              return StringUtils.countMatches (log, "Hello from cron")
                   """  ;
          isScriptExists("${SeleniumTestUtils.getSeleniumRIPath()}logValidator.groovy",scriptContent);


        String scriptContentTwo = """import script.*

                def resp ="";
                res = CmdbScript.search("scriptFile:cron")
                res.results.each{
                resp = resp+"scriptName:\${it.name} loglevel: \${it.logLevel} useOwnLogger: \${it.logFileOwn} staticParameter:\${it.staticParam}"
                }
                logger.warn("Hello from cron")
                return resp
                 """  ;

          isScriptExists("${SeleniumTestUtils.getSeleniumRIPath()}cron.groovy",scriptContentTwo);

        // test a scheduled cron script
        login();
        // looks RapidServer.log file for "Hello from cron" entries
        String stored = newLogValidatorScript();
        newScript();

        // Creates a scheduled script with attributes
        selenium.type("name", "scheduled2");
        selenium.type("scriptFile", "cron");
        selenium.select("type", "label=Scheduled");
        selenium.select("scheduleType", "label=Cron");
        selenium.type("cronExpression", "* * * * * ?");
        selenium.click("enabled");
        selenium.type("staticParam", "Hello from cron");

        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");

        // scheduled script attributes will be cheched if it has the right attributes
        verifyTrue(selenium.isTextPresent("Script created"));
        verifyEquals("scheduled2", selenium.getText("name"));
        verifyEquals("cron", selenium.getText("scriptFile"));
        verifyEquals("WARN", selenium.getText("logLevel"));
        verifyEquals("false", selenium.getText("logFileOwn"));
        verifyEquals("Hello from cron", selenium.getText("staticParam"));
        verifyEquals("Scheduled", selenium.getText("type"));
        verifyEquals("Cron", selenium.getText("scheduleType"));
        verifyEquals("0", selenium.getText("startDelay"));
        // selenium.type("cronExpression", "* * * * * ?");
        assertEquals("* * * * * ?", selenium.getText("cronExpression"));
        verifyEquals("true", selenium.getText("enabled"));

        // the script id will be stored
        String idValue = selenium.getText("document.getElementById('id')");

        // RapidServer.log file be looked if there is new "Hello from cron" entries
        newScript();
        stored = newLogValidatorScript();

        // if the "Hello from cron" number has changed means not equal to the value in 'stored', test will pass
        assertNotEquals(stored, selenium.isTextPresent(stored));
        // the script will be updated
        selenium.open("/RapidSuite/script/show/" + idValue);
        selenium.click("_action_Edit");
        selenium.waitForPageToLoad("30000");
        selenium.click("logFileOwn");
        selenium.click("_action_Update");
        selenium.waitForPageToLoad("30000");

        assertEquals("Script " + idValue + " updated", selenium.getText("pageMessage"))
        // after update the use own log will be checked if it is true test will pass
        verifyEquals("true", selenium.getText("logFileOwn"));


        newScript();
        // file scheduled2.log  will be looked if it has "Hello from cron" entries
        selenium.type("name", "logValidator");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        selenium.open("/RapidSuite/script/run/logValidator?file=logs/scheduled2.log");

        // file scheduled2.log must have some entries
        assertNotEquals("0", selenium.getText("//body"));
        // the "Hello from cron" entry number will be stored
        stored = newLogValidatorScript();

        selenium.open("/RapidSuite/script/show/" + idValue);
        // the script will be updated again
        selenium.click("_action_Edit");
        selenium.waitForPageToLoad("30000");
        selenium.click("enabled");
        selenium.click("_action_Update");
        selenium.waitForPageToLoad("30000");

        verifyTrue(selenium.isTextPresent("Script " + idValue + " updated"));
        // after update the enable attribute will be checked if it is false test will pass
        verifyEquals("false", selenium.getText("enabled"));
                                                                       
        Thread.sleep(3000);
        // file RapidServer.log must have some entries

        newScript();
        selenium.type("name", "logValidator");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        selenium.open("/RapidSuite/script/run/logValidator?file=logs/RapidServer.log");
        // if RapidServer.log has no new entries test will pass
        assertTrue(selenium.isTextPresent(stored));
        selenium.open("/RapidSuite/script/show/" + idValue);
        selenium.waitForPageToLoad("30000");

    }
}



