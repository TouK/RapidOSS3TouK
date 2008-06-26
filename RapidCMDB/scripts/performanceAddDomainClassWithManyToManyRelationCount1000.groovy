def NUMBER_OF_OBJECT
def curTime
def afterTime1vs1000 = 0
def afterTime1000vs1 = 0
def afterTime100vs100v1 = 0
def afterTime100vs100v2 = 0


def device;
              
def link; 

SmartsObject.list().each{ it.remove() }

device = Device.add(name: "myDevice", creationClassName: "Device", smartsDs: "smartsDs", ipAddress: "192.168.1.1",
                location: "myLocation", model: "myModel", snmpReadCommunity: "mysnmpReadCommunity", vendor: "myVendor")
NUMBER_OF_OBJECT = 1000;
for( int i = 0; i < NUMBER_OF_OBJECT; i++ )
{
	Link.add(name: "myLink"+i, creationClassName: "Link", smartsDs: "smartsDs")
}
for( int i = 0; i < NUMBER_OF_OBJECT; i++ )
{
	link = Link.get(name: "myLink"+i, creationClassName: "Link", smartsDs: "smartsDs")
	curTime = System.currentTimeMillis()
    device.addRelation(connectedVia: link);
    afterTime1vs1000 += System.currentTimeMillis() - curTime  
}

SmartsObject.list().each{ it.remove() }

device = Device.add(name: "myDevice", creationClassName: "Device", smartsDs: "smartsDs", ipAddress: "192.168.1.1",
                location: "myLocation", model: "myModel", snmpReadCommunity: "mysnmpReadCommunity", vendor: "myVendor")
NUMBER_OF_OBJECT = 1000;
for( int i = 0; i < NUMBER_OF_OBJECT; i++ )
{
	Link.add(name: "myLink"+i, creationClassName: "Link", smartsDs: "smartsDs")
}
for( int i = 0; i < NUMBER_OF_OBJECT; i++ )
{
	link = Link.get(name: "myLink"+i, creationClassName: "Link", smartsDs: "smartsDs")
	curTime = System.currentTimeMillis()
    link.addRelation(connectedSystems: device);
    afterTime1000vs1 += System.currentTimeMillis() - curTime  
}


SmartsObject.list().each{ it.remove() }
NUMBER_OF_OBJECT = 100;
for( int i = 0; i < NUMBER_OF_OBJECT; i++ )
{
	Device.add(name: "myDevice"+i, creationClassName: "Device", smartsDs: "smartsDs", ipAddress: "192.168.1.1",
                location: "myLocation", model: "myModel", snmpReadCommunity: "mysnmpReadCommunity", vendor: "myVendor")

	Link.add(name: "myLink"+i, creationClassName: "Link", smartsDs: "smartsDs")
}
for( int i = 0; i < NUMBER_OF_OBJECT; i++ )
{
	link = Link.get(name: "myLink"+i, creationClassName: "Link", smartsDs: "smartsDs")
	for( int j = 0; j < NUMBER_OF_OBJECT; j++ )
	{
		device = Device.get(name: "myDevice"+j, creationClassName: "Device")
		curTime = System.currentTimeMillis()
	    device.addRelation(connectedVia: link);
	    afterTime100vs100v1 += System.currentTimeMillis() - curTime  
    }
}

SmartsObject.list().each{ it.remove() }
NUMBER_OF_OBJECT = 100;
for( int i = 0; i < NUMBER_OF_OBJECT; i++ )
{
	Device.add(name: "myDevice"+i, creationClassName: "Device", smartsDs: "smartsDs", ipAddress: "192.168.1.1",
                location: "myLocation", model: "myModel", snmpReadCommunity: "mysnmpReadCommunity", vendor: "myVendor")

	Link.add(name: "myLink"+i, creationClassName: "Link", smartsDs: "smartsDs")
}
for( int i = 0; i < NUMBER_OF_OBJECT; i++ )
{
	device = Device.get(name: "myDevice"+i, creationClassName: "Device")
	
	for( int j = 0; j < NUMBER_OF_OBJECT; j++ )
	{
		link = Link.get(name: "myLink"+j, creationClassName: "Link", smartsDs: "smartsDs")
		curTime = System.currentTimeMillis()
	    link.addRelation(connectedSystems: device);
	    afterTime100vs100v2 += System.currentTimeMillis() - curTime  
    }
}


SmartsObject.list().each{ it.remove() }
return "Times for the different input counts<br>" +
		"Input Count &nbsp Time<br>" +
		"afterTime1vs1000 \t ${afterTime1vs1000} ms<br>" +
		"afterTime1000vs1 \t ${afterTime1000vs1} ms<br>" +
		"afterTime100vs100v1\t${afterTime100vs100v1} ms<br>" +
		"afterTime100vs100v2\t${afterTime100vs100v2} ms<br>";
		/*"100\t${afterTime100}ms<br>";
		"1000\t${afterTime1000}\n" +
		"10000\t${afterTime10000}\n" +
		"100000\t${afterTime100000}\n";*/