package com.ifountain.snmp.test.util;

import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDUv1;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.transport.DefaultUdpTransportMapping;
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
public class SnmpTestUtils {

    public static void sendTrap() {
        Address targetAddress = GenericAddress.parse("udp:127.0.0.1/162");
        CommunityTarget target = new CommunityTarget();
           target.setCommunity(new OctetString("public"));
             target.setAddress(targetAddress);
            target.setVersion(SnmpConstants.version1);

             PDUv1 pdu = new PDUv1();
             pdu.setType(PDU.V1TRAP);

              VariableBinding vb = new VariableBinding();
        
//              vb.setOid(OVO_STRING_TYPE_OID);
//             vb.setVariable(new OctetString(stralarm));
//             pdu.add(vb);
//             vb.setOid(OVO_ALARM_LEVEL_OID);
//             vb.setVariable(new OctetString("Critical"));
//             pdu.add(vb);
//             pdu.setEnterprise(BOCO_ENTERPRISE_OID);
        try {
                 DefaultUdpTransportMapping udpTransportMap=new DefaultUdpTransportMapping();
                Snmp snmp = new Snmp(udpTransportMap);
                 ResponseEvent response =  snmp.send(pdu, target);
                 System.out.println("pdu:"+pdu);
                 System.out.println("response:"+response);
                 snmp.close();
    }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception{
        SnmpTestUtils.sendTrap();
    }
}
