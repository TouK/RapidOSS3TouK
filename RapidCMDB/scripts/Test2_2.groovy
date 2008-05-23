/*
Non-key property rename
	
	Verify: Property is renamed. Values for existing instances are not lost. 
*/	

// TEST AFTER MANUAL MODEL GENERATION AND APPLICATION RELOAD
println "TEST RESULTS FOR TEST2_2"
def result = SmartsObject.get(name:'route3', creationClassName:'Router');
println "route3.prop1_1: $prop1 value route3"; 
assert result.prop1_1 == "prop1 value route3"; 

def myModel = Model.findByName("SmartsObject");
def prop= ModelProperty.findByNameAndModel("prop1_1",myModel);
prop.name = 'prop1';
prop.save();

return "Success";
