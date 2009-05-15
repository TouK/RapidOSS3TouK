// ex: x=3 notification, y=5 sec

import script.*

println "++++++++++++"
RsEvent.removeAll()
RsXinYCorrelation.removeAll()

RsEvent.notify([source:"SyslogAdapter", name:"Router_router1_Down"])
RsEvent.notify([source:"SyslogAdapter", name:"Router_router1_Down"])

CmdbScript.runScript("xTimesInYActions")
// Action will create an RsEvent with name:"correlation"+currentTime
def notificationCount = RsEvent.search("name:correlation*").total 
assert notificationCount == 1

sleep(1100)
RsEvent.notify([source:"SyslogAdapter", name:"Router_router1_Down"])
CmdbScript.runScript("xTimesInYActions")
notificationCount = RsEvent.search("name:correlation*").total 
assert notificationCount == 1

sleep(300)
RsEvent.notify([source:"SyslogAdapter", name:"Router_router1_Down"])
sleep(300)
RsEvent.notify([source:"SyslogAdapter", name:"Router_router1_Down"])
sleep(300)
RsEvent.notify([source:"SyslogAdapter", name:"Router_router1_Down"])
CmdbScript.runScript("xTimesInYActions")
notificationCount = RsEvent.search("name:correlation*").total 
assert notificationCount == 2

sleep(800)
CmdbScript.runScript("xTimesInYActions")
notificationCount = RsEvent.search("name:correlation*").total 
assert notificationCount == 2

RsEvent.notify([source:"MyAdapter", name:"Interface_if1_Down"])
RsEvent.notify([source:"MyAdapter", name:"Interface_if1_Down"])
RsEvent.notify([source:"MyAdapter", name:"Interface_if1_Down"])
CmdbScript.runScript("xTimesInYActions")
notificationCount = RsEvent.search("name:correlation*").total 
assert notificationCount == 2

RsEvent.notify([source:"TrapAdapter", name:"Host_host1_Down"])
RsEvent.notify([source:"TrapAdapter", name:"Host_host1_Down"])
RsEvent.notify([source:"TrapAdapter", name:"Host_host1_Down"])
CmdbScript.runScript("xTimesInYActions")
notificationCount = RsEvent.search("name:correlation*").total 
assert notificationCount == 3

RsEvent.notify([source:"TrapAdapter", name:"Host_host1_Down"])
CmdbScript.runScript("xTimesInYActions")
notificationCount = RsEvent.search("name:correlation*").total 
assert notificationCount == 3

RsEvent.notify([source:"TrapAdapter", name:"Host_host1_Down"])
RsEvent.notify([source:"TrapAdapter", name:"Host_host1_Down"])
RsEvent.notify([source:"TrapAdapter", name:"Host_host1_Down"])
sleep(2200)
CmdbScript.runScript("xTimesInYActions")
notificationCount = RsEvent.search("name:correlation*").total 
assert notificationCount == 3

return