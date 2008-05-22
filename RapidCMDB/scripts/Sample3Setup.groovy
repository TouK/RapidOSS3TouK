import connection.*
import datasource.*
import model.*
import com.ifountain.rcmdb.domain.generation.ModelGenerator

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

def rcmdbDS = BaseDatasource.findByName("RCMDB");
def rcmdbModelDatasource = ModelDatasource.create(datasource:rcmdbDS, master:true);
def eastRegionModelDatasource = ModelDatasource.create(datasource:datasources.eastRegionDs, master:false);
def westRegionModelDatasource = ModelDatasource.create(datasource:datasources.westRegionDs, master:false);

def smartsObject = Model.create(name:"SmartsObject");
def name = ModelProperty.create(name:"name", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:rcmdbModelDatasource);
def creationClassName = ModelProperty.create(name:"creationClassName", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:rcmdbModelDatasource);
def keyMappings = [ModelDatasourceKeyMapping.create(property:name, nameInDatasource:"Name", datasource:eastRegionModelDatasource),
				   ModelDatasourceKeyMapping.create(property:creationClassName, nameInDatasource:"CreationClassName", datasource:eastRegionModelDatasource),
				   ModelDatasourceKeyMapping.create(property:name, nameInDatasource:"Name", datasource:westRegionModelDatasource),
				   ModelDatasourceKeyMapping.create(property:creationClassName, nameInDatasource:"CreationClassName", datasource:westRegionModelDatasource),
                   ModelDatasourceKeyMapping.create(property:name, datasource:rcmdbModelDatasource),
                   ModelDatasourceKeyMapping.create(property:creationClassName, datasource:rcmdbModelDatasource)];
def smartDs= ModelProperty.create(name:"smartDs", type:ModelProperty.stringType, blank:true, lazy:false, propertyDatasource:rcmdbModelDatasource);
def displayName= ModelProperty.create(name:"displayName", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"DisplayName", propertySpecifyingDatasource:smartDs);
smartsObject = constructModel(smartsObject, [creationClassName, name, smartDs, displayName], [rcmdbModelDatasource, eastRegionModelDatasource, westRegionModelDatasource], keyMappings);

/*def redundancyGroup = Model.create(name:"RedundancyGroup");
modelDatasource = ModelDatasource.create(datasource:rcmdbDatasource, master:true);
creationClassName = ModelProperty.create(name:"creationClassName", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:modelDatasource);
name = ModelProperty.create(name:"name", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:modelDatasource);
keyMappings = [ModelDatasourceKeyMapping.create(property:creationClassName, datasource:modelDatasource),
                    ModelDatasourceKeyMapping.create(property:name, datasource:modelDatasource)]
redundancyGroup = constructModel(redundancyGroup, [creationClassName, name], [modelDatasource], keyMappings);
*/
def device = Model.create(name:"Device", parentModel:smartsObject);
def location= ModelProperty.create(name:"location", type:ModelProperty.stringType, blank:true, lazy:false, propertyDatasource:rcmdbModelDatasource);
def model= ModelProperty.create(name:"model", type:ModelProperty.stringType, blank:true, lazy:false, propertyDatasource:rcmdbModelDatasource);
def ipAddress= ModelProperty.create(name:"ipAddress", type:ModelProperty.stringType, blank:true, lazy:false, propertyDatasource:rcmdbModelDatasource);
def snmpReadCommunity= ModelProperty.create(name:"snmpReadCommunity", type:ModelProperty.stringType, blank:true, lazy:false, propertyDatasource:rcmdbModelDatasource);
def vendor= ModelProperty.create(name:"vendor", type:ModelProperty.stringType, blank:true, lazy:false, propertyDatasource:rcmdbModelDatasource);
def discoveredLastAt= ModelProperty.create(name:"discoveredLastAt", type:ModelProperty.numberType, blank:false, lazy:true, defaultValue:0, nameInDatasource:"DiscoveredLastAt", propertySpecifyingDatasource:smartDs);
def description= ModelProperty.create(name:"description", type:ModelProperty.stringType, blank:false, lazy:true, nameInDatasource:"Description", propertySpecifyingDatasource:smartDs);
def discoveryErrorInfo= ModelProperty.create(name:"discoveryErrorInfo", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"DiscoveryErrorInfo", propertySpecifyingDatasource:smartDs);
def discoveryTime= ModelProperty.create(name:"discoveryTime", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"DiscoveryTime", propertySpecifyingDatasource:smartDs);
constructModel(device, [location,model,ipAddress,snmpReadCommunity,vendor,discoveredLastAt,description,discoveryErrorInfo,discoveryTime], [], []);

