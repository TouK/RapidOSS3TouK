package message

import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import script.CmdbScript

import connection.EmailConnection
import datasource.EmailDatasource
import connector.EmailConnectorController
import connector.EmailConnector
import connection.Connection
import datasource.BaseDatasource
import com.ifountain.rcmdb.scripting.ScriptManager
import org.apache.log4j.Logger

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 13, 2009
 * Time: 3:12:46 PM
 * To change this template use File | Settings | File Templates.
 */
class EmailSenderScriptIntegrationTests extends RapidCmdbIntegrationTestCase {
    static transactional = false;
    def connectorParams;
    def classes=[:]
    def destination="abdurrahim"
    void setUp() throws Exception {
        super.setUp();
        loadClasses(["RsEvent","RsHistoricalEvent"])
        clearAll();
        buildConnectorParams();

        
    }

    void tearDown() throws Exception {
        super.tearDown();        
    }
    void buildConnectorParams(){

        connectorParams=[:]

        connectorParams["smtpHost"]="xxx"
        connectorParams["smtpPort"]=25
        connectorParams["name"] = "testConnector";
        connectorParams["protocol"] = EmailConnection.SMTP;
        println "connectorParams : ${connectorParams}"

    }
     def addEvents(prefix,count)
    {
        def events=[]
        count.times{
            def event=classes.RsEvent.add(name:"${prefix}${it}",severity:it)
            assertFalse(event.hasErrors())
            events.add(event)
        }
        return events;
    }
    def addHistoricalEvents(prefix,count)
    {
        def events=[]
        count.times{
            def event=classes.RsHistoricalEvent.add(name:"${prefix}${it}",severity:it,activeId:it)
            assertFalse(event.hasErrors())
            events.add(event)
        }
        return events;
    }
    void clearAll()
    {

        classes.RsEvent.removeAll();
        classes.RsHistoricalEvent.removeAll();
        RsMessage.removeAll();
        RsMessageRule.removeAll();
        Connection.removeAll();
        BaseDatasource.removeAll();
        EmailConnector.removeAll();
        EmailConnection.removeAll();
        EmailDatasource.removeAll();
        CmdbScript.list().each{
            CmdbScript.deleteScript(it);
        }
    }
     void loadClasses(classList)
    {
        classList.each{
            def loadedClass=this.class.classLoader.loadClass(it)
            classes[loadedClass.getSimpleName()]=loadedClass
        }
    }
    void testSenderRecievesConnectorNameFromStaticParam(){
         assertEquals(CmdbScript.list().size(),0)
         
         def createScript=CmdbScript.addScript([name:"createDefaults"])
         CmdbScript.runScript(createScript.name);
         
         assertTrue(CmdbScript.list().size()>1)

         def senderScript=CmdbScript.get(name:"emailSender")
         assertEquals(senderScript.staticParam,"connectorName:emailConnector")

         def newScriptFileName="emailSenderStaticParamTest.groovy"
         File newScriptFile=new File(System.getProperty("base.dir")+"/scripts/"+newScriptFileName)
         newScriptFile.write("return [staticParam:staticParam,staticParamMap:staticParamMap]")

         CmdbScript.updateScript(senderScript,[scriptFile:newScriptFileName,name:senderScript.name],false)
         assertFalse(senderScript.hasErrors())
         assertEquals(senderScript.scriptFile,newScriptFileName)
         

         senderScript.reload();
         
         def result=CmdbScript.runScript(senderScript.name);
         assertEquals(result.staticParam,senderScript.staticParam)
         assertEquals(result.staticParamMap.connectorName,"emailConnector")
         
    }
    void testSenderProcessesMessages()
    {
        EmailDatasource.metaClass.sendEmail= { Map params ->
            println "my send email";
        }
        assertEquals(classes.RsEvent.countHits("alias:*"),0)
        assertEquals(RsMessage.countHits("alias:*"),0)

        def events=addEvents("testev1",4)
        events.each{ event ->
            RsMessage.add(eventId:event.id,destination:destination,destinationType:RsMessage.EMAIL,action:"create",state:1);
        }
        assertEquals(classes.RsEvent.countHits("alias:*"),4)
        assertEquals(RsMessage.countHits("state:1"),4)

        def historicalEvents=addHistoricalEvents("testhistev1",4)
        historicalEvents.each{ event ->
            RsMessage.add(eventId:event.activeId,destination:destination,destinationType:RsMessage.EMAIL,action:"clear",state:1);
        }
        assertEquals(classes.RsHistoricalEvent.countHits("alias:*"),4)
        assertEquals(RsMessage.countHits("state:1"),8)
        assertEquals(RsMessage.countHits("state:1 AND action:clear"),4)


        def senderScript=CmdbScript.addScript([name: "emailSender", type:CmdbScript.ONDEMAND,logFileOwn:true,staticParam:"connectorName:${connectorParams.name}"]);
        assertFalse(senderScript.hasErrors());

        addEmailConnectorViaController();
        def connector=EmailConnector.get(name:connectorParams.name);
        assertNotNull(connector);

        CmdbScript.runScript(senderScript,[:])
        assertEquals(RsMessage.countHits("state:1"),0)
        assertEquals(RsMessage.countHits("state:3"),8)
    }
     void testSenderDoesNotProcessMessagesIfSendGeneratesException()
    {
        EmailDatasource.metaClass.sendEmail= { Map params ->
           println "will generate exception in sendEmail"
            throw new Exception("Can not send email");
        }
        assertEquals(classes.RsEvent.countHits("alias:*"),0)
        assertEquals(RsMessage.countHits("alias:*"),0)

        def events=addEvents("testev1",4)
        events.each{ event ->
            RsMessage.add(eventId:event.id,destination:destination,destinationType:RsMessage.EMAIL,action:"create",state:1);
        }
        assertEquals(classes.RsEvent.countHits("alias:*"),4)
        assertEquals(RsMessage.countHits("state:1"),4)

        def historicalEvents=addHistoricalEvents("testhistev1",4)
        historicalEvents.each{ event ->
            RsMessage.add(eventId:event.activeId,destination:destination,destinationType:RsMessage.EMAIL,action:"clear",state:1);
        }
        assertEquals(classes.RsHistoricalEvent.countHits("alias:*"),4)
        assertEquals(RsMessage.countHits("state:1"),8)
        assertEquals(RsMessage.countHits("state:1 AND action:clear"),4)


        def senderScript=CmdbScript.addScript([name: "emailSender", type:CmdbScript.ONDEMAND,logFileOwn:true,staticParam:"connectorName:${connectorParams.name}"]);
        assertFalse(senderScript.hasErrors());

        addEmailConnectorViaController();
        def connector=EmailConnector.get(name:connectorParams.name);
        assertNotNull(connector);

        CmdbScript.runScript(senderScript,[:])
        assertEquals(RsMessage.countHits("state:1"),8)
        
    }
     void testSenderProcessesMessagesAddedByRsMessageOperations()
    {
        EmailDatasource.metaClass.sendEmail= { Map params ->
            println "my send email";
        }
        assertEquals(classes.RsEvent.countHits("alias:*"),0)
        assertEquals(RsMessage.countHits("alias:*"),0)

        def events=addEvents("testev1",4)
        events.each{ event ->
            RsMessage.addEventCreateEmail(Logger.getRootLogger(),[id:event.id],destination,0)            
        }
        assertEquals(classes.RsEvent.countHits("alias:*"),4)
        assertEquals(RsMessage.countHits("state:1"),4)


        def senderScript=CmdbScript.addScript([name: "emailSender", type:CmdbScript.ONDEMAND,logFileOwn:true,staticParam:"connectorName:${connectorParams.name}"]);
        assertFalse(senderScript.hasErrors());

        addEmailConnectorViaController();
        def connector=EmailConnector.get(name:connectorParams.name);
        assertNotNull(connector);

        CmdbScript.runScript(senderScript,[:])
        assertEquals(RsMessage.countHits("state:1"),0)
        assertEquals(RsMessage.countHits("state:3"),4)

        events.each{
            it.clear();
        }
        
        assertEquals(classes.RsEvent.countHits("alias:*"),0)
        assertEquals(classes.RsHistoricalEvent.countHits("alias:*"),4)
        assertEquals(RsMessage.countHits("state:1"),0)
        classes.RsHistoricalEvent.list().each{ event ->
            RsMessage.addEventClearEmail(Logger.getRootLogger(),[activeId:event.activeId],destination)            
        }
        assertEquals(RsMessage.countHits("alias:*"),8)
        assertEquals(RsMessage.countHits("state:1 AND action:clear"),4)
        assertEquals(RsMessage.countHits("state:3 AND action:create"),4)
        
        CmdbScript.runScript(senderScript,[:])
        assertEquals(RsMessage.countHits("alias:*"),8)
        assertEquals(RsMessage.countHits("state:3"),8)
        assertEquals(RsMessage.countHits("state:3 AND action:clear"),4)
        assertEquals(RsMessage.countHits("state:3 AND action:create"),4)
        
        
    }
    void testSenderDoesNotProcessMessagesIfConnectorIsMissing()
    {
        EmailDatasource.metaClass.sendEmail= { Map params ->
            println "my send email";
        }
        assertEquals(classes.RsEvent.countHits("alias:*"),0)
        assertEquals(RsMessage.countHits("alias:*"),0)

        def events=addEvents("testev1",4)
        events.each{ event ->
            RsMessage.add(eventId:event.id,destination:destination,destinationType:RsMessage.EMAIL,action:"create",state:1);
        }
        assertEquals(classes.RsEvent.countHits("alias:*"),4)
        assertEquals(RsMessage.countHits("state:1"),4)

        def senderScript=CmdbScript.addScript([name: "emailSender", type:CmdbScript.ONDEMAND,logFileOwn:true,staticParam:"connectorName:${connectorParams.name}"]);
        assertFalse(senderScript.hasErrors());

        addEmailConnectorViaController();
        def connector=EmailConnector.get(name:connectorParams.name);
        assertNotNull(connector);

        CmdbScript.runScript(senderScript,[:])
        assertEquals(RsMessage.countHits("state:1"),0)

        events.each{ event ->
            RsMessage.add(eventId:event.id,destination:destination,destinationType:RsMessage.EMAIL,action:"create",state:1);
        }
        assertEquals(classes.RsEvent.countHits("alias:*"),4)
        assertEquals(RsMessage.countHits("state:1"),4)

        connector.remove();
        connector=EmailConnector.get(name:connectorParams.name);
        assertNull(connector);
        CmdbScript.runScript(senderScript,[:])
        assertEquals(RsMessage.countHits("state:1"),4)

    }
    void testSenderChangesMessageStateWhenEventsAreMissing(){
        EmailDatasource.metaClass.sendEmail= { Map params ->
            println "my send email";
        }
        assertEquals(classes.RsEvent.countHits("alias:*"),0)
        assertEquals(classes.RsHistoricalEvent.countHits("alias:*"),0)
        assertEquals(RsMessage.countHits("alias:*"),0)


        4.times{ eventId ->
            RsMessage.add(eventId:eventId,destination:destination,destinationType:RsMessage.EMAIL,action:"create",state:1);
        }
        4.times{ eventId ->
            RsMessage.add(eventId:eventId,destination:destination,destinationType:RsMessage.EMAIL,action:"clear",state:1);
        }
        
        assertEquals(RsMessage.countHits("state:1"),8)
        assertEquals(RsMessage.countHits("state:1 AND action:create"),4)
        assertEquals(RsMessage.countHits("state:1 AND action:create"),4)

        def senderScript=CmdbScript.addScript([name: "emailSender", type:CmdbScript.ONDEMAND,logFileOwn:true,staticParam:"connectorName:${connectorParams.name}"]);
        assertFalse(senderScript.hasErrors());

        addEmailConnectorViaController();
        def connector=EmailConnector.get(name:connectorParams.name);
        assertNotNull(connector);

        CmdbScript.runScript(senderScript,[:])
        assertEquals(RsMessage.countHits("state:1"),0)
        assertEquals(RsMessage.countHits("state:1 AND action:create"),0)
        assertEquals(RsMessage.countHits("state:1 AND action:create"),0)
        assertEquals(RsMessage.countHits("state:4"),8)
        assertEquals(RsMessage.countHits("state:4 AND action:create"),4)
        assertEquals(RsMessage.countHits("state:4 AND action:create"),4)
    }
    void addEmailConnectorViaController()
    {
        assertEquals(EmailConnector.list().size(),0)
        assertEquals(EmailConnection.list().size(),0)
        assertEquals(EmailDatasource.list().size(),0)
        
        def params=[:]
        params.putAll(connectorParams)


        def controller = new EmailConnectorController();
        params.each{ key , val ->
            controller.params[key] = val;
        }

        controller.save();
        println controller.flash
        def emailConnectors = EmailConnector.list();
        assertEquals(1, emailConnectors.size());
        EmailConnector emailConnector = emailConnectors[0]
        assertEquals(params.name, emailConnector.name);

        def emailConnection=EmailConnection.list()[0];
        assertEquals(emailConnection.id,emailConnector.emailConnection.id);
        assertEquals (emailConnection.emailDatasources.size(), 1);

        EmailDatasource emailDatasource = EmailDatasource.list()[0];
        assertEquals (emailDatasource.id, emailConnector.emailDatasource.id);
        assertEquals (emailDatasource.connection.id, emailConnection.id);
        
    }

}