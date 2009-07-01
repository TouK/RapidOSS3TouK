import com.ifountain.rcmdb.test.util.SeleniumTestCase
import com.ifountain.rcmdb.test.util.SeleniumTestUtils

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
    }



    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        logout()
    }


    private void logout()
    {
        selenium.click("link=Logout");
    }

    private void login(String name, String passWord)
    {
        selenium.open("/RapidSuite/auth/login?targetUri=%2Fadmin.gsp&format=html");
        selenium.waitForPageToLoad("30000");
        selenium.type("login", name);
        selenium.type("password", passWord);
        selenium.click("//input[@value='Sign in']");
        selenium.waitForPageToLoad("30000");
    }



    private String createNewGroup(String name)
    {
        selenium.click("link=Groups");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=New Group");
        selenium.waitForPageToLoad("30000");
        selenium.type("name", name);
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        return selenium.getText("document.getElementById('id')")
    }

    private void deleteGroup(String groupId)
    {
        selenium.open("/RapidSuite/group/show/" + groupId);
        selenium.waitForPageToLoad("30000");
        selenium.click("_action_Delete");
        selenium.waitForPageToLoad("30000");
        assertTrue(selenium.getConfirmation().matches("^Are you sure[\\s\\S]\$"));
        selenium.waitForPageToLoad("30000");
    }

    private void deleteUser(String userId)
    {
        selenium.open("/RapidSuite/rsUser/show/" + userId);
        selenium.waitForPageToLoad("30000");
        selenium.click("_action_Delete");
        selenium.waitForPageToLoad("30000");
        assertTrue(selenium.getConfirmation().matches("^Are you sure[\\s\\S]\$"));
        selenium.waitForPageToLoad("30000");
    }

    public void newGroup()
    {
        selenium.click("link=Groups");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=New Group");
        selenium.waitForPageToLoad("30000");
    }

    public void newUser()
    {
        selenium.click("link=Users");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=New User");
        selenium.waitForPageToLoad("30000");
    }

    private void deleteSNMPConnector(String snmpId)
    {
        selenium.open("/RapidSuite/snmpConnector/show/" + snmpId);
        selenium.waitForPageToLoad("30000");
        selenium.click("_action_Delete");
        selenium.waitForPageToLoad("30000");
        assertTrue(selenium.getConfirmation().matches("^Are you sure[\\s\\S]\$"));
        selenium.waitForPageToLoad("30000");
    }

    public void testCreateAGroup()
    {
        login("rsadmin", "changeme")
        newGroup()

        selenium.type("name", "nmd1Group");
        selenium.select("role.id", "label=User");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        String firstGroupsUd = selenium.getText("document.getElementById('id')")
        assertEquals("Group " + firstGroupsUd + " created", selenium.getText("pageMessage"))

        newGroup()
        selenium.type("name", "nmd2Group");
        selenium.select("role.id", "label=User");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        String secondGroupsUd = selenium.getText("document.getElementById('id')")
        assertEquals("Group " + secondGroupsUd + " created", selenium.getText("pageMessage"))

        deleteGroup(firstGroupsUd)
        deleteGroup(secondGroupsUd)
    }

    private void deleteScript(String id) {
        selenium.open("/RapidSuite/script/show/" + id);
        selenium.click("_action_Delete");
        assertTrue(selenium.getConfirmation().matches("^Are you sure[\\s\\S]\$"));
    }

    public String userId()
    {
        def line = selenium.getLocation()
        def splitted = new String[3]
        splitted = line.split("RapidSuite/rsUser/show/")
        return splitted[1]
    }

    private void deleteScriptFile(String name)
    {
        File file = new File("${SeleniumTestUtils.getRsHome()}/RapidSuite/scripts/" + name)
        if (file.exists())
            file.delete();
    }


    public String groupId()
    {
        return selenium.getText("document.getElementById('id')")
    }

    public void testScriptAuthorizationMechanism()
    {

        login("rsadmin", "changeme")

        newGroup()
        selenium.type("name", "default");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        def defaultGroupId = groupId()

        newUser()
        selenium.type("username", "user1");
        selenium.type("password1", "user1");
        selenium.type("password2", "user1");
        selenium.addSelection("availablegroupsSelect", "label=default");
        selenium.click("//button[@type='button']");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        def user1Id = userId()


        newGroup()
        selenium.type("name", "group1");
        selenium.select("role.id", "label=User");
        selenium.addSelection("availableusersSelect", "label=user1");
        selenium.click("//button[@type='button']");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        def group1Id = groupId()


        newUser()
        selenium.type("username", "user2");
        selenium.type("password1", "user2");
        selenium.type("password2", "user2");
        selenium.addSelection("availablegroupsSelect", "label=default");
        selenium.click("//button[@type='button']");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        def user2Id = userId()

        newGroup()
        selenium.type("name", "group2");
        selenium.select("role.id", "label=User");
        selenium.addSelection("availableusersSelect", "label=user2");
        selenium.click("//button[@type='button']");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        def group2Id = groupId()

        selenium.click("link=Scripts");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=New Script");
        selenium.waitForPageToLoad("30000");
        selenium.type("name", "HelloWorld");
        selenium.addSelection("availableallowedGroupsSelect", "label=group1");
        selenium.click("//button[@type='button']");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        def scriptId = selenium.getText("document.getElementById('id')")

        selenium.click("_action_Run");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Hello World!"));


        login("user1", "user1")
        selenium.open("/RapidSuite/script/run/HelloWorld");
        verifyTrue(selenium.isTextPresent("Hello World!"));


        login("user2", "user2")
        selenium.open("/RapidSuite/script/run/HelloWorld");
        def line = selenium.getLocation()
        def splitted = new String[3]
        splitted = line.split("RapidSuite")
        assertEquals("/auth/unauthorized", splitted[1]);


        login("rsadmin", "changeme")
        selenium.open("/RapidSuite/script/list");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=Scripts");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=HelloWorld");
        selenium.waitForPageToLoad("30000");
        selenium.click("_action_Edit");
        selenium.waitForPageToLoad("30000");
        selenium.click("//input[@id='enabledForAllGroups']");
        selenium.click("_action_Update");
        selenium.waitForPageToLoad("30000");

        selenium.click("_action_Run");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Hello World!"));

        selenium.open("/RapidSuite/script/list");
        logout()
        selenium.waitForPageToLoad("30000");


        login("user1", "user1")
        selenium.open("/RapidSuite/script/run/HelloWorld");
        verifyTrue(selenium.isTextPresent("Hello World!"));

        login("user2", "user2")
        selenium.open("/RapidSuite/script/run/HelloWorld");
        verifyTrue(selenium.isTextPresent("Hello World!"));


        login("rsadmin", "changeme");
        deleteGroup(defaultGroupId)
        deleteGroup(group1Id)
        deleteGroup(group2Id)
        deleteUser(user1Id)
        deleteUser(user2Id)
        deleteScript(scriptId)

    }
    public String findId(String parser)
    {
        def line = selenium.getLocation()
        def splitted = new String[3]
        splitted = line.split(parser)
        return splitted[1]
    }
    private void createScriptFile(String path, String scriptContent)
    {
        File file = new File(path)
        if (file.exists())
            file.delete();
        SeleniumTestUtils.createScript(path, scriptContent)
    }


    public void testUpdateAGroupWithSegmentFilter()
    {

        def testScriptContent = generateSnmpScriptContent()
        createScriptFile("${SeleniumTestUtils.getRsHome()}/RapidSuite/scripts/GenerateSnmpTraps.groovy", testScriptContent);
        def scriptContent = eventSnmpContent()
        createScriptFile("${SeleniumTestUtils.getRsHome()}/RapidSuite/scripts/EventSnmpListener.groovy", scriptContent);

        login("rsadmin", "changeme");

        selenium.click("link=SNMP");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=New SnmpConnector");
        selenium.waitForPageToLoad("30000");
        selenium.type("name", "snmp1");
        selenium.type("scriptFile", "EventSnmpListener");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        def snmpId = findId("RapidSuite/snmpConnector/show/")
        selenium.click("link=SNMP");
        selenium.waitForPageToLoad("30000");
        selenium.open("/RapidSuite/snmpConnector/show/" + snmpId)
        selenium.click("_action_StartConnector");
        selenium.waitForPageToLoad("30000");

        selenium.click("link=Scripts");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=New Script");
        selenium.waitForPageToLoad("30000");
        selenium.type("name", "GenerateSnmpTraps");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        def GenerateSnmpTrapsIdValue = findId("RapidSuite/script/show/")
        selenium.click("_action_Run");
        Thread.sleep(180000);


        selenium.open("/RapidSuite/script/list");
        newGroup()
        selenium.type("name", "nmd1Users");
        selenium.select("role.id", "label=User");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        def nmd1GroupId = groupId()

        newGroup()
        selenium.type("name", "nmd2Users");
        selenium.select("role.id", "label=User");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        def nmd2GroupId = groupId()

        newUser()
        selenium.type("username", "nmd1User");
        selenium.addSelection("availablegroupsSelect", "label=nmd1Users");
        selenium.click("//button[@type='button']");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        def nmd1UserId = userId()

        newUser()
        selenium.type("username", "nmd2User");
        selenium.addSelection("availablegroupsSelect", "label=nmd2Users");
        selenium.click("//button[@type='button']");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        def nmd2UserId = userId()

        selenium.open("/RapidSuite/group/show/" + nmd1GroupId);
        selenium.waitForPageToLoad("30000");
        selenium.click("_action_Edit");
        selenium.waitForPageToLoad("30000");
        selenium.type("segmentFilter", "NMD1");
        selenium.click("_action_Update");
        selenium.waitForPageToLoad("30000");
        assertEquals("Group " + nmd1GroupId + " updated", selenium.getText("pageMessage"))

        selenium.open("/RapidSuite/group/show/" + nmd2GroupId);
        selenium.click("_action_Edit");
        selenium.waitForPageToLoad("30000");
        selenium.type("segmentFilter", "NMD2");
        selenium.click("_action_Update");
        selenium.waitForPageToLoad("30000");
        assertEquals("Group " + nmd2GroupId + " updated", selenium.getText("pageMessage"))


        logout()
        selenium.open("/RapidSuite/auth/login?targetUri=%2Findex.gsp&format=html");
        selenium.waitForPageToLoad("30000");
        selenium.type("login", "nmd1User");
        selenium.click("//input[@value='Sign in']");
        Thread.sleep(15000);
        verifyTrue(selenium.isTextPresent("NMD1"));
        verifyFalse(selenium.isTextPresent("NMD2"));
        selenium.click("link=Logout");
        selenium.waitForPageToLoad("30000");
        selenium.open("/RapidSuite/auth/login?targetUri=%2Findex.gsp&format=html");
        selenium.waitForPageToLoad("30000");
        selenium.type("login", "nmd2User");
        selenium.click("//input[@value='Sign in']");
        Thread.sleep(15000);
        verifyTrue(selenium.isTextPresent("NMD2"));
        verifyFalse(selenium.isTextPresent("NMD1"));
        selenium.click("link=Logout");
        selenium.waitForPageToLoad("30000");
        login("rsadmin", "changeme");


        deleteGroup(nmd1GroupId)
        deleteGroup(nmd2GroupId)
        deleteUser(nmd1UserId)
        deleteUser(nmd2UserId)
        deleteSNMPConnector(snmpId)
        deleteScriptFile("EventSnmpListener.groovy")
        deleteScript(GenerateSnmpTrapsIdValue)
        deleteScriptFile("GenerateSnmpTraps.groovy")

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
                sleep(15000)
                sec = (System.currentTimeMillis())/1000;
                SnmpUtils.sendV1Trap("localhost/162", "localhost", "public", "1.3.6.1.2.1.11",sec,1,1,[["OID":"1.3.6.1.2.1.1.3.0", "Value":"5"],["OID":"1.3.6.1.2.1.1.3.1", "Value":"Down"],["OID":"1.3.6.1.2.1.1.3.2", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.3", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.4", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.5", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.6", "Value":"NMD1"]]);
                sleep(15000)
                sec = (System.currentTimeMillis())/1000;
                SnmpUtils.sendV1Trap("localhost/162", "localhost", "public", "1.3.6.1.2.1.11",sec,1,1,[["OID":"1.3.6.1.2.1.1.3.0", "Value":"10"],["OID":"1.3.6.1.2.1.1.3.1", "Value":"Down"],["OID":"1.3.6.1.2.1.1.3.2", "Value":"2"],["OID":"1.3.6.1.2.1.1.3.3", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.4", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.5", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.6", "Value":"NMD1"]]);
                sleep(15000)
                sec = (System.currentTimeMillis())/1000;
                SnmpUtils.sendV1Trap("localhost/162", "localhost", "public", "1.3.6.1.2.1.11",sec,1,1,[["OID":"1.3.6.1.2.1.1.3.0", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.1", "Value":"Down"],["OID":"1.3.6.1.2.1.1.3.2", "Value":"2"],["OID":"1.3.6.1.2.1.1.3.3", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.4", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.5", "Value":"2"],["OID":"1.3.6.1.2.1.1.3.6", "Value":"NMD1"]]);
                sleep(15000)
                sec = (System.currentTimeMillis())/1000;
                SnmpUtils.sendV1Trap("localhost/162", "localhost", "public", "1.3.6.1.2.1.11",sec,1,1,[["OID":"1.3.6.1.2.1.1.3.0", "Value":"5"],["OID":"1.3.6.1.2.1.1.3.1", "Value":"Up"],["OID":"1.3.6.1.2.1.1.3.2", "Value":"0"],["OID":"1.3.6.1.2.1.1.3.3", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.4", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.5", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.6", "Value":"NMD1"]]);
                sleep(15000)
                sec = (System.currentTimeMillis())/1000;
                SnmpUtils.sendV1Trap("localhost/162", "localhost", "public", "1.3.6.1.2.1.11",sec,1,1,[["OID":"1.3.6.1.2.1.1.3.0", "Value":"10"],["OID":"1.3.6.1.2.1.1.3.1", "Value":"HardDown"],["OID":"1.3.6.1.2.1.1.3.2", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.3", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.4", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.5", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.6", "Value":"NMD1"]]);
                sleep(15000)
                sec = (System.currentTimeMillis())/1000;
                SnmpUtils.sendV1Trap("localhost/162", "localhost", "public", "1.3.6.1.2.1.11",sec,1,1,[["OID":"1.3.6.1.2.1.1.3.0", "Value":"30"],["OID":"1.3.6.1.2.1.1.3.1", "Value":"Down"],["OID":"1.3.6.1.2.1.1.3.2", "Value":"2"],["OID":"1.3.6.1.2.1.1.3.3", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.4", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.5", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.6", "Value":"NMD2"]]);
                sleep(15000)
                sec = (System.currentTimeMillis())/1000;
                SnmpUtils.sendV1Trap("localhost/162", "localhost", "public", "1.3.6.1.2.1.11",sec,1,1,[["OID":"1.3.6.1.2.1.1.3.0", "Value":"27"],["OID":"1.3.6.1.2.1.1.3.1", "Value":"HardDown"],["OID":"1.3.6.1.2.1.1.3.2", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.3", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.4", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.5", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.6", "Value":"NMD1"]]);
                sleep(15000)
                sec = (System.currentTimeMillis())/1000;
                SnmpUtils.sendV1Trap("localhost/162", "localhost", "public", "1.3.6.1.2.1.11",sec,1,1,[["OID":"1.3.6.1.2.1.1.3.0", "Value":"33"],["OID":"1.3.6.1.2.1.1.3.1", "Value":"Down"],["OID":"1.3.6.1.2.1.1.3.2", "Value":"2"],["OID":"1.3.6.1.2.1.1.3.3", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.4", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.5", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.6", "Value":"NMD2"]]);
                sleep(15000)
                sec = (System.currentTimeMillis())/1000;
                SnmpUtils.sendV1Trap("localhost/162", "localhost", "public", "1.3.6.1.2.1.11",sec,1,1,[["OID":"1.3.6.1.2.1.1.3.0", "Value":"30"],["OID":"1.3.6.1.2.1.1.3.1", "Value":"HardDown"],["OID":"1.3.6.1.2.1.1.3.2", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.3", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.4", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.5", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.6", "Value":"NMD2"]]);
                sleep(15000)
                sec = (System.currentTimeMillis())/1000;
                SnmpUtils.sendV1Trap("localhost/162", "localhost", "public", "1.3.6.1.2.1.11",sec,1,1,[["OID":"1.3.6.1.2.1.1.3.0", "Value":"8"],["OID":"1.3.6.1.2.1.1.3.1", "Value":"Down"],["OID":"1.3.6.1.2.1.1.3.2", "Value":"2"],["OID":"1.3.6.1.2.1.1.3.3", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.4", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.5", "Value":"1"],["OID":"1.3.6.1.2.1.1.3.6", "Value":"NMD2"]]);
                return "Completed event trap generation"
            """
    }

}