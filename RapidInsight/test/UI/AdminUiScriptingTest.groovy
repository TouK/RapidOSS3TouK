import com.ifountain.rcmdb.test.util.SeleniumTestCase
import com.ifountain.rcmdb.test.util.SeleniumTestUtils
import utils.CommonUiTestUtils

/**
* Created by IntelliJ IDEA.
* User: fadime
* Date: Jun 8, 2009
* Time: 2:24:02 AM
* To change this template use File | Settings | File Templates.
*/

class AdminUiScriptingTest extends SeleniumTestCase
{
    public static final String logValidatorName = "logValidator"
    public static final String logLevelModifier = "logLevelModifier"
    void setUp() throws Exception
    {
        super.setUp("http://${SeleniumTestUtils.getRIHost()}:${SeleniumTestUtils.getRIPort()}/RapidSuite/",
                SeleniumTestUtils.getSeleniumBrowser());
        selenium.login("rsadmin", "changeme");
        selenium.runScript("removeAll");
        createLogValidator()
        createLogLevelModifier()
    }



    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        selenium.runScriptByName(logLevelModifier, [loggerName:"root", level:"WARN"])
        selenium.deleteScriptByName(logValidatorName, false);
        selenium.deleteScriptByName(logLevelModifier, false);
        selenium.logout()

    }





    public void testCreateAnOnDemandScriptByScriptFileName()
    {
        selenium.deleteScriptsByFileName("aScript");
        selenium.deleteScriptByName("ondemand2", false);
        //creates aScript.groovy in RS_HOME/RapidSuite/scripts folder with the following content
        def scriptContent = """import script.*
             def resp ="";
             res = CmdbScript.search("name:ondemand2")
             res.results.each{
            resp = resp+"scriptName:\${it.name} loglevel: \${it.logLevel} useOwnLogger: \${it.logFileOwn} staticParameter:\${it.staticParam}"
            }
             logger.warn(resp)
             return resp """;

        //checkes a script named aScript.groovy exists, if not creates a new one with specified content
        SeleniumTestUtils.createScriptFile("aScript", scriptContent);
        try {
            selenium.login("rsadmin", "changeme");
            selenium.createOnDemandScript("ondemand2", [scriptFile: "aScript"]);

            assertEquals("ondemand2", selenium.getText("name"));
            assertEquals("aScript", selenium.getText("scriptFile"));
            assertEquals("WARN", selenium.getText("logLevel"));
            assertEquals("false", selenium.getText("identifier=logFileOwn"));
            assertEquals("OnDemand", selenium.getText("identifier=type"));
        } finally {
            SeleniumTestUtils.deleteScriptFile("ondemand2")
        }
    }


    public void testCreateAnOnDemandscriptByName()
    {
        selenium.deleteScriptsByFileName("aScript");
        selenium.deleteScriptByName("aScript", false);
        //creates aScript.groovy in RS_HOME/RapidSuite/scripts folder with the following content
        String scriptContent = """import script.*
             def resp ="";
             res = CmdbScript.search("name:aScript")
             res.results.each{
                resp = resp+"scriptName:\${it.name} loglevel: \${it.logLevel} useOwnLogger: \${it.logFileOwn} staticParameter:\${it.staticParam}"
             }
             logger.warn(resp)
             return resp """;

        //checkes a script named aScript.groovy exists, if not creates a new one with specified content
        SeleniumTestUtils.createScriptFile("aScript", scriptContent);
        try {
            selenium.login("rsadmin", "changeme");
            selenium.createOnDemandScript("aScript", [:]);

            verifyEquals("aScript", selenium.getText("name"));
            verifyEquals("aScript", selenium.getText("scriptFile"));
            verifyEquals("WARN", selenium.getText("logLevel"));
            verifyEquals("false", selenium.getText("logFileOwn"));
            verifyEquals("", selenium.getText("identifier=staticParam"));
            verifyEquals("OnDemand", selenium.getText("identifier=type"));
        } finally {
            SeleniumTestUtils.deleteScriptFile("aScript")
        }
    }


    public void testTestAScheduledCronScriptFilesSelTest()
    {
        selenium.deleteScriptsByFileName("cron");
        selenium.deleteScriptByName("scheduled2", false);
        try {
            String expectedMessage = "${System.currentTimeMillis()}${Math.random()}Hello from cron"
            //creates cron.groovy in RS_HOME/RapidSuite/scripts folder with the following content
            String cronScriptContent = """import script.*
                    def resp ="";
                    res = CmdbScript.search("name:scheduled2")
                    res.results.each{
                    resp = resp+"scriptName:\${it.name} loglevel: \${it.logLevel} useOwnLogger: \${it.logFileOwn} staticParameter:\${it.staticParam}"
                    }
                    logger.warn("${expectedMessage}")
                    return resp
                     """;

            //checkes a script named cron.groovy exists, if not creates a new one with specified content
            SeleniumTestUtils.createScriptFile("cron", cronScriptContent);

            // looks RapidServer.log file for old "Hello from cron" entries
            String numberOfMessagesInLogFile = getLogValidatorResult("logs/RapidServer.log", expectedMessage);

            // Creates a scheduled script with attributes
            def cronScriptId = selenium.createCronScript("scheduled2", [cronExpression: "* * * * * ?", enabled: true, scriptFile: "cron", staticParam: "Hello from cron"]);

            // scheduled script attributes will be cheched if it has the right attributes
            assertEquals("scheduled2", selenium.getText("name"));
            assertEquals("cron", selenium.getText("scriptFile"));
            assertEquals("WARN", selenium.getText("logLevel"));
            assertEquals("false", selenium.getText("logFileOwn"));
            assertEquals("Hello from cron", selenium.getText("staticParam"));
            assertEquals("Scheduled", selenium.getText("type"));
            assertEquals("Cron", selenium.getText("scheduleType"));
            assertEquals("0", selenium.getText("startDelay"));
            assertEquals("* * * * * ?", selenium.getText("cronExpression"));
            assertEquals("true", selenium.getText("enabled"));
            Thread.sleep(3000);

            // RapidServer.log file be looked if there is new "Hello from cron" entries
            def numberOfMessagesInFileAfterCronScript = getLogValidatorResult("logs/RapidServer.log", expectedMessage);
            // if the "Hello from cron" number has changed means not equal to the value in 'stored', test will pass
            assertNotEquals(numberOfMessagesInLogFile, numberOfMessagesInFileAfterCronScript);

            selenium.updateScript("scheduled2", [logFileOwn: true]);

            // after update the use own log will be checked if it is true test will pass
            assertEquals("true", selenium.getText("logFileOwn"));


            // file scheduled2.log  will be looked if it has "Hello from cron" entries
            def numberOfMessagesInFileAfterCronOwnLogFile = getLogValidatorResult("logs/scheduled2.log", expectedMessage);
            // file scheduled2.log must have some entries
            assertNotEquals("0", numberOfMessagesInFileAfterCronOwnLogFile);

            // the script will be updated again
            selenium.updateScript("scheduled2", [enabled: false]);

            // after update the enable attribute will be checked if it is false test will pass
            assertEquals("false", selenium.getText("enabled"));
            Thread.sleep(1100);
            def numberOfMessagesInFileAfterDisabling = getLogValidatorResult("logs/scheduled2.log", expectedMessage)

            Thread.sleep(3000);

            // if RapidServer.log has no new entries test will pass
            assertEquals(numberOfMessagesInFileAfterDisabling, getLogValidatorResult("logs/scheduled2.log", expectedMessage));
        } finally {
            selenium.deleteScriptByName("scheduled2", false);
        }
    }



    public void testAScheduledPeriodicScript()
    {
        selenium.deleteScriptsByFileName("periodic");
        selenium.deleteScriptByName("scheduled1", false);
        try{
            String expectedMessage = "${System.currentTimeMillis()}${Math.random()}Hello from periodic"
            //creates periodic.groovy in RS_HOME/RapidSuite/scripts folder with the following content
            def scriptContent = """import script.*

            def resp ="";
            res = CmdbScript.search("name:scheduled1")
            res.results.each{
            resp = resp+"scriptName:\${it.name} loglevel: \${it.logLevel} useOwnLogger: \${it.logFileOwn} staticParameter:\${it.staticParam}"
            }
            logger.warn("${expectedMessage}")
            return resp """;

            //checkes a script named periodic.groovy exists, if not creates a new one with specified content
            SeleniumTestUtils.createScriptFile("periodic", scriptContent);


            String idValue = selenium.createPeriodicScript("scheduled1", [period: "1", staticParam: expectedMessage, enabled: true, scriptFile: "periodic"]);
            verifyEquals("scheduled1", selenium.getText("name"));
            verifyEquals("periodic", selenium.getText("scriptFile"));
            verifyEquals("WARN", selenium.getText("logLevel"));
            verifyEquals("false", selenium.getText("logFileOwn"));
            verifyEquals(expectedMessage, selenium.getText("staticParam"));
            verifyEquals("Scheduled", selenium.getText("type"));
            verifyEquals("Periodic", selenium.getText("scheduleType"));
            verifyEquals("0", selenium.getText("startDelay"));
            verifyEquals("1", selenium.getText("period"));
            verifyEquals("true", selenium.getText("enabled"));

            def numberOfLogMessages = Integer.parseInt(getLogValidatorResult("logs/RapidServer.log", expectedMessage))
            Thread.sleep(5100);
            def numberOfLogMessagesAfterWaitTime = Integer.parseInt(getLogValidatorResult("logs/RapidServer.log", expectedMessage))

            def diff = numberOfLogMessagesAfterWaitTime - numberOfLogMessages
            assertTrue(diff >= 5 && diff <= 7);

            Thread.sleep(3100);
            numberOfLogMessagesAfterWaitTime = Integer.parseInt(getLogValidatorResult("logs/RapidServer.log", expectedMessage))
            diff = numberOfLogMessagesAfterWaitTime - numberOfLogMessages
            assertTrue(diff >= 8 && diff <= 10);

            //disable script
            selenium.updateScript("scheduled1", [enabled: false]);
            assertEquals("false", selenium.getText("enabled"));
            Thread.sleep(1100);
            def numberOfMessagesAfterDisabling = getLogValidatorResult("logs/RapidServer.log", expectedMessage)
            Thread.sleep(3000);
            assertEquals(numberOfMessagesAfterDisabling, getLogValidatorResult("logs/RapidServer.log", expectedMessage));
        }
        finally{
            selenium.deleteScriptByName("scheduled1", false);   
        }
    }


    public void testLoggerParameters()
    {
        selenium.deleteScriptsByFileName("aScript");
        selenium.deleteScriptByName("aScript", false);
        try{
            selenium.runScriptByName(logLevelModifier, [loggerName:"root", level:"DEBUG"])
            def messagePrefix = "${System.currentTimeMillis()}${Math.random()}"
            def scriptContent = """import script.*

                def resp ="${messagePrefix}";
                res = CmdbScript.search("name:aScript")
                res.results.each{
                  resp = resp+"scriptName:\${it.name} loglevel: \${it.logLevel} useOwnLogger: \${it.logFileOwn} staticParameter:\${it.staticParam}"
                }
                logger.warn(resp)
                return resp""";

            SeleniumTestUtils.createScriptFile("aScript", scriptContent);

            String idValue = selenium.createOnDemandScript("aScript");
            selenium.updateScript("aScript", [logLevel: "DEBUG", logFileOwn: true]);
            assertEquals("aScript", selenium.getText("name"));
            assertEquals("aScript", selenium.getText("scriptFile"));
            assertEquals("DEBUG", selenium.getText("logLevel"));
            assertEquals("true", selenium.getText("logFileOwn"));
            assertEquals("OnDemand", selenium.getText("type"));

            selenium.runScriptByName("aScript");

            def numberOfMessages = getLogValidatorResult("logs/aScript.log", "${messagePrefix}scriptName:aScript loglevel: DEBUG useOwnLogger: true staticParameter:")
            def numberOfMessagesInRapidServerLog = getLogValidatorResult("logs/RapidServer.log", "${messagePrefix}scriptName:aScript loglevel: DEBUG useOwnLogger: true staticParameter:")
            assertNotEquals("0", numberOfMessages);
            assertEquals("0", numberOfMessagesInRapidServerLog);

            selenium.updateScript("aScript", [logFileOwn: false]);

            assertEquals("aScript", selenium.getText("name"));
            assertEquals("aScript", selenium.getText("scriptFile"));
            assertEquals("DEBUG", selenium.getText("logLevel"));
            assertEquals("false", selenium.getText("logFileOwn"));
            assertEquals("OnDemand", selenium.getText("type"));

            selenium.runScriptByName("aScript");

            numberOfMessagesInRapidServerLog = getLogValidatorResult("logs/RapidServer.log", "${messagePrefix}scriptName:aScript loglevel: DEBUG useOwnLogger: false staticParameter:")
            assertNotEquals("0", numberOfMessagesInRapidServerLog);
        }finally{
            selenium.deleteScriptByName("aScript", false);
        }
    }

    private getLogValidatorResult(String logFile, expectedMessage)
    {
        selenium.runScriptByName(logValidatorName, [file: logFile, expectedMessage: expectedMessage]);
        String numberOfMessagesInLogFile = selenium.getText("//body")
        return numberOfMessagesInLogFile
    }



    private void createLogLevelModifier()
    {
        selenium.deleteScriptByName(logLevelModifier, false);
        //creates logValidator.groovy in RS_HOME/RapidSuite/scripts folder with the following content
        String scriptContent = """
              import org.apache.log4j.*;
              if(params.loggerName == "root")
                {
                    Logger.getRootLogger().setLevel(Level.toLevel(params.level));
                }
                else{
                        Logger.getLogger(params.loggerName).setLevel(Level.toLevel(params.level));
                }
               """;

        //checkes a script named logValidator.groovy exists, if not creates a new one with specified content
        SeleniumTestUtils.createScriptFile(logLevelModifier, scriptContent);
        selenium.createOnDemandScript(logLevelModifier);

    }

    private void createLogValidator()
    {
        selenium.deleteScriptByName(logValidatorName, false);
        //creates logValidator.groovy in RS_HOME/RapidSuite/scripts folder with the following content
        String scriptContent = """ import script.*
              import org.apache.commons.lang.StringUtils
              def logFile = new File(params.file);
              def log = logFile.getText();
              return StringUtils.countMatches (log, params.expectedMessage)
               """;

        //checkes a script named logValidator.groovy exists, if not creates a new one with specified content
        SeleniumTestUtils.createScriptFile(logValidatorName, scriptContent);
        selenium.createOnDemandScript(logValidatorName);

    }

}



