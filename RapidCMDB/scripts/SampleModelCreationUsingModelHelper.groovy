import datasource.*
import model.*

println "=========== STARTING THE PROCESS ==========="
def datasources = [];
def props = [];

// SmartsObject
// ModelHelper is the utility class to make the model generation  from script easier.

def myModel = Model.findByName("Ip");
if (myModel!=null)	myModel.delete(flush:true);

myModel = Model.findByName("Port");
if (myModel!=null)	myModel.delete(flush:true);

myModel = Model.findByName("Card");
if (myModel!=null)	myModel.delete(flush:true);

myModel = Model.findByName("DeviceInterface");
if (myModel!=null)	myModel.delete(flush:true);

myModel = Model.findByName("DeviceAdapter");
if (myModel!=null)	myModel.delete(flush:true);

myModel = Model.findByName("DeviceComponent");
if (myModel!=null)	myModel.delete(flush:true);

myModel = Model.findByName("Link");
if (myModel!=null)	myModel.delete(flush:true);

myModel = Model.findByName("Device");
if (myModel!=null)	myModel.delete(flush:true);

myModel = Model.findByName("SmartsObject");
if (myModel!=null)	myModel.delete(flush:true);


def modelhelperSO = new ModelHelper("SmartsObject"); 

/* Define properties. Note that :
1. 'propertyDatasource' takes the Datasource name
2. 'propertySpecifyingDatasource' takes the dynamic datasource property name
3. all the datasources, except RCMDB, refered to are assumed to be created before running the script
*/

def name = [name:"name", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:"RCMDB"];
def creationClassName = [name:"creationClassName", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:"RCMDB"];
def dynamicDs= [name:"smartDs", type:ModelProperty.stringType, blank:true, lazy:false, propertyDatasource:"RCMDB"];
def displayName= [name:"displayName", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"DisplayName", propertySpecifyingDatasource:"smartDs"];

props.add(name);
props.add(creationClassName);
props.add(dynamicDs);
props.add(displayName);

// Identify the datasources. Since the user knows whether it is a Smarts, Netcool or DB datasource,
// it makes sense to let the the user to get the datasource[s] and pass it to the ModelHelper. 
def rcmdbDs = BaseDatasource.findByName("RCMDB");
def rcmdbKey1 = [name:"name"];
def rcmdbKey2 = [name:"creationClassName"];
datasources.add([datasource:rcmdbDs, master:true, keys:[rcmdbKey1, rcmdbKey2]]);

// Specify keys to datasources. nameInDs is optional. If not given, it is assumed that 
// the name is the same as nameInDs
def eastRegionDs = SmartsTopologyDatasource.findByName("eastRegionDs");
def eastRegionDsKey1 = [name:"name",nameInDs:"Name"];
def eastRegionDsKey2 = [name:"creationClassName",nameInDs:"CreationClassName"];
datasources.add([datasource:eastRegionDs, master:false, keys:[eastRegionDsKey1, eastRegionDsKey2]]);

def westRegionDs = SmartsTopologyDatasource.findByName("westRegionDs");
def westRegionDsKey1 = [name:"name",nameInDs:"Name"];
def westRegionDsKey2 = [name:"creationClassName",nameInDs:"CreationClassName"];
datasources.add([datasource:westRegionDs, master:false, keys:[westRegionDsKey1, westRegionDsKey2]]);

// The order of setting datasources, properties, and keymappings should be as follows:
modelhelperSO.datasources = datasources;
modelhelperSO.props = props; 
modelhelperSO.setKeyMappings();  

// Device 
// Version of ModelHelper where a model class extends from a parent class
// You don't need to define datasources from parent class or 
// you can directly use the properties of parent class in propertySpecifyingDatasource
def modelhelperDevice = new ModelHelper("Device", "SmartsObject");

