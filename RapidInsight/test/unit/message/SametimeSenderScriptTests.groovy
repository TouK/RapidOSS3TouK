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
* Date: Aug 7, 2009
* Time: 3:19:04 PM
* To change this template use File | Settings | File Templates.
*/
class SametimeSenderScriptTests  extends RapidCmdbWithCompassTestCase {
    def connectorParams=[name:"testConnector"];
    def destination="abdurrahim"
    def scripts_directory="../testoutput";

    def RsEvent;
    def RsHistoricalEvent;
    def RsEventOperations;
    def RsEventJournal;
    def RsTemplate;

    def DESTINATION_TYPE="sametime";



    public void setUp() throws Exception {
        super.setUp();



        ["RsEvent","RsHistoricalEvent","RsEventJournal","RsEventOperations","RsTemplate"].each{ className ->
            setProperty(className,gcl.loadClass(className));
        }

        clearMetaClasses();



        initialize([RsEvent,RsHistoricalEvent,RsEventJournal,RsMessage,RapidApplication], []);
        CompassForTests.addOperationSupport (RsMessage,RsMessageOperations);
        CompassForTests.addOperationSupport (RsEvent,RsEventOperations);
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

        FileUtils.copyFileToDirectory (new File(getWorkspacePath()+"/RapidModules/RapidInsight/scripts/sametimeSender.groovy"),scriptsDir);

        ScriptManagerForTest.addScript('sametimeSender');
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
        mockDatasource.sendMessage= { target, message ->
            sendMessageParams.add([target:target,message:message]);
            println "my send email";
        }

        NotificationConnector.metaClass.'static'.get={ Map props ->
            return [ds:mockDatasource,name:DESTINATION_TYPE]
        }


        assertEquals(RsEvent.countHits("alias:*"),0)
        assertEquals(RsMessage.countHits("alias:*"),0)

        def events=addEvents("testev1",4)
        events.each{ event ->
            RsMessage.add(eventId:event.id,destination:destination,destinationType:DESTINATION_TYPE,eventType:RsMessage.EVENT_TYPE_CREATE,state:RsMessage.STATE_READY);
        }
        assertEquals(RsEvent.countHits("alias:*"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),4)

        def historicalEvents=addHistoricalEvents("testhistev1",4)
        historicalEvents.each{ event ->
            RsMessage.add(eventId:event.activeId,destination:destination,destinationType:DESTINATION_TYPE,eventType:RsMessage.EVENT_TYPE_CLEAR,state:RsMessage.STATE_READY);
        }
        assertEquals(RsHistoricalEvent.countHits("alias:*"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),8)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY} AND eventType:${RsMessage.EVENT_TYPE_CREATE}"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY} AND eventType:${RsMessage.EVENT_TYPE_CLEAR}"),4)

        TestLogUtils.enableLogger (TestLogUtils.log);

        ScriptManagerForTest.runScript("sametimeSender",[staticParamMap:[connectorName:connectorParams.name],logger:TestLogUtils.log])
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),0)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_SENT}"),8)


        assertEquals(8,sendMessageParams.size());
        assertEquals(8,renderTemplateParams.size());

        def index=0;
        RsMessage.searchEvery("alias:*",[sort: "id",order:"asc"]).each{ message ->
           def event=message.retrieveEvent();

           assertEquals("grails-app/templates/message/sametimeTemplate.gsp",renderTemplateParams[index].templatePath);
           assertEquals(event.id,renderTemplateParams[index].params.event.id);
           assertEquals(message.id,renderTemplateParams[index].params.message.id);
           assertEquals(2,renderTemplateParams[index].params.size());

           assertEquals(message.destination,sendMessageParams[index].target)
           assertEquals("renderTestResult",sendMessageParams[index].message)

           index++;
        }


    }

}