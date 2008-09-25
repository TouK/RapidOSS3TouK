class RsRiEventOperations extends RsEventOperations
{

	static notify(Map originalEventProps) {
		def eventProps = [:]
		eventProps.putAll(originalEventProps)
		eventProps.active = "true"
		def event  = RsEvent.get(name:eventProps.name)
		if (event==null){
			eventProps.firstNotifiedAt = new Date()	
			event.count = 0;
		}
		eventProps.lastNotifiedAt = new Date()
		event.lastChangedAt = new Date()
		event.count ++
		event = RsRiEvent.add(eventProps)
		RsEventJournal.add(eventId:event.id,eventName:event.eventName,rsTime:new Date())
	}
}
