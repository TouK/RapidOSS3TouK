RsInMaintenance.removeAll()
RsTopologyObject.removeAll()
RsEvent.removeAll()


def maintScheduler = "MaintenanceScheduler"

//RsInMaintenance.add(name:"Device1")
def maint1 = RsInMaintenance.add(objectName:"Device1")

def event11 = RsEvent.add(name:"Event11", elementName:maint1.objectName)
def event12 = RsEvent.add(name:"Event12", elementName:maint1.objectName)

assert(!RsInMaintenance.isObjectInMaintenance(maint1.objectName))
assert(!RsEvent.get(name:event11.name).inMaintenance)
assert(!RsEvent.get(name:event12.name).inMaintenance)

// no problem taking out of maintenance even if device was not in maintenance in the first place
RsInMaintenance.takeObjectOutOfMaintenance(maint1.objectName)
assert(!RsInMaintenance.isObjectInMaintenance(maint1.objectName))
assert(!RsEvent.get(name:event11.name).inMaintenance)
assert(!RsEvent.get(name:event12.name).inMaintenance)


// indeterminate inMaintenance
maint1 = RsInMaintenance.putObjectInMaintenance("Device1")
assert(maint1.active)
assert(RsInMaintenance.isObjectInMaintenance(maint1.objectName))
assert(RsEvent.get(name:event11.name).inMaintenance)
assert(RsEvent.get(name:event12.name).inMaintenance)

RsInMaintenance.takeObjectOutOfMaintenance(maint1.objectName)
assert(!RsInMaintenance.isObjectInMaintenance(maint1.objectName))
assert(!RsEvent.get(name:event11.name).inMaintenance)
assert(!RsEvent.get(name:event12.name).inMaintenance)

def event13 = RsEvent.add(name:"Event13", elementName:maint1.objectName)
assert(!event13.inMaintenance)


// inMaintenance with duration
def endTime = new Date(System.currentTimeMillis() + 1000)
//device1.putInMaintenance(endTime, true)

maint1 = RsInMaintenance.putObjectInMaintenance("Device1",endTime)
assert(RsInMaintenance.isObjectInMaintenance(maint1.objectName))
assert(RsEvent.get(name:event11.name).inMaintenance)
assert(RsEvent.get(name:event12.name).inMaintenance)

sleep(500)
script.CmdbScript.runScript(maintScheduler) // still in maintenance
assert(RsInMaintenance.isObjectInMaintenance(maint1.objectName))
assert(RsEvent.get(name:event11.name).inMaintenance)
assert(RsEvent.get(name:event12.name).inMaintenance)

def event14 = RsEvent.add(name:"Event14", elementName:maint1.objectName)
assert(RsEvent.get(name:event14.name).inMaintenance)

sleep(600)
script.CmdbScript.runScript(maintScheduler) // no longer in maintenance
assert(!RsInMaintenance.isObjectInMaintenance(maint1.objectName))
assert(!RsEvent.get(name:event11.name).inMaintenance)
assert(!RsEvent.get(name:event12.name).inMaintenance)
assert(!RsEvent.get(name:event14.name).inMaintenance)


// scheduled inMaintenance
def startTime = new Date(System.currentTimeMillis()+ 500)
endTime = new Date(System.currentTimeMillis() + 1200)
//device1.putInMaintenance(startTime, endTime)
maint1 = RsInMaintenance.putObjectInMaintenance("Device1",startTime,endTime)



script.CmdbScript.runScript(maintScheduler) // not yet in maintenance
assert(!RsInMaintenance.isObjectInMaintenance(maint1.objectName))
assert(!RsEvent.get(name:event11.name).inMaintenance)
assert(!RsEvent.get(name:event12.name).inMaintenance)
assert(!RsEvent.get(name:event14.name).inMaintenance)

sleep(550)

script.CmdbScript.runScript(maintScheduler) // now in maintenance
assert(RsInMaintenance.isObjectInMaintenance(maint1.objectName))
assert(RsEvent.get(name:event11.name).inMaintenance)
assert(RsEvent.get(name:event12.name).inMaintenance)

sleep(200)


script.CmdbScript.runScript(maintScheduler) // still in maintenance
assert(RsInMaintenance.isObjectInMaintenance(maint1.objectName))
assert(RsEvent.get(name:event11.name).inMaintenance)
assert(RsEvent.get(name:event12.name).inMaintenance)

def event15 = RsEvent.add(name:"Event15", elementName:maint1.objectName)
assert(RsEvent.get(name:event15.name).inMaintenance)

sleep(500)
script.CmdbScript.runScript(maintScheduler) // no longer in maintenance
assert(!RsInMaintenance.isObjectInMaintenance(maint1.objectName))
assert(!RsEvent.get(name:event11.name).inMaintenance)
assert(!RsEvent.get(name:event12.name).inMaintenance)
assert(!RsEvent.get(name:event15.name).inMaintenance)


// inMaintenance with duration - user aborted
endTime = new Date(System.currentTimeMillis() + 1000)
//device1.putInMaintenance(endTime)
maint1 = RsInMaintenance.putObjectInMaintenance("Device1",endTime)
assert(RsInMaintenance.isObjectInMaintenance(maint1.objectName))
assert(RsEvent.get(name:event11.name).inMaintenance)
assert(RsEvent.get(name:event12.name).inMaintenance)

RsInMaintenance.takeObjectOutOfMaintenance(maint1.objectName) // manually take out of maintenance
assert(!RsInMaintenance.isObjectInMaintenance(maint1.objectName))
assert(!RsEvent.get(name:event11.name).inMaintenance)
assert(!RsEvent.get(name:event12.name).inMaintenance)

return "SUCCESS";