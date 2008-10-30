import org.apache.log4j.Logger;

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
		event = RsRiEvent.add(eventProps)
		
		if (!event.hasErrors()) {
            RsEventJournal.add(eventId:event.id,eventName:event.eventName,rsTime:new Date(),details:"Created the event")
		}
		else
        {
           Logger.getRootLogger().warn("Could not add RsRiEvent ${eventProps} (skipping RsEventJournal add), Reason ${event.errors}");           
        }

		return event;
	}

}
