package solutionTests.maintenanceTests

import application.RapidApplication
import com.ifountain.rcmdb.test.util.CompassForTests
import com.ifountain.rcmdb.test.util.RapidApplicationTestUtils
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase

/**
 * Created by IntelliJ IDEA.
 * User: sezgin
 * Date: 29.Oca.2010
 * Time: 11:27:37
 * To change this template use File | Settings | File Templates.
 */
class RsInMaintenanceOperationsTests extends RapidCmdbWithCompassTestCase {
    def RsTopologyObject;
    def RsEvent;
    def RsInMaintenance;
    def RsHistoricalInMaintenance;
    def RsInMaintenaceOperations;
    def RsEventOperations;

    def deviceName = "Device1";
    def info = "testMaintenance";
    def source = "testSource";

    public void setUp() {
        super.setUp();
        ["RsTopologyObject", "RsEvent", "RsInMaintenance", "RsHistoricalInMaintenance"].each {className ->
            setProperty(className, gcl.loadClass(className));
        }
        setProperty("RsEventOperations", gcl.loadClass("RsEventOperations"));
        def solutionPath = getWorkspacePath() + "/RapidModules/RapidInsight/solutions/inMaintenance"
        setProperty("RsInMaintenaceOperations", gcl.parseClass(new File("${solutionPath}/operations/RsInMaintenanceOperations.groovy")));

        initialize([RsEvent, RsTopologyObject, RapidApplication, RsInMaintenance, RsHistoricalInMaintenance], []);
        CompassForTests.addOperationSupport(RsEvent, RsEventOperations);
        CompassForTests.addOperationSupport(RsInMaintenance, RsInMaintenaceOperations);
        RapidApplicationTestUtils.initializeRapidApplicationOperations(RapidApplication);
        RapidApplicationTestUtils.clearProcessors();
        RapidApplicationTestUtils.utilityPaths = ["InMaintenanceCalculator": new File("${solutionPath}/operations/InMaintenanceCalculator.groovy")];
        RapidApplication.getUtility("EventProcessor").beforeProcessors = ["InMaintenanceCalculator"];
    }

    public void tearDown() {
        RapidApplicationTestUtils.clearProcessors();
        RapidApplicationTestUtils.clearUtilityPaths();
        super.tearDown();
    }

    public void testNoActiveMaintenance() throws Exception {
        def event11 = RsEvent.add(name: "Event11", elementName: deviceName)
        def event12 = RsEvent.add(name: "Event12", elementName: deviceName)

        assertFalse(RsInMaintenance.isObjectInMaintenance(deviceName))
        assertFalse(RsEvent.get(name: event11.name).inMaintenance)
        assertFalse(RsEvent.get(name: event12.name).inMaintenance)
    }

    public void testTakingADeviceOutOfMaintenanceWhichIsNotInMaintenance() throws Exception {
        def event11 = RsEvent.add(name: "Event11", elementName: deviceName)
        def event12 = RsEvent.add(name: "Event12", elementName: deviceName)

        RsInMaintenance.takeObjectOutOfMaintenance(deviceName)
        assertFalse(RsInMaintenance.isObjectInMaintenance(deviceName))
        assertFalse(RsEvent.get(name: event11.name).inMaintenance)
        assertFalse(RsEvent.get(name: event12.name).inMaintenance)
    }

    public void testMaintenanceWithNoEnding() throws Exception {
        def event11 = RsEvent.add(name: "Event11", elementName: deviceName)
        def event12 = RsEvent.add(name: "Event12", elementName: deviceName)

        def props = ["objectName": deviceName, "source": source, "info": info]
        def startTimeForMaint1 = System.currentTimeMillis();
        def maint1 = RsInMaintenance.putObjectInMaintenance(props)

        assertTrue(RsInMaintenance.isObjectInMaintenance(maint1.objectName))
        assertTrue(RsEvent.get(name: event11.name).inMaintenance)
        assertTrue(RsEvent.get(name: event12.name).inMaintenance)
        assertEquals(source, maint1.source)
        assertEquals(info, maint1.info)
        assertTrue(maint1.starting.getTime() >= startTimeForMaint1);
        assertEquals(0, maint1.ending.getTime())

        def eventDuringMaintenance1 = RsEvent.add(name: "eventDuringMaintenance1", elementName: maint1.objectName)
        assertTrue(eventDuringMaintenance1.inMaintenance)
    }



