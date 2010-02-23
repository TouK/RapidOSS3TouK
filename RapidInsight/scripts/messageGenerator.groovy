import auth.ChannelUserInformation
import auth.Role
import auth.RsUser
import message.RsMessage
import message.RsMessageRule
import search.SearchQuery

/**
* Created by IntelliJ IDEA.
* User: iFountain
* Date: Jan 5, 2009
* Time: 5:29:28 PM
* To change this template use File | Settings | File Templates.
*/


DESTINATIONS = RsMessageRule.getDestinations();

def createTimeLookup = RsLookup.get(name: "messageGeneratorMaxEventCreateTime")
if (createTimeLookup == null)
{
    def maxEventTime = 0
    def maxEvent = RsEvent.search("alias:*", [max: 1, sort: "rsInsertedAt", order: "desc"]).results[0]
    if (maxEvent != null)
    {
        maxEventTime = Long.valueOf(maxEvent.rsInsertedAt) + 1
    }
    createTimeLookup = RsLookup.add(name: "messageGeneratorMaxEventCreateTime", value: maxEventTime)
}
def maxCreateTime = Long.valueOf(createTimeLookup.value)


def clearTimeLookup = RsLookup.get(name: "messageGeneratorMaxEventClearTime")
if (clearTimeLookup == null)
{
    def maxEventTime = 0
    def maxEvent = RsHistoricalEvent.search("alias:*", [max: 1, sort: "rsInsertedAt", order: "desc"]).results[0]
    if (maxEvent != null)
    {
        maxEventTime = Long.valueOf(maxEvent.rsInsertedAt) + 1
    }
    clearTimeLookup = RsLookup.add(name: "messageGeneratorMaxEventClearTime", value: maxEventTime)
}
def maxClearTime = Long.valueOf(clearTimeLookup.value)

//process delayingMessages which has exceeded delay time
RsMessage.processDelayedMessages()
def users = RsUser.list()
users.each {user ->
    withSession(user.username) {
        def userId = user.id;
        DESTINATIONS.each {destinationInfo ->


            def destinationType=destinationInfo.name;
            def channelType=destinationInfo.channelType;
            
            logger.debug("Going to search RsMessageRule for userId:${userId} ${user.username} with destinationType:${destinationType}");
            
            def createRules = RsMessageRule.searchEvery("userId:\"${userId}\" AND destinationType:${destinationType} AND enabled:true")
            def clearRules = RsMessageRule.searchEvery("userId:\"${userId}\" AND destinationType:${destinationType} AND sendClearEventType:true AND enabled:true")
            if(createRules.size()>0 || clearRules.size>0)
            {
                def destination=RsMessageRule.getUserDestinationForChannel(user,channelType);
                try{
                    RsMessageRule.validateUserDestinationForChannel(user,destination,channelType);
                    if(!RsMessageRule.isChannelType(channelType))
                    {
                         destination="admin_destination";
                    }
                    if(destination == null)
                    {
                        throw new Exception("Critical Error can not find destination for ${destinationType}");
                    }
                }
                catch(e)
                {
                   logger.warn("Skipping search RsMessageRule for userId:${userId}. Reason ${e.getMessage()}");
                }



                if (destination!=null)
                {
                    logger.debug("Processing RsMessageRule for userId:${userId}, destination is:${destination}");
                    //processing for RsEvent creates
                    //note that we use maxCreateTime for search , and use newMaxCreateTime to save the last processed Event
                    def newMaxCreateTime = maxCreateTime;
                    createRules.each {rule ->
                        def delay = rule.delay
                        def searchQuery = SearchQuery.get(id: rule.searchQueryId)
                        if (searchQuery)
                        {
                            def createQuery = " (${searchQuery.query}) AND rsInsertedAt:[${maxCreateTime} TO *]"


                            def eventClass=RsEvent;
                            if(searchQuery.searchClass)
                            {
                                eventClass= application.RapidApplication.getModelClass(searchQuery.searchClass);
                            }
                            logger.debug("Seaching ${eventClass.name}, for userid: ${userId}  with createQuery : ${createQuery}")

                            def createdEvents = eventClass.getPropertyValues(createQuery, ["id","rsInsertedAt"], [sort: "rsInsertedAt", order: "asc", max: 1000])
                            def date = new Date()
                            def now = date.getTime();

                            createdEvents.each {event ->
                                RsMessage.addEventCreateMessage(event, destinationType, destination, delay);
                                if (newMaxCreateTime < Long.valueOf(event.rsInsertedAt) + 1)
                                {
                                    newMaxCreateTime = Long.valueOf(event.rsInsertedAt) + 1;
                                }
                            }
                        }
                        else
                        {
                            logger.debug("SearchQuery with id ${rule.searchQueryId} does not exist")
                        }

                    }
                    if (newMaxCreateTime > maxCreateTime)
                    {
                        createTimeLookup.update(value: String.valueOf(newMaxCreateTime))
                    }


                    //process Event Clears, HistoricalEvent Creates
                    //note that we use maxClearTime for search , and use newMaxClearTime to save the last processed Event
                    def newMaxClearTime = maxClearTime;
                    clearRules.each {rule ->
                        def searchQuery = SearchQuery.get(id: rule.searchQueryId)
                        if (searchQuery)
                        {
                            def clearQuery = " (${searchQuery.query}) AND rsInsertedAt:[${maxClearTime} TO *]"

                            def eventClass=RsHistoricalEvent;
                            if(searchQuery.searchClass)
                            {
                                eventClass= application.RapidApplication.getModelClass(searchQuery.searchClass).historicalEventModel();
                            }

                            logger.debug("Searching ${eventClass.name}, for userid: ${userId} with clearQuery : ${clearQuery}")
                            def clearedEvents = eventClass.getPropertyValues(clearQuery, ["id", "activeId","rsInsertedAt"], [sort: "rsInsertedAt", order: "asc", max: 1000])
                            def date = new Date()
                            def now = date.getTime();

                            clearedEvents.each {event ->
                                RsMessage.addEventClearMessage(event, destinationType, destination)
                                if (newMaxClearTime < Long.valueOf(event.rsInsertedAt) + 1)
                                {
                                    newMaxClearTime = Long.valueOf(event.rsInsertedAt) + 1;
                                }

                            }
                        }
                        else
                        {
                            logger.debug("SearchQuery with id ${rule.searchQueryId} does not exist")
                        }

                    }
                    if (newMaxClearTime > maxClearTime)
                    {
                        clearTimeLookup.update(value: String.valueOf(newMaxClearTime))
                    }

                }
            }
            else
            {
                logger.debug("No rules / enabled rules found for user userId:${userId} ${user.username}  with destinationType:${destinationType}");
            }
        }
    }
}

