/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Jan 5, 2009
 * Time: 5:29:28 PM
 * To change this template use File | Settings | File Templates.
 */
RsMessage.removeAll();

def delay=6000;
def lastMaxId=0

def queries=search.SearchQuery.searchEvery("id:38 OR id:40")
def eventQuery="(id:0)  "

queries.each {
    eventQuery +=  "OR (${it.query})"
}
eventQuery = " (${eventQuery}) AND id:[${lastMaxId} TO *]"

logger.info("eventQuery is ${eventQuery}")

def events=RsEvent.searchEvery(eventQuery,[sort:"id", order:"asc"])
def date=new Date()
def now=date.getTime();

events.each{ event ->
    now=date.getTime();
    RsMessage.add(eventId:event.id,destination:"abdurrahim",destinationType:"email",insertedAt:now,sendAfter:now+delay,state:0,action:"create")
}

now=date.getTime();
def delayingMessages=RsMessage.searchEvery("state:0 AND sendAfter:[0 TO ${now}]")
delayingMessages.each{
    it.update(state:1)
    logger.debug("Updated delaying message with id ${it.id}, changed state to 1");
}
