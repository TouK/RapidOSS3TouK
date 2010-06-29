package com.ifountain.snmp.datasource;

import com.ifountain.comp.test.util.CommonTestUtils;
import com.ifountain.comp.test.util.WaitAction;
import com.ifountain.comp.test.util.logging.TestLogUtils;
import com.ifountain.core.test.util.RapidCoreTestCase;
import com.ifountain.core.test.util.DatasourceTestUtils;
import com.ifountain.core.connection.ConnectionParam;
import com.ifountain.snmp.test.util.SnmpTestUtils;
import com.ifountain.snmp.util.RSnmpConstants;
import com.ifountain.snmp.connection.SnmpConnectionImpl;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.TimeTicks;

import java.util.*;

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
    public static final String SNMP_TEST_CONNECTION_NAME = "snmpConn";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        DatasourceTestUtils.getParamSupplier().setParam(getConnectionParam("127.0.0.1", new Long(162)));
        adapter = new SnmpListeningAdapter(SNMP_TEST_CONNECTION_NAME, TestLogUtils.log);
    }

    @Override
    protected void tearDown() throws Exception {
        if (adapter != null) {
            adapter.unsubscribe();
        }
        super.tearDown();
    }
    public void testOpenThrowsExceptionIfHostNameIsInvalid() throws Exception {
        DatasourceTestUtils.getParamSupplier().setParam(getConnectionParam("invalidHost", new Long(162)));
        try {
            adapter.subscribe();
        }
        catch (Exception e) {
            assertEquals("Invalid address invalidHost/162", e.getMessage());
        }
    }

    public void testOpenThrowsExceptionIfPortIsInvalid() throws Exception {
        DatasourceTestUtils.getParamSupplier().setParam(getConnectionParam("127.0.0.1", new Long(-1)));
        try {
            adapter.subscribe();
        }
        catch (Exception e) {
            assertEquals("Invalid address 127.0.0.1/-1", e.getMessage());
        }
    }

    public void testOpenThrowsExceptionCannotConnect() throws Exception {
        DatasourceTestUtils.getParamSupplier().setParam(getConnectionParam("192.168.1.190", new Long(162)));
        try {
            adapter.subscribe();
        }
        catch (Exception e) {
            assertTrue(e.getMessage().indexOf("Cannot assign requested address") > -1);
        }
    }

    public void testSuccessfulOpen() throws Exception {
        adapter.subscribe();
        assertTrue(adapter.isSubscribed());
        assertTrue(adapter.trapProcessorThread.isAlive());
    }

    public void testClose() throws Exception {
        adapter.subscribe();
        assertTrue(adapter.isSubscribed());
        assertTrue(adapter.trapProcessorThread.isAlive());
        adapter.unsubscribe();
        assertFalse(adapter.isSubscribed());
        assertFalse(adapter.trapProcessorThread.isAlive());
    }

    public void testCloseWithoutInterruptException() throws Exception {
        final MockSnmpObserverImplWithoutInterruptException trapProcessor1 = new MockSnmpObserverImplWithoutInterruptException();

        adapter.addObserver(trapProcessor1);
        adapter.subscribe();
        assertTrue(adapter.isSubscribed());
        assertTrue(adapter.trapProcessorThread.isAlive());

        Map trap = new HashMap();
        trap.put("name", "myTrap");
        adapter.addTrapToBuffer(trap);
        adapter.unsubscribe();
        assertFalse(adapter.isSubscribed());
        assertFalse(adapter.trapProcessorThread.isAlive());

        CommonTestUtils.waitFor(new WaitAction() {
            public void check() throws Exception {
                assertEquals(1, trapProcessor1.traps.size());
                assertEquals("myTrap", ((Map) trapProcessor1.traps.get(0)).get("name"));
            }
        });
        assertNotNull(trapProcessor1.lastException);
        assertTrue(trapProcessor1.lastException instanceof InterruptedException);

        //should do resubscribe
        final MockSnmpObserverImpl trapProcessor2 = new MockSnmpObserverImpl();

        adapter.addObserver(trapProcessor2);
        adapter.subscribe();
        assertTrue(adapter.isSubscribed());
        assertTrue(adapter.trapProcessorThread.isAlive());

        trap = new HashMap();
        trap.put("name", "myTrap2");
        adapter.addTrapToBuffer(trap);

        CommonTestUtils.waitFor(new WaitAction() {
            public void check() throws Exception {
                assertEquals(1, trapProcessor2.traps.size());
                assertEquals("myTrap2", ((Map) trapProcessor2.traps.get(0)).get("name"));
            }
        });

        adapter.unsubscribe();
        assertFalse(adapter.isSubscribed());
        assertFalse(adapter.trapProcessorThread.isAlive());

    }

    public void testAdapterSendsTrapsToAllSubscribers() throws Exception {
        final MockSnmpObserverImpl trapProcessor1 = new MockSnmpObserverImpl();
        final MockSnmpObserverImpl trapProcessor2 = new MockSnmpObserverImpl();
        final MockSnmpObserverImpl trapProcessor3 = new MockSnmpObserverImpl();
        adapter.addObserver(trapProcessor1);
        adapter.addObserver(trapProcessor2);
        adapter.addObserver(trapProcessor3);
        adapter.subscribe();
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
        final MockSnmpObserverImpl observer = new MockSnmpObserverImpl();
        adapter.addObserver(observer);
        adapter.subscribe();
        adapter.addTrapToBuffer(new HashMap());
        CommonTestUtils.waitFor(new WaitAction() {
            public void check() throws Exception {
                assertEquals(1, observer.traps.size());
            }
        });
        Thread.sleep(300);
        adapter.addTrapToBuffer(new HashMap());
        CommonTestUtils.waitFor(new WaitAction() {
            public void check() throws Exception {
                assertEquals(2, observer.traps.size());
            }
        });
    }

    public void testVersion1Trap() throws Exception {
//        final MockSnmpObserverImpl observer = new MockSnmpObserverImpl();
//        adapter.addObserver(observer);
//        adapter.subscribe();
        final String enterpriseOid = "1.3.6.1.2.1.11";
        List varBinds = new ArrayList();
        varBinds.add(getVarbind("1.3.6.1.2.1.1.3.0", "0", "t"));
        varBinds.add(getVarbind("1.3.6.1.6.3.1.1.4.1.0", "1.3.6.1.6.3.1.1.5.2", "o"));
        varBinds.add(getVarbind("1.3.6.1.2.1.1.1.0", "System XYZ"));
        long current = System.currentTimeMillis()/1000;
        for (int i = 0; i < 10000; i++) {
            SnmpTestUtils.sendV1Trap(enterpriseOid, current + i, 1, 0, varBinds);    
        }

//        CommonTestUtils.waitFor(new WaitAction() {
//            public void check() throws Exception {
//                assertEquals(1, observer.traps.size());
//                Map trap = (Map) observer.traps.get(0);
//                assertEquals("0.0.0.0", trap.get(RSnmpConstants.AGENT));
//                assertEquals("1210767363", trap.get(RSnmpConstants.TIMESTAMP));
//                assertEquals(enterpriseOid, trap.get(RSnmpConstants.ENTERPRISE));
//                assertEquals("1", trap.get(RSnmpConstants.GENERIC_TYPE));
//                assertEquals("0", trap.get(RSnmpConstants.SPECIFIC_TYPE));
//                List vars = (List) trap.get(RSnmpConstants.VARBINDS);
//                assertEquals(3, vars.size());
//                Map varbind1 = (Map) vars.get(0);
//                assertEquals("1.3.6.1.2.1.1.3.0", varbind1.get(RSnmpConstants.OID));
//                assertEquals(new TimeTicks(0).toString(), varbind1.get(RSnmpConstants.VARBIND_VALUE));
//                Map varbind2 = (Map) vars.get(1);
//                assertEquals("1.3.6.1.6.3.1.1.4.1.0", varbind2.get(RSnmpConstants.OID));
//                assertEquals(new OID("1.3.6.1.6.3.1.1.5.2").toString(), varbind2.get(RSnmpConstants.VARBIND_VALUE));
//                Map varbind3 = (Map) vars.get(2);
//                assertEquals("1.3.6.1.2.1.1.1.0", varbind3.get(RSnmpConstants.OID));
//                assertEquals("System XYZ", varbind3.get(RSnmpConstants.VARBIND_VALUE));
//            }
//        });
    }

    public void testVersion2Trap() throws Exception {
        final MockSnmpObserverImpl observer = new MockSnmpObserverImpl();
        adapter.addObserver(observer);
        adapter.subscribe();
        List varBinds = new ArrayList();
        varBinds.add(getVarbind(SnmpConstants.sysUpTime.toString(), "1210767363", "t"));
        varBinds.add(getVarbind(SnmpConstants.snmpTrapOID.toString(), "1.3.6.1.6.3.1.1.5.2", "o"));
        varBinds.add(getVarbind("1.3.6.1.2.1.1.1.0", "System XYZ"));
        SnmpTestUtils.sendV2Trap(varBinds);
        CommonTestUtils.waitFor(new WaitAction() {
            public void check() throws Exception {
                assertEquals(1, observer.traps.size());
                Map trap = (Map) observer.traps.get(0);
                assertEquals("127.0.0.1", trap.get(RSnmpConstants.AGENT));
                assertEquals("1210767363", trap.get(RSnmpConstants.TIMESTAMP));
                assertEquals("1.3.6.1.6.3.1.1.5.2", trap.get(RSnmpConstants.ENTERPRISE));
                assertEquals("1", trap.get(RSnmpConstants.GENERIC_TYPE));
                assertEquals("0", trap.get(RSnmpConstants.SPECIFIC_TYPE));
                List vars = (List) trap.get(RSnmpConstants.VARBINDS);
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

    public static ConnectionParam getConnectionParam(String host, Long port){
        Map<String, Object> otherParams = new HashMap<String, Object>();
        otherParams.put(SnmpConnectionImpl.HOST, host);
        otherParams.put(SnmpConnectionImpl.PORT, port);
        return new ConnectionParam(SNMP_TEST_CONNECTION_NAME, SnmpConnectionImpl.class.getName(), otherParams, 10, 1000, 6000);
    }

    class MockSnmpObserverImpl implements Observer {
        public List traps = new ArrayList();

        public void update(Observable o, Object trap) {
            traps.add(trap);
        }
    }

    class MockSnmpObserverImplWithoutInterruptException implements Observer {
        public List traps = new ArrayList();
        public Exception lastException=null;

        public void update(Observable o, Object trap) {
            try{
              Thread.sleep(2000);
            }
            catch(InterruptedException e)
            {
                this.lastException=e;
                System.out.println(e);
            }
            traps.add(trap);
        }
    }

}
