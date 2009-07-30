import junit.framework.Assert;

RsInMaintenance.removeAll()
RsTopologyObject.removeAll()
RsEvent.removeAll()


def maintScheduler = "MaintenanceScheduler"

def deviceName="Device1";
def info="testMaintenance";
def source="testSource";

def event11 = RsEvent.add(name:"Event11", elementName:deviceName)
def event12 = RsEvent.add(name:"Event12", elementName:deviceName)

assert(!RsInMaintenance.isObjectInMaintenance(deviceName))
assert(!RsEvent.get(name:event11.name).inMaintenance)
assert(!RsEvent.get(name:event12.name).inMaintenance)

// no problem taking out of maintenance even if device was not in maintenance in the first place
RsInMaintenance.takeObjectOutOfMaintenance(deviceName)
assert(!RsInMaintenance.isObjectInMaintenance(deviceName))
assert(!RsEvent.get(name:event11.name).inMaintenance)
assert(!RsEvent.get(name:event12.name).inMaintenance)


// indeterminate inMaintenance
def props = ["objectName":"Device1", "source":source, "info":info]
def startTimeForMaint1=System.currentTimeMillis();
def maint1 = RsInMaintenance.putObjectInMaintenance(props)

assert(RsInMaintenance.isObjectInMaintenance(maint1.objectName))
assert(RsEvent.get(name:event11.name).inMaintenance)
assert(RsEvent.get(name:event12.name).inMaintenance)
assert(maint1.source==source)
assert(maint1.info==info)
assert(maint1.starting.getTime()>=startTimeForMaint1);
assert(maint1.ending.getTime()==0)

def eventDuringMaintenance1 = RsEvent.add(name:"eventDuringMaintenance1", elementName:maint1.objectName)
assert(eventDuringMaintenance1.inMaintenance)

RsInMaintenance.takeObjectOutOfMaintenance(maint1.objectName)
assert(!RsInMaintenance.isObjectInMaintenance(maint1.objectName))
assert(!RsEvent.get(name:event11.name).inMaintenance)
assert(!RsEvent.get(name:event12.name).inMaintenance)

def event13 = RsEvent.add(name:"Event13", elementName:maint1.objectName)
assert(!event13.inMaintenance)
assert(!RsEvent.get(name:eventDuringMaintenance1.name).inMaintenance)

// inMaintenance with duration
def startTime=new Date(System.currentTimeMillis()-1500);
def endTime = new Date(System.currentTimeMillis()+1500)


props = ["objectName":"Device1", "source":source, "info":info,"starting":startTime, "ending":endTime]
maint1 = RsInMaintenance.putObjectInMaintenance(props)

assert(RsInMaintenance.isObjectInMaintenance(maint1.objectName))
assert(RsEvent.get(name:event11.name).inMaintenance)
assert(RsEvent.get(name:event12.name).inMaintenance)
assert(maint1.source==source)
assert(maint1.info==info)
assert(maint1.starting.getTime()==startTime.getTime());
assert(maint1.ending.getTime()==endTime.getTime());


sleep(100)

script.CmdbScript.runScript(maintScheduler) // still in maintenance

assert(RsInMaintenance.isObjectInMaintenance(maint1.objectName))
assert(RsEvent.get(name:event11.name).inMaintenance)
assert(RsEvent.get(name:event12.name).inMaintenance)

def event14 = RsEvent.add(name:"Event14", elementName:maint1.objectName)
assert(RsEvent.get(name:event14.name).inMaintenance)

def remainingTime=endTime.getTime()-System.currentTimeMillis();
sleep(remainingTime+100);
script.CmdbScript.runScript(maintScheduler) // no longer in maintenance
assert(!RsInMaintenance.isObjectInMaintenance(maint1.objectName))
assert(!RsEvent.get(name:event11.name).inMaintenance)
assert(!RsEvent.get(name:event12.name).inMaintenance)
assert(!RsEvent.get(name:event14.name).inMaintenance)


// inMaintenance with duration - user aborted
endTime = new Date(System.currentTimeMillis() + 1000)

startTimeForMaint1=System.currentTimeMillis();

props = ["objectName":"Device1", "source":source, "info":info, "ending":endTime]
maint1 = RsInMaintenance.putObjectInMaintenance(props)

assert(RsInMaintenance.isObjectInMaintenance(maint1.objectName))
assert(RsEvent.get(name:event11.name).inMaintenance)
assert(RsEvent.get(name:event12.name).inMaintenance)
assert(maint1.source==source)
assert(maint1.info==info)
assert(maint1.starting.getTime()>=startTimeForMaint1);
assert(maint1.ending.getTime()==endTime.getTime())

RsInMaintenance.takeObjectOutOfMaintenance(maint1.objectName) // manually take out of maintenance
assert(!RsInMaintenance.isObjectInMaintenance(maint1.objectName))
assert(!RsEvent.get(name:event11.name).inMaintenance)
assert(!RsEvent.get(name:event12.name).inMaintenance)



//putObjectInMaintenance throws exception if endtime is smaller than starttime
endTime = new Date(System.currentTimeMillis() - 1000)

props = ["objectName":"Device1", "source":source, "info":info, "ending":endTime]
try{
    maint1 = RsInMaintenance.putObjectInMaintenance(props)
    fail("should throw exception");
}
catch(e)
{
    Assert.assertTrue("wrong exception ${e}",e.getMessage().indexOf("time should be greater than starting time")>=0);
}
assert(RsInMaintenance.count()==0)


return "SUCCESS";