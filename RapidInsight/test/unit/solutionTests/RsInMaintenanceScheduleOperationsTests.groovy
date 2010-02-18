package solutionTests

import application.RapidApplication
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.test.util.RapidApplicationTestUtils
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import org.quartz.impl.StdSchedulerFactory
import org.quartz.Scheduler
import org.quartz.Trigger
import org.quartz.SimpleTrigger
import org.quartz.CronTrigger
import org.quartz.SchedulerException
import org.quartz.JobDetail
import org.quartz.StatefulJob
import org.quartz.JobExecutionContext

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jan 29, 2010
* Time: 9:36:04 AM
*/
class RsInMaintenanceScheduleOperationsTests extends RapidCmdbWithCompassTestCase {
    def RsInMaintenanceSchedule;
    def RsInMaintenanceScheduleOperations;
    def RsInMaintenance;
    def RsInMaintenanceOperations;
    def RsEvent;
    def RsEventOperations;
    def deviceName = "device1"
    def info = "info";
    Scheduler quartzScheduler = null;
    public void setUp() {
        super.setUp();
        ["RsInMaintenanceSchedule", "RsInMaintenance", "RsEvent", "RsEventOperations"].each {className ->
            setProperty(className, gcl.loadClass(className));
        }
        initialize([RsInMaintenance, RsInMaintenanceSchedule, RsEvent, RsEventOperations], [])
        def solutionPath = getWorkspacePath() + "/RapidModules/RapidInsight/solutions/inMaintenance"
        setProperty("RsInMaintenanceScheduleOperations", gcl.parseClass(new File("${solutionPath}/operations/RsInMaintenanceScheduleOperations.groovy")));
        setProperty("RsInMaintenanceOperations", gcl.parseClass(new File("${solutionPath}/operations/RsInMaintenanceOperations.groovy")));
        CompassForTests.addOperationSupport(RsInMaintenanceSchedule, RsInMaintenanceScheduleOperations)
        CompassForTests.addOperationSupport(RsInMaintenance, RsInMaintenanceOperations)
        CompassForTests.addOperationSupport(RsEvent, RsEventOperations)
        RapidApplicationTestUtils.initializeRapidApplicationOperations(RapidApplication);
        RapidApplicationTestUtils.clearProcessors();
        RapidApplicationTestUtils.utilityPaths = ["InMaintenanceCalculator": new File("${solutionPath}/operations/InMaintenanceCalculator.groovy"),
                "QuartzMaintenanceJob": new File("${solutionPath}/operations/QuartzMaintenanceJob.groovy")];
        RapidApplication.getUtility("EventProcessor").beforeProcessors = ["InMaintenanceCalculator"];
        quartzScheduler = StdSchedulerFactory.getDefaultScheduler();
        quartzScheduler.start();
        RsInMaintenanceScheduleOperations.metaClass.getScheduler = {->
            return quartzScheduler
        }
    }

    public void tearDown() {
        quartzScheduler.shutdown();
        RapidApplicationTestUtils.clearProcessors();
        RapidApplicationTestUtils.clearUtilityPaths();
        clearMetaClasses();
        super.tearDown();
    }
    public void clearMetaClasses()
    {
        ExpandoMetaClass.disableGlobally();
        GroovySystem.metaClassRegistry.removeMetaClass(RsInMaintenanceScheduleOperations)
        ExpandoMetaClass.enableGlobally();
    }

    public void testRunOnceSchedule() {
        def starting = new Date(System.currentTimeMillis() + 10000L);
        def ending = new Date(starting.getTime() + 100000L);

        def props = [objectName: deviceName, info: info, maintStarting: starting, maintEnding: ending, type: RsInMaintenanceSchedule.RUN_ONCE];

        def schedule = RsInMaintenanceSchedule.addObjectSchedule(props);
        assertFalse(schedule.hasErrors())
        assertEquals(deviceName, schedule.objectName)
        assertEquals(info, schedule.info)
        assertEquals(starting.getTime(), schedule.maintStarting.getTime())
        assertEquals(ending.getTime(), schedule.maintEnding.getTime())
        assertEquals(RsInMaintenanceSchedule.RUN_ONCE, schedule.type)

        Trigger trigger = schedule.getTrigger();
        assertTrue(trigger instanceof SimpleTrigger);
        SimpleTrigger simpleTrigger = (SimpleTrigger) trigger;

        assertEquals(schedule.getTriggerName(), simpleTrigger.getName());
        assertEquals(starting, simpleTrigger.getStartTime());
        assertEquals(starting, simpleTrigger.getFinalFireTime())
    }

