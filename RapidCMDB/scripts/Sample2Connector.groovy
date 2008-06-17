import connection.*
import datasource.*
import org.apache.log4j.Logger

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

// Clean RCMDB model instances
cleanRCMDB();

// Create connection and datasources
def datasources = [:];
datasources = checkDatasources();

// Create tables in MySql DB
prepareDBTables();

// Populate RCMDB
populateRCMDB(datasources);

return "Successfully executed";


def cleanRCMDB(){
	Resource.list().each{
		it.remove();
	}
	Sla.list().each{
		it.remove();
	}
	Service.list().each{
		it.remove();
	}
	Customer.list().each{
		it.remove();
	}
	Event.list().each{
		it.remove();
	}
}

def checkDatasources(){
	def datasources =[:];
	def conn1 = DatabaseConnection.get(name:"mysql");
	
	if(conn1 == null){
	    conn1 = connection.DatabaseConnection.add(name:"mysql", driver:"com.mysql.jdbc.Driver", url:"jdbc:mysql://localhost/test", username:"root", userPassword:"root");
	}

	dsCustomer = SingleTableDatabaseDatasource.get(name:"dsCustomer");
	if (dsCustomer == null){
	    dsCustomer = datasource.SingleTableDatabaseDatasource.add(connection:conn1, name:"dsCustomer", tableName:"customers", tableKeys:"name");
	}
	datasources.put("customer","dsCustomer");

	dsService = SingleTableDatabaseDatasource.get(name:"dsService");
	if (dsService == null){
	    dsService = datasource.SingleTableDatabaseDatasource.add(connection:conn1, name:"dsService", tableName:"services", tableKeys:"name");
	}
	datasources.put("service","dsService");

	dsEvent = SingleTableDatabaseDatasource.get(name:"dsEvent");
	if (dsEvent == null){
	    dsEvent= datasource.SingleTableDatabaseDatasource.add(connection:conn1, name:"dsEvent", tableName:"events", tableKeys:"EventName");
	}
	datasources.put("event","dsEvent");

	dsDevice = SingleTableDatabaseDatasource.get(name:"dsDevice");
	if (dsDevice == null){
	    dsDevice = datasource.SingleTableDatabaseDatasource.add(connection:conn1, name:"dsDevice", tableName:"deviceds", tableKeys:"ID");
	}
	datasources.put("device","dsDevice");

	dsLink = SingleTableDatabaseDatasource.get(name:"dsLink");
	if (dsLink == null){
	    dsLink = datasource.SingleTableDatabaseDatasource.add(connection:conn1, name:"dsLink", tableName:"linkds", tableKeys:"ID");
	}
	datasources.put("link","dsLink");

	dsResource1 = SingleTableDatabaseDatasource.get(name:"dsResource1");
	if (dsResource1 == null){
	    dsResource1 = datasource.SingleTableDatabaseDatasource.add(connection:conn1, name:"dsResource1", tableName:"resources1", tableKeys:"name");
	}
	datasources.put("resource1","dsResource1");

	dsResource2 = SingleTableDatabaseDatasource.get(name:"dsResource2");
	if (dsResource2 == null){
	    dsResource1 = datasource.SingleTableDatabaseDatasource.add(connection:conn1, name:"dsResource2", tableName:"resources2", tableKeys:"ID");
	}
	datasources.put("resource2","dsResource2");
	return datasources;
}

