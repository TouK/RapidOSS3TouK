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
class ModelTests extends RapidCmdbIntegrationTestCase{
    static transactional = false;
    void setUp() {
        super.setUp();
        SmartsObject.list()*.remove();
        Person.list()*.remove();

    }

    void testAddWithInheritance(){
        DeviceComponent.add(name:"deviceComp1", creationClassName:"DeviceComponent");
        assertEquals(1, DeviceComponent.list().size());

        Ip.add(name:"ip1", creationClassName:"Ip", ipAddress:"192.168.1.1");
        assertEquals(2, DeviceComponent.list().size());
        assertEquals(1, Ip.list().size());
        def ip = Ip.get(name:"ip1", creationClassName:"Ip");
        assertNotNull(ip);

        DeviceComponent.add(name:"deviceComp2", creationClassName:"DeviceComponent");
        assertEquals(3, DeviceComponent.list().size());
    }

    void testAddCard(){
        Card.add(name:"card1", creationClassName:"Card");
        assertEquals(1, Card.list().size());
    }

//    void testOneToManyRelationToSelf(){
//        def emp1= Employee.add(name:"ayse",bday:"1/1/11",dept:"QA");
//        def emp2= Employee.add(name:"ali",bday:"2/2/22",dept:"QA");
//        def dev1= Developer.add(name:"gonca",bday:"4/4/44",dept:"Dev",language:"java");
//
//        emp1.addRelation(employees:emp2);
//        assertEquals(1, emp1.employees.size());
//        assertEquals(emp2.name, emp1.employees.toArray()[0].name)
//        assertNotNull(emp2.manager);
//        assertEquals(emp1.name, emp2.manager.name)
//
//        emp1.addRelation(manager:dev1);
//        assertNotNull(emp1.manager);
//        assertEquals(dev1.name, emp1.manager.name)
//        assertEquals(1, dev1.employees.size());
//        assertEquals(emp1.name, dev1.employees.toArray()[0].name)
//
//        emp1.remove();
//        assertEquals(0, dev1.employees.size())
//        assertNull(emp2.manager);
//
//    }
    
}