import datasource.BaseDatasource

/*
Non-key property add test and make one property a key
    Verify: New properties are added. Values for existing instances are not lost. For those
            properties where blank is false, default values are filled in for existing instances.
*/	
// TEST AFTER MANUAL MODEL GENERATION AND APPLICATION RELOAD
println "TEST RESULTS FOR TEST1_2"
def results = SmartsObject.search("*").results;
assert results.size() == 3
for (i in 0.. results.size()-1){
	println "Results : $results[i]"
	println "prop1: " + results[i].prop1
	println "prop2: " + results[i].prop2 
	println "prop3 with string default value: " + results[i].prop3 
	println "prop4 with number default value: " + results[i].prop4 
	println "prop5 with string user defined default value: " + results[i].prop5 
	println "prop6 with number user defined default value: " + results[i].prop6 
	println "prop7 with string user defined default value: " + results[i].prop7 
	println "prop8 with number user defined default value: " + results[i].prop8 
	
	assert results[i].prop1 == null;
	assert results[i].prop2 == null;
	assert results[i].prop3 == "";
	assert results[i].prop4 == 0;
	assert results[i].prop5 == "my default for blank false";
	assert results[i].prop6 == 9999;
	assert results[i].prop7 == "my default for blank true";
	assert results[i].prop8 == 6666;
}

SmartsObject.add([name:'route3',creationClassName:'Router',smartDs:'eastRegionDs', prop1:'prop1 value route3', prop2:22, prop3:'prop3 value route3', prop4:44, prop5:'prop5 value route3', prop6: 66, prop7:'prop7 for route3', prop8:88]);
def result = SmartsObject.get([name:'route3',creationClassName:'Router']);
assert result.prop1 == 'prop1 value route3';
assert result.prop2 == 22;
assert result.prop3 == 'prop3 value route3';
assert result.prop4 == 44;
assert result.prop5 == "my default for blank false";
assert result.prop6 == 66;
assert result.prop7 == "my default for blank true";
assert result.prop8 == 88;

SmartsObject.add([name:'route4',creationClassName:'Router',prop3:'prop3', prop4:44]);
result = SmartsObject.get([name:'route4',creationClassName:'Router']);
assert result.prop1 == null;
assert result.prop2 == null;
assert result.prop3 == "prop3";
assert result.prop4 == 44;
assert result.prop5 == "my default for blank false";
assert result.prop6 == 9999;
assert result.prop7 == "my default for blank true";
assert result.prop8 == 6666;

// MAKE A PROPERTY ONE OF THE KEYS: DATA IS KEPT
def myModel = Model.findByName("SmartsObject");
def rcmdbDS = BaseDatasource.findByName("RCMDB");
def rcmdbModelDatasource = ModelDatasource.findByModelAndDatasource(myModel,rcmdbDS);
def prop= ModelProperty.findByNameAndModel("prop3",myModel);

new ModelDatasourceKeyMapping(property:prop, datasource:rcmdbModelDatasource).save();

return "Model is modified. Generate SmartsObject and reload application!";
