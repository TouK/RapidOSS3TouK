import datasource.SingleTableDatabaseAdapter
import datasource.SingleTableDatabaseDatasource

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
 * Time: 2:57:18 PM
 * To change this template use File | Settings | File Templates.
 */
// Test lazy load
def name = "device1";

Device device = Device.get(["name":name]);
def oldip = device.ipaddress;
def deviceId = device.id;

SingleTableDatabaseDatasource ds = SingleTableDatabaseDatasource.get(name:"dsDevice");
SingleTableDatabaseAdapter adapter = ds.adapter;
adapter.updateRecord(['ID':name, 'ip':'bogus ip']);
def record = adapter.getRecord(name);
if (record.ip == oldip){
    println "Could not properly update record in the database!"
}
def newip = device.ipaddress;
if (oldip == newip){
    println "Error: Lazy load doesn't work! (oldip: $oldip vs. newip: $newip)"
}
adapter.updateRecord(['ID':name, 'ip':oldip]);

// Test federated data can not be modified
device.ipaddress = '192.168.1.1';
newip = device.ipaddress ;

if (newip != oldip){
    return ("Error: Federated data is updated!")
}

 //  Test master data can be updated in RCMDB database
device.name = 'device1_1';
def newname = device.name;
if (name == newname){
     return("Error: Master data could not be updated!")
}

if(name == Device.get(deviceId).name){
     return("Error: Master data could not be updated!")
}
device.name = 'device1';
return "Successfully executed";
