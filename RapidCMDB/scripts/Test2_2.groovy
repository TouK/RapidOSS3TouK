/*
Non-key property rename
	
	Verify: Property is renamed. Values for existing instances are lost. Defaults are filled in.
*/	

// TEST AFTER MANUAL MODEL GENERATION AND APPLICATION RELOAD
println "TEST RESULTS FOR TEST2_2"
def result = SmartsObject.get(name:'route1', creationClassName:'Router');
println "route1.prop1_1: $result.prop1_1"; 
println "route1.prop1_2: $result.prop1_2"; 
println "route1.prop1_3: $result.prop1_3"; 
println "route1.prop1_4: $result.prop1_4"; 
println "route1.prop1_5: $result.prop1_5"; 
println "route1.prop1_6: $result.prop1_6"; 
println "route1.prop1_7: $result.prop1_7"; 
println "route1.prop1_8: $result.prop1_8"; 
assert result.prop1_1 == null; 
assert result.prop1_2 == null; 
assert result.prop1_3 == "RCMDB_Default"; 
assert result.prop1_4 == -1111; 
assert result.prop1_5 == "my default for blank true"; 
assert result.prop1_6 == 9999; 
assert result.prop1_7 == "my default for blank false"; 
assert result.prop1_8 == 6666; 

result = SmartsObject.get(name:'route2', creationClassName:'Router');
println "route2.prop1_1: $result.prop1_1"; 
println "route2.prop1_2: $result.prop1_2"; 
println "route2.prop1_3: $result.prop1_3"; 
println "route2.prop1_4: $result.prop1_4"; 
println "route2.prop1_5: $result.prop1_5"; 
println "route2.prop1_6: $result.prop1_6"; 
println "route1.prop1_7: $result.prop1_7"; 
println "route1.prop1_8: $result.prop1_8"; 
assert result.prop1_1 == null; 
assert result.prop1_2 == null; 
assert result.prop1_3 == "RCMDB_Default"; 
assert result.prop1_4 == -1111; 
assert result.prop1_5 == "my default for blank true"; 
assert result.prop1_6 == 9999; 
assert result.prop1_7 == "my default for blank false"; 
assert result.prop1_8 == 6666; 

def myModel = Model.findByName("SmartsObject");
def prop= ModelProperty.findByNameAndModel("prop1_1",myModel);
prop.name = 'prop1';
prop.save();

prop= ModelProperty.findByNameAndModel("prop1_2",myModel);
prop.name = 'prop2';
prop.save();

prop= ModelProperty.findByNameAndModel("prop1_3",myModel);
prop.name = 'prop3';
prop.save();

prop= ModelProperty.findByNameAndModel("prop1_4",myModel);
prop.name = 'prop4';
prop.save();

prop= ModelProperty.findByNameAndModel("prop1_5",myModel);
prop.name = 'prop5';
prop.save();

prop= ModelProperty.findByNameAndModel("prop1_6",myModel);
prop.name = 'prop6';
prop.save();

prop= ModelProperty.findByNameAndModel("prop1_7",myModel);
prop.name = 'prop7';
prop.save();

prop= ModelProperty.findByNameAndModel("prop1_8",myModel);
prop.name = 'prop8';
prop.save();

return "Success";
