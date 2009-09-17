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
package message

import org.codehaus.groovy.grails.commons.ApplicationHolder

public class RsMessageOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{
    public static void processDelayedMessages()
    {
        def now = (new Date()).getTime();
        def delayingMessages = RsMessage.searchEvery("state:${RsMessage.STATE_IN_DELAY} AND sendAfter:[0 TO ${now}]")
        delayingMessages.each {
            it.update(state: RsMessage.STATE_READY)
            getLogger().info("Updated delaying message with id ${it.id}, changed state to 1. Message : ${it.asMap()}");
        }
    }
    public static RsMessage addEventCreateMessage(Map event, String destinationType, String destination, Long delay)
    {
        def now = (new Date()).getTime();
        def state = RsMessage.STATE_IN_DELAY
        if (delay < 1)
        {
            state = RsMessage.STATE_READY;
        }
        def message = RsMessage.add(eventId: event.id, destination: destination, insertedAt: now, sendAfter: now + (delay * 1000), state: state, destinationType: destinationType, eventType: RsMessage.EVENT_TYPE_CREATE)
        if (message.hasErrors())
        {
            getLogger().warn("Error occured while adding RsMessage. Reason : ${message.errors}")
        }
        else
        {
            getLogger().info("Added create message for event with id ${event.id}. Message: ${message.asMap()}");
        }
        return message

    }
    public static RsMessage addEventClearMessage(Map historicalEvent, String destinationType, String destination)
    {
        def now = (new Date()).getTime();
        def state = RsMessage.STATE_READY;
        def message = null;

        def createMessage = RsMessage.get([eventId: historicalEvent.activeId, eventType: RsMessage.EVENT_TYPE_CREATE, destination: destination, destinationType: destinationType])
        getLogger().debug("Checking whether create event exists");
        // if there is no create message then we will skip the clear message
        if (createMessage != null) {
            // if create message is still in delay we will skip clear message and update create Message, create message will also not be sent by this way

            if (createMessage.state == RsMessage.STATE_IN_DELAY)
            {
                createMessage.update(state: RsMessage.STATE_ABORT)
                getLogger().debug("Skipped clear message, Updated create message state as 2, for event ${historicalEvent.activeId} since cleared happened before create delay exceeded. CreateMessage: ${createMessage.asMap()}");
            }
            else
            {
                message = RsMessage.add(eventId: historicalEvent.activeId, destination: destination, destinationType: destinationType, insertedAt: now, state: state, eventType: RsMessage.EVENT_TYPE_CLEAR)
                if (message.hasErrors())
                {
                    getLogger().warn("Error occured while adding RsMessage. Reason : ${message.errors}")
                }
                else
                {
                    getLogger().info("Added clear message for event with id ${historicalEvent.activeId}. Message: ${message.asMap()}");
                }
            }

        }
        else {
            getLogger().debug("Skipped clear message for event with id ${historicalEvent.activeId} . No create message exists for event ")
        }
        return message
    }

    public def retrieveEvent()
    {
       def event=null;

       if(eventType==RsMessage.EVENT_TYPE_CREATE )
       {
           event=_loadDomainClass("RsEvent").get(id:eventId);
       }
       else
       {
           event=_loadDomainClass("RsHistoricalEvent").search("activeId:${eventId}").results[0];
       }

       return event;
    }
    private def _loadDomainClass(domainClassName)
    {
        return ApplicationHolder.application.getDomainClass(domainClassName).clazz;
    }
}
