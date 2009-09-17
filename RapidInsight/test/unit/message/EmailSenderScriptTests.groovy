package message

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import org.apache.commons.io.FileUtils
import datasource.EmailDatasource
import connection.EmailConnection
import com.ifountain.rcmdb.test.util.scripting.ScriptManagerForTest
import connector.EmailConnector
import connector.EmailConnectorOperations
import com.ifountain.rcmdb.test.util.CompassForTests
import datasource.EmailDatasourceOperations
import application.RsApplication
import com.ifountain.rcmdb.test.util.RsApplicationTestUtils
import com.ifountain.comp.test.util.logging.TestLogUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 27, 2009
* Time: 3:58:44 PM
* To change this template use File | Settings | File Templates.
*/
class EmailSenderScriptTests extends RapidCmdbWithCompassTestCase {

    def connectorParams;
    def destination="abdurrahim"
    def scripts_directory="../testoutput";

    def RsEvent;
    def RsHistoricalEvent;
    def RsEventOperations;
    def RsEventJournal;
    def RsTemplate;

    def EMAIL_TYPE="email";



    public void setUp() throws Exception {
        super.setUp();


        
        ["RsEvent","RsHistoricalEvent","RsEventJournal","RsEventOperations","RsTemplate"].each{ className ->
            setProperty(className,gcl.loadClass(className));
        }
        
        clearMetaClasses();



        initialize([RsEvent,RsHistoricalEvent,RsEventJournal,RsMessage,RsApplication,EmailConnector,EmailConnection,EmailDatasource], []);
        CompassForTests.addOperationSupport (EmailConnector,EmailConnectorOperations);
        CompassForTests.addOperationSupport (EmailDatasource,EmailDatasourceOperations);
        CompassForTests.addOperationSupport (RsMessage,RsMessageOperations);
        CompassForTests.addOperationSupport (RsEvent,RsEventOperations);
        RsApplicationTestUtils.initializeRsApplicationOperations (RsApplication);
        RsApplicationTestUtils.clearProcessors();
        RsApplication.getUtility("RsTemplate").metaClass.'static'.render={ String templatePath,params ->
            return "___renderTestResult";
        }

        buildConnectorParams();

        initializeScriptManager(); 
    }

    public void tearDown() throws Exception {
        clearMetaClasses();
        super.tearDown();
    }

