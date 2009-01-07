/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Jan 5, 2009
 * Time: 5:29:28 PM
 * To change this template use File | Settings | File Templates.
 */
import auth.RsUser
import search.SearchQuery

def createIdLookup=RsLookup.get(name:"emailGeneratorMaxEventCreateId")
if(createIdLookup==null)
{
    def maxEventId=0
    def maxEvent=RsEvent.search("alias:*",[max:1,sort:"id",order:"desc"]).results[0]
    if(maxEvent!=null)
    {
        maxEventId=Long.valueOf(maxEvent.id)+1
    }
    createIdLookup=RsLookup.add(name:"emailGeneratorMaxEventCreateId",value:maxEventId)
}
def maxCreateId=createIdLookup.value


def clearIdLookup=RsLookup.get(name:"emailGeneratorMaxEventClearId")
if(clearIdLookup==null)
{
    def maxEventId=0
    def maxEvent=RsHistoricalEvent.search("alias:*",[max:1,sort:"id",order:"desc"]).results[0]
    if(maxEvent!=null)
    {
        maxEventId=Long.valueOf(maxEvent.id)+1
    }
    clearIdLookup=RsLookup.add(name:"emailGeneratorMaxEventClearId",value:maxEventId)
}
def maxClearId=clearIdLookup.value

def users=RsUser.list()
users.each{ user ->
    def userId=user.id;
    def destination="abdurrahim";
    logger.debug("Searching RsMessageRule for userId:${userId}, destination is:${destination}");
    
    def createSearchQueryIds="(id:0)";
    def clearSearchQueryIds="(id:0)";
    def userRules=RsMessageRule.searchEvery("userId:${userId} AND destinationType:\"email\"")
    userRules.each{
        createSearchQueryIds +=  "OR (id:${it.searchQueryId})"
        if(it.clearAction==true)
        {
            clearSearchQueryIds +=  "OR (id:${it.searchQueryId})"
        }
    }

    def delay=0;
    def createQueries=SearchQuery.searchEvery(createSearchQueryIds)
    def createEventQuery="(id:0)  "

    createQueries.each {
        createEventQuery +=  "OR (${it.query})"
    }

    //process Event Creates
    def createQuery = " (${createEventQuery}) AND id:[${maxCreateId} TO *]"

    logger.debug("Seaching RsEvent, for userid: ${userId}  with createQuery : ${createQuery}")

    def createdEvents=RsEvent.searchEvery(createQuery,[sort:"id", order:"asc"])
    def date=new Date()
    def now=date.getTime();

    createdEvents.each{ event ->
        RsMessage.addEventCreateEmail(logger,event,destination,delay);
        maxCreateId=Long.valueOf(event.id)+1;
    }

    createIdLookup.update(value:String.valueOf(maxCreateId))

    //process delayingMessages which has exceeded delay time
    RsMessage.processDelayedEmails(logger)



    //process Event Clears, HistoricalEvent Creates

    def clearQueries=SearchQuery.searchEvery(clearSearchQueryIds)
    def clearEventQuery="(id:0)  "

    clearQueries.each {
        clearEventQuery +=  "OR (${it.query})"
    }



    def clearQuery = " (${clearEventQuery}) AND id:[${maxClearId} TO *]"
    def clearedEvents=RsHistoricalEvent.searchEvery(clearQuery,[sort:"activeId", order:"asc"])

    logger.debug("Searching RsHistoricalEvent, for userid: ${userId} with clearQuery : ${clearQuery}")

    clearedEvents.each{ event ->
          RsMessage.addEventClearEmail(logger,event,destination)
          maxClearId=Long.valueOf(event.id)+1;
    }
    clearIdLookup.update(value:String.valueOf(maxClearId))
}
