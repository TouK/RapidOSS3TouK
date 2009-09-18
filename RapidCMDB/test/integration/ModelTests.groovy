import com.ifountain.rcmdb.test.util.RapidCmdbIntegrationTestCase

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
 * User: Sezgin Kucukkaraaslan
 * Date: Apr 11, 2008
 * Time: 9:58:10 AM
 * To change this template use File | Settings | File Templates.
 */
class ModelTests extends RapidCmdbIntegrationTestCase {
    static transactional = false;

    void setUp() {
        super.setUp();
        Person.list()*.remove();
    }

    public void testAdd() {
        Developer addedDeveloper = Developer.add(name: "ibrahim", bday: "01/01/1982");
        assertFalse("No errors should occur while adding developer. However, ${addedDeveloper.errors}", addedDeveloper.hasErrors());

        Developer returnedDeveloper = Developer.get(name: "ibrahim");
        assertEquals(addedDeveloper.name, returnedDeveloper.name);
        assertEquals(addedDeveloper.bday, returnedDeveloper.bday);
        assertEquals(addedDeveloper.id, returnedDeveloper.id);
    }

    public void testAddReturnsErrorIfKeyAttributeNotSpecified() {
        Developer addedDeveloper = Developer.add(bday: "01/01/1982");
        assertTrue("Should return errors because key attribute not specified.", addedDeveloper.hasErrors());

        Developer returnedDeveloper = Developer.get(name: "ibrahim");
        assertNull(returnedDeveloper);
    }

    public void testAddWithNullAttribute() {
        Developer addedDeveloper = Developer.add(name: "nurullah", bday: null);
        assertFalse(addedDeveloper.hasErrors());

        Developer returnedDeveloper = Developer.get(name: "nurullah");
        assertEquals("", returnedDeveloper.bday);
    }

    public void testUpdate() {
        Developer dev = Developer.add(name: "nurullah", bday: "01/01/1982");
        dev.update(bday: "01/01/1986");

        assertFalse(dev.hasErrors());
        assertEquals("01/01/1986", dev.bday);

        Developer returnedDev = Developer.get(name: "nurullah");
        assertEquals(dev.bday, returnedDev.bday);

        dev.update(bday: "");

        assertFalse(dev.hasErrors());
        assertEquals("", dev.bday);
    }

    public void testUpdateReturnsErrorIfKeyUpdatedAndThereAreOtherExistingInstancesWithThisKey() {
        Developer dev1 = Developer.add(name: "nurullah", bday: "01/01/1982");
        Developer dev2 = Developer.add(name: "ibrahim", bday: "01/01/1982");
        dev1.update(name: "ibrahim");

        assertTrue(dev1.hasErrors());
    }

    public void testUpdateKeys() {
        Developer dev1 = Developer.add(name: "nurullah", bday: "01/01/1982");
        dev1.update(name: "ibrahim");

        assertFalse(dev1.hasErrors());

        Developer returnedDev = Developer.get(name: "nurullah");
        assertNull (returnedDev);

        returnedDev = Developer.get(name: "ibrahim");
        assertEquals("01/01/1982", returnedDev.bday);
    }

    public void testUpdateWithNullAttribute() {
        Developer dev = Developer.add(name: "nurullah", bday: "01/01/1982");
        dev.update(bday: null);

        assertFalse(dev.hasErrors());

        Developer returnedDev = Developer.get(name: "nurullah");
        assertEquals("", returnedDev.bday);
    }

    public void testList() {
        Developer dev1 = Developer.add(name: "ibrahim")
        Developer dev2 = Developer.add(name: "nurullah")
        List devList = Developer.list()

        assertEquals([dev1, dev2].toString(), devList.toString())
    }

    public void testRemove() {
        Developer dev1 = Developer.add(name:"ibrahim")
        dev1.remove()
        Developer developerInDB = Developer.get(name:"ibrahim")

        assertNull(developerInDB)
    }

    public void testRemoveTwiceReturnsError() {
        Developer dev1 = Developer.add(name:"ibrahim")
        dev1.remove()
        dev1.remove()

        assertTrue(dev1.hasErrors())
    }

    public void testRemovePurgesPropertyValues() {
        Developer dev1 = Developer.add(name:"ibrahim", bday:"01/01/1986")
        dev1.remove()

        Developer dev2 = Developer.add(name:"ibrahim")
        assertEquals("", dev2.bday)
    }


    public void testAsMap() {
        Developer dev1 = Developer.add(name:"ibrahim", bday:"01/01/1986")
        Map returnedMap = dev1.asMap()

        assertEquals (8, returnedMap.size());
        assertEquals("ibrahim", returnedMap.name)
        assertEquals("01/01/1986", returnedMap.bday)
        assertEquals("", returnedMap.language)
        assertEquals(dev1.id, returnedMap.id)

    }

    public void testAsMapWithProperties() {
        Developer dev1 = Developer.add(name:"ibrahim", bday:"01/01/1986", language:"tr")
        Map returnedMap = dev1.asMap(["name", "bday"])

        assertEquals (2, returnedMap.size());
        assertEquals("ibrahim", returnedMap.name)
        assertEquals("01/01/1986", returnedMap.bday)
    }
}