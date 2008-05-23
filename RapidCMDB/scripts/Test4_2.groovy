/*
Non-key property updated
	
	Verify: The property is updated. The instances ????????????????????????????
*/	

import model.*
import datasource.*

// TEST AFTER MANUAL MODEL GENERATION AND APPLICATION RELOAD
println "TEST RESULTS FOR TEST4_2"

def results = SmartsObject.search("*").results;
for (i in 0..results.size()-1){
	assert results[i].displayName == "RCMDB_Default";
	assert results[i].smartDs == -1111;
}

SmartsObject.add(name:'route6',creationClassName:'Router', smartDs: 6, displayName:"ROUTER DISPLAY NAME");
result = SmartsObject.add(name:'host1', creationClassName:'Host');
println result.errors;  // assert for error message here since displayName and smartDs properties are updated to be blank=false

result = SmartsObject.get(name:'route2',creationClassName:'Router');
println "result.smartDs: $result.smartDs"
assert result.smartDs == 6;

def myModel = Model.findByName("SmartsObject");
def eastRegionDs = SmartsTopologyDatasource.findByName("eastRegionDs");
def eastRegionModelDatasource = ModelDatasource.findByModelAndDatasource(myModel,eastRegionDs);

def prop= ModelProperty.findByNameAndModel("displayName",myModel);
prop.propertyDatasource = null;
prop.propertySpecifyingDatasource = ModelProperty.findByModelAndName(myModel,"smartDs");
prop.type = ModelProperty.stringType;
prop.lazy = true;
prop.blank = true;

prop.save();

prop= ModelProperty.findByNameAndModel("smartDs",myModel);
prop.type = ModelProperty.stringType;
prop.lazy = false;
prop.blank = true;

prop.save();


return "Model is modified. Generate SmartsObject and reload application!";
