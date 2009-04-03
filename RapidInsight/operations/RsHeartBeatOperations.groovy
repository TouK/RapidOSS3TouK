public class RsHeartBeatOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{
	public static void configureHeartBeatMonitoring(name, interval){
		RsHeartBeat.add(objectName:name,interval:interval)
	}
	
	public static void recordHeartBeat(logger, systemName) {
		def heartbeat = RsHeartBeat.get(objectName:systemName)
		logger.info("Recording heartbeat for: $systemName")
		if(heartbeat!=null){
		    def props = [:]
		    props.lastChangedAt = new Date().getTime()
		    props.consideredDownAt = props.lastChangedAt + (heartbeat.interval * 1000)
		    heartbeat.update(props)
		    clearEvent(logger, systemName)
		}
	}
	
	private static void clearEvent(logger, systemName){
		def eventName = "${systemName}_Down"
		def event = RsEvent.get(name:eventName)
		if(event!=null){
			logger.info("Clearing event: $eventName")
			event.clear()
		}
	}

	public static checkHeartBeat(logger){
		def currentTime = new Date().getTime() 
		def after = RsHeartBeat.search("consideredDownAt:{* TO $currentTime}")
		logger.info("Found heartbeat item size: $after.total")
		after.results.each {
		    def eventProps = [name:"${it.objectName}_Down", elementName:it.objectName]
		    logger.info("Creating event: ${eventProps.name}")
		    RsEvent.notify(eventProps)
		}
	}
}
    