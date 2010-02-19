import java.util.Date

public class RsInMaintenanceOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {
    //	changed for isLocal property
	def beforeInsert()
    {
		application.RapidApplication.getUtility("RedundancyUtility").objectInBeforeInsert(this.domainObject);
    }
	def beforeUpdate(params)
    {
		application.RapidApplication.getUtility("RedundancyUtility").objectInBeforeUpdate(this.domainObject);
    }
	//change ended
    
    def afterInsert()
    {
		putEventsInMaintenance (true,objectName);
    }
	def afterUpdate(params)
    {

    }
     //	changed for isLocal property
	def afterDelete()
    {
        application.RapidApplication.getUtility("RedundancyUtility").objectInAfterDelete(this.domainObject);
        RsHistoricalInMaintenance.add(asMap());
        putEventsInMaintenance (false,objectName);
    }
    //change ended

    public static boolean isObjectInMaintenance(objectName) {
        return RsInMaintenance.countHits("objectName:${objectName.exactQuery()}") > 0;
    }

    public static boolean isEventInMaintenance(event) {
        return isObjectInMaintenance(event.elementName);
    }

    //map should contain objectName and can optionally contain ending, source, and info
    public static RsInMaintenance putObjectInMaintenance(props, boolean override) {
        def tempProps = [:];
        tempProps.putAll(props);
        if (tempProps.starting == null) {
            tempProps.starting = new Date();
        }
        if (tempProps.ending == null) {
            tempProps.ending = new Date(0);
        }
        def endTime = tempProps.ending.getTime()
        if (endTime != 0 && endTime <= tempProps.starting.getTime()) {
            throw new Exception("ending ${tempProps.ending} time should be greater than starting time ${tempProps.starting}");
        }

        def maintObj = RsInMaintenance.get(objectName:tempProps.objectName);
        if (maintObj) {
            tempProps.remove("starting");
            if(!override && (maintObj.ending.getTime() == 0 || (endTime != 0 && endTime <= maintObj.ending.getTime())))return maintObj;
        }

        maintObj = RsInMaintenance.add(tempProps);
        if (maintObj.hasErrors()) throw new Exception(maintObj.errors.toString())
        return maintObj;
    }

    public static RsInMaintenance putObjectInMaintenance(props) {
         putObjectInMaintenance(props, false)
    }

    public static void takeObjectOutOfMaintenance(String objectName) {
        def maintObj = RsInMaintenance.get(objectName: objectName)
        if (maintObj) {
            RsInMaintenance.takeObjectOutOfMaintenance(maintObj);
        }
    }

    public static void takeObjectOutOfMaintenance(RsInMaintenance maintObj) {
        maintObj.remove()
    }

    public static void putEventsInMaintenance(boolean maint, String objectName) {
        def events = RsEvent.searchEvery("elementName:${objectName} AND inMaintenance:${!maint}");
        events.each {event ->
            event.update(inMaintenance: maint);
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
        activeItems.each {maintenance ->
            logger.debug("ending.getTime(): ${maintenance.ending.getTime()}")
            if (maintenance.ending.getTime() > nullDate && maintenance.ending.getTime() <= currentTime) {
                logger.debug("deactivating maintenance for: ${maintenance.objectName}")
                RsInMaintenance.takeObjectOutOfMaintenance(maintenance);
            }
        }
    }
}
