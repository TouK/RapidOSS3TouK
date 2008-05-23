/*
Non-key property update
	
	Verify: Property is updated. 
*/	

import datasource.*
import model.*

SmartsObject.list()*.remove();

SmartsObject.add([name:'route2',creationClassName:'Router',smartDs:'eastRegionDs']);
SmartsObject.add([name:'host1', creationClassName:'Host', smartDs:'eastRegionDs']);
SmartsObject.add([name:'host2', creationClassName:'Host', smartDs:'eastRegionDs']);
SmartsObject.add([name:'route3', creationClassName:'Router', smartDs:'eastRegionDs']);

def count = SmartsObject.search("*").results.size();
assert count == 4

def myModel = Model.findByName("SmartsObject");
def rcmdbDS = BaseDatasource.findByName("RCMDB");
def rcmdbModelDatasource = ModelDatasource.findByModelAndDatasource(myModel,rcmdbDS);

// Federated to non-federated (dynamic DS to static RCMDB)
def prop= ModelProperty.findByNameAndModel("displayName",myModel);
prop.propertyDatasource = rcmdbModelDatasource;
prop.lazy = false;
prop.blank = false;
prop.propertySpecifyingDatasource = null;

prop.save(flush:true);

// String to Number
prop= ModelProperty.findByNameAndModel("smartDs",myModel);
prop.type = ModelProperty.numberType;
prop.lazy = false;
prop.blank = false;

prop.save(flush:true);

return "Model is modified. Generate SmartsObject and reload application!";
