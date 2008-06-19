/*
Non-key property remove
	
	Verify: Add the removed property back with the same name. The old value should not reappear in the newly added property
*/	

// TEST AFTER MANUAL MODEL GENERATION AND APPLICATION RELOAD
println "TEST RESULTS FOR TEST3_3"
def result = SmartsObject.get(name:'route3', creationClassName:'Router');
println "result.prop4: $result.prop4"
assert result.prop4 == 0;   

return "Success";
