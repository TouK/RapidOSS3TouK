package scriptTests

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.scripting.ScriptManagerForTest

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 10, 2010
 * Time: 5:19:27 PM
 * To change this template use File | Settings | File Templates.
 */
class ClearHistoricalEventsScriptTest extends RapidCmdbWithCompassTestCase {
    def RsHistoricalEvent;
    def RsEventJournal;
    Long dayDiff=24l*3600l*1000l;

    public void setUp() {
        super.setUp();

        ["RsHistoricalEvent","RsEventJournal"].each{ className ->
            setProperty(className,gcl.loadClass(className));
        }

        initialize([RsHistoricalEvent,RsEventJournal], []);
        initializeScriptManager();
    }

    public void tearDown() {
        super.tearDown();
    }
    void initializeScriptManager()
    {
        def base_directory = getWorkspacePath()+"/RapidModules/RapidInsight/scripts"
        println "base path is :"+new File(base_directory).getCanonicalPath();
        ScriptManagerForTest.initialize(gcl,base_directory);
        ScriptManagerForTest.addScript('clearHistoricalEvents');

    }
    public void testClearHistoricalEventsRemovesOlderThan30Days()
    {
         RsHistoricalEvent.add(name:"ev1",activeId:1,clearedAt:Date.now()-(35l*dayDiff));
         RsHistoricalEvent.add(name:"ev2",activeId:2,clearedAt:Date.now()-(31l*dayDiff));
         RsHistoricalEvent.add(name:"ev3",activeId:3,clearedAt:Date.now()-(29l*dayDiff));
         RsHistoricalEvent.add(name:"ev4",activeId:4,clearedAt:Date.now());

         RsHistoricalEvent.list().each{ event ->
             RsEventJournal.add(eventId:event.activeId,eventName:event.name);
         }

         assertEquals(4,RsHistoricalEvent.countHits("alias:*"));
         assertEquals(4,RsEventJournal.countHits("alias:*"));

         ScriptManagerForTest.runScript("clearHistoricalEvents",[:]);

         assertEquals(2,RsHistoricalEvent.countHits("alias:*"));
         assertEquals(1,RsHistoricalEvent.countHits("name:ev3"));
         assertEquals(1,RsHistoricalEvent.countHits("name:ev4"));

         assertEquals(2,RsEventJournal.countHits("alias:*"));
         assertEquals(1,RsEventJournal.countHits("eventName:ev3"));
         assertEquals(1,RsEventJournal.countHits("eventName:ev4"));
    }

}