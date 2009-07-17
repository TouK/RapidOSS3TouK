package message

import application.RsApplication
import auth.*
import com.ifountain.compass.search.FilterSessionListener
import com.ifountain.rcmdb.methods.MethodFactory
import com.ifountain.rcmdb.scripting.ScriptManager
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.RsApplicationTestUtils
import com.ifountain.session.SessionManager
import org.apache.commons.io.FileUtils
import org.apache.log4j.Level
import script.CmdbScript
import script.CmdbScriptOperations
import search.SearchQuery
import search.SearchQueryGroup
import com.ifountain.rcmdb.auth.SegmentQueryHelper
import com.ifountain.comp.test.util.logging.TestLogUtils

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: May 15, 2009
* Time: 4:59:31 PM
*/
class MessageGeneratorScriptTests extends RapidCmdbWithCompassTestCase {
    def rsEventClass;
    def rsHistoricalEventClass;
    def rsLookupClass;
    def rsEventJournalClass;
    def base_directory = "";
    def script_manager_directory = "../testoutput/";
    def script_directory;
    def destination = "abdurrahim"
    String scriptName = "messageGenerator"
    void setUp() {
        super.setUp();
        clearMetaClasses();
        base_directory = getWorkspacePath()+"/RapidModules/RapidInsight";
        initializeScriptManager();
        initializeClasses();
        SegmentQueryHelper.getInstance().initialize([rsEventClass, rsHistoricalEventClass, rsLookupClass, rsEventJournalClass])
    }
    void tearDown() {
        SessionManager.destroyInstance();
        clearMetaClasses();
        super.tearDown();
    }
    public void clearMetaClasses()
    {
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(RsMessageRuleOperations)
        ExpandoMetaClass.enableGlobally();
    }
    private void initializeClasses() {
        rsEventClass = gcl.loadClass("RsEvent")
        rsHistoricalEventClass = gcl.loadClass("RsHistoricalEvent")
        rsLookupClass = gcl.loadClass("RsLookup")
        rsEventJournalClass = gcl.loadClass("RsEventJournal")
        def rsEventOperationsClass = gcl.loadClass("RsEventOperations")
        initialize([CmdbScript, RsUser, RsUserInformation, ChannelUserInformation, Role, Group, SearchQueryGroup, SearchQuery, RsMessageRule, RsMessage,
                rsEventClass, rsHistoricalEventClass, rsLookupClass, rsEventJournalClass], [], true);

        CompassForTests.addOperationSupport(CmdbScript, CmdbScriptOperations);
        CompassForTests.addOperationSupport(RsMessage, RsMessageOperations);
        CompassForTests.addOperationSupport(RsMessageRule, RsMessageRuleOperations);
        CompassForTests.addOperationSupport(RsUser, RsUserOperations);
        CompassForTests.addOperationSupport(Group, GroupOperations);
        CompassForTests.addOperationSupport(rsEventClass, rsEventOperationsClass);
        RsApplicationTestUtils.initializeRsApplicationOperations(RsApplication);
    }

    private void initializeScriptManager() {
        ScriptManager manager = ScriptManager.getInstance();
        if (new File(script_manager_directory).exists())
        {
            FileUtils.deleteDirectory(new File(script_manager_directory));
        }
        def defaultMethods = [
                "${MethodFactory.WITH_SESSION_METHOD}": MethodFactory.createMethod(MethodFactory.WITH_SESSION_METHOD)
        ]
        manager.initialize(this.class.getClassLoader(), script_manager_directory, [], defaultMethods);
        script_directory = "$script_manager_directory/$ScriptManager.SCRIPT_DIRECTORY";
        new File(script_directory).mkdirs();
    }

    void copyScript(scriptName)
    {
        def scriptPath = "${base_directory}/scripts/${scriptName}.groovy";

        def ant = new AntBuilder();

        ant.copy(file: scriptPath, toDir: script_directory, overwrite: true);
    }

