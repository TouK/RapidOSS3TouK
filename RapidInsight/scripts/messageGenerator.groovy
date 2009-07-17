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

def createIdLookup = RsLookup.get(name: "messageGeneratorMaxEventCreateId")
if (createIdLookup == null)
{
    def maxEventId = 0
    def maxEvent = RsEvent.search("alias:*", [max: 1, sort: "id", order: "desc"]).results[0]
    if (maxEvent != null)
    {
        maxEventId = Long.valueOf(maxEvent.id) + 1
    }
    createIdLookup = RsLookup.add(name: "messageGeneratorMaxEventCreateId", value: maxEventId)
}
def maxCreateId = Long.valueOf(createIdLookup.value)


def clearIdLookup = RsLookup.get(name: "messageGeneratorMaxEventClearId")
if (clearIdLookup == null)
{
    def maxEventId = 0
    def maxEvent = RsHistoricalEvent.search("alias:*", [max: 1, sort: "id", order: "desc"]).results[0]
    if (maxEvent != null)
    {
        maxEventId = Long.valueOf(maxEvent.id) + 1
    }
    clearIdLookup = RsLookup.add(name: "messageGeneratorMaxEventClearId", value: maxEventId)
}
def maxClearId = Long.valueOf(clearIdLookup.value)

//process delayingMessages which has exceeded delay time
RsMessage.processDelayedMessages()
def users = RsUser.list()
users.each {user ->
    def isAdmin = isAdmin(user);
    withSession(user.username) {
        def userId = user.id;
        DESTINATIONS.each {destinationInfo ->
            def destinationType=destinationInfo.name;
            def channelType=destinationInfo.channelType;

            def destination = RsMessageRule.getUserDestinationForChannel(user,channelType);
            logger.debug("Going to search RsMessageRule for userId:${userId}");
            if (isAdmin || (destination != null && destination != ""))
            {
                if (isAdmin && (destination == null || destination == "")) {
                    destination = "admin_destination"
                }
                logger.debug("Searching RsMessageRule for userId:${userId}, destination is:${destination}");
                //processing for RsEvent creates
                //note that we use maxCreateId for search , and use newMaxCreateId to save the last processed Event
                def newMaxCreateId = maxCreateId;
                def createRules = RsMessageRule.searchEvery("userId:\"${userId}\" AND destinationType:${destinationType} AND enabled:true")
                createRules.each {rule ->
                    def delay = rule.delay
                    def searchQuery = SearchQuery.get(id: rule.searchQueryId)
                    if (searchQuery)
                    {
                        def createQuery = " (${searchQuery.query}) AND id:[${maxCreateId} TO *]"

                        logger.debug("Seaching RsEvent, for userid: ${userId}  with createQuery : ${createQuery}")
                        def createdEvents = RsEvent.getPropertyValues(createQuery, ["id"], [sort: "id", order: "asc", max: 1000])
                        def date = new Date()
                        def now = date.getTime();

                        createdEvents.each {event ->
                            RsMessage.addEventCreateMessage(event, destinationType, destination, delay);
                            if (newMaxCreateId < Long.valueOf(event.id) + 1)
                            {
                                newMaxCreateId = Long.valueOf(event.id) + 1;
                            }
                        }
                    }
                    else
                    {
                        logger.debug("SearchQuery with id ${rule.searchQueryId} does not exist")
                    }

                }
                if (newMaxCreateId > maxCreateId)
                {
                    createIdLookup.update(value: String.valueOf(newMaxCreateId))
                }


                //process Event Clears, HistoricalEvent Creates
                //note that we use maxClearId for search , and use newMaxClearId to save the last processed Event
                def newMaxClearId = maxClearId;
                def clearRules = RsMessageRule.searchEvery("userId:\"${userId}\" AND destinationType:${destinationType} AND clearAction:true AND enabled:true")
                clearRules.each {rule ->
                    def searchQuery = SearchQuery.get(id: rule.searchQueryId)
                    if (searchQuery)
                    {
                        def clearQuery = " (${searchQuery.query}) AND id:[${maxClearId} TO *]"

                        logger.debug("Searching RsHistoricalEvent, for userid: ${userId} with clearQuery : ${clearQuery}")
                        def clearedEvents = RsHistoricalEvent.getPropertyValues(clearQuery, ["id", "activeId"], [sort: "id", order: "asc", max: 1000])
                        def date = new Date()
                        def now = date.getTime();

                        clearedEvents.each {event ->
                            RsMessage.addEventClearMessage(event, destinationType, destination)
                            if (newMaxClearId < Long.valueOf(event.id) + 1)
                            {
                                newMaxClearId = Long.valueOf(event.id) + 1;
                            }

                        }
                    }
                    else
                    {
                        logger.debug("SearchQuery with id ${rule.searchQueryId} does not exist")
                    }

                }
                if (newMaxClearId > maxClearId)
                {
                    clearIdLookup.update(value: String.valueOf(newMaxClearId))
                }

            }
            else
            {
                logger.warn("Skipping search RsMessageRule for userId:${userId}. User does not have a destination for destinationType ${destinationType}");
            }

        }
    }
}

def isAdmin(RsUser user) {
    def groups = user.groups;
    for (group in groups) {
        if (group.role?.name == Role.ADMINISTRATOR) {
            return true;
        }
    }
    return false;
}