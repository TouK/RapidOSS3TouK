import org.apache.log4j.Logger;

class RsRiEventOperations  extends RsEventOperations {
	static notify(Map originalEventProps) {
		def eventProps = [:]
		eventProps.putAll(originalEventProps)
		def event = RsEvent.get(name:eventProps.name)
		if (event == null){
			eventProps.createdAt = Date.now()
		} else {
			eventProps.count = event.count + 1;
		}
		eventProps.changedAt = Date.now()
		event = RsRiEvent.add(eventProps)
		
		if (!event.hasErrors()) {
            RsEventJournal.add(eventId:event.id,eventName:event.identifier,rsTime:new Date(),details:"Created the event")
		}
		else
        {
           Logger.getRootLogger().warn("Could not add RsRiEvent ${eventProps} (skipping RsEventJournal add), Reason ${event.errors}");           
        }

		return event;
	}

	public void clear() {
		def props = asMap();
		props.clearedAt = Date.now()
		RsEventJournal.add(eventId:id,eventName:"cleared",rsTime:new Date())
		def historicalEvent = RsRiHistoricalEvent.add(props)
		def journals = RsEventJournal.search("eventId:${id}").results
		journals.each{
		    it.eventId = historicalEvent.id
		}
		remove()
	}	

}
