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
 * Time: 2:59:03 PM
 * To change this template use File | Settings | File Templates.
 */
import model.*;
def dsCustomer;
def dsEvents;
def dsDevice;
def dsLink;
def dsResource1;
def dsResource2;

def datasources = checkDatasources();

Model.findByName("Customer")?.delete(flush:true);
Model.findByName("Service")?.delete(flush:true);
Model.findByName("Event")?.delete(flush:true);
Model.findByName("Sla")?.delete(flush:true);
Model.findByName("Device")?.delete(flush:true);
Model.findByName("Link")?.delete(flush:true);
Model.findByName("Resource")?.delete(flush:true);

def rcmdbDS = DatasourceName.findByName("RCMDB");
if(rcmdbDS == null){
    rcmdbDS = new DatasourceName(name: "RCMDB");
}

// Customer class
def customer = new Model(name:"Customer");
def rcmdbModelDatasource = new ModelDatasource(datasource:rcmdbDS, master:true);
def custModelDatasource = new ModelDatasource(datasource:datasources.customer, master:false);
def name = new ModelProperty(name:"name", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:rcmdbModelDatasource);
def accountmanager = new ModelProperty(name:"accountmanager", type:ModelProperty.stringType, blank:false, lazy:true, nameInDatasource:"manager", propertyDatasource:custModelDatasource);
def keyMappings = [new ModelDatasourceKeyMapping(property:name, datasource:rcmdbModelDatasource), new ModelDatasourceKeyMapping(property:name, datasource:custModelDatasource)];
customer = constructModel(customer, [name, accountmanager], [rcmdbModelDatasource, custModelDatasource], keyMappings);

// Service class
def service = new Model(name:"Service");
rcmdbModelDatasource = new ModelDatasource(datasource:rcmdbDS, master:true);
name = new ModelProperty(name:"name", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:rcmdbModelDatasource);
keyMappings = [new ModelDatasourceKeyMapping(property:name, datasource:rcmdbModelDatasource)];
def manager = new ModelProperty(name:"manager", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:rcmdbModelDatasource);
def status = new ModelProperty(name:"status", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:rcmdbModelDatasource);
service = constructModel(service, [name, manager, status], [rcmdbModelDatasource], keyMappings);

// Resource class
def resource = new Model(name:"Resource");
rcmdbModelDatasource = new ModelDatasource(datasource:rcmdbDS, master:true);
def resource1ModelDatasource = new ModelDatasource(datasource:datasources.resource1, master:false);
def resource2ModelDatasource = new ModelDatasource(datasource:datasources.resource2, master:false);
name = new ModelProperty(name:"name", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:rcmdbModelDatasource);
keyMappings = [new ModelDatasourceKeyMapping(property:name, datasource:rcmdbModelDatasource)];
keyMappings += new ModelDatasourceKeyMapping(property:name, datasource:resource1ModelDatasource)
keyMappings += new ModelDatasourceKeyMapping(property:name, nameInDatasource:"ID", datasource:resource2ModelDatasource);
def displayname = new ModelProperty(name:"displayname", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:rcmdbModelDatasource);
def dsname = new ModelProperty(name:"dsname", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:rcmdbModelDatasource);
def operationalstate = new ModelProperty(name:"operationalstate", type:ModelProperty.stringType, blank:true, lazy:true, propertySpecifyingDatasource:dsname);
def vendor = new ModelProperty(name:"vendor", type:ModelProperty.stringType, blank:true, lazy:true, propertySpecifyingDatasource:dsname);
def location = new ModelProperty(name:"location", type:ModelProperty.stringType, blank:true, lazy:true, propertySpecifyingDatasource:dsname);
def model = new ModelProperty(name:"model", type:ModelProperty.stringType, blank:true, lazy:true, propertySpecifyingDatasource:dsname);
resource = constructModel(resource, [name, displayname, dsname, operationalstate, vendor, location, model], [rcmdbModelDatasource, resource1ModelDatasource, resource2ModelDatasource], keyMappings);
resource.refresh();

//Device class
def device = new Model(name:"Device", parentModel:resource);
def deviceModelDatasource = new ModelDatasource(datasource:datasources.device, master:false);
def ipaddress = new ModelProperty(name:"ipaddress", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"ip", propertyDatasource:deviceModelDatasource);
keyMappings = [new ModelDatasourceKeyMapping(property:name, nameInDatasource:"ID", datasource:deviceModelDatasource)];
device = constructModel(device, [ipaddress], [deviceModelDatasource], keyMappings);

//Link class
def link = new Model(name:"Link", parentModel:resource);
def linkModelDatasource = new ModelDatasource(datasource:datasources.link, master:false);
def memberof = new ModelProperty(name:"memberOf", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"memberof", propertyDatasource:linkModelDatasource);
keyMappings = [new ModelDatasourceKeyMapping(property:name, nameInDatasource:"ID", datasource:linkModelDatasource)];
link = constructModel(link, [memberof], [linkModelDatasource], keyMappings);

