import java.util.Date

public class RsInMaintenanceOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{
    public static boolean isObjectInMaintenance(objectName)
    {
        def maintObject = RsInMaintenance.get(objectName:objectName)
		if (maintObject?.active)
		  return true
		else
		  return false
    }
    public static void eventInBeforeInsert(event)
    {
         if(isObjectInMaintenance(event.elementName))
         {
             event.setPropertyWithoutUpdate("inMaintenance",true);
         }
    }
    public static RsInMaintenance putObjectInMaintenance(String objectName)
    {
        def addParams=[:];
        addParams.objectName=objectName;
        addParams.active=true;
        def maintObj=RsInMaintenance.add(addParams);
        eventsInMaintenance(true,objectName);
        return maintObj;
        
    }
    public static RsInMaintenance putObjectInMaintenance(String objectName,Date endTime)
    {
        def addParams=[:];
        addParams.objectName=objectName;
        addParams.ending=endTime;
        addParams.active=true;
        def maintObj=RsInMaintenance.add(addParams);
        eventsInMaintenance(true,objectName);
        return maintObj;

    }
    public static RsInMaintenance putObjectInMaintenance(String objectName,Date startTime,Date endTime)
    {
        def addParams=[:];
        addParams.objectName=objectName;
        addParams.starting=startTime;
        addParams.ending=endTime;
        def maintObj=RsInMaintenance.add(addParams);
        return maintObj;

    }

    public static void takeObjectOutOfMaintenance(objectName)
    {
       def maintObj = RsInMaintenance.get(objectName:objectName)
	   maintObj?.remove()
       eventsInMaintenance(false,objectName);
    }



    public static void eventsInMaintenance(boolean maint,String objectName) {
		def events = RsEvent.search("elementName:${objectName}")
		events.results.each{
			if (it.inMaintenance != maint)
				it.inMaintenance = maint
		}
	}

	public static void activateScheduledItems(logger){
        logger.debug("BEGIN activateScheduledItems")
        def currentTime = new Date().getTime()
        logger.debug("current time: $currentTime")
        def nullDate = new Date(0).getTime()
        def scheduledItems = RsInMaintenance.search("active:false")
        logger.debug("scheduled item count: ${scheduledItems.total}")
        scheduledItems.results.each{
            logger.debug("starting.getTime(): ${it.starting.getTime()}")
            if (it.starting.getTime()>nullDate && it.starting.getTime() <= currentTime){
                it.active = true
                logger.debug("activating maintenance for: ${it.objectName}")
                RsInMaintenance.eventsInMaintenance(true,it.objectName);


            }
        }
        logger.debug("END activateScheduledItems")
    }

    public static void removeExpiredItems(logger){
        logger.debug("BEGIN removeExpiredItems")
        def currentTime = new Date().getTime()
        logger.debug("current time: $currentTime")


        def nullDate = new Date(0).getTime()
        def activeItems = RsInMaintenance.search("active:true")
        logger.debug("active item count: ${activeItems.total}")

        activeItems.results.each{
            logger.debug("ending.getTime(): ${it.ending.getTime()}")
            if (it.ending.getTime()>nullDate && it.ending.getTime() <= currentTime){
                logger.debug("deactivating maintenance for: ${it.objectName}")
                RsInMaintenance.eventsInMaintenance(false,it.objectName);
                it.remove()
            }
        }
    }
}
