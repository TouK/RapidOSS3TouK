/*
Relation rename
	
	Verify: Relation is renamed back to original. Values for existing instances are lost. 
*/	

println "TEST RESULTS FOR TEST5_3"
def result = DeviceInterface.get(name:'devinterface1',creationClassName:'DevInterface');
assert result.underlying == null;
result = Ip.get(name:'ip1', creationClassName:'Ip');
assert result.layeredOver == null;

result = DeviceInterface.get(name:'devinterface2',creationClassName:'DevInterface');
assert result.underlying == null;
result = Ip.get(name:'ip2', creationClassName:'Ip');
assert result.layeredOver == null;


result = Device.get(name:'device1',creationClassName:'Device');
assert result.connectedVia.size() == 0;
assert result.composedOf.size() == 0;

result = Device.get(name:'device2',creationClassName:'Device');
assert result.connectedVia.size() == 0;
assert result.composedOf.size() == 0;

result = Device.get(name:'device3',creationClassName:'Device');
assert result.connectedVia.size() == 0;
assert result.composedOf.size() == 0;

result = Link.get(name:'link1', creationClassName:'Link');
assert result.connectedSystems.size() == 0;

result = Link.get(name:'link2', creationClassName:'Link');
assert result.connectedSystems.size() == 0;

result = Link.get(name:'link3', creationClassName:'Link');
assert result.connectedSystems.size() == 0;

result = DeviceComponent.get(name:'comp1',creationClassName:'Component');
assert result.partOf == null;

result = DeviceComponent.get(name:'comp2',creationClassName:'Component');
assert result.partOf == null;


/* 	Verify: Relation is renamed back to original. Values for existing instances are lost. 

def result = DeviceInterface.get(name:'devinterface1',creationClassName:'DevInterface');
assert result.underlying.name == "ip1";
result = Ip.get(name:'ip1', creationClassName:'Ip');
assert result.layeredOver.name == "devinterface1";

result = DeviceInterface.get(name:'devinterface2',creationClassName:'DevInterface');
assert result.underlying.name == "ip2";
result = Ip.get(name:'ip2', creationClassName:'Ip');
assert result.layeredOver.name == "devinterface2";


result = Device.get(name:'device1',creationClassName:'Device');
assert result.connectedVia.size() == 3;
result.connectedVia.each{
	assert it.name == "link1" || it.name == "link2" || it.name == "link3";
}
assert result.composedOf.size() == 2;
result.composedOf.each{
	assert it.name == "comp1" || it.name == "comp2";
}

result = Device.get(name:'device2',creationClassName:'Device');
assert result.connectedVia.size() == 1;
result.connectedVia.each{
	assert it.name == "link2";
}
assert result.composedOf.size() == 0;

result = Device.get(name:'device3',creationClassName:'Device');
assert result.connectedVia.size() == 1;
result.connectedVia.each{
	assert it.name == "link2";
}
assert result.composedOf.size() == 0;

result = Link.get(name:'link1', creationClassName:'Link');
assert result.connectedSystems.size() == 1;
result.connectedSystems.each{
	assert it.name == "device1";
}

result = Link.get(name:'link2', creationClassName:'Link');
assert result.connectedSystems.size() == 1;
result.connectedSystems.each{
	assert it.name == "device1" || it.name == "device2" || it.name == "device3"; 
}

result = Link.get(name:'link3', creationClassName:'Link');
assert result.connectedSystems.size() == 1;
result.connectedSystems.each{
	assert it.name == "device1";
}

result = DeviceComponent.get(name:'comp1',creationClassName:'Component');
result.partOfDevice.name == "device1";

result = DeviceComponent.get(name:'comp2',creationClassName:'Component');
assert result.partOf.name == "device1";
*/
return "Success";
