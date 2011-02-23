package message

import application.RapidApplication
import auth.*
import com.ifountain.compass.search.FilterSessionListener
import com.ifountain.rcmdb.methods.MethodFactory
import com.ifountain.rcmdb.scripting.ScriptManager
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.RapidApplicationTestUtils
import com.ifountain.session.SessionManager
import org.apache.commons.io.FileUtils
import org.apache.log4j.Level
import script.CmdbScript
import script.CmdbScriptOperations
import search.SearchQuery
import search.SearchQueryGroup
import com.ifountain.rcmdb.auth.SegmentQueryHelper
import com.ifountain.comp.test.util.logging.TestLogUtils
import com.ifountain.rcmdb.auth.UserConfigurationSpace
import java.text.SimpleDateFormat

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: May 15, 2009
* Time: 4:59:31 PM
*/
class MessageGeneratorScriptTests extends RapidCmdbWithCompassTestCase {
    def rsEventClass;
    def rsHistoricalEventClass;
    def rsRiEventClass;
    def rsRiHistoricalEventClass;
    def rsLookupClass;
    def rsEventJournalClass;
    def base_directory = "";
    def script_manager_directory = "../testoutput/";
    def script_directory;
    

    String scriptName = "messageGenerator"
    void setUp() {
        super.setUp();
        clearMetaClasses();
        base_directory = getWorkspacePath() + "/RapidModules/RapidInsight";
        initializeScriptManager();
        initializeClasses();
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
        rsRiEventClass = gcl.loadClass("RsRiEvent")
        rsRiHistoricalEventClass = gcl.loadClass("RsRiHistoricalEvent")
        rsLookupClass = gcl.loadClass("RsLookup")
        rsEventJournalClass = gcl.loadClass("RsEventJournal")
        def rsEventOperationsClass = gcl.loadClass("RsEventOperations")
        def rsRiEventOperationsClass = gcl.loadClass("RsRiEventOperations")
        def rsHistoricalEventOperationsClass = gcl.loadClass("RsHistoricalEventOperations")
        initialize([CmdbScript, RsUser, RsUserInformation, ChannelUserInformation, Role, Group, SearchQueryGroup, SearchQuery, RsMessageRule, RsMessage, RsMessageRuleCalendar,
                rsEventClass, rsHistoricalEventClass, rsRiEventClass, rsRiHistoricalEventClass, rsLookupClass, rsEventJournalClass], [], true);

        CompassForTests.addOperationSupport(CmdbScript, CmdbScriptOperations);
        CompassForTests.addOperationSupport(RsMessage, RsMessageOperations);
        CompassForTests.addOperationSupport(RsMessageRule, RsMessageRuleOperations);
        CompassForTests.addOperationSupport(RsUser, RsUserOperations);
        CompassForTests.addOperationSupport(Group, GroupOperations);
        CompassForTests.addOperationSupport(rsEventClass, rsEventOperationsClass);
        CompassForTests.addOperationSupport(rsHistoricalEventClass, rsHistoricalEventOperationsClass);
        CompassForTests.addOperationSupport(rsRiEventClass, rsRiEventOperationsClass);
        RapidApplicationTestUtils.initializeRapidApplicationOperations(RapidApplication);
        UserConfigurationSpace.getInstance().initialize();

        RsMessageRuleOperations.setConfiguredDestinationNames(["email"]);
        
        SegmentQueryHelper.getInstance().initialize([rsEventClass, rsHistoricalEventClass, rsRiEventClass, rsRiHistoricalEventClass, rsLookupClass, rsEventJournalClass])
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
        manager.initialize(this.class.getClassLoader(), script_manager_directory, defaultMethods);
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
        def destination = "abdurrahim"
        user.addChannelInformation(type: "email", destination: destination);
        assertFalse(user.hasErrors())

        def adminUser = RsUser.RSADMIN;
        def defaultEventGroup = SearchQueryGroup.add(name: "MyDefault", username: adminUser, isPublic: true, type: "event");

        def searchQuery = SearchQuery.add(group: defaultEventGroup, name: "My All Events", query: "alias:*", sortProperty: "changedAt", sortOrder: "desc", username: adminUser, isPublic: true, type: "event");

        def rule = RsMessageRule.add(users: user.username, searchQueryId: searchQuery.id, destinationType: "email", enabled: false, sendClearEventType: true)
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
        def destinationType = "email"
        def user = RsUser.add(username: "sezgin", passwordHash: "sezgin");
        assertFalse(user.hasErrors())

        def adminGroup = createGroupWithRole("adminGroup", Role.ADMINISTRATOR);
        def adminUser = RsUser.add(username: "adminUser", passwordHash: "aaa");
        assertFalse(adminUser.hasErrors());

        def adminUserName = RsUser.RSADMIN;
        def defaultEventGroup = SearchQueryGroup.add(name: "MyDefault", username: adminUserName, isPublic: true, type: "event");

        def searchQuery = SearchQuery.add(group: defaultEventGroup, name: "My All Events", query: "alias:*", sortProperty: "changedAt", sortOrder: "desc", username: adminUserName, isPublic: true, type: "event");
        assertFalse(searchQuery.hasErrors())

        def rule = RsMessageRule.add(users:user.username,searchQueryId: searchQuery.id, destinationType: destinationType, enabled: true, sendClearEventType: true)
        assertFalse(rule.hasErrors())

        def ruleForAdmin = RsMessageRule.add( users:adminUser.username, searchQueryId: searchQuery.id, destinationType: destinationType, enabled: true, sendClearEventType: true)
        assertFalse("${ruleForAdmin.errors}",ruleForAdmin.hasErrors())

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
        RsMessageRuleOperations.metaClass.'static'.getDestinations = {->
            return [
                    [name: "email", channelType: "email"],
                    [name: "destwithnochannel"]
            ];
        }

        def destinationType = "destwithnochannel";

        def adminUserName = RsUser.RSADMIN;

        def adminGroup = createGroupWithRole("adminGroup", Role.ADMINISTRATOR);
        def adminUser = RsUser.addUser(username: "testadmin", password: "aaa", groups: [adminGroup]);
        assertFalse(adminUser.hasErrors());

        def user = RsUser.add(username: "testuser", passwordHash: "bbb");
        assertFalse(user.hasErrors());


        def defaultEventGroup = SearchQueryGroup.add(name: "MyDefault", username: adminUserName, isPublic: true, type: "event");

        def searchQuery = SearchQuery.add(group: defaultEventGroup, name: "My All Events", query: "alias:*", sortProperty: "changedAt", sortOrder: "desc", username: adminUserName, isPublic: true, type: "event");


        def rule = RsMessageRule.add(users:adminUser.username,searchQueryId: searchQuery.id, destinationType: destinationType, enabled: true, sendClearEventType: true)
        assertFalse(rule.errors.toString(), rule.hasErrors())


        def ruleForUser = RsMessageRule.add(users:user.username, searchQueryId: searchQuery.id, destinationType: destinationType, enabled: true, sendClearEventType: true)
        assertFalse(ruleForUser.errors.toString(), ruleForUser.hasErrors())

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
        def destinationType = "email"
        def destination = "sezgin@gmail.com"
        def user = RsUser.add(username: "sezgin", passwordHash: "sezgin");
        assertFalse(user.hasErrors())
        def imInformation = ChannelUserInformation.add(userId: user.id, type: destinationType, destination: destination, rsUser: user);
        assertFalse(imInformation.hasErrors());

        def adminUser = RsUser.RSADMIN;
        def defaultEventGroup = SearchQueryGroup.add(name: "MyDefault", username: adminUser, isPublic: true, type: "event");

        def searchQuery = SearchQuery.add(group: defaultEventGroup, name: "My All Events", query: "alias:*", sortProperty: "changedAt", sortOrder: "desc", username: adminUser, isPublic: true, type: "event");

        def rule = RsMessageRule.add(users: user.username, searchQueryId: searchQuery.id, destinationType: destinationType, enabled: true, sendClearEventType: false)
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
        rsHistoricalEventClass.saveHistoricalEventCache();

        assertEquals(rsHistoricalEventClass."countHits"("alias:*"), 4)
        CmdbScript.runScript(script, [:])
        assertEquals(RsMessage.countHits("alias:*"), 4)
    }

