import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Jun 24, 2008
* Time: 2:16:28 PM
* To change this template use File | Settings | File Templates.
*/
class ModelRelationTests extends RapidCmdbIntegrationTestCase {
   static transactional = false;
    public void setUp() {
        super.setUp();
        SmartsObject.list()*.remove();
    }

    public void tearDown() {
        super.tearDown();
    }

    void testAddOneToOneRelation() throws Exception {
        def ip = Ip.add(name: "myIp", creationClassName: "Ip", smartsDs: "smartsDs", ipAddress: "192.168.1.1");
        assertFalse(ip.hasErrors());

        def devInterface = DeviceInterface.add(name: "myDeviceInt", creationClassName: "DeviceInterface",
                smartsDs: "smartsDs", description: "desc", isManaged: "true", macAddress: "3245", type: "myType")
        assertFalse(devInterface.hasErrors());

        ip.addRelation(layeredOver: devInterface);
        assertFalse(ip.hasErrors());

        def layeredOverObject = ip.layeredOver
        assertNotNull(layeredOverObject);

        assertTrue(layeredOverObject instanceof DeviceInterface)
        assertEquals("myDeviceInt", layeredOverObject.name)
        assertEquals("DeviceInterface", layeredOverObject.creationClassName)
        assertNotNull(Ip.get(name: "myIp", creationClassName: "Ip").layeredOver);

        def underlyingObject = devInterface.underlying;
        assertNotNull(underlyingObject);

        assertTrue(underlyingObject instanceof Ip)
        assertEquals("myIp", underlyingObject.name)
        assertEquals("Ip", underlyingObject.creationClassName)

        assertNotNull(DeviceInterface.get(name: "myDeviceInt", creationClassName: "DeviceInterface").underlying)

        devInterface.remove();
        assertEquals(0, DeviceInterface.list().size())
        assertNull("layered over is not null", ip.layeredOver)
    }

    void testAddOneToOneRelationIsIgnoredIfRelationAlreadyExists(){
    	def ip = Ip.add(name: "myIp", creationClassName: "Ip", smartsDs: "smartsDs", ipAddress: "192.168.1.1");
    	assertFalse(ip.hasErrors())
        def devInterface = DeviceInterface.add(name: "myDeviceInt1", creationClassName: "DeviceInterface",
                smartsDs: "smartsDs", description: "desc", isManaged: "true", macAddress: "3245", type: "myType")
        assertFalse(devInterface.hasErrors());

        ip.addRelation(layeredOver: devInterface);
                
        assertEquals("myDeviceInt1", ip.layeredOver.name)
                
        try {
        	ip.addRelation(layeredOver: devInterface);
        } catch (e)
        {
            fail("Should not throw exception")
        }
    }
    
    void testAddOneToOneRelationReplacesAlreadyExistingRelation() {
        def ip = Ip.add(name: "myIp", creationClassName: "Ip", smartsDs: "smartsDs", ipAddress: "192.168.1.1");

        def devInterface1 = DeviceInterface.add(name: "myDeviceInt1", creationClassName: "DeviceInterface",
                smartsDs: "smartsDs", description: "desc", isManaged: "true", macAddress: "3245", type: "myType")
        def devInterface2 = DeviceInterface.add(name: "myDeviceInt2", creationClassName: "DeviceInterface",
                smartsDs: "smartsDs", description: "desc", isManaged: "true", macAddress: "3245", type: "myType")

        ip.addRelation(layeredOver: devInterface1);
        assertEquals("myDeviceInt1", ip.layeredOver.name)

        assertNotNull(devInterface1.underlying);

        ip.addRelation(layeredOver: devInterface2);
        assertFalse(ip.hasErrors());

        assertEquals("myDeviceInt2", ip.layeredOver.name)
        assertEquals("myDeviceInt2", Ip.get(name: "myIp", creationClassName: "Ip").layeredOver.name)
        assertNull(devInterface1.underlying);
        assertNull(DeviceInterface.get(name: "myDeviceInt1", creationClassName: "DeviceInterface").underlying);
        assertNotNull(devInterface2.underlying);
        assertNotNull(DeviceInterface.get(name: "myDeviceInt2", creationClassName: "DeviceInterface").underlying);

        devInterface1.addRelation(underlying: ip)
        assertFalse(devInterface1.hasErrors())
        assertNotNull(devInterface1.underlying);

        assertNull(devInterface2.underlying)
        assertEquals("myDeviceInt1", ip.layeredOver.name)

    }

