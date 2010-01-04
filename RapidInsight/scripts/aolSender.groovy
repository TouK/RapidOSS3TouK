/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 23, 2008
 * Time: 2:23:36 PM
 * To change this template use File | Settings | File Templates.
 */

import connector.NotificationConnector;
import message.RsMessage


def templatePath="grails-app/templates/message/aolTemplate.gsp";

def connector=NotificationConnector.get(name:staticParamMap?.connectorName);
def ds=connector?.ds;

if(connector==null || ds==null)
{
    logger.warn("Connector specified does not exist");
    return;
}

def destinationType=connector.name;


// Process RsMessage instances with destinationType
def messages=RsMessage.search("state:${RsMessage.STATE_READY} AND destinationType:${destinationType.exactQuery()}", [sort: "id",order:"asc",max:100]).results;

messages.each{ message ->

    def event=message.retrieveEvent();
    if(event!=null)
    {
        //////// MESSAGE SENDING PART ////////////////////////////////////////////
        ////// Modify the code below to execute your action /////////////////////
        logger.debug("Will send message about RsEvent : ${event.name}");
        try{
            def templateParams=[event:event,message:message]
            def messageContent=application.RapidApplication.getUtility("RsTemplate").render(templatePath,templateParams);
            ds.sendMessage(message.destination,messageContent)
            logger.debug("Sended message about RsEvent: ${event.name}")
            message.update(state:RsMessage.STATE_SENT,sendAt:Date.now());
            logger.debug("Updated state of message as 3,with eventId ${message.eventId}")
        }
        catch(e)
        {
            logger.warn("Error occured while sending message.Reason ${e}");
        }
        ////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////

    }
    else
    {
        message.update(state:RsMessage.STATE_NOT_EXISTS,sendAt:Date.now());
        logger.warn("RsEvent/RsHistoricalEvent with eventId ${message.eventId} does not exist. Will not send message. Updated state of message as 4");
    }

}

return "ended"

