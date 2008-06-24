/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Apr 7, 2008
 * Time: 10:59:07 AM
 * To change this template use File | Settings | File Templates.
 */
import model.*;
import com.ifountain.rcmdb.domain.generation.ModelGenerator;

ModelRelation.list()*.delete(flush:true);
ModelDatasourceKeyMapping.list()*.delete(flush:true);
ModelProperty.list().each{
	it.propertySpecifyingDatasource = null;
	it.save(flush:true);
}
ModelProperty.list()*.delete(flush:true);
ModelDatasource.list()*.delete(flush:true);

Model.list().each{
    it.parentModel = null;   
    it.save(flush: true);
}
Model.list()*.delete(flush:true);

def datasources = checkDatasources();

def rcmdbDS = DatasourceName.findByName("RCMDB");
if(rcmdbDS == null){
    rcmdbDS = new DatasourceName(name: "RCMDB");
    rcmdbDS.save();
}
def rcmdbModelDatasource = new ModelDatasource(datasource:rcmdbDS, master:true);
def eastRegionModelDatasource = new ModelDatasource(datasource:datasources.eastRegionDs, master:false);
def westRegionModelDatasource = new ModelDatasource(datasource:datasources.westRegionDs, master:false);

def smartsObject = new Model(name:"SmartsObject");
def name = new ModelProperty(name:"name", type:ModelProperty.stringType,  lazy:false, propertyDatasource:rcmdbModelDatasource);
def creationClassName = new ModelProperty(name:"creationClassName", type:ModelProperty.stringType,  lazy:false, propertyDatasource:rcmdbModelDatasource);
def keyMappings = [new ModelDatasourceKeyMapping(property:name, nameInDatasource:"Name", datasource:eastRegionModelDatasource),
				   new ModelDatasourceKeyMapping(property:creationClassName, nameInDatasource:"CreationClassName", datasource:eastRegionModelDatasource),
				   new ModelDatasourceKeyMapping(property:name, nameInDatasource:"Name", datasource:westRegionModelDatasource),
				   new ModelDatasourceKeyMapping(property:creationClassName, nameInDatasource:"CreationClassName", datasource:westRegionModelDatasource),
                   new ModelDatasourceKeyMapping(property:name, datasource:rcmdbModelDatasource),
                   new ModelDatasourceKeyMapping(property:creationClassName, datasource:rcmdbModelDatasource)];
def smartDs= new ModelProperty(name:"smartDs", type:ModelProperty.stringType,  lazy:false, propertyDatasource:rcmdbModelDatasource);
def displayName= new ModelProperty(name:"displayName", type:ModelProperty.stringType,  lazy:true, nameInDatasource:"DisplayName", propertySpecifyingDatasource:smartDs);
smartsObject = constructModel(smartsObject, [creationClassName, name, smartDs, displayName], [rcmdbModelDatasource, eastRegionModelDatasource, westRegionModelDatasource], keyMappings);

/*def redundancyGroup = Model(name:"RedundancyGroup");
modelDatasource = new ModelDatasource(datasource:rcmdbDatasource, master:true);
creationClassName = new ModelProperty(name:"creationClassName", type:ModelProperty.stringType,  lazy:false, propertyDatasource:modelDatasource);
name = new ModelProperty(name:"name", type:ModelProperty.stringType,  lazy:false, propertyDatasource:modelDatasource);
keyMappings = [new ModelDatasourceKeyMapping(property:creationClassName, datasource:modelDatasource),
                   new  ModelDatasourceKeyMapping(property:name, datasource:modelDatasource)]
redundancyGroup = constructModel(redundancyGroup, [creationClassName, name], [modelDatasource], keyMappings);
*/
def device = new Model(name:"Device", parentModel:smartsObject);
def location= new ModelProperty(name:"location", type:ModelProperty.stringType,  lazy:false, propertyDatasource:rcmdbModelDatasource);
def model= new ModelProperty(name:"model", type:ModelProperty.stringType,  lazy:false, propertyDatasource:rcmdbModelDatasource);
def ipAddress= new ModelProperty(name:"ipAddress", type:ModelProperty.stringType,  lazy:false, propertyDatasource:rcmdbModelDatasource);
def snmpReadCommunity= new ModelProperty(name:"snmpReadCommunity", type:ModelProperty.stringType,  lazy:false, propertyDatasource:rcmdbModelDatasource);
def vendor= new ModelProperty(name:"vendor", type:ModelProperty.stringType,  lazy:false, propertyDatasource:rcmdbModelDatasource);
def discoveredLastAt= new ModelProperty(name:"discoveredLastAt", type:ModelProperty.numberType,  lazy:true, defaultValue:0, nameInDatasource:"DiscoveredLastAt", propertySpecifyingDatasource:smartDs);
def description= new ModelProperty(name:"description", type:ModelProperty.stringType,  lazy:true, nameInDatasource:"Description", propertySpecifyingDatasource:smartDs);
def discoveryErrorInfo= new ModelProperty(name:"discoveryErrorInfo", type:ModelProperty.stringType,  lazy:true, nameInDatasource:"DiscoveryErrorInfo", propertySpecifyingDatasource:smartDs);
def discoveryTime= new ModelProperty(name:"discoveryTime", type:ModelProperty.stringType,  lazy:true, nameInDatasource:"DiscoveryTime", propertySpecifyingDatasource:smartDs);
constructModel(device, [location,model,ipAddress,snmpReadCommunity,vendor,discoveredLastAt,description,discoveryErrorInfo,discoveryTime], [], []);

