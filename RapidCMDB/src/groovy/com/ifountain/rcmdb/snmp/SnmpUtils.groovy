package com.ifountain.rcmdb.snmp;

import org.snmp4j.smi.*;
import org.snmp4j.transport.AbstractTransportMapping;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.*;
import org.snmp4j.util.TableUtils;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;

import java.io.IOException;
import java.util.*;
import java.math.BigInteger
import com.ifountain.snmp.util.RSnmpConstants;
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
 * User: Administrator
 * Date: May 15, 2008
 * Time: 9:15:46 AM
 */
public class SnmpUtils {

    public static final String DEFAULT_COMMUNITY = "public";
    public static final int DEFAULT_TIMEOUT = 1000;
    public static final int DEFAULT_RETRIES = 1;
    public static final int DEFAULT_MAX_REPETITIONS = 10;
    public static final int DEFAULT_NON_REPEATERS = 0;
    public static final int DEFAULT_MAX_SIZE_RESPONSE_PDU = 65535;
    public static final OID DEFULT_SNMP_TRAP_OID = SnmpConstants.coldStart;
    public static final int VERSION_1 = SnmpConstants.version1;
    public static final int VERSION_2c = SnmpConstants.version2c;


    public static String get(String transportAddress, String oid, String community, int version) throws IOException {
        return get(transportAddress, oid, community, version, DEFAULT_TIMEOUT, DEFAULT_RETRIES, DEFAULT_MAX_SIZE_RESPONSE_PDU);
    }

    public static String get(String transportAddress, String oid, String community) throws IOException {
        return get(transportAddress, oid, community, VERSION_1);
    }

    public static String get(String transportAddress, String oid) throws IOException {
        return get(transportAddress, oid, DEFAULT_COMMUNITY);
    }

    public static String get(String transportAddress, String oid, String community, int version, int timeout, int retries, int maxSizeResponsePDU) throws IOException {
        return getAction(PDU.GET, transportAddress, oid, community, version, timeout, retries, maxSizeResponsePDU);
    }

    public static String getNext(String transportAddress, String oid, String community, int version, int timeout, int retries, int maxSizeResponsePDU) throws IOException {
        return getAction(PDU.GETNEXT, transportAddress, oid, community, version, timeout, retries, maxSizeResponsePDU);
    }
    public static String getNext(String transportAddress, String oid, String community, int version) throws IOException {
        return getNext(transportAddress, oid, community, version, DEFAULT_TIMEOUT, DEFAULT_RETRIES, DEFAULT_MAX_SIZE_RESPONSE_PDU);
    }

    public static String getNext(String transportAddress, String oid, String community) throws IOException {
        return getNext(transportAddress, oid, community, VERSION_1);
    }

    public static String getNext(String transportAddress, String oid) throws IOException {
        return getNext(transportAddress, oid, DEFAULT_COMMUNITY);
    }

    public static PDU sendV1Trap(String transportAddress, String community, String enterprise, long timestamp,
                                 int genericTrap, int specificTrap, List varbinds) throws IOException {
        return sendV1Trap(transportAddress, community, enterprise, timestamp, genericTrap, specificTrap, varbinds, DEFAULT_TIMEOUT, DEFAULT_RETRIES, DEFAULT_MAX_SIZE_RESPONSE_PDU);
    }

    public static PDU sendV1Trap(String transportAddress, String enterprise, long timestamp, int genericTrap, int specificTrap, List varbinds) throws IOException {
        return sendV1Trap(transportAddress, DEFAULT_COMMUNITY, enterprise, timestamp, genericTrap, specificTrap, varbinds);
    }

    public static PDU sendV2cTrap(String transportAddress, String community, long timestamp, String snmpTrapOid, List varbinds) throws IOException {
        sendV2cTrap(transportAddress, community, timestamp, snmpTrapOid, varbinds, DEFAULT_TIMEOUT, DEFAULT_RETRIES, DEFAULT_MAX_SIZE_RESPONSE_PDU);
    }

    public static PDU sendV2cTrap(String transportAddress, long timestamp, String snmpTrapOid, List varbinds) throws IOException {
        sendV2cTrap(transportAddress, DEFAULT_COMMUNITY, timestamp, snmpTrapOid, varbinds);
    }
    public static PDU sendV2cTrap(String transportAddress, List varbinds) throws IOException {
        sendV2cTrap(transportAddress, 0, null, varbinds);
    }

    public static PDU set(String transportAddress, String oid, String value, String type, String community, int version) throws IOException {
        return set(transportAddress, oid, value, type, community, version, DEFAULT_TIMEOUT, DEFAULT_RETRIES, DEFAULT_MAX_SIZE_RESPONSE_PDU);
    }
    public static PDU set(String transportAddress, String oid, String value, String type, String community) throws IOException {
        return set(transportAddress, oid, value, type, community, VERSION_1);
    }
    public static PDU set(String transportAddress, String oid, String value, String type) throws IOException {
        return set(transportAddress, oid, value, type, DEFAULT_COMMUNITY);
    }

