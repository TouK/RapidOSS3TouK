package message

import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase
import search.SearchQuery
import script.CmdbScript
import auth.RsUser


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
    void testEmailGeneratorDoesNotProcessOldEvents()
    {
        def user=RsUser.get(username:"rsadmin");
        user.update(email:destination)

        
        assertEquals(user.email,destination)

        def testUser=RsUser.get(username:"rsadmin")
        assertEquals(testUser.email,destination)
        
        def searchQuery=SearchQuery.searchEvery("name:All Events")[0]

        def rule=RsMessageRule.add(userId:user.id,searchQueryId:searchQuery.id,destinationType:RsMessage.EMAIL,enabled:true,clearAction:true)
        assertFalse(rule.hasErrors())
        assertEquals(RsMessageRule.countHits("alias:*"),1)

        def script=CmdbScript.get(name:"emailGenerator")
        CmdbScript.updateScript(script,[logLevel:org.apache.log4j.Level.DEBUG],false)
        assertNotNull (script)

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
}