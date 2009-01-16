package message

import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import search.SearchQuery
import search.SearchQueryGroup
import script.CmdbScript
import auth.RsUser
import auth.Group
import auth.Role


/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 12, 2009
* Time: 3:27:08 PM
* To change this template use File | Settings | File Templates.
*/
class EmailGeneratorScriptIntegrationTests extends RapidCmdbIntegrationTestCase {
    static transactional = false;
    def classes=[:]
    def destination="abdurrahim"
    void setUp() throws Exception {
        super.setUp();
        loadClasses(["RsEvent","RsHistoricalEvent","RsLookup"])
        clearAll();
        def generatorScript=CmdbScript.addScript([name:"emailGenerator"])
        assertFalse(generatorScript.hasErrors())

        
    }

    void tearDown() throws Exception {
        super.tearDown();
    }
    
    void clearAll()
    {

        classes.RsEvent.removeAll();
        classes.RsHistoricalEvent.removeAll();
        RsMessage.removeAll();
        RsMessageRule.removeAll();
        classes.RsLookup.removeAll();
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
    void loadClasses(classList)
    {
        classList.each{
            def loadedClass=this.class.classLoader.loadClass(it)
            classes[loadedClass.getSimpleName()]=loadedClass
        }
    }
    void testEmailGeneratorDoesNotProcessIfUserDoesNotHaveEmail()
    {
        def user=RsUser.get(username:"rsadmin");
        user.update(email:null)
        assertFalse(user.hasErrors())

        def adminUser = RsUser.RSADMIN;
        def defaultEventGroup = SearchQueryGroup.add(name: "MyDefault", username:adminUser, isPublic:true, type:"event");

        def searchQuery=SearchQuery.add(group: defaultEventGroup, name: "My All Events", query: "alias:*", sortProperty: "changedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"event");


        def rule=RsMessageRule.add(userId:user.id,searchQueryId:searchQuery.id,destinationType:RsMessage.EMAIL,enabled:true,clearAction:true)
        assertFalse(rule.hasErrors())
        assertEquals(RsMessageRule.countHits("alias:*"),1)

        def script=CmdbScript.get(name:"emailGenerator")        
        CmdbScript.updateScript(script,[logLevel:org.apache.log4j.Level.DEBUG],false)
        assertFalse (script.hasErrors())

        CmdbScript.runScript(script,[:])
        assertEquals(RsMessage.countHits("alias:*"),0)
        
        
        def newEvents=addEvents("newevents",4)
        assertEquals(classes.RsEvent.countHits("alias:*"),4)

        CmdbScript.runScript(script,[:])
        assertEquals(RsMessage.countHits("alias:*"),0)
    }
    void testEmailGeneratorDoesNotProcessDisabledRules()
    {
        def user=RsUser.get(username:"rsadmin");
        user.update(email:destination)
        assertFalse(user.hasErrors())

        def adminUser = RsUser.RSADMIN;
        def defaultEventGroup = SearchQueryGroup.add(name: "MyDefault", username:adminUser, isPublic:true, type:"event");

        def searchQuery=SearchQuery.add(group: defaultEventGroup, name: "My All Events", query: "alias:*", sortProperty: "changedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"event");

        def rule=RsMessageRule.add(userId:user.id,searchQueryId:searchQuery.id,destinationType:RsMessage.EMAIL,enabled:false,clearAction:true)
        assertFalse(rule.hasErrors())
        assertEquals(RsMessageRule.countHits("alias:*"),1)

        def script=CmdbScript.get(name:"emailGenerator")
        CmdbScript.updateScript(script,[logLevel:org.apache.log4j.Level.DEBUG],false)
        assertFalse (script.hasErrors())

        CmdbScript.runScript(script,[:])
        assertEquals(RsMessage.countHits("alias:*"),0)


        addEvents("newevents",4)
        assertEquals(classes.RsEvent.countHits("alias:*"),4)

        CmdbScript.runScript(script,[:])
        assertEquals(RsMessage.countHits("alias:*"),0)
    }
    void testEmailGeneratorDoesNotProcessClearEventsForClearDisabledRules()
    {
        def user=RsUser.get(username:"rsadmin");
        user.update(email:destination)
        assertFalse(user.hasErrors())

        def adminUser = RsUser.RSADMIN;
        def defaultEventGroup = SearchQueryGroup.add(name: "MyDefault", username:adminUser, isPublic:true, type:"event");

        def searchQuery=SearchQuery.add(group: defaultEventGroup, name: "My All Events", query: "alias:*", sortProperty: "changedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"event");

        def rule=RsMessageRule.add(userId:user.id,searchQueryId:searchQuery.id,destinationType:RsMessage.EMAIL,enabled:true,clearAction:false)
        assertFalse(rule.hasErrors())
        assertEquals(RsMessageRule.countHits("alias:*"),1)

        def script=CmdbScript.get(name:"emailGenerator")
        CmdbScript.updateScript(script,[logLevel:org.apache.log4j.Level.DEBUG],false)
        assertFalse (script.hasErrors())

        CmdbScript.runScript(script,[:])
        assertEquals(RsMessage.countHits("alias:*"),0)


        def newEvents=addEvents("newevents",4)
        assertEquals(classes.RsEvent.countHits("alias:*"),4)

        CmdbScript.runScript(script,[:])
        assertEquals(RsMessage.countHits("alias:*"),4)

        newEvents.each{
            it.clear();
        }
        assertEquals(classes.RsHistoricalEvent.countHits("alias:*"),4)
        CmdbScript.runScript(script,[:])
        assertEquals(RsMessage.countHits("alias:*"),4)
    }
    void testEmailGeneratorProcessNewEventsAndDoesNotProcessOldEvents()
    {
        def user=RsUser.get(username:"rsadmin");
        user.update(email:destination)
        assertFalse(user.hasErrors())
       

        def adminUser = RsUser.RSADMIN;
        def defaultEventGroup = SearchQueryGroup.add(name: "MyDefault", username:adminUser, isPublic:true, type:"event");

        def searchQuery=SearchQuery.add(group: defaultEventGroup, name: "My All Events", query: "alias:*", sortProperty: "changedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"event");


        def rule=RsMessageRule.add(userId:user.id,searchQueryId:searchQuery.id,destinationType:RsMessage.EMAIL,enabled:true,clearAction:true)
        assertFalse(rule.hasErrors())
        assertEquals(RsMessageRule.countHits("alias:*"),1)

        def script=CmdbScript.get(name:"emailGenerator")
        CmdbScript.updateScript(script,[logLevel:org.apache.log4j.Level.DEBUG],false)
        assertFalse (script.hasErrors())

        // add old events
        addEvents("oldevents",4)
        assertEquals(classes.RsEvent.countHits("alias:*"),4)


        //add old historical events
        addHistoricalEvents("oldclearevents",4)
        assertEquals(classes.RsHistoricalEvent.countHits("alias:*"),4)

        def maxEventId=0
        def maxEvent=classes.RsEvent.search("alias:*",[max:1,sort:"id",order:"desc"]).results[0]
        if(maxEvent!=null)
        {
            maxEventId=Long.valueOf(maxEvent.id)+1
        }
        
        def maxEventClearId=0
        def maxClearEvent=classes.RsHistoricalEvent.search("alias:*",[max:1,sort:"id",order:"desc"]).results[0]
        if(maxClearEvent!=null)
        {
            maxEventClearId=Long.valueOf(maxClearEvent.id)+1
        }



        //run the script
        CmdbScript.runScript(script,[:])
        assertEquals(RsMessage.countHits("alias:*"),0)

        //add new events
        def newEvents=addEvents("newevents",4)
        assertEquals(classes.RsEvent.countHits("alias:*"),8)

        //run the script and check that only new events are processed
        CmdbScript.runScript(script,[:])
        assertEquals(RsMessage.countHits("alias:*"),4)
        assertEquals(RsMessage.countHits("action:create"),4)
        RsMessage.searchEvery("action:create").each{ mes ->
            assertEquals(mes.destination,destination)
            def event=classes.RsEvent.get(id:mes.eventId)
            assertNotNull(event)            
            assertTrue(event.id>maxEventId)
        }
        
        //now clear the events 
        newEvents.each{
            it.clear();
        }
        assertEquals(classes.RsHistoricalEvent.countHits("alias:*"),8)

        CmdbScript.runScript(script,[:])
        assertEquals(RsMessage.countHits("alias:*"),8)
        assertEquals(RsMessage.countHits("action:clear"),4)
        RsMessage.searchEvery("action:clear").each{ mes ->
            assertEquals(mes.destination,destination)
            def event=classes.RsHistoricalEvent.search("activeId:${mes.eventId}").results[0]
            assertNotNull(event)
            assertTrue(event.id>maxEventClearId)
        }

    }
    void testEmailGeneratorProcessDelayedEmails()
    {
        assertEquals(RsMessage.list().size(),0)
        def date=new Date();
        def delay=5000
        def params=[:]
        params.eventId=1
        params.state=0
        params.destination="xxx"
        params.destinationType=RsMessage.EMAIL
        params.action="create"
        params.sendAfter=date.getTime()+delay


        def script=CmdbScript.get(name:"emailGenerator")
        CmdbScript.updateScript(script,[logLevel:org.apache.log4j.Level.DEBUG],false)
        assertFalse (script.hasErrors())
        
        def message=RsMessage.add(params)
        assertFalse(message.hasErrors())        
        

        CmdbScript.runScript(script,[:])
        def mes=RsMessage.get(id:message.id)
        assertEquals(mes.state,0)
        Thread.sleep(delay+1000)

        CmdbScript.runScript(script,[:])
        mes=RsMessage.get(id:message.id)
        assertEquals(mes.state,1)
    }
    void testEmailGeneratorUsesWithSessionToApplySegmentation()
    {
        def userRole = Role.get(name: Role.USER);
        assertNotNull(userRole);
        def userGroup = Group.add(name: "testusergroup", role: userRole,segmentFilter:"severity:2");
        assertFalse(userGroup.hasErrors());
        def user=RsUser.add(username: "testuser", passwordHash: "xxx");
        assertFalse(user.hasErrors());

        user.addRelation(groups:userGroup);
        assertFalse(user.hasErrors());        
        assertEquals(user.groups.size(),1);



        
        user.update(email:destination)
        assertFalse(user.hasErrors())


        
        def defaultEventGroup = SearchQueryGroup.add(name: "MyDefault", username:user.username, isPublic:false, type:"event");
        assertFalse(defaultEventGroup.hasErrors())
        def searchQuery=SearchQuery.add(group: defaultEventGroup, name: "My All Events", query: "alias:*", sortProperty: "changedAt", sortOrder: "desc", username:user.username, isPublic:false, type:"event");
        assertFalse(searchQuery.hasErrors())

        def rule=RsMessageRule.add(userId:user.id,searchQueryId:searchQuery.id,destinationType:RsMessage.EMAIL,enabled:true,clearAction:true)
        assertFalse(rule.hasErrors())
        assertEquals(RsMessageRule.countHits("alias:*"),1)

        def script=CmdbScript.get(name:"emailGenerator")
        CmdbScript.updateScript(script,[logLevel:org.apache.log4j.Level.DEBUG],false)
        assertFalse (script.hasErrors())


        CmdbScript.runScript(script,[:])
        assertEquals(RsMessage.countHits("alias:*"),0)

        def newEvents=addEvents("testevents",4)
        assertEquals(classes.RsEvent.countHits("alias:*"),4)

        CmdbScript.runScript(script,[:])
        assertEquals(RsMessage.countHits("alias:*"),1)
        assertEquals(RsMessage.countHits("action:create"),1)

        newEvents.each{
            it.clear();
        }
        assertEquals(classes.RsHistoricalEvent.countHits("alias:*"),4)

        CmdbScript.runScript(script,[:])
        assertEquals(RsMessage.countHits("alias:*"),2)
        assertEquals(RsMessage.countHits("action:clear"),1)

        //now we change the segment filter and test again
        classes.RsEvent.removeAll();
        classes.RsHistoricalEvent.removeAll();
        RsMessage.removeAll();
        

        userGroup.update(segmentFilter:"severity:[2 TO 3]")
        assertFalse(userGroup.hasErrors())
        
        def newEvents2=addEvents("testevents2",4)
        assertEquals(classes.RsEvent.countHits("alias:*"),4)
        
        CmdbScript.runScript(script,[:])
        assertEquals(RsMessage.countHits("alias:*"),2)
        assertEquals(RsMessage.countHits("action:create"),2)

        //now we remove the segment filter and test again
        classes.RsEvent.removeAll();
        classes.RsHistoricalEvent.removeAll();
        RsMessage.removeAll();


        userGroup.update(segmentFilter:"")
        assertFalse(userGroup.hasErrors())
        
        def newEvents3=addEvents("testevents2",4)
        assertEquals(classes.RsEvent.countHits("alias:*"),4)

        CmdbScript.runScript(script,[:])
        assertEquals(RsMessage.countHits("alias:*"),4)
        assertEquals(RsMessage.countHits("action:create"),4)
    }
}