    public void testTakingObjectOutOfMaintenance() {
        def event11 = RsEvent.add(name: "Event11", elementName: deviceName)
        def event12 = RsEvent.add(name: "Event12", elementName: deviceName)

        def props = ["objectName": deviceName, "source": source, "info": info]
        def maint1 = RsInMaintenance.putObjectInMaintenance(props)

        assertTrue(RsInMaintenance.isObjectInMaintenance(maint1.objectName))
        assertTrue(RsEvent.get(name: event11.name).inMaintenance)
        assertTrue(RsEvent.get(name: event12.name).inMaintenance)

        RsInMaintenance.takeObjectOutOfMaintenance(maint1.objectName)
        assertFalse(RsInMaintenance.isObjectInMaintenance(maint1.objectName))
        assertFalse(RsEvent.get(name: event11.name).inMaintenance)
        assertFalse(RsEvent.get(name: event12.name).inMaintenance)
        def historicalMaintenances = RsHistoricalInMaintenance.searchEvery("objectName:${maint1.objectName}");
        assertEquals(1, historicalMaintenances.size())
        def allActiveMaintenanceProps = maint1.asMap();
        def allHistoricalMaintenanceProps = historicalMaintenances[0].asMap();
        allActiveMaintenanceProps.each {key, value ->
            if (key != "id" && key != "rsInsertedAt" && key != "rsUpdatedAt") {
                assertEquals(value, allHistoricalMaintenanceProps[key])
            }
        }

        def event13 = RsEvent.add(name: "Event13", elementName: maint1.objectName)
        assertFalse(event13.inMaintenance)
    }

    public void testMaintenanceWithDuration() {
        def startTime = new Date(System.currentTimeMillis() - 1500);
        def endTime = new Date(System.currentTimeMillis() + 3000)

        def event11 = RsEvent.add(name: "Event11", elementName: deviceName)
        def event12 = RsEvent.add(name: "Event12", elementName: deviceName)

        def props = ["objectName": deviceName, "source": source, "info": info, "starting": startTime, "ending": endTime]
        def maint1 = RsInMaintenance.putObjectInMaintenance(props)

        assertTrue(RsInMaintenance.isObjectInMaintenance(maint1.objectName))
        assertTrue(RsEvent.get(name: event11.name).inMaintenance)
        assertTrue(RsEvent.get(name: event12.name).inMaintenance)
        assertEquals(source, maint1.source)
        assertEquals(info, maint1.info)
        assertEquals(startTime.getTime(), maint1.starting.getTime());
        assertEquals(endTime.getTime(), maint1.ending.getTime());

        Thread.sleep(100)

        RsInMaintenance.removeExpiredItems() // still in maintenance

        assertTrue(RsInMaintenance.isObjectInMaintenance(maint1.objectName))
        assertTrue(RsEvent.get(name: event11.name).inMaintenance)
        assertTrue(RsEvent.get(name: event12.name).inMaintenance)

        def event14 = RsEvent.add(name: "Event14", elementName: maint1.objectName)
        assertTrue(RsEvent.get(name: event14.name).inMaintenance)

        def remainingTime = endTime.getTime() - System.currentTimeMillis();
        Thread.sleep(remainingTime + 100);
        RsInMaintenance.removeExpiredItems() // no longer in maintenance
        assertFalse(RsInMaintenance.isObjectInMaintenance(maint1.objectName))
        assertFalse(RsEvent.get(name: event11.name).inMaintenance)
        assertFalse(RsEvent.get(name: event12.name).inMaintenance)
        assertFalse(RsEvent.get(name: event14.name).inMaintenance)
    }

    public void testDoesNotChangeStartTimeIfThereIsAMaintenanceForTheSameObject() {
        def props = ["objectName": deviceName, "source": source, "info": info]
        def startTimeForMaint1 = System.currentTimeMillis();
        def maint1 = RsInMaintenance.putObjectInMaintenance(props)
        def oldStartTime = maint1.starting.getTime();

        assertTrue(maint1.starting.getTime() >= startTimeForMaint1);

        Thread.sleep(500);
        assertEquals(1, RsInMaintenance.count())

        def endTime = new Date(System.currentTimeMillis() + 5000000)
        props = ["objectName": deviceName, "source": source, "info": info, "ending": endTime]
        maint1 = RsInMaintenance.putObjectInMaintenance(props)

        assertEquals(oldStartTime, maint1.starting.getTime());

        def startTime = new Date(System.currentTimeMillis() + 3000000)
        props = ["objectName": deviceName, "source": source, "info": info, "starting": startTime];
        maint1 = RsInMaintenance.putObjectInMaintenance(props);

        assertEquals(oldStartTime, maint1.starting.getTime());
    }