    void testMessageGeneratorDoesNotProcessDisabledRules()
    {
        def user = RsUser.add(username: "sezgin", passwordHash: "sezgin");
        user.addEmail(destination)
        assertFalse(user.hasErrors())

        def adminUser = RsUser.RSADMIN;
        def defaultEventGroup = SearchQueryGroup.add(name: "MyDefault", username: adminUser, isPublic: true, type: "event");

        def searchQuery = SearchQuery.add(group: defaultEventGroup, name: "My All Events", query: "alias:*", sortProperty: "changedAt", sortOrder: "desc", username: adminUser, isPublic: true, type: "event");

        def rule = RsMessageRule.add(userId: user.id, searchQueryId: searchQuery.id, destinationType: RsMessage.EMAIL, enabled: false, clearAction: true)
        assertFalse(rule.hasErrors())
        assertEquals(RsMessageRule.countHits("alias:*"), 1)

        copyScript(scriptName)
        def script = CmdbScript.addScript(name: scriptName, type: CmdbScript.ONDEMAND, logLevel: Level.DEBUG)
        assertFalse(script.hasErrors())

        CmdbScript.runScript(script, [:])
        assertEquals(RsMessage.countHits("alias:*"), 0)

        addEvents("newevents", 4)
        assertEquals(rsEventClass."countHits"("alias:*"), 4)

        CmdbScript.runScript(script, [:])
        assertEquals(RsMessage.countHits("alias:*"), 0)
    }

    void testMessageGeneratorDoesNotProcessIfUserAndAdminDoesNotHaveDestination()
    {
        def destinationType = RsMessage.EMAIL
        def user = RsUser.add(username: "sezgin", passwordHash: "sezgin");
        assertFalse(user.hasErrors())

        def adminGroup=createGroupWithRole("adminGroup",Role.ADMINISTRATOR);
        def adminUser=RsUser.add(username:"adminUser",passwordHash:"aaa");
        assertFalse(adminUser.hasErrors());

        def adminUserName = RsUser.RSADMIN;
        def defaultEventGroup = SearchQueryGroup.add(name: "MyDefault", username: adminUserName, isPublic: true, type: "event");

        def searchQuery = SearchQuery.add(group: defaultEventGroup, name: "My All Events", query: "alias:*", sortProperty: "changedAt", sortOrder: "desc", username: adminUserName, isPublic: true, type: "event");


        def rule = RsMessageRule.add(userId: user.id, searchQueryId: searchQuery.id, destinationType: destinationType, enabled: true, clearAction: true)
        assertFalse(rule.hasErrors())

        def ruleForAdmin = RsMessageRule.add(userId: adminUser.id, searchQueryId: searchQuery.id, destinationType: destinationType, enabled: true, clearAction: true)
        assertFalse(ruleForAdmin.hasErrors())

        assertEquals(RsMessageRule.countHits("alias:*"), 2)

        copyScript(scriptName)
        def script = CmdbScript.addScript(name: scriptName, type: CmdbScript.ONDEMAND, logLevel: Level.DEBUG)
        assertFalse(script.hasErrors())


        
        CmdbScript.runScript(script, [:])
        assertEquals(RsMessage.countHits("alias:*"), 0)


        def newEvents = addEvents("newevents", 4)
        assertEquals(rsEventClass."countHits"("alias:*"), 4)

        CmdbScript.runScript(script, [:])
        assertEquals(RsMessage.countHits("alias:*"), 0)
    }

