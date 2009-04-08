import java.util.Date

public class RsInMaintenanceOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{
    public static boolean isObjectInMaintenance(objectName)
    {
        return RsInMaintenance.countHits("objectName:${objectName.exactQuery()}");
    }
    public static boolean isEventInMaintenance(event)
    {
        return isObjectInMaintenance(event.elementName);
    }
    public static RsInMaintenance putObjectInMaintenance(String objectName,String source,String info)
    {
        def addParams=[:];
        addParams.objectName=objectName;
        addParams.source=source;
        addParams.info=info;
        def maintObj=RsInMaintenance.add(addParams);
        eventsInMaintenance(true,objectName);
        return maintObj;
        
    }
    public static RsInMaintenance putObjectInMaintenance(String objectName,String source,String info,Date endTime)
    {
        def addParams=[:];
        addParams.objectName=objectName;
        addParams.source=source;
        addParams.info=info;
        addParams.ending=endTime;
        def maintObj=RsInMaintenance.add(addParams);
        eventsInMaintenance(true,objectName);
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



    public static void removeExpiredItems(logger){
        logger.debug("BEGIN removeExpiredItems")
        def currentTime = new Date().getTime()
        logger.debug("current time: $currentTime")

        def activeItems = RsInMaintenance.searchEvery("alias:*")
        logger.debug("active item count: ${activeItems.size()}")
        def nullDate = new Date(0).getTime()
        activeItems.each{
            logger.debug("ending.getTime(): ${it.ending.getTime()}")
            if (it.ending.getTime()>nullDate && it.ending.getTime() <= currentTime){
                logger.debug("deactivating maintenance for: ${it.objectName}")
                RsInMaintenance.eventsInMaintenance(false,it.objectName);
                it.remove()
            }
        }
    }
}
