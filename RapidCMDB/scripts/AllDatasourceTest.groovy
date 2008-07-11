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
  def conn1 = DatabaseConnection.findByName(CONNAME);
	if(conn1 == null){
	    conn1 = DatabaseConnection.add(name:CONNAME, driver:"com.mysql.jdbc.Driver",url:"jdbc:mysql://192.168.1.100/test", username:"root", userPassword:"root");
	}
	def ds= DatabaseDatasource.findByName(DSNAME);
	if (ds == null){
	    ds = DatabaseDatasource.add(connection:conn1, name:DSNAME);
	}
	ds= SingleTableDatabaseDatasource.findByName(STDSNAME);
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



