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

    void testAddWithInheritance(){
        DeviceComponent.add(name:"deviceComp1", creationClassName:"DeviceComponent");
        assertEquals(1, DeviceComponent.list().size());

        Ip.add(name:"ip1", creationClassName:"Ip", ipAddress:"192.168.1.1");
        assertEquals(2, DeviceComponent.list().size());
        assertEquals(1, Ip.list().size());
        def ip = Ip.findByName("ip1");
        assertNotNull(ip);

        DeviceComponent.add(name:"deviceComp2", creationClassName:"DeviceComponent");
        assertEquals(3, DeviceComponent.list().size());
    }

    void testAddCard(){
        Card.add(name:"card1", creationClassName:"Card");
        assertEquals(1, Card.list().size());
    }

    
}