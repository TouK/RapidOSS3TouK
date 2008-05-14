import datasource.*
import connection.*

CONNAME = "mysql";
DSNAME = "ds1";
STDSNAME = "stds1";
NCCONNAME = "ncConn";
NC_URL = "jdbc:sybase:Tds:ossmuse:4100/?LITERAL_PARAMS=true";
USERNAME = "root";
PSW = "";
NCDSNAME = "ncDs";

generateConnAndDSForDatabase();
testDatabaseDatasource();
testSingleTableDatabaseDatasource();
testNetcoolDatasource();

return "successfully run";


def generateConnAndDSForDatabase(){
  def conn1 = DatabaseConnection.findByName(CONNAME);
	if(conn1 == null){
	    conn1 = new DatabaseConnection(name:CONNAME, driver:"com.mysql.jdbc.Driver",
	            url:"jdbc:mysql://192.168.1.100/test", username:"root", password:"root").save();
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

// TEST NETCOOLDATASOURCE
def testNetcoolDatasource(){
    configureForNC();
    def eventId= "event999";
    testAddEvent(eventId);
    testGetEvent(eventId);
    testGetEventWithColumnList('vixenMachineStats3Stats');
    testRemoveEvent(eventId);
    testUpdateEvent(eventId);
    testTaskListAction(eventId);
    testAssignAction(eventId);
    testTakeOwnershipAction(eventId);
    testSetSeverityAction(eventId);
    testSuppressAction(eventId);
    testAcknowledgeAction(eventId);
}

def configureForNC(){
    def datasources = NetcoolDatasource.list()*.delete(flush:true);
    def connections= NetcoolConnection.list()*.delete(flush:true);
    def nc;
    nc = NetcoolConnection.add(name: NCCONNAME, url: NC_URL,username: USERNAME, password:PSW);

    def ds;
    def props =[:];
    props.put('name',NCDSNAME); // DO NOT USE " FOR KEY. IT CAUSES NULL POINTER EXCEPTION!
    props.put('connection',nc);
    ds = NetcoolDatasource.add(props);
}
def testGetEvent(id){
    def ds = NetcoolDatasource.findByName(NCDSNAME);
    def serial = ds.getSerialFromNetcool(id);
    def event = ds.getEvent(serial);
    assert event.identifier == id;
}
def testGetEventWithColumnList(id){
    def columns = ['Serial', 'Node'];
    def ds = NetcoolDatasource.findByName(NCDSNAME);
    def serial = ds.getSerialFromNetcool(id);
    def event = ds.getEvent(serial,columns);
    println "event:$event"
    assert event.node == "vixen"
    assert event.severity == null;
}
def testRemoveEvent(id){
    def ds = NetcoolDatasource.findByName(NCDSNAME);
    def props = ["Identifier":id,"Node":"myNode", "Severity":3];
    def serial = createEvent(id, props);
    ds.removeEvent(serial);
    def event = ds.getEvent(serial);
    assert event.size()==0;
}
def testAddEvent(id){
    def props = ["Identifier":id,"Node":"myNode", "Severity":3];
    def ds = NetcoolDatasource.findByName(NCDSNAME);
    try{
	    def serial = ds.getSerialFromNetcool(id);
        ds.removeEvent(serial);
    }
    catch(Exception e){
    }
    ds.addEvent(props);
    serial = ds.getSerialFromNetcool(id);
    def event = ds.getEvent(serial);
    assert event.identifier==id;
}
def testUpdateEvent(id){
    def ds = NetcoolDatasource.findByName(NCDSNAME);
    def props = ["Identifier":id,"Node":"myNode", "Severity":3];
    def serial = createEvent(id, props);
    ds.updateEvent(["ServerSerial":serial,"Node":"yourNode"]);
    def event = ds.getEvent(serial);
    assert event.node == "yourNode";
}
def testTaskListAction(id){
    def ds = NetcoolDatasource.findByName(NCDSNAME);
    def props = ["Identifier":id,"Node":"myNode", "Severity":3];
    def serial = createEvent(id, props);
    ds.taskListAction(serial,true);
    def event = ds.getEvent(serial);
    assert event.tasklist == "1";
}
def testAssignAction(id){
    def ds = NetcoolDatasource.findByName(NCDSNAME);
    def props = ["Identifier":id,"Node":"myNode", "Severity":3];
    def serial = createEvent(id, props);
    def ownerUID= "65534";
    ds.assignAction(serial,ownerUID);
    def event = ds.getEvent(serial);
    assert event.ownerUID == ownerUID;
}
def testTakeOwnershipAction(id){
    def ds = NetcoolDatasource.findByName(NCDSNAME);
    def props = ["Identifier":id,"Node":"myNode", "Severity":3];
    def serial = createEvent(id, props);
    def ownerUID= 65534;  // PROBLEM IF INTEGER IS PASSED AS PARAMETER
    ds.assignAction(serial,ownerUID);
    def event = ds.getEvent(serial);
    assert Integer.parseInt(event.ownerUID) == ownerUID;
}
def testSetSeverityAction(id){
    def ds = NetcoolDatasource.findByName(NCDSNAME);
    def props = ["Identifier":id,"Node":"myNode","Severity":0];
    def serial = createEvent(id, props);
    def severity = 2;
    ds.setSeverityAction(serial,severity,"Tugrul");
    def event = ds.getEvent(serial);
    assert Integer.parseInt(event.severity) == severity;
    sleep(6000);
    severity = "4";
    ds.setSeverityAction(serial,severity,"Pinar");
    event = ds.getEvent(serial);
    assert event.severity == severity;
}
def testSuppressAction(id){
    def ds = NetcoolDatasource.findByName(NCDSNAME);
    def props = ["Identifier":id,"Node":"myNode", "Severity":3, "SuppressEscl":0];
    def serial = createEvent(id, props);
    def suppress = 3;
    ds.suppressAction(serial,suppress,"Tugrul");
    def event = ds.getEvent(serial);
    assert Integer.parseInt(event.suppressescl) == suppress;
    suppress = "5";
    ds.suppressAction(serial,suppress,"Pinar");
    event = ds.getEvent(serial);
    assert event.suppressescl == suppress;
}
def testAcknowledgeAction(id){
    def ds = NetcoolDatasource.findByName(NCDSNAME);
    def props = ["Identifier":id,"Node":"myNode", "Severity":3, "Acknowledged":0];
    def serial = createEvent(id, props);
    ds.acknowledgeAction(serial,true,"Tugrul");
    def event = ds.getEvent(serial);
    assert event.acknowledged == "1";
}
def createEvent(id, props){
    def ds = NetcoolDatasource.findByName(NCDSNAME);
    def serial;
    try{
	    serial = ds.getSerialFromNetcool(id);
    	def event = ds.getEvent(serial);
    	serial = Integer.parseInt(event.serverserial);
	}
	catch(Exception e){
        ds.addEvent(props);
        serial = ds.getSerialFromNetcool(id);
    }
    return serial;
}


