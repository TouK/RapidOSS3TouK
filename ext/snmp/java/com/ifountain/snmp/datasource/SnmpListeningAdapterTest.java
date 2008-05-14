package com.ifountain.snmp.datasource;

import com.ifountain.core.test.util.RapidCoreTestCase;
import com.ifountain.comp.test.util.CommonTestUtils;
import com.ifountain.comp.test.util.WaitAction;
import com.ifountain.comp.test.util.logging.TestLogUtils;
import com.ifountain.snmp.util.RSnmpConstants;
import com.ifountain.snmp.test.util.SnmpTestUtils;

import java.util.*;

import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.OID;
import org.snmp4j.mp.SnmpConstants;

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
        assertTrue(adapter.trapProcessorThread.isAlive());
    }

    public void testClose() throws Exception {
        adapter = new SnmpListeningAdapter("localhost", 162, TestLogUtils.log);
        adapter.open();
        assertTrue(adapter.isOpen());
        assertTrue(adapter.trapProcessorThread.isAlive());
        adapter.close();
        assertFalse(adapter.isOpen());
        assertFalse(adapter.trapProcessorThread.isAlive());
    }

    public void testAdapterSendsTrapsToAllSubscribers() throws Exception {
        adapter = new SnmpListeningAdapter("localhost", 162, TestLogUtils.log);
        final MockSnmpTrapProcessorImpl trapProcessor1 = new MockSnmpTrapProcessorImpl();
        final MockSnmpTrapProcessorImpl trapProcessor2 = new MockSnmpTrapProcessorImpl();
        final MockSnmpTrapProcessorImpl trapProcessor3 = new MockSnmpTrapProcessorImpl();
        adapter.addTrapProcessor(trapProcessor1);
        adapter.addTrapProcessor(trapProcessor2);
        adapter.addTrapProcessor(trapProcessor3);
        adapter.open();
        Map trap = new HashMap();
        trap.put("name", "myTrap");
        adapter.addTrapToBuffer(trap);
        CommonTestUtils.waitFor(new WaitAction() {
            public void check() throws Exception {
                assertEquals(1, trapProcessor1.traps.size());
                assertEquals(1, trapProcessor2.traps.size());
                assertEquals(1, trapProcessor3.traps.size());
                assertEquals("myTrap", ((Map) trapProcessor1.traps.get(0)).get("name"));
                assertEquals("myTrap", ((Map) trapProcessor2.traps.get(0)).get("name"));
                assertEquals("myTrap", ((Map) trapProcessor3.traps.get(0)).get("name"));
            }
        });
    }

    public void testAdapterWaitsIfBufferIsEmpty() throws Exception {
        adapter = new SnmpListeningAdapter("localhost", 162, TestLogUtils.log);
        final MockSnmpTrapProcessorImpl trapProcessor = new MockSnmpTrapProcessorImpl();
        adapter.addTrapProcessor(trapProcessor);
        adapter.open();
        adapter.addTrapToBuffer(new HashMap());
        CommonTestUtils.waitFor(new WaitAction() {
            public void check() throws Exception {
                assertEquals(1, trapProcessor.traps.size());
            }
        });
        Thread.sleep(300);
        adapter.addTrapToBuffer(new HashMap());
        CommonTestUtils.waitFor(new WaitAction() {
            public void check() throws Exception {
                assertEquals(2, trapProcessor.traps.size());
            }
        });
    }

    public void testVersion1Trap() throws Exception {
        adapter = new SnmpListeningAdapter("localhost", 162, TestLogUtils.log);
        final MockSnmpTrapProcessorImpl trapProcessor = new MockSnmpTrapProcessorImpl();
        adapter.addTrapProcessor(trapProcessor);
        adapter.open();
        final String enterpriseOid = "1.3.6.1.2.1.11";
        List varBinds = new ArrayList();
        varBinds.add(getVarbind("1.3.6.1.2.1.1.3.0", "0", "t"));
        varBinds.add(getVarbind("1.3.6.1.6.3.1.1.4.1.0", "1.3.6.1.6.3.1.1.5.2", "o"));
        varBinds.add(getVarbind("1.3.6.1.2.1.1.1.0", "System XYZ"));
        SnmpTestUtils.sendV1Trap(enterpriseOid, 1210767363, 1, 0, varBinds);
        CommonTestUtils.waitFor(new WaitAction() {
            public void check() throws Exception {
                assertEquals(1, trapProcessor.traps.size());
                Map trap = (Map) trapProcessor.traps.get(0);
                assertEquals("0.0.0.0", trap.get(RSnmpConstants.AGENT));
                assertEquals("1210767363", trap.get(RSnmpConstants.TIMESTAMP));
                assertEquals(enterpriseOid, trap.get(RSnmpConstants.ENTERPRISE));
                assertEquals("1", trap.get(RSnmpConstants.GENERIC_TYPE));
                assertEquals("0", trap.get(RSnmpConstants.SPECIFIC_TYPE));
                List vars = (List) trap.get(RSnmpConstants.VARBINDS);
                assertEquals(3, vars.size());
                Map varbind1 = (Map) vars.get(0);
                assertEquals("1.3.6.1.2.1.1.3.0", varbind1.get(RSnmpConstants.OID));
                assertEquals(new TimeTicks(0).toString(), varbind1.get(RSnmpConstants.VARBIND_VALUE));
                Map varbind2 = (Map) vars.get(1);
                assertEquals("1.3.6.1.6.3.1.1.4.1.0", varbind2.get(RSnmpConstants.OID));
                assertEquals(new OID("1.3.6.1.6.3.1.1.5.2").toString(), varbind2.get(RSnmpConstants.VARBIND_VALUE));
                Map varbind3 = (Map) vars.get(2);
                assertEquals("1.3.6.1.2.1.1.1.0", varbind3.get(RSnmpConstants.OID));
                assertEquals("System XYZ", varbind3.get(RSnmpConstants.VARBIND_VALUE));
            }
        });
    }

    public void testVersion2Trap() throws Exception {
        adapter = new SnmpListeningAdapter("localhost", 162, TestLogUtils.log);
        final MockSnmpTrapProcessorImpl trapProcessor = new MockSnmpTrapProcessorImpl();
        adapter.addTrapProcessor(trapProcessor);
        adapter.open();
        List varBinds = new ArrayList();
        varBinds.add(getVarbind(SnmpConstants.sysUpTime.toString(), "1210767363", "t"));
        varBinds.add(getVarbind(SnmpConstants.snmpTrapOID.toString(), "1.3.6.1.6.3.1.1.5.2", "o"));
        varBinds.add(getVarbind("1.3.6.1.2.1.1.1.0", "System XYZ"));
        SnmpTestUtils.sendV2Trap(varBinds);
        CommonTestUtils.waitFor(new WaitAction() {
            public void check() throws Exception {
                assertEquals(1, trapProcessor.traps.size());
                Map trap = (Map) trapProcessor.traps.get(0);
                assertEquals("127.0.0.1", trap.get(RSnmpConstants.AGENT));
                assertEquals("1210767363", trap.get(RSnmpConstants.TIMESTAMP));
                assertEquals("1.3.6.1.6.3.1.1.5.2", trap.get(RSnmpConstants.ENTERPRISE));
                assertEquals("1", trap.get(RSnmpConstants.GENERIC_TYPE));
                assertEquals("0", trap.get(RSnmpConstants.SPECIFIC_TYPE));
                List vars = (List)trap.get(RSnmpConstants.VARBINDS);
                assertEquals(3, vars.size());
                Map varbind1 = (Map) vars.get(0);
                assertEquals("1.3.6.1.2.1.1.3.0", varbind1.get(RSnmpConstants.OID));
                assertEquals(new TimeTicks(1210767363).toString(), varbind1.get(RSnmpConstants.VARBIND_VALUE));
                Map varbind2 = (Map) vars.get(1);
                assertEquals("1.3.6.1.6.3.1.1.4.1.0", varbind2.get(RSnmpConstants.OID));
                assertEquals(new OID("1.3.6.1.6.3.1.1.5.2").toString(), varbind2.get(RSnmpConstants.VARBIND_VALUE));
                Map varbind3 = (Map) vars.get(2);
                assertEquals("1.3.6.1.2.1.1.1.0", varbind3.get(RSnmpConstants.OID));
                assertEquals("System XYZ", varbind3.get(RSnmpConstants.VARBIND_VALUE));
            }
        });
    }

    private Map getVarbind(String oid, String value, String type) {
        Map varbind = new HashMap();
        varbind.put(RSnmpConstants.OID, oid);
        varbind.put(RSnmpConstants.VARBIND_VALUE, value);
        varbind.put(RSnmpConstants.VARBIND_TYPE, type);
        return varbind;
    }

    private Map getVarbind(String oid, String value) {
        return getVarbind(oid, value, "s");
    }

    class MockSnmpTrapProcessorImpl implements SnmpTrapProcessor {
        public List traps = new ArrayList();

        public void processTrap(Map trap) {
            traps.add(trap);
        }
    }

}
