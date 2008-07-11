/*
Relation rename
	
	Verify: Relation is renamed. Values for existing instances are lost. 
*/	

println "TEST RESULTS FOR TEST5_2"

def result = DeviceInterface.get(name:'devinterface1',creationClassName:'DevInterface');
assert result.underlyingIP == null;
result = Ip.get(name:'ip1', creationClassName:'Ip');
assert result.layeredOverDI == null;

result = DeviceInterface.get(name:'devinterface2',creationClassName:'DevInterface');
assert result.underlyingIP == null;
result = Ip.get(name:'ip2', creationClassName:'Ip');
assert result.layeredOverDI == null;


result = Device.get(name:'device1',creationClassName:'Device');
assert result.connectedViaLink.size() == 0;
assert result.composedOfParts.size() == 0;

result = Device.get(name:'device2',creationClassName:'Device');
assert result.connectedViaLink.size() == 0;
assert result.composedOfParts.size() == 0;

result = Device.get(name:'device3',creationClassName:'Device');
assert result.connectedViaLink.size() == 0;
assert result.composedOfParts.size() == 0;

result = Link.get(name:'link1', creationClassName:'Link');
assert result.connectedDevices.size() == 0;

result = Link.get(name:'link2', creationClassName:'Link');
assert result.connectedDevices.size() == 0;
result = Link.get(name:'link3', creationClassName:'Link');
assert result.connectedDevices.size() == 0;

result = DeviceComponent.get(name:'comp1',creationClassName:'Component');
assert result.partOfDevice == null;

result = DeviceComponent.get(name:'comp2',creationClassName:'Component');
assert result.partOfDevice == null;


/*
Existing instance relations are NOT lost
def result = DeviceInterface.get(name:'devinterface1',creationClassName:'DevInterface');
assert result.underlyingIP.name == "ip1";
result = Ip.get(name:'ip1', creationClassName:'Ip');
assert result.layeredOverDI.name == "devinterface1";

result = DeviceInterface.get(name:'devinterface2',creationClassName:'DevInterface');
assert result.underlyingIP.name == "ip2";
result = Ip.get(name:'ip2', creationClassName:'Ip');
assert result.layeredOverDI.name == "devinterface2";


result = Device.get(name:'device1',creationClassName:'Device');
assert result.connectedViaLink.size() == 3;
result.connectedViaLink.each{
	assert it.name == "link1" || it.name == "link2" || it.name == "link3";
}
assert result.composedOfParts.size() == 2;
result.composedOfParts.each{
	assert it.name == "comp1" || it.name == "comp2";
}

result = Device.get(name:'device2',creationClassName:'Device');
assert result.connectedViaLink.size() == 1;
result.connectedViaLink.each{
	assert it.name == "link2";
}
assert result.composedOfParts.size() == 0;

result = Device.get(name:'device3',creationClassName:'Device');
assert result.connectedViaLink.size() == 1;
result.connectedViaLink.each{
	assert it.name == "link2";
}
assert result.composedOfParts.size() == 0;

result = Link.get(name:'link1', creationClassName:'Link');
assert result.connectedDevices.size() == 1;
result.connectedDevices.each{
	assert it.name == "device1";
}

result = Link.get(name:'link2', creationClassName:'Link');
assert result.connectedDevices.size() == 1;
result.connectedDevices.each{
	assert it.name == "device1" || it.name == "device2" || it.name == "device3"; 
}

result = Link.get(name:'link3', creationClassName:'Link');
assert result.connectedDevices.size() == 1;
result.connectedDevices.each{
	assert it.name == "device1";
}


result = DeviceComponent.get(name:'comp1',creationClassName:'Component');
assert result.partOfDevice.name == "device1";

result = DeviceComponent.get(name:'comp2',creationClassName:'Component');
assert result.partOfDevice.name == "device1";
*/

def model1 = Model.findByName("DeviceInterface");
def model2 = Model.findByName("Ip");
def relation= ModelRelation.findByFirstNameAndFirstModel("underlyingIP", model1);

relation.firstName = 'underlying';
relation.secondName = 'layeredOver';

model1 = Model.findByName("Device");
model2 = Model.findByName("Link");
relation= ModelRelation.findByFirstNameAndFirstModel("connectedViaLink", model1);

relation.firstName = 'connectedVia';
relation.secondName = 'connectedSystems';

model2 = Model.findByName("DeviceComponent");
relation= ModelRelation.findByFirstNameAndFirstModel("composedOfParts", model1);

relation.firstName = 'composedOf';
relation.secondName = 'partOf';

return "Model is modified. Generate SmartsObject and reload application!";
