import java.util.Date

public class RsInMaintenanceOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{
    public static boolean getObjectInMaintenance(object)
    {
        def o = RsInMaintenance.get(objectId:object.id)
		if (o?.active)
		  return true
		else
		  return false
    }
    public static void  putObjectInMaintenance(object,maintParams)
    {
        maintParams.objectId=object.id;
        def maintObj=RsInMaintenance.add(maintParams);
        if(maintObj.active == true)
        {
           eventsInMaintenance(true,object.name);
        }
    }
    public static void takeObjectOutOfMaintenance(object)
    {
       def maintObj = RsInMaintenance.get(objectId:object.id)
	   maintObj?.remove()
       eventsInMaintenance(false,object.name);
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
                def object = RsTopologyObject.get(id:it.objectId)
                logger.debug("activating maintenance for: ${object}")
                if(object!=null)
                {
                    RsInMaintenance.eventsInMaintenance(true,object.name);
                }

            }
        }
        logger.debug("END activateScheduledItems")
    }

    public static void removeExpiredItems(logger){
        def currentTime = new Date().getTime()
        logger.debug("current time: $currentTime")
        def nullDate = new Date(0).getTime()
        def activeItems = RsInMaintenance.search("active:true")
        logger.debug("active item count: ${activeItems.total}")
        activeItems.results.each{
            logger.debug("ending.getTime(): ${it.ending.getTime()}")
            if (it.ending.getTime()>nullDate && it.ending.getTime() <= currentTime){
                def object = RsTopologyObject.get(id:it.objectId)
                logger.debug("deactivating maintenance for: ${object}")
                if(object!=null)
                {
                    RsInMaintenance.eventsInMaintenance(false,object.name);
                }
                it.remove()
            }
        }
    }
}
