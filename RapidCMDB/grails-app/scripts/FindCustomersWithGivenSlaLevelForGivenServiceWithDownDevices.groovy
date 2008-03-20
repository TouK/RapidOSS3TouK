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

 Service name and sla level are provided as parameters
 */

def service = Service.findByName(params.service);
def resources = service.getResources();
def downDeviceInfo = [:];
def customerContactInfo = [];

def slaLevel = params.Slalevel;
def serviceDown = false;

for (resource in resources){
    if ((resource instanceof Device) && (resource.operationalState == "down")){
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


def collectDeviceInfo(device){
    def deviceInfo = [:];
    deviceInfo.put("Vendor",device.vendor);
    deviceInfo.put("Model",device.model);
    deviceInfo.put("Location",device.location);
    deviceInfo.put("IP",device.ip);
    return deviceInfo;
}

def  findAllCustomersUsingThisServiceWithDownDevice(service, slaLevel){
    def custAccountMgrs = [];
    def slas = Sla.findByServiceAndSlalevel(service, slaLevel);
    for (sla in slas){
        custAccountMgrs.add(sla.customer.accountmanager);
    }
    return custAccountMgrs;
}

def renderDownDeviceInfo(deviceInfo){

}
def renderCustomerInfo(custInfo){

}