    void testAddOneToOneRelationWithInvalidRelationNameIsIgnored() {
        def ip = Ip.add(name: "myIp", creationClassName: "Ip", smartsDs: "smartsDs", ipAddress: "192.168.1.1");

        def devInterface = DeviceInterface.add(name: "myDeviceInt1", creationClassName: "DeviceInterface",
                smartsDs: "smartsDs", description: "desc", isManaged: "true", macAddress: "3245", type: "myType")

        try {
            ip.addRelation(invalidRelationName: devInterface)
        }
        catch (e) {
            fail("Should not throw exception");
        }
        assertFalse(ip.hasErrors());
    }

    void testAddOneToOneRelationWithInvalidObjectType() {
        def ip = Ip.add(name: "myIp", creationClassName: "Ip", smartsDs: "smartsDs", ipAddress: "192.168.1.1");

        def device = Device.add(name: "myDevice1", creationClassName: "Device", smartsDs: "smartsDs", ipAddress: "192.168.1.1",
                location: "myLocation", model: "myModel", snmpReadCommunity: "mysnmpReadCommunity", vendor: "myVendor")

        try {
            ip.addRelation(layeredOver: device)
        }
        catch (e) {
            fail("Should not throw exception");
        }

        assertFalse(ip.hasErrors());
        assertNull(ip.layeredOver)

    }

    void testRemoveOneToOneRelation() {

        def ip = Ip.add(name: "myIp", creationClassName: "Ip", smartsDs: "smartsDs", ipAddress: "192.168.1.1");

        def devInterface = DeviceInterface.add(name: "myDeviceInt", creationClassName: "DeviceInterface",
                smartsDs: "smartsDs", description: "desc", isManaged: "true", macAddress: "3245", type: "myType")

        ip.addRelation(layeredOver: devInterface);

        assertNotNull(ip.layeredOver);

        ip.removeRelation(layeredOver: devInterface);

        def layeredOverObject = ip.layeredOver
        assertNull(layeredOverObject);
        assertNull(Ip.get(name: "myIp", creationClassName: "Ip").layeredOver);

        def underlyingObject = devInterface.underlying;
        assertNull(underlyingObject);
        assertNull(DeviceInterface.get(name: "myDeviceInt", creationClassName: "DeviceInterface").underlying);

    }

    // Test removing the object from both sides of the relations 
    void testRemoveOneToOneRelationObject(){

    }
    
    void testUpdateOneToOneRelationObjectKeepsExistingRelations(){
    	
    }
    
    void testAddOneToManyRelation() {

        def link = Link.add(name: "myLink", creationClassName: "Link", smartsDs: "smartsDs")
        assertFalse(link.hasErrors())

        def devAdapter1 = DeviceAdapter.add(name: "myDeviceInt1", creationClassName: "DeviceInterface",
                smartsDs: "smartsDs", description: "desc", isManaged: "true", macAddress: "3245", type: "myType")
        assertFalse(devAdapter1.hasErrors());

        link.addRelation(connectedTo: devAdapter1);
        assertEquals(1, link.connectedTo.size());
        assertTrue(link.connectedTo.contains(devAdapter1))
        def linkInCompass = Link.get(name: "myLink", creationClassName: "Link")
        assertEquals(1, linkInCompass.connectedTo.size());

        def linkObject = devAdapter1.connectedVia;
        assertNotNull(linkObject);
        assertEquals("myLink", linkObject.name)
        assertNotNull(DeviceAdapter.get(name: "myDeviceInt1", creationClassName: "DeviceInterface").connectedVia)


        def devAdapter2 = DeviceAdapter.add(name: "myDeviceInt2", creationClassName: "DeviceInterface",
                smartsDs: "smartsDs", description: "desc", isManaged: "true", macAddress: "3245", type: "myType")
        assertFalse(devAdapter2.hasErrors());

        link.addRelation(connectedTo: devAdapter2);
        assertEquals(2, link.connectedTo.size());
        assertTrue(link.connectedTo.contains(devAdapter2))
        linkInCompass = Link.get(name: "myLink", creationClassName: "Link")
        assertEquals(2, linkInCompass.connectedTo.size());

        linkObject = devAdapter2.connectedVia;
        assertNotNull(linkObject);
        assertEquals("myLink", linkObject.name)
        assertNotNull(DeviceAdapter.get(name: "myDeviceInt2", creationClassName: "DeviceInterface").connectedVia);
        devAdapter2.remove();
        assertEquals(1, link.connectedTo.size());
    }

