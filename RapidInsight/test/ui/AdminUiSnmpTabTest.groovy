import com.ifountain.rcmdb.test.util.SeleniumTestCase
import com.ifountain.rcmdb.test.util.SeleniumTestUtils
import utils.CommonUiTestUtils

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

    void testConnectorWithScript(){
        def lookupName = "trapCount";
        def scriptContent = """
            RsEvent.removeAll();
            RsHistoricalEvent.removeAll();
            RsEventJournal.removeAll();
        """
        selenium.executeScript(scriptContent);

        scriptContent = """
            def getParameters(){
                return [:]
            }
            TRAP_COUNT = 0;
            LOOKUP_NAME = '${lookupName}'
            RsLookup.get(name:LOOKUP_NAME)?.remove();
            def init(){}
            def cleanUp(){}
            def update(eventTrap){
                println "TRAP_COUNT: " + TRAP_COUNT
                def currentDate = new Date();
                def deviceid = eventTrap.Varbinds[0].Value.toString();
                def eventType = eventTrap.Varbinds[1].Value;
                def severity = eventTrap.Varbinds[2].Value;
                def source = eventTrap.Varbinds[6].Value;
                def trapTime = Long.parseLong(eventTrap.Timestamp)*1000;
                def name = deviceid+'|'+eventType+'|'+currentDate.toString();
                def event;
                switch(eventType) {
                     case 'Down' :
                        TRAP_COUNT ++;
                        event = RsRiEvent.add(name:name,elementName:deviceid,identifier:eventType,createdAt:trapTime,changedAt:trapTime,severity:severity,source:source);
                        RsEventJournal.add(eventId:event.id,eventName:event.identifier,rsTime:currentDate);
                        break;
                     case 'Up' :
                        TRAP_COUNT ++;
                        event = RsRiEvent.add(name:name,elementName:deviceid,identifier:eventType,createdAt:trapTime,changedAt:trapTime,severity:severity,source:source);
                        RsEventJournal.add(eventId:event.id,eventName:event.identifier,rsTime:currentDate);
                        event.clear();
                        break;
                     default: println 'Need to implement trap code for new event type \${eventType}';
                 }
                 if(TRAP_COUNT == 8){
                    RsLookup.add(name:LOOKUP_NAME, value:TRAP_COUNT);
                 }
            }
        """
        def connectorName = "snmp";
        def host = "0.0.0.0";
        def port = "162"
        def scriptName = "snmpTestListener";
        SeleniumTestUtils.createScriptFile(scriptName, scriptContent)
        def connectorId = selenium.createSnmpConnector(connectorName, host, port, scriptName, "", "DEBUG", true)
        selenium.startSnmpConnectorById(connectorId, true);
        Thread.sleep(1000);

        scriptContent = """
            import com.ifountain.rcmdb.snmp.*
            import snmp.SnmpUtils

            long sec = (System.currentTimeMillis())/1000;
            SnmpUtils.sendV1Trap("localhost/162", "localhost", "public", "1.3.6.1.2.1.11",sec,1,1,[["OID":"1.3.6.1.2.1.1.3.0", "Value":"41"],["OID":"1.3.6.1.2.1.1.3.1", "Value":"Up"],["OID":"1.3.6.1.2.1.1.3.2", "Value":"0"],["OID":"1.3.6.1.2.1.1.3.3", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.4", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.5", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.6", "Value":"NMD1"]]);
            sleep(1000)
            sec = (System.currentTimeMillis())/1000;
            SnmpUtils.sendV1Trap("localhost/162", "localhost", "public", "1.3.6.1.2.1.11",sec,1,1,[["OID":"1.3.6.1.2.1.1.3.0", "Value":"5"],["OID":"1.3.6.1.2.1.1.3.1", "Value":"Down"],["OID":"1.3.6.1.2.1.1.3.2", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.3", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.4", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.5", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.6", "Value":"NMD1"]]);
            sleep(1000)
            sec = (System.currentTimeMillis())/1000;
            SnmpUtils.sendV1Trap("localhost/162", "localhost", "public", "1.3.6.1.2.1.11",sec,1,1,[["OID":"1.3.6.1.2.1.1.3.0", "Value":"10"],["OID":"1.3.6.1.2.1.1.3.1", "Value":"Down"],["OID":"1.3.6.1.2.1.1.3.2", "Value":"2"],["OID":"1.3.6.1.2.1.1.3.3", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.4", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.5", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.6", "Value":"NMD1"]]);
            sleep(1000)
            sec = (System.currentTimeMillis())/1000;
            SnmpUtils.sendV1Trap("localhost/162", "localhost", "public", "1.3.6.1.2.1.11",sec,1,1,[["OID":"1.3.6.1.2.1.1.3.0", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.1", "Value":"Down"],["OID":"1.3.6.1.2.1.1.3.2", "Value":"2"],["OID":"1.3.6.1.2.1.1.3.3", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.4", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.5", "Value":"2"],["OID":"1.3.6.1.2.1.1.3.6", "Value":"NMD1"]]);
            sleep(1000)
            sec = (System.currentTimeMillis())/1000;
            SnmpUtils.sendV1Trap("localhost/162", "localhost", "public", "1.3.6.1.2.1.11",sec,1,1,[["OID":"1.3.6.1.2.1.1.3.0", "Value":"5"],["OID":"1.3.6.1.2.1.1.3.1", "Value":"Up"],["OID":"1.3.6.1.2.1.1.3.2", "Value":"0"],["OID":"1.3.6.1.2.1.1.3.3", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.4", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.5", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.6", "Value":"NMD1"]]);
            sleep(1000)
            sec = (System.currentTimeMillis())/1000;
            SnmpUtils.sendV1Trap("localhost/162", "localhost", "public", "1.3.6.1.2.1.11",sec,1,1,[["OID":"1.3.6.1.2.1.1.3.0", "Value":"30"],["OID":"1.3.6.1.2.1.1.3.1", "Value":"Down"],["OID":"1.3.6.1.2.1.1.3.2", "Value":"2"],["OID":"1.3.6.1.2.1.1.3.3", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.4", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.5", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.6", "Value":"NMD2"]]);
            sleep(1000)
            sec = (System.currentTimeMillis())/1000;
            SnmpUtils.sendV1Trap("localhost/162", "localhost", "public", "1.3.6.1.2.1.11",sec,1,1,[["OID":"1.3.6.1.2.1.1.3.0", "Value":"33"],["OID":"1.3.6.1.2.1.1.3.1", "Value":"Down"],["OID":"1.3.6.1.2.1.1.3.2", "Value":"2"],["OID":"1.3.6.1.2.1.1.3.3", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.4", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.5", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.6", "Value":"NMD2"]]);
            sleep(1000)
            sec = (System.currentTimeMillis())/1000;
            SnmpUtils.sendV1Trap("localhost/162", "localhost", "public", "1.3.6.1.2.1.11",sec,1,1,[["OID":"1.3.6.1.2.1.1.3.0", "Value":"8"],["OID":"1.3.6.1.2.1.1.3.1", "Value":"Down"],["OID":"1.3.6.1.2.1.1.3.2", "Value":"2"],["OID":"1.3.6.1.2.1.1.3.3", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.4", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.5", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.6", "Value":"NMD2"]]);
            return "Completed event trap generation"
        """
        selenium.executeScript(scriptContent);
        checkLookup(lookupName, 30000)
        selenium.runScript("saveHistoricalEventCache");
        assertTrue(CommonUiTestUtils.search(selenium, "RsRiEvent", "alias:*").size() > 0)
        assertTrue(CommonUiTestUtils.search(selenium, "RsRiHistoricalEvent", "alias:*").size() > 0)
        assertTrue(CommonUiTestUtils.search(selenium, "RsEventJournal", "alias:*").size() > 0)
    }

    void testConnectorScriptReload(){
        def lookupName = "trapCount";
        def connectorScriptContent = """
            def getParameters(){
                return [:]
            }
            LOOKUP_NAME = '${lookupName}'
            RsLookup.get(name:LOOKUP_NAME)?.remove();
            def init(){}
            def cleanUp(){}
            def update(eventTrap){
               RsLookup.add(name:LOOKUP_NAME, value:"value");
            }
        """
        def connectorName = "snmp";
        def host = "0.0.0.0";
        def port = "162"
        def scriptName = "snmpTestListener";
        SeleniumTestUtils.createScriptFile(scriptName, connectorScriptContent)
        def connectorId = selenium.createSnmpConnector(connectorName, host, port, scriptName, "", "DEBUG", true)
        selenium.startSnmpConnectorById(connectorId, true);
        Thread.sleep(1000);

        def scriptContent = """
            import com.ifountain.rcmdb.snmp.*
            import snmp.SnmpUtils

            long sec = (System.currentTimeMillis())/1000;
            SnmpUtils.sendV1Trap("localhost/162", "localhost", "public", "1.3.6.1.2.1.11",sec,1,1,[["OID":"1.3.6.1.2.1.1.3.0", "Value":"41"],["OID":"1.3.6.1.2.1.1.3.1", "Value":"Up"],["OID":"1.3.6.1.2.1.1.3.2", "Value":"0"],["OID":"1.3.6.1.2.1.1.3.3", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.4", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.5", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.6", "Value":"NMD1"]]);
            return "Completed event trap generation"
        """
        selenium.executeScript(scriptContent);
        checkLookup(lookupName, 5000)
        selenium.stopSnmpConnectorById(connectorId, true);

        
        def newLookupName =  "newTrapCount";
        SeleniumTestUtils.createScriptFile(scriptName, connectorScriptContent.replaceAll(lookupName, newLookupName));
        selenium.reloadSnmpConnectorById(connectorId, connectorName, true);
        selenium.startSnmpConnectorById(connectorId, true);
        Thread.sleep(1000);
        selenium.executeScript(scriptContent);
        checkLookup(newLookupName, 5000)
    }

    def checkLookup(lookupName, maxSleepTime){
        def sleepTime = 0
        boolean lookupCreated = false;
        while(sleepTime < maxSleepTime){
            def results = CommonUiTestUtils.search(selenium, "RsLookup", "name:${lookupName}")
            if(results.size() > 0){
                lookupCreated = true;
                break;
            }
            Thread.sleep(1000);
            sleepTime +=1000
        }

        if(!lookupCreated){
            throw new Exception("Listening script could not finish in ${maxSleepTime} milliseconds");
        }
    }

}