    public void clearMetaClasses()
    {
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(RsTemplate);
        ExpandoMetaClass.enableGlobally();
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
            def event=RsEvent.add(name:"${prefix}${it}",severity:it)
            assertFalse(event.hasErrors())
            events.add(event)
        }
        return events;
    }
    def addHistoricalEvents(prefix,count)
    {
        def events=[]
        count.times{
            def event=RsHistoricalEvent.add(name:"${prefix}${it}",severity:it,activeId:it)
            assertFalse(event.hasErrors())
            events.add(event)
        }
        return events;
    }

    void initializeScriptManager()
    {
        File scriptsDir=new File(scripts_directory);
        scriptsDir.mkdirs();
        assertTrue(scriptsDir.isDirectory());

        println "base path is :"+scriptsDir.getCanonicalPath();

        ScriptManagerForTest.initialize (gcl,scripts_directory);

        FileUtils.copyFileToDirectory (new File(getWorkspacePath()+"/RapidModules/RapidInsight/scripts/emailSender.groovy"),scriptsDir);

        ScriptManagerForTest.addScript('emailSender');
    }

    void testSenderCallsRenderTemplateAndPassesTemplateResultToSendMessage()
    {
        def renderTemplateParams=[];
        RsApplication.getUtility("RsTemplate").metaClass.'static'.render={ String templatePath,params ->
            renderTemplateParams.add([templatePath:templatePath,params:params]);
            return "renderTestResult";
        }

        def sendEmailParams=[];

        EmailDatasource.metaClass.sendEmail= { Map params ->
            sendEmailParams.add([params:params]);
            println "my send email";
        }


        assertEquals(RsEvent.countHits("alias:*"),0)
        assertEquals(RsMessage.countHits("alias:*"),0)

        def events=addEvents("testev1",4)
        events.each{ event ->
            RsMessage.add(eventId:event.id,destination:destination,destinationType:EMAIL_TYPE,eventType:RsMessage.EVENT_TYPE_CREATE,state:RsMessage.STATE_READY);
        }
        assertEquals(RsEvent.countHits("alias:*"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),4)

        def historicalEvents=addHistoricalEvents("testhistev1",4)
        historicalEvents.each{ event ->
            RsMessage.add(eventId:event.activeId,destination:destination,destinationType:EMAIL_TYPE,eventType:RsMessage.EVENT_TYPE_CLEAR,state:RsMessage.STATE_READY);
        }
        assertEquals(RsHistoricalEvent.countHits("alias:*"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),8)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY} AND eventType:${RsMessage.EVENT_TYPE_CREATE}"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY} AND eventType:${RsMessage.EVENT_TYPE_CLEAR}"),4)


        def connector=addEmailConnector();

        TestLogUtils.enableLogger (TestLogUtils.log);
        
        ScriptManagerForTest.runScript("emailSender",[staticParamMap:[connectorName:connectorParams.name],logger:TestLogUtils.log])
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),0)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_SENT}"),8)
        

        assertEquals(8,sendEmailParams.size());
        assertEquals(8,renderTemplateParams.size());

        def index=0;
        RsMessage.searchEvery("alias:*",[sort: "id",order:"asc"]).each{ message ->
           def event=message.retrieveEvent();

           assertEquals("grails-app/templates/message/emailTemplate.gsp",renderTemplateParams[index].templatePath);
           assertEquals(event.id,renderTemplateParams[index].params.event.id);
           assertEquals(message.id,renderTemplateParams[index].params.message.id);
           assertEquals(2,renderTemplateParams[index].params.size());

           assertEquals("IFountainEmailSender@ifountain.com",sendEmailParams[index].params.from)
           assertEquals(message.destination,sendEmailParams[index].params.to)
           assertEquals("renderTestResult",sendEmailParams[index].params.body)
           assertEquals("text/html",sendEmailParams[index].params.contentType)
            if(message.eventType==RsMessage.EVENT_TYPE_CREATE)
                assertEquals("Event Created",sendEmailParams[index].params.subject)
            else
                assertEquals("Event Cleared",sendEmailParams[index].params.subject)


           index++;
        }


    }

    void testSenderProcessesMessages()
    {
        EmailDatasource.metaClass.sendEmail= { Map params ->
            println "my send email";
        }
        assertEquals(RsEvent.countHits("alias:*"),0)
        assertEquals(RsMessage.countHits("alias:*"),0)

        def events=addEvents("testev1",4)
        events.each{ event ->
            RsMessage.add(eventId:event.id,destination:destination,destinationType:EMAIL_TYPE,eventType:RsMessage.EVENT_TYPE_CREATE,state:RsMessage.STATE_READY);
        }
        assertEquals(RsEvent.countHits("alias:*"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),4)

        def historicalEvents=addHistoricalEvents("testhistev1",4)
        historicalEvents.each{ event ->
            RsMessage.add(eventId:event.activeId,destination:destination,destinationType:EMAIL_TYPE,eventType:RsMessage.EVENT_TYPE_CLEAR,state:RsMessage.STATE_READY);
        }
        assertEquals(RsHistoricalEvent.countHits("alias:*"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),8)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY} AND eventType:${RsMessage.EVENT_TYPE_CREATE}"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY} AND eventType:${RsMessage.EVENT_TYPE_CLEAR}"),4)


        def connector=addEmailConnector();

        ScriptManagerForTest.runScript("emailSender",[staticParamMap:[connectorName:connectorParams.name]])
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),0)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_SENT}"),8)
    }
    void testSenderDoesNotProcessMessagesIfSendGeneratesException()
    {
        EmailDatasource.metaClass.sendEmail= { Map params ->
           println "will generate exception in sendEmail"
            throw new Exception("Can not send email");
        }
        assertEquals(RsEvent.countHits("alias:*"),0)
        assertEquals(RsMessage.countHits("alias:*"),0)

        def events=addEvents("testev1",4)
        events.each{ event ->
            RsMessage.add(eventId:event.id,destination:destination,destinationType:EMAIL_TYPE,eventType:RsMessage.EVENT_TYPE_CREATE,state:RsMessage.STATE_READY);
        }
        assertEquals(RsEvent.countHits("alias:*"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),4)

        def historicalEvents=addHistoricalEvents("testhistev1",4)
        historicalEvents.each{ event ->
            RsMessage.add(eventId:event.activeId,destination:destination,destinationType:EMAIL_TYPE,eventType:RsMessage.EVENT_TYPE_CLEAR,state:RsMessage.STATE_READY);
        }
        assertEquals(RsHistoricalEvent.countHits("alias:*"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),8)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY} AND eventType:${RsMessage.EVENT_TYPE_CREATE}"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY} AND eventType:${RsMessage.EVENT_TYPE_CLEAR}"),4)


        def connector=addEmailConnector();

        ScriptManagerForTest.runScript("emailSender",[staticParamMap:[connectorName:connectorParams.name]])
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),8)

    }
    void testSenderProcessesMessagesAddedByRsMessageOperations()
    {
        EmailDatasource.metaClass.sendEmail= { Map params ->
            println "my send email";
        }
        assertEquals(RsEvent.countHits("alias:*"),0)
        assertEquals(RsMessage.countHits("alias:*"),0)

        def events=addEvents("testev1",4)
        events.each{ event ->
            RsMessage.addEventCreateMessage([id:event.id],EMAIL_TYPE, destination,0)
        }
        assertEquals(RsEvent.countHits("alias:*"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),4)

        def connector=addEmailConnector();

        ScriptManagerForTest.runScript("emailSender",[staticParamMap:[connectorName:connectorParams.name]])

        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),0)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_SENT}"),4)

        events.each{
            it.clear();
        }

        assertEquals(RsEvent.countHits("alias:*"),0)
        assertEquals(RsHistoricalEvent.countHits("alias:*"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),0)
        RsHistoricalEvent.list().each{ event ->
            RsMessage.addEventClearMessage([activeId:event.activeId],EMAIL_TYPE, destination)
        }
        assertEquals(RsMessage.countHits("alias:*"),8)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY} AND eventType:${RsMessage.EVENT_TYPE_CLEAR}"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_SENT} AND eventType:${RsMessage.EVENT_TYPE_CREATE}"),4)
        
        ScriptManagerForTest.runScript("emailSender",[staticParamMap:[connectorName:connectorParams.name]])
        assertEquals(RsMessage.countHits("alias:*"),8)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_SENT}"),8)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_SENT} AND eventType:${RsMessage.EVENT_TYPE_CLEAR}"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_SENT} AND eventType:${RsMessage.EVENT_TYPE_CREATE}"),4)


    }
    void testSenderDoesNotProcessMessagesIfConnectorIsMissing()
    {
        EmailDatasource.metaClass.sendEmail= { Map params ->
            println "my send email";
        }
        assertEquals(RsEvent.countHits("alias:*"),0)
        assertEquals(RsMessage.countHits("alias:*"),0)

        def events=addEvents("testev1",4)
        events.each{ event ->
            RsMessage.add(eventId:event.id,destination:destination,destinationType:EMAIL_TYPE,eventType:RsMessage.EVENT_TYPE_CREATE,state:RsMessage.STATE_READY);
        }
        assertEquals(RsEvent.countHits("alias:*"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),4)



        def connector=addEmailConnector();

        ScriptManagerForTest.runScript("emailSender",[staticParamMap:[connectorName:connectorParams.name]])

        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),0)

        events.each{ event ->
            RsMessage.add(eventId:event.id,destination:destination,destinationType:EMAIL_TYPE,eventType:RsMessage.EVENT_TYPE_CREATE,state:RsMessage.STATE_READY);
        }
        assertEquals(RsEvent.countHits("alias:*"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),4)

        connector.remove();
        connector=EmailConnector.get(name:connectorParams.name);
        assertNull(connector);
        ScriptManagerForTest.runScript("emailSender",[staticParamMap:[connectorName:connectorParams.name]])
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),4)

    }
    void testSenderChangesMessageStateWhenEventsAreMissing(){
        EmailDatasource.metaClass.sendEmail= { Map params ->
            println "my send email";
        }
        assertEquals(RsEvent.countHits("alias:*"),0)
        assertEquals(RsHistoricalEvent.countHits("alias:*"),0)
        assertEquals(RsMessage.countHits("alias:*"),0)


        4.times{ eventId ->
            RsMessage.add(eventId:eventId,destination:destination,destinationType:EMAIL_TYPE,eventType:RsMessage.EVENT_TYPE_CREATE,state:RsMessage.STATE_READY);
        }
        4.times{ eventId ->
            RsMessage.add(eventId:eventId,destination:destination,destinationType:EMAIL_TYPE,eventType:RsMessage.EVENT_TYPE_CLEAR,state:RsMessage.STATE_READY);
        }

        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),8)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY} AND eventType:${RsMessage.EVENT_TYPE_CREATE}"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY} AND eventType:${RsMessage.EVENT_TYPE_CLEAR}"),4)

        def connector=addEmailConnector();

        ScriptManagerForTest.runScript("emailSender",[staticParamMap:[connectorName:connectorParams.name]])

        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),0)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY} AND eventType:${RsMessage.EVENT_TYPE_CREATE}"),0)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY} AND eventType:${RsMessage.EVENT_TYPE_CLEAR}"),0)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_NOT_EXISTS}"),8)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_NOT_EXISTS} AND eventType:${RsMessage.EVENT_TYPE_CREATE}"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_NOT_EXISTS} AND eventType:${RsMessage.EVENT_TYPE_CLEAR}"),4)
    }
    

    def addEmailConnector()
    {
        assertEquals(0,EmailConnector.count())
        assertEquals(0,EmailConnection.count())
        assertEquals(0,EmailDatasource.count())

        def params=[:]
        params.putAll(connectorParams)

        def addedObjects=EmailConnector.addConnector(params);
        assertEquals(3,addedObjects.size());
        addedObjects.each{ key , object ->
            assertFalse(object.hasErrors());
        }
        assertNotNull(addedObjects.emailConnector)

        assertEquals(1,EmailConnector.count())
        assertEquals(1,EmailConnection.count())
        assertEquals(1,EmailDatasource.count())

        return addedObjects.emailConnector;
    }

}