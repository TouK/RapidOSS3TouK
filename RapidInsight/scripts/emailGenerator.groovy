/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Jan 5, 2009
 * Time: 5:29:28 PM
 * To change this template use File | Settings | File Templates.
 */
def now= (new Date()).getTime()

def event=RsEvent.get(name:"ev1")

def mes=RsMessage.add(eventId:event.id,destination:"abdurrahim",destinationType:"email",action:"create",insertedAt:now,state:1)

if(mes.hasErrors())
{
	return mes.errors
}
return "success"