def location= [name:"location", type:ModelProperty.stringType, blank:true, lazy:false, propertyDatasource:"RCMDB"];
def model= [name:"model", type:ModelProperty.stringType, blank:true, lazy:false, propertyDatasource:"RCMDB"];
def ipAddress=[name:"ipAddress", type:ModelProperty.stringType, blank:true, lazy:false, propertyDatasource:"RCMDB"];
def snmpReadCommunity= [name:"snmpReadCommunity", type:ModelProperty.stringType, blank:true, lazy:false, propertyDatasource:"RCMDB"];
def vendor= [name:"vendor", type:ModelProperty.stringType, blank:true, lazy:false, propertyDatasource:"RCMDB"];
def discoveredLastAt= [name:"discoveredLastAt", type:ModelProperty.numberType, blank:false, lazy:true, defaultValue:0, nameInDatasource:"DiscoveredLastAt", propertySpecifyingDatasource:"smartDs"];
def description= [name:"description", type:ModelProperty.stringType, blank:false, lazy:true, nameInDatasource:"Description", propertySpecifyingDatasource:"smartDs"];
def discoveryErrorInfo= [name:"discoveryErrorInfo", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"DiscoveryErrorInfo", propertySpecifyingDatasource:"smartDs"];
def discoveryTime= [name:"discoveryTime", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"DiscoveryTime", propertySpecifyingDatasource:"smartDs"];

props = [];
props.add(location);
props.add(model);
props.add(ipAddress);
props.add(snmpReadCommunity);
props.add(vendor);
props.add(discoveredLastAt);
props.add(description);
props.add(discoveredLastAt);
props.add(description);
props.add(discoveryErrorInfo);
props.add(discoveryTime);

modelhelperDevice.props = props;

// Link
def modelhelperLink = new ModelHelper("Link", "SmartsObject");
def a_AdminStatus= [name:"a_AdminStatus", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"A_AdminStatus", propertySpecifyingDatasource:"smartDs"];
def a_OperStatus= [name:"a_OperStatus", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"A_OperStatus", propertySpecifyingDatasource:"smartDs"];
def a_DisplayName= [name:"a_DisplayName", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"A_DisplayName", propertySpecifyingDatasource:"smartDs"];
def z_AdminStatus= [name:"z_AdminStatus", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"Z_AdminStatus", propertySpecifyingDatasource:"smartDs"];
def z_OperStatus= [name:"z_OperStatus", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"Z_OperStatus", propertySpecifyingDatasource:"smartDs"];
def z_DisplayName= [name:"z_DisplayName", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"Z_DisplayName", propertySpecifyingDatasource:"smartDs"];

props = [];
props.add(a_AdminStatus);
props.add(a_OperStatus);
props.add(a_DisplayName);
props.add(z_AdminStatus);
props.add(z_OperStatus);
props.add(z_DisplayName);

modelhelperLink.props = props;

// DeviceComponent
def modelhelperDeviceComponent = new ModelHelper("DeviceComponent", "SmartsObject");

// DeviceAdapter 
def modelhelperDeviceAdapter = new ModelHelper("DeviceAdapter", "DeviceComponent");
description= [name:"description", type:ModelProperty.stringType, blank:true, lazy:false, propertyDatasource:"RCMDB"];
def macAddress= [name:"macAddress", type:ModelProperty.stringType, blank:true, lazy:false, propertyDatasource:"RCMDB"];
def type= [name:"type", type:ModelProperty.stringType, blank:true, lazy:false, propertyDatasource:"RCMDB"];
def isManaged= [name:"isManaged", type:ModelProperty.stringType, blank:true, lazy:false, propertyDatasource:"RCMDB"];
def maxSpeed= [name:"maxSpeed", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"MaxSpeed", propertySpecifyingDatasource:"smartDs"];
def adminStatus= [name:"adminStatus", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"AdminStatus", propertySpecifyingDatasource:"smartDs"];
def maxTransferUnit= [name:"maxTransferUnit", type:ModelProperty.numberType, blank:false, lazy:true, defaultValue:0, nameInDatasource:"MaxTransferUnit", propertySpecifyingDatasource:"smartDs"];
def mode= [name:"mode", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"Mode", propertySpecifyingDatasource:"smartDs"];
def status= [name:"status", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"Status", propertySpecifyingDatasource:"smartDs"];
def duplexMode= [name:"duplexMode", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"DuplexMode", propertySpecifyingDatasource:"smartDs"];
def currentUtilization= [name:"currentUtilization", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"CurrentUtilization",propertySpecifyingDatasource:"smartDs"];
def operStatus= [name:"operStatus", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"OperStatus", propertySpecifyingDatasource:"smartDs"];
def isFlapping= [name:"isFlapping", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"IsFlapping", propertySpecifyingDatasource:"smartDs"];
def deviceId= [name:"deviceID", type:ModelProperty.stringType, blank:true, lazy:true,  nameInDatasource:"DeviceID", propertySpecifyingDatasource:"smartDs"];
def peerSystemName= [name:"peerSystemName", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"PeerSystemName", propertySpecifyingDatasource:"smartDs"];

