Long daysToDeleteBefore=30;

Long dayDiff=24l*3600l*1000l;
Long deleteStart=Date.now()-(daysToDeleteBefore*dayDiff);

def eventPerIteration=1000;
def eventQuery="clearedAt:[* TO ${deleteStart}]";
def eventsRemainingCount=RsHistoricalEvent.countHits(eventQuery);

logger.warn("---------------------------------------");
logger.warn("Clear Historical Event Starts, Will delete ${eventsRemainingCount} older then ${new Date(deleteStart)}");

while(eventsRemainingCount>0 && !IS_STOPPED())
{
	logger.warn("Will delete ${eventPerIteration}");
	def events=RsHistoricalEvent.search(eventQuery,[max:eventPerIteration]).results;
	logger.warn("Retrieved ${events.size()} events");

	events.each{ event ->
		RsEventJournal.searchEvery("eventId:${event.activeId}").each{ journal ->
			journal.remove();
		}
		event.remove();
	}
	logger.warn("Deleted ${events.size()} SmartsHistoricalNotification ");

	eventsRemainingCount=events.size();
}
logger.warn("Clear Historical Event Finishes");