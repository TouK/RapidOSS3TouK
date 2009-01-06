/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Jan 5, 2009
 * Time: 5:29:28 PM
 * To change this template use File | Settings | File Templates.
 */


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

def delay=0;
def queries=search.SearchQuery.searchEvery("id:38 OR id:40")
def eventQuery="(id:0)  "

queries.each {
    eventQuery +=  "OR (${it.query})"
}

//process Event Creates
def createQuery = " (${eventQuery}) AND id:[${maxCreateId} TO *]"

logger.debug("createQuery is ${createQuery}")

def createdEvents=RsEvent.searchEvery(createQuery,[sort:"id", order:"asc"])
def date=new Date()
def now=date.getTime();

createdEvents.each{ event ->
    RsMessage.addEventCreateEmail(logger,event,"abdurrahim",delay);
    maxCreateId=Long.valueOf(event.id)+1;
}

createIdLookup.update(value:String.valueOf(maxCreateId))

//process delayingMessages which has exceeded delay time
RsMessage.processDelayedEmails(logger)


//process Event Clears, HistoricalEvent Creates
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



def clearQuery = " (${eventQuery}) AND id:[${maxClearId} TO *]"
def clearedEvents=RsHistoricalEvent.searchEvery(clearQuery,[sort:"activeId", order:"asc"])

logger.debug("clearQuery is ${createQuery}")

clearedEvents.each{ event ->
      RsMessage.addEventClearEmail(logger,event,"abdurrahim")
      maxClearId=Long.valueOf(event.id)+1;
}
clearIdLookup.update(value:String.valueOf(maxClearId))

