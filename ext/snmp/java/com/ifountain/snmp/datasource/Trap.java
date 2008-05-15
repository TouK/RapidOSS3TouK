package com.ifountain.snmp.datasource;

import com.ifountain.snmp.util.RSnmpConstants;

import java.util.*;
import java.net.InetAddress;

import org.snmp4j.smi.*;
import org.snmp4j.PDUv1;
import org.snmp4j.PDU;
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
 * User: Administrator
 * Date: May 14, 2008
 * Time: 3:52:45 PM
 */
public class Trap {
    private int genericType;
    private int specificType;
    private String enterpriseId;
    private Vector variableBindings;
    private long timestamp;
    private InetAddress address;
    private String typeString;
    private static final List GENERIC_TRAPS;
    private static final int SYSUPTIME_OID_INDEX = 0;
    private static final int TRAP_OID_INDEX = 1;
    static final OID EXT_SYSUPTIME_OID = new OID(".1.3.6.1.2.1.1.3");

    static {
        GENERIC_TRAPS = new ArrayList();
        GENERIC_TRAPS.add(new OID("1.3.6.1.6.3.1.1.5.1")); // coldStart
        GENERIC_TRAPS.add(new OID("1.3.6.1.6.3.1.1.5.2")); // warmStart
        GENERIC_TRAPS.add(new OID("1.3.6.1.6.3.1.1.5.3")); // linkDown
        GENERIC_TRAPS.add(new OID("1.3.6.1.6.3.1.1.5.4")); // linkUp
        GENERIC_TRAPS.add(new OID("1.3.6.1.6.3.1.1.5.5")); // authenticationFailure
        GENERIC_TRAPS.add(new OID("1.3.6.1.6.3.1.1.5.6")); // egpNeighborLoss
    }

    public Trap(PDUv1 pdu) {
        this.typeString = PDU.getTypeString(pdu.getType());
        this.genericType = pdu.getGenericTrap();
        this.specificType = pdu.getSpecificTrap();
        this.enterpriseId = pdu.getEnterprise().toString();
        this.variableBindings = pdu.getVariableBindings();
        this.timestamp = pdu.getTimestamp();
        this.address = pdu.getAgentAddress().getInetAddress();
    }

    public Trap(PDU pdu, InetAddress address) throws Exception {
        this.typeString = PDU.getTypeString(pdu.getType());
        this.address = address;
        OID firstVarBindOid = pdu.get(0).getOid();
        OID secondVarBindOid = pdu.get(1).getOid();
        if ((!(firstVarBindOid.equals(SnmpConstants.sysUpTime))) ||
                (!(secondVarBindOid.equals(SnmpConstants.snmpTrapOID) || secondVarBindOid.equals(EXT_SYSUPTIME_OID)))) {
            throw new Exception("Trap " + typeString + " is invalid. The first varbind must be sysUpTime.0 and the second snmpTrapOID.0");
        }

        getTimeStamp(pdu);
        OID snmpTrapOid = (OID) pdu.get(TRAP_OID_INDEX).getVariable();
        VariableBinding lastVarBind = pdu.get(pdu.size() - 1);
        String snmpTrapOidStr = snmpTrapOid.toString();
        int lastIndex = snmpTrapOidStr.lastIndexOf('.');
        String lastSubIdStr = snmpTrapOidStr.substring(lastIndex + 1);
        int lastSubId = -1;
        try {
            lastSubId = Integer.parseInt(lastSubIdStr);
        } catch (NumberFormatException nfe) {
            lastSubId = -1;
        }
        if (Trap.GENERIC_TRAPS.contains(snmpTrapOid)) {
            genericType = lastSubId - 1;
            specificType = 0;
            if (lastVarBind.getOid().equals(SnmpConstants.snmpTrapEnterprise)) {
                enterpriseId = lastVarBind.getVariable().toString();
            } else {
                enterpriseId = SnmpConstants.snmpTraps.toString() + "." + snmpTrapOidStr.charAt(snmpTrapOidStr.length() - 1);
            }
        }
        else {
            genericType = 6;
            specificType = lastSubId;
            int nextToLastIndex = snmpTrapOidStr.lastIndexOf('.', lastIndex - 1);
            String nextToLastSubIdStr = snmpTrapOidStr.substring(nextToLastIndex + 1, lastIndex);
            if (nextToLastSubIdStr.equals("0")) {
                enterpriseId = snmpTrapOidStr.substring(0, nextToLastIndex);
            }
            else {
                enterpriseId = snmpTrapOidStr.substring(0, lastIndex);
            }
        }
        this.variableBindings = pdu.getVariableBindings();
    }

    private void getTimeStamp(PDU pdu) throws Exception {
        Variable timestampVb = pdu.get(SYSUPTIME_OID_INDEX).getVariable();
        switch (timestampVb.getSyntax()) {
            case SMIConstants.SYNTAX_TIMETICKS:
                this.timestamp = ((TimeTicks) timestampVb).getValue();
                break;
            case SMIConstants.SYNTAX_INTEGER32:
                this.timestamp = ((Integer32) timestampVb).getValue();
                break;
            default:
                throw new Exception(typeString + " does not have the required first varbind as TIMETICKS");
        }
    }

    public Map toMap() {
        Map trap = new HashMap();
        trap.put(RSnmpConstants.AGENT, address.getHostAddress());
        trap.put(RSnmpConstants.TIMESTAMP, String.valueOf(timestamp));
        trap.put(RSnmpConstants.ENTERPRISE, enterpriseId);
        trap.put(RSnmpConstants.GENERIC_TYPE, String.valueOf(genericType));
        trap.put(RSnmpConstants.SPECIFIC_TYPE, String.valueOf(specificType));
        List varbinds = new ArrayList();

        for (int i = 0; i < variableBindings.size(); i++) {
            VariableBinding var = (VariableBinding) variableBindings.get(i);
            Map varbind = new HashMap();
            varbind.put(RSnmpConstants.OID, var.getOid().toString());
            varbind.put(RSnmpConstants.VARBIND_VALUE, var.getVariable().toString());
            varbinds.add(varbind);
        }
        trap.put(RSnmpConstants.VARBINDS, varbinds);
        return trap;
    }
}
