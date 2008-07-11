/*
Non-key property rename
	
	Verify: Property is renamed. Values for existing instances are lost. 
*/	

SmartsObject.list()*.remove();
SmartsObject.add([name:'route1',creationClassName:'Router',smartDs:'eastRegionDs', prop3:'prop3 value route1', prop4:44]);
SmartsObject.add([name:'route2',creationClassName:'Router',smartDs:'eastRegionDs', prop1:'prop1 value route2', prop2:11, prop3:'prop3 value route2', prop4:44, prop5:'prop5 value route2', prop6: 66, prop7:'prop7 for route2', prop8:88]);

def count = SmartsObject.search("*").results.size();
assert count == 2

def myModel = Model.findByName("SmartsObject");
def prop= ModelProperty.findByNameAndModel("prop1",myModel);
prop.name = 'prop1_1';

prop= ModelProperty.findByNameAndModel("prop2",myModel);
prop.name = 'prop1_2';

prop= ModelProperty.findByNameAndModel("prop3",myModel);
prop.name = 'prop1_3';

prop= ModelProperty.findByNameAndModel("prop4",myModel);
prop.name = 'prop1_4';

prop= ModelProperty.findByNameAndModel("prop5",myModel);
prop.name = 'prop1_5';

prop= ModelProperty.findByNameAndModel("prop6",myModel);
prop.name = 'prop1_6';

prop= ModelProperty.findByNameAndModel("prop7",myModel);
prop.name = 'prop1_7';

prop= ModelProperty.findByNameAndModel("prop8",myModel);
prop.name = 'prop1_8';

return "Model is modified. Generate SmartsObject and reload application!";
