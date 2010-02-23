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

def createInsertedAtLookup = RsLookup.get(name: "messageGeneratorMaxEventCreateInsertedAt")
if (createInsertedAtLookup == null)
{
    def maxEventInsertedAt = 0
    def maxEvent = RsEvent.search("alias:*", [max: 1, sort: "rsInsertedAt", order: "desc"]).results[0]
    if (maxEvent != null)
    {
        maxEventInsertedAt = Long.valueOf(maxEvent.rsInsertedAt) + 1
    }
    createInsertedAtLookup = RsLookup.add(name: "messageGeneratorMaxEventCreateInsertedAt", value: maxEventInsertedAt)
}
def maxCreateInsertedAt = Long.valueOf(createInsertedAtLookup.value)


def clearInsertedAtLookup = RsLookup.get(name: "messageGeneratorMaxEventClearInsertedAt")
if (clearInsertedAtLookup == null)
{
    def maxEventInsertedAt = 0
    def maxEvent = RsHistoricalEvent.search("alias:*", [max: 1, sort: "rsInsertedAt", order: "desc"]).results[0]
    if (maxEvent != null)
    {
        maxEventInsertedAt = Long.valueOf(maxEvent.rsInsertedAt) + 1
    }
    clearInsertedAtLookup = RsLookup.add(name: "messageGeneratorMaxEventClearInsertedAt", value: maxEventInsertedAt)
}
def maxClearInsertedAt = Long.valueOf(clearInsertedAtLookup.value)

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
                    //note that we use maxCreateInsertedAt for search , and use newMaxCreateInsertedAt to save the last processed Event
                    def newMaxCreateInsertedAt = maxCreateInsertedAt;
                    createRules.each {rule ->
                        def delay = rule.delay
                        def searchQuery = SearchQuery.get(id: rule.searchQueryId)
                        if (searchQuery)
                        {
                            def createQuery = " (${searchQuery.query}) AND rsInsertedAt:[${maxCreateInsertedAt} TO *]"


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
                                if (newMaxCreateInsertedAt < Long.valueOf(event.rsInsertedAt) + 1)
                                {
                                    newMaxCreateInsertedAt = Long.valueOf(event.rsInsertedAt) + 1;
                                }
                            }
                        }
                        else
                        {
                            logger.debug("SearchQuery with id ${rule.searchQueryId} does not exist")
                        }

                    }
                    if (newMaxCreateInsertedAt > maxCreateInsertedAt)
                    {
                        createInsertedAtLookup.update(value: String.valueOf(newMaxCreateInsertedAt))
                    }


                    //process Event Clears, HistoricalEvent Creates
                    //note that we use maxClearInsertedAt for search , and use newMaxClearInsertedAt to save the last processed Event
                    def newMaxClearInsertedAt = maxClearInsertedAt;
                    clearRules.each {rule ->
                        def searchQuery = SearchQuery.get(id: rule.searchQueryId)
                        if (searchQuery)
                        {
                            def clearQuery = " (${searchQuery.query}) AND rsInsertedAt:[${maxClearInsertedAt} TO *]"

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
                                if (newMaxClearInsertedAt < Long.valueOf(event.rsInsertedAt) + 1)
                                {
                                    newMaxClearInsertedAt = Long.valueOf(event.rsInsertedAt) + 1;
                                }

                            }
                        }
                        else
                        {
                            logger.debug("SearchQuery with id ${rule.searchQueryId} does not exist")
                        }

                    }
                    if (newMaxClearInsertedAt > maxClearInsertedAt)
                    {
                        clearInsertedAtLookup.update(value: String.valueOf(newMaxClearInsertedAt))
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

