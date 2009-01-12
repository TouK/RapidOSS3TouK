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
    void addEvents()
    {
        classes.RsEvent.add(name:"ev1",severity:1)
        classes.RsEvent.add(name:"ev2",severity:2)
        classes.RsEvent.add(name:"ev3",severity:3)
        classes.RsEvent.add(name:"ev4",severity:4)
    }
    void loadClasses(classList)
    {
        classList.each{
            def loadedClass=this.class.classLoader.loadClass(it)
            classes[loadedClass.getSimpleName()]=loadedClass
        }
    }
    void testDummy()
    {
        def user=RsUser.get(username:"rsadmin");
        user.update(email:destination)

        
        assertEquals(user.email,destination)

        def testUser=RsUser.get(username:"rsadmin")
        assertEquals(testUser.email,destination)
        
        def searchQuery=SearchQuery.searchEvery("name:All Events")[0]

        def rule=RsMessageRule.add(userId:user.id,searchQueryId:searchQuery.id,destinationType:RsMessage.EMAIL,enabled:true)
        assertFalse(rule.hasErrors())
        assertEquals(RsMessageRule.countHits("alias:*"),1)

        def script=CmdbScript.get(name:"emailGenerator")
        CmdbScript.updateScript(script,[logLevel:org.apache.log4j.Level.DEBUG],false)
        assertNotNull (script)



        CmdbScript.runScript(script,[:])
        assertEquals(RsMessage.countHits("alias:*"),0)
        
        addEvents()
        assertEquals(classes.RsEvent.countHits("alias:*"),4)

        CmdbScript.runScript(script,[:])
        assertEquals(RsMessage.countHits("alias:*"),4)
        RsMessage.list().each{ mes ->
            assertEquals(mes.destination,destination)
            assertNotNull(classes.RsEvent.get(id:mes.eventId))
        }
        
        
    }
}