    public static List getSubtree(String transportAddress, String oid, String community, int version, String lowerBoundOid, String upperBoundOid) throws Exception {
        return getSubtree(transportAddress, oid, community, version, lowerBoundOid, upperBoundOid, DEFAULT_MAX_REPETITIONS, DEFAULT_TIMEOUT, DEFAULT_RETRIES, DEFAULT_MAX_SIZE_RESPONSE_PDU);
    }

    public static List getSubtree(String transportAddress, String oid, String community, int version) throws Exception {
        return getSubtree(transportAddress, oid, community, version, null, null);
    }

    public static List getSubtree(String transportAddress, String oid, String community) throws Exception {
        return getSubtree(transportAddress, oid, community, VERSION_1);
    }

    public static List getSubtree(String transportAddress, String oid) throws Exception {
        return getSubtree(transportAddress, oid, DEFAULT_COMMUNITY);
    }

    public static List getSubtree(String transportAddress, String oid, String community, int version, String lowerBoundOid,
                                  String upperBoundOid, int maxRepetitions, int timeout, int retries, int maxSizeResponsePDU) throws Exception {
        Address address = getAddress(transportAddress);
        Snmp snmp = createSnmpSession(address);
        Target target = createTarget(version, community, address, retries, timeout, maxSizeResponsePDU);
        snmp.listen();
        OID o = new OID(oid);
        String tableString = o.toString();
        OID lowerBoundIndex = null;
        OID upperBoundIndex = null;
        if (lowerBoundOid != null) {
            lowerBoundIndex = new OID(lowerBoundOid);
        }
        if (upperBoundOid != null) {
            upperBoundIndex = new OID(upperBoundOid);
        }
        TableUtils tableUtils = new TableUtils(snmp, new DefaultPDUFactory());
        tableUtils.setMaxNumRowsPerPDU(maxRepetitions);
        OID[] columns = [o] as OID[];
        LinkedList rows = (LinkedList) tableUtils.getTable(target, columns, lowerBoundIndex, upperBoundIndex);
        try {
            snmp.close();
        } catch (IOException e) {
        }
        List results = new ArrayList();
        while (!rows.isEmpty()) {
            TableEvent event = (TableEvent) rows.removeFirst();
            if (event.getStatus() != -1) {
                for (int i = 0; i < event.getColumns().length; i++) {
                    VariableBinding vb = event.getColumns()[i];
                    Map vbMap = new HashMap();
                    String vbOid = vb.getOid().toString();
                    vbMap.put(RSnmpConstants.OID, vbOid);
                    vbMap.put(RSnmpConstants.VARBIND_VALUE, vb.getVariable().toString());
                    vbMap.put("Remainder", new OID(vbOid.substring(tableString.length() -1)).toString());
                    results.add(vbMap);
                }
            } else {
                throw new Exception("Snmp request for the table defined with " + o.toString() + " received timeout");
            }
        }
        return results;
    }
    public static PDU set(String transportAddress, String oid, String value, String type, String community, int version, int timeout, int retries, int maxSizeResponsePDU) throws IOException {
        Address address = getAddress(transportAddress);
        Snmp snmp = createSnmpSession(address);
        Target target = createTarget(version, community, address, retries, timeout, maxSizeResponsePDU);
        snmp.listen();
        PDU request = new PDU();
        request.setType(PDU.SET);
        addVariableBinding(request, oid, value, type);
        ResponseEvent responseEvent = snmp.send(request, target);
        try {
            snmp.close();
        } catch (Exception e) {
        }
        if (responseEvent != null) {
            return responseEvent.getResponse();
        }
        return null;
    }

    public static PDU sendV1Trap(String transportAddress, String community, String enterprise, long timestamp,
                                 int genericTrap, int specificTrap, List varbinds, int timeout, int retries, int maxSizeResponsePDU) throws IOException {
        Address address = getAddress(transportAddress);
        Snmp snmp = createSnmpSession(address);
        Target target = createTarget(VERSION_1, community, address, retries, timeout, maxSizeResponsePDU);
        snmp.listen();
        PDU trap = createVersion1TrapPdu(enterprise, timestamp, genericTrap, specificTrap);
        addVariableBindings(trap, varbinds);
        ResponseEvent responseEvent = snmp.send(trap, target);
        try {
            snmp.close();
        } catch (Exception e) {
        }
        if (responseEvent != null) {
            return responseEvent.getResponse();
        }
        return null;
    }