    public void testDailySchedule() {
        def starting = new Date(1000 * 60 * 60 * 10L);
        def ending = new Date(1000 * 60 * 60 * 15L);
        def schedStarting = new Date(System.currentTimeMillis());
        def schedEnding = new Date(System.currentTimeMillis() + 10 * 24 * 60 * 60 * 1000L);
        def props = [objectName: deviceName, info: info, maintStarting: starting, maintEnding: ending, type: RsInMaintenanceSchedule.DAILY,
                startWith: 2, repeatEvery: 3, schedStarting: schedStarting, schedEnding: schedEnding];

        def schedule = RsInMaintenanceSchedule.addObjectSchedule(props);
        println schedule.errors
        assertFalse(schedule.hasErrors())
        assertEquals(deviceName, schedule.objectName)
        assertEquals(info, schedule.info)
        assertEquals(starting.getTime(), schedule.maintStarting.getTime())
        assertEquals(ending.getTime(), schedule.maintEnding.getTime())
        assertEquals(schedStarting.getTime(), schedule.schedStarting.getTime())
        assertEquals(schedEnding.getTime(), schedule.schedEnding.getTime())
        assertEquals(RsInMaintenanceSchedule.DAILY, schedule.type)
        assertEquals(2, schedule.startWith)
        assertEquals(3, schedule.repeatEvery)

        Trigger trigger = schedule.getTrigger();
        assertTrue(trigger instanceof CronTrigger);
        CronTrigger cronTrigger = (CronTrigger) trigger;

        assertEquals(schedule.getTriggerName(), cronTrigger.getName());
        Calendar cal1 = new GregorianCalendar();
        cal1.setTimeInMillis(schedStarting.getTime())
        Calendar cal2 = new GregorianCalendar();
        cal2.setTimeInMillis(starting.getTime())
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        assertEquals(cal1.getTime().getTime(), cronTrigger.getStartTime().getTime())
        assertEquals(schedule.schedEnding, cronTrigger.getEndTime())
        assertEquals("0 ${cal2.get(Calendar.MINUTE)} ${cal2.get(Calendar.HOUR_OF_DAY)} 2/3 * ?", cronTrigger.getCronExpression())
    }

    public void testWeeeklySchedule() {
        def starting = new Date(1000 * 60 * 60 * 10L);
        def ending = new Date(1000 * 60 * 60 * 15L);
        def schedStarting = new Date(System.currentTimeMillis());
        def schedEnding = new Date(System.currentTimeMillis() + 10 * 24 * 60 * 60 * 1000L);

        def props = [objectName: deviceName, info: info, maintStarting: starting, maintEnding: ending, type: RsInMaintenanceSchedule.WEEKLY,
                daysOfWeek: "1,3,6", schedStarting: schedStarting, schedEnding: schedEnding];

        def schedule = RsInMaintenanceSchedule.addObjectSchedule(props);
        assertFalse(schedule.hasErrors())
        assertEquals(deviceName, schedule.objectName)
        assertEquals(info, schedule.info)
        assertEquals(starting.getTime(), schedule.maintStarting.getTime())
        assertEquals(ending.getTime(), schedule.maintEnding.getTime())
        assertEquals(schedStarting.getTime(), schedule.schedStarting.getTime())
        assertEquals(schedEnding.getTime(), schedule.schedEnding.getTime())
        assertEquals(RsInMaintenanceSchedule.WEEKLY, schedule.type)
        assertEquals("1,3,6", schedule.daysOfWeek)

        Trigger trigger = schedule.getTrigger();
        assertTrue(trigger instanceof CronTrigger);
        CronTrigger cronTrigger = (CronTrigger) trigger;

        assertEquals(schedule.getTriggerName(), cronTrigger.getName());
        Calendar cal1 = new GregorianCalendar();
        cal1.setTimeInMillis(schedStarting.getTime())
        Calendar cal2 = new GregorianCalendar();
        cal2.setTimeInMillis(starting.getTime())
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        assertEquals(cal1.getTime().getTime(), cronTrigger.getStartTime().getTime())
        assertEquals(schedule.schedEnding, cronTrigger.getEndTime())
        assertEquals("0 ${cal2.get(Calendar.MINUTE)} ${cal2.get(Calendar.HOUR_OF_DAY)} ? * ${schedule.daysOfWeek}", cronTrigger.getCronExpression())
    }

