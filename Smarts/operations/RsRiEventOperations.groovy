class RsRiEventOperations  extends RsEventOperations {
	static notify(Map originalEventProps) {
		def eventProps = [:]
		eventProps.putAll(originalEventProps)
		eventProps.active = "true"
		def event = RsEvent.get(name:eventProps.name)
		if (event == null){
			eventProps.firstNotifiedAt = Date.now() 
		} else {
			eventProps.count = event.count + 1;
		}
		eventProps.lastNotifiedAt = Date.now()
		eventProps.lastChangedAt = Date.now()
		println "eventProps: " + eventProps
		event = RsRiEvent.add(eventProps)
		if (event.hasErrors()) {
		    println event.errors
		}
		RsEventJournal.add(eventId:event.id,eventName:event.eventName,rsTime:new Date(),details:"Created the event")
		return event;
	}
}
