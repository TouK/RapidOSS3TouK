/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 23, 2008
 * Time: 2:23:36 PM
 * To change this template use File | Settings | File Templates.
 */
import connection.EmailConnection
import datasource.EmailDatasource


def templatePath="grails-app/views/emailTemplate.gsp";
def from="mustafa"

def con=EmailConnection.add(name:"emailcon",smtpHost:"192.168.1.100",smtpPort:25,username:"testaccount",userPassword:"123",protocol:EmailConnection.SMTP)
if(con.hasErrors())
{
    return "Error occured. Reason"+ con.errors
}
def emailDs=EmailDatasource.add(name:"emailds",connection:con)
if(emailDs.hasErrors())
{
    return "Error occured. Reason"+ emailDs.errors
}


def ds=EmailDatasource.get(name:"emailds")


def messages=RsMessage.searchEvery("alias:*", [sort: "id"]);

messages.each{ message ->
	RsEvent event=RsEvent.get(id:message.eventId);
	if(event!=null)
	{

		def eventParams=event.asMap();
		logger.debug("Will send email about RsEvent : ${eventParams}");

		def templateParams=[eventParams:eventParams]
		def emailParams=[:]
		emailParams.from=from
		emailParams.to=message.to
		emailParams.subject= ( event.createdAt  == event.changedAt ? "Event Created" : "Event Updated" )
		emailParams.template=templatePath
		emailParams.templateParams=templateParams
		emailParams.contentType="text/html"

		try{
			ds.sendEmail(emailParams)
			logger.debug("Sended email about RsEvent: ${eventParams}")
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

