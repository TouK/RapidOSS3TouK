/*
Non-key property remove
	
	Verify: The property is removed. The instances do not have that column anymore.
*/	

import model.*
import datasource.*

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

def newProp= new ModelProperty(name:"prop4", type:ModelProperty.numberType, blank:false, lazy:false, propertyDatasource:rcmdbModelDatasource, model:myModel).save();

return "Model is modified. Generate SmartsObject and reload application!";
