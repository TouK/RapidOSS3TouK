class RsEventOperations  extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {
	public void clear() {
		def props = asMap();
		props.remove('__operation_class__');
		props.remove('__is_federated_properties_loaded__');
		props.remove('errors');
		props.lastClearedAt = Date.now()
		props.active = "false"
		RsEventJournal.add(eventId:id,eventName:"cleared",rsTime:new Date())
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
				RsEventJournal.add(eventId:id, eventName:"acknowledged", rsTime:new Date(), details:"Acknowledged by ${userName}")
			}
			else{
				RsEventJournal.add(eventId:id, eventName:"unacknowledged", rsTime:new Date(), details:"UnAcknowledged by ${userName}")
			}
		}
		acknowledged = action
		lastChangedAt = Date.now()
	}	

	public void setOwnership(boolean action, userName) {
    	if(action)        {
            RsEventJournal.add(eventId:id, eventName:"TakeOwnership", rsTime:new Date(), details:"TakeOwnership by ${userName}")
            owner = userName
        }
        else{
            RsEventJournal.add(eventId:id, eventName:"ReleaseOwnership", rsTime:new Date(), details:"RelaseOwnership by ${userName}")
            owner = ""
        }
		lastChangedAt = Date.now()
	}
	
	public void addToJournal(name, details){
		RsEventJournal.add(eventId:id, eventName:name, rsTime:new Date(), details:details)
	}
	
	public void addToJournal(name){
		RsEventJournal.add(eventId:id, eventName:name, rsTime:new Date())
	}
	
	public void addToJournal(Map props){
		def propsTemp = [:];
		propsTemp.putAll(props);
		propsTemp.put("eventId",id);
		RsEventJournal.add(propsTemp);
	}
}
