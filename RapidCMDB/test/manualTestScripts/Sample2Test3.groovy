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
 * User: Pinar Kinikoglu
 * Date: Mar 20, 2008
 * Time: 12:55:44 PM
 * To change this template use File | Settings | File Templates.
 */

/*
 Find all 'down" devices for a given service and list their model, vendor, location and ip information
 Also find the customers with a specific service agreement for those services with down devices and
 get the account manager's name

 Service name (Service), service level (Servicelevel), and operational state (State) are provided as parameters
 ex: 
http://localhost:12222/RapidCMDB/script/run/Sample2Test3?Service=service1&Servicelevel=Gold&State=state1
 
 prints following lines in the RapidServer/RapidCMDB/logs/RapidServerOut.log

Device Info: ["device1":["Vendor":"vendor1", "Model":"model1", "Location":"location1", "IP":"ip1"]]
Cust Info: ["manager1"]

 */

def service = Service.get(name:params.Service);
def slaLevel = params.Servicelevel;
def operationalState = params.State;

def downDeviceInfo = [:];
def customerContactInfo = [];
def serviceDown = false;

/* UNCOMMENT WHEN CMDB-283 IS FIXED 

def resources = service.resources;

for (resource in resources){
    if ((resource instanceof Device) && (resource.operationalstate == operationalState)){
        def deviceInfo = [:];
        deviceInfo = collectDeviceInfo(resource);
        downDeviceInfo.put(resource.name, deviceInfo);
        serviceDown = true;
    }
}
*/
def resources = service.devices;

for (resource in resources){
    if (resource.operationalstate == operationalState){
        def deviceInfo = [:];
        deviceInfo = collectDeviceInfo(resource);
        downDeviceInfo.put(resource.name, deviceInfo);
        serviceDown = true;
    }
}

if (serviceDown){
    customerContactInfo = findAllCustomersUsingThisServiceWithDownDevice(service, slaLevel);
    renderDownDeviceInfo(downDeviceInfo);
    renderCustomerInfo(customerContactInfo);
}
return "Successfully executed. Please check RapidServer/RapidCMDB/logs/RapidServerOut.log file to verify the expected result.";

def collectDeviceInfo(device){
    def deviceInfo = [:];
    deviceInfo.put("Vendor",device.vendor);
    deviceInfo.put("Model",device.model);
    deviceInfo.put("Location",device.location);
    deviceInfo.put("IP",device.ipaddress);
    return deviceInfo;
}

def  findAllCustomersUsingThisServiceWithDownDevice(service, slaLevel){
    def custAccountMgrs = [];
    def slas = Sla.findByLevel(slaLevel);
    slas.each{
	    if (it.service.id == service.id){
			custAccountMgrs.add(it.customer.accountmanager);    
		}
    }
    
    return custAccountMgrs;
}

def renderDownDeviceInfo(deviceInfo){
    println "Device Info: " + deviceInfo;
}
def renderCustomerInfo(custInfo){
    println "Cust Info: " + custInfo;
}