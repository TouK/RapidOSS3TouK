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
 * Time: 11:43:45 AM
 * To change this template use File | Settings | File Templates.
 */
import datasources.DatabaseAdapter;
import org.apache.log4j.Logger
import datasource.SingleTableDatabaseDatasource
import connection.DatabaseConnection;

def dsConn = DatabaseConnection.findByName("mysql");
if(dsConn == null){
    dsConn = new DatabaseConnection(name:"mysql", driver:"com.mysql.jdbc.Driver",
            url:"jdbc:mysql://localhost/students", username:"root", password:"root").save();
}

def dbAdapter = new DatabaseAdapter("mysql", 0, Logger.getRootLogger());

try{
    dbAdapter.executeUpdate("drop table resources1");
}
catch(e){}
try{
    dbAdapter.executeUpdate("drop table resources2");
}
catch(e){}
try{
    dbAdapter.executeUpdate("drop table deviceds");
}
catch(e){}
try{
    dbAdapter.executeUpdate("drop table linkds");
}
catch(e){}

try{
    dbAdapter.executeUpdate("drop table services");
}
catch(e){}
try{
    dbAdapter.executeUpdate("drop table events");
}
catch(e){}
try{
    dbAdapter.executeUpdate("drop table customers");
}
catch(e){}


dbAdapter.executeUpdate("create table resources1 (name varchar(50), displayname varchar(50), classname varchar(50), operationalstate varchar(50), model varchar(50), location varchar(50), vendor varchar(50), primary key (name));");
dbAdapter.executeUpdate("create table resources2 (ID varchar(50), displayname varchar(50), classname varchar(50), operationalstate varchar(50), model varchar(50), location varchar(50), vendor varchar(50), primary key (ID));");
dbAdapter.executeUpdate("create table deviceds (ID varchar(50), ipaddress varchar(50), primary key (ID));");
dbAdapter.executeUpdate("create table linkds (ID varchar(50), memberof varchar(50), primary key (ID));");
dbAdapter.executeUpdate("create table services (name varchar(50), manager varchar(50), status varchar(50), primary key (name));");
dbAdapter.executeUpdate("create table events (EventName varchar(50), Resource varchar(50), Severity integer, Acknowledged varchar(10), Owner varchar(50), Description varchar(50), LastChangedAt datetime, LastOccuredAt datetime,  primary key (EventName));");
dbAdapter.executeUpdate("create table customers (name varchar(50), manager varchar(50), primary key (name));");

dbAdapter.executeUpdate("insert into customers values ('c1', 'manager1')");
dbAdapter.executeUpdate("insert into customers values ('c2', 'manager2')");

dbAdapter.executeUpdate("insert into resources1 values ('device1', 'device1', 'Device', 'state1', 'model1', 'location1', 'vendor1')");
dbAdapter.executeUpdate("insert into resources1 values ('device2', 'device2', 'Device', 'state2', 'model2', 'location2', 'vendor2')");
dbAdapter.executeUpdate("insert into resources1 values ('device3', 'device3', 'Device', 'state3', 'model3', 'location3', 'vendor3')");
dbAdapter.executeUpdate("insert into resources2 values ('device4', 'device4', 'Device', 'state4', 'model4', 'location4', 'vendor4')");
dbAdapter.executeUpdate("insert into resources2 values ('device5', 'device5', 'Device', 'state5', 'model5', 'location5', 'vendor5')");
dbAdapter.executeUpdate("insert into resources2 values ('device6', 'device6', 'Device', 'state6', 'model6', 'location6', 'vendor6')");

dbAdapter.executeUpdate("insert into deviceds values ('device1', 'ip1')");
dbAdapter.executeUpdate("insert into deviceds values ('device2', 'ip2')");
dbAdapter.executeUpdate("insert into deviceds values ('device3', 'ip3')");
dbAdapter.executeUpdate("insert into deviceds values ('device4', 'ip4')");
dbAdapter.executeUpdate("insert into deviceds values ('device5', 'ip5')");
dbAdapter.executeUpdate("insert into deviceds values ('device6', 'ip6')");