    void testMessageGeneratorProcessesOnlyEventsOfTheQuerySearchClass()
    {
        def destinationType = "email"
        def destination = "sezgin@gmail.com"
        def user = RsUser.add(username: "sezgin", passwordHash: "sezgin");
        assertFalse(user.hasErrors())
        def imInformation = ChannelUserInformation.add(userId: user.id, type: destinationType, destination: destination, rsUser: user);
        assertFalse(imInformation.hasErrors());

        def adminUser = RsUser.RSADMIN;
        def defaultEventGroup = SearchQueryGroup.add(name: "MyDefault", username: adminUser, isPublic: true, type: "event");

        def searchQuery = SearchQuery.add(group: defaultEventGroup, name: "My All Events", searchClass: "RsRiEvent", query: "alias:*", sortProperty: "changedAt", sortOrder: "desc", username: adminUser, isPublic: true, type: "event");

        def rule = RsMessageRule.add(users: user.username, searchQueryId: searchQuery.id, destinationType: destinationType, enabled: true, sendClearEventType: true)
        assertFalse(rule.hasErrors())

        assertEquals(RsMessageRule.countHits("alias:*"), 1)

        copyScript(scriptName)
        def script = CmdbScript.addScript(name: scriptName, type: CmdbScript.ONDEMAND, logLevel: Level.DEBUG)
        assertFalse(script.hasErrors())

        //TestLogUtils.enableLogger ();

        //add  events
        def newEvents = addEvents("newevents", 2)
        assertEquals(rsEventClass.countHits("alias:\"(RsEvent)\""), 2)
        assertEquals(rsRiEventClass.countHits("alias:*"), 0)

        //run the script
        CmdbScript.runScript(script, [logger: TestLogUtils.log])
        assertEquals(RsMessage.countHits("alias:*"), 0)

        //add new RsRiEvents
        def newRsRiEvents = addEvents("newrsrievents", 2, rsRiEventClass)
        assertEquals(rsEventClass.countHits("alias:\"(RsEvent)\""), 2)
        assertEquals(rsRiEventClass.countHits("alias:*"), 2)

        //run the script
        rsLookupClass.add(name: "messageGeneratorMaxEventCreateTime", value: 0);
        rsLookupClass.add(name: "messageGeneratorMaxEventClearTime", value: 0);

        CmdbScript.runScript(script, [logger: TestLogUtils.log])
        assertEquals(RsMessage.countHits("alias:*"), 2)
        assertEquals(RsMessage.countHits("eventType:${RsMessage.EVENT_TYPE_CREATE}*"), 2)


        newEvents.each {event ->
            assertEquals(0, RsMessage.countHits("eventId:${event.id}"))
        };
        newRsRiEvents.each {event ->
            assertEquals(1, RsMessage.countHits("eventId:${event.id}"))
        };

        //HISTORICAL EVENT TEST

        //add create messages here because , clear messages wont be added if create messages does not exist
        newEvents.each {event ->
            RsMessage.addEventCreateMessage(event.asMap(), destinationType, destination, new Long(0));
            event.clear();
        };
        newRsRiEvents.each {event ->
            event.clear();
        };
        rsHistoricalEventClass.saveHistoricalEventCache();

        assertEquals(rsHistoricalEventClass.countHits("alias:\"(RsHistoricalEvent)\""), 2)
        assertEquals(rsRiHistoricalEventClass.countHits("alias:*"), 2)


        assertEquals(RsMessage.countHits("alias:*"), 4)
        assertEquals(RsMessage.countHits("eventType:${RsMessage.EVENT_TYPE_CREATE}"), 4)

        CmdbScript.runScript(script, [logger: TestLogUtils.log])

        assertEquals(RsMessage.countHits("alias:*"), 6)
        assertEquals(RsMessage.countHits("eventType:${RsMessage.EVENT_TYPE_CREATE}"), 4)
        assertEquals(RsMessage.countHits("eventType:${RsMessage.EVENT_TYPE_CLEAR}"), 2)

        newEvents.each {event ->
            //create messages added by test
            assertEquals(1, RsMessage.countHits("eventId:${event.id} AND eventType:${RsMessage.EVENT_TYPE_CREATE}"))
            //clear messages should not exist
            assertEquals(0, RsMessage.countHits("eventId:${event.id} AND eventType:${RsMessage.EVENT_TYPE_CLEAR}"))
        };
        newRsRiEvents.each {event ->
            assertEquals(1, RsMessage.countHits("eventId:${event.id} AND eventType:${RsMessage.EVENT_TYPE_CREATE} "))
            assertEquals(1, RsMessage.countHits("eventId:${event.id} AND eventType:${RsMessage.EVENT_TYPE_CLEAR}"))
        };

    }

