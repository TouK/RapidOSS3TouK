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

import connection.EmailConnection
import datasource.EmailDatasource

class EmailConnectionTemplate {
    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "emailConnection", "emailDatasource"];
    };
    static datasources = ["RCMDB": ["keys": ["name": ["nameInDs": "name"]]]]


    String name = "";

    Long id;

    Long version;

    org.springframework.validation.Errors errors;

    Object __operation_class__;

    Object __is_federated_properties_loaded__;

    EmailConnection emailConnection;
    EmailDatasource emailDatasource;



    static relations = [
            emailConnection: [type: EmailConnection, isMany: false],

            emailDatasource: [type: EmailDatasource, isMany: false]

    ]

    static constraints = {
        name(blank: false, nullable: false, key: [])
        __operation_class__(nullable: true)

        __is_federated_properties_loaded__(nullable: true)

        errors(nullable: true)

        emailConnection(nullable: true)
        emailDatasource(nullable: true)


    }

    static propertyConfiguration = [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "emailConnection", "emailDatasource"];

    public String toString()
    {
        return "${name}";
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
}