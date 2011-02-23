import auth.ChannelUserInformation
import auth.Role
import auth.RsUser
import message.RsMessage
import message.RsMessageRule
import search.SearchQuery
import message.RsMessageRuleCalendar
import com.ifountain.rcmdb.util.CollectionUtils
import auth.Group
import java.text.SimpleDateFormat
import org.apache.commons.lang.time.DateUtils

/**
* Created by IntelliJ IDEA.
* User: iFountain
* Date: Jan 5, 2009
* Time: 5:29:28 PM
* To change this template use File | Settings | File Templates.
*/


DESTINATIONS = RsMessageRule.getDestinations();
DESTINATION_MAP = [:]
CHANNEL_MAP = [:]
DESTINATIONS.each {
    DESTINATION_MAP.put(it.name, it.channelType);
    CHANNEL_MAP.put(it.channelType, it.name);
}

USER_DESTINATIONS = [:]

def currentTime = System.currentTimeMillis();
CURRENT_TIME_CALENDAR = new GregorianCalendar()
CURRENT_TIME_CALENDAR.setTimeInMillis(currentTime);
CURRENT_HOUR_MINUTE_CALENDAR = new GregorianCalendar();
CURRENT_HOUR_MINUTE_CALENDAR.setTime(new Date(0));
CURRENT_HOUR_MINUTE_CALENDAR.set(Calendar.HOUR_OF_DAY, CURRENT_TIME_CALENDAR.get(Calendar.HOUR_OF_DAY))
CURRENT_HOUR_MINUTE_CALENDAR.set(Calendar.MINUTE, CURRENT_TIME_CALENDAR.get(Calendar.MINUTE))


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
def calendars = RsMessageRuleCalendar.list();
def matchingCalendars = [];
calendars.each {RsMessageRuleCalendar cal ->
    if (isCalendarMatching(cal)) matchingCalendars.add(cal)
}
def createRules = [];
def clearRules = [];
def calendarIds = matchingCalendars.id;
calendarIds.add(0);
CollectionUtils.executeForEachBatch(calendarIds, 100) {List calendarsIdsToBeProcessed ->
    if (calendarsIdsToBeProcessed.size() == 0) return;
    StringBuffer queryBuffer = new StringBuffer("enabled:true AND (");
    calendarsIdsToBeProcessed.each {
        queryBuffer.append("calendarId:${it} OR ")
    }
    def query = queryBuffer.toString();
    query = query.substring(0, query.length() - 4) + ")";
    createRules.addAll(RsMessageRule.searchEvery(query));
}
CollectionUtils.executeForEachBatch(calendarIds, 100) {List calendarsIdsToBeProcessed ->
    if (calendarsIdsToBeProcessed.size() == 0) return;
    StringBuffer queryBuffer = new StringBuffer("enabled:true AND sendClearEventType:true AND (");
    calendarsIdsToBeProcessed.each {
        queryBuffer.append("calendarId:${it} OR ")
    }
    def query = queryBuffer.toString();
    query = query.substring(0, query.length() - 4) + ")";
    clearRules.addAll(RsMessageRule.searchEvery(query));
}
def eventsWillBeProcessed = RsEvent.getPropertyValues("rsInsertedAt:[${maxCreateTime} TO *]", ["rsInsertedAt"], [max: 1000, sort: 'rsInsertedAt', order: 'desc']);
if (eventsWillBeProcessed.size() > 0) {
    def newMaxCreateTime = eventsWillBeProcessed[0].rsInsertedAt;
    createTimeLookup.update(value: String.valueOf(newMaxCreateTime + 1))
    createRules.each {RsMessageRule rule ->
        def ruleDestinationType = rule.destinationType;
        def users = getRuleUsers(rule);
        users.each {RsUser user ->
            def destinationData = getUserDestinationData(user, ruleDestinationType)
            def destination=destinationData.destination;
            def destinationType=destinationData.destinationType;
            def channelType=destinationData.channelType;
            if (destination != null) {
                withSession(user.username) {
                    def delay = rule.delay
                    def searchQuery = SearchQuery.get(id: rule.searchQueryId)
                    if (searchQuery)
                    {
                        logger.debug("Processing RsMessageRule ${searchQuery.name} for user ${user.username} for channel ${channelType}")
                        def createQuery = " (${searchQuery.query}) AND rsInsertedAt:[${maxCreateTime} TO ${newMaxCreateTime}]"
                        def eventClass = RsEvent;
                        if (searchQuery.searchClass)
                        {
                            eventClass = application.RapidApplication.getModelClass(searchQuery.searchClass);
                        }
                        logger.debug("Seaching ${eventClass.name}, for user: ${user.username}  with createQuery : ${createQuery}")
                        def createdEvents = eventClass.getPropertyValues(createQuery, ["id", "rsInsertedAt"], [sort: "rsInsertedAt", order: "asc"])
                        def date = new Date()
                        def now = date.getTime();
                        createdEvents.each {event ->
                            RsMessage.addEventCreateMessage(event, destinationType, destination, delay);
                        }
                    }
                    else
                    {
                        logger.debug("SearchQuery with id ${rule.searchQueryId} does not exist")
                    }
                }
            }
        }
    }
}
def historicalEventsWillBeProcessed = RsHistoricalEvent.getPropertyValues("rsInsertedAt:[${maxClearTime} TO *]", ["rsInsertedAt"], [max: 1000, sort: 'rsInsertedAt', order: 'desc']);
if (historicalEventsWillBeProcessed.size() > 0) {
    def newMaxClearTime = historicalEventsWillBeProcessed[0].rsInsertedAt
    clearTimeLookup.update(value: String.valueOf(newMaxClearTime + 1))
    clearRules.each {RsMessageRule rule ->
        def ruleDestinationType = rule.destinationType;
        def users = getRuleUsers(rule);
        users.each {RsUser user ->
            def destinationData = getUserDestinationData(user, ruleDestinationType)
            def destination=destinationData.destination;
            def destinationType=destinationData.destinationType;
            def channelType=destinationData.channelType; 
            if (destination != null) {
                withSession(user.username) {
                    def searchQuery = SearchQuery.get(id: rule.searchQueryId)
                    if (searchQuery)
                    {
                        logger.debug("Processing RsMessageRule ${searchQuery.name} for user ${user.username} for channel ${channelType}")
                        def clearQuery = " (${searchQuery.query}) AND rsInsertedAt:[${maxClearTime} TO *]"
                        def eventClass = RsHistoricalEvent;
                        if (searchQuery.searchClass)
                        {
                            eventClass = application.RapidApplication.getModelClass(searchQuery.searchClass).historicalEventModel();
                        }
                        logger.debug("Searching ${eventClass.name}, for username: ${user.username} with clearQuery : ${clearQuery}")
                        def clearedEvents = eventClass.getPropertyValues(clearQuery, ["id", "activeId", "rsInsertedAt"], [sort: "rsInsertedAt", order: "asc", max: 1000])
                        def date = new Date()
                        def now = date.getTime();

                        clearedEvents.each {event ->
                            RsMessage.addEventClearMessage(event, destinationType, destination)
                        }
                    }
                    else
                    {
                        logger.debug("SearchQuery with id ${rule.searchQueryId} does not exist")
                    }
                }
            }
        }
    }
}

