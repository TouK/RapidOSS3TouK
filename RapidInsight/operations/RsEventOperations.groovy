/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
class RsEventOperations  extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {
	public void clear() {
		def props = asMap();
		props.clearedAt = Date.now()
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
		changedAt = Date.now()
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
		changedAt = Date.now()
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