    public void testMonthlyByDateSchedule() {
        def starting = new Date(1000 * 60 * 60 * 10L);
        def ending = new Date(1000 * 60 * 60 * 15L);
        def schedStarting = new Date(System.currentTimeMillis());
        def schedEnding = new Date(System.currentTimeMillis() + (300 * 24 * 60 * 60 * 1000L));
        def props = [objectName: deviceName, info: info, maintStarting: starting, maintEnding: ending, type: RsInMaintenanceSchedule.MONTHLY_BY_DATE,
                startWith: 3, repeatEvery: 2, schedStarting: schedStarting, schedEnding: schedEnding, daysOfMonth: "12,13,14"];

        def schedule = RsInMaintenanceSchedule.addObjectSchedule(props);
        assertFalse(schedule.hasErrors())
        assertEquals(deviceName, schedule.objectName)
        assertEquals(info, schedule.info)
        assertEquals(starting.getTime(), schedule.maintStarting.getTime())
        assertEquals(ending.getTime(), schedule.maintEnding.getTime())
        assertEquals(schedStarting.getTime(), schedule.schedStarting.getTime())
        assertEquals(schedEnding.getTime(), schedule.schedEnding.getTime())
        assertEquals(RsInMaintenanceSchedule.MONTHLY_BY_DATE, schedule.type)
        assertEquals(3, schedule.startWith)
        assertEquals(2, schedule.repeatEvery)
        assertEquals("12,13,14", schedule.daysOfMonth)

        Trigger trigger = schedule.getTrigger();
        assertTrue(trigger instanceof CronTrigger);
        CronTrigger cronTrigger = (CronTrigger) trigger;

        assertEquals(schedule.getTriggerName(), cronTrigger.getName());
        Calendar cal1 = new GregorianCalendar();
        cal1.setTimeInMillis(schedStarting.getTime())
        Calendar cal2 = new GregorianCalendar();
        cal2.setTimeInMillis(starting.getTime())
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        assertEquals(cal1.getTime().getTime(), cronTrigger.getStartTime().getTime())
        assertEquals(schedule.schedEnding, cronTrigger.getEndTime())
        assertEquals("0 ${cal2.get(Calendar.MINUTE)} ${cal2.get(Calendar.HOUR_OF_DAY)} ${schedule.daysOfMonth} 3/2 ?", cronTrigger.getCronExpression())

    }

    public void testMonthlyByDaySchedule() {
        def starting = new Date(1000 * 60 * 60 * 10L);
        def ending = new Date(1000 * 60 * 60 * 15L);
        def schedStarting = new Date(System.currentTimeMillis());
        def schedEnding = new Date(System.currentTimeMillis() + (300 * 24 * 60 * 60 * 1000L));
        def props = [objectName: deviceName, info: info, maintStarting: starting, maintEnding: ending, type: RsInMaintenanceSchedule.MONTHLY_BY_DAY,
                startWith: 3, repeatEvery: 2, schedStarting: schedStarting, schedEnding: schedEnding, daysOfMonth: "6#1"];

        def schedule = RsInMaintenanceSchedule.addObjectSchedule(props);
        assertFalse(schedule.hasErrors())
        assertEquals(deviceName, schedule.objectName)
        assertEquals(info, schedule.info)
        assertEquals(starting.getTime(), schedule.maintStarting.getTime())
        assertEquals(ending.getTime(), schedule.maintEnding.getTime())
        assertEquals(schedStarting.getTime(), schedule.schedStarting.getTime())
        assertEquals(schedEnding.getTime(), schedule.schedEnding.getTime())
        assertEquals(RsInMaintenanceSchedule.MONTHLY_BY_DAY, schedule.type)
        assertEquals(3, schedule.startWith)
        assertEquals(2, schedule.repeatEvery)
        assertEquals("6#1", schedule.daysOfMonth)

        Trigger trigger = schedule.getTrigger();
        assertTrue(trigger instanceof CronTrigger);
        CronTrigger cronTrigger = (CronTrigger) trigger;

        assertEquals(schedule.getTriggerName(), cronTrigger.getName());
        Calendar cal1 = new GregorianCalendar();
        cal1.setTimeInMillis(schedStarting.getTime())
        Calendar cal2 = new GregorianCalendar();
        cal2.setTimeInMillis(starting.getTime())
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        assertEquals(cal1.getTime().getTime(), cronTrigger.getStartTime().getTime())
        assertEquals(schedule.schedEnding, cronTrigger.getEndTime())
        assertEquals("0 ${cal2.get(Calendar.MINUTE)} ${cal2.get(Calendar.HOUR_OF_DAY)} ? 3/2 ${schedule.daysOfMonth}", cronTrigger.getCronExpression())
    }

