/*
Non-key property remove
	
	Verify: Property is removed. Values for existing instances are removed. 
*/	

import datasource.*
import model.*

SmartsObject.list()*.remove();
SmartsObject.add([name:'route2',creationClassName:'Router',smartDs:'eastRegionDs', prop1_1:'prop1 value route2', prop4:11]);
SmartsObject.add([name:'host1', creationClassName:'Host', smartDs:'eastRegionDs', prop1_1:'prop1 value host1', prop4:22]);
SmartsObject.add([name:'host2', creationClassName:'Host', smartDs:'eastRegionDs', prop1_1:'prop1 value host2', prop4:33]);
SmartsObject.add([name:'route3', creationClassName:'Router', smartDs:'eastRegionDs', prop1_1:'prop1 value route3', prop4:44]);

def count = SmartsObject.search("*").results.size();
assert count == 4

def myModel = Model.findByName("SmartsObject");
def prop= ModelProperty.findByNameAndModel("prop4",myModel);
prop.delete(flush:true);

return "Model is modified. Generate SmartsObject and reload application!";
