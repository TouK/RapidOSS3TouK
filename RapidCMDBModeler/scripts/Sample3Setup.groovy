/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
import model.*

println "=========== STARTING THE PROCESS ==========="
def datasources = [];
def props = [];

// SmartsObject
// ModelHelper is the utility class to make the model generation  from script easier.

// Model.list()*.remove();

Model.get(name:"Ip")?.remove();
Model.get(name:"Port")?.remove();
Model.get(name:"Card")?.remove();
Model.get(name:"DeviceInterface")?.remove();
Model.get(name:"DeviceAdapter")?.remove();
Model.get(name:"DeviceComponent")?.remove();
Model.get(name:"Link")?.remove();
Model.get(name:"Device")?.remove();
Model.get(name:"SmartsObject")?.remove();

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

// Identify the datasource names. 
def rcmdbDs = DatasourceName.get(name:"RCMDB");
if(rcmdbDs == null){
    rcmdbDs = DatasourceName.add(name: "RCMDB");
}
def rcmdbKey1 = [name:"name"];
def rcmdbKey2 = [name:"creationClassName"];
datasources.add([datasource:rcmdbDs, master:true, keys:[rcmdbKey1, rcmdbKey2]]);

// Specify keys to datasources. nameInDs is optional. If not given, it is assumed that 
// the name is the same as nameInDs
def eastRegionDs = DatasourceName.get(name:"eastRegionDs");
if(eastRegionDs == null){
    eastRegionDs = DatasourceName.add(name: "eastRegionDs");
}
def eastRegionDsKey1 = [name:"name",nameInDs:"Name"];
def eastRegionDsKey2 = [name:"creationClassName",nameInDs:"CreationClassName"];
datasources.add([datasource:eastRegionDs, master:false, keys:[eastRegionDsKey1, eastRegionDsKey2]]);

def westRegionDs = DatasourceName.get(name:"westRegionDs");
if(westRegionDs == null){
    westRegionDs = DatasourceName.add(name: "westRegionDs");
}
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
def a_AdminStatus= [name:"aa_AdminStatus", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"A_AdminStatus", propertySpecifyingDatasource:"smartDs"];
def a_OperStatus= [name:"aa_OperStatus", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"A_OperStatus", propertySpecifyingDatasource:"smartDs"];
def a_DisplayName= [name:"aa_DisplayName", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"A_DisplayName", propertySpecifyingDatasource:"smartDs"];
def z_AdminStatus= [name:"zz_AdminStatus", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"Z_AdminStatus", propertySpecifyingDatasource:"smartDs"];
def z_OperStatus= [name:"zz_OperStatus", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"Z_OperStatus", propertySpecifyingDatasource:"smartDs"];
def z_DisplayName= [name:"zz_DisplayName", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"Z_DisplayName", propertySpecifyingDatasource:"smartDs"];

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


modelhelperDevice.createRelation(modelhelperLink.model, "connectedVia", "connectedSystems", ModelRelation.MANY, ModelRelation.MANY);
modelhelperLink.createRelation(modelhelperDeviceAdapter.model, "connectedTo", "connectedVia", ModelRelation.ONE, ModelRelation.MANY);
modelhelperDevice.createRelation(modelhelperIp.model, "hostsAccessPoints", "hostedBy", ModelRelation.ONE, ModelRelation.MANY);
modelhelperDevice.createRelation(modelhelperDeviceComponent.model, "composedOf", "partOf", ModelRelation.ONE, ModelRelation.MANY);
modelhelperDeviceAdapter.createRelation(modelhelperCard.model, "realizedBy", "realises", ModelRelation.MANY, ModelRelation.ONE);
modelhelperDeviceInterface.createRelation(modelhelperIp.model, "underlying", "layeredOver", ModelRelation.ONE, ModelRelation.ONE);

return "Success"