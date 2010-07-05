package snmp

import org.snmp4j.PDU
import org.snmp4j.smi.Address
import org.snmp4j.Snmp
import org.snmp4j.Target
import org.snmp4j.smi.OID
import org.snmp4j.util.TableUtils
import org.snmp4j.util.DefaultPDUFactory
import org.snmp4j.util.TableEvent
import org.snmp4j.smi.VariableBinding
import com.ifountain.snmp.util.RSnmpConstants
import org.snmp4j.event.ResponseEvent
import org.snmp4j.mp.SnmpConstants
import org.snmp4j.smi.UdpAddress
import org.snmp4j.smi.TcpAddress
import org.snmp4j.transport.AbstractTransportMapping
import org.snmp4j.transport.DefaultTcpTransportMapping
import org.snmp4j.transport.DefaultUdpTransportMapping
import org.snmp4j.CommunityTarget
import org.snmp4j.smi.OctetString
import org.snmp4j.smi.Variable
import org.snmp4j.smi.Integer32
import org.snmp4j.smi.UnsignedInteger32
import org.snmp4j.smi.Null
import org.snmp4j.smi.TimeTicks
import org.snmp4j.smi.IpAddress
import org.snmp4j.smi.Counter32
import org.snmp4j.smi.Counter64
import org.snmp4j.PDUv1
import org.snmp4j.TransportMapping

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Apr 1, 2009
* Time: 3:28:33 PM
*/
class SnmpUtilsOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation{

    
   public static String get(String transportAddress, String oid, String community, int version) throws IOException {
        return get(transportAddress, oid, community, version, SnmpUtils.DEFAULT_TIMEOUT, SnmpUtils.DEFAULT_RETRIES, SnmpUtils.DEFAULT_MAX_SIZE_RESPONSE_PDU);
    }

    public static String get(String transportAddress, String oid, String community) throws IOException {
        return get(transportAddress, oid, community, SnmpUtils.VERSION_1);
    }

    public static String get(String transportAddress, String oid) throws IOException {
        return get(transportAddress, oid, SnmpUtils.DEFAULT_COMMUNITY);
    }

    public static String get(String transportAddress, String oid, String community, int version, int timeout, int retries, int maxSizeResponsePDU) throws IOException {
        return getAction(PDU.GET, transportAddress, oid, community, version, timeout, retries, maxSizeResponsePDU);
    }

    public static String getNext(String transportAddress, String oid, String community, int version, int timeout, int retries, int maxSizeResponsePDU) throws IOException {
        return getAction(PDU.GETNEXT, transportAddress, oid, community, version, timeout, retries, maxSizeResponsePDU);
    }
    public static String getNext(String transportAddress, String oid, String community, int version) throws IOException {
        return getNext(transportAddress, oid, community, version, SnmpUtils.DEFAULT_TIMEOUT, SnmpUtils.DEFAULT_RETRIES, SnmpUtils.DEFAULT_MAX_SIZE_RESPONSE_PDU);
    }

    public static String getNext(String transportAddress, String oid, String community) throws IOException {
        return getNext(transportAddress, oid, community, SnmpUtils.VERSION_1);
    }

    public static String getNext(String transportAddress, String oid) throws IOException {
        return getNext(transportAddress, oid, SnmpUtils.DEFAULT_COMMUNITY);
    }

    public static PDU sendV1Trap(String transportAddress, String agent, String community, String enterprise, long timestamp,
                                 int genericTrap, int specificTrap, List varbinds) throws IOException {
        return sendV1Trap(transportAddress, agent, community, enterprise, timestamp, genericTrap, specificTrap, varbinds, SnmpUtils.DEFAULT_TIMEOUT, SnmpUtils.DEFAULT_RETRIES, SnmpUtils.DEFAULT_MAX_SIZE_RESPONSE_PDU);
    }

    public static PDU sendV1Trap(String transportAddress, String agent, String enterprise, long timestamp, int genericTrap, int specificTrap, List varbinds) throws IOException {
        return sendV1Trap(transportAddress, agent, SnmpUtils.DEFAULT_COMMUNITY, enterprise, timestamp, genericTrap, specificTrap, varbinds);
    }

     public static PDU sendV1Trap(String transportAddress, String enterprise, long timestamp, int genericTrap, int specificTrap, List varbinds) throws IOException {
        return sendV1Trap(transportAddress, null, SnmpUtils.DEFAULT_COMMUNITY, enterprise, timestamp, genericTrap, specificTrap, varbinds);
    }

    public static PDU sendV2cTrap(String transportAddress, String agent, String community, long timestamp, String snmpTrapOid, List varbinds) throws IOException {
        sendV2cTrap(transportAddress, agent, community, timestamp, snmpTrapOid, varbinds, SnmpUtils.DEFAULT_TIMEOUT, SnmpUtils.DEFAULT_RETRIES, SnmpUtils.DEFAULT_MAX_SIZE_RESPONSE_PDU);
    }

    public static PDU sendV2cTrap(String transportAddress, String agent, long timestamp, String snmpTrapOid, List varbinds) throws IOException {
        sendV2cTrap(transportAddress, agent, SnmpUtils.DEFAULT_COMMUNITY, timestamp, snmpTrapOid, varbinds);
    }
    public static PDU sendV2cTrap(String transportAddress, String agent, List varbinds) throws IOException {
        sendV2cTrap(transportAddress, agent, (long) Math.floor(System.currentTimeMillis()/1000), null, varbinds);
    }