    void testAddOneToManyRelationIsIgnoredIfRelationAlreadyExists() {

        def link = Link.add(name: "myLink", creationClassName: "Link", smartsDs: "smartsDs")
        assertFalse(link.hasErrors())

        def devAdapter1 = DeviceAdapter.add(name: "myDeviceInt1", creationClassName: "DeviceInterface",
                smartsDs: "smartsDs", description: "desc", isManaged: "true", macAddress: "3245", type: "myType")
        assertFalse(devAdapter1.hasErrors());

        link.addRelation(connectedTo: devAdapter1);
        assertEquals(1, link.connectedTo.size());
        assertTrue(link.connectedTo.contains(devAdapter1))
        def linkInCompass = Link.get(name: "myLink", creationClassName: "Link")
        assertEquals(1, linkInCompass.connectedTo.size());

        try {
            link.addRelation(connectedTo: devAdapter1);
        } catch (e)
        {
            fail("Should not throw exception")
        }

        assertEquals(1, link.connectedTo.size());
        assertTrue(link.connectedTo.contains(devAdapter1))
        linkInCompass = Link.get(name: "myLink", creationClassName: "Link")
        assertEquals(1, linkInCompass.connectedTo.size())
    }

    void testAddOneToManyRelationBreaksAlreadyExistingRelationIfOneSideIsChanged() {
        def link1 = Link.add(name: "myLink1", creationClassName: "Link", smartsDs: "smartsDs")
        def link2 = Link.add(name: "myLink2", creationClassName: "Link", smartsDs: "smartsDs")

        def devAdapter1 = DeviceAdapter.add(name: "myDeviceInt1", creationClassName: "DeviceInterface",
                smartsDs: "smartsDs", description: "desc", isManaged: "true", macAddress: "3245", type: "myType")

        link1.addRelation(connectedTo: devAdapter1);
        assertEquals(1, link1.connectedTo.size());
        assertTrue(link1.connectedTo.contains(devAdapter1))
        def linkInCompass = Link.get(name: "myLink1", creationClassName: "Link")
        assertEquals(1, linkInCompass.connectedTo.size());

        def linkObject = devAdapter1.connectedVia;

        def devAdapter2 = DeviceAdapter.add(name: "myDeviceInt2", creationClassName: "DeviceInterface",
                smartsDs: "smartsDs", description: "desc", isManaged: "true", macAddress: "3245", type: "myType")

        link1.addRelation(connectedTo: devAdapter2);
        assertEquals(2, link1.connectedTo.size());
        assertTrue(link1.connectedTo.contains(devAdapter2))
        linkInCompass = Link.get(name: "myLink1", creationClassName: "Link")
        assertEquals(2, linkInCompass.connectedTo.size());


        devAdapter2.addRelation(connectedVia: link2)
        assertEquals(1, link1.connectedTo.size());
        assertEquals(1, Link.get(name: "myLink1", creationClassName: "Link").connectedTo.size());
        assertEquals(1, link2.connectedTo.size());
        assertEquals(1, Link.get(name: "myLink2", creationClassName: "Link").connectedTo.size());


        assertFalse(link1.connectedTo.contains(devAdapter2))
        assertTrue(link2.connectedTo.contains(devAdapter2))

    }

    void testAddOneToManyRelationWithInvalidRelationNameIsIgnored(){
    	
    }
    
    void testAddOneToManyRelationWithInvalidObjectType() {
        def link1 = Link.add(name: "myLink1", creationClassName: "Link", smartsDs: "smartsDs")

        def device = Device.add(name: "myDevice1", creationClassName: "Device", smartsDs: "smartsDs", ipAddress: "192.168.1.1",
                location: "myLocation", model: "myModel", snmpReadCommunity: "mysnmpReadCommunity", vendor: "myVendor")

        try {
            link1.addRelation(connectedTo: device);
        }
        catch (e) {
            fail("Should not throw exception");
        }

        assertFalse(link1.hasErrors());
        assertNull(link1.connectedTo)
    }
    
