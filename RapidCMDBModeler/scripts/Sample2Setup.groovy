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

println "======================== STARTING =========================="
def datasources = [];
def props = [];

def datasourceNames = checkDatasourceNames(); 

Model.get(name:"Customer")?.remove();
Model.get(name:"Service")?.remove();
Model.get(name:"Event")?.remove();
Model.get(name:"Sla")?.remove();
Model.get(name:"Device")?.remove();
Model.get(name:"Link")?.remove();
Model.get(name:"Resource")?.remove();

/* Define properties. Note that :
1. 'propertyDatasource' takes the Datasource name
2. 'propertySpecifyingDatasource' takes the dynamic datasource property name
3. all the datasources, except RCMDB, refered to are assumed to be created before running the script
*/

// Customer class
def customer = new ModelHelper("Customer");
def name = [name:"name", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:"RCMDB"];
def accountmanager = [name:"accountmanager", type:ModelProperty.stringType, blank:false, lazy:true, nameInDatasource:"manager", propertyDatasource:datasourceNames.customer];

props.add(name);
props.add(accountmanager);

// Identify the datasource names. 
def rcmdbDs = DatasourceName.get(name:"RCMDB");
def rcmdbKey = [name:"name"];
def custDs = DatasourceName.get(name:datasourceNames.customer);
def custdbKey = [name:"name"];
datasources.add([datasource:rcmdbDs, master:true, keys:[rcmdbKey]]);
datasources.add([datasource:custDs, master:false, keys:[custdbKey]]);

// The order of setting datasources, properties, and keymappings should be as follows:
customer.datasources = datasources;
customer.props = props; 
customer.setKeyMappings();  

// Service class
def service = new ModelHelper("Service");
name = [name:"name", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:"RCMDB"];
def manager = [name:"manager", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:"RCMDB"];
def status = [name:"status", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:"RCMDB"];

props = [];
props.add(name);
props.add(manager);
props.add(status);

datasources = [];
datasources.add([datasource:rcmdbDs, master:true, keys:[rcmdbKey]]);

service.datasources = datasources;
service.props = props; 
service.setKeyMappings();  

// Resource class
def resource = new ModelHelper("Resource");

name = [name:"name", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:"RCMDB"];
def displayname = [name:"displayname", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:"RCMDB"];
def dynamicDsname = [name:"dsname", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:"RCMDB"];
def operationalstate = [name:"operationalstate", type:ModelProperty.stringType, blank:true, lazy:true, propertySpecifyingDatasource:"dsname"];
def vendor = [name:"vendor", type:ModelProperty.stringType, blank:true, lazy:true, propertySpecifyingDatasource:"dsname"];
def location = [name:"location", type:ModelProperty.stringType, blank:true, lazy:true, propertySpecifyingDatasource:"dsname"];
def model = [name:"model", type:ModelProperty.stringType, blank:true, lazy:true, propertySpecifyingDatasource:"dsname"];

props = [];
props.add(name);
props.add(displayname);
props.add(dynamicDsname);
props.add(operationalstate);
props.add(vendor);
props.add(location);
props.add(model);

datasources = [];
def resourceDs1 = DatasourceName.get(name:datasourceNames.resource1);
def resourcedbKey1 = [name:"name"];
def resourceDs2 = DatasourceName.get(name:datasourceNames.resource2);
def resourcedbKey2 = [name:"name", nameInDs:"ID"];

datasources.add([datasource:rcmdbDs, master:true, keys:[rcmdbKey]]);
datasources.add([datasource:resourceDs1, master:false, keys:[resourcedbKey1]]);
datasources.add([datasource:resourceDs2, master:false, keys:[resourcedbKey2]]);

resource.datasources = datasources;
resource.props = props; 
resource.setKeyMappings();  

//Device class
def device = new ModelHelper("Device", "Resource");
def ipaddress = [name:"ipaddress", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"ip", propertyDatasource:datasourceNames.device];

props = [];
props.add(ipaddress);

datasources = [];
def deviceDs = DatasourceName.get(name:datasourceNames.device);
def devicedbKey = [name:"name", nameInDs:"ID"];
datasources.add([datasource:deviceDs, master:false, keys:[devicedbKey]]);

device.datasources = datasources;
device.props = props; 
device.setKeyMappings();  

//Link class
def link = new ModelHelper("Link", "Resource");
def memberof = [name:"memberOf", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"memberof", propertyDatasource:datasourceNames.link];

props = [];
props.add(memberof);

