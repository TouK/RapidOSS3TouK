class RsEventOperations  extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {
	
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
		event = RsEvent.add(eventProps)
		RsEventJournal.add(eventId:event.id,eventName:event.eventName,rstime:new Date())
	}
	
	public void clear() {
		props = getAsMap();
		props.lastClearedAt = new Date()
		props.active = "false"
		RsEventJournal.add(eventId:id,eventName:"cleared",rstime:new Date())
		def historicalEvent = RsHistoricalEvent.add(props)
		
		def journals = RsEventJournal.search("eventId:${id}").results
		journals.each{
		    it.eventId = historicalEvent.id
		}
		remove()
	}	
	
	public void acknowledge(boolean action, userName){
		if(acknowledged != action){
			if(action){
				RsEventJournal.add(eventId:id, name:"acknowledged", rsTime:new Date(), details:"Acknowledged by ${userName}")
			}
			else{
				RsEventJournal.add(eventId:id, name:"unacknowledged", rsTime:new Date(), details:"UnAcknowledged by ${userName}")
			}
		}
		acknowledged = action
		lastChangedAt = new Date()
	}	

	public void setOwnership(boolean action, userName) {
    	if(action)        {
                RsEventJournal.add(eventId:id, name:"TakeOwnership", rsTime:new Date(), details:"TakeOwnership by ${userName}")
        }
        else{
                RsEventJournal.add(eventId:id, name:"ReleaseOwnership", rsTime:new Date(), details:"RelaseOwnership by ${userName}")
        }
                
		if(action){
			owner = userName
		}
		else{
			owner = ""
		}
		lastChangedAt = new Date()
	}
	
	public void addToJournal(name, details){
		RsEventJournal.add(eventId:id, name:name, rsTime:new Date(), details:details)
	}
}