    void testMessageGeneratorProcessNewEventsAndDoesNotProcessOldEvents()
    {
        def destinationType = "email"
        def destination = "sezgin@gmail.com"
        def user = RsUser.add(username: "sezgin", passwordHash: "sezgin");
        assertFalse(user.hasErrors())
        def imInformation = ChannelUserInformation.add(userId: user.id, type: destinationType, destination: destination, rsUser: user);
        assertFalse(imInformation.hasErrors());

        def adminUser = RsUser.RSADMIN;
        def defaultEventGroup = SearchQueryGroup.add(name: "MyDefault", username: adminUser, isPublic: true, type: "event");

        def searchQuery = SearchQuery.add(group: defaultEventGroup, name: "My All Events", query: "alias:*", sortProperty: "changedAt", sortOrder: "desc", username: adminUser, isPublic: true, type: "event");


        def rule = RsMessageRule.add(users: user.username, searchQueryId: searchQuery.id, destinationType: destinationType, enabled: true, sendClearEventType: true)
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
        assertEquals(4, RsMessage.countHits("eventType:${RsMessage.EVENT_TYPE_CREATE}"))
        RsMessage.searchEvery("eventType:${RsMessage.EVENT_TYPE_CREATE}").each {mes ->
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
        rsHistoricalEventClass.saveHistoricalEventCache();

        assertEquals(rsHistoricalEventClass."countHits"("alias:*"), 8)

        CmdbScript.runScript(script, [:])
        assertEquals(RsMessage.countHits("alias:*"), 8)
        assertEquals(RsMessage.countHits("eventType:${RsMessage.EVENT_TYPE_CLEAR}"), 4)
        RsMessage.searchEvery("eventType:${RsMessage.EVENT_TYPE_CLEAR}").each {mes ->
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
        params.destinationType = "email"
        params.eventType = RsMessage.EVENT_TYPE_CREATE
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
        def destinationType = "email"
        def userRole = Role.add(name: Role.USER);
        def userGroup = Group.addGroup(name: "testusergroup", role: userRole, segmentFilter: "severity:2");
        assertFalse(userGroup.hasErrors());
        def user = RsUser.addUser(username: "testuser", password: "xxx", groups: [userGroup]);
        assertFalse(user.hasErrors());
        assertEquals(user.groups.size(), 1);


        def imInformation = ChannelUserInformation.add(userId: user.id, type: destinationType, destination: "testuser@gmail.com", rsUser: user);
        assertFalse(imInformation.hasErrors());

        def defaultEventGroup = SearchQueryGroup.add(name: "MyDefault", username: user.username, isPublic: false, type: "event");
        assertFalse(defaultEventGroup.hasErrors())
        def searchQuery = SearchQuery.add(group: defaultEventGroup, name: "My All Events", query: "alias:*", sortProperty: "changedAt", sortOrder: "desc", username: user.username, isPublic: false, type: "event");
        assertFalse(searchQuery.hasErrors())

        def rule = RsMessageRule.add(users: user.username, searchQueryId: searchQuery.id, destinationType: destinationType, enabled: true, sendClearEventType: true)
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
        assertEquals(RsMessage.countHits("eventType:${RsMessage.EVENT_TYPE_CREATE}"), 1)

        newEvents.each {
            it.clear();
        }
        rsHistoricalEventClass.saveHistoricalEventCache();

        assertEquals(rsHistoricalEventClass."countHits"("alias:*"), 4)

        CmdbScript.runScript(script, [:])
        assertEquals(RsMessage.countHits("alias:*"), 2)
        assertEquals(RsMessage.countHits("eventType:${RsMessage.EVENT_TYPE_CLEAR}"), 1)

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
        assertEquals(RsMessage.countHits("eventType:${RsMessage.EVENT_TYPE_CREATE}"), 2)

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
        assertEquals(RsMessage.countHits("eventType:${RsMessage.EVENT_TYPE_CREATE}"), 4)
    }

    public void testDefaultDestinationType() {
        RsMessageRuleOperations.setConfiguredDestinationNames(["email","jabber"]);

        def user1 = RsUser.add(username: "user1", passwordHash: "user1")
        ChannelUserInformation.add(userId: user1.id, type: "email", destination: "dest1", rsUser: user1);
        ChannelUserInformation.add(userId: user1.id, type: "jabber", destination: "dest2", rsUser: user1, isDefault: true);



        def defaultEventGroup = SearchQueryGroup.add(name: "MyDefault", username: user1.username, type: "event");
        assertFalse(defaultEventGroup.hasErrors())
        def searchQuery = SearchQuery.add(group: defaultEventGroup, name: "My All Events", query: "alias:*", sortProperty: "changedAt", sortOrder: "desc", username: user1.username, type: "event");
        assertFalse(searchQuery.hasErrors())

        def rule = RsMessageRule.add(users: user1.username, searchQueryId: searchQuery.id, destinationType: RsMessageRule.DEFAULT_DESTINATION, enabled: true)
        assertFalse(rule.hasErrors())

        copyScript(scriptName)
        def script = CmdbScript.addScript(name: scriptName, type: CmdbScript.ONDEMAND, logLevel: Level.DEBUG)
        assertFalse(script.hasErrors())

        CmdbScript.runScript(script, [:])
        assertEquals(RsMessage.countHits("alias:*"), 0)

        def newEvents = addEvents("testevents", 1)
        assertEquals(rsEventClass."countHits"("alias:*"), 1)

        CmdbScript.runScript(script, [:])

        assertEquals(1, RsMessage.countHits("alias:*"))
        def rsMessage = RsMessage.list()[0];
        assertEquals("dest2", rsMessage.destination)
        assertEquals("jabber", rsMessage.destinationType)
    }

    public void testPublicRuleWithDefaultDestinationType() {
        RsMessageRuleOperations.setConfiguredDestinationNames(["email","jabber"]);

        def adminGroup = createGroupWithRole("rsadmin", Role.ADMINISTRATOR)
        def rsAdmin = RsUser.add(username: RsUser.RSADMIN, passwordHash: "rsadmin", groups: [adminGroup])
        def group1 = createGroupWithRole("group1", Role.USER)
        def user1 = RsUser.add(username: "user1", passwordHash: "user1", groups: [group1])

        ChannelUserInformation.add(userId: user1.id, type: "email", destination: "dest1", rsUser: user1);
        ChannelUserInformation.add(userId: user1.id, type: "jabber", destination: "dest2", rsUser: user1,isDefault: true);

        def defaultEventGroup = SearchQueryGroup.add(name: "MyDefault", username: rsAdmin.username, type: "event", isPublic: true);
        assertFalse(defaultEventGroup.hasErrors())
        def searchQuery = SearchQuery.add(group: defaultEventGroup, name: "My All Events", query: "alias:*", sortProperty: "changedAt", sortOrder: "desc", username: rsAdmin.username, type: "event", isPublic: true);
        assertFalse(searchQuery.hasErrors())

        def rule = RsMessageRule.add(users:user1.username, searchQueryId: searchQuery.id, destinationType: RsMessageRule.DEFAULT_DESTINATION, enabled: true, groups: "group1", ruleType: "public")
        assertFalse("${rule.errors}",rule.hasErrors())

        copyScript(scriptName)
        def script = CmdbScript.addScript(name: scriptName, type: CmdbScript.ONDEMAND, logLevel: Level.DEBUG)
        assertFalse(script.hasErrors())

        CmdbScript.runScript(script, [:])
        assertEquals(RsMessage.countHits("alias:*"), 0)

        def newEvents = addEvents("testevents", 1)
        assertEquals(rsEventClass."countHits"("alias:*"), 1)

        CmdbScript.runScript(script, [:])

        assertEquals(1, RsMessage.countHits("alias:*"))
        def rsMessage = RsMessage.list()[0];
        assertEquals("dest2", rsMessage.destination)
        assertEquals("jabber", rsMessage.destinationType)
    }

    public void testProcessingPublicRules() {
        def destinationType = "email"
        def adminGroup = createGroupWithRole("rsadmin", Role.ADMINISTRATOR)
        def rsAdmin = RsUser.add(username: RsUser.RSADMIN, passwordHash: "rsadmin", groups: [adminGroup])
        def group1 = createGroupWithRole("group1", Role.USER)
        def group2 = createGroupWithRole("group2", Role.USER)
        def group3 = createGroupWithRole("group3", Role.USER)

        def user1 = RsUser.add(username: "user1", passwordHash: "user1", groups: [group1])
        def user2 = RsUser.add(username: "user2", passwordHash: "user2", groups: [group1])
        def user3 = RsUser.add(username: "user3", passwordHash: "user3", groups: [group2])
        def user4 = RsUser.add(username: "user4", passwordHash: "user4", groups: [group3])

        ChannelUserInformation.add(userId: user1.id, type: destinationType, destination: "dest1", rsUser: user1);
        ChannelUserInformation.add(userId: user2.id, type: destinationType, destination: "dest2", rsUser: user2);
        ChannelUserInformation.add(userId: user3.id, type: destinationType, destination: "dest3", rsUser: user3);
        ChannelUserInformation.add(userId: user4.id, type: destinationType, destination: "dest4", rsUser: user4);

        def defaultEventGroup = SearchQueryGroup.add(name: "MyDefault", username: RsUser.RSADMIN, isPublic: true, type: "event");
        assertFalse(defaultEventGroup.hasErrors())
        def searchQuery = SearchQuery.add(group: defaultEventGroup, name: "My All Events", query: "alias:*", sortProperty: "changedAt", sortOrder: "desc", username: RsUser.RSADMIN, isPublic: true, type: "event");
        assertFalse(searchQuery.hasErrors())

        def rule = RsMessageRule.add(users: rsAdmin.username, searchQueryId: searchQuery.id, destinationType: destinationType, enabled: true, sendClearEventType: true, groups: "group1,group2", users: "user1,user4", ruleType: "public")
        assertFalse(rule.hasErrors())

        copyScript(scriptName)
        def script = CmdbScript.addScript(name: scriptName, type: CmdbScript.ONDEMAND, logLevel: Level.DEBUG)
        assertFalse(script.hasErrors())

        CmdbScript.runScript(script, [:])
        assertEquals(0, RsMessage.countHits("alias:*"))

        def newEvents = addEvents("testevents", 1)
        assertEquals(rsEventClass."countHits"("alias:*"), 1)

        CmdbScript.runScript(script, [:])

        assertEquals(4, RsMessage.countHits("alias:*"))
        RsMessage.searchEvery("alias:*", [sort: "destination", order: "asc"]).eachWithIndex {RsMessage message, i ->
            assertEquals(destinationType, message.destinationType)
            assertEquals(RsMessage.EVENT_TYPE_CREATE, message.eventType)
            assertEquals("dest${i + 1}", message.destination)
        }

        newEvents.each {
            it.clear();
        }
        rsHistoricalEventClass.saveHistoricalEventCache();

        assertEquals(rsHistoricalEventClass."countHits"("alias:*"), 1)

        CmdbScript.runScript(script, [:])

        assertEquals(4, RsMessage.countHits("eventType:${RsMessage.EVENT_TYPE_CLEAR}"))
        RsMessage.searchEvery("eventType:${RsMessage.EVENT_TYPE_CLEAR}", [sort: "destination", order: "asc"]).eachWithIndex {RsMessage message, i ->
            assertEquals(destinationType, message.destinationType)
            assertEquals("dest${i + 1}", message.destination)
        }
    }

    public void testRuleWithCalendar() {
        def destinationType = "email"
        def user1 = RsUser.add(username: "user1", passwordHash: "user1")
        ChannelUserInformation.add(userId: user1.id, type: destinationType, destination: "dest1", rsUser: user1);

        def defaultEventGroup = SearchQueryGroup.add(name: "MyDefault", username: RsUser.RSADMIN, isPublic: true, type: "event");
        assertFalse(defaultEventGroup.hasErrors())
        def searchQuery = SearchQuery.add(group: defaultEventGroup, name: "My All Events", query: "alias:*", sortProperty: "changedAt", sortOrder: "desc", username: RsUser.RSADMIN, isPublic: true, type: "event");
        assertFalse(searchQuery.hasErrors())

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date(0))
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        def starting = calendar.getTime().getTime();
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        def ending = calendar.getTime().getTime();
        calendar.setTime(new Date(System.currentTimeMillis()))
        def currentDay = calendar.get(Calendar.DAY_OF_WEEK)
        def days = "${(currentDay % 7) + 1}"

        def cal = RsMessageRuleCalendar.add(name: "cal", starting: starting, ending: ending, days: days, username: user1.username, daysString: "Mon");
        assertFalse(cal.hasErrors())
        def rule = RsMessageRule.add(users: user1.username, searchQueryId: searchQuery.id, destinationType: destinationType, enabled: true, calendarId: cal.id)
        assertFalse(rule.hasErrors())

        copyScript(scriptName)
        def script = CmdbScript.addScript(name: scriptName, type: CmdbScript.ONDEMAND, logLevel: Level.DEBUG)
        assertFalse(script.hasErrors())

        CmdbScript.runScript(script, [:])
        assertEquals(0, RsMessage.countHits("alias:*"))

        def newEvents = addEvents("testevents", 1)
        assertEquals(rsEventClass."countHits"("alias:*"), 1)

        CmdbScript.runScript(script, [:])
        assertEquals(0, RsMessage.countHits("alias:*"))

        cal.update(days: "${cal.days},${currentDay}");
        newEvents = addEvents("newevents", 1)
        assertEquals(rsEventClass."countHits"("alias:*"), 2)

        CmdbScript.runScript(script, [:])
        assertEquals(1, RsMessage.countHits("alias:*"))
        RsMessage message = RsMessage.list()[0]
        assertEquals("dest1", message.destination)
        assertEquals(newEvents[0].id, message.eventId)
    }

    public void testCalendarWithExceptionDays(){
        def destinationType = "email"
        def user1 = RsUser.add(username: "user1", passwordHash: "user1")
        ChannelUserInformation.add(userId: user1.id, type: destinationType, destination: "dest1", rsUser: user1);

        def defaultEventGroup = SearchQueryGroup.add(name: "MyDefault", username: RsUser.RSADMIN, isPublic: true, type: "event");
        assertFalse(defaultEventGroup.hasErrors())
        def searchQuery = SearchQuery.add(group: defaultEventGroup, name: "My All Events", query: "alias:*", sortProperty: "changedAt", sortOrder: "desc", username: RsUser.RSADMIN, isPublic: true, type: "event");
        assertFalse(searchQuery.hasErrors())

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date(System.currentTimeMillis()))
        def currentDay = calendar.get(Calendar.DAY_OF_WEEK)

        SimpleDateFormat format = new SimpleDateFormat(RsMessageRuleCalendar.EXCEPTION_DATE_FORMAT);
        def exceptions = "12/12/2010,${format.format(calendar.getTime())}"

        calendar.setTime(new Date(0))
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        def starting = calendar.getTime().getTime();
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        def ending = calendar.getTime().getTime();


        def cal = RsMessageRuleCalendar.add(name: "cal", starting: starting, ending: ending, days: "${currentDay}", username: user1.username, daysString: "Mon", exceptions:exceptions);
        assertFalse(cal.hasErrors())
        def rule = RsMessageRule.add(users: user1.username, searchQueryId: searchQuery.id, destinationType: destinationType, enabled: true, calendarId: cal.id)
        assertFalse(rule.hasErrors())

        copyScript(scriptName)
        def script = CmdbScript.addScript(name: scriptName, type: CmdbScript.ONDEMAND, logLevel: Level.DEBUG)
        assertFalse(script.hasErrors())

        CmdbScript.runScript(script, [:])
        assertEquals(0, RsMessage.countHits("alias:*"))

        def newEvents = addEvents("testevents", 1)
        assertEquals(rsEventClass."countHits"("alias:*"), 1)

        CmdbScript.runScript(script, [:])
        assertEquals(0, RsMessage.countHits("alias:*"))

        cal.update(exceptions:"");

        newEvents = addEvents("newevents", 1)
        assertEquals(rsEventClass."countHits"("alias:*"), 2)

        CmdbScript.runScript(script, [:])
        assertEquals(1, RsMessage.countHits("alias:*"))
        RsMessage message = RsMessage.list()[0]
        assertEquals("dest1", message.destination)
        assertEquals(newEvents[0].id, message.eventId)
    }

