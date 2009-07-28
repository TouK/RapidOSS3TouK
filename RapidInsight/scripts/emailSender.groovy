/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 23, 2008
 * Time: 2:23:36 PM
 * To change this template use File | Settings | File Templates.
 */

import connector.EmailConnector
import message.RsMessage

def destinationType=RsMessage.EMAIL;
def templatePath="grails-app/templates/email/emailTemplate.gsp";
//should be a valid email address
def from="IFountainEmailSender@ifountain.com"

def date=new Date();

def ds=EmailConnector.get(name:staticParamMap?.connectorName)?.ds
if(ds!=null)
{

    // Process RsMessage instances with destinationType
    def messages=RsMessage.search("state:${RsMessage.STATE_READY} AND destinationType:${destinationType.exactQuery()}", [sort: "id",order:"asc",max:100]).results;

    messages.each{ message ->
        
        def event=null;
        if(message.action==RsMessage.ACTION_CREATE )
        {
            event=RsEvent.get(id:message.eventId);
        }
        else
        {
            event=RsHistoricalEvent.search("activeId:${message.eventId}").results[0];
        }

        if(event!=null)
        {

            def eventProps=event.asMap();

            //////// EMAIL SENDING PART ////////////////////////////////////////////
            ////// Modify the code below to execute your action /////////////////////
            logger.debug("Will send email about RsEvent : ${eventProps}");

            def templateParams=[eventParams:eventProps]
            def emailParams=[:]
            emailParams.from=from
            emailParams.to=message.destination
            emailParams.subject= ( message.action  == RsMessage.ACTION_CREATE ? "Event Created" : "Event Cleared" )
            emailParams.template=templatePath
            emailParams.templateParams=templateParams
            emailParams.contentType="text/html"

            try{
                ds.sendEmail(emailParams)
                logger.debug("Sended email about RsEvent: ${eventProps}")
                message.update(state:RsMessage.STATE_SENT,sendAt:date.getTime());
                logger.debug("Updated state of message as 3,with eventId ${message.eventId}")
            }
            catch(e)
            {
                logger.warn("Error occured while sending email.Reason ${e}",e);
            }
            ////////////////////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////

        }
        else
        {
            message.update(state:RsMessage.STATE_NOT_EXISTS,sendAt:date.getTime());
            logger.warn("RsEvent/RsHistoricalEvent with eventId ${message.eventId} does not exist. Will not send email. Updated state of message as 4");
        }

    }
}
else{
    logger.warn("Connector specified does not exist");
}

return "ended"

