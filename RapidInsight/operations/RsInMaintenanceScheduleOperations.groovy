import java.util.Date

public class RsInMaintenanceScheduleOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{
   
    public static RsInMaintenanceSchedule addObjectSchedule(String objectName,String info,Date startTime,Date endTime)
    {
        if(endTime.compareTo(startTime)<=0)
        {
            throw new Exception("endTime should be greater than startTime ");
        }

        def currentSchedules=RsInMaintenanceSchedule.searchEvery("objectName:${objectName.exactQuery()}");
        currentSchedules.each{ RsInMaintenanceSchedule schedule ->


            //new schedule can be before of existing schedule
            def isBefore =   startTime.compareTo(schedule.starting) < 0 && endTime.compareTo(schedule.starting)<= 0 ;
            //new schedule can be after of existing schedule
            def isAfter =  startTime.compareTo(schedule.ending) >= 0 && endTime.compareTo(schedule.ending) > 0 ;

            if(!isBefore && !isAfter)
            {
                throw new Exception("Timespan collision, entered timespan should be before or after, start time:${schedule.starting} end time: ${schedule.ending}");
            }

        }
        def addParams=[:];
        addParams.objectName=objectName;
        addParams.info=info;
        addParams.starting=startTime;
        addParams.ending=endTime;
        def schedule=RsInMaintenanceSchedule.add(addParams);
        return schedule;

    }
    public static void removeSchedule(scheduleId)
    {
        def schedule=RsInMaintenanceSchedule.get(id:scheduleId);
        if(schedule)
        {
            def source="schedule";
            def maintResults=RsInMaintenance.search("objectName:${schedule.objectName.exactQuery()} AND source:${source.exactQuery()}").results;            
            if(maintResults.size()>0)
            {
                maintResults[0].remove();
            }
            schedule.remove();

        }
    }


	public static void activateScheduledItems(logger){
        logger.debug("BEGIN activateScheduledItems")
        def currentTime = new Date().getTime()
        logger.debug("current time: $currentTime")
        def nullDate = new Date(0).getTime()
        def scheduledItems = RsInMaintenanceSchedule.search("active:false")
        logger.debug("scheduled item count: ${scheduledItems.total}")
        scheduledItems.results.each{
            logger.debug("starting.getTime(): ${it.starting.getTime()}")
            if (it.starting.getTime()>nullDate && it.starting.getTime() <= currentTime){
                it.active = true
                logger.debug("activating maintenance for: ${it.objectName}")
                RsInMaintenance.putObjectInMaintenance(it.objectName,"schedule",it.info,it.ending);
            }
        }
        logger.debug("END activateScheduledItems")
    }

    public static void removeExpiredItems(logger){
        logger.debug("BEGIN removeExpiredItems")
        def currentTime = new Date().getTime()
        logger.debug("current time: $currentTime")


        def nullDate = new Date(0).getTime()
        def activeItems = RsInMaintenanceSchedule.search("active:true")
        logger.debug("active item count: ${activeItems.total}")

        activeItems.results.each{
            logger.debug("ending.getTime(): ${it.ending.getTime()}")
            if (it.ending.getTime()>nullDate && it.ending.getTime() <= currentTime){
                logger.debug("removing schedule for: ${it.objectName}")
                it.remove();
            }
        }
    }
}