def prepareDBTables(){
	def dsConn = DatabaseConnection.get(name:"mysql");
	if(dsConn == null){
	    dsConn = connection.DatabaseConnection.add(name:"mysql", driver:"com.mysql.jdbc.Driver",url:"jdbc:mysql://localhost/test", username:"root", userPassword:"root");
	}
// Creating a DatabaseAdapter using "new" will only create a variable, and wont add to RapidCMDB
	def dbDs = DatabaseDatasource.add(connection:dsConn,name:"dbDs");

	try{
	    dbDs.runUpdate("drop table resources1");
	}
	catch(e){}
	try{
	    dbDs.runUpdate("drop table resources2");
	}
	catch(e){}
	try{
	    dbDs.runUpdate("drop table deviceds");
	}
	catch(e){}
	try{
	    dbDs.runUpdate("drop table linkds");
	}
	catch(e){}

	try{
	    dbDs.runUpdate("drop table services");
	}
	catch(e){}
	try{
	    dbDs.runUpdate("drop table events");
	}
	catch(e){}
	try{
	    dbDs.runUpdate("drop table customers");
	}
	catch(e){}


	dbDs.runUpdate("create table resources1 (name varchar(50), displayname varchar(50), classname varchar(50), operationalstate varchar(50), model varchar(50), location varchar(50), vendor varchar(50), primary key (name));");
	dbDs.runUpdate("create table resources2 (ID varchar(50), displayname varchar(50), classname varchar(50), operationalstate varchar(50), model varchar(50), location varchar(50), vendor varchar(50), primary key (ID));");
	dbDs.runUpdate("create table deviceds (ID varchar(50), ip varchar(50), primary key (ID));");
	dbDs.runUpdate("create table linkds (ID varchar(50), memberof varchar(50), primary key (ID));");
	dbDs.runUpdate("create table services (name varchar(50), manager varchar(50), status varchar(50), primary key (name));");
	dbDs.runUpdate("create table events (EventName varchar(50), Resource varchar(50), Severity integer, Acknowledged varchar(10), Owner varchar(50), Description varchar(50), LastChangedAt datetime, LastOccuredAt datetime,  primary key (EventName));");
	dbDs.runUpdate("create table customers (name varchar(50), manager varchar(50), primary key (name));");

	dbDs.runUpdate("insert into customers values ('c1', 'manager1')");
	dbDs.runUpdate("insert into customers values ('c2', 'manager2')");

	dbDs.runUpdate("insert into resources1 values ('device1', 'device1', 'Device', 'state1', 'model1', 'location1', 'vendor1')");
	dbDs.runUpdate("insert into resources1 values ('device2', 'device2', 'Device', 'state2', 'model2', 'location2', 'vendor2')");
	dbDs.runUpdate("insert into resources1 values ('device3', 'device3', 'Device', 'state3', 'model3', 'location3', 'vendor3')");
	dbDs.runUpdate("insert into resources2 values ('device4', 'device4', 'Device', 'state4', 'model4', 'location4', 'vendor4')");
	dbDs.runUpdate("insert into resources2 values ('device5', 'device5', 'Device', 'state5', 'model5', 'location5', 'vendor5')");
	dbDs.runUpdate("insert into resources2 values ('device6', 'device6', 'Device', 'state6', 'model6', 'location6', 'vendor6')");

	dbDs.runUpdate("insert into deviceds values ('device1', 'ip1')");
	dbDs.runUpdate("insert into deviceds values ('device2', 'ip2')");
	dbDs.runUpdate("insert into deviceds values ('device3', 'ip3')");
	dbDs.runUpdate("insert into deviceds values ('device4', 'ip4')");
	dbDs.runUpdate("insert into deviceds values ('device5', 'ip5')");
	dbDs.runUpdate("insert into deviceds values ('device6', 'ip6')");

	dbDs.runUpdate("insert into resources1 values ('link1', 'link1', 'Link', 'state1', 'model1', 'location1', 'vendor1')");
	dbDs.runUpdate("insert into resources1 values ('link2', 'link2', 'Link', 'state2', 'model2', 'location2', 'vendor2')");
	dbDs.runUpdate("insert into resources1 values ('link3', 'link3', 'Link', 'state3', 'model3', 'location3', 'vendor3')");
	dbDs.runUpdate("insert into resources2 values ('link4', 'link4', 'Link', 'state4', 'model4', 'location4', 'vendor4')");
	dbDs.runUpdate("insert into resources2 values ('link5', 'link5', 'Link', 'state5', 'model5', 'location5', 'vendor5')");
	dbDs.runUpdate("insert into resources2 values ('link6', 'link6', 'Link', 'state6', 'model6', 'location6', 'vendor6')");

	dbDs.runUpdate("insert into linkds values ('link1', 'memberof1')");
	dbDs.runUpdate("insert into linkds values ('link2', 'memberof2')");
	dbDs.runUpdate("insert into linkds values ('link3', 'memberof3')");
	dbDs.runUpdate("insert into linkds values ('link4', 'memberof4')");
	dbDs.runUpdate("insert into linkds values ('link5', 'memberof5')");
	dbDs.runUpdate("insert into linkds values ('link6', 'memberof6')");

	dbDs.runUpdate("insert into services values ('service1', 'manager1', 'status1')");
	dbDs.runUpdate("insert into services values ('service2', 'manager2', 'status2')");
	dbDs.runUpdate("insert into services values ('service3', 'manager3', 'status3')");

	dbDs.runUpdate("insert into events values ('event1', 'device1', 1, 'true', 'owner1', 'descr1','1999-01-01', '1999-01-01')");
	dbDs.runUpdate("insert into events values ('event2', 'device1', 1, 'true', 'owner2', 'descr2','1999-02-01', '1999-02-01')");
	dbDs.runUpdate("insert into events values ('event3', 'device2', 1, 'true', 'owner3', 'descr3','1999-03-01', '1999-03-01')");
	dbDs.runUpdate("insert into events values ('event4', 'device2', 1, 'false', 'owner4', 'descr4','1999-04-01', '1999-04-01')");
	dbDs.runUpdate("insert into events values ('event5', 'device3', 1, 'false', 'owner5', 'descr5','1999-05-01', '1999-05-01')");
	dbDs.runUpdate("insert into events values ('event6', 'device3', 1, 'false', 'owner6', 'descr6','1999-06-01', '1999-06-01')");

}

