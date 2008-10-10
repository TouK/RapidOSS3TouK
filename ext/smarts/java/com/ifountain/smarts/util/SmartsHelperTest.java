/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
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
