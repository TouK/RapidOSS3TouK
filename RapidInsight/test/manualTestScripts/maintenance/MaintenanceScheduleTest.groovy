RsInMaintenance.removeAll()
RsInMaintenanceSchedule.removeAll()
RsTopologyObject.removeAll()
RsEvent.removeAll()

def maintScheduler = "MaintenanceScheduler"

def info="testMaintenance";


def objectName="Device1";

def event11 = RsEvent.add(name:"Event11", elementName:objectName)
def event12 = RsEvent.add(name:"Event12", elementName:objectName)
def event14 = RsEvent.add(name:"Event14", elementName:objectName)

def startTime = new Date(System.currentTimeMillis()+ 500)
endTime = new Date(System.currentTimeMillis() + 2000)
def maintSchedule= RsInMaintenanceSchedule.addObjectSchedule(objectName,info,startTime,endTime)
assert(maintSchedule.info==info)
assert(maintSchedule.objectName==objectName)



script.CmdbScript.runScript(maintScheduler) // not yet in maintenance
assert(RsInMaintenance.countHits("alias:*")==0)
assert(RsInMaintenanceSchedule.countHits("objectName:${objectName.exactQuery()}")==1)
assert(!RsInMaintenance.isObjectInMaintenance(objectName))
assert(!RsEvent.get(name:event11.name).inMaintenance)
assert(!RsEvent.get(name:event12.name).inMaintenance)
assert(!RsEvent.get(name:event14.name).inMaintenance)

sleep(550)

script.CmdbScript.runScript(maintScheduler) // now in maintenance
assert(RsInMaintenance.countHits("objectName:${objectName.exactQuery()}")==1)
assert(RsInMaintenanceSchedule.countHits("objectName:${objectName.exactQuery()}")==1)
assert(RsInMaintenance.isObjectInMaintenance(objectName))
assert(RsEvent.get(name:event11.name).inMaintenance)
assert(RsEvent.get(name:event12.name).inMaintenance)
def maint=RsInMaintenance.get(objectName:objectName)
assert(maint.info==maintSchedule.info)
assert(maint.ending==maintSchedule.ending)
assert(maint.source=="schedule")
sleep(200)


script.CmdbScript.runScript(maintScheduler) // still in maintenance
assert(RsInMaintenance.countHits("objectName:${objectName.exactQuery()}")==1)
assert(RsInMaintenanceSchedule.countHits("objectName:${objectName.exactQuery()}")==1)
assert(RsInMaintenance.isObjectInMaintenance(objectName))
assert(RsEvent.get(name:event11.name).inMaintenance)
assert(RsEvent.get(name:event12.name).inMaintenance)

def event15 = RsEvent.add(name:"Event15", elementName:objectName)
assert(RsEvent.get(name:event15.name).inMaintenance)


def remainingTime=endTime.getTime()-System.currentTimeMillis();
sleep(remainingTime+100);

script.CmdbScript.runScript(maintScheduler) // no longer in maintenance
assert(RsInMaintenance.countHits("objectName:${objectName.exactQuery()}")==0)
assert(RsInMaintenanceSchedule.countHits("objectName:${objectName.exactQuery()}")==0)
assert(!RsInMaintenance.isObjectInMaintenance(objectName))
assert(!RsEvent.get(name:event11.name).inMaintenance)
assert(!RsEvent.get(name:event12.name).inMaintenance)
assert(!RsEvent.get(name:event15.name).inMaintenance)


//manually remove schedule while its active, in maintenance source is schedule
startTime = new Date(System.currentTimeMillis())
endTime = new Date(System.currentTimeMillis() + 1200)
maintSchedule= RsInMaintenanceSchedule.addObjectSchedule(objectName,info,startTime,endTime)

sleep(100)

script.CmdbScript.runScript(maintScheduler) // now in maintenance
assert(RsInMaintenance.countHits("objectName:${objectName.exactQuery()}")==1)
assert(RsInMaintenanceSchedule.countHits("objectName:${objectName.exactQuery()}")==1)

RsInMaintenanceSchedule.removeSchedule(maintSchedule.id);
assert(RsInMaintenance.countHits("objectName:${objectName.exactQuery()}")==0)
assert(RsInMaintenanceSchedule.countHits("objectName:${objectName.exactQuery()}")==0)