def populateRCMDB(datasources){
	def ds1 = SingleTableDatabaseDatasource.get(name:datasources.resource1);
	def ds2 = SingleTableDatabaseDatasource.get(name:datasources.resource2);
	def serviceDs = SingleTableDatabaseDatasource.get(name:datasources.service);
	def eventDs = SingleTableDatabaseDatasource.get(name:datasources.event);
	def cds = SingleTableDatabaseDatasource.get(name:datasources.customer);

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

	Sla.add(slaId:"1", level:"Gold", customer: Customer.get(["name":"c1"]), service:Service.get(["name":"service1"]));
	Sla.add(slaId:"2", level:"Standard", customer: Customer.get(["name":"c1"]), service:Service.get(["name":"service2"]));
	Sla.add(slaId:"3", level:"Standard", customer: Customer.get(["name":"c2"]), service:Service.get(["name":"service1"]));
	Sla.add(slaId:"4", level:"Platinum", customer: Customer.get(["name":"c2"]), service:Service.get(["name":"service3"]));

	records = ds1.getRecords(["name", "classname", "displayname"]);
	for(record in records){
	    def className = record.CLASSNAME;
	    def name = record.NAME;
	    def displayname = record.DISPLAYNAME;
	    if(className == "Device"){
	          Device.add(name:name, displayname:displayname, dsname:"dsResource1");
	    }
	    else{
	           Link.add(name:name, displayname:displayname, dsname:"dsResource1");
	    }
	}
	records = ds2.getRecords(["id", "classname", "displayname"]);
	for(record in records){
	    def className = record.CLASSNAME;
	    def name = record.ID;
	    def displayname = record.DISPLAYNAME;
	    if(className == "Device"){
	          Device.add(name:name, displayname:displayname, dsname:"dsResource2");
	    }
	    else{
	        Link.add(name:name, displayname:displayname, dsname:"dsResource2");
	    }
	}

	/*UNCOMMENT WHEN CMDB-283 IS FIXED 
	
	def service1 = Service.get(["name":"service1"]);
	service1.addRelation(devices:Device.get(["name":"device1"]));
	service1.addRelation(devices:Device.get(["name":"device2"]));
	service1.addRelation(devices:Device.get(["name":"device3"]));
	service1.addRelation(links:Link.get(["name":"link1"]));
	service1.addRelation(links:Link.get(["name":"link2"]));
	service1.addRelation(links:Link.get(["name":"link3"]));

	def service2 = Service.get(["name":"service2"]);
	service2.addRelation(devices:Device.get(["name":"device3"]));
	service2.addRelation(devices:Device.get(["name":"device4"]));
	service2.addRelation(links:Link.get(["name":"link3"]));
	service2.addRelation(links:Link.get(["name":"link4"]));

	def service3 = Service.get(["name":"service3"]);
	service3.addRelation(devices:Device.get(["name":"device4"]));
	service3.addRelation(devices:Device.get(["name":"device5"]));
	service3.addRelation(devices:Device.get(["name":"device6"]));
	service3.addRelation(links:Link.get(["name":"link4"]));
	service3.addRelation(links:Link.get(["name":"link5"]));
	service3.addRelation(links:Link.get(["name":"link6"]));*/

	def service1 = Service.get(["name":"service1"]);
	service1.addRelation(devices:Device.get(["name":"device1"]));
	service1.addRelation(devices:Device.get(["name":"device2"]));
	service1.addRelation(devices:Device.get(["name":"device3"]));
	service1.addRelation(links:Link.get(["name":"link1"]));
	service1.addRelation(links:Link.get(["name":"link2"]));
	service1.addRelation(links:Link.get(["name":"link3"]));

	def service2 = Service.get(["name":"service2"]);
	service2.addRelation(devices:Device.get(["name":"device3"]));
	service2.addRelation(devices:Device.get(["name":"device4"]));
	service2.addRelation(links:Link.get(["name":"link3"]));
	service2.addRelation(links:Link.get(["name":"link4"]));

	def service3 = Service.get(["name":"service3"]);
	service3.addRelation(devices:Device.get(["name":"device4"]));
	service3.addRelation(devices:Device.get(["name":"device5"]));
	service3.addRelation(devices:Device.get(["name":"device6"]));
	service3.addRelation(links:Link.get(["name":"link4"]));
	service3.addRelation(links:Link.get(["name":"link5"]));
	service3.addRelation(links:Link.get(["name":"link6"]));

	records = eventDs.getRecords();

	for(record in records){

	    def eventName = record.EVENTNAME;
	    def resource = record.RESOURCE;

	    Resource.get(["name":resource]).addRelation(events:Event.add(name:eventName));
	}
}