import message.RsMessage;

Long daysToDeleteBefore=90;

Long dayDiff=24l*3600l*1000l;
Long deleteStart=Date.now()-(daysToDeleteBefore*dayDiff);

def eventPerIteration=1000;
def eventQuery="rsInsertedAt:[* TO ${deleteStart}]";
def eventsRemainingCount=RsMessage.countHits(eventQuery);

logger.warn("---------------------------------------");
logger.warn("Clear RsMessage Starts, Will delete ${eventsRemainingCount} older then ${new Date(deleteStart)}");

while(eventsRemainingCount>0 && !IS_STOPPED())
{
	logger.warn("Will delete ${eventPerIteration}");
	def events=RsMessage.search(eventQuery,[max:eventPerIteration]).results;
	logger.warn("Retrieved ${events.size()} messages");

	application.RapidApplication.executeBatch(){
		events.each{ event ->
			event.remove();
		}
	}
	logger.warn("Deleted ${events.size()} RsMessage ");

	eventsRemainingCount=events.size();
}
logger.warn("Clear RsMessage Finishes");