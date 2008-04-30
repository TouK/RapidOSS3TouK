def eventid = "newevent";
def myEvent = NetcoolEvent.findByIdentifier(eventid);
println myEvent
try {
	myEvent.serverity = 3;  //should throw exception
}
catch(Exception e) {
	//assert e.toString().indexOf("No such property")>-1;
	assert e.toString().indexOf("Cannot invoke method setProperty()")>-1;
}

myEvent.netcoolclass = "555";
myEvent.addToTaskList(true);
def updateParams = ["acknowledged":1, "class":888];
try {
	myEvent.updateAtNc(updateParams); //should throw exception for class for its name here is netcoolclass
	assert (false), "Should throw exception for class"; 
}
catch(Exception e) {
	assert e.toString().indexOf("No such property")>-1;
}

updateParams.remove("class");
updateParams.put("nodes","London node");
try {
	myEvent.updateAtNc(updateParams); //should throw exception for nodes for there is a typo (nodeS)
	assert (false), "Should throw exception for Nodes"; 
}
catch(Exception e) {
	assert e.toString().indexOf("No such property")>-1;
}

myEvent.acknowledged = 0;
myEvent.setSeverity(4,"tugrul");
sleep(1000);
myEvent.severity = 3;

myEvent.netcoolclass = 666; // or 666

myEvent.identifier = "should not allow me to change Identifier!"
assert myEvent.identifier == eventid;
	
myEvent.severity = "3" // or 3. only updates Severity. No entry in Journal table
myEvent.setSeverity(3, "tugrul")  // both updates Severity and puts an entry in Journal table

myEvent.suppressescl= "4" // or 4. only updates SuppressEscl. No entry in Journal table
sleep(1000);
myEvent.setSuppressescl(2, "tugrul")  // both updates SuppressEscl and puts an entry in Journal table

myEvent.tasklist = "0";  // or 1
myEvent.addToTaskList(true);

myEvent.acknowledged = "1";  // or 1
sleep(1000);
myEvent.acknowledge(false,"okay");
myEvent.owneruid = 0; // 0 is for Root
sleep(1000);
myEvent.assign(0);

def params = [:];
params.put("severity", 0);
params.put("node", "best node");
myEvent.updateAtNc(params);
//myEvent.removeFromNc();

return "successfully executed"