    void testMessageGeneratorProcessNonChannelDestinationsForAdminButNotForUser()
    {
        RsMessageRuleOperations.metaClass.'static'.getDestinations = { ->
            return [
                    [name:"email",channelType:"email"],
                    [name:"destwithnochannel"]
                   ];
        }

        def destinationType = "destwithnochannel";

        def adminUserName = RsUser.RSADMIN;

        def adminGroup=createGroupWithRole("adminGroup",Role.ADMINISTRATOR);
        def adminUser = RsUser.add(username: "testadmin", passwordHash:"aaa",groups:[adminGroup]);
        assertFalse(adminUser.hasErrors());

        def user=RsUser.add(username:"testuser",passwordHash:"bbb");
        assertFalse(user.hasErrors());


        def defaultEventGroup = SearchQueryGroup.add(name: "MyDefault", username: adminUserName, isPublic: true, type: "event");

        def searchQuery = SearchQuery.add(group: defaultEventGroup, name: "My All Events", query: "alias:*", sortProperty: "changedAt", sortOrder: "desc", username: adminUserName, isPublic: true, type: "event");


        def rule = RsMessageRule.add(userId: adminUser.id, searchQueryId: searchQuery.id, destinationType: destinationType, enabled: true, clearAction: true)
        assertFalse(rule.errors.toString(),rule.hasErrors())


        def ruleForUser = RsMessageRule.add(userId: user.id, searchQueryId: searchQuery.id, destinationType: destinationType, enabled: true, clearAction: true)
        assertFalse(ruleForUser.errors.toString(),ruleForUser.hasErrors())

        assertEquals(RsMessageRule.countHits("alias:*"), 2)

        copyScript(scriptName)
        def script = CmdbScript.addScript(name: scriptName, type: CmdbScript.ONDEMAND, logLevel: Level.DEBUG)
        assertFalse(script.hasErrors())


        CmdbScript.runScript(script, [:])
        assertEquals(RsMessage.countHits("alias:*"), 0)


        def newEvents = addEvents("newevents", 4)
        assertEquals(rsEventClass."countHits"("alias:*"), 4)

        CmdbScript.runScript(script, [:])
        assertEquals(RsMessage.countHits("alias:*"), 4)
        assertEquals(RsMessage.countHits("destination:admin_destination AND destinationType:${destinationType}"), 4)


    }

    void testMessageGeneratorDoesNotProcessClearEventsForClearDisabledRules()
    {
        def destinationType = RsMessage.EMAIL
        def destination = "sezgin@gmail.com"
        def user = RsUser.add(username: "sezgin", passwordHash: "sezgin");
        assertFalse(user.hasErrors())
        def imInformation = ChannelUserInformation.add(userId: user.id, type: destinationType, destination: destination, rsUser: user);
        assertFalse(imInformation.hasErrors());

        def adminUser = RsUser.RSADMIN;
        def defaultEventGroup = SearchQueryGroup.add(name: "MyDefault", username: adminUser, isPublic: true, type: "event");

        def searchQuery = SearchQuery.add(group: defaultEventGroup, name: "My All Events", query: "alias:*", sortProperty: "changedAt", sortOrder: "desc", username: adminUser, isPublic: true, type: "event");

        def rule = RsMessageRule.add(userId: user.id, searchQueryId: searchQuery.id, destinationType: destinationType, enabled: true, clearAction: false)
        assertFalse(rule.hasErrors())
        assertEquals(RsMessageRule.countHits("alias:*"), 1)

        copyScript(scriptName)
        def script = CmdbScript.addScript(name: scriptName, type: CmdbScript.ONDEMAND, logLevel: Level.DEBUG)
        assertFalse(script.hasErrors())

        CmdbScript.runScript(script, [:])
        assertEquals(RsMessage.countHits("alias:*"), 0)


        def newEvents = addEvents("newevents", 4)
        assertEquals(rsEventClass."countHits"("alias:*"), 4)

        CmdbScript.runScript(script, [:])
        assertEquals(RsMessage.countHits("alias:*"), 4)

        newEvents.each {
            it.clear();
        }
        assertEquals(rsHistoricalEventClass."countHits"("alias:*"), 4)
        CmdbScript.runScript(script, [:])
        assertEquals(RsMessage.countHits("alias:*"), 4)
    }

