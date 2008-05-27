/*
Relation remove and instance deletion from a many side of 1-m and m-m relation. 
	
	Verify: Relation is removed. Values for existing instances are lost. 
*/	

import datasource.*
import model.*
import com.ifountain.rcmdb.domain.generation.ModelGenerator

SmartsObject.list()*.remove();

def di1 = DeviceInterface.add(name:'devinterface1',creationClassName:'DevInterface');
def di2 = DeviceInterface.add(name:'devinterface2', creationClassName:'DevInterface');
def ip1 = Ip.add(name:'ip1', creationClassName:'Ip', ipAddress:'192.168.1.1');
def ip2 = Ip.add(name:'ip2', creationClassName:'Ip', ipAddress:'192.168.1.2');

di1.addRelation(underlying:ip1);
ip2.addRelation(layeredOver:di2);

def dev1 = Device.add(name:'device1',creationClassName:'Device');
def dev2 = Device.add(name:'device2', creationClassName:'Device');
def dev3 = Device.add(name:'device3', creationClassName:'Device');
def link1 = Link.add(name:'link1', creationClassName:'Link');
def link2 = Link.add(name:'link2', creationClassName:'Link');
def link3 = Link.add(name:'link3', creationClassName:'Link');

dev1.addRelation(connectedVia:[link1,link3]);
dev3.addRelation(connectedVia:[link2]);
link2.addRelation(connectedSystems:[dev1,dev2]);

def devComp1 = DeviceComponent.add(name:'comp1',creationClassName:'Component');
def devComp2 = DeviceComponent.add(name:'comp2', creationClassName:'Component');

dev1.addRelation(composedOf:[devComp1]);
devComp2.addRelation(partOf:[dev1]);

def result = Device.get(name:'device1',creationClassName:'Device');
result.connectedVia.each{
	if (it.name == "link1"){
		it.remove();
	}
}

result.composedOf.each{
	if (it.name == "comp2"){
		it.remove();
	}
}

result = Device.get(name:'device1',creationClassName:'Device');
result.connectedVia.each{
	assert it.name!="link1";	
}

result.composedOf.each{
	assert it.name!="comp2";		
}

def model1 = Model.findByName("DeviceInterface");
def model2 = Model.findByName("Ip");
def relation= ModelRelation.findByFirstNameAndFirstModel("underlying", model1);

relation.delete(flush:true);

model1 = Model.findByName("Device");
model2 = Model.findByName("Link");
relation= ModelRelation.findByFirstNameAndFirstModel("connectedVia", model1);

relation.delete(flush:true);

model2 = Model.findByName("DeviceComponent");
relation= ModelRelation.findByFirstNameAndFirstModel("composedOf", model1);

relation.delete(flush:true);

return "Model is modified. Generate SmartsObject and reload application!";
