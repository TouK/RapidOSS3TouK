package com.ifountain.snmp.test.util;

import com.ifountain.snmp.util.RSnmpConstants;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
public class SnmpTestUtils {


    public static void sendV1Trap(String enterprise, long timestamp, int genericTrap, int specificTrap, List varbinds) throws Exception {
        sendV1Trap("127.0.0.1/162", "public", enterprise, timestamp, genericTrap, specificTrap, varbinds);
    }

    public static void sendV1Trap(String address, String community, String enterprise, long timestamp, int genericTrap, int specificTrap, List varbinds) throws Exception {
        PDU pdu = createVersion1TrapPdu(enterprise, timestamp, genericTrap, specificTrap);
        sendTrap(pdu, address, community, SnmpConstants.version1, varbinds);
    }

    public static void sendV2Trap(String address, String community, List varbinds) throws Exception {
        PDU pdu = createVersion2TrapPdu();
        sendTrap(pdu, address, community, SnmpConstants.version2c, varbinds);
    }
    public static void sendV2Trap(List varbinds) throws Exception {
        sendV2Trap("127.0.0.1/162", "public", varbinds);
    }

    private static PDUv1 createVersion1TrapPdu(String enterprise, long timestamp, int genericTrap, int specificTrap) {
        PDUv1 pdu = new PDUv1();
        pdu.setType(PDU.V1TRAP);
        pdu.setEnterprise(new OID(enterprise));
        pdu.setTimestamp(timestamp);
        pdu.setGenericTrap(genericTrap);
        pdu.setSpecificTrap(specificTrap);
        return pdu;
    }

    private static PDU createVersion2TrapPdu() {
        PDU pdu = new PDU();
        pdu.setType(PDU.TRAP);
        return pdu;
    }

    public static void sendTrap(PDU pdu, String address, String community, int snmpVersion, List varbinds) throws Exception {
        Address targetAddress = new UdpAddress(address);
        if (targetAddress == null || !targetAddress.isValid()) {
            throw new Exception("Invalid address: " + address);
        }
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(targetAddress);
        if (snmpVersion != SnmpConstants.version1 && snmpVersion != SnmpConstants.version2c) {
            throw new Exception("Invalid version: " + snmpVersion);
        }
        target.setVersion(snmpVersion);
        for (Iterator iterator = varbinds.iterator(); iterator.hasNext();) {
            Map varbind = (Map) iterator.next();
            String oid = (String) varbind.get(RSnmpConstants.OID);
            String value = (String) varbind.get(RSnmpConstants.VARBIND_VALUE);
            String type = (String) varbind.get(RSnmpConstants.VARBIND_TYPE);
            if (type == null) {
                type = "s";
            }
            VariableBinding vb = new VariableBinding(new OID(oid));
            if (value != null) {
                Variable variable;
                if (type.equals("i")) {
                    variable = new Integer32(Integer.parseInt(value));
                } else if (type.equals("u")) {
                    variable = new UnsignedInteger32(Long.parseLong(value));
                } else if (type.equals("s")) {
                    variable = new OctetString(value);
                } else if (type.equals("x")) {
                    variable = OctetString.fromString(value, ':', 16);
                } else if (type.equals("d")) {
                    variable = OctetString.fromString(value, '.', 10);
                } else if (type.equals("b")) {
                    variable = OctetString.fromString(value, ' ', 2);
                } else if (type.equals("n")) {
                    variable = new Null();
                } else if (type.equals("o")) {
                    variable = new OID(value);
                } else if (type.equals("t")) {
                    variable = new TimeTicks(Long.parseLong(value));
                } else if (type.equals("a")) {
                    variable = new IpAddress(value);
                } else {
                    throw new IllegalArgumentException("Variable type " + type +
                            " not supported");
                }
                vb.setVariable(variable);
            }
            pdu.add(vb);
        }
        try {
            DefaultUdpTransportMapping udpTransportMap = new DefaultUdpTransportMapping();
            Snmp snmp = new Snmp(udpTransportMap);
            ResponseEvent response = snmp.send(pdu, target);
            snmp.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
    }
}
