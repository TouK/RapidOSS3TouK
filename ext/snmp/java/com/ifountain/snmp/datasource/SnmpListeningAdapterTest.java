package com.ifountain.snmp.datasource;

import com.ifountain.core.test.util.RapidCoreTestCase;
import com.ifountain.comp.test.util.CommonTestUtils;
import com.ifountain.comp.test.util.logging.TestLogUtils;

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
 */
public class SnmpListeningAdapterTest extends RapidCoreTestCase {
    SnmpListeningAdapter adapter;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        if (adapter != null && adapter.isOpen()) {
            adapter.close();
        }
        super.tearDown();
    }

    public void testOpenThrowsExceptionIfHostNameIsInvalid() throws Exception {
        adapter = new SnmpListeningAdapter("invalidHost", 162, TestLogUtils.log);
        try {
            adapter.open();
        }
        catch (Exception e) {
            assertEquals("Invalid address invalidHost/162", e.getMessage());
        }
    }

    public void testOpenThrowsExceptionIfPortIsInvalid() throws Exception {
        adapter = new SnmpListeningAdapter("127.0.0.1", -1, TestLogUtils.log);
        try {
            adapter.open();
        }
        catch (Exception e) {
            assertEquals("Invalid address 127.0.0.1/-1", e.getMessage());
        }
    }

    public void testOpenThrowsExceptionCannotConnect() throws Exception {
        adapter = new SnmpListeningAdapter("192.168.1.190", 162, TestLogUtils.log);
        try {
            adapter.open();
        }
        catch (Exception e) {
            assertTrue(e.getMessage().indexOf("Cannot assign requested address") > -1);
        }
    }

    public void testSuccessfulOpen() throws Exception {
        adapter = new SnmpListeningAdapter("localhost", 162, TestLogUtils.log);
        adapter.open();
        assertTrue(adapter.isOpen());
    }

     public void testClose() throws Exception {
        adapter = new SnmpListeningAdapter("localhost", 162, TestLogUtils.log);
        adapter.open();
        assertTrue(adapter.isOpen());
        adapter.close();
        assertFalse(adapter.isOpen());
    }

    
}
