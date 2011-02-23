package message

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import org.apache.commons.io.FileUtils
import com.ifountain.rcmdb.test.util.scripting.ScriptManagerForTest
import connector.NotificationConnector
import com.ifountain.rcmdb.test.util.CompassForTests
import application.RapidApplication
import com.ifountain.rcmdb.test.util.RapidApplicationTestUtils
import com.ifountain.comp.test.util.logging.TestLogUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 27, 2009
* Time: 3:58:44 PM
* To change this template use File | Settings | File Templates.
*/
class EmailSenderScriptTests extends RapidCmdbWithCompassTestCase {

    def connectorParams=[name:"testConnector"];
    def destination="abdurrahim"
    def scripts_directory="../testoutput";

    def RsEvent;
    def RsHistoricalEvent;
    def RsEventOperations;
    def RsHistoricalEventOperations;
    def RsEventJournal;
    def RsTemplate;

    def DESTINATION_TYPE="email";



    public void setUp() throws Exception {
        super.setUp();


        
        ["RsEvent","RsHistoricalEvent","RsEventJournal","RsEventOperations","RsHistoricalEventOperations","RsTemplate"].each{ className ->
            setProperty(className,gcl.loadClass(className));
        }
        
        clearMetaClasses();

        initialize([RsEvent,RsHistoricalEvent,RsEventJournal,RsMessage,RapidApplication], []);
        CompassForTests.addOperationSupport (RsMessage,RsMessageOperations);
        CompassForTests.addOperationSupport (RsEvent,RsEventOperations);
        CompassForTests.addOperationSupport (RsHistoricalEvent,RsHistoricalEventOperations);
        RapidApplicationTestUtils.initializeRapidApplicationOperations (RapidApplication);
        RapidApplicationTestUtils.clearProcessors();
        RapidApplication.getUtility("RsTemplate").metaClass.'static'.render={ String templatePath,params ->
            return "___renderTestResult";
        }

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
        GroovySystem.metaClassRegistry.removeMetaClass(NotificationConnector);
        ExpandoMetaClass.enableGlobally();
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
        RapidApplication.getUtility("RsTemplate").metaClass.'static'.render={ String templatePath,params ->
            renderTemplateParams.add([templatePath:templatePath,params:params]);
            return "renderTestResult";
        }

        def sendMessageParams=[];

        def mockDatasource=[:];
        mockDatasource.connection=[username:"IFountainEmailSender@ifountain.com"];
        mockDatasource.sendEmail= { Map params ->
            sendMessageParams.add([params:params]);
            println "my send email";
        }

        NotificationConnector.metaClass.'static'.get={ Map props ->
            return [ds:mockDatasource,name:DESTINATION_TYPE]
        }


        assertEquals(RsEvent.countHits("alias:*"),0)
        assertEquals(RsMessage.countHits("alias:*"),0)

        def events=addEvents("testev1",2)
        events.each{ event ->
            RsMessage.add(eventId:event.id,destination:destination,destinationType:DESTINATION_TYPE,eventType:RsMessage.EVENT_TYPE_CREATE,state:RsMessage.STATE_READY);
        }
        assertEquals(RsEvent.countHits("alias:*"),2)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),2)

        def historicalEvents=addHistoricalEvents("testhistev1",2)
        historicalEvents.each{ event ->
            RsMessage.add(eventId:event.activeId,destination:destination,destinationType:DESTINATION_TYPE,eventType:RsMessage.EVENT_TYPE_CLEAR,state:RsMessage.STATE_READY);
        }
        assertEquals(RsHistoricalEvent.countHits("alias:*"),2)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY} AND eventType:${RsMessage.EVENT_TYPE_CREATE}"),2)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY} AND eventType:${RsMessage.EVENT_TYPE_CLEAR}"),2)


        ScriptManagerForTest.runScript("emailSender",[staticParamMap:[connectorName:connectorParams.name],logger:TestLogUtils.log])
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),0)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_SENT}"),4)
        

        assertEquals(4,sendMessageParams.size());
        assertEquals(4,renderTemplateParams.size());

        def index=0;
        RsMessage.searchEvery("alias:*",[sort: "id",order:"asc"]).each{ message ->
           def event=message.retrieveEvent();

           assertEquals("grails-app/templates/message/emailTemplate.gsp",renderTemplateParams[index].templatePath);
           assertEquals(event.id,renderTemplateParams[index].params.event.id);
           assertEquals(message.id,renderTemplateParams[index].params.message.id);
           assertEquals(2,renderTemplateParams[index].params.size());

           assertEquals("IFountainEmailSender@ifountain.com",sendMessageParams[index].params.from)
           assertEquals(message.destination,sendMessageParams[index].params.to)
           assertEquals("renderTestResult",sendMessageParams[index].params.body)
           assertEquals("text/html",sendMessageParams[index].params.contentType)
            if(message.eventType==RsMessage.EVENT_TYPE_CREATE)
                assertEquals("Event Created",sendMessageParams[index].params.subject)
            else
                assertEquals("Event Cleared",sendMessageParams[index].params.subject)


           index++;
        }


    }

    void testSenderProcessesMessages()
    {
        def mockDatasource=[:];
        mockDatasource.connection=[username:"IFountainEmailSender@ifountain.com"];
        mockDatasource.sendEmail= { Map params ->
            println "my send email";
        }
        
        NotificationConnector.metaClass.'static'.get={ Map props ->
            return [ds:mockDatasource,name:DESTINATION_TYPE]
        }

        assertEquals(RsEvent.countHits("alias:*"),0)
        assertEquals(RsMessage.countHits("alias:*"),0)

        def events=addEvents("testev1",2)
        events.each{ event ->
            RsMessage.add(eventId:event.id,destination:destination,destinationType:DESTINATION_TYPE,eventType:RsMessage.EVENT_TYPE_CREATE,state:RsMessage.STATE_READY);
        }
        assertEquals(RsEvent.countHits("alias:*"),2)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),2)

        def historicalEvents=addHistoricalEvents("testhistev1",2)
        historicalEvents.each{ event ->
            RsMessage.add(eventId:event.activeId,destination:destination,destinationType:DESTINATION_TYPE,eventType:RsMessage.EVENT_TYPE_CLEAR,state:RsMessage.STATE_READY);
        }
        assertEquals(RsHistoricalEvent.countHits("alias:*"),2)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY} AND eventType:${RsMessage.EVENT_TYPE_CREATE}"),2)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY} AND eventType:${RsMessage.EVENT_TYPE_CLEAR}"),2)




        ScriptManagerForTest.runScript("emailSender",[staticParamMap:[connectorName:connectorParams.name]])
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),0)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_SENT}"),4)
    }
    void testSenderMarksMessagesWithErrorIfSendGeneratesExceptionAndDoesNotSendIfErrorLimitIsReached()
    {
        def mockDatasource=[:];
        mockDatasource.connection=[username:"IFountainEmailSender@ifountain.com"];
        mockDatasource.sendEmail= { Map params ->
            println "will generate exception in sendEmail"
            throw new Exception("Can not send email");
        }

        NotificationConnector.metaClass.'static'.get={ Map props ->
            return [ds:mockDatasource,name:DESTINATION_TYPE]
        }

        assertEquals(RsEvent.countHits("alias:*"),0)
        assertEquals(RsMessage.countHits("alias:*"),0)

        def events=addEvents("testev1",2)
        events.each{ event ->
            RsMessage.add(eventId:event.id,destination:destination,destinationType:DESTINATION_TYPE,eventType:RsMessage.EVENT_TYPE_CREATE,state:RsMessage.STATE_READY);
        }
        assertEquals(RsEvent.countHits("alias:*"),2)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),2)

        def historicalEvents=addHistoricalEvents("testhistev1",2)
        historicalEvents.each{ event ->
            RsMessage.add(eventId:event.activeId,destination:destination,destinationType:DESTINATION_TYPE,eventType:RsMessage.EVENT_TYPE_CLEAR,state:RsMessage.STATE_READY);
        }
        assertEquals(RsHistoricalEvent.countHits("alias:*"),2)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY} AND eventType:${RsMessage.EVENT_TYPE_CREATE}"),2)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY} AND eventType:${RsMessage.EVENT_TYPE_CLEAR}"),2)
        assertEquals(RsMessage.countHits("tryCount:0"),4)

        ScriptManagerForTest.runScript("emailSender",[staticParamMap:[connectorName:connectorParams.name]])
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),0)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_ERROR}"),4)
        assertEquals(RsMessage.countHits("tryCount:1"),4)

        ScriptManagerForTest.runScript("emailSender",[staticParamMap:[connectorName:connectorParams.name]])
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_ERROR}"),4)
        assertEquals(RsMessage.countHits("tryCount:2"),4)

        4.times{
            ScriptManagerForTest.runScript("emailSender",[staticParamMap:[connectorName:connectorParams.name]])
        }
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_ERROR_LIMIT}"),4)
        assertEquals(RsMessage.countHits("tryCount:5"),4)


    }
    void testSenderProcessesMessagesAddedByRsMessageOperations()
    {
        def mockDatasource=[:];
        mockDatasource.connection=[username:"IFountainEmailSender@ifountain.com"];
        mockDatasource.sendEmail= { Map params ->
             println "my send email";
        }

        NotificationConnector.metaClass.'static'.get={ Map props ->
            return [ds:mockDatasource,name:DESTINATION_TYPE]
        }
        

        assertEquals(RsEvent.countHits("alias:*"),0)
        assertEquals(RsMessage.countHits("alias:*"),0)

        def events=addEvents("testev1",2)
        events.each{ event ->
            RsMessage.addEventCreateMessage([id:event.id],DESTINATION_TYPE, destination,0)
        }
        assertEquals(RsEvent.countHits("alias:*"),2)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),2)


        ScriptManagerForTest.runScript("emailSender",[staticParamMap:[connectorName:connectorParams.name]])

        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),0)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_SENT}"),2)

        events.each{
            it.clear();
        }
        RsHistoricalEvent.saveHistoricalEventCache();

        assertEquals(RsEvent.countHits("alias:*"),0)
        assertEquals(RsHistoricalEvent.countHits("alias:*"),2)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),0)
        RsHistoricalEvent.list().each{ event ->
            RsMessage.addEventClearMessage([activeId:event.activeId],DESTINATION_TYPE, destination)
        }
        assertEquals(RsMessage.countHits("alias:*"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY} AND eventType:${RsMessage.EVENT_TYPE_CLEAR}"),2)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_SENT} AND eventType:${RsMessage.EVENT_TYPE_CREATE}"),2)
        
        ScriptManagerForTest.runScript("emailSender",[staticParamMap:[connectorName:connectorParams.name]])
        assertEquals(RsMessage.countHits("alias:*"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_SENT}"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_SENT} AND eventType:${RsMessage.EVENT_TYPE_CLEAR}"),2)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_SENT} AND eventType:${RsMessage.EVENT_TYPE_CREATE}"),2)


    }
    void testSenderDoesNotProcessMessagesIfConnectorIsMissing()
    {
        def mockDatasource=[:];
        mockDatasource.connection=[username:"IFountainEmailSender@ifountain.com"];
        mockDatasource.sendEmail= { Map params ->
             println "my send email";
        }

        NotificationConnector.metaClass.'static'.get={ Map props ->
            return [ds:mockDatasource,name:DESTINATION_TYPE]
        }

        assertEquals(RsEvent.countHits("alias:*"),0)
        assertEquals(RsMessage.countHits("alias:*"),0)

        def events=addEvents("testev1",2)
        events.each{ event ->
            RsMessage.add(eventId:event.id,destination:destination,destinationType:DESTINATION_TYPE,eventType:RsMessage.EVENT_TYPE_CREATE,state:RsMessage.STATE_READY);
        }
        assertEquals(RsEvent.countHits("alias:*"),2)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),2)

        ScriptManagerForTest.runScript("emailSender",[staticParamMap:[connectorName:connectorParams.name]])

        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),0)

        events.each{ event ->
            RsMessage.add(eventId:event.id,destination:destination,destinationType:DESTINATION_TYPE,eventType:RsMessage.EVENT_TYPE_CREATE,state:RsMessage.STATE_READY);
        }
        assertEquals(RsEvent.countHits("alias:*"),2)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),2)


        NotificationConnector.metaClass.'static'.get={ Map props ->
            return null;
        }
        def connector=NotificationConnector.get(name:connectorParams.name);
        assertNull(connector);

        ScriptManagerForTest.runScript("emailSender",[staticParamMap:[connectorName:connectorParams.name]])
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),2)

    }
    void testSenderChangesMessageStateWhenEventsAreMissing(){
        def mockDatasource=[:];
        mockDatasource.connection=[username:"IFountainEmailSender@ifountain.com"];
        mockDatasource.sendEmail= { Map params ->
             println "my send email";
        }

        NotificationConnector.metaClass.'static'.get={ Map props ->
            return [ds:mockDatasource,name:DESTINATION_TYPE]
        }

        assertEquals(RsEvent.countHits("alias:*"),0)
        assertEquals(RsHistoricalEvent.countHits("alias:*"),0)
        assertEquals(RsMessage.countHits("alias:*"),0)


        2.times{ eventId ->
            RsMessage.add(eventId:eventId,destination:destination,destinationType:DESTINATION_TYPE,eventType:RsMessage.EVENT_TYPE_CREATE,state:RsMessage.STATE_READY);
        }
        2.times{ eventId ->
            RsMessage.add(eventId:eventId,destination:destination,destinationType:DESTINATION_TYPE,eventType:RsMessage.EVENT_TYPE_CLEAR,state:RsMessage.STATE_READY);
        }

        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY} AND eventType:${RsMessage.EVENT_TYPE_CREATE}"),2)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY} AND eventType:${RsMessage.EVENT_TYPE_CLEAR}"),2)

        ScriptManagerForTest.runScript("emailSender",[staticParamMap:[connectorName:connectorParams.name]])

        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),0)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY} AND eventType:${RsMessage.EVENT_TYPE_CREATE}"),0)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY} AND eventType:${RsMessage.EVENT_TYPE_CLEAR}"),0)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_NOT_EXISTS}"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_NOT_EXISTS} AND eventType:${RsMessage.EVENT_TYPE_CREATE}"),2)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_NOT_EXISTS} AND eventType:${RsMessage.EVENT_TYPE_CLEAR}"),2)
    }


}