def link = new Model(name:"Link", parentModel:smartsObject);
def a_AdminStatus= new ModelProperty(name:"a_AdminStatus", type:ModelProperty.stringType,  lazy:true, nameInDatasource:"A_AdminStatus", propertySpecifyingDatasource:smartDs);
def a_OperStatus= new ModelProperty(name:"a_OperStatus", type:ModelProperty.stringType,  lazy:true, nameInDatasource:"A_OperStatus", propertySpecifyingDatasource:smartDs);
def a_DisplayName= new ModelProperty(name:"a_DisplayName", type:ModelProperty.stringType,  lazy:true, nameInDatasource:"A_DisplayName", propertySpecifyingDatasource:smartDs);
def z_AdminStatus= new ModelProperty(name:"z_AdminStatus", type:ModelProperty.stringType,  lazy:true, nameInDatasource:"Z_AdminStatus", propertySpecifyingDatasource:smartDs);
def z_OperStatus= new ModelProperty(name:"z_OperStatus", type:ModelProperty.stringType,  lazy:true, nameInDatasource:"Z_OperStatus", propertySpecifyingDatasource:smartDs);
def z_DisplayName= new ModelProperty(name:"z_DisplayName", type:ModelProperty.stringType,  lazy:true, nameInDatasource:"Z_DisplayName", propertySpecifyingDatasource:smartDs);
constructModel(link, [a_AdminStatus,a_OperStatus,a_DisplayName,z_AdminStatus,z_OperStatus,z_DisplayName], [], []);

def deviceComponent = new Model(name:"DeviceComponent", parentModel:smartsObject);
constructModel(deviceComponent, [], [], []);

def deviceAdapter = new Model(name:"DeviceAdapter", parentModel:deviceComponent);
description= new ModelProperty(name:"description", type:ModelProperty.stringType,  lazy:false, propertyDatasource:rcmdbModelDatasource);
def macAddress= new ModelProperty(name:"macAddress", type:ModelProperty.stringType,  lazy:false, propertyDatasource:rcmdbModelDatasource);
def type= new ModelProperty(name:"type", type:ModelProperty.stringType,  lazy:false, propertyDatasource:rcmdbModelDatasource);
def isManaged= new ModelProperty(name:"isManaged", type:ModelProperty.stringType,  lazy:false, propertyDatasource:rcmdbModelDatasource);
def maxSpeed= new ModelProperty(name:"maxSpeed", type:ModelProperty.stringType,  lazy:true, nameInDatasource:"MaxSpeed", propertySpecifyingDatasource:smartDs);
def adminStatus= new ModelProperty(name:"adminStatus", type:ModelProperty.stringType,  lazy:true, nameInDatasource:"AdminStatus", propertySpecifyingDatasource:smartDs);
def maxTransferUnit= new ModelProperty(name:"maxTransferUnit", type:ModelProperty.numberType,  lazy:true, defaultValue:0, nameInDatasource:"MaxTransferUnit", propertySpecifyingDatasource:smartDs);
def mode= new ModelProperty(name:"mode", type:ModelProperty.stringType,  lazy:true, nameInDatasource:"Mode", propertySpecifyingDatasource:smartDs);
def status= new ModelProperty(name:"status", type:ModelProperty.stringType,  lazy:true, nameInDatasource:"Status", propertySpecifyingDatasource:smartDs);
def duplexMode= new ModelProperty(name:"duplexMode", type:ModelProperty.stringType,  lazy:true, nameInDatasource:"DuplexMode", propertySpecifyingDatasource:smartDs);
def currentUtilization= new ModelProperty(name:"currentUtilization", type:ModelProperty.stringType,  lazy:true, nameInDatasource:"CurrentUtilization",propertySpecifyingDatasource:smartDs);
def operStatus= new ModelProperty(name:"operStatus", type:ModelProperty.stringType,  lazy:true, nameInDatasource:"OperStatus", propertySpecifyingDatasource:smartDs);
def isFlapping= new ModelProperty(name:"isFlapping", type:ModelProperty.stringType,  lazy:true, nameInDatasource:"IsFlapping", propertySpecifyingDatasource:smartDs);
def deviceId= new ModelProperty(name:"deviceID", type:ModelProperty.stringType,  lazy:true,  nameInDatasource:"DeviceID", propertySpecifyingDatasource:smartDs);
def peerSystemName= new ModelProperty(name:"peerSystemName", type:ModelProperty.stringType,  lazy:true, nameInDatasource:"PeerSystemName", propertySpecifyingDatasource:smartDs);
constructModel(deviceAdapter, [description,macAddress,type,isManaged,maxSpeed,adminStatus,maxTransferUnit,mode,status,duplexMode,currentUtilization,operStatus,isFlapping,deviceId,peerSystemName], [], []);

