import model.*
import connection.*
import datasource.*
import com.ifountain.domain.ModelGenerator

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

def rcmdbDS = BaseDatasource.findByName("RCMDB");

// Customer class
def customer = Model.create(name:"Customer");
def rcmdbModelDatasource = ModelDatasource.create(datasource:rcmdbDS, master:true);
def custModelDatasource = ModelDatasource.create(datasource:datasources.customer, master:false);
def name = ModelProperty.create(name:"name", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:rcmdbModelDatasource);
def accountmanager = ModelProperty.create(name:"accountmanager", type:ModelProperty.stringType, blank:false, lazy:true, nameInDatasource:"manager", propertyDatasource:custModelDatasource);
def keyMappings = [ModelDatasourceKeyMapping.create(property:name, datasource:rcmdbModelDatasource), ModelDatasourceKeyMapping.create(property:name, datasource:custModelDatasource)];
customer = constructModel(customer, [name, accountmanager], [rcmdbModelDatasource, custModelDatasource], keyMappings);

// Service class
def service = Model.create(name:"Service");
rcmdbModelDatasource = ModelDatasource.create(datasource:rcmdbDS, master:true);
name = ModelProperty.create(name:"name", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:rcmdbModelDatasource);
keyMappings = [ModelDatasourceKeyMapping.create(property:name, datasource:rcmdbModelDatasource)];
def manager = ModelProperty.create(name:"manager", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:rcmdbModelDatasource);
def status = ModelProperty.create(name:"status", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:rcmdbModelDatasource);
service = constructModel(service, [name, manager, status], [rcmdbModelDatasource], keyMappings);

// Resource class
def resource = Model.create(name:"Resource");
rcmdbModelDatasource = ModelDatasource.create(datasource:rcmdbDS, master:true);
def resource1ModelDatasource = ModelDatasource.create(datasource:datasources.resource1, master:false);
def resource2ModelDatasource = ModelDatasource.create(datasource:datasources.resource2, master:false);
name = ModelProperty.create(name:"name", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:rcmdbModelDatasource);
keyMappings = [ModelDatasourceKeyMapping.create(property:name, datasource:rcmdbModelDatasource)];
keyMappings += ModelDatasourceKeyMapping.create(property:name, datasource:resource1ModelDatasource)
keyMappings += ModelDatasourceKeyMapping.create(property:name, nameInDatasource:"ID", datasource:resource2ModelDatasource);
def displayname = ModelProperty.create(name:"displayname", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:rcmdbModelDatasource);
def dsname = ModelProperty.create(name:"dsname", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:rcmdbModelDatasource);
def operationalstate = ModelProperty.create(name:"operationalstate", type:ModelProperty.stringType, blank:true, lazy:true, propertySpecifyingDatasource:dsname);
def vendor = ModelProperty.create(name:"vendor", type:ModelProperty.stringType, blank:true, lazy:true, propertySpecifyingDatasource:dsname);
def location = ModelProperty.create(name:"location", type:ModelProperty.stringType, blank:true, lazy:true, propertySpecifyingDatasource:dsname);
def model = ModelProperty.create(name:"model", type:ModelProperty.stringType, blank:true, lazy:true, propertySpecifyingDatasource:dsname);
resource = constructModel(resource, [name, displayname, dsname, operationalstate, vendor, location, model], [rcmdbModelDatasource, resource1ModelDatasource, resource2ModelDatasource], keyMappings);
resource.refresh();

//Device class
def device = Model.create(name:"Device", parentModel:resource);
def deviceModelDatasource = ModelDatasource.create(datasource:datasources.device, master:false);
def ipaddress = ModelProperty.create(name:"ipaddress", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"ip", propertyDatasource:deviceModelDatasource);
keyMappings = [ModelDatasourceKeyMapping.create(property:name, nameInDatasource:"ID", datasource:deviceModelDatasource)];
device = constructModel(device, [ipaddress], [deviceModelDatasource], keyMappings);

//Link class
def link = Model.create(name:"Link", parentModel:resource);
def linkModelDatasource = ModelDatasource.create(datasource:datasources.link, master:false);
def memberof = ModelProperty.create(name:"memberOf", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"memberof", propertyDatasource:linkModelDatasource);
keyMappings = [ModelDatasourceKeyMapping.create(property:name, nameInDatasource:"ID", datasource:linkModelDatasource)];
link = constructModel(link, [memberof], [linkModelDatasource], keyMappings);

