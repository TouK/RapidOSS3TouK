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
package connector

import script.CmdbScript
import connection.SnmpConnection

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Nov 18, 2008
* Time: 2:16:23 PM
*/
class SnmpConnector {
    static searchable = {
        except = ["errors"];
    };
    static datasources = ["RCMDB": ["keys": ["name": ["nameInDs": "name"]]]]
    Long id;
    Long version;
    Date rsInsertedAt = new Date(0);
    Date rsUpdatedAt  = new Date(0);
    String name = "";
    String rsOwner = "p";
    CmdbScript script;
    SnmpConnection connection;
    org.springframework.validation.Errors errors;
    Object __operation_class__;
    Object __dynamic_property_storage__;


    static relations = [
            script: [type: CmdbScript, isMany: false],
            connection: [type: SnmpConnection, isMany: false]
    ]
    static constraints = {
        name(blank: false, nullable: false, key: [])
        script(nullable: true)
        connection(nullable: true)
        errors(nullable: true)
        __operation_class__(nullable: true)
        __dynamic_property_storage__(nullable: true)
    }
    static transients = ["errors"];

    public String toString()
    {
        return name;
    }

    static def getConnectionName(connectorName) {
        return "${connectorName}Conn";
    }

    static def getDatasourceName(connectorName) {
        return "${connectorName}Ds";
    }
}