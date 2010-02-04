import application.RapidApplication
import com.ifountain.annotations.HideProperty
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.quartz.JobDetail
import org.quartz.Scheduler
import org.quartz.Trigger
import org.quartz.SimpleTrigger
import org.quartz.CronTrigger;

public class RsInMaintenanceScheduleOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{
    public static SCHEDULE_SOURCE = "schedule";
    public static RsInMaintenanceSchedule addObjectSchedule(Map props) {
        return addObjectSchedule(props, true);
    }
    public static RsInMaintenanceSchedule addObjectSchedule(Map props, boolean createQuartzJob)
    {
        RsInMaintenanceSchedule schedule = RsInMaintenanceSchedule.add(props);
        if (!schedule.hasErrors() && createQuartzJob) {
            try {
                schedule.scheduleMaintenance()
            }
            catch (org.quartz.SchedulerException e) {
                schedule.remove();
                throw e;
            }
        }
        return schedule;
    }

    public synchronized void scheduleMaintenance() {
        Class executor = RapidApplication.getUtility('QuartzMaintenanceJob').class;
        JobDetail jobDetail = new JobDetail(getTriggerName(), null, executor);
        def props = [source: getSource()]
        props.putAll(asMap())
        jobDetail.getJobDataMap().put("scheduleProps", props);
        def qScheduler = getScheduler();
        def trigger = getTrigger();
        try {
            qScheduler.scheduleJob(jobDetail, trigger);
        }
        catch (org.quartz.ObjectAlreadyExistsException e)
        {
            logger.info("[InMaintenanceScheduler]: in scheduleJob, ${trigger.getName()} already scheduled, rescheduling it.");
            qScheduler.rescheduleJob(trigger.getName(), null, trigger)
        }
    }

    public synchronized void unschedule() {
        getScheduler().deleteJob(getTriggerName(), null);
    }

    @HideProperty public Scheduler getScheduler() {
        return ServletContextHolder.servletContext.getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT).getBean("quartzScheduler")
    }

    @HideProperty public Trigger getTrigger() {
        def props = [name: getTriggerName()]
        props.putAll(asMap());
        return RsInMaintenanceScheduleOperations.getTrigger(props);
    }
    public static Trigger getTrigger(Map props) {
        def type = props.type;
        if (type == RsInMaintenanceSchedule.RUN_ONCE) {
            return new SimpleTrigger(props.name, null, props.maintStarting, null, 0, 0L)
        }
        else {
            Calendar scheduleStart = new GregorianCalendar();
            scheduleStart.setTime(props.schedStarting);
            scheduleStart.set(Calendar.HOUR_OF_DAY, 0);
            scheduleStart.set(Calendar.MINUTE, 0);
            scheduleStart.set(Calendar.SECOND, 0);
            scheduleStart.set(Calendar.MILLISECOND, 0);
            Calendar maintStart = new GregorianCalendar();
            maintStart.setTime(props.maintStarting);
            def cronExp = "0 ${maintStart.get(Calendar.MINUTE)} ${maintStart.get(Calendar.HOUR_OF_DAY)}"
            if (type == RsInMaintenanceSchedule.DAILY) {
                def dayOfMonth = props.repeatEvery < 2 ? "*" : "${props.startWith}/${props.repeatEvery}"
                cronExp += " ${dayOfMonth} * ?"
            }
            else if (type == RsInMaintenanceSchedule.WEEKLY) {
                cronExp += " ? * ${props.daysOfWeek}"
            }
            else if (type == RsInMaintenanceSchedule.MONTHLY_BY_DATE) {
                def month = props.repeatEvery < 2 ? "*" : "${props.startWith}/${props.repeatEvery}"
                cronExp += " ${props.daysOfMonth} ${month} ?"
            }
            else if (type == RsInMaintenanceSchedule.MONTHLY_BY_DAY) {
                def month = props.repeatEvery < 2 ? "*" : "${props.startWith}/${props.repeatEvery}"
                cronExp += " ? ${month} ${props.daysOfMonth}"
            }
            CronTrigger trigger = new CronTrigger(props.name, null, cronExp)
            trigger.setStartTime(scheduleStart.getTime());
            trigger.setEndTime(props.schedEnding);
            return trigger;

        }
    }
    public String getTriggerName() {
        return "MaintenanceSchedule_${objectName}_${id}"
    }

    public static void removeSchedule(scheduleId)
    {
        def schedule = RsInMaintenanceSchedule.get(id: scheduleId);
        if (schedule)
        {
            schedule.unschedule();
            schedule.remove();
        }
    }
    public def getSource()
    {
        return "${SCHEDULE_SOURCE}_${id}";
    }

    public static void removeExpiredItems() {
        def logger = getLogger()
        logger.debug("Removing expired maintenance schedules");
        def query = "(type:${RsInMaintenanceSchedule.RUN_ONCE} AND maintEnding:{* TO now}) OR (maintEnding:{* TO now} AND NOT type:${RsInMaintenanceSchedule.RUN_ONCE})"
        def expiredSchedules = RsInMaintenanceSchedule.getPropertyValues(query, ["id"])
        logger.debug("There are ${expiredSchedules.size()} number of expired maintenace schedule");
        expiredSchedules.each {
            RsInMaintenanceSchedule.removeSchedule(it.id);
        }
    }
}
