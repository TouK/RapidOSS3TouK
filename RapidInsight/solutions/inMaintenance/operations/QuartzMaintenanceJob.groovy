import org.quartz.JobExecutionContext
import org.quartz.Job
import org.apache.log4j.Logger

class QuartzMaintenanceJob implements Job {
    Logger logger = Logger.getLogger(QuartzMaintenanceJob);
    public void execute(JobExecutionContext jobExecutionContext) {
        Map scheduleProps = (Map) jobExecutionContext.getJobDetail().getJobDataMap().get("scheduleProps")
        Date ending;
        if (scheduleProps.schedType == RsInMaintenanceSchedule.RUN_ONCE) {
            ending = scheduleProps.maintEnding;
        }
        else {
            Calendar cal1 = Calendar.getInstance()
            cal1.setTimeInMillis(System.currentTimeMillis());
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(scheduleProps.maintEnding)
            cal1.set(Calendar.HOUR_OF_DAY, cal2.get(Calendar.HOUR_OF_DAY))
            cal1.set(Calendar.MINUTE, cal2.get(Calendar.MINUTE))
            cal1.set(Calendar.SECOND, 0)
            cal1.set(Calendar.MILLISECOND, 0)
            ending = cal1.getTime();
        }
        try {
            RsInMaintenance.putObjectInMaintenance(
                    objectName: scheduleProps.objectName,
                    info: scheduleProps.info,
                    source: scheduleProps.source,
                    starting: scheduleProps.maintStarting,
                    ending: ending)
        }
        catch (e) {
            logger.warn("Could not put object ${scheduleProps.objectName} into maintenance from schedule. Reason:${e.getMessage()}")
        }
    }

}