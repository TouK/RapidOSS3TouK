import datasource.*
import model.*
println "==========================================================================="

myModel = Model.findByName("Device");
if (myModel!=null)	myModel.delete(flush:true);

def myModel = Model.findByName("SmartsObject");
if (myModel!=null)	myModel.delete(flush:true);



def datasources = [];
def properties = [];
def relations = [];


// SMARTSOBJECT

def modelhelper = new ModelHelper("SmartsObject");

def name = ['name':'name', 'type':ModelProperty.stringType, 'blank':false, 'lazy':false, 'propertyDatasource':"RCMDB"];
def creationClassName = ['name':"creationClassName", 'type':ModelProperty.stringType, 'blank':false, 'lazy':false, 'propertyDatasource':"RCMDB"];
def dynamicDs= ['name':"smartDs", 'type':ModelProperty.stringType, 'blank':true, 'lazy':false, 'propertyDatasource':"RCMDB"];
def displayName= ['name':"displayName", 'type':ModelProperty.stringType, 'blank':true, 'lazy':true, 'nameInDatasource':"DisplayName", 'propertySpecifyingDatasource':"smartDs"];

properties.add(name);
properties.add(creationClassName);
properties.add(dynamicDs);
properties.add(displayName);

def rcmdbDs = BaseDatasource.findByName("RCMDB");
def rcmdbKey1 = [name:"name"];
def rcmdbKey2 = [name:"creationClassName"];
datasources.add([datasource:rcmdbDs, master:true, keys:[rcmdbKey1, rcmdbKey2]]);

def eastRegionDs = SmartsTopologyDatasource.findByName("eastRegionDs");
def eastRegionDsKey1 = [name:"name",nameInDs:"Name"];
def eastRegionDsKey2 = [name:"creationClassName",nameInDs:"CreationClassName"];
datasources.add([datasource:eastRegionDs, master:false, keys:[eastRegionDsKey1, eastRegionDsKey2]]);


def westRegionDs = SmartsTopologyDatasource.findByName("westRegionDs");
def westRegionDsKey1 = [name:"name",nameInDs:"Name"];
def westRegionDsKey2 = [name:"creationClassName",nameInDs:"CreationClassName"];
datasources.add([datasource:westRegionDs, master:false, keys:[westRegionDsKey1, westRegionDsKey2]]);

modelhelper.setDatasources(datasources);
modelhelper.setProperties(properties);
modelhelper.setKeyMappings();

// DEVICE 
def modelhelperDevice = new ModelHelper("Device", "SmartsObject");

def location= ['name':"location", 'type':ModelProperty.stringType, 'blank':true, 'lazy':false, 'propertyDatasource':"RCMDB"];
def model= ['name':"model", 'type':ModelProperty.stringType, 'blank':true, 'lazy':false, 'propertyDatasource':"RCMDB"];
def ipAddress=['name':"ipAddress", 'type':ModelProperty.stringType, 'blank':true, 'lazy':false, 'propertyDatasource':"RCMDB"];
/*def discoveredLastAt= ['name':"discoveredLastAt", 'type':ModelProperty.numberType, 'blank':false, 'lazy':true, 'defaultValue':0, 'nameInDatasource':"DiscoveredLastAt", 'propertySpecifyingDatasource':"smartDs"];
def description= ['name':"description", 'type':ModelProperty.stringType, 'blank':false, 'lazy':true, 'nameInDatasource':"Description", 'propertySpecifyingDatasource':"smartDs"];
*/
properties = [];
properties.add(location);
properties.add(model);
properties.add(ipAddress);
// properties.add(discoveredLastAt);
// properties.add(description);

//modelhelperDevice.setDatasources(datasources);
modelhelperDevice.setProperties(properties);

//relations.add(["deviceComponent", "composedOf", "partOf", ModelRelation.ONE, ModelRelation.MANY]);

//modelhelper.relations = relations;

//modelhelper.constructModel();


