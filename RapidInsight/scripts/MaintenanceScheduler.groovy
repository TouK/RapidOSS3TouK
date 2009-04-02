import java.util.Date

activateScheduledItems()
expireItems()

def activateScheduledItems(){
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
			def object = RsTopologyObject.get(name:it.objectName)
			logger.debug("activating maintenance for: ${object}")
			object?.eventsInMaintenance(true)
		}
	}
	logger.debug("END activateScheduledItems")
}

def expireItems(){
	def currentTime = new Date().getTime()
	logger.debug("current time: $currentTime")
	def nullDate = new Date(0).getTime()
	def activeItems = RsInMaintenance.search("active:true")
	logger.debug("active item count: ${activeItems.total}")
	activeItems.results.each{
		logger.debug("ending.getTime(): ${it.ending.getTime()}")
		if (it.ending.getTime()>nullDate && it.ending.getTime() <= currentTime){
			def object = RsTopologyObject.get(name:it.objectName)
			logger.debug("deactivating maintenance for: ${object}")
			object?.eventsInMaintenance(false)
			it.remove()
		}
	}	
}
