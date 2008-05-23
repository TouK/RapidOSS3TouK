/*
Non-key property add test and make one property a key
	Verify: New properties are added. Values for existing instances are not lost. For those
			properties where blank is false, default values are filled in for existing instances.
*/	
import datasource.*
import model.*

// TEST AFTER MANUAL MODEL GENERATION AND APPLICATION RELOAD
println "TEST RESULTS FOR TEST1_2"
def results = SmartsObject.search("*").results;
assert results.size() == 3
for (i in 0.. results.size()-1){
	println "prop1: " + results[i].prop1
	println "prop2: " + results[i].prop2 
	println "prop3 with string default value: " + results[i].prop3 
	println "prop4 with number default value: " + results[i].prop4 
	
	assert results[i].prop1 == null;
	assert results[i].prop2 == null;
	assert results[i].prop3 == "RCMDB_Default";
	assert results[i].prop4 == -1111;
}

// MAKE A PROPERTY ONE OF THE KEYS
def myModel = Model.findByName("SmartsObject");
def rcmdbDS = BaseDatasource.findByName("RCMDB");
def rcmdbModelDatasource = ModelDatasource.findByModelAndDatasource(myModel,rcmdbDS);
def prop= ModelProperty.findByNameAndModel("prop3",myModel);

new ModelDatasourceKeyMapping(property:prop, datasource:rcmdbModelDatasource).save();

return "Model is modified. Generate SmartsObject and reload application!";