    public void testIfCalendarsStartingEndingTimeIntervalDoesNotMatchMessagesAreNotCreated() {
        def destinationType = "email"
        def user1 = RsUser.add(username: "user1", passwordHash: "user1")
        ChannelUserInformation.add(userId: user1.id, type: destinationType, destination: "dest1", rsUser: user1);

        def defaultEventGroup = SearchQueryGroup.add(name: "MyDefault", username: RsUser.RSADMIN, isPublic: true, type: "event");
        assertFalse(defaultEventGroup.hasErrors())
        def searchQuery = SearchQuery.add(group: defaultEventGroup, name: "My All Events", query: "alias:*", sortProperty: "changedAt", sortOrder: "desc", username: RsUser.RSADMIN, isPublic: true, type: "event");
        assertFalse(searchQuery.hasErrors())

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date(System.currentTimeMillis()))
        def currentDay = calendar.get(Calendar.DAY_OF_WEEK)
        
        calendar.setTime(new Date(0))
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        def starting = calendar.getTime().getTime();
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 1)
        def ending = calendar.getTime().getTime();


        def cal = RsMessageRuleCalendar.add(name: "cal", starting: starting, ending: ending, days: "${currentDay}", username: user1.username, daysString: "Mon");
        assertFalse(cal.hasErrors())
        def rule = RsMessageRule.add(users: user1.username, searchQueryId: searchQuery.id, destinationType: destinationType, enabled: true, calendarId: cal.id)
        assertFalse(rule.hasErrors())

        copyScript(scriptName)
        def script = CmdbScript.addScript(name: scriptName, type: CmdbScript.ONDEMAND, logLevel: Level.DEBUG)
        assertFalse(script.hasErrors())

        CmdbScript.runScript(script, [:])
        assertEquals(0, RsMessage.countHits("alias:*"))

        def newEvents = addEvents("testevents", 1)
        assertEquals(rsEventClass."countHits"("alias:*"), 1)

        CmdbScript.runScript(script, [:])
        assertEquals(0, RsMessage.countHits("alias:*"))

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        ending = calendar.getTime().getTime();
        cal.update(ending:ending);

        newEvents = addEvents("newevents", 1)
        assertEquals(rsEventClass."countHits"("alias:*"), 2)

        CmdbScript.runScript(script, [:])
        assertEquals(1, RsMessage.countHits("alias:*"))
        RsMessage message = RsMessage.list()[0]
        assertEquals("dest1", message.destination)
        assertEquals(newEvents[0].id, message.eventId)
    }

    def addEvents(prefix, count, eventClass = rsEventClass)
    {
        def events = []
        count.times {
            def event = eventClass."add"(name: "${prefix}${it}", severity: it)
            assertFalse(event.hasErrors())
            events.add(event)
        }
        return events;
    }
    def addHistoricalEvents(prefix, count, eventClass = rsHistoricalEventClass)
    {
        def events = []
        count.times {
            def event = eventClass."add"(name: "${prefix}${it}", severity: it, activeId: it)
            assertFalse(event.hasErrors())
            events.add(event)
        }
        return events;
    }

    private def createGroupWithRole(groupName, roleName)
    {
        def role = Role.add(name: roleName);
        assertFalse(role.hasErrors());
        def group = Group.add(name: groupName, role: role);
        assertFalse(group.hasErrors());

        return group;
    }
}