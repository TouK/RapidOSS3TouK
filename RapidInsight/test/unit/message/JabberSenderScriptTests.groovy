package message

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import org.apache.commons.io.FileUtils
import datasource.JabberDatasource
import connection.JabberConnection
import com.ifountain.rcmdb.test.util.scripting.ScriptManagerForTest
import connector.JabberConnector
import connector.JabberConnectorOperations
import com.ifountain.rcmdb.test.util.CompassForTests
import datasource.JabberDatasourceOperations
import application.RsApplication
import com.ifountain.rcmdb.test.util.RsApplicationTestUtils
import com.ifountain.comp.test.util.logging.TestLogUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Aug 7, 2009
* Time: 3:19:04 PM
* To change this template use File | Settings | File Templates.
*/
class JabberSenderScriptTests  extends RapidCmdbWithCompassTestCase {
    def connectorParams;
    def destination="abdurrahim"
    def scripts_directory="../testoutput";

    def RsEvent;
    def RsHistoricalEvent;
    def RsEventOperations;
    def RsEventJournal;
    def RsTemplate;

    def JABBER_TYPE="jabber";



    public void setUp() throws Exception {
        super.setUp();



        ["RsEvent","RsHistoricalEvent","RsEventJournal","RsEventOperations","RsTemplate"].each{ className ->
            setProperty(className,gcl.loadClass(className));
        }

        clearMetaClasses();


        RsTemplate.metaClass.'static'.render={ String templatePath,params ->
            return "___renderTestResult";
        }

        initialize([RsEvent,RsHistoricalEvent,RsEventJournal,RsMessage,RsApplication,JabberConnector,JabberConnection,JabberDatasource], []);
        CompassForTests.addOperationSupport (JabberConnector,JabberConnectorOperations);
        CompassForTests.addOperationSupport (JabberDatasource,JabberDatasourceOperations);
        CompassForTests.addOperationSupport (RsMessage,RsMessageOperations);
        CompassForTests.addOperationSupport (RsEvent,RsEventOperations);
        RsApplicationTestUtils.initializeRsApplicationOperations (RsApplication);
        RsApplicationTestUtils.clearProcessors();

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


        connectorParams["name"] = "testConnector";
        connectorParams["host"]="jabberHost"
        connectorParams["port"]=5222
        connectorParams["username"] = "testaccount";
        connectorParams["userPassword"] = "3600";
        connectorParams["serviceName"] = "a.com";
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

        FileUtils.copyFileToDirectory (new File(getWorkspacePath()+"/RapidModules/RapidInsight/scripts/jabberSender.groovy"),scriptsDir);

        ScriptManagerForTest.addScript('jabberSender');
    }

    void testSenderCallsRenderTemplateAndPassesTemplateResultToSendMessage()
    {
        def renderTemplateParams=[];
        RsTemplate.metaClass.'static'.render={ String templatePath,params ->
            renderTemplateParams.add([templatePath:templatePath,params:params]);
            return "renderTestResult";
        }

        def sendMessageParams=[];

        JabberDatasource.metaClass.sendMessage= { target, message ->
            sendMessageParams.add([target:target,message:message]);
            println "my send message";
        }


        assertEquals(RsEvent.countHits("alias:*"),0)
        assertEquals(RsMessage.countHits("alias:*"),0)

        def events=addEvents("testev1",4)
        events.each{ event ->
            RsMessage.add(eventId:event.id,destination:destination,destinationType:JABBER_TYPE,action:RsMessage.ACTION_CREATE,state:RsMessage.STATE_READY);
        }
        assertEquals(RsEvent.countHits("alias:*"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),4)

        def historicalEvents=addHistoricalEvents("testhistev1",4)
        historicalEvents.each{ event ->
            RsMessage.add(eventId:event.activeId,destination:destination,destinationType:JABBER_TYPE,action:RsMessage.ACTION_CLEAR,state:RsMessage.STATE_READY);
        }
        assertEquals(RsHistoricalEvent.countHits("alias:*"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),8)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY} AND action:${RsMessage.ACTION_CREATE}"),4)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY} AND action:${RsMessage.ACTION_CLEAR}"),4)


        def connector=addConnector();

        TestLogUtils.enableLogger (TestLogUtils.log);

        ScriptManagerForTest.runScript("jabberSender",[staticParamMap:[connectorName:connectorParams.name],logger:TestLogUtils.log])
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_READY}"),0)
        assertEquals(RsMessage.countHits("state:${RsMessage.STATE_SENT}"),8)


        assertEquals(8,sendMessageParams.size());
        assertEquals(8,renderTemplateParams.size());

        def index=0;
        RsMessage.searchEvery("alias:*",[sort: "id",order:"asc"]).each{ message ->
           def event=message.retrieveEvent();

           assertEquals("grails-app/templates/message/jabberTemplate.gsp",renderTemplateParams[index].templatePath);
           assertEquals(event.id,renderTemplateParams[index].params.event.id);
           assertEquals(message.id,renderTemplateParams[index].params.message.id);
           assertEquals(2,renderTemplateParams[index].params.size());

           assertEquals(message.destination,sendMessageParams[index].target)
           assertEquals("renderTestResult",sendMessageParams[index].message)
           
           index++;
        }


    }


    def addConnector()
    {
        assertEquals(0,JabberConnector.count())
        assertEquals(0,JabberConnection.count())
        assertEquals(0,JabberDatasource.count())

        def params=[:]
        params.putAll(connectorParams)

        def addedObjects=JabberConnector.addConnector(params);
        assertEquals(3,addedObjects.size());
        addedObjects.each{ key , object ->
            assertFalse(object.hasErrors());
        }
        assertNotNull(addedObjects.jabberConnector)

        assertEquals(1,JabberConnector.count())
        assertEquals(1,JabberConnection.count())
        assertEquals(1,JabberDatasource.count())
        return
    }
}