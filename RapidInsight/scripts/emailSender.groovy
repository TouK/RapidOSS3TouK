/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 23, 2008
 * Time: 2:23:36 PM
 * To change this template use File | Settings | File Templates.
 */
import connection.EmailConnection
import datasource.EmailDatasource


def templatePath="grails-app/templates/email/emailTemplate.gsp";
def from="mustafa"

def connectionParams=[:]
connectionParams.name="emailcon"
connectionParams.smtpHost="192.168.1.100"
connectionParams.smtpPort=25
connectionParams.username="testaccount"
connectionParams.userPassword="123"
connectionParams.protocol=EmailConnection.SMTP

def con=EmailConnection.add(connectionParams)
if(con.hasErrors())
{
    return "Error occured. Reason"+ con.errors
}
def emailDs=EmailDatasource.add(name:"emailds",connection:con)
if(emailDs.hasErrors())
{
    return "Error occured. Reason"+ emailDs.errors
}


def date=new Date();

def ds=EmailDatasource.get(name:"emailds")


def messages=RsMessage.searchEvery('state:1 AND destinationType:"email"', [sort: "id"]);

messages.each{ message ->
	RsEvent event=RsEvent.get(id:message.eventId);
	if(event!=null)
	{

		def eventParams=event.asMap();
		logger.debug("Will send email about RsEvent : ${eventParams}");

		def templateParams=[eventParams:eventParams]
		def emailParams=[:]
		emailParams.from=from
		emailParams.to=message.destination
		emailParams.subject= ( message.action  == "create" ? "Event Created" : "Event Cleared" )
		emailParams.template=templatePath
		emailParams.templateParams=templateParams
		emailParams.contentType="text/html"

		try{
			ds.sendEmail(emailParams)
			logger.debug("Sended email about RsEvent: ${eventParams}")
            message.update(state:3,sendAt:date.getTime());
            logger.debug("Updated state of message as 3,with eventId ${message.eventId}")


		}
		catch(e)
		{
			logger.warn("Error occured while sending email.Reason ${e}",e);
		}


	}
	else
	{
		logger.warn("RsEvent with id ${message.eventId} does not exist. Will not send email");
	}

}
return "ended"