def ip = new Model(name:"Ip", parentModel:deviceComponent);
ipAddress= new ModelProperty(name:"ipAddress", type:ModelProperty.stringType,  lazy:false, propertyDatasource:rcmdbModelDatasource);
def netMask= new ModelProperty(name:"netMask", type:ModelProperty.stringType,  lazy:true, nameInDatasource:"NetMask", propertySpecifyingDatasource:smartDs);
def interfaceAdminStatus= new ModelProperty(name:"interfaceAdminStatus", type:ModelProperty.stringType,  lazy:true, nameInDatasource:"InterfaceAdminStatus", propertySpecifyingDatasource:smartDs);
def interfaceName= new ModelProperty(name:"interfaceName", type:ModelProperty.stringType,  lazy:true, nameInDatasource:"InterfaceName", propertySpecifyingDatasource:smartDs);
def interfaceOperStatus= new ModelProperty(name:"interfaceOperStatus", type:ModelProperty.stringType,  lazy:true, nameInDatasource:"InterfaceOperStatus", propertySpecifyingDatasource:smartDs);
def ipStatus= new ModelProperty(name:"ipStatus", type:ModelProperty.stringType,  lazy:true, nameInDatasource:"IPStatus", propertySpecifyingDatasource:smartDs);
constructModel(ip, [ipAddress,netMask,interfaceAdminStatus,interfaceName,interfaceOperStatus,ipStatus], [], []);

def deviceInterface = new Model(name:"DeviceInterface", parentModel:deviceAdapter);
def interfaceKey= new ModelProperty(name:"interfaceKey", type:ModelProperty.stringType,  lazy:true, nameInDatasource:"InterfaceKey", propertySpecifyingDatasource:smartDs);
constructModel(deviceInterface, [interfaceKey], [], []);

def port = new Model(name:"Port", parentModel:deviceAdapter);
def portType= new ModelProperty(name:"portType", type:ModelProperty.stringType,  lazy:false, propertyDatasource:rcmdbModelDatasource);
def portNumber= new ModelProperty(name:"portNumber", type:ModelProperty.stringType,  lazy:false, nameInDatasource:"PortNumber", propertySpecifyingDatasource:smartDs);
def portKey= new ModelProperty(name:"portKey", type:ModelProperty.stringType,  lazy:true, nameInDatasource:"PortKey", propertySpecifyingDatasource:smartDs);
constructModel(port, [portType,portNumber,portKey], [], []);

def card = new Model(name:"Card", parentModel:deviceComponent);
status= new ModelProperty(name:"status", type:ModelProperty.stringType,  lazy:true, nameInDatasource:"Status", propertySpecifyingDatasource:smartDs);
constructModel(card, [status], [], []);

//createRelation(smartsObject, redundancyGroup, "memberOf", "consistsOf", ModelRelation.MANY, ModelRelation.ONE);
createRelation(device, deviceComponent, "composedOf", "partOf", ModelRelation.ONE, ModelRelation.MANY);
createRelation(device, link, "connectedVia", "connectedSystems", ModelRelation.MANY, ModelRelation.MANY);
createRelation(link, deviceAdapter, "connectedTo", "connectedVia", ModelRelation.ONE, ModelRelation.MANY);
createRelation(deviceAdapter, card, "realizedBy", "realises", ModelRelation.MANY, ModelRelation.ONE);
createRelation(device, ip, "hostsAccessPoints", "hostedBy", ModelRelation.ONE, ModelRelation.MANY);
createRelation(deviceInterface, ip, "underlying", "layeredOver", ModelRelation.ONE, ModelRelation.ONE);

ModelGenerator.getInstance().generateModels(Model.list());


def checkDatasources(){
	def datasources =[:];

	def eastRegionDs= DatasourceName.findByName("eastRegionDs");
	if (eastRegionDs == null){
	    eastRegionDs = new DatasourceName(name:"eastRegionDs").save();
	}
	datasources.put("eastRegionDs",eastRegionDs);

	def westRegionDs= DatasourceName.findByName("westRegionDs");
	if (westRegionDs == null){
	    westRegionDs = new DatasourceName(name:"westRegionDs").save();
	}
	datasources.put("westRegionDs",westRegionDs);
	
	return datasources;
}

def constructModel(model, listOfProperties, listOfDatasources, listOfKeyMappings)
{
    model = model.save();
    listOfDatasources.each
    {
        it.model = model;
        it.save();
    }
    listOfProperties.each {
        it.model = model;
        it.save();
    }

    listOfKeyMappings.each
    {
        it.save();
    }
    listOfDatasources.each
    {
        it.refresh();
    }
    model.refresh();
    return model;
}

def createRelation(firstModel, secondModel, firstName, secondName, firstCar, secondCar){
    new ModelRelation(firstModel:firstModel, secondModel:secondModel, firstName:firstName, secondName:secondName, firstCardinality:firstCar, secondCardinality:secondCar).save();
    firstModel.refresh();
    secondModel.refresh();
}