def getRuleUsers(RsMessageRule rule) {
    def users = [];
    if (rule.ruleType == 'public') {
        def usersMap = [:]
        if (rule.groups != '') {
            def groups = rule.groups.split(',');
            groups.each {
                Group.get(name: it)?.users.each {RsUser groupUser ->
                    usersMap.put(groupUser.username, groupUser);
                }
            }
        }
        if (rule.users != '') {
            rule.users.split(',').each {
                if (!usersMap.containsKey(it)) {
                    def user = RsUser.get(username: it);
                    if (user) usersMap.put(it, user);
                }
            }
        }
        users.addAll(usersMap.values());
    }
    else {
        RsUser user = RsUser.get(username: rule.users)
        if (user) users.add(user);
    }
    return users;
}
def getUserDestinationData(RsUser user, ruleDestinationType) {

    if (!USER_DESTINATIONS.containsKey(user.username)) {
        def userChannelInformations = user.getChannelInformations();
        def channelInfoMap = [:]
        userChannelInformations.each {ChannelUserInformation info ->
            if (info.isDefault) channelInfoMap.put(RsMessageRule.DEFAULT_DESTINATION, info.type) //default contains the type of default for the user
            channelInfoMap.put(info.type, info.destination)
        }
        USER_DESTINATIONS.put(user.username, channelInfoMap);
    }
    def destinationType=ruleDestinationType;
    def channelType = DESTINATION_MAP[destinationType];
    if(channelType == RsMessageRule.DEFAULT_DESTINATION) //if type is default then the get the user default channel 
    {
    	channelType = USER_DESTINATIONS[user.username][channelType];
    	destinationType = CHANNEL_MAP[channelType]; //get the destination type for this channel
    }

	def destination = USER_DESTINATIONS[user.username][channelType];

    try {
        RsMessageRule.validateUserDestinationForChannel(user, destination, channelType);
        if (!RsMessageRule.isChannelType(channelType))
        {
            destination = "admin_destination";
        }
        if (destination == null)
        {
            throw new Exception("Critical Error can not find destination for ${destinationType}");
        }
    }
    catch (e)
    {
        logger.warn("Skipping search RsMessageRule for user:${user.username}. Reason ${e.getMessage()}");
    }
    return [channelType:channelType,destinationType:destinationType,destination:destination];
}

def isCalendarMatching(RsMessageRuleCalendar cal) {
    def days = Arrays.asList(cal.days.split(','));
    if (days.contains(CURRENT_TIME_CALENDAR.get(Calendar.DAY_OF_WEEK).toString())) {
        if (cal.exceptions != '') {
            def currentDate = CURRENT_TIME_CALENDAR.getTime();
            def exceptions = Arrays.asList(cal.exceptions.split(','));
            SimpleDateFormat format = new SimpleDateFormat(RsMessageRuleCalendar.EXCEPTION_DATE_FORMAT)
            for (def i = 0; i < exceptions.size(); i++) {
                if (DateUtils.isSameDay(currentDate, format.parse(exceptions[i]))) return false;
            }
        }
        def currentHourMinuteTime = CURRENT_HOUR_MINUTE_CALENDAR.getTime().getTime();
        if(currentHourMinuteTime < cal.starting || currentHourMinuteTime > cal.ending) return false;
        return true;
    }
    return false;

}

