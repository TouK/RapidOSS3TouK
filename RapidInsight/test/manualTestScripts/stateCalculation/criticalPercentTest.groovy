import application.RapidApplication
import com.ifountain.comp.utils.SmartWait
import com.ifountain.rcmdb.util.ClosureWaitAction

logger.warn("Starting..............")


try {
    RapidApplication.getUtility("StateCalculator").calculateMethod = "criticalPercent";

    //RI- FINDMAX
    final static int CRITICAL = 5
    final static int MAJOR = 4
    final static int WARNING = 2
    final static int NORMAL = 0
    final static int NOTSET = -1

    RsCustomer.removeAll()
    RsService.removeAll()
    RsTopologyObject.removeAll()
    RsEvent.removeAll()
    RsObjectState.removeAll();

    def cust1 = RsCustomer.add([name: "Cust1"])
    def cust2 = RsCustomer.add([name: "Cust2"])

    def serv11 = RsService.add([name: "Service11"])
    def serv12 = RsService.add([name: "Service12"])
    def serv21 = RsService.add([name: "Service21"])
    def serv22 = RsService.add([name: "Service22"])

    def device111 = RsTopologyObject.add([name: "Device111"])
    def device112 = RsTopologyObject.add([name: "Device112"])
    def device121 = RsTopologyObject.add([name: "Device121"])
    def device122 = RsTopologyObject.add([name: "Device122"])
    def device211 = RsTopologyObject.add([name: "Device211"])
    def device212 = RsTopologyObject.add([name: "Device212"])
    def device221 = RsTopologyObject.add([name: "Device221"])
    def device222 = RsTopologyObject.add([name: "Device222"])

    cust1.addRelation(childObjects: [serv11, serv12])
    cust2.addRelation(childObjects: [serv21, serv22])

    serv11.addRelation(childObjects: [device111, device112])
    serv12.addRelation(childObjects: [device121, device122])
    serv21.addRelation(childObjects: [device211, device212])
    serv22.addRelation(childObjects: [device221, device222])

    assert device111.currentState() == NOTSET
    assert serv11.currentState() == NOTSET
    assert cust1.currentState() == NOTSET


    def event1111 = RsEvent.add(name: "Event1111", elementName: device111.name, severity: NORMAL)

    SmartWait.waitFor(new ClosureWaitAction({
        assert device111.currentState() == NORMAL
        assert serv11.currentState() == NORMAL
        assert cust1.currentState() == NORMAL
    }))

    def event1112 = RsEvent.add(name: "Event1112", elementName: device111.name, severity: MAJOR)
    SmartWait.waitFor(new ClosureWaitAction({
        assert device111.currentState() == NORMAL
        assert serv11.currentState() == NORMAL
        assert cust1.currentState() == NORMAL
    }))


    def event1113 = RsEvent.add(name: "Event1113", elementName: device111.name, severity: CRITICAL)
    SmartWait.waitFor(new ClosureWaitAction({
        assert device111.currentState() == MAJOR
        assert serv11.currentState() == NORMAL
        assert cust1.currentState() == NORMAL
    }))


    event1112.severity = CRITICAL
    SmartWait.waitFor(new ClosureWaitAction({
        assert device111.currentState() == CRITICAL
        assert serv11.currentState() == CRITICAL
        assert cust1.currentState() == CRITICAL
    }))


    event1112.severity = MAJOR
    SmartWait.waitFor(new ClosureWaitAction({
        assert device111.currentState() == MAJOR
        assert serv11.currentState() == NORMAL
        assert cust1.currentState() == NORMAL
    }))


    def event1221 = RsEvent.add(name: "Event1221", elementName: device122.name, severity: CRITICAL)
    SmartWait.waitFor(new ClosureWaitAction({
        assert device122.currentState() == CRITICAL
        assert serv12.currentState() == CRITICAL
        assert cust1.currentState() == CRITICAL
    }))


    event1221.clear();
    SmartWait.waitFor(new ClosureWaitAction({
        assert device122.currentState() == NORMAL
        assert serv12.currentState() == NORMAL
        assert cust1.currentState() == NORMAL
    }))



    //test RsObjectState removed when object is removed
    def objectId = device122.id;
    SmartWait.waitFor(new ClosureWaitAction({
        assert RsObjectState.get(objectId: objectId).state == NORMAL
    }))

    device122.remove();
    SmartWait.waitFor(new ClosureWaitAction({
        assert RsObjectState.get(objectId: objectId) == null
    }))
    return "success"

}
catch (e)
{
    throw e;
}
finally {
    RapidApplication.getUtility("StateCalculator").setToDefault();
}