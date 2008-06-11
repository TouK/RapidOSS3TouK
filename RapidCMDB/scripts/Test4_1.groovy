/*
Non-key property update
	
	Verify: Property is updated. 
*/	

import datasource.*
import model.*
import com.ifountain.rcmdb.domain.generation.ModelGenerator

SmartsObject.list()*.remove();

SmartsObject.add([name:'route2',creationClassName:'Router',smartDs:'eastRegionDs', prop3:'prop3 value route2', prop4:11]);
SmartsObject.add([name:'host1', creationClassName:'Host', smartDs:'eastRegionDs', prop3:'prop3 value host1', prop4:22]);
SmartsObject.add([name:'host2', creationClassName:'Host', smartDs:'eastRegionDs', prop3:'prop3 value host2', prop4:33]);
SmartsObject.add([name:'route3', creationClassName:'Router', smartDs:'eastRegionDs', prop3:'prop3 value route3', prop4:44]);


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
// This test fails: For existing instances, it gives org.codehaus.groovy.runtime.InvokerInvocationException: java.lang.NumberFormatException: For input string: "eastRegionDs
prop= ModelProperty.findByNameAndModel("smartDs",myModel);
prop.type = ModelProperty.numberType;
prop.lazy = false;
prop.blank = false;

prop.save(flush:true);

// String to Number (blank=true, defaultValue provided by the user) expected after conversion: 0
// This test fails: Invalid default value. It should be a number.
prop= ModelProperty.findByNameAndModel("prop5",myModel);
prop.type = ModelProperty.numberType;

prop.save(flush:true);

// String to Number (blank=false, defaultValue provided by the user) expected after conversion: null
// This test fails: Invalid default value. It should be a number.
prop= ModelProperty.findByNameAndModel("prop7",myModel);
prop.type = ModelProperty.numberType;

prop.save(flush:true);


return "Model is modified. Generate SmartsObject and reload application!";
