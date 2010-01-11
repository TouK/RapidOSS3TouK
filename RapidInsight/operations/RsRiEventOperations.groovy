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

public class RsRiEventOperations  extends RsEventOperations {
	public static notify(Map originalEventProps) {
		return _notify(RsRiEvent,originalEventProps);
	}
	public static _notify(Class eventModel,Map originalEventProps)
    {
         _notify(eventModel, originalEventProps, true)
    }
    public static _notify(Class eventModel,Map originalEventProps, createJournal)
    {
       def eventProps = [:]
		eventProps.putAll(originalEventProps)
		def event = RsEvent.get(name:eventProps.name)
		def now=Date.now();
		def journalDetails="";
		if (event == null){
            if(!eventProps.containsKey("createdAt"))
            {
                eventProps.createdAt = now;
            }
			journalDetails=RsEventJournal.MESSAGE_CREATE;
		} else {
            if(!eventProps.containsKey("count"))
            {
                eventProps.count = event.count + 1;
            }
			journalDetails=RsEventJournal.MESSAGE_UPDATE;
		}

        if(!eventProps.containsKey("changedAt"))
        {
            eventProps.changedAt = now
        }

		event = eventModel.add(eventProps)

		if (!event.hasErrors()) {
            if(createJournal){
                def eventName = journalDetails == RsEventJournal.MESSAGE_CREATE ? "created" : "updated"
                RsEventJournal.add(eventId:event.id,eventName:eventName,rsTime:Date.toDate(now),details:journalDetails)    
            }

		}
		else
        {
           getLogger().warn("Could not add RsRiEvent ${eventProps} (skipping RsEventJournal add), Reason ${event.errors}");
        }

        
        return event;
    }
	public static Class historicalEventModel()
    {
        return RsRiHistoricalEvent;
    }
   

}