    void testEmailGeneratorProcessNewEventsAndDoesNotProcessOldEvents()
    {
        def destinationType = RsMessage.EMAIL
        def destination = "sezgin@gmail.com"
        def user = RsUser.add(username: "sezgin", passwordHash: "sezgin");
        assertFalse(user.hasErrors())
        def imInformation = ChannelUserInformation.add(userId: user.id, type: destinationType, destination: destination, rsUser: user);
        assertFalse(imInformation.hasErrors());

        def adminUser = RsUser.RSADMIN;
        def defaultEventGroup = SearchQueryGroup.add(name: "MyDefault", username: adminUser, isPublic: true, type: "event");

        def searchQuery = SearchQuery.add(group: defaultEventGroup, name: "My All Events", query: "alias:*", sortProperty: "changedAt", sortOrder: "desc", username: adminUser, isPublic: true, type: "event");


        def rule = RsMessageRule.add(userId: user.id, searchQueryId: searchQuery.id, destinationType: destinationType, enabled: true, clearAction: true)
        assertFalse(rule.hasErrors())
        assertEquals(RsMessageRule.countHits("alias:*"), 1)

        copyScript(scriptName)
        def script = CmdbScript.addScript(name: scriptName, type: CmdbScript.ONDEMAND, logLevel: Level.DEBUG)
        assertFalse(script.hasErrors())

        // add old events
        addEvents("oldevents", 4)
        assertEquals(rsEventClass."countHits"("alias:*"), 4)


        //add old historical events
        addHistoricalEvents("oldclearevents", 4)
        assertEquals(rsHistoricalEventClass."countHits"("alias:*"), 4)

        def maxEventId = 0
        def maxEvent = rsEventClass."search"("alias:*", [max: 1, sort: "id", order: "desc"]).results[0]
        if (maxEvent != null)
        {
            maxEventId = Long.valueOf(maxEvent.id) + 1
        }

        def maxEventClearId = 0
        def maxClearEvent = rsHistoricalEventClass."search"("alias:*", [max: 1, sort: "id", order: "desc"]).results[0]
        if (maxClearEvent != null)
        {
            maxEventClearId = Long.valueOf(maxClearEvent.id) + 1
        }

        //run the script
        CmdbScript.runScript(script, [:])
        assertEquals(RsMessage.countHits("alias:*"), 0)

        //add new events
        def newEvents = addEvents("newevents", 4)
        assertEquals(rsEventClass."countHits"("alias:*"), 8)

        //run the script and check that only new events are processed
        CmdbScript.runScript(script, [:])
        assertEquals(4, RsMessage.countHits("alias:*"))
        assertEquals(4, RsMessage.countHits("action:${RsMessage.ACTION_CREATE}"))
        RsMessage.searchEvery("action:${RsMessage.ACTION_CREATE}").each {mes ->
            assertEquals(mes.destination, destination)
            assertEquals(mes.destinationType, destinationType)
            def event = rsEventClass."get"(id: mes.eventId)
            assertNotNull(event)
            assertTrue(event.id > maxEventId)
        }

        //now clear the events
        newEvents.each {
            it.clear();
        }
        assertEquals(rsHistoricalEventClass."countHits"("alias:*"), 8)

        CmdbScript.runScript(script, [:])
        assertEquals(RsMessage.countHits("alias:*"), 8)
        assertEquals(RsMessage.countHits("action:${RsMessage.ACTION_CLEAR}"), 4)
        RsMessage.searchEvery("action:${RsMessage.ACTION_CLEAR}").each {mes ->
            assertEquals(mes.destination, destination)
            assertEquals(mes.destinationType, destinationType)
            def event = rsHistoricalEventClass."search"("activeId:${mes.eventId}").results[0]
            assertNotNull(event)
            assertTrue(event.id > maxEventClearId)
        }
    }

    void testMessageGeneratorProcessDelayedMessages()
    {
        assertEquals(RsMessage.count(), 0)
        def date = new Date();
        def delay = 5000
        def params = [:]
        params.eventId = 1
        params.state = RsMessage.STATE_IN_DELAY
        params.destination = "xxx"
        params.destinationType = RsMessage.EMAIL
        params.action = RsMessage.ACTION_CREATE
        params.sendAfter = date.getTime() + delay


        copyScript(scriptName)
        def script = CmdbScript.addScript(name: scriptName, type: CmdbScript.ONDEMAND, logLevel: Level.DEBUG)
        assertFalse(script.hasErrors())

        def message = RsMessage.add(params)
        assertFalse(message.hasErrors())


        CmdbScript.runScript(script, [:])
        def mes = RsMessage.get(id: message.id)
        assertEquals(mes.state, RsMessage.STATE_IN_DELAY)
        Thread.sleep(delay + 1000)

        CmdbScript.runScript(script, [:])
        mes = RsMessage.get(id: message.id)
        assertEquals(mes.state, RsMessage.STATE_READY)
    }

