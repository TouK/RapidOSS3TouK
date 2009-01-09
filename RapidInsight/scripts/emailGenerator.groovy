/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Jan 5, 2009
 * Time: 5:29:28 PM
 * To change this template use File | Settings | File Templates.
 */
import auth.RsUser
import search.SearchQuery
import message.RsMessageRule
import message.RsMessage

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
def maxCreateId=Long.valueOf(createIdLookup.value)


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
def maxClearId=Long.valueOf(clearIdLookup.value)

def users=RsUser.list()
users.each{ user ->
    def userId=user.id;
    def destination=user.email;
    logger.debug("Going to search RsMessageRule for userId:${userId}");
    if(destination!=null && destination!="")
    {
        logger.debug("Searching RsMessageRule for userId:${userId}, destination is:${destination}");


        //processing for RsEvent creates
        //note that we use maxCreateId for search , and use newMaxCreateId to save the last processed Event
        def newMaxCreateId=maxCreateId;
        def createRules=RsMessageRule.searchEvery("userId:${userId} AND destinationType:\"${RsMessage.EMAIL}\" AND enabled:true")
        createRules.each{ rule ->
            def delay=rule.delay
            def searchQuery=SearchQuery.get(id:rule.searchQueryId)
            if(searchQuery)
            {
                def createQuery = " (${searchQuery.query}) AND id:[${maxCreateId} TO *]"

                logger.debug("Seaching RsEvent, for userid: ${userId}  with createQuery : ${createQuery}")
                def createdEvents=RsEvent.getPropertyValues(createQuery,["id"],[sort:"id", order:"asc",max:1000])
                def date=new Date()
                def now=date.getTime();

                createdEvents.each{ event ->
                    RsMessage.addEventCreateEmail(logger,event,destination,delay);
                    if(newMaxCreateId<Long.valueOf(event.id)+1)
                    {
                        newMaxCreateId=Long.valueOf(event.id)+1;
                    }
                }
            }
            else
            {
                logger.debug("SearchQuery with id ${it.searchQueryId} does not exist")
            }

        }
        if(newMaxCreateId>maxCreateId)
        {
            createIdLookup.update(value:String.valueOf(newMaxCreateId))
        }

        //process delayingMessages which has exceeded delay time
        RsMessage.processDelayedEmails(logger)



        //process Event Clears, HistoricalEvent Creates
        //note that we use maxClearId for search , and use newMaxClearId to save the last processed Event
        def newMaxClearId=maxClearId;
        def clearRules=RsMessageRule.searchEvery("userId:${userId} AND destinationType:\"${RsMessage.EMAIL}\" AND clearAction:true AND enabled:true")
        clearRules.each{ rule ->
            def searchQuery=SearchQuery.get(id:rule.searchQueryId)
            if(searchQuery)
            {
                def clearQuery = " (${searchQuery.query}) AND id:[${maxClearId} TO *]"

                logger.debug("Searching RsHistoricalEvent, for userid: ${userId} with clearQuery : ${clearQuery}")
                def clearedEvents=RsHistoricalEvent.getPropertyValues(clearQuery,["id","activeId"],[sort:"id", order:"asc",max:1000])
                def date=new Date()
                def now=date.getTime();

                clearedEvents.each{ event ->
                      RsMessage.addEventClearEmail(logger,event,destination)
                        if(newMaxClearId<Long.valueOf(event.id)+1)
                        {
                            newMaxClearId=Long.valueOf(event.id)+1;
                        }

                }
            }
            else
            {
                logger.debug("SearchQuery with id ${it.searchQueryId} does not exist")
            }

        }
        if(newMaxClearId>maxClearId)
        {
            clearIdLookup.update(value:String.valueOf(newMaxClearId))
        }

    }
    else
    {
        logger.warn("Skipping search RsMessageRule for userId:${userId}. User does not have an email");
    }

}
