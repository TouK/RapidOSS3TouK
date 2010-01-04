// This script allows the following 2 important operations of the CorrelationProcessor to be invoked peridically.
logger.debug("BEGIN xTimesInYActions")
def correlationProcessor = application.RapidApplication.getUtility("CorrelationProcessor")

//Each RsXinY has a time to live. This operation will remove expired instances.
logger.debug("calling removeExpiredCorrelationItems")
correlationProcessor.removeExpiredCorrelationItems();

//this operation will determine if specified number of events have accumulated in the specified time interval. If so, action will be taken.
logger.debug("calling takeAction")
correlationProcessor.takeAction();


