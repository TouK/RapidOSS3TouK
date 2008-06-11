/*
Non-key property rename
	
	Verify: Property is renamed back to its original. Values for existing instances are lost. Defaults are filled in.
*/	

// TEST AFTER MANUAL MODEL GENERATION AND APPLICATION RELOAD
println "TEST RESULTS FOR TEST2_3"
def result = SmartsObject.get(name:'route1', creationClassName:'Router');
println "route1.prop1: $result.prop1"; 
println "route1.prop2: $result.prop2"; 
println "route1.prop3: $result.prop3"; 
println "route1.prop4: $result.prop4"; 
println "route1.prop5: $result.prop5"; 
println "route1.prop6: $result.prop6"; 
println "route1.prop7: $result.prop7"; 
println "route1.prop8: $result.prop8"; 
assert result.prop1 == null; 
assert result.prop1 == null; 
assert result.prop1 == "";
assert result.prop1 == 0; 
assert result.prop1_5 == "my default for blank true"; 
assert result.prop1_6 == 9999; 
assert result.prop1_7 == "my default for blank false"; 
assert result.prop1_8 == 6666; 

result = SmartsObject.get(name:'route2', creationClassName:'Router');
println "route2.prop1: $result.prop1"; 
println "route2.prop2: $result.prop2"; 
println "route2.prop3: $result.prop3"; 
println "route2.prop4: $result.prop4"; 
println "route2.prop:  $result.prop5"; 
println "route2.prop6: $result.prop6"; 
println "route2.prop7: $result.prop7"; 
println "route2.prop8: $result.prop8"; 
assert result.prop1 == null; 
assert result.prop1 == null; 
assert result.prop1 == "";
assert result.prop1 == 0; 
assert result.prop1_5 == "my default for blank true"; 
assert result.prop1_6 == 9999; 
assert result.prop1_7 == "my default for blank false"; 
assert result.prop1_8 == 6666; 

return "Success";
