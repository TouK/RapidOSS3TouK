import datasource.BaseDatasource

/*
Non-key property remove

    Verify: The property is removed. The instances do not have that column anymore.
*/	

// TEST AFTER MANUAL MODEL GENERATION AND APPLICATION RELOAD
def myModel = Model.findByName("SmartsObject");
def rcmdbDS = BaseDatasource.findByName("RCMDB");
def rcmdbModelDatasource = ModelDatasource.findByModelAndDatasource(myModel,rcmdbDS);

def props = ModelProperty.findAllByModel(myModel);
def propnames = [];
for (prop in props){
	propnames += prop.name;
}
assert !propnames.contains('prop4')

def newProp= ModelProperty.add(name:"prop4", type:ModelProperty.numberType, blank:false, lazy:false, propertyDatasource:rcmdbModelDatasource, model:myModel);

return "Model is modified. Generate SmartsObject and reload application!";