props = [];
props.add(description);
props.add(macAddress);
props.add(type);
props.add(isManaged);
props.add(maxSpeed);
props.add(adminStatus);
props.add(maxTransferUnit);
props.add(mode);
props.add(status);
props.add(duplexMode);
props.add(currentUtilization);
props.add(operStatus);
props.add(isFlapping);
props.add(deviceId);
props.add(peerSystemName);

modelhelperDeviceAdapter.props = props;

// Ip
def modelhelperIp = new ModelHelper("Ip", "DeviceComponent");
ipAddress= [name:"ipAddress", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:"RCMDB"];
def netMask= [name:"netMask", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"NetMask", propertySpecifyingDatasource:"smartDs"];
def interfaceAdminStatus= [name:"interfaceAdminStatus", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"InterfaceAdminStatus", propertySpecifyingDatasource:"smartDs"];
def interfaceName= [name:"interfaceName", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"InterfaceName", propertySpecifyingDatasource:"smartDs"];
def interfaceOperStatus= [name:"interfaceOperStatus", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"InterfaceOperStatus", propertySpecifyingDatasource:"smartDs"];
def ipStatus= [name:"ipStatus", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"IPStatus", propertySpecifyingDatasource:"smartDs"];

props = [];
props.add(ipAddress);
props.add(netMask);
props.add(interfaceAdminStatus);
props.add(interfaceName);
props.add(interfaceOperStatus);
props.add(ipStatus);

modelhelperIp.props = props;

// DeviceInterface
def modelhelperDeviceInterface = new ModelHelper("DeviceInterface", "DeviceAdapter");
def interfaceKey= [name:"interfaceKey", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"InterfaceKey", propertySpecifyingDatasource:"smartDs"];

props = [];
props.add(interfaceKey);

modelhelperIp.props = props;


def modelhelperPort = new ModelHelper("Port", "DeviceAdapter");
def portType= [name:"portType", type:ModelProperty.stringType, blank:true, lazy:false, propertyDatasource:"RCMDB"];
def portNumber= [name:"portNumber", type:ModelProperty.stringType, blank:true, lazy:false, nameInDatasource:"PortNumber", propertySpecifyingDatasource:"smartDs"];
def portKey= [name:"portKey", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"PortKey", propertySpecifyingDatasource:"smartDs"];

props = [];
props.add(portType);
props.add(portNumber);
props.add(portKey);

modelhelperPort.props = props;

def modelhelperCard = new ModelHelper("Card", "DeviceComponent");
status= [name:"status", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"Status", propertySpecifyingDatasource:"smartDs"];

props = [];
props.add(status);
modelhelperCard.props = props;


// Relation is supposed to be initiated from the first model class. 
// Therefore, you only provide the secondClass name.
// Param1: Second class name
// Param2: Relation name from first class to second class
// Param3: Reverse relation name from second class to first class
// Param4: Cardinality to first class
// Param5: Cardinality to second class

modelhelperDevice.createRelation("DeviceComponent", "composedOf", "partOf", ModelRelation.ONE, ModelRelation.MANY);
modelhelperDevice.createRelation("Link", "connectedVia", "connectedSystems", ModelRelation.MANY, ModelRelation.MANY);
modelhelperLink.createRelation("DeviceAdapter", "connectedTo", "connectedVia", ModelRelation.ONE, ModelRelation.MANY);
modelhelperDeviceAdapter.createRelation("Card", "realizedBy", "realises", ModelRelation.MANY, ModelRelation.ONE);
modelhelperDevice.createRelation("Ip", "hostsAccessPoints", "hostedBy", ModelRelation.ONE, ModelRelation.MANY);
modelhelperDeviceInterface.createRelation("Ip", "underlying", "layeredOver", ModelRelation.ONE, ModelRelation.ONE);

return "Success"