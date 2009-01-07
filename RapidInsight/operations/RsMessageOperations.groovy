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

import org.apache.log4j.Logger

public class RsMessageOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{
    static void processDelayedEmails(Logger externalLogger)
    {
        def now=(new Date()).getTime();
        def delayingMessages=RsMessage.searchEvery("state:0 AND sendAfter:[0 TO ${now}] AND destinationType:\"email\"")
        delayingMessages.each{
            it.update(state:1)            
            externalLogger.info("Updated delaying message with id ${it.id}, changed state to 1. Message : ${it.asMap()}");
        }
    }
    static void addEventCreateEmail(Logger externalLogger,RsEvent event,String destination,Long delay)
    {
        def now=(new Date()).getTime();
        def state=0
        if(delay<1)
        {
           state=1; 
        }
        def message=RsMessage.add(eventId:event.id,destination:destination,insertedAt:now,sendAfter:now+delay,state:state,destinationType:"email",action:"create")         
        if(message.hasErrors())
        {
            externalLogger.warn("Error occured while adding RsMessage. Reason : ${message.errors}")
        }
        else
        {
            externalLogger.info("Added create message for event with id ${event.id}. Message: ${message.asMap()}");
        }

    }
    static void addEventClearEmail(Logger externalLogger,RsHistoricalEvent event,String destination)
    {
        def now=(new Date()).getTime();
        def state=1          
        def message=null;
        
        def createMessage=RsMessage.get([eventId:event.activeId,action:"create",destination:destination,destinationType:"email"])
        externalLogger.debug("Checking whether create event exists");
        // if there is no create message then we will skip the clear message
        if(createMessage != null ){
          // if create message is still in delay we will skip clear message and update create Message, create message will also not be sent by this way

          if(createMessage.state==0)
          {
            createMessage.update(state:2)
            externalLogger.debug("Skipped clear message, Updated create message state as 2, for event ${event.activeId} since cleared happened before create delay exceeded. CreateMessage: ${createMessage.asMap()}");
          }
          else
          {
            message=RsMessage.add(eventId:event.activeId,destination:"abdurrahim",destinationType:"email",insertedAt:now,state:state,action:"clear")
            if(message.hasErrors())
            {
                externalLogger.warn("Error occured while adding RsMessage. Reason : ${message.errors}")
            }
            else
            {
                externalLogger.info("Added clear message for event with id ${event.activeId}. Message: ${message.asMap()}");
            } 
          }

        }
        else{
            externalLogger.debug("Skipped clear message for event with id ${event.activeId} . No create message exists for event ")
        }
    }
}
