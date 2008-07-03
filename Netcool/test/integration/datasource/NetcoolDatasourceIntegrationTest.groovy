package datasource
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 3, 2008
 * Time: 11:38:39 AM
 * To change this template use File | Settings | File Templates.
 */
class NetcoolDatasourceIntegrationTest {
    
//    // TEST NETCOOLDATASOURCE
//    def testNetcoolDatasource(){
//        configureForNC();
//        def eventId= "event999";
//        testAddEvent(eventId);
//        testGetEvent(eventId);
//        testGetEventWithColumnList('vixenMachineStats3Stats');
//        testRemoveEvent(eventId);
//        testUpdateEvent(eventId);
//        testTaskListAction(eventId);
//        testAssignAction(eventId);
//        testTakeOwnershipAction(eventId);
//        testSetSeverityAction(eventId);
//        testSuppressAction(eventId);
//        testAcknowledgeAction(eventId);
//    }
//
//    def configureForNC(){
//        def datasources = NetcoolDatasource.list()*.delete(flush:true);
//        def connections= NetcoolConnection.list()*.delete(flush:true);
//        def nc;
//        nc = NetcoolConnection.add(name: NCCONNAME, url: NC_URL,username: USERNAME, password:PSW);
//
//        def ds;
//        def props =[:];
//        props.put('name',NCDSNAME); // DO NOT USE " FOR KEY. IT CAUSES NULL POINTER EXCEPTION!
//        props.put('connection',nc);
//        ds = NetcoolDatasource.add(props);
//    }
//    def testGetEvent(id){
//        def ds = NetcoolDatasource.findByName(NCDSNAME);
//        def serial = ds.getSerialFromNetcool(id);
//        def event = ds.getEvent(serial);
//        assert event.identifier == id;
//    }
//    def testGetEventWithColumnList(id){
//        def columns = ['Serial', 'Node'];
//        def ds = NetcoolDatasource.findByName(NCDSNAME);
//        def serial = ds.getSerialFromNetcool(id);
//        def event = ds.getEvent(serial,columns);
//        println "event:$event"
//        assert event.node == "vixen"
//        assert event.severity == null;
//    }
//    def testRemoveEvent(id){
//        def ds = NetcoolDatasource.findByName(NCDSNAME);
//        def props = ["Identifier":id,"Node":"myNode", "Severity":3];
//        def serial = createEvent(id, props);
//        ds.removeEvent(serial);
//        def event = ds.getEvent(serial);
//        assert event.size()==0;
//    }
//    def testAddEvent(id){
//        def props = ["Identifier":id,"Node":"myNode", "Severity":3];
//        def ds = NetcoolDatasource.findByName(NCDSNAME);
//        try{
//            def serial = ds.getSerialFromNetcool(id);
//            ds.removeEvent(serial);
//        }
//        catch(Exception e){
//        }
//        ds.addEvent(props);
//        serial = ds.getSerialFromNetcool(id);
//        def event = ds.getEvent(serial);
//        assert event.identifier==id;
//    }
//    def testUpdateEvent(id){
//        def ds = NetcoolDatasource.findByName(NCDSNAME);
//        def props = ["Identifier":id,"Node":"myNode", "Severity":3];
//        def serial = createEvent(id, props);
//        ds.updateEvent(["ServerSerial":serial,"Node":"yourNode"]);
//        def event = ds.getEvent(serial);
//        assert event.node == "yourNode";
//    }
//    def testTaskListAction(id){
//        def ds = NetcoolDatasource.findByName(NCDSNAME);
//        def props = ["Identifier":id,"Node":"myNode", "Severity":3];
//        def serial = createEvent(id, props);
//        ds.taskListAction(serial,true);
//        def event = ds.getEvent(serial);
//        assert event.tasklist == "1";
//    }
//    def testAssignAction(id){
//        def ds = NetcoolDatasource.findByName(NCDSNAME);
//        def props = ["Identifier":id,"Node":"myNode", "Severity":3];
//        def serial = createEvent(id, props);
//        def ownerUID= "65534";
//        ds.assignAction(serial,ownerUID);
//        def event = ds.getEvent(serial);
//        assert event.ownerUID == ownerUID;
//    }
//    def testTakeOwnershipAction(id){
//        def ds = NetcoolDatasource.findByName(NCDSNAME);
//        def props = ["Identifier":id,"Node":"myNode", "Severity":3];
//        def serial = createEvent(id, props);
//        def ownerUID= 65534;  // PROBLEM IF INTEGER IS PASSED AS PARAMETER
//        ds.assignAction(serial,ownerUID);
//        def event = ds.getEvent(serial);
//        assert Integer.parseInt(event.ownerUID) == ownerUID;
//    }
//    def testSetSeverityAction(id){
//        def ds = NetcoolDatasource.findByName(NCDSNAME);
//        def props = ["Identifier":id,"Node":"myNode","Severity":0];
//        def serial = createEvent(id, props);
//        def severity = 2;
//        ds.setSeverityAction(serial,severity,"Tugrul");
//        def event = ds.getEvent(serial);
//        assert Integer.parseInt(event.severity) == severity;
//        sleep(6000);
//        severity = "4";
//        ds.setSeverityAction(serial,severity,"Pinar");
//        event = ds.getEvent(serial);
//        assert event.severity == severity;
//    }
//    def testSuppressAction(id){
//        def ds = NetcoolDatasource.findByName(NCDSNAME);
//        def props = ["Identifier":id,"Node":"myNode", "Severity":3, "SuppressEscl":0];
//        def serial = createEvent(id, props);
//        def suppress = 3;
//        ds.suppressAction(serial,suppress,"Tugrul");
//        def event = ds.getEvent(serial);
//        assert Integer.parseInt(event.suppressescl) == suppress;
//        suppress = "5";
//        ds.suppressAction(serial,suppress,"Pinar");
//        event = ds.getEvent(serial);
//        assert event.suppressescl == suppress;
//    }
//    def testAcknowledgeAction(id){
//        def ds = NetcoolDatasource.findByName(NCDSNAME);
//        def props = ["Identifier":id,"Node":"myNode", "Severity":3, "Acknowledged":0];
//        def serial = createEvent(id, props);
//        ds.acknowledgeAction(serial,true,"Tugrul");
//        def event = ds.getEvent(serial);
//        assert event.acknowledged == "1";
//    }
//    def createEvent(id, props){
//        def ds = NetcoolDatasource.findByName(NCDSNAME);
//        def serial;
//        try{
//            serial = ds.getSerialFromNetcool(id);
//            def event = ds.getEvent(serial);
//            serial = Integer.parseInt(event.serverserial);
//        }
//        catch(Exception e){
//            ds.addEvent(props);
//            serial = ds.getSerialFromNetcool(id);
//        }
//        return serial;
//    }
}