/*
Non-key property updated
	
	Verify: The property is updated. 
	federated to non-federated => Null or default depending blank setting
	non-federated to federated => federated values
	type change=> null or default, String default provided by user has to be lost during conversion. 
				  Therefore, when a property is changed from string to number to string again, the string default no longer exists and 
				  RCMDB default is used if blank is false
*/	

import model.*
import datasource.*

// TEST AFTER MANUAL MODEL GENERATION AND APPLICATION RELOAD
println "TEST RESULTS FOR TEST4_3"
def result = SmartsObject.get(name:'route2', creationClassName:'Router');
assert result.displayName == 'DisplayNameForRoute2';   
assert result.smartDs == null;
assert results[i].prop5 == "";
assert results[i].prop7 == null;

result = SmartsObject.get(name:'route6', creationClassName:'Router');
println "smartDs: $result.smartDs"
assert result.smartDs == null;
assert results[i].prop5 == "";
assert results[i].prop7 == null;

return "Success";
