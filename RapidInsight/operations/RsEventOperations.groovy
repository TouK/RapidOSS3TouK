import application.RsApplication

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
public class RsEventOperations  extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {

	def beforeInsert(){
        RsApplication.getUtility("EventProcessor").eventInBeforeInsert(this.domainObject);
	}
	def beforeUpdate(params)
    {
        RsApplication.getUtility("EventProcessor").eventInBeforeUpdate(this.domainObject,params);
    }
	def afterInsert(){
        RsApplication.getUtility("EventProcessor").eventIsAdded(this.domainObject);
    }
    def afterUpdate(params){
        RsApplication.getUtility("EventProcessor").eventIsUpdated(this.domainObject,params);
    }
    def afterDelete()
    {
        RsApplication.getUtility("EventProcessor").eventIsDeleted(this.domainObject);
    }


    static notify(Map eventProps) {

        def event=RsEvent.add(eventProps);
        return event;
    }

    public void clear() {
		clear(true);
	}

	public void clear(boolean createJournal, Map extraProperties = [:]) {
		Map props = asMap();
		props.putAll (extraProperties)
		def now=Date.now();
		if(!props.clearedAt){
            props.clearedAt = now    
        }
		props.activeId = id;
		if(createJournal)
        {
		    RsEventJournal.add(eventId:id,eventName:"cleared",rsTime:new Date(now))
        }
		def historicalEvent = domainObject.historicalEventModel().'add'(props)
		remove()
		
	}
	public static Class historicalEventModel()
    {
        return RsHistoricalEvent;    
    }
	
	public void acknowledge(boolean action, userName){
        def now=Date.now();

        if(acknowledged != action){
			if(action){
				RsEventJournal.add(eventId:id, eventName:"acknowledged", rsTime:new Date(now), details:"Acknowledged by ${userName}")
				update(acknowledged:true,changedAt:now)
			}
			else{
				RsEventJournal.add(eventId:id, eventName:"unacknowledged", rsTime:new Date(now), details:"UnAcknowledged by ${userName}")
				update(acknowledged:false,changedAt:now)
			}
		}

	}	

	public void setOwnership(boolean action, userName) {
        def now=Date.now();

        if(action)        {
            RsEventJournal.add(eventId:id, eventName:"TakeOwnership", rsTime:new Date(now), details:"TakeOwnership by ${userName}")
            update(owner:userName,changedAt:now)
        }
        else{
            RsEventJournal.add(eventId:id, eventName:"ReleaseOwnership", rsTime:new Date(now), details:"ReleaseOwnership by ${userName}")
            update(owner:"",changedAt:now)
        }

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
