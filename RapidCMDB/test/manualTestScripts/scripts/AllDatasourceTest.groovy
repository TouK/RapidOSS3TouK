/*
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
import connection.DatabaseConnection
import datasource.DatabaseDatasource
import datasource.SingleTableDatabaseDatasource

CONNAME = "mysql";
DSNAME = "ds1";
STDSNAME = "stds1";

generateConnAndDSForDatabase();
testDatabaseDatasource();
testSingleTableDatabaseDatasource();

return "successfully run";


def generateConnAndDSForDatabase(){
  def conn1 = DatabaseConnection.get(name:CONNAME);
	if(conn1 == null){
	    conn1 = DatabaseConnection.add(name:CONNAME, driver:"oracle.jdbc.driver.OracleDriver",url:"jdbc:oracle:thin:@192.168.1.100:1521:xe", username:"system", userPassword:"system",minTimeout:20,maxTimeout:20);
	}
	def ds= DatabaseDatasource.get(name:DSNAME);
	if (ds == null){
	    ds = DatabaseDatasource.add(connection:conn1, name:DSNAME);
	}
	ds= SingleTableDatabaseDatasource.get(name:STDSNAME);
	if (ds == null){
	    ds = SingleTableDatabaseDatasource.add(connection:conn1, name:STDSNAME, tableName:"testtable", tableKeys:"name");
	}
}

// TEST DATABASEDATASOURCE
def testDatabaseDatasource(){
    def ds = DatabaseDatasource.get(name:"ds1");
    try{
        ds.runUpdate("drop table testtable");
    }
    catch(e){}
    ds.runUpdate("create table testtable (name varchar(50), age varchar(5));");
    ds.runUpdate("insert into testtable values (?,?);",['my name','20']);
    ds.runUpdate("insert into testtable values (?,?);",['your name','30']);
    ds.runUpdate("insert into testtable values (?,?);",['his name','40']);

    def records = ds.runQuery("select * from testtable where name=\"my name\";");
    assert records[0].age=="20";
}

// TEST SINGLETABLEDATABASEDATASOURCE
def testSingleTableDatabaseDatasource(){
    def ds = SingleTableDatabaseDatasource.get(name:"stds1");
    def record = ds.getRecord("your name");
    assert record.age=="30";

    def records = ds.getRecords(["name"]);
    println records;
    assert records.size() == 3;

    records = ds.getRecords("age>\"30\"");
    println records;
    assert records[0].age=="40";

    records = ds.getRecords("age>\"30\"",["name"]);
    println records;
    assert records[0].name=="his name";

    ds.updateRecord(["name":"my name","age":"22"]);
    record = ds.getRecord("my name");
    assert record.age == "22";

    ds.removeRecord("my name");
    record = ds.getRecord("my name");
    assert record == [:];

    ds.addRecord(["name":"my name","age":"20"]);
    record = ds.getRecord("my name");
    assert record.age == "20";

    try{
        ds.runUpdate("drop table testtable");
    }
    catch(e){}
    ds.runUpdate("create table testtable (name varchar(50), age varchar(5));");
    ds.runUpdate("insert into testtable values (?,?);",['my name','20']);
    ds.runUpdate("insert into testtable values (?,?);",['your name','30']);
    ds.runUpdate("insert into testtable values (?,?);",['his name','40']);

    //records = ds.runQuery("select * from testtable where name=\"my name\";");
    records = ds.runQuery("select * from testtable where name=?",["my name"]);
    assert records[0].age=="20";
}



