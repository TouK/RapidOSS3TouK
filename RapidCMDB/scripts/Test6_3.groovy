/*
Relation type change
	
	Verify: Relation type is changed to its original. Values for existing instances are lost. 
*/	
// ?????????????????????/ MAY NEED TO CHANGE THE TEST FOR 1-M TO M-M
println "TEST RESULTS FOR TEST6_3"
def result = DeviceInterface.get(name:'devinterface1',creationClassName:'DevInterface');
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


return "Success";
