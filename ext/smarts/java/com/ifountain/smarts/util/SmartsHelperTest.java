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
 * Created on Feb 20, 2008
 *
 * Author Sezgin
 */
package com.ifountain.smarts.util;

import com.ifountain.smarts.test.util.SmartsTestCase;

public class SmartsHelperTest extends SmartsTestCase {

    public void testConstructNotificationName() throws Exception {
        String className = "Router";
        String instanceName = "router1";
        String eventName = "Down";
        String notification = SmartsHelper.constructNotificationName(className, instanceName, eventName);
        assertEquals("NOTIFICATION-Router_router1_Down", notification);
        
        className = "Rou _ter";
        instanceName = "_router  1";
        eventName = "D o w n";
        notification = SmartsHelper.constructNotificationName(className, instanceName, eventName);
        assertEquals("NOTIFICATION-Rou_20__ter___router_20_201_D_20o_20w_20n", notification);
    }
}
