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
 * User: Administrator
 * Date: Mar 20, 2008
 * Time: 1:38:50 PM
 * To change this template use File | Settings | File Templates.
 */

def ds1 = SingleTableDatabaseDatasource.findByName("DS1");
def ds2 = SingleTableDatabaseDatasource.findByName("DS2");
def serviceDs = SingleTableDatabaseDatasource.findByName("serviceDS");
def eventDs = SingleTableDatabaseDatasource.findByName("EVENTDS");
def cds = SingleTableDatabaseDatasource.findByName("CustomerDS");


def records = cds.getRecords();
for(record in records){
    Customer.add(name:record.NAME);
}
records = serviceDs.getRecords();
for(record in records){
    def name = record.NAME;
    def manager = record.MANAGER;
    def status = record.STATUS;
    Service.add(name:name, manager:manager, status:status);
}

Sla.add(level:"Gold", customer: Customer.get(["name":"c1"]), service:Service.get(["name":"service1"]));
Sla.add(level:"Standard", customer: Customer.get(["name":"c1"]), service:Service.get(["name":"service2"]));
Sla.add(level:"Standard", customer: Customer.get(["name":"c2"]), service:Service.get(["name":"service1"]));
Sla.add(level:"Platinum", customer: Customer.get(["name":"c2"]), service:Service.get(["name":"service3"]));

records = ds1.getRecords(["name", "classname", "displayname"]);
for(record in records){
    def className = record.CLASSNAME;
    def name = record.NAME;
    def displayname = record.DISPLAYNAME;
    if(className == "Device"){
          Device.add(name:name, displayname:displayname, dsname:"DS1");
    }
    else{
           Link.add(name:name, displayname:displayname, dsname:"DS1");
    }
}
records = ds2.getRecords(["id", "classname", "displayname"]);
for(record in records){
    def className = record.CLASSNAME;
    def name = record.ID;
    def displayname = record.DISPLAYNAME;
    if(className == "Device"){
          Device.add(name:name, displayname:displayname, dsname:"DS2");
    }
    else{
        Link.add(name:name, displayname:displayname, dsname:"DS2");
    }
}

def service1 = Service.get(["name":"service1"]);
service1.addToResources(Device.get(["name":"device1"]));
service1.addToResources(Device.get(["name":"device2"]));
service1.addToResources(Device.get(["name":"device3"]));
service1.addToResources(Link.get(["name":"link1"]));
service1.addToResources(Link.get(["name":"link2"]));
service1.addToResources(Link.get(["name":"link3"]));

def service2 = Service.get(["name":"service2"]);
service2.addToResources(Device.get(["name":"device3"]));
service2.addToResources(Device.get(["name":"device4"]));
service2.addToResources(Link.get(["name":"link3"]));
service2.addToResources(Link.get(["name":"link4"]));

def service3 = Service.get(["name":"service3"]);
service3.addToResources(Device.get(["name":"device4"]));
service3.addToResources(Device.get(["name":"device5"]));
service3.addToResources(Device.get(["name":"device6"]));
service3.addToResources(Link.get(["name":"link4"]));
service3.addToResources(Link.get(["name":"link5"]));
service3.addToResources(Link.get(["name":"link6"]));


records = eventDs.getRecords();

for(record in records){

    def eventName = record.EVENTNAME;
    def resource = record.RESOURCE;
    Resource.get(["name":resource]).addToEvents(new Event(name:eventName));
}
return "Successfully executed";