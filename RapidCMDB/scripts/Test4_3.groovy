/*
Non-key property updated
	
	Verify: The property is updated. 
	federated to non-federated => Null or default depending blank setting
	non-federated to federated => federated values
	type change=> null
*/	

import model.*
import datasource.*

// TEST AFTER MANUAL MODEL GENERATION AND APPLICATION RELOAD
println "TEST RESULTS FOR TEST4_3"
def result = SmartsObject.get(name:'route2', creationClassName:'Router');
assert result.displayName == 'DisplayNameForRoute2';   
assert result.smartDs == null;

result = SmartsObject.get(name:'route6', creationClassName:'Router');
println "smartDs: $result.smartDs"
assert result.smartDs == null;

return "Success";