def link = Model.create(name:"Link", parentModel:smartsObject);
def a_AdminStatus= ModelProperty.create(name:"a_AdminStatus", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"A_AdminStatus", propertySpecifyingDatasource:smartDs);
def a_OperStatus= ModelProperty.create(name:"a_OperStatus", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"A_OperStatus", propertySpecifyingDatasource:smartDs);
def a_DisplayName= ModelProperty.create(name:"a_DisplayName", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"A_DisplayName", propertySpecifyingDatasource:smartDs);
def z_AdminStatus= ModelProperty.create(name:"z_AdminStatus", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"Z_AdminStatus", propertySpecifyingDatasource:smartDs);
def z_OperStatus= ModelProperty.create(name:"z_OperStatus", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"Z_OperStatus", propertySpecifyingDatasource:smartDs);
def z_DisplayName= ModelProperty.create(name:"z_DisplayName", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"Z_DisplayName", propertySpecifyingDatasource:smartDs);
constructModel(link, [a_AdminStatus,a_OperStatus,a_DisplayName,z_AdminStatus,z_OperStatus,z_DisplayName], [], []);

def deviceComponent = Model.create(name:"DeviceComponent", parentModel:smartsObject);
constructModel(deviceComponent, [], [], []);

def deviceAdapter = Model.create(name:"DeviceAdapter", parentModel:deviceComponent);
description= ModelProperty.create(name:"description", type:ModelProperty.stringType, blank:true, lazy:false, propertyDatasource:rcmdbModelDatasource);
def macAddress= ModelProperty.create(name:"macAddress", type:ModelProperty.stringType, blank:true, lazy:false, propertyDatasource:rcmdbModelDatasource);
def type= ModelProperty.create(name:"type", type:ModelProperty.stringType, blank:true, lazy:false, propertyDatasource:rcmdbModelDatasource);
def isManaged= ModelProperty.create(name:"isManaged", type:ModelProperty.stringType, blank:true, lazy:false, propertyDatasource:rcmdbModelDatasource);
def maxSpeed= ModelProperty.create(name:"maxSpeed", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"MaxSpeed", propertySpecifyingDatasource:smartDs);
def adminStatus= ModelProperty.create(name:"adminStatus", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"AdminStatus", propertySpecifyingDatasource:smartDs);
def maxTransferUnit= ModelProperty.create(name:"maxTransferUnit", type:ModelProperty.numberType, blank:false, lazy:true, defaultValue:0, nameInDatasource:"MaxTransferUnit", propertySpecifyingDatasource:smartDs);
def mode= ModelProperty.create(name:"mode", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"Mode", propertySpecifyingDatasource:smartDs);
def status= ModelProperty.create(name:"status", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"Status", propertySpecifyingDatasource:smartDs);
def duplexMode= ModelProperty.create(name:"duplexMode", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"DuplexMode", propertySpecifyingDatasource:smartDs);
def currentUtilization= ModelProperty.create(name:"currentUtilization", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"CurrentUtilization",propertySpecifyingDatasource:smartDs);
def operStatus= ModelProperty.create(name:"operStatus", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"OperStatus", propertySpecifyingDatasource:smartDs);
def isFlapping= ModelProperty.create(name:"isFlapping", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"IsFlapping", propertySpecifyingDatasource:smartDs);
def deviceId= ModelProperty.create(name:"deviceID", type:ModelProperty.stringType, blank:true, lazy:true,  nameInDatasource:"DeviceID", propertySpecifyingDatasource:smartDs);
def peerSystemName= ModelProperty.create(name:"peerSystemName", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"PeerSystemName", propertySpecifyingDatasource:smartDs);
constructModel(deviceAdapter, [description,macAddress,type,isManaged,maxSpeed,adminStatus,maxTransferUnit,mode,status,duplexMode,currentUtilization,operStatus,isFlapping,deviceId,peerSystemName], [], []);

