/*
This script should be configured as a periodic script to create meaningful data in short time
*/

//get variables from repository
def allEvents = RrdVariable.get(name:"allEvents")
def criticalEvents = RrdVariable.get(name:"criticalEvents")
def majorEvents = RrdVariable.get(name:"majorEvents")

//calculate data to be inserted. In this scenario, we will search event counts according to their severity
def allEventCount = RsEvent.countHits("alias:*");
def criticalEventCount = RsEvent.count("severity:5");
def majorEventCount = RsEvent.count("severity:4");

//Time series database will be updated by found event counts
allEvents.updateDB(allEventCount)
// time parameter can be specified in updateDB optionally. If it is not specified, it will be assigned to current time
allEvents.updateDB(criticalEventCount, Date.now())
allEvents.updateDB(majorEventCount)