/*
Key property removed
	Verify: SmartsObject table is dropped.

*/	
import datasource.*
import model.*
import com.ifountain.rcmdb.domain.generation.ModelGenerator

// TEST AFTER MANUAL MODEL GENERATION AND APPLICATION RELOAD
println "TEST RESULTS FOR TEST1_4"
def results = SmartsObject.search("*").results;
assert results.size() == 0

return "Success";