    void testRemoveOneToManyRelation() {

        def link = Link.add(name: "myLink", creationClassName: "Link", smartsDs: "smartsDs")

        def devAdapter1 = DeviceAdapter.add(name: "myDeviceInt1", creationClassName: "DeviceInterface",
                smartsDs: "smartsDs", description: "desc", isManaged: "true", macAddress: "3245", type: "myType")

        link.addRelation(connectedTo: devAdapter1);
        assertEquals(1, link.connectedTo.size());

        assertTrue(link.connectedTo.contains(devAdapter1))

        def linkObject = devAdapter1.connectedVia;
        assertNotNull(linkObject);
        assertEquals("myLink", linkObject.name)

        link.removeRelation(connectedTo: devAdapter1)

        assertEquals(0, link.connectedTo.size())
        assertEquals(0, Link.get(name: "myLink", creationClassName: "Link").connectedTo.size())

        assertNull(devAdapter1.connectedVia)
        assertNull(DeviceAdapter.get(name: "myDeviceInt1", creationClassName: "DeviceInterface").connectedVia)

    }

    //  Test removing the object from both sides of the relations
    void testRemoveOneToManyRelationObject() {

        def link1 = Link.add(name: "myLink1", creationClassName: "Link", smartsDs: "smartsDs")

        def devAdapter1 = DeviceAdapter.add(name: "myDeviceInt1", creationClassName: "DeviceInterface",
                smartsDs: "smartsDs", description: "desc", isManaged: "true", macAddress: "3245", type: "myType")

        link1.addRelation(connectedTo: devAdapter1);
        assertEquals(1, link1.connectedTo.size());
        assertTrue(link1.connectedTo.contains(devAdapter1))
        def linkInCompass = Link.get(name: "myLink1", creationClassName: "Link")
        assertEquals(1, linkInCompass.connectedTo.size());

        def linkObject = devAdapter1.connectedVia;

        def devAdapter2 = DeviceAdapter.add(name: "myDeviceInt2", creationClassName: "DeviceInterface",
                smartsDs: "smartsDs", description: "desc", isManaged: "true", macAddress: "3245", type: "myType")

        def link2 = Link.add(name: "myLink2", creationClassName: "Link", smartsDs: "smartsDs")
        link2.addRelation(connectedTo: devAdapter1);
        devAdapter2.addRelation(connectedVia: link2);
        assertEquals(2, link2.connectedTo.size());
        assertTrue(link2.connectedTo.contains(devAdapter1))
        assertTrue(link2.connectedTo.contains(devAdapter2))
        linkInCompass = Link.get(name: "myLink2", creationClassName: "Link")
        assertEquals(2, linkInCompass.connectedTo.size());

        link1.addRelation(connectedTo: devAdapter2);
        assertEquals(1, link1.connectedTo.size());
        assertTrue(link1.connectedTo.contains(devAdapter2))
        linkInCompass = Link.get(name: "myLink1", creationClassName: "Link")
        assertEquals(1, linkInCompass.connectedTo.size());

        // test removing from many side
        devAdapter2.remove();
        assertEquals(0, link1.connectedTo.size());
        assertEquals(0,Link.get(name: "myLink1", creationClassName: "Link").connectedTo.size())
        assertEquals(1, link2.connectedTo.size());
        assertTrue(link2.connectedTo.contains(devAdapter1))
        
//      test removing from 1 side
        link2.remove();
        assertNull(devAdapter1.connectedVia)
        assertNull(DeviceAdapter.get(name: "myDeviceInt1", creationClassName: "DeviceInterface").connectedVia)
    }

    void testUpdateOneToManyRelationObjectKeepsExistingRelations(){
    	
    }
    
