RsInMaintenance.removeAll()
RsTopologyObject.removeAll()
RsEvent.removeAll()

def maintScheduler = "MaintenanceScheduler"

def device1 = RsTopologyObject.add([name:"Device1"])
def event11 = RsEvent.add(name:"Event11", elementName:device1.name)
def event12 = RsEvent.add(name:"Event12", elementName:device1.name)

assert(!device1.inMaintenance)
assert(!RsEvent.get(name:event11.name).inMaintenance)
assert(!RsEvent.get(name:event12.name).inMaintenance)

// no problem taking out of maintenance even if device was not in maintenance in the first place
device1.takeOutOfMaintenance()
assert(!device1.inMaintenance)
assert(!RsEvent.get(name:event11.name).inMaintenance)
assert(!RsEvent.get(name:event12.name).inMaintenance)

// indeterminate inMaintenance
device1.putInMaintenance()
assert(device1.inMaintenance)
assert(RsEvent.get(name:event11.name).inMaintenance)
assert(RsEvent.get(name:event12.name).inMaintenance)

device1.takeOutOfMaintenance()
assert(!device1.inMaintenance)
assert(!RsEvent.get(name:event11.name).inMaintenance)
assert(!RsEvent.get(name:event12.name).inMaintenance)

def event13 = RsEvent.add(name:"Event13", elementName:device1.name)
assert(!event13.inMaintenance)

// inMaintenance with duration
def endTime = new Date(System.currentTimeMillis() + 1000)
device1.putInMaintenance(endTime)
assert(device1.inMaintenance)
assert(RsEvent.get(name:event11.name).inMaintenance)
assert(RsEvent.get(name:event12.name).inMaintenance)

sleep(500)
script.CmdbScript.runScript(maintScheduler) // still in maintenance
assert(device1.inMaintenance)
assert(RsEvent.get(name:event11.name).inMaintenance)
assert(RsEvent.get(name:event12.name).inMaintenance)

def event14 = RsEvent.add(name:"Event14", elementName:device1.name)
assert(RsEvent.get(name:event14.name).inMaintenance)

sleep(600)
script.CmdbScript.runScript(maintScheduler) // no longer in maintenance
assert(!device1.inMaintenance)
assert(!RsEvent.get(name:event11.name).inMaintenance)
assert(!RsEvent.get(name:event12.name).inMaintenance)
assert(!RsEvent.get(name:event14.name).inMaintenance)

// scheduled inMaintenance
def startTime = new Date(System.currentTimeMillis()+ 500)
endTime = new Date(System.currentTimeMillis() + 1200)
device1.putInMaintenance(startTime, endTime)



script.CmdbScript.runScript(maintScheduler) // not yet in maintenance
assert(!device1.inMaintenance)
assert(!RsEvent.get(name:event11.name).inMaintenance)
assert(!RsEvent.get(name:event12.name).inMaintenance)
assert(!RsEvent.get(name:event14.name).inMaintenance)

sleep(550)

script.CmdbScript.runScript(maintScheduler) // now in maintenance
assert(device1.inMaintenance)
assert(RsEvent.get(name:event11.name).inMaintenance)
assert(RsEvent.get(name:event12.name).inMaintenance)

sleep(200)


script.CmdbScript.runScript(maintScheduler) // still in maintenance
assert(device1.inMaintenance)
assert(RsEvent.get(name:event11.name).inMaintenance)
assert(RsEvent.get(name:event12.name).inMaintenance)

def event15 = RsEvent.add(name:"Event15", elementName:device1.name)
assert(RsEvent.get(name:event15.name).inMaintenance)

sleep(500)
script.CmdbScript.runScript(maintScheduler) // no longer in maintenance
assert(!device1.inMaintenance)
assert(!RsEvent.get(name:event11.name).inMaintenance)
assert(!RsEvent.get(name:event12.name).inMaintenance)
assert(!RsEvent.get(name:event15.name).inMaintenance)

// inMaintenance with duration - user aborted
endTime = new Date(System.currentTimeMillis() + 1000)
device1.putInMaintenance(endTime)
assert(device1.inMaintenance)
assert(RsEvent.get(name:event11.name).inMaintenance)
assert(RsEvent.get(name:event12.name).inMaintenance)

device1.takeOutOfMaintenance() // manually take out of maintenance
assert(!device1.inMaintenance)
assert(!RsEvent.get(name:event11.name).inMaintenance)
assert(!RsEvent.get(name:event12.name).inMaintenance)

return "SUCCESS";