    void testMessageGeneratorUsesWithSessionToApplySegmentation()
    {
        SessionManager.getInstance().addSessionListener(new FilterSessionListener());
        def destinationType = RsMessage.EMAIL
        def userRole = Role.add(name: Role.USER);
        def userGroup = Group.add(name: "testusergroup", role: userRole, segmentFilter: "severity:2");
        assertFalse(userGroup.hasErrors());
        def user = RsUser.add(username: "testuser", passwordHash: "xxx");
        assertFalse(user.hasErrors());

        user.addRelation(groups: userGroup);
        assertFalse(user.hasErrors());
        assertEquals(user.groups.size(), 1);


        def imInformation = ChannelUserInformation.add(userId: user.id, type: destinationType, destination: "testuser@gmail.com", rsUser: user);
        assertFalse(imInformation.hasErrors());

        def defaultEventGroup = SearchQueryGroup.add(name: "MyDefault", username: user.username, isPublic: false, type: "event");
        assertFalse(defaultEventGroup.hasErrors())
        def searchQuery = SearchQuery.add(group: defaultEventGroup, name: "My All Events", query: "alias:*", sortProperty: "changedAt", sortOrder: "desc", username: user.username, isPublic: false, type: "event");
        assertFalse(searchQuery.hasErrors())

        def rule = RsMessageRule.add(userId: user.id, searchQueryId: searchQuery.id, destinationType: destinationType, enabled: true, clearAction: true)
        assertFalse(rule.hasErrors())
        assertEquals(RsMessageRule.countHits("alias:*"), 1)

        copyScript(scriptName)
        def script = CmdbScript.addScript(name: scriptName, type: CmdbScript.ONDEMAND, logLevel: Level.DEBUG)
        assertFalse(script.hasErrors())

        CmdbScript.runScript(script, [:])

        assertEquals(RsMessage.countHits("alias:*"), 0)

        def newEvents = addEvents("testevents", 4)
        assertEquals(rsEventClass."countHits"("alias:*"), 4)

        CmdbScript.runScript(script, [:])

        assertEquals(1, RsMessage.countHits("alias:*"))
        assertEquals(RsMessage.countHits("action:${RsMessage.ACTION_CREATE}"), 1)

        newEvents.each {
            it.clear();
        }
        assertEquals(rsHistoricalEventClass."countHits"("alias:*"), 4)

        CmdbScript.runScript(script, [:])
        assertEquals(RsMessage.countHits("alias:*"), 2)
        assertEquals(RsMessage.countHits("action:${RsMessage.ACTION_CLEAR}"), 1)

        //now we change the segment filter and test again
        rsEventClass."removeAll"();
        rsHistoricalEventClass."removeAll"();
        RsMessage.removeAll();


        userGroup.update(segmentFilter: "severity:[2 TO 3]")
        assertFalse(userGroup.hasErrors())

        def newEvents2 = addEvents("testevents2", 4)
        assertEquals(rsEventClass."countHits"("alias:*"), 4)

        CmdbScript.runScript(script, [:])
        assertEquals(RsMessage.countHits("alias:*"), 2)
        assertEquals(RsMessage.countHits("action:${RsMessage.ACTION_CREATE}"), 2)

        //now we remove the segment filter and test again
        rsEventClass."removeAll"();
        rsHistoricalEventClass."removeAll"();
        RsMessage.removeAll();


        userGroup.update(segmentFilter: "")
        assertFalse(userGroup.hasErrors())

        def newEvents3 = addEvents("testevents2", 4)
        assertEquals(rsEventClass."countHits"("alias:*"), 4)

        CmdbScript.runScript(script, [:])
        assertEquals(RsMessage.countHits("alias:*"), 4)
        assertEquals(RsMessage.countHits("action:${RsMessage.ACTION_CREATE}"), 4)
    }

    def addEvents(prefix, count)
    {
        def events = []
        count.times {
            def event = rsEventClass."add"(name: "${prefix}${it}", severity: it)
            assertFalse(event.hasErrors())
            events.add(event)
        }
        return events;
    }
    def addHistoricalEvents(prefix, count)
    {
        def events = []
        count.times {
            def event = rsHistoricalEventClass."add"(name: "${prefix}${it}", severity: it, activeId: it)
            assertFalse(event.hasErrors())
            events.add(event)
        }
        return events;
    }

     private def createGroupWithRole(groupName,roleName)
     {
        def role=Role.add(name:roleName);
        assertFalse(role.hasErrors());
        def group=Group.add(name:groupName,role:role);
        assertFalse(group.hasErrors());

        return group;
     }
}