datasources = [];
def linkDs = DatasourceName.get(name:datasourceNames.link);
def linkdbKey = [name:"name", nameInDs:"ID"];
datasources.add([datasource:linkDs, master:false, keys:[linkdbKey]]);

link.datasources = datasources;
link.props = props; 
link.setKeyMappings();  

// Event class
def event = new ModelHelper("Event");
name = [name:"name", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:"RCMDB"];
def severity = [name:"severity", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"Severity", propertyDatasource:datasourceNames.event];
def ack = [name:"ack", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"Acknowledged", propertyDatasource:datasourceNames.event];
def owner = [name:"owner", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"Owner", propertyDatasource:datasourceNames.event];
def description = [name:"description", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"Description", propertyDatasource:datasourceNames.event];
def lastOccured = [name:"lastOccured", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"LastOccuredAt", propertyDatasource:datasourceNames.event];
def lastChanged = [name:"lastChanged", type:ModelProperty.stringType, blank:true, lazy:true, nameInDatasource:"LastChangedAt", propertyDatasource:datasourceNames.event];

props = [];
props.add(name);
props.add(severity);
props.add(ack);
props.add(owner);
props.add(description);
props.add(lastOccured);
props.add(lastChanged);

datasources = [];
datasources.add([datasource:rcmdbDs, master:true, keys:[rcmdbKey]]);
def eventDs = DatasourceName.get(name:datasourceNames.event);
def eventdbKey = [name:"name"];

datasources.add([datasource:rcmdbDs, master:true, keys:[rcmdbKey]]);
datasources.add([datasource:eventDs, master:false, keys:[eventdbKey]]);

event.datasources = datasources;
event.props = props; 
event.setKeyMappings();  

// Sla class
def sla = new ModelHelper("Sla");
def slaId = [name:"slaId", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:"RCMDB"];
def level = [name:"level", type:ModelProperty.stringType, blank:false, lazy:false, propertyDatasource:"RCMDB"];

props = [];
props.add(slaId);
props.add(level);

datasources = [];
rcmdbKey = [name:"slaId"];
datasources.add([datasource:rcmdbDs, master:true, keys:[rcmdbKey]]);

sla.datasources = datasources;
sla.props = props; 
sla.setKeyMappings();  

customer.createRelation(sla.model, "slas", "customer", ModelRelation.ONE, ModelRelation.MANY);
service.createRelation(sla.model, "slas", "service", ModelRelation.ONE, ModelRelation.MANY);
resource.createRelation(event.model, "events", "resource", ModelRelation.ONE, ModelRelation.MANY);
// UNCOMMENT WHEN CMDB-283 IS FIXED createRelation(service, resource, "resources", "services", ModelRelation.MANY, ModelRelation.MANY);
service.createRelation(device.model, "devices", "services", ModelRelation.MANY, ModelRelation.MANY);
service.createRelation(link.model, "links", "services", ModelRelation.MANY, ModelRelation.MANY);

return "Successfully created model classes for Scenario 2! Now, GENERATE, RELOAD RapidCMDB APPLICATION, and run SampleScenario2Connector.groovy to populate MySql DB and RCMDB tables.";

def checkDatasourceNames(){
	def dsCustomer;
	def dsEvents;
	def dsDevice;
	def dsLink;
	def dsResource1;
	def dsResource2;

	def datasources =[:];

	dsCustomer = DatasourceName.get(name:"dsCustomer");
	if (dsCustomer == null){
	    dsCustomer = DatasourceName.add(name:"dsCustomer");
	}
	datasources.put("customer",dsCustomer.name);

	dsEvent = DatasourceName.get(name:"dsEvent");
	if (dsEvent == null){
	    dsEvent= DatasourceName.add(name:"dsEvent");
	}
	datasources.put("event",dsEvent.name);

	dsDevice = DatasourceName.get(name:"dsDevice");
	if (dsDevice == null){
	    dsDevice = DatasourceName.add(name:"dsDevice");
	}
	datasources.put("device",dsDevice.name);

	dsLink = DatasourceName.get(name:"dsLink");
	if (dsLink == null){
	    dsLink = DatasourceName.add(name:"dsLink");
	}
	datasources.put("link",dsLink.name);

	dsResource1 = DatasourceName.get(name:"dsResource1");
	if (dsResource1 == null){
	    dsResource1 = DatasourceName.add(name:"dsResource1");
	}
	datasources.put("resource1",dsResource1.name);

	dsResource2 = DatasourceName.get(name:"dsResource2");
	if (dsResource2 == null){
	    dsResource2 = DatasourceName.add(name:"dsResource2");
	}
	datasources.put("resource2",dsResource2.name);
	return datasources;
}


