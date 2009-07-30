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
        if(props.starting==null)
        {
            props.starting=new Date();
        }
        if(props.ending!=null)
        {
            if(props.ending.getTime()<=props.starting.getTime())
            {
                throw new Exception("ending ${props.ending} time should be greater than starting time ${props.starting}");
            }
        }
        def maintObj = RsInMaintenance.add(props);
        if (maintObj.hasErrors()) throw new Exception(maintObj.errors.toString())
        eventsInMaintenance(true, props.objectName);
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
