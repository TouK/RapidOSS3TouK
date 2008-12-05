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
package auth
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Sep 26, 2008
 * Time: 2:01:25 PM
 */

class Group
{
    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "users", "role"];
    };
    static datasources = ["RCMDB": ["keys": ["name": ["nameInDs": "name"]]]]
    String name = "";
    String segmentFilter = "";
    String rsOwner = "p"
    Long id;
    Long version;
    org.springframework.validation.Errors errors;
    Object __operation_class__;
    Object __is_federated_properties_loaded__;
    List users = [];
    Role role;
    static relations = [
            users: [type: RsUser, reverseName: "groups", isMany: true],
            role: [type: Role, reverseName: "groups", isMany: false]
    ]
    static constraints = {
        name(key:[],blank: false, nullable: false)
        segmentFilter(blank: true, nullable: true)
        __operation_class__(nullable: true)
        __is_federated_properties_loaded__(nullable: true)
        errors(nullable: true)
        role(nullable: true)
    }

    static propertyConfiguration = [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "users", "role"];

    public String toString()
    {
        return name;
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
}