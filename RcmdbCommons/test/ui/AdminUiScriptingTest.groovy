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
        SeleniumTestUtils.createScriptFile(path, scriptContent)
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



    public void testCreateAnOnDemandScriptByScriptFileName()
    {
        //creates aScript.groovy in RS_HOME/RapidSuite/scripts folder with the following content
        def scriptContent = """import script.*
             def resp ="";
             res = CmdbScript.search("scriptFile:aScript")
             res.results.each{
            resp = resp+"scriptName:\${it.name} loglevel: \${it.logLevel} useOwnLogger: \${it.logFileOwn} staticParameter:\${it.staticParam}"
            }
             logger.warn(resp)
             return resp """;

        //checkes a script named aScript.groovy exists, if not creates a new one with specified content
        createScriptFile("${SeleniumTestUtils.getRsHome()}/RapidSuite/scripts/aScript.groovy", scriptContent);

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
        String idValue = selenium.getText("document.getElementById('id')");

        deleteScript(idValue)
        deleteScriptFile("aScript.groovy")
    }


    public void testCreateAnOnDemandscriptByName()
    {
        //creates aScript.groovy in RS_HOME/RapidSuite/scripts folder with the following content
        String scriptContent = """import script.*
             def resp ="";
             res = CmdbScript.search("scriptFile:aScript")
             res.results.each{
             resp = resp+"scriptName:\${it.name} loglevel: \${it.logLevel} useOwnLogger: \${it.logFileOwn} staticParameter:\${it.staticParam}"
             }
             logger.warn(resp)
             return resp """;

        //checkes a script named aScript.groovy exists, if not creates a new one with specified content
        createScriptFile("${SeleniumTestUtils.getRsHome()}/RapidSuite/scripts/aScript.groovy", scriptContent);

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
        String idValue = selenium.getText("document.getElementById('id')");

        deleteScript(idValue)
        deleteScriptFile("aScript.groovy")
    }


    public void testTestAScheduledCronScriptFilesSelTest()
    {
        //creates logValidator.groovy in RS_HOME/RapidSuite/scripts folder with the following content
        String scriptContent = """ import script.*
              import org.apache.commons.lang.StringUtils
              def logFile = new File(params.file);
              def log = logFile.getText();
              return StringUtils.countMatches (log, "Hello from cron")
               """;

        //checkes a script named logValidator.groovy exists, if not creates a new one with specified content
        createScriptFile("${SeleniumTestUtils.getRsHome()}/RapidSuite/scripts/logValidator.groovy", scriptContent);


        //creates cron.groovy in RS_HOME/RapidSuite/scripts folder with the following content
        String scriptContentTwo = """import script.*
                def resp ="";
                res = CmdbScript.search("scriptFile:cron")
                res.results.each{
                resp = resp+"scriptName:\${it.name} loglevel: \${it.logLevel} useOwnLogger: \${it.logFileOwn} staticParameter:\${it.staticParam}"
                }
                logger.warn("Hello from cron")
                return resp
                 """;

        //checkes a script named cron.groovy exists, if not creates a new one with specified content
        createScriptFile("${SeleniumTestUtils.getRsHome()}/RapidSuite/scripts/cron.groovy", scriptContentTwo);

        // test a scheduled cron script
        login();

        // looks RapidServer.log file for old "Hello from cron" entries
        String stored = newLogValidatorScript();

        // Creates a scheduled script with attributes
        newScript();
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


        // file scheduled2.log  will be looked if it has "Hello from cron" entries
        newScript();
        selenium.type("name", "logValidator");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        String idValueLog = selenium.getText("document.getElementById('id')");
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

        deleteScript(idValue)
        deleteScript(idValueLog)

        deleteScriptFile("cron.groovy")
        deleteScriptFile("logValidator.groovy")
    }



    public void testAScheduledPeriodicScript()
    {
        //creates periodic.groovy in RS_HOME/RapidSuite/scripts folder with the following content
        def scriptContent = """import script.*

        def resp ="";
        res = CmdbScript.search("scriptFile:periodic")
        res.results.each{
        resp = resp+"scriptName:\${it.name} loglevel: \${it.logLevel} useOwnLogger: \${it.logFileOwn} staticParameter:\${it.staticParam}"
        }
        logger.warn("Hello from periodic")
        return resp """;

        //checkes a script named periodic.groovy exists, if not creates a new one with specified content
        createScriptFile("${SeleniumTestUtils.getRsHome()}/RapidSuite/scripts/periodic.groovy", scriptContent);


        //creates logValidator.groovy in RS_HOME/RapidSuite/scripts folder with the following content
        def scriptContentLog = """ import org.apache.commons.lang.StringUtils

          def logFile = new File(params.file);
          def log = logFile.getText();
          return StringUtils.countMatches (log, "WARN: Hello from periodic")""";

        //checkes a script named logValidator.groovy exists, if not creates a new one with specified content
        createScriptFile("${SeleniumTestUtils.getRsHome()}/RapidSuite/scripts/logValidator.groovy", scriptContentLog);


        def scriptContentTime = """      import java.text.SimpleDateFormat;

                 import org.apache.commons.lang.StringUtils

                 def arrayL = new ArrayList();

                 def  br=new BufferedReader(new FileReader(params.file));
                 String line=null;
                 while((line=br.readLine())!=null){
                     if (line.endsWith("Hello from periodic"))
                         arrayL.add(line)
                 }
                 arrayL.trimToSize()
                 def arrayListSize = arrayL.size()-1
                 def splitted = new String[3]
                 splitted = StringUtils.split(arrayL.get(arrayListSize--),' ')
                 def secondTime=splitted[1]
                 splitted = StringUtils.split(arrayL.get(arrayListSize--),' ')
                 def firstTime =splitted[1]
                 SimpleDateFormat   formatter = new SimpleDateFormat("HH:mm:ss.SSS")
                 Date date = (Date)formatter.parse(firstTime);
                 long FlongDate=date.getTime();
                 date = (Date)formatter.parse(secondTime);
                 long SlongDate=date.getTime();

              return (SlongDate-FlongDate) """;

        createScriptFile("${SeleniumTestUtils.getRsHome()}/RapidSuite/scripts/timeController.groovy", scriptContentTime);

        login();
        newScript();

        selenium.type("name", "scheduled1");
        selenium.type("scriptFile", "periodic");
        selenium.select("type", "label=Scheduled");
        selenium.type("period", "10");
        selenium.type("staticParam", "Hello from periodic");
        selenium.click("enabled");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");

        verifyTrue(selenium.isTextPresent("Script created"));
        verifyEquals("scheduled1", selenium.getText("name"));
        verifyEquals("periodic", selenium.getText("scriptFile"));
        verifyEquals("WARN", selenium.getText("logLevel"));
        verifyEquals("false", selenium.getText("logFileOwn"));
        verifyEquals("Hello from periodic", selenium.getText("staticParam"));
        verifyEquals("Scheduled", selenium.getText("type"));
        verifyEquals("Periodic", selenium.getText("scheduleType"));
        verifyEquals("0", selenium.getText("startDelay"));
        verifyEquals("10", selenium.getText("period"));
        verifyEquals("true", selenium.getText("enabled"));
        String idValue = selenium.getText("document.getElementById('id')");


        int store = Integer.parseInt(newLogValidatorScript()) + 2
        String stored = store.toString()

        newScript();
        selenium.type("name", "logValidator");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        String idValueLog = selenium.getText("document.getElementById('id')");

        Thread.sleep(20000);

        selenium.open("/RapidSuite/script/run/logValidator?file=logs/RapidServer.log");
        String str = selenium.getText("//body");

        String idValueTime = "*"
        if (str != stored)
        {
            newScript();
            selenium.type("name", "timeController");
            selenium.click("//input[@value='Create']");
            selenium.waitForPageToLoad("30000");
            idValueTime = selenium.getText("document.getElementById('id')")
            selenium.open("/RapidSuite/script/run/timeController?file=logs/RapidServer.log");
            verifyEquals("10000", selenium.getText("//body"));
        }


        selenium.open("/RapidSuite/script/show/" + idValue);
        selenium.waitForPageToLoad("30000");
        selenium.click("_action_Edit");
        selenium.waitForPageToLoad("30000");
        selenium.click("enabled");
        selenium.click("_action_Update");
        selenium.waitForPageToLoad("30000");

        verifyEquals("Script " + idValue + " updated", selenium.getText("pageMessage"));
        verifyTrue(selenium.isTextPresent("Script " + idValue + " updated"));
        verifyEquals("false", selenium.getText("enabled"));

        stored = newLogValidatorScript()

        newScript();
        selenium.type("name", "logValidator");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");

        Thread.sleep(20000);

        selenium.open("/RapidSuite/script/run/logValidator?file=logs/RapidServer.log");
        str = selenium.getText("//body");

        if (str != stored)
        {
            newScript();
            selenium.type("name", "timeController");
            selenium.click("//input[@value='Create']");
            selenium.waitForPageToLoad("30000");
            idValueTime = selenium.getText("document.getElementById('id')")
            selenium.open("/RapidSuite/script/run/timeController?file=logs/RapidServer.log");
            verifyNotEquals("10000", selenium.getText("//body"));
        }

        deleteScript(idValue)
        deleteScript(idValueLog)
        if (!idValueTime.equals("*"))
            deleteScript(idValueTime)

        deleteScriptFile("timeController.groovy")
        deleteScriptFile("periodic.groovy")
        deleteScriptFile("logValidator.groovy")
    }


    public void testLoggerParameters()
    {
        def scriptContent = """import script.*

            def resp ="";
            res = CmdbScript.search("scriptFile:aScript")
            res.results.each{
              resp = resp+"scriptName:\${it.name} loglevel: \${it.logLevel} useOwnLogger: \${it.logFileOwn} staticParameter:\${it.staticParam}"
            }
            logger.warn(resp)
            return resp""";

        createScriptFile("${SeleniumTestUtils.getRsHome()}/RapidSuite/scripts/aScript.groovy", scriptContent);


        def scriptContentLog = """
                 import org.apache.commons.lang.StringUtils
                  def logFile = new File(params.file);
                  def log = logFile.getText();
                  return StringUtils.countMatches(log,"scriptName:aScript loglevel: DEBUG useOwnLogger: \${params.content} staticParameter:" )
                  """;

        createScriptFile("${SeleniumTestUtils.getRsHome()}/RapidSuite/scripts/logValidator.groovy", scriptContentLog);

        login()
        newScript()

        selenium.type("name", "aScript");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");

        selenium.click("link=Script List");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=aScript");
        selenium.waitForPageToLoad("30000");

        selenium.click("_action_Edit");
        selenium.waitForPageToLoad("30000");
        selenium.select("logLevel", "label=DEBUG");
        selenium.click("logFileOwn");
        selenium.click("_action_Update");
        selenium.waitForPageToLoad("30000");

        verifyEquals("aScript", selenium.getText("name"));
        verifyEquals("aScript", selenium.getText("scriptFile"));
        verifyEquals("DEBUG", selenium.getText("logLevel"));
        verifyEquals("true", selenium.getText("logFileOwn"));
        verifyEquals("OnDemand", selenium.getText("type"));
        String idValue = selenium.getText("document.getElementById('id')");

        selenium.click("_action_Run");
        selenium.waitForPageToLoad("30000");

        newScript()
        selenium.type("name", "logValidator");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        String idValueLog = selenium.getText("document.getElementById('id')");

        selenium.open("/RapidSuite/script/run/logValidator?file=logs/aScript.log&content=true");
        assertNotEquals("0", selenium.getText("//body"));

        selenium.open("/RapidSuite/script/show/" + idValue);
        selenium.click("_action_Edit");
        selenium.waitForPageToLoad("30000");
        selenium.click("logFileOwn");
        selenium.click("_action_Update");
        selenium.waitForPageToLoad("30000");

        verifyEquals("aScript", selenium.getText("name"));
        verifyEquals("aScript", selenium.getText("scriptFile"));
        verifyEquals("DEBUG", selenium.getText("logLevel"));
        verifyEquals("false", selenium.getText("logFileOwn"));
        verifyEquals("OnDemand", selenium.getText("type"));

        selenium.click("_action_Run");
        selenium.waitForPageToLoad("30000");

        newScript()
        selenium.type("name", "logValidator");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        selenium.open("/RapidSuite/script/run/logValidator?file=logs/RapidServer.log&content=false");
        assertNotEquals("0", selenium.getText("//body"));

        deleteScript(idValue)
        deleteScript(idValueLog)
        deleteScriptFile("aScript.groovy")
        deleteScriptFile("logValidator.groovy")

    }

}



