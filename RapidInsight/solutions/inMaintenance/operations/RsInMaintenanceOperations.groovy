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

    //map should contain objectName and can optionally contain ending, source, and info
    public static RsInMaintenance putObjectInMaintenance(props)
    {
        def tempProps=[:];
        tempProps.putAll(props);

        if(tempProps.starting==null)
        {
            tempProps.starting=new Date();
        }
        if(isObjectInMaintenance(tempProps.objectName))
        {
            tempProps.remove("starting");
        }

        if(tempProps.ending!=null && tempProps.starting!=null)
        {
            if(tempProps.ending.getTime()<=tempProps.starting.getTime())
            {
                throw new Exception("ending ${tempProps.ending} time should be greater than starting time ${tempProps.starting}");
            }
        }

        def maintObj = RsInMaintenance.add(tempProps);
        if (maintObj.hasErrors()) throw new Exception(maintObj.errors.toString())
        eventsInMaintenance(true, tempProps.objectName);
        return maintObj;

    }

    public static void takeObjectOutOfMaintenance(String objectName)
    {
        def maintObj = RsInMaintenance.get(objectName: objectName)
        if (maintObj) {
            RsInMaintenance.takeObjectOutOfMaintenance(maintObj);
        }
    }

    public static void takeObjectOutOfMaintenance(RsInMaintenance maintObj)
    {
        maintObj.remove()
        eventsInMaintenance(false, maintObj.objectName);
    }

    public static void eventsInMaintenance(boolean maint, String objectName) {
        def events = RsEvent.search("elementName:${objectName}")
        events.results.each { event ->
            if (event.inMaintenance != maint)
                event.inMaintenance = maint
        }
    }



    public static void removeExpiredItems() {
        def logger = getLogger()
        logger.debug("BEGIN removeExpiredItems")
        def currentTime = new Date().getTime()
        logger.debug("current time: $currentTime")

        def activeItems = RsInMaintenance.searchEvery("alias:*")
        logger.debug("active item count: ${activeItems.size()}")
        def nullDate = new Date(0).getTime()
        activeItems.each { maintenance ->
            logger.debug("ending.getTime(): ${maintenance.ending.getTime()}")
            if (maintenance.ending.getTime() > nullDate && maintenance.ending.getTime() <= currentTime) {
                logger.debug("deactivating maintenance for: ${maintenance.objectName}")
                RsInMaintenance.takeObjectOutOfMaintenance(maintenance);
            }
        }
    }
}