// Event class
def event = Model.create(name:"Event");
rcmdbModelDatasource = ModelDatasource.create(datasource:rcmdbDS, master:true);
def eventModelDatasource = ModelDatasource.create(datasource:datasources.event, master:false);
name = ModelProperty.create(name:"name", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:rcmdbModelDatasource);
keyMappings = [ModelDatasourceKeyMapping.create(property:name, datasource:rcmdbModelDatasource), ModelDatasourceKeyMapping.create(property:name, nameInDatasource:"EventName", datasource:eventModelDatasource)];
def severity = ModelProperty.create(name:"severity", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"Severity", propertyDatasource:eventModelDatasource);
def ack = ModelProperty.create(name:"ack", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"Acknowledged", propertyDatasource:eventModelDatasource);
def owner = ModelProperty.create(name:"owner", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"Owner", propertyDatasource:eventModelDatasource);
def description = ModelProperty.create(name:"description", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"Description", propertyDatasource:eventModelDatasource);
def lastOccured = ModelProperty.create(name:"lastOccured", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"LastOccuredAt", propertyDatasource:eventModelDatasource);
def lastChanged = ModelProperty.create(name:"lastChanged", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"LastChangedAt", propertyDatasource:eventModelDatasource);
event = constructModel(event, [name, severity, ack, owner, description, lastOccured, lastChanged], [rcmdbModelDatasource, eventModelDatasource], keyMappings);

// Sla class
def sla = Model.create(name:"Sla");
rcmdbModelDatasource = ModelDatasource.create(datasource:rcmdbDS, master:true);
def slaId = ModelProperty.create(name:"slaId", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:rcmdbModelDatasource);
def level = ModelProperty.create(name:"level", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:rcmdbModelDatasource);
keyMappings = [ModelDatasourceKeyMapping.create(property:slaId, datasource:rcmdbModelDatasource)];
sla = constructModel(sla, [slaId, level], [rcmdbModelDatasource], keyMappings);


createRelation(customer, sla, "slas", "customer", ModelRelation.ONE, ModelRelation.MANY);
createRelation(service, sla, "slas", "service", ModelRelation.ONE, ModelRelation.MANY);
createRelation(resource, event, "events", "resource", ModelRelation.ONE, ModelRelation.MANY);
createRelation(service, resource, "resources", "services", ModelRelation.MANY, ModelRelation.MANY);

ModelGenerator.getInstance().generateModel(device);
ModelGenerator.getInstance().generateModel(link);

return "Successfully created model classes for Scenario 2! Now, run SampleScenario2Connector.groovy to populate MySql DB and RCMDB tables.";


def checkDatasources(){
	def datasources =[:];
	def conn1 = DatabaseConnection.findByName("mysql");
	if(conn1 == null){
	    conn1 = new DatabaseConnection(name:"mysql", driver:"com.mysql.jdbc.Driver",
	            url:"jdbc:mysql://192.168.1.100/test", username:"root", password:"root").save();
	}

	dsCustomer = SingleTableDatabaseDatasource.findByName("dsCustomer");
	if (dsCustomer == null){
	    dsCustomer = SingleTableDatabaseDatasource.add(connection:conn1, name:"dsCustomer", tableName:"customers", tableKeys:"name");
	}
	datasources.put("customer",dsCustomer);

	dsEvent = SingleTableDatabaseDatasource.findByName("dsEvent");
	if (dsEvent == null){
	    dsEvent= SingleTableDatabaseDatasource.add(connection:conn1, name:"dsEvent", tableName:"events", tableKeys:"EventName");
	}
	datasources.put("event",dsEvent);

	dsDevice = SingleTableDatabaseDatasource.findByName("dsDevice");
	if (dsDevice == null){
	    dsDevice = SingleTableDatabaseDatasource.add(connection:conn1, name:"dsDevice", tableName:"deviceds", tableKeys:"ID");
	}
	datasources.put("device",dsDevice);

	dsLink = SingleTableDatabaseDatasource.findByName("dsLink");
	if (dsLink == null){
	    dsLink = SingleTableDatabaseDatasource.add(connection:conn1, name:"dsLink", tableName:"linkds", tableKeys:"ID");
	}
	datasources.put("link",dsLink);

	dsResource1 = SingleTableDatabaseDatasource.findByName("dsResource1");
	if (dsResource1 == null){
	    dsResource1 = SingleTableDatabaseDatasource.add(connection:conn1, name:"dsResource1", tableName:"resources1", tableKeys:"name");
	}
	datasources.put("resource1",dsResource1);

	dsResource2 = SingleTableDatabaseDatasource.findByName("dsResource2");
	if (dsResource2 == null){
	    dsResource2 = SingleTableDatabaseDatasource.add(connection:conn1, name:"dsResource2", tableName:"resources2", tableKeys:"ID");
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
    ModelRelation.add(firstModel:firstModel, secondModel:secondModel, firstName:firstName, secondName:secondName, firstCardinality:firstCar, secondCardinality:secondCar);
    firstModel.refresh();
    secondModel.refresh();
}


