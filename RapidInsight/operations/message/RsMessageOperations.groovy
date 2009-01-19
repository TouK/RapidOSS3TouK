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
import org.apache.log4j.Logger

public class RsMessageOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{
    public static void processDelayedEmails(Logger externalLogger)
    {
        def now=(new Date()).getTime();
        def delayingMessages=RsMessage.searchEvery("state:${RsMessage.STATE_IN_DELAY} AND sendAfter:[0 TO ${now}] AND destinationType:\"${RsMessage.EMAIL}\"")
        delayingMessages.each{
            it.update(state:RsMessage.STATE_READY)
            externalLogger.info("Updated delaying message with id ${it.id}, changed state to 1. Message : ${it.asMap()}");
        }
    }
    public static RsMessage addEventCreateEmail(Logger externalLogger,Map event,String destination,Long delay)
    {
        def now=(new Date()).getTime();
        def state=RsMessage.STATE_IN_DELAY
        if(delay<1)
        {
           state=RsMessage.STATE_READY;
        }
        def message=RsMessage.add(eventId:event.id,destination:destination,insertedAt:now,sendAfter:now+(delay*1000),state:state,destinationType:RsMessage.EMAIL,action:"create")
        if(message.hasErrors())
        {
            externalLogger.warn("Error occured while adding RsMessage. Reason : ${message.errors}")
        }
        else
        {
            externalLogger.info("Added create message for event with id ${event.id}. Message: ${message.asMap()}");
        }
        return message

    }
    public static RsMessage addEventClearEmail(Logger externalLogger,Map historicalEvent,String destination)
    {
        def now=(new Date()).getTime();
        def state=RsMessage.STATE_READY;
        def message=null;

        def createMessage=RsMessage.get([eventId:historicalEvent.activeId,action:"create",destination:destination,destinationType:RsMessage.EMAIL])
        externalLogger.debug("Checking whether create event exists");
        // if there is no create message then we will skip the clear message
        if(createMessage != null ){
          // if create message is still in delay we will skip clear message and update create Message, create message will also not be sent by this way

          if(createMessage.state==0)
          {
            createMessage.update(state:2)
            externalLogger.debug("Skipped clear message, Updated create message state as 2, for event ${historicalEvent.activeId} since cleared happened before create delay exceeded. CreateMessage: ${createMessage.asMap()}");
          }
          else
          {
            message=RsMessage.add(eventId:historicalEvent.activeId,destination:destination,destinationType:RsMessage.EMAIL,insertedAt:now,state:state,action:"clear")
            if(message.hasErrors())
            {
                externalLogger.warn("Error occured while adding RsMessage. Reason : ${message.errors}")
            }
            else
            {
                externalLogger.info("Added clear message for event with id ${historicalEvent.activeId}. Message: ${message.asMap()}");
            }
          }

        }
        else{
            externalLogger.debug("Skipped clear message for event with id ${historicalEvent.activeId} . No create message exists for event ")
        }
        return message
    }
}
