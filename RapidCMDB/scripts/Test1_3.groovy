import datasource.BaseDatasource

/*
Key property add test (DATA IS KEPT????????????) and remove key property
    Verify: SmartsObject table is dropped.

*/	
// TEST AFTER MANUAL MODEL GENERATION AND APPLICATION RELOAD
println "TEST RESULTS FOR TEST1_3"
def results = SmartsObject.search("*").results;
assert results.size() == 0

SmartsObject.add([name:'route2',creationClassName:'Router',smartDs:'eastRegionDs']);
SmartsObject.add([name:'host1', creationClassName:'Host', smartDs:'eastRegionDs']);
SmartsObject.add([name:'host2', creationClassName:'Host', smartDs:'eastRegionDs']);

// REMOVE A KEY PROPERTY
def myModel = Model.findByName("SmartsObject");
def rcmdbDS = BaseDatasource.findByName("RCMDB");
def rcmdbModelDatasource = ModelDatasource.findByModelAndDatasource(myModel,rcmdbDS);
def prop= ModelProperty.findByNameAndModel("prop3",myModel);
prop.delete(flush:true);

return "Model is modified. Generate SmartsObject and reload application!";