def ip = Model.create(name:"Ip", parentModel:deviceComponent);
ipAddress= ModelProperty.create(name:"ipAddress", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:rcmdbModelDatasource);
def netMask= ModelProperty.create(name:"netMask", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"NetMask", propertySpecifyingDatasource:smartDs);
def interfaceAdminStatus= ModelProperty.create(name:"interfaceAdminStatus", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"InterfaceAdminStatus", propertySpecifyingDatasource:smartDs);
def interfaceName= ModelProperty.create(name:"interfaceName", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"InterfaceName", propertySpecifyingDatasource:smartDs);
def interfaceOperStatus= ModelProperty.create(name:"interfaceOperStatus", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"InterfaceOperStatus", propertySpecifyingDatasource:smartDs);
def ipStatus= ModelProperty.create(name:"ipStatus", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"IPStatus", propertySpecifyingDatasource:smartDs);
constructModel(ip, [ipAddress,netMask,interfaceAdminStatus,interfaceName,interfaceOperStatus,ipStatus], [], []);

def deviceInterface = Model.create(name:"DeviceInterface", parentModel:deviceAdapter);
def interfaceKey= ModelProperty.create(name:"interfaceKey", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"InterfaceKey", propertySpecifyingDatasource:smartDs);
constructModel(deviceInterface, [interfaceKey], [], []);

def port = Model.create(name:"Port", parentModel:deviceAdapter);
def portType= ModelProperty.create(name:"portType", type:ModelProperty.stringType, blank:true, lazy:false, propertyDatasource:rcmdbModelDatasource);
def portNumber= ModelProperty.create(name:"portNumber", type:ModelProperty.stringType, blank:true, lazy:false, nameInDatasource:"PortNumber", propertySpecifyingDatasource:smartDs);
def portKey= ModelProperty.create(name:"portKey", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"PortKey", propertySpecifyingDatasource:smartDs);
constructModel(port, [portType,portNumber,portKey], [], []);

def card = Model.create(name:"Card", parentModel:deviceComponent);
status= ModelProperty.create(name:"status", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"Status", propertySpecifyingDatasource:smartDs);
constructModel(card, [status], [], []);

//createRelation(smartsObject, redundancyGroup, "memberOf", "consistsOf", ModelRelation.MANY, ModelRelation.ONE);
createRelation(device, deviceComponent, "composedOf", "partOf", ModelRelation.ONE, ModelRelation.MANY);
createRelation(device, link, "connectedVia", "connectedSystems", ModelRelation.MANY, ModelRelation.MANY);
createRelation(link, deviceAdapter, "connectedTo", "connectedVia", ModelRelation.ONE, ModelRelation.MANY);
createRelation(deviceAdapter, card, "realizedBy", "realises", ModelRelation.MANY, ModelRelation.ONE);
createRelation(device, ip, "hostsAccessPoints", "hostedBy", ModelRelation.ONE, ModelRelation.MANY);
createRelation(deviceInterface, ip, "underlying", "layeredOver", ModelRelation.ONE, ModelRelation.ONE);

ModelGenerator.getInstance().generateModel(deviceInterface);
ModelGenerator.getInstance().generateModel(port);


def checkDatasources(){
	def datasources =[:];
	def conn1 = SmartsConnection.findByName("smartsconn");
	if(conn1 == null){
	    conn1 = new SmartsConnection(name:"smartsconn", broker:"192.168.1.102:426", domain:"INCHARGE-SA", username:"admin", password:"rcpass").save();
	}

	def eastRegionDs= SmartsTopologyDatasource.findByName("eastRegionDs");
	if (eastRegionDs == null){
	    eastRegionDs = new SmartsTopologyDatasource(connection:conn1, name:"eastRegionDs").save();
	}
	datasources.put("eastRegionDs",eastRegionDs);

	def westRegionDs= SmartsTopologyDatasource.findByName("westRegionDs");
	if (westRegionDs == null){
	    westRegionDs = new SmartsTopologyDatasource(connection:conn1, name:"westRegionDs").save();
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
    ModelRelation.add(firstModel:firstModel, secondModel:secondModel, firstName:firstName, secondName:secondName, firstCardinality:firstCar, secondCardinality:secondCar);
    firstModel.refresh();
    secondModel.refresh();
}

