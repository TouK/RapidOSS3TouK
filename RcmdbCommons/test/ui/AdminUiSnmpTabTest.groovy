


import com.ifountain.rcmdb.test.util.SeleniumTestCase
import com.ifountain.rcmdb.test.util.SeleniumTestUtils

/**
 * Created by IntelliJ IDEA.
 * User: fadime
 * Date: Jun 23, 2009
 * Time: 9:28:20 PM
 * To change this template use File | Settings | File Templates.
 */
class AdminUiSnmpTabTest extends SeleniumTestCase
{
    void setUp() throws Exception
    {
        super.setUp("http://${SeleniumTestUtils.getRIHost()}:${SeleniumTestUtils.getRIPort()}/RapidSuite/",
                SeleniumTestUtils.getSeleniumBrowser());
    }



    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
      //  logout()
    }


   public void testLog()
   {
       login()
      selenium.open("http://localhost:12222/RapidSuite/index/events.gsp") 
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
        selenium.waitForPageToLoad("30000");
        selenium.click("_action_Delete");
        assertTrue(selenium.getConfirmation().matches("^Are you sure[\\s\\S]\$"));
    }

    public String findId(String parser)
    {
        def line = selenium.getLocation()
        def splitted = new String[3]
        splitted = line.split(parser)
        return splitted[1]
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


    public void testCreateASNMPConnector()
      {
       def  scriptContent= eventSnmpContent()
        createScriptFile("${SeleniumTestUtils.getRsHome()}/RapidSuite/scripts/EventSnmpListener.groovy", scriptContent);

        login()
        
		selenium.click("link=SNMP");
		selenium.waitForPageToLoad("30000");
		selenium.click("link=New SnmpConnector");
		selenium.waitForPageToLoad("30000");
		selenium.type("name", "snmp1");
		selenium.type("scriptFile", "EventSnmpListener");
		selenium.click("//input[@value='Create']");
		selenium.waitForPageToLoad("30000");

        verifyEquals("snmp1", selenium.getText("name"));
		verifyEquals("0.0.0.0", selenium.getText("host"));
		verifyEquals("162", selenium.getText("port"));
		verifyEquals("EventSnmpListener", selenium.getText("scriptFile"));
		verifyEquals("WARN", selenium.getText("logLevel"));
		verifyEquals("true", selenium.getText("logFileOwn"));
		def snmpId=findId("RapidSuite/snmpConnector/show/")


        selenium.open("/RapidSuite/script/list");
		selenium.click("//em");
		selenium.waitForPageToLoad("30000");
		selenium.click("link=snmp1");
		selenium.waitForPageToLoad("30000");
        def scriptIdValue =  findId("RapidSuite/script/show/")

		deleteScript(scriptIdValue)
        deleteScriptFile("EventSnmpListener.groovy")
        deleteSNMPConnector(snmpId)

        File file = new File("${SeleniumTestUtils.getRsHome()}/RapidSuite/logs/snmp1.log")
        def isSnmp1LogFileCreated=false
        if(file.exists())
            isSnmp1LogFileCreated=true
        assertTrue(isSnmp1LogFileCreated)

 }



    public void testTestSnmpConnectorStartStop()
    {
        def  scriptContent= eventSnmpContent()
           createScriptFile("${SeleniumTestUtils.getRsHome()}/RapidSuite/scripts/EventSnmpListener.groovy", scriptContent);
        login()
        selenium.click("link=SNMP");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=New SnmpConnector");
        selenium.waitForPageToLoad("30000");
        selenium.type("name", "snmp1");
        selenium.type("scriptFile", "EventSnmpListener");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        def snmpId=findId("RapidSuite/snmpConnector/show/")

        selenium.click("link=SNMP");
        selenium.waitForPageToLoad("30000");

        selenium.click("link=Start");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Connector snmp1 successfully started"));
        selenium.click("link=Stop");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium.isTextPresent("Connector snmp1 successfully stopped"));

        deleteScriptFile("EventSnmpListener.groovy")
        deleteSNMPConnector(snmpId)
    }




      public void testTestSnmpConnectorUsingAScript()
      {
         def testScriptContent = generateSnmpScriptContent()
          createScriptFile("${SeleniumTestUtils.getRsHome()}/RapidSuite/scripts/GenerateSnmpTraps.groovy", testScriptContent);
         def scriptContent= eventSnmpContent()
          createScriptFile("${SeleniumTestUtils.getRsHome()}/RapidSuite/scripts/EventSnmpListener.groovy", scriptContent);

        login()
        selenium.click("link=SNMP");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=New SnmpConnector");
        selenium.waitForPageToLoad("30000");
        selenium.type("name", "snmp1");
        selenium.type("scriptFile", "EventSnmpListener");
        selenium.click("//input[@value='Create']");
        selenium.waitForPageToLoad("30000");
        def snmp1Id=findId("RapidSuite/snmpConnector/show/")
        selenium.click("link=SNMP");
		selenium.waitForPageToLoad("30000");
		verifyTrue(selenium.isTextPresent("snmp1"));
		selenium.click("link=Start");
		selenium.waitForPageToLoad("30000");

        selenium.click("//em");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=snmp1");
        selenium.waitForPageToLoad("30000");
        def eventSnmpScriptIdValue =  findId("RapidSuite/script/show/")

		selenium.click("link=Scripts");
		selenium.waitForPageToLoad("30000");
		selenium.click("link=New Script");
		selenium.waitForPageToLoad("30000");
		selenium.type("name", "GenerateSnmpTraps");
		selenium.click("//input[@value='Create']");
		selenium.waitForPageToLoad("30000");
		def GenerateSnmpTrapsIdValue =  findId("RapidSuite/script/show/")
		selenium.click("_action_Run");
		Thread.sleep(180000);
		verifyTrue(selenium.isTextPresent("Completed event trap generation"));

		
		selenium.open("http://localhost:12222/RapidSuite/index/events.gsp");
		selenium.waitForPageToLoad("30000");
		selenium.click("link=Events");
	    selenium.waitForPageToLoad("30000");
		selenium.click("link=Historical Events");
		selenium.waitForPageToLoad("30000");


//      deleteScript(eventSnmpScriptIdValue)
//      deleteScriptFile("EventSnmpListener.groovy")
//      deleteScript(GenerateSnmpTrapsIdValue)
//      deleteSNMPConnector(snmp1Id) ;
//      deleteScriptFile("GenerateSnmpTraps.groovy")
          
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




  public String  generateSnmpScriptContent()
  {
      return  """
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