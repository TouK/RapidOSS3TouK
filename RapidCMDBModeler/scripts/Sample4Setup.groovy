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
import model.*;

println "=========== STARTING THE PROCESS ==========="
def datasources = [];
def props = [];

// ModelHelper is the utility class to make the model generation  from script easier.

Model.get(name:"Port_S4")?.remove();
Model.get(name:"DeviceAdapter_S4")?.remove();
Model.get(name:"DeviceComponent_S4")?.remove();
Model.get(name:"SmartsObject_S4")?.remove();
Model.get(name:"Model1")?.remove();
Model.get(name:"Model2")?.remove();
Model.get(name:"Model3")?.remove();

/* Define properties. Note that :
1. 'propertyDatasource' takes the Datasource name
2. 'propertySpecifyingDatasource' takes the dynamic datasource property name
3. all the datasources, except RCMDB, refered to are assumed to be created before running the script
*/
def model1 = new ModelHelper("Model1");
def prop1 = [name:"prop1", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:"RCMDB"];
def prop2 = [name:"prop2", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:"RCMDB"];

props.add(prop1);
props.add(prop2);

// Identify the datasource names. 
def rcmdbDs = DatasourceName.get(name:"RCMDB");
if(rcmdbDs == null){
    rcmdbDs = DatasourceName.add(name: "RCMDB");
}
def rcmdbKey = [name:"prop1"];
datasources.add([datasource:rcmdbDs, master:true, keys:[rcmdbKey]]);

// The order of setting datasources, properties, and keymappings should be as follows:
model1.datasources = datasources;
model1.props = props; 
model1.setKeyMappings();

// Model 2
def model2 = new ModelHelper("Model2");
prop1 = [name:"prop1", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:"RCMDB"];
prop2 = [name:"prop2", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:"RCMDB"];

props.add(prop1);
props.add(prop2);

model2.datasources = datasources;
model2.props = props; 
model2.setKeyMappings();  

//Model 3
def model3 = new ModelHelper("Model3");
prop1 = [name:"prop1", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:"RCMDB"];
prop2 = [name:"prop2", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:"RCMDB"];

props.add(prop1);
props.add(prop2);

model3.datasources = datasources;
model3.props = props; 
model3.setKeyMappings();  

// SmartsObject	
def modelhelperSO = new ModelHelper("SmartsObject_S4");	
def name = [name:"name", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:"RCMDB"];
def creationClassName = [name:"creationClassName", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:"RCMDB"];
def dynamicDs= [name:"smartDs", type:ModelProperty.stringType, blank:true, lazy:false, propertyDatasource:"RCMDB"];
def displayName= [name:"displayName", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"DisplayName", propertySpecifyingDatasource:"smartDs"];

props = [];
props.add(name);
props.add(creationClassName);
props.add(dynamicDs);
props.add(displayName);

def rcmdbKey1 = [name:"name"];
def rcmdbKey2 = [name:"creationClassName"];
datasources = [];
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

// DeviceComponent
def modelhelperDeviceComponent = new ModelHelper("DeviceComponent_S4", "SmartsObject_S4");

// DeviceAdapter 
def modelhelperDeviceAdapter = new ModelHelper("DeviceAdapter_S4", "DeviceComponent_S4");
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

// Port
def modelhelperPort = new ModelHelper("Port_S4", "DeviceAdapter_S4");
def portType= [name:"portType", type:ModelProperty.stringType, blank:true, lazy:false, propertyDatasource:"RCMDB"];
def portNumber= [name:"portNumber", type:ModelProperty.stringType, blank:true, lazy:false, nameInDatasource:"PortNumber", propertySpecifyingDatasource:"smartDs"];
def portKey= [name:"portKey", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"PortKey", propertySpecifyingDatasource:"smartDs"];

props = [];
props.add(portType);
props.add(portNumber);
props.add(portKey);

modelhelperPort.props = props;

// Relation is supposed to be initiated from the first model class. 
// Therefore, you only provide the secondClass name.
// Param1: Second class name
// Param2: Relation name from first class to second class
// Param3: Reverse relation name from second class to first class
// Param4: Cardinality to first class
// Param5: Cardinality to second class

modelhelperSO.createRelation(model1.model, "one2one", "reverseOne2one",  ModelRelation.ONE, ModelRelation.ONE);
modelhelperSO.createRelation(model2.model, "one2many", "reverseOne2many", ModelRelation.ONE, ModelRelation.MANY);
//UNCOMMENT THIS WHEN CMDB-283 IS FIXED modelhelperSO.createRelation(model3.model, "many2many", "reverseMany2many",ModelRelation.MANY, ModelRelation.MANY);

return "Success"