/*
Relation add
	
	Verify: Relation is added back. Values for existing instances are lost. 
*/	

import datasource.*
import model.*

def model1 = Model.findByName("DeviceInterface");
def model2 = Model.findByName("Ip");
new ModelRelation(firstModel:model1, secondModel:model2, firstName:"underlying", secondName:"layeredOver", firstCardinality:ModelRelation.ONE, secondCardinality:ModelRelation.ONE).save();

model1 = Model.findByName("Device");
model2 = Model.findByName("DeviceComponent");
new ModelRelation(firstModel:model1, secondModel:model2, firstName:"composedOf", secondName:"partOf", firstCardinality:ModelRelation.ONE, secondCardinality:ModelRelation.MANY).save();

//model2 = Model.findByName("Link");
//new ModelRelation(firstModel:model1, secondModel:model2, firstName:"connectedVia", secondName:"connectedSystems", firstCardinality:ModelRelation.ONE, secondCardinality:ModelRelation.ONE).save();

return "Model is modified. Generate SmartsObject and reload application!   RUN TEST6_3!!!";