// Event class
def event = new Model(name:"Event");
rcmdbModelDatasource = new ModelDatasource(datasource:rcmdbDS, master:true);
def eventModelDatasource = new ModelDatasource(datasource:datasources.event, master:false);
name = new ModelProperty(name:"name", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:rcmdbModelDatasource);
keyMappings = [new ModelDatasourceKeyMapping(property:name, datasource:rcmdbModelDatasource), new ModelDatasourceKeyMapping(property:name, nameInDatasource:"EventName", datasource:eventModelDatasource)];
def severity = new ModelProperty(name:"severity", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"Severity", propertyDatasource:eventModelDatasource);
def ack = new ModelProperty(name:"ack", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"Acknowledged", propertyDatasource:eventModelDatasource);
def owner = new ModelProperty(name:"owner", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"Owner", propertyDatasource:eventModelDatasource);
def description = new ModelProperty(name:"description", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"Description", propertyDatasource:eventModelDatasource);
def lastOccured = new ModelProperty(name:"lastOccured", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"LastOccuredAt", propertyDatasource:eventModelDatasource);
def lastChanged = new ModelProperty(name:"lastChanged", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"LastChangedAt", propertyDatasource:eventModelDatasource);
event = constructModel(event, [name, severity, ack, owner, description, lastOccured, lastChanged], [rcmdbModelDatasource, eventModelDatasource], keyMappings);

// Sla class
def sla = new Model(name:"Sla");
rcmdbModelDatasource = new ModelDatasource(datasource:rcmdbDS, master:true);
def slaId = new ModelProperty(name:"slaId", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:rcmdbModelDatasource);
def level = new ModelProperty(name:"level", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:rcmdbModelDatasource);
keyMappings = [new ModelDatasourceKeyMapping(property:slaId, datasource:rcmdbModelDatasource)];
sla = constructModel(sla, [slaId, level], [rcmdbModelDatasource], keyMappings);


createRelation(customer, sla, "slas", "customer", ModelRelation.ONE, ModelRelation.MANY);
createRelation(service, sla, "slas", "service", ModelRelation.ONE, ModelRelation.MANY);
createRelation(resource, event, "events", "resource", ModelRelation.ONE, ModelRelation.MANY);
// UNCOMMENT WHEN CMDB-283 IS FIXED createRelation(service, resource, "resources", "services", ModelRelation.MANY, ModelRelation.MANY);
createRelation(service, device, "devices", "services", ModelRelation.MANY, ModelRelation.MANY);
createRelation(service, link, "links", "services", ModelRelation.MANY, ModelRelation.MANY);

ModelGenerator.getInstance().generateModels(Model.list());

return "Successfully created model classes for Scenario 2! Now, GENERATE ANY ONE OF THE MODEL CLASS, RESTART RapidCMDBModeler APPLICATION, and run SampleScenario2Connector.groovy to populate MySql DB and RCMDB tables.";

def checkDatasources(){
	def datasources =[:];

	dsCustomer = DatasourceName.findByName("dsCustomer");
	if (dsCustomer == null){
	    dsCustomer = new DatasourceName(name:"dsCustomer").save();
	}
	datasources.put("customer",dsCustomer);

	dsEvent = DatasourceName.findByName("dsEvent");
	if (dsEvent == null){
	    dsEvent= new DatasourceName(name:"dsEvent").save();
	}
	datasources.put("event",dsEvent);

	dsDevice = DatasourceName.findByName("dsDevice");
	if (dsDevice == null){
	    dsDevice = new DatasourceName(name:"dsDevice").save();
	}
	datasources.put("device",dsDevice);

	dsLink = DatasourceName.findByName("dsLink");
	if (dsLink == null){
	    dsLink = new DatasourceName(name:"dsLink").save();
	}
	datasources.put("link",dsLink);

	dsResource1 = DatasourceName.findByName("dsResource1");
	if (dsResource1 == null){
	    dsResource1 = new DatasourceName(name:"dsResource1").save();
	}
	datasources.put("resource1",dsResource1);

	dsResource2 = DatasourceName.findByName("dsResource2");
	if (dsResource2 == null){
	    dsResource2 = new DatasourceName(name:"dsResource2").save();
	}
	datasources.put("resource2",dsResource2);
	return datasources;
}

def constructModel(model, listOfProperties, listOfDatasources, listOfKeyMappings)
{
    model = model.save();
    listOfDatasources.each{
        it.model = model;
        it.save();
    }
    listOfProperties.each{
        it.model = model;
        it.save();
    }

    listOfKeyMappings.each{
    	it.save();
    }

    listOfDatasources.each{
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