//manually remove schedule while its active but  in maintenance source is not schedule , user has overriden
startTime = new Date(System.currentTimeMillis())
endTime = new Date(System.currentTimeMillis() + 1200)
maintSchedule= RsInMaintenanceSchedule.addObjectSchedule(objectName,info,startTime,endTime)


sleep(100)

script.CmdbScript.runScript(maintScheduler) // now in maintenance
assert(RsInMaintenance.countHits("objectName:${objectName.exactQuery()}")==1)
assert(RsInMaintenanceSchedule.countHits("objectName:${objectName.exactQuery()}")==1)

def props = ["objectName":objectName, "source":"userX", "info":info]
RsInMaintenance.putObjectInMaintenance(props)
RsInMaintenanceSchedule.removeSchedule(maintSchedule.id);
assert(RsInMaintenance.countHits("objectName:${objectName.exactQuery()}")==1)
assert(RsInMaintenanceSchedule.countHits("objectName:${objectName.exactQuery()}")==0)

//testing invalid schedules
try{
   def newStartTime=new Date(System.currentTimeMillis())
   def newEndTime=new Date(System.currentTimeMillis()-1000)

   RsInMaintenanceSchedule.addObjectSchedule(objectName,info,newStartTime,newEndTime)
   assert(false) //should throw exception here
}
catch(e)
{
  println e
}


startTime = new Date(System.currentTimeMillis())
endTime = new Date(System.currentTimeMillis() + 10000)
def schedule=RsInMaintenanceSchedule.addObjectSchedule(objectName,info,startTime,endTime)
assert(RsInMaintenanceSchedule.countHits("id:${schedule.id}")==1)

try{
   RsInMaintenanceSchedule.addObjectSchedule(objectName,info,startTime,endTime)
   assert(false) //should throw exception here
}
catch(e)
{
  println e
}
assert(RsInMaintenanceSchedule.countHits("objectName:${objectName.exactQuery()}")==1)

try{
   def newStartTime=new Date(startTime.getTime()-1000)
   def newEndTime=new Date(endTime.getTime()+1000)
   RsInMaintenanceSchedule.addObjectSchedule(objectName,info,newStartTime,newEndTime)
   assert(false) //should throw exception here
}
catch(e)
{
  println e
}
assert(RsInMaintenanceSchedule.countHits("objectName:${objectName.exactQuery()}")==1)


try{
   def newStartTime=new Date(startTime.getTime()+1000)
   def newEndTime=new Date(endTime.getTime()-1000)
   RsInMaintenanceSchedule.addObjectSchedule(objectName,info,newStartTime,newEndTime)
   assert(false) //should throw exception here
}
catch(e)
{
  println e
}
assert(RsInMaintenanceSchedule.countHits("objectName:${objectName.exactQuery()}")==1)

try{
   def newEndTime=new Date(endTime.getTime()+1000)
   RsInMaintenanceSchedule.addObjectSchedule(objectName,info,startTime,newEndTime)
   assert(false) //should throw exception here
}
catch(e)
{
  println e
}
assert(RsInMaintenanceSchedule.countHits("objectName:${objectName.exactQuery()}")==1)


try{
   def newStartTime=new Date(startTime.getTime()-1000)
   RsInMaintenanceSchedule.addObjectSchedule(objectName,info,newStartTime,endTime)
   assert(false) //should throw exception here
}
catch(e)
{
  println e
}
assert(RsInMaintenanceSchedule.countHits("objectName:${objectName.exactQuery()}")==1)

//now we will try valid
//before existing
def newStartTime=new Date(startTime.getTime()-1000)
def newEndTime=startTime;

RsInMaintenanceSchedule.addObjectSchedule(objectName,info,newStartTime,newEndTime)
assert(RsInMaintenanceSchedule.countHits("objectName:${objectName.exactQuery()}")==2)

//after existing
newStartTime=endTime;
newEndTime=new Date(endTime.getTime()+1000)


RsInMaintenanceSchedule.addObjectSchedule(objectName,info,newStartTime,newEndTime)
assert(RsInMaintenanceSchedule.countHits("objectName:${objectName.exactQuery()}")==3)