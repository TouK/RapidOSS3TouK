/*
Relation type change
	
	Verify: Relation type is changed to its original. Values for existing instances are lost. 
*/	

import datasource.*
import model.*

println "TEST RESULTS FOR TEST6_2"
def result = DeviceInterface.get(name:'devinterface1',creationClassName:'DevInterface');
assert result.underlying == null;
result = Ip.get(name:'ip2', creationClassName:'Ip');
assert result.layeredOver == null;

result = Device.get(name:'device1',creationClassName:'Device');
assert result.connectedVia.size() == 0;
assert result.composedOfParts.size() == 0;

result = Device.get(name:'device2',creationClassName:'Device');
assert result.connectedVia.size() == 0;
assert result.composedOfParts.size() == 0;

result = Device.get(name:'device3',creationClassName:'Device');
assert result.connectedVia.size() == 0;
assert result.composedOfParts.size() == 0;

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


def model1 = Model.findByName("DeviceInterface");
def model2 = Model.findByName("Ip");
def relation= ModelRelation.findByFirstNameAndFirstModel("underlying", model1);

relation.firstCardinality = ModelRelation.ONE;
relation.secondCardinality = ModelRelation.ONE;
relation.save();

model1 = Model.findByName("Device");
model2 = Model.findByName("Link");
relation= ModelRelation.findByFirstNameAndFirstModel("connectedVia", model1);

relation.firstCardinality = ModelRelation.MANY;
relation.secondCardinality = ModelRelation.MANY;
relation.belongsTo = model1;
relation.save();

model2 = Model.findByName("DeviceComponent");
relation= ModelRelation.findByFirstNameAndFirstModel("composedOf", model1);

relation.firstCardinality = ModelRelation.ONE;
relation.secondCardinality = ModelRelation.MANY;
relation.save();


return "Model is modified. Generate SmartsObject and reload application!";
