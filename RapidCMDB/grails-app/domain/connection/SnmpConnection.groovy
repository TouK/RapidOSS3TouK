/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
package connection

import datasource.SnmpDatasource;
public class SnmpConnection extends Connection{
    static searchable = {
        except = ["snmpDatasources"];
    };
    static cascaded = ["snmpDatasources":true]
    static datasources = [:]

    
    Long port =162;
    String connectionClass = "com.ifountain.snmp.connection.SnmpConnectionImpl";
    String host ="0.0.0.0";
    List snmpDatasources = [];
   org.springframework.validation.Errors errors ;
    static relations = [
            snmpDatasources:[isMany:true, reverseName:"connection", type:SnmpDatasource]
    ]
    static constraints={
    port(nullable:true)
        
     host(blank:true,nullable:true)
        
     
    }

    static transients = [];
}