    public static PDU sendV2cTrap(String transportAddress, List varbinds) throws IOException {
        sendV2cTrap(transportAddress, null, (long) Math.floor(System.currentTimeMillis()/1000), null, varbinds);
    }

    public static PDU set(String transportAddress, String oid, String value, String type, String community, int version) throws IOException {
        return set(transportAddress, oid, value, type, community, version, SnmpUtils.DEFAULT_TIMEOUT, SnmpUtils.DEFAULT_RETRIES, SnmpUtils.DEFAULT_MAX_SIZE_RESPONSE_PDU);
    }
    public static PDU set(String transportAddress, String oid, String value, String type, String community) throws IOException {
        return set(transportAddress, oid, value, type, community, SnmpUtils.VERSION_1);
    }
    public static PDU set(String transportAddress, String oid, String value, String type) throws IOException {
        return set(transportAddress, oid, value, type, SnmpUtils.DEFAULT_COMMUNITY);
    }

    public static List getSubtree(String transportAddress, String oid, String community, int version, String lowerBoundOid, String upperBoundOid) throws Exception {
        return getSubtree(transportAddress, oid, community, version, lowerBoundOid, upperBoundOid, SnmpUtils.DEFAULT_MAX_REPETITIONS, SnmpUtils.DEFAULT_TIMEOUT, SnmpUtils.DEFAULT_RETRIES, SnmpUtils.DEFAULT_MAX_SIZE_RESPONSE_PDU);
    }

    public static List getSubtree(String transportAddress, String oid, String community, int version) throws Exception {
        return getSubtree(transportAddress, oid, community, version, null, null);
    }

    public static List getSubtree(String transportAddress, String oid, String community) throws Exception {
        return getSubtree(transportAddress, oid, community, SnmpUtils.VERSION_1);
    }

    public static List getSubtree(String transportAddress, String oid) throws Exception {
        return getSubtree(transportAddress, oid, SnmpUtils.DEFAULT_COMMUNITY);
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
            if (event.getStatus() != -1 ) {
                def eventColumns=event.getColumns();
                if(eventColumns!=null) {
                    for (int i = 0; i < eventColumns.length; i++) {
                        VariableBinding vb = eventColumns[i];
                        Map vbMap = new HashMap();
                        String vbOid = vb.getOid().toString();
                        vbMap.put(RSnmpConstants.OID, vbOid);
                        vbMap.put(RSnmpConstants.VARBIND_VALUE, vb.getVariable().toString());
                        vbMap.put("Remainder", new OID(vbOid.substring(tableString.length() -1)).toString());
                        results.add(vbMap);
                    }
                }
            }
            else {
                return new ArrayList();
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

    public static PDU sendV1Trap(String transportAddress, String agent, String community, String enterprise, long timestamp,
                                 int genericTrap, int specificTrap, List varbinds, int timeout, int retries, int maxSizeResponsePDU) throws IOException {
        Address address = getAddress(transportAddress);
        Snmp snmp = createSnmpSession(address);
        Target target = createTarget(SnmpUtils.VERSION_1, community, address, retries, timeout, maxSizeResponsePDU);
        snmp.listen();
        PDU trap = createVersion1TrapPdu(enterprise, agent, timestamp, genericTrap, specificTrap);
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

    public static PDU sendV2cTrap(String transportAddress, String agent, String community, long timestamp, String snmpTrapOid, List varbinds, int timeout, int retries, int maxSizeResponsePDU) throws IOException {
        Address address = getAddress(transportAddress);
        Snmp snmp = createSnmpSession(address);
        Target target = createTarget(SnmpUtils.VERSION_2c, community, address, retries, timeout, maxSizeResponsePDU);
        snmp.listen();
        PDU trap = new PDU();
        trap.setType(PDU.TRAP);
        if(snmpTrapOid == null){
            snmpTrapOid = SnmpUtils.DEFULT_SNMP_TRAP_OID.toString();
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
        TransportMapping tm = null;
        String agentAddress = "localhost/161"
        if(agent != null){
           if(agent.indexOf("/") < 0){
               agent += "/161"
           }
           agentAddress = agent;
        }
        if(address instanceof UdpAddress){
            tm = new DefaultUdpTransportMapping(new UdpAddress(agentAddress))
        }
        else{
            tm = new DefaultTcpTransportMapping(new TcpAddress(agentAddress))
        }
        ResponseEvent responseEvent = snmp.send(trap, target, tm);
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
                return getVariableValue(result.getVariable());
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
    private static PDUv1 createVersion1TrapPdu(String enterprise, String agent, long timestamp, int genericTrap, int specificTrap) {
        PDUv1 pdu = new PDUv1();
        if(agent != null){
            pdu.setAgentAddress(IpAddress.parse(agent))    
        }
        pdu.setType(PDU.V1TRAP);
        pdu.setEnterprise(new OID(enterprise));
        pdu.setTimestamp(timestamp);
        pdu.setGenericTrap(genericTrap);
        pdu.setSpecificTrap(specificTrap);
        return pdu;
    }

    private static String getVariableValue(Variable v){
        if(v instanceof OctetString){
            OctetString oc = (OctetString)v;
            char c = ' '
            return oc.toASCII(c);
        }
        return v.toString();
    }
}