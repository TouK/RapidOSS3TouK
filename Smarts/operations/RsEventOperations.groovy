class RsEventOperations  extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {
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
            owner = userName
        }
        else{
            RsEventJournal.add(eventId:id, name:"ReleaseOwnership", rsTime:new Date(), details:"RelaseOwnership by ${userName}")
            owner = ""
        }
		lastChangedAt = new Date()
	}
	
	public void addToJournal(name, details){
		RsEventJournal.add(eventId:id, name:name, rsTime:new Date(), details:details)
	}
	
	public void addToJournal(name){
		RsEventJournal.add(eventId:id, name:name, rsTime:new Date())
	}
	
	public void addToJournal(Map props){
		def propsTemp = [:];
		propsTemp.putAll(props);
		propsTemp.put("eventId",id);
		RsEventJournal.add(propsTemp);
	}
}
