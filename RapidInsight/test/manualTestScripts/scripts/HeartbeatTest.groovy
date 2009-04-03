RsHeartBeat.removeAll()
RsEvent.removeAll()
RsHistoricalEvent.removeAll()

def SMARTS = "SmartsServer"
def DB = "DatabaseServer"

// configure 2 systems that are being monitored for heartbeats
def conf1 = RsHeartBeat.configureHeartBeatMonitoring(SMARTS,1)
def conf2 = RsHeartBeat.configureHeartBeatMonitoring(DB,2)

// receiving heartbeat for an unconfigured system is OK
RsHeartBeat.recordHeartBeat(logger, "UnconfiguredSystem")

assert(RsEvent.list().size()==0)

RsHeartBeat.recordHeartBeat(logger, SMARTS)
sleep(100)
RsHeartBeat.recordHeartBeat(logger, DB)

RsHeartBeat.checkHeartBeat(logger)
assert(RsEvent.list().size()==0)

sleep(600)
// only smarts down - passed at least 1000 and no heartbeat
RsHeartBeat.checkHeartBeat(logger)
assert(RsEvent.list().size()==1)
def event = RsEvent.get(name:"${SMARTS}_Down") 
assert(event != null)
event = RsEvent.get(name:"${DB}_Down") 
assert(event == null)

sleep(1200)
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

return "SUCCESS";