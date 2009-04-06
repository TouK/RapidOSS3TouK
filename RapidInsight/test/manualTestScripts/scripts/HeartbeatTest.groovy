RsHeartBeat.removeAll()
RsEvent.removeAll()
RsHistoricalEvent.removeAll()
RsEventJournal.removeAll()

def SMARTS = "SmartsServer"
def DB = "DatabaseServer"

//	unit test for heartbeat search
RsHeartBeat.configureHeartBeatMonitoring("System1",1)
RsHeartBeat.configureHeartBeatMonitoring("System2",1)
RsHeartBeat.configureHeartBeatMonitoring("System3",1)
RsHeartBeat.configureHeartBeatMonitoring("System4",1)

RsHeartBeat.get(objectName:"System1").consideredDownAt = new Date().getTime();
RsHeartBeat.get(objectName:"System2").consideredDownAt = new Date().getTime();
RsHeartBeat.get(objectName:"System3").consideredDownAt = new Date().getTime() + 10000;
sleep(5)
RsHeartBeat.checkHeartBeat(logger)
assert(RsEvent.list().size()==3) //(System1, 2, and 4)
RsEvent.removeAll()
RsHeartBeat.removeAll()

// configure 2 systems that are being monitored for heartbeats
def conf1 = RsHeartBeat.configureHeartBeatMonitoring(SMARTS,1)
def conf2 = RsHeartBeat.configureHeartBeatMonitoring(DB,2)

// receiving heartbeat for an unconfigured system is OK
RsHeartBeat.recordHeartBeat(logger, "UnconfiguredSystem")

RsHeartBeat.checkHeartBeat(logger)
// Both systems are  considered down since no heartbeat have been received for either, yet.
assert(RsEvent.list().size()==2)
RsEvent.removeAll()
assert(RsEvent.list().size()==0)

RsHeartBeat.recordHeartBeat(logger, SMARTS)
sleep(100)
RsHeartBeat.recordHeartBeat(logger, DB)
RsHeartBeat.checkHeartBeat(logger)
assert(RsEvent.list().size()==0)
sleep(1000)
// only smarts down - passed at least 1000 and no heartbeat
RsHeartBeat.checkHeartBeat(logger)
assert(RsEvent.list().size()==1)
def event = RsEvent.get(name:"${SMARTS}_Down") 
assert(event != null)
event = RsEvent.get(name:"${DB}_Down") 
assert(event == null)
sleep(1000)
// now both down
RsHeartBeat.checkHeartBeat(logger)
assert(RsEvent.list().size()==2)
event = RsEvent.get(name:"${DB}_Down") 
assert(event != null)
RsHeartBeat.recordHeartBeat(logger, SMARTS)
// Smarts is no longer down
RsHeartBeat.checkHeartBeat(logger)
assert(RsEvent.list().size()==1)
event = RsEvent.get(name:"${DB}_Down") 
assert(event != null)
assert(RsHistoricalEvent.list().size()==1)
event = RsHistoricalEvent.findByName("${SMARTS}_Down") 
assert(event != null)
RsHeartBeat.recordHeartBeat(logger, SMARTS)
RsHeartBeat.recordHeartBeat(logger, DB)
assert(RsEvent.list().size()==0)

return "SUCCESS";