    public void testDoesNotUpdateIfGivenEndingIsSmallerIfThereIsAMaintenanceForTheSameObject() {
        def endTime = new Date(System.currentTimeMillis() + 100000)
        def props = ["objectName": deviceName, "source": source, "info": info, "ending": endTime]
        def maint1 = RsInMaintenance.putObjectInMaintenance(props)

        assertEquals(endTime.getTime(), maint1.ending.getTime())
        assertEquals(source, maint1.source)
        assertEquals(info, maint1.info)

        props = ["objectName": deviceName, "source": "newSource", "info": "newInfo", "ending": new Date(endTime.getTime() - 1)]
        maint1 = RsInMaintenance.putObjectInMaintenance(props)

        assertEquals(endTime.getTime(), maint1.ending.getTime())
        assertEquals(source, maint1.source)
        assertEquals(info, maint1.info)

        def newEndTime = new Date(endTime.getTime() + 10000);
        props = ["objectName": deviceName, "source": "newSource", "info": "newInfo", "ending": newEndTime]
        maint1 = RsInMaintenance.putObjectInMaintenance(props)

        assertEquals(newEndTime.getTime(), maint1.ending.getTime())
        assertEquals("newSource", maint1.source)
        assertEquals("newInfo", maint1.info)

        //maintenance with ending 0 cannot be overriden if override option is false
        maint1.update(ending:new Date(0));

        maint1 = RsInMaintenance.putObjectInMaintenance(props)
        assertEquals(new Date(0).getTime(), maint1.ending.getTime())

    }

    public void testCanUpdateWithSmallerEndingIfOverrideOptionIsTrue() {
        def endTime = new Date(System.currentTimeMillis() + 100000)
        def props = ["objectName": deviceName, "source": source, "info": info, "ending": endTime]
        def maint1 = RsInMaintenance.putObjectInMaintenance(props)

        assertEquals(endTime.getTime(), maint1.ending.getTime())
        assertEquals(source, maint1.source)
        assertEquals(info, maint1.info)

        def newEndTime = new Date(endTime.getTime() - 10000);
        props = ["objectName": deviceName, "source": "newSource", "info": "newInfo", "ending": newEndTime]
        maint1 = RsInMaintenance.putObjectInMaintenance(props, true)

        assertEquals(newEndTime.getTime(), maint1.ending.getTime())
        assertEquals("newSource", maint1.source)
        assertEquals("newInfo", maint1.info)
    }

    public void testPuttingObjectIntoMaintenanceThrowsExceptionIfStartIsGreaterThanEnding() {
        def endTime = new Date(System.currentTimeMillis() - 1000)

        def props = ["objectName": deviceName, "source": source, "info": info, "ending": endTime]
        try {
            RsInMaintenance.putObjectInMaintenance(props)
            fail("should throw exception");
        }
        catch (e) {
            assertTrue("wrong exception ${e}", e.getMessage().indexOf("time should be greater than starting time") >= 0);
        }
        assertEquals(0, RsInMaintenance.count())
    }

    public void testEventsAreInMaintenanceWhenMaintenanceAddedOrRemovedByAfterTriggers() {
        def event11 = RsEvent.add(name: "Event11", elementName: deviceName)
        def event12 = RsEvent.add(name: "Event12", elementName: deviceName)

        def props = ["objectName": deviceName, "source": source, "info": info]
        def maint1 = RsInMaintenance.add(props)

        assertTrue(RsInMaintenance.isObjectInMaintenance(maint1.objectName))
        assertTrue(RsEvent.get(name: event11.name).inMaintenance)
        assertTrue(RsEvent.get(name: event12.name).inMaintenance)

        maint1.remove();
        assertFalse(RsInMaintenance.isObjectInMaintenance(maint1.objectName))
        assertFalse(RsEvent.get(name: event11.name).inMaintenance)
        assertFalse(RsEvent.get(name: event12.name).inMaintenance)
        def historicalMaintenances = RsHistoricalInMaintenance.searchEvery("objectName:${maint1.objectName}");
        assertEquals(1, historicalMaintenances.size())
        def allActiveMaintenanceProps = maint1.asMap();
        def allHistoricalMaintenanceProps = historicalMaintenances[0].asMap();
        allActiveMaintenanceProps.each {key, value ->
            if (key != "id" && key != "rsInsertedAt" && key != "rsUpdatedAt") {
                assertEquals(value, allHistoricalMaintenanceProps[key])
            }
        }

        def event13 = RsEvent.add(name: "Event13", elementName: maint1.objectName)
        assertFalse(event13.inMaintenance)
    }
}