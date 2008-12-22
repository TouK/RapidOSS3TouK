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
		def journals = RsEventJournal.searchEvery("eventId:${id}")
		journals.each{
		    it.eventId = historicalEvent.id
		}
		remove()
	}	

}
