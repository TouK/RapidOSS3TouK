import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.util.ExecutionContextManagerUtils
import auth.*;

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: May 5, 2009
* Time: 11:36:57 AM
* To change this template use File | Settings | File Templates.
*/
class StatisticsOperationsTest extends RapidCmdbWithCompassTestCase{
   public void setUp() {
        super.setUp();
        initialize([Statistics,InstrumentationParameters,RsUser], []);
        CompassForTests.addOperationSupport(Statistics,StatisticsOperations);
        CompassForTests.addOperationSupport(RsUser,RsUserOperations);
        Statistics.enableGlobally();
    }

    public void tearDown() {
       super.tearDown();
    }

    public void testRecordState()
    {
        def insParam=InstrumentationParameters.add(name:"testStats",enabled:true);
        assertFalse(insParam.hasErrors());

        def duration=500;

        assertEquals(0,Statistics.countHits("alias:*"));
        Statistics.record("testStats",duration);

        assertEquals(1,Statistics.countHits("parameter:${insParam.name}"));
        Statistics stat1=Statistics.searchEvery("parameter:${insParam.name}",[sort:"id",order:"asc"])[0];

        Thread.sleep(200);
        assertTrue(stat1.timestamp<Date.now());
        assertFalse(stat1.timestamp<Date.now()-400)

        assertEquals("system",stat1.user);
        assertEquals("",stat1.description);
        assertEquals(duration.toString(),stat1.value);

        //add again
        Statistics.record("testStats",duration);
        assertEquals(2,Statistics.countHits("parameter:${insParam.name}"));
        Statistics stat2=Statistics.searchEvery("parameter:${insParam.name}",[sort:"id",order:"desc"])[0];
        assertEquals("system",stat2.user);
        assertEquals(duration.toString(),stat2.value);
        assertTrue(stat1.timestamp<stat2.timestamp);
        assertTrue(stat1.id<stat2.id);
        
        //now test with username
        Statistics stat3;
        String currentUserNameToBeAddedtoContext = "testuser";
        ExecutionContextManagerUtils.executeInContext ([:])
        {
            ExecutionContextManagerUtils.addUsernameToCurrentContext (currentUserNameToBeAddedtoContext);
            Statistics.record("testStats",duration);
            stat3=Statistics.searchEvery("parameter:${insParam.name}",[sort:"id",order:"desc"])[0];
            assertEquals(currentUserNameToBeAddedtoContext,stat3.user);
            assertEquals(duration.toString(),stat3.value);
            assertTrue(stat2.timestamp<stat3.timestamp);
            assertTrue(stat2.id<stat3.id);
        }
    }

    public void testRecordWithDescription()
    {
        def insParam=InstrumentationParameters.add(name:"testStats",enabled:true);
        assertFalse(insParam.hasErrors());

        def duration=500;
        def description = "a description";
        assertEquals(0,Statistics.countHits("alias:*"));
        Statistics.record("testStats",duration, description);

        assertEquals(1,Statistics.countHits("parameter:${insParam.name}"));
        Statistics stat1=Statistics.searchEvery("parameter:${insParam.name}",[sort:"id",order:"asc"])[0];

        
        assertEquals("system",stat1.user);
        assertEquals(description,stat1.description);
        assertEquals(duration.toString(),stat1.value);

    }

    public void testRecordStateDoesNotRecordIfInstrumentationParameterIsDisabled()
    {
        def insParam=InstrumentationParameters.add(name:"testStats",enabled:true);
        assertFalse(insParam.hasErrors());

        def duration=500;

        assertEquals(0,Statistics.countHits("alias:*"));

        //first test successfull add
        Statistics.record("testStats",duration);
        assertEquals(1,Statistics.countHits("parameter:${insParam.name}"));

        //disable  instrumentation
        insParam.update(enabled:false);
        assertFalse(insParam.hasErrors());

        Statistics.record("testStats",duration);
        assertEquals(1,Statistics.countHits("parameter:${insParam.name}"));

        //enable back and test
        insParam.update(enabled:true);
        assertFalse(insParam.hasErrors());

        Statistics.record("testStats",duration);
        assertEquals(2,Statistics.countHits("parameter:${insParam.name}"));

        //delete and test
        insParam.remove();
        assertEquals(0,InstrumentationParameters.countHits("alias:*"));

        Statistics.record("testStats",duration);
        assertEquals(2,Statistics.countHits("parameter:${insParam.name}"));

        //add instrumentation active and test
        insParam=InstrumentationParameters.add(name:"testStats",enabled:true);
        assertFalse(insParam.hasErrors());

        Statistics.record("testStats",duration);
        assertEquals(3,Statistics.countHits("parameter:${insParam.name}"));
    }
    public void testRecorStateDoesNotRecordIfDisabledGlobally()
    {
        def insParam=InstrumentationParameters.add(name:"testStats",enabled:true);
        assertFalse(insParam.hasErrors());

        def duration=500;

        assertEquals(0,Statistics.countHits("alias:*"));

        //first test successfull add
        Statistics.record("testStats",duration);
        assertEquals(1,Statistics.countHits("parameter:${insParam.name}"));

        //disable  globally
        Statistics.disableGlobally();


        Statistics.record("testStats",duration);
        assertEquals(1,Statistics.countHits("parameter:${insParam.name}"));

        //enable back and test
        Statistics.enableGlobally();

        Statistics.record("testStats",duration);
        assertEquals(2,Statistics.countHits("parameter:${insParam.name}"));

    }
    public void testGlobalEnableDisable()
    {
         System.clearProperty(StatisticsOperations.GLOBAL_ENABLE_KEY);

         assertFalse(Statistics.isEnabledGlobally())

         Statistics.enableGlobally();
         assertTrue(Statistics.isEnabledGlobally())

         Statistics.disableGlobally();
         assertFalse(Statistics.isEnabledGlobally())

         Statistics.enableGlobally();
         assertTrue(Statistics.isEnabledGlobally())

         System.clearProperty(StatisticsOperations.GLOBAL_ENABLE_KEY);
         assertFalse(Statistics.isEnabledGlobally())
    }

    
}