dbAdapter.executeUpdate("insert into resources1 values ('link1', 'link1', 'Link', 'state1', 'model1', 'location1', 'vendor1')");
dbAdapter.executeUpdate("insert into resources1 values ('link2', 'link2', 'Link', 'state2', 'model2', 'location2', 'vendor2')");
dbAdapter.executeUpdate("insert into resources1 values ('link3', 'link3', 'Link', 'state3', 'model3', 'location3', 'vendor3')");
dbAdapter.executeUpdate("insert into resources2 values ('link4', 'link4', 'Link', 'state4', 'model4', 'location4', 'vendor4')");
dbAdapter.executeUpdate("insert into resources2 values ('link5', 'link5', 'Link', 'state5', 'model5', 'location5', 'vendor5')");
dbAdapter.executeUpdate("insert into resources2 values ('link6', 'link6', 'Link', 'state6', 'model6', 'location6', 'vendor6')");

dbAdapter.executeUpdate("insert into linkds values ('link1', 'memberof1')");
dbAdapter.executeUpdate("insert into linkds values ('link2', 'memberof2')");
dbAdapter.executeUpdate("insert into linkds values ('link3', 'memberof3')");
dbAdapter.executeUpdate("insert into linkds values ('link4', 'memberof4')");
dbAdapter.executeUpdate("insert into linkds values ('link5', 'memberof5')");
dbAdapter.executeUpdate("insert into linkds values ('link6', 'memberof6')");

dbAdapter.executeUpdate("insert into services values ('service1', 'manager1', 'status1')");
dbAdapter.executeUpdate("insert into services values ('service2', 'manager2', 'status2')");
dbAdapter.executeUpdate("insert into services values ('service3', 'manager3', 'status3')");

dbAdapter.executeUpdate("insert into events values ('event1', 'device1', 1, 'true', 'owner1', 'descr1','1999-01-01', '1999-01-01')");
dbAdapter.executeUpdate("insert into events values ('event2', 'device1', 1, 'true', 'owner2', 'descr2','1999-02-01', '1999-02-01')");
dbAdapter.executeUpdate("insert into events values ('event3', 'device2', 1, 'true', 'owner3', 'descr3','1999-03-01', '1999-03-01')");
dbAdapter.executeUpdate("insert into events values ('event4', 'device2', 1, 'false', 'owner4', 'descr4','1999-04-01', '1999-04-01')");
dbAdapter.executeUpdate("insert into events values ('event5', 'device3', 1, 'false', 'owner5', 'descr5','1999-05-01', '1999-05-01')");
dbAdapter.executeUpdate("insert into events values ('event6', 'device3', 1, 'false', 'owner6', 'descr6','1999-06-01', '1999-06-01')");



def ds1 = SingleTableDatabaseDatasource.findByName("DS1");
if(ds1 == null){
    ds1 = new SingleTableDatabaseDatasource(name:"DS1", connection: dsConn, tableName:"resources1", tableKeys:"name").save();
}

def ds2 = SingleTableDatabaseDatasource.findByName("DS2");
if(ds2 == null){
    ds2 = new SingleTableDatabaseDatasource(name:"DS2", connection: dsConn, tableName:"resources2", tableKeys:"ID").save();
}

def deviceDS = SingleTableDatabaseDatasource.findByName("DeviceDS");
if(deviceDS == null){
    deviceDS = new SingleTableDatabaseDatasource(name:"DeviceDS", connection: dsConn, tableName:"deviceds", tableKeys:"ID").save();
}
def linkDS = SingleTableDatabaseDatasource.findByName("LINKDS");
if(linkDS == null){
    linkDS = new SingleTableDatabaseDatasource(name:"LINKDS", connection: dsConn, tableName:"linkds", tableKeys:"ID").save();
}

def serviceDS = SingleTableDatabaseDatasource.findByName("serviceDS");
if(serviceDS == null){
    serviceDS = new SingleTableDatabaseDatasource(name:"serviceDS", connection: dsConn, tableName:"services", tableKeys:"name").save();
}

def eventDS = SingleTableDatabaseDatasource.findByName("EVENTDS");
if(eventDS == null){
    eventDS = new SingleTableDatabaseDatasource(name:"EVENTDS", connection: dsConn, tableName:"events", tableKeys:"EventName").save();
}

def cds = SingleTableDatabaseDatasource.findByName("CustomerDS");
if(cds == null){
    cds = new SingleTableDatabaseDatasource(name:"CustomerDS", connection: dsConn, tableName:"customers", tableKeys:"name").save();
}

return "Successfully executed";