    public static PDU sendV2cTrap(String transportAddress, String community, long timestamp, String snmpTrapOid, List varbinds, int timeout, int retries, int maxSizeResponsePDU) throws IOException {
        Address address = getAddress(transportAddress);
        Snmp snmp = createSnmpSession(address);
        Target target = createTarget(VERSION_2c, community, address, retries, timeout, maxSizeResponsePDU);
        snmp.listen();
        PDU trap = new PDU();
        trap.setType(PDU.TRAP);
        if(snmpTrapOid == null){
            snmpTrapOid = DEFULT_SNMP_TRAP_OID.toString();
        }
        if ((varbinds.size() == 0) || ((varbinds.size() > 0) && (!new OID(varbinds[0].get(RSnmpConstants.OID)).equals(SnmpConstants.sysUpTime)))) {
            Map varbind = new HashMap();
            varbind.put(RSnmpConstants.OID, SnmpConstants.sysUpTime.toString());
            varbind.put(RSnmpConstants.VARBIND_VALUE, String.valueOf(timestamp));
            varbind.put(RSnmpConstants.VARBIND_TYPE, "t");
            varbinds.add(0, varbind);
        }
        if ((varbinds.size() == 1) || ((varbinds.size() > 1) && (!new OID(varbinds[1].get(RSnmpConstants.OID)).equals(SnmpConstants.sysUpTime)))) {
            Map varbind = new HashMap();
            varbind.put(RSnmpConstants.OID, SnmpConstants.snmpTrapOID.toString());
            varbind.put(RSnmpConstants.VARBIND_VALUE, snmpTrapOid);
            varbind.put(RSnmpConstants.VARBIND_TYPE, "o");
            varbinds.add(1, varbind);
        }
        addVariableBindings(trap, varbinds);
        ResponseEvent responseEvent = snmp.send(trap, target);
        try {
            snmp.close();
        } catch (Exception e) {
        }
        if (responseEvent != null) {
            return responseEvent.getResponse();
        }
        return null;
    }
    private static String getAction(int operation, String transportAddress, String oid, String community, int version, int timeout, int retries, int maxSizeResponsePDU) throws IOException {
        Address address = getAddress(transportAddress);
        Snmp snmp = createSnmpSession(address);
        Target target = createTarget(version, community, address, retries, timeout, maxSizeResponsePDU);
        snmp.listen();
        OID o = new OID(oid);
        PDU request = new PDU();
        request.setType(operation);
        request.add(new VariableBinding(o));
        ResponseEvent responseEvent = snmp.send(request, target);
        try {
            snmp.close();
        } catch (Exception e) {
        }
        PDU response = null;
        if (responseEvent != null && (response = responseEvent.getResponse()) != null) {
            VariableBinding result = response.get(0);
            if (result != null) {
                return result.getVariable().toString();
            }
        }
        return null;
    }

    private static Address getAddress(String transportAddress) {
        String transport = "udp";
        int colon = transportAddress.indexOf(':');
        if (colon > 0) {
            transport = transportAddress.substring(0, colon);
            transportAddress = transportAddress.substring(colon + 1);
        }
        if (transportAddress.indexOf('/') < 0) {
            transportAddress += "/161";
        }
        if (transport.equalsIgnoreCase("udp")) {
            return new UdpAddress(transportAddress);
        } else if (transport.equalsIgnoreCase("tcp")) {
            return new TcpAddress(transportAddress);
        }
        throw new IllegalArgumentException("Unknown transport " + transport);
    }

    private static Snmp createSnmpSession(Address address) throws IOException {
        AbstractTransportMapping transport;
        if (address instanceof TcpAddress) {
            transport = new DefaultTcpTransportMapping();
        } else {
            transport = new DefaultUdpTransportMapping();
        }
        Snmp snmp = new Snmp(transport);
        return snmp;
    }

    private static Target createTarget(int version, String community, Address address, int retries, int timeout, int maxSizeResponsePDU) {
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setVersion(version);
        target.setAddress(address);
        target.setRetries(retries);
        target.setTimeout(timeout);
        target.setMaxSizeRequestPDU(maxSizeResponsePDU);
        return target;
    }

    private static List getResults(PDU response) {
        List results = new ArrayList();
        for (int i = 0; i < response.size(); i++) {
            VariableBinding vb = response.get(i);
            String value = vb.getVariable().toString();
            String oid = vb.getOid().toString();
            Map vbMap = new HashMap();
            vbMap.put(RSnmpConstants.OID, oid);
            vbMap.put(RSnmpConstants.VARBIND_VALUE, value);
        }
        return results;
    }

    private static void addVariableBindings(PDU pdu, List variableBindings) {
        for (Iterator iterator = variableBindings.iterator(); iterator.hasNext();) {
            Map varbind = (Map) iterator.next();
            addVariableBinding(pdu, varbind);
        }
    }

    private static void addVariableBinding(PDU pdu, Map variableBinding) {
        String oid = (String) variableBinding.get(RSnmpConstants.OID);
        String value = (String) variableBinding.get(RSnmpConstants.VARBIND_VALUE);
        String type = (String) variableBinding.get(RSnmpConstants.VARBIND_TYPE);
        if (oid != null) {
            if (type == null) {
                type = "s";
            }
            addVariableBinding(pdu, oid, value, type);
        } else {
            throw new IllegalArgumentException("No OID specified for variable binding");
        }
    }

    private static void addVariableBinding(PDU pdu, String oid, String value, String type) {
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
            } else if (type.equals("c")) {
                variable = new Counter32(Long.parseLong(value));
            } else if (type.equals("C")) {
                variable = new Counter64(new BigInteger(value).longValue());
            } else {
                throw new IllegalArgumentException("Variable type " + type +
                        " not supported");
            }
            vb.setVariable(variable);
        }
        pdu.add(vb);
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
}
