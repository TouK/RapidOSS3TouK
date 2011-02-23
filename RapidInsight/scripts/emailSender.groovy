/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 23, 2008
 * Time: 2:23:36 PM
 * To change this template use File | Settings | File Templates.
 */

import connector.NotificationConnector;
import message.RsMessage;


def templatePath="grails-app/templates/message/emailTemplate.gsp";

def connector=NotificationConnector.get(name:staticParamMap?.connectorName);
def ds=connector?.ds;

if(connector==null || ds==null)
{
    logger.warn("Connector specified does not exist");
    return;
}

def destinationType=connector.name;

//should be a valid email address
def from=ds.connection.username;

// Process RsMessage instances with destinationType
def messages=RsMessage.search("( state:${RsMessage.STATE_READY} OR state:${RsMessage.STATE_ERROR}) AND destinationType:${destinationType.exactQuery()}", [sort: "id",order:"asc",max:100]).results;

messages.each{ message ->

    def event=message.retrieveEvent();
    if(event!=null)
    {
        //////// EMAIL SENDING PART ////////////////////////////////////////////
        ////// Modify the code below to execute your action /////////////////////
        logger.debug("Will send email about RsEvent : ${event.name}");
        try{
            def templateParams=[event:event,message:message]
            def emailParams=[:]
            emailParams.from=from
            emailParams.to=message.destination
            emailParams.subject= ( message.eventType  == RsMessage.EVENT_TYPE_CREATE ? "Event Created" : "Event Cleared" )
            emailParams.body=application.RapidApplication.getUtility("RsTemplate").render(templatePath,templateParams);
            emailParams.contentType="text/html"

            ds.sendEmail(emailParams)
            logger.debug("Sended email about RsEvent: ${event.name}")
            message.recordSuccess();
            logger.debug("Updated state of message as SENT,with eventId ${message.eventId}")
        }
        catch(e)
        {
            logger.warn("Error occured while sending email.Reason ${e}");
            message.recordFailure();
        }
        ////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////

    }
    else
    {
        message.recordNotExists();
        logger.warn("RsEvent/RsHistoricalEvent with eventId ${message.eventId} does not exist. Will not send email. Updated state of message as 4");
    }

}

return "ended"

