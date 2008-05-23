/*
Non-key property rename
	
	Verify: Property is renamed back to its original. Values for existing instances are not lost. 
*/	

// TEST AFTER MANUAL MODEL GENERATION AND APPLICATION RELOAD
println "TEST RESULTS FOR TEST2_3"
def result = SmartsObject.get(name:'route3', creationClassName:'Router');
println "result.prop: $result.prop1"
assert result.prop1 == "prop1 value route3";

return "Success";