    public void testAddObjectScheduleValidatesTheTrigger() {
        def starting = new Date(1000 * 60 * 60 * 10L);
        def ending = new Date(1000 * 60 * 60 * 15L);
        def schedStarting = new Date(System.currentTimeMillis());
        def schedEnding = new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 10L));
        Calendar cal = Calendar.getInstance();
        def today = cal.get(Calendar.DAY_OF_WEEK);
        def props = [objectName: deviceName, info: info, maintStarting: starting, maintEnding: ending, type: RsInMaintenanceSchedule.WEEKLY,
                daysOfWeek: ((today + 2) % 7) +1, schedStarting: schedStarting, schedEnding: schedEnding];

        try {
            RsInMaintenanceSchedule.addObjectSchedule(props);
            fail("should throw exception")
        }
        catch (SchedulerException e) {
            assertEquals("Based on configured schedule, the given trigger will never fire.", e.getMessage())
        }
        assertEquals(0, RsInMaintenanceSchedule.count())
    }

    public void testIfSchedEndingLessThanCurrentTimeAddPopulatesErrors() {
        def starting = new Date(1000 * 60 * 60 * 10L);
        def ending = new Date(1000 * 60 * 60 * 15L);
        def schedStarting = new Date(System.currentTimeMillis() - 2000);
        def schedEnding = new Date(System.currentTimeMillis() - 1000);
        def props = [objectName: deviceName, info: info, maintStarting: starting, maintEnding: ending, type: RsInMaintenanceSchedule.WEEKLY,
                daysOfWeek: "1", schedStarting: schedStarting, schedEnding: schedEnding];
        def schedule = RsInMaintenanceSchedule.addObjectSchedule(props);

        assertTrue(schedule.hasErrors());
        def error = schedule.errors.allErrors[0];
        assertEquals("default.not.greater.than", error.code)
        assertEquals("current time", error.arguments[3])

        Scheduler scheduler = schedule.getScheduler();
        Trigger trigger = scheduler.getTrigger(schedule.getTriggerName(), null);
        assertNull(trigger);
    }

    public void testRunOnceScheduleWithQuartzJob() {
        def starting = new Date(System.currentTimeMillis() + 1000L);
        def ending = new Date(starting.getTime() + 100000L);

        def props = [objectName: deviceName, info: info, maintStarting: starting, maintEnding: ending, type: RsInMaintenanceSchedule.RUN_ONCE];

        def schedule = RsInMaintenanceSchedule.addObjectSchedule(props);
        assertFalse(schedule.hasErrors())

        Scheduler scheduler = schedule.getScheduler();
        Trigger trigger = scheduler.getTrigger(schedule.getTriggerName(), null);
        assertTrue(trigger instanceof SimpleTrigger);
        SimpleTrigger simpleTrigger = (SimpleTrigger) trigger;

        assertEquals(schedule.getTriggerName(), simpleTrigger.getName());
        assertEquals(starting, simpleTrigger.getStartTime());
        assertEquals(starting, simpleTrigger.getFinalFireTime())

        Thread.sleep(2000);

        def maintObj = RsInMaintenance.get(objectName: deviceName);
        assertNotNull(maintObj);
        assertEquals(starting, maintObj.starting)
        assertEquals(ending, maintObj.ending)
        assertEquals(info, maintObj.info)
        assertEquals(schedule.getSource(), maintObj.source)

        def event = RsEvent.add(name: "event", elementName: maintObj.objectName)
        assertTrue(RsEvent.get(name: event.name).inMaintenance)
    }

    public void testScheduledMaintenancesDoesNotOverrideIfThereIsAnAlreadyDefinedMaintenace() {
        RsInMaintenance.putObjectInMaintenance([objectName: deviceName, info: "ondemand maintenance", source: "user user1"]);

        def starting = new Date(System.currentTimeMillis() + 1000L);
        def ending = new Date(starting.getTime() + 100000L);
        def props = [objectName: deviceName, info: info, maintStarting: starting, maintEnding: ending, type: RsInMaintenanceSchedule.RUN_ONCE];
        def schedule = RsInMaintenanceSchedule.addObjectSchedule(props);
        assertFalse(schedule.hasErrors())

        Scheduler scheduler = schedule.getScheduler();
        Trigger trigger = scheduler.getTrigger(schedule.getTriggerName(), null);
        assertNotNull(trigger)

        Thread.sleep(2000);

        def maintObj = RsInMaintenance.get(objectName: deviceName);
        assertEquals(new Date(0).getTime(), maintObj.ending.getTime());
        assertEquals("ondemand maintenance", maintObj.info);
        assertEquals("user user1", maintObj.source);
    }

    public void testRemoveExpiredItems() {
        def starting = new Date(System.currentTimeMillis() - 10000L);
        def ending = new Date(System.currentTimeMillis() + 500);
        def props = [objectName: deviceName, info: info, maintStarting: starting, maintEnding: ending, type: RsInMaintenanceSchedule.RUN_ONCE];

        def schedule = RsInMaintenanceSchedule.add(props);
        assertFalse(schedule.hasErrors())
        schedule.unschedule();

        Scheduler scheduler = schedule.getScheduler();
        scheduler.scheduleJob(
                new JobDetail(schedule.getTriggerName(), null, MockMaintenanceJob),
                new SimpleTrigger(schedule.getTriggerName(), null, new Date(System.currentTimeMillis() + 100000L), null, 0, 0L));
        assertNotNull(scheduler.getTrigger(schedule.getTriggerName(), null));

        Thread.sleep(1500);
        RsInMaintenanceSchedule.removeExpiredItems();
        assertEquals(0, RsInMaintenanceSchedule.count());
        assertNull(scheduler.getTrigger(schedule.getTriggerName(), null));

        starting = new Date(System.currentTimeMillis() + 1000l);
        ending = new Date(System.currentTimeMillis() + 100000l);
        props = [objectName: deviceName, info: info, maintStarting: starting, maintEnding: ending, type: RsInMaintenanceSchedule.RUN_ONCE];

        schedule = RsInMaintenanceSchedule.addObjectSchedule(props);
        assertFalse(schedule.hasErrors())
                
        RsInMaintenanceSchedule.removeExpiredItems();
        
        assertEquals(1, RsInMaintenanceSchedule.count());
    }
    public void testScheduleAndUnscheduleDoneWhenMaintenanceScheduleAddedOrRemovedByTriggers() {
        def starting = new Date(System.currentTimeMillis() - 10000L);
        def ending = new Date(System.currentTimeMillis() + 500);
        def props = [objectName: deviceName, info: info, maintStarting: starting, maintEnding: ending, type: RsInMaintenanceSchedule.RUN_ONCE];

        def schedule = RsInMaintenanceSchedule.add(props);
        assertFalse(schedule.hasErrors())    

        Scheduler scheduler = schedule.getScheduler();
        assertNotNull(scheduler.getTrigger(schedule.getTriggerName(), null));

        schedule.remove();
        assertNull(scheduler.getTrigger(schedule.getTriggerName(), null));

        assertEquals(0, RsInMaintenanceSchedule.count());

    }
}

class MockMaintenanceJob implements StatefulJob {
    public void execute(JobExecutionContext jobExecutionContext) {
    }
}