    void testAddManyToManyRelation() {

        def device1 = Device.add(name: "myDevice1", creationClassName: "Device", smartsDs: "smartsDs", ipAddress: "192.168.1.1",
                location: "myLocation", model: "myModel", snmpReadCommunity: "mysnmpReadCommunity", vendor: "myVendor")

        def device2 = Device.add(name: "myDevice2", creationClassName: "Device", smartsDs: "smartsDs", ipAddress: "192.168.1.1",
                location: "myLocation", model: "myModel", snmpReadCommunity: "mysnmpReadCommunity", vendor: "myVendor")

        assertFalse(device1.hasErrors())
        assertFalse(device2.hasErrors())

        def link1 = Link.add(name: "myLink1", creationClassName: "Link", smartsDs: "smartsDs")
        assertFalse(link1.hasErrors())

        def link2 = Link.add(name: "myLink2", creationClassName: "Link", smartsDs: "smartsDs")
        assertFalse(link2.hasErrors())

        device1.addRelation(connectedVia: link1)
        device1.addRelation(connectedVia: link2)

        assertEquals(2, device1.connectedVia.size())
        assertEquals(2, Device.get(name: "myDevice1", creationClassName: "Device").connectedVia.size())
        assertTrue(device1.connectedVia.contains(link1))
        assertTrue(device1.connectedVia.contains(link2))

        assertEquals(1, link1.connectedSystems.size())
        assertEquals(1, Link.get(name: "myLink1", creationClassName: "Link").connectedSystems.size())
        assertTrue(link1.connectedSystems.contains(device1))

        assertEquals(1, link2.connectedSystems.size())
        assertEquals(1, Link.get(name: "myLink2", creationClassName: "Link").connectedSystems.size())
        assertTrue(link2.connectedSystems.contains(device1))

        device2.addRelation(connectedVia: link1)
        device2.addRelation(connectedVia: link2)

        assertEquals(2, device2.connectedVia.size())
        assertEquals(2, Device.get(name: "myDevice2", creationClassName: "Device").connectedVia.size())
        assertTrue(device2.connectedVia.contains(link1))
        assertTrue(device2.connectedVia.contains(link2))

        assertEquals(2, link1.connectedSystems.size())
        assertEquals(2, Link.get(name: "myLink1", creationClassName: "Link").connectedSystems.size())
        assertTrue(link1.connectedSystems.contains(device2))

        assertEquals(2, link2.connectedSystems.size())
        assertEquals(2, Link.get(name: "myLink2", creationClassName: "Link").connectedSystems.size())
        assertTrue(link2.connectedSystems.contains(device2))

        device1.remove()

        assertEquals(1, link2.connectedSystems.size())
        assertEquals(1, Link.get(name: "myLink2", creationClassName: "Link").connectedSystems.size())
        assertFalse(link2.connectedSystems.contains(device1))

        assertEquals(1, link1.connectedSystems.size())
        assertEquals(1, Link.get(name: "myLink1", creationClassName: "Link").connectedSystems.size())
        assertFalse(link1.connectedSystems.contains(device1))

    }
    
    void testAddManyToManyRelationIsIgnoredIfRelationAlreadyExists(){
    	
    }
    
    void testAddManyToManyRelationWithInvalidRelationNameIsIgnored(){
    	
    }
    
    void testAddManyToManyRelationWithInvalidObjectType() {

    }
    
    void testRemoveManyToManyRelation() {
        def device1 = Device.add(name: "myDevice1", creationClassName: "Device", smartsDs: "smartsDs", ipAddress: "192.168.1.1",
                location: "myLocation", model: "myModel", snmpReadCommunity: "mysnmpReadCommunity", vendor: "myVendor")

        def device2 = Device.add(name: "myDevice2", creationClassName: "Device", smartsDs: "smartsDs", ipAddress: "192.168.1.1",
                location: "myLocation", model: "myModel", snmpReadCommunity: "mysnmpReadCommunity", vendor: "myVendor")

        def link1 = Link.add(name: "myLink1", creationClassName: "Link", smartsDs: "smartsDs")
        assertFalse(link1.hasErrors())

        def link2 = Link.add(name: "myLink2", creationClassName: "Link", smartsDs: "smartsDs")
        assertFalse(link2.hasErrors())

        device1.addRelation(connectedVia: link1)
        device1.addRelation(connectedVia: link2)

        assertEquals(2, device1.connectedVia.size())
        assertEquals(2, Device.get(name: "myDevice1", creationClassName: "Device").connectedVia.size())

        assertEquals(1, link1.connectedSystems.size())
        assertEquals(1, Link.get(name: "myLink1", creationClassName: "Link").connectedSystems.size())

        device1.removeRelation(connectedVia: link1)

        assertEquals(1, device1.connectedVia.size())
        assertEquals(1, Device.get(name: "myDevice1", creationClassName: "Device").connectedVia.size())
        assertFalse(device1.connectedVia.contains(link1))
        assertEquals(0, link1.connectedSystems.size())
        assertEquals(0, Link.get(name: "myLink1", creationClassName: "Link").connectedSystems.size())

    }
    
    void testRemoveManyToManyRelationObjectDeletesManySideRelations() {

    }

    void testUpdateManyToManyRelationObjectKeepsExistingRelations() {

    }
}