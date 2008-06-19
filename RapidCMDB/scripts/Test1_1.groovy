import datasource.BaseDatasource

/*
Non-key property add
    i.blank false (all instances updated with default values)
    ii.blank true (all instances with blank values)
    iii.Repeat for number, and string data types

    Verify: New properties are added. Values for existing instances are not lost. For those
            properties where blank is false, default values are filled in for existing instances.
*/	

SmartsObject.list()*.remove();

SmartsObject.add([name:'route1',creationClassName:'Router',smartDs:'eastRegionDs']);
SmartsObject.add([name:'host1', creationClassName:'Host', smartDs:'eastRegionDs']);
SmartsObject.add([name:'host2', creationClassName:'Host', smartDs:'eastRegionDs']);

def count = SmartsObject.search("*").results.size();
assert count == 3

def myModel = Model.findByName("SmartsObject");
def rcmdbDS = BaseDatasource.findByName("RCMDB");
def rcmdbModelDatasource = ModelDatasource.findByModelAndDatasource(myModel,rcmdbDS);

def newProp= new ModelProperty(name:"prop1", type:ModelProperty.stringType, blank:true, lazy:false, propertyDatasource:rcmdbModelDatasource, model:myModel).save();
newProp= new ModelProperty(name:"prop2", type:ModelProperty.numberType, blank:true, lazy:false, propertyDatasource:rcmdbModelDatasource, model:myModel).save();
newProp= new ModelProperty(name:"prop3", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:rcmdbModelDatasource, model:myModel).save();
newProp= new ModelProperty(name:"prop4", type:ModelProperty.numberType, blank:false, lazy:false, propertyDatasource:rcmdbModelDatasource, model:myModel).save();
newProp= new ModelProperty(name:"prop5", type:ModelProperty.stringType, blank:false, defaultValue: "my default for blank false", lazy:false, propertyDatasource:rcmdbModelDatasource, model:myModel).save();
newProp= new ModelProperty(name:"prop6", type:ModelProperty.numberType, blank:false, defaultValue: 9999,lazy:false, propertyDatasource:rcmdbModelDatasource, model:myModel).save();
newProp= new ModelProperty(name:"prop7", type:ModelProperty.stringType, blank:true, defaultValue: "my default for blank true", lazy:false, propertyDatasource:rcmdbModelDatasource, model:myModel).save();
newProp= new ModelProperty(name:"prop8", type:ModelProperty.numberType, blank:true, defaultValue: 6666,lazy:false, propertyDatasource:rcmdbModelDatasource, model:myModel).save();


return "Model is modified. Generate SmartsObject and reload application!";
