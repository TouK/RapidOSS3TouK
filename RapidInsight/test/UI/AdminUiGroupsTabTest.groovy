import com.ifountain.rcmdb.test.util.SeleniumTestCase
import com.ifountain.rcmdb.test.util.SeleniumTestUtils
import utils.CommonUiTestUtils

/**
* Created by IntelliJ IDEA.
* User: fadime
* Date: Jun 23, 2009
* Time: 2:20:24 AM
* To change this template use File | Settings | File Templates.
*/
class AdminUiGroupsTabTest extends SeleniumTestCase
{

    void setUp() throws Exception
    {
        super.setUp("http://${SeleniumTestUtils.getRIHost()}:${SeleniumTestUtils.getRIPort()}/RapidSuite/",
                SeleniumTestUtils.getSeleniumBrowser());
        selenium.logout()
        selenium.login("rsadmin", "changeme");
        selenium.runScriptByName("removeAll")
        selenium.deleteAllGroups();
        selenium.deleteAllUsers();
        deleteSnmpConnectors();
    }



    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        selenium.login("rsadmin", "changeme");
        deleteSnmpConnectors();
        selenium.logout()
    }


    public void testCreateAGroup()
    {
        selenium.login("rsadmin", "changeme")
        String firstGroupsUd = selenium.createGroup("nmd1Group", "User", []);
        assertEquals("Group " + firstGroupsUd + " created", selenium.getText("pageMessage"))

        String secondGroupsUd = selenium.createGroup("nmd2Group", "User", []);
        assertEquals("Group " + secondGroupsUd + " created", selenium.getText("pageMessage"))
    }




    public void testScriptAuthorizationMechanism()
    {
        selenium.deleteScriptsByFileName("HelloWorld");
        def stringToBeReturned = "HelloWorld"
        def scriptContent = "return '${stringToBeReturned}'"
        selenium.deleteScriptByName ("HelloWorld", false);
        SeleniumTestUtils.createScriptFile ("HelloWorld", scriptContent);
        selenium.deleteAllUsers();
        selenium.login("rsadmin", "changeme")

        def defaultGroupId  = selenium.createGroup("default", null, []);
        def user1Id  = selenium.createUser("user1", "user1", ["default"]);
        def group1Id = selenium.createGroup("group1", "User", ["user1"]);
        def user2Id  = selenium.createUser("user2", "user2", ["default"]);
        def group2Id = selenium.createGroup("group2", "User", ["user2"]);


        def scriptId = selenium.createOnDemandScript("HelloWorld", [:], ["group1"]);
        selenium.reloadScriptByName("HelloWorld");
        selenium.runScriptByName ("HelloWorld");
        assertTrue(selenium.isTextPresent(stringToBeReturned));

        selenium.login("user1", "user1")
        selenium.runScriptByName("HelloWorld");
        assertTrue(selenium.isTextPresent(stringToBeReturned));

        selenium.login("user2", "user2")
        selenium.runScriptByName("HelloWorld");
        def line = selenium.getLocation()
        assertTrue (selenium.getLocation().indexOf("/auth/unauthorized") >= 0);


        selenium.login("rsadmin", "changeme")
        selenium.updateScript("HelloWorld", [enabledForAllGroups:true], []);

        selenium.runScriptByName("HelloWorld")
        verifyTrue(selenium.isTextPresent(stringToBeReturned));
        selenium.logout()


        selenium.login("user1", "user1")
        selenium.runScriptByName("HelloWorld")
        assertTrue(selenium.isTextPresent(stringToBeReturned));
        selenium.logout()

        selenium.login("user2", "user2")
        selenium.runScriptByName("HelloWorld")
        assertTrue(selenium.isTextPresent(stringToBeReturned));
        selenium.logout()
        selenium.login("rsadmin", "changeme")
        selenium.deleteScriptById(scriptId)
    }


    public void testUpdateAGroupWithSegmentFilter()
    {
        selenium.deleteScriptByName("GenerateSnmpTraps", false);
        

        def generateSnmpTrapScriptContent = generateSnmpScriptContent()
        SeleniumTestUtils.createScriptFile ("GenerateSnmpTraps", generateSnmpTrapScriptContent);
        def eventSnmpScriptContent = eventSnmpContent()
        SeleniumTestUtils.createScriptFile ("EventSnmpListener", eventSnmpScriptContent);

        selenium.login("rsadmin", "changeme", "/script/list");

        selenium.click("link=SNMP");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=New SnmpConnector");
        selenium.waitForPageToLoad("30000");
        selenium.type("name", "snmp1");
        selenium.type("scriptFile", "EventSnmpListener");
        selenium.clickAndWait("//input[@value='Create']");
        assertTrue ("expected show view but was ${selenium.getLocation()}", selenium.getLocation().indexOf("snmpConnector/show") >= 0);
        def snmpId = CommonUiTestUtils.getIdFromlocation(selenium.getLocation())
        selenium.clickAndWait("link=SNMP");
        selenium.openAndWait("/RapidSuite/snmpConnector/show/" + snmpId)
        selenium.clickAndWait("_action_StartConnector");

        selenium.createOnDemandScript("GenerateSnmpTraps", [:], []);
        selenium.runScriptByName("GenerateSnmpTraps", [:], "120000");

        def nmd1GroupId  = selenium.createGroup("nmd1Users", "User", []);
        def nmd2GroupId  = selenium.createGroup("nmd2Users", "User", []);
        def nmd1UserId  = selenium.createUser("nmd1User", "nmd1User", ["nmd1Users"]);
        def nmd2UserId  = selenium.createUser("nmd2User", "nmd2User", ["nmd2Users"]);

        selenium.updateGroupById(nmd1GroupId, [segmentFilter:"NMD1"]);
        assertEquals("Group " + nmd1GroupId + " updated", selenium.getText("pageMessage"))

        selenium.updateGroupById(nmd2GroupId, [segmentFilter:"NMD2"]);
        assertEquals("Group " + nmd2GroupId + " updated", selenium.getText("pageMessage"))


        selenium.logout()
        selenium.login("nmd1User", "nmd1User")
        Thread.sleep(5000);
        assertTrue(selenium.isTextPresent("NMD1"));
        assertFalse(selenium.isTextPresent("NMD2"));
        selenium.logout()
        selenium.login("nmd2User", "nmd2User")
        Thread.sleep(5000);
        assertTrue(selenium.isTextPresent("NMD2"));
        assertFalse(selenium.isTextPresent("NMD1"));
        selenium.logout()
        selenium.login("rsadmin", "changeme");

        selenium.deleteScriptByName("GenerateSnmpTraps");
        SeleniumTestUtils.deleteScriptFile("EventSnmpListener")
        SeleniumTestUtils.deleteScriptFile("GenerateSnmpTraps.groovy")

    }

    private deleteSnmpConnectors()
    {
        def res = CommonUiTestUtils.search(selenium, "connector.SnmpConnector", "alias:*");
        res.each{
            CommonUiTestUtils.deleteInstance (selenium, "/RapidSuite/snmpConnector/show/" + it.id);
        }
    }


    public String eventSnmpContent()
    {
        return """import datasource.*
    def getParameters(){
       return [:]}
    def init(){ }
    def cleanUp(){ }
    def update(eventTrap){
        def currentDate = new Date();
        def deviceid = eventTrap.Varbinds[0].Value.toString();
        def eventType = eventTrap.Varbinds[1].Value;
        def severity = eventTrap.Varbinds[2].Value;
        def source = eventTrap.Varbinds[6].Value;
        def trapTime = Long.parseLong(eventTrap.Timestamp)*1000;
        def name = deviceid+"|"+eventType+"|"+currentDate.toString();
        def event;
        switch(eventType) {
             case "HardDown" :
             case "Down" :
                def result = RsRiEvent.search("elementName:\$deviceid identifier:\$eventType ", max:10);
                if (result.total==0){
                    event = RsRiEvent.add("name":name,"elementName":deviceid,"identifier":eventType,"createdAt":trapTime,"changedAt":trapTime,"severity":severity,"source":source);
                }
                else{
                    event = result.results[0];
                    event.update("changedAt":trapTime,"severity":severity); }
                RsEventJournal.add(eventId:event.id,eventName:event.identifier,rsTime:currentDate);
                break;
             case "Up" :
                def result = RsRiEvent.search("elementName:\$deviceid identifier:\$eventType ", max:10);
                if (result.total==0){
                    event = RsRiEvent.add("name":name,"elementName":deviceid,"identifier":eventType,"createdAt":trapTime,"changedAt":trapTime,"severity":severity,"source":source);
                }
                else{
                    event = result.results[0];
                    event.update("clearedAt":trapTime); }
                RsEventJournal.add(eventId:event.id,eventName:event.identifier,rsTime:currentDate);
                event.clear();
                break;
             default: println "Need to implement trap code for new event type \${eventType}";
         }
          } """;

    }

    public String generateSnmpScriptContent()
    {
        return """
                import com.ifountain.rcmdb.snmp.*
                import snmp.SnmpUtils
                long sec = (System.currentTimeMillis())/1000;
                SnmpUtils.sendV1Trap("localhost/162", "localhost", "public", "1.3.6.1.2.1.11",sec,1,1,[["OID":"1.3.6.1.2.1.1.3.0", "Value":"41"],["OID":"1.3.6.1.2.1.1.3.1", "Value":"Up"],["OID":"1.3.6.1.2.1.1.3.2", "Value":"0"],["OID":"1.3.6.1.2.1.1.3.3", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.4", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.5", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.6", "Value":"NMD1"]]);
                sec++;
                SnmpUtils.sendV1Trap("localhost/162", "localhost", "public", "1.3.6.1.2.1.11",sec,1,1,[["OID":"1.3.6.1.2.1.1.3.0", "Value":"5"],["OID":"1.3.6.1.2.1.1.3.1", "Value":"Down"],["OID":"1.3.6.1.2.1.1.3.2", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.3", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.4", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.5", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.6", "Value":"NMD1"]]);
                sec++;
                SnmpUtils.sendV1Trap("localhost/162", "localhost", "public", "1.3.6.1.2.1.11",sec,1,1,[["OID":"1.3.6.1.2.1.1.3.0", "Value":"10"],["OID":"1.3.6.1.2.1.1.3.1", "Value":"Down"],["OID":"1.3.6.1.2.1.1.3.2", "Value":"2"],["OID":"1.3.6.1.2.1.1.3.3", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.4", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.5", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.6", "Value":"NMD1"]]);
                sec++;
                SnmpUtils.sendV1Trap("localhost/162", "localhost", "public", "1.3.6.1.2.1.11",sec,1,1,[["OID":"1.3.6.1.2.1.1.3.0", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.1", "Value":"Down"],["OID":"1.3.6.1.2.1.1.3.2", "Value":"2"],["OID":"1.3.6.1.2.1.1.3.3", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.4", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.5", "Value":"2"],["OID":"1.3.6.1.2.1.1.3.6", "Value":"NMD1"]]);
                sec++;
                SnmpUtils.sendV1Trap("localhost/162", "localhost", "public", "1.3.6.1.2.1.11",sec,1,1,[["OID":"1.3.6.1.2.1.1.3.0", "Value":"5"],["OID":"1.3.6.1.2.1.1.3.1", "Value":"Up"],["OID":"1.3.6.1.2.1.1.3.2", "Value":"0"],["OID":"1.3.6.1.2.1.1.3.3", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.4", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.5", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.6", "Value":"NMD1"]]);
                sec++;
                SnmpUtils.sendV1Trap("localhost/162", "localhost", "public", "1.3.6.1.2.1.11",sec,1,1,[["OID":"1.3.6.1.2.1.1.3.0", "Value":"10"],["OID":"1.3.6.1.2.1.1.3.1", "Value":"HardDown"],["OID":"1.3.6.1.2.1.1.3.2", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.3", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.4", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.5", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.6", "Value":"NMD1"]]);
                sec++;
                SnmpUtils.sendV1Trap("localhost/162", "localhost", "public", "1.3.6.1.2.1.11",sec,1,1,[["OID":"1.3.6.1.2.1.1.3.0", "Value":"30"],["OID":"1.3.6.1.2.1.1.3.1", "Value":"Down"],["OID":"1.3.6.1.2.1.1.3.2", "Value":"2"],["OID":"1.3.6.1.2.1.1.3.3", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.4", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.5", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.6", "Value":"NMD2"]]);
                sec++;
                SnmpUtils.sendV1Trap("localhost/162", "localhost", "public", "1.3.6.1.2.1.11",sec,1,1,[["OID":"1.3.6.1.2.1.1.3.0", "Value":"27"],["OID":"1.3.6.1.2.1.1.3.1", "Value":"HardDown"],["OID":"1.3.6.1.2.1.1.3.2", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.3", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.4", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.5", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.6", "Value":"NMD1"]]);
                sec++;
                SnmpUtils.sendV1Trap("localhost/162", "localhost", "public", "1.3.6.1.2.1.11",sec,1,1,[["OID":"1.3.6.1.2.1.1.3.0", "Value":"33"],["OID":"1.3.6.1.2.1.1.3.1", "Value":"Down"],["OID":"1.3.6.1.2.1.1.3.2", "Value":"2"],["OID":"1.3.6.1.2.1.1.3.3", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.4", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.5", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.6", "Value":"NMD2"]]);
                sec++;
                SnmpUtils.sendV1Trap("localhost/162", "localhost", "public", "1.3.6.1.2.1.11",sec,1,1,[["OID":"1.3.6.1.2.1.1.3.0", "Value":"30"],["OID":"1.3.6.1.2.1.1.3.1", "Value":"HardDown"],["OID":"1.3.6.1.2.1.1.3.2", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.3", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.4", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.5", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.6", "Value":"NMD2"]]);
                sec++;
                SnmpUtils.sendV1Trap("localhost/162", "localhost", "public", "1.3.6.1.2.1.11",sec,1,1,[["OID":"1.3.6.1.2.1.1.3.0", "Value":"8"],["OID":"1.3.6.1.2.1.1.3.1", "Value":"Down"],["OID":"1.3.6.1.2.1.1.3.2", "Value":"2"],["OID":"1.3.6.1.2.1.1.3.3", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.4", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.5", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.6", "Value":"NMD2"]]);
                return "Completed event trap generation"
            """
    }

}