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

class RsUser {
    def static final String RSADMIN = "rsadmin";
    def static final String RSUSER = "rsuser";
    def static final String DEFAULT_PASSWORD = "changeme";
    static searchable = {
        except= ["groups", "permissionRelations", "userInformations"]
    };
    Long id;
    Long version;
    String username
    String rsOwner = "p"
    String passwordHash
    List groups = [];
    List permissionRelations = [];
    List userInformations = [];
    org.springframework.validation.Errors errors;
    Object __operation_class__;
    Object __is_federated_properties_loaded__;
    static cascaded = ["permissionRelations": true, "userInformations": true]
    static relations = [
            groups: [type: Group, reverseName: "users", isMany: true],
            permissionRelations: [isMany: true, reverseName: "rsUser", type: UserPermissionRel],
            userInformations: [type: RsUserInformation, isMany: true, reverseName: "rsUser"]
    ]
    static constraints = {
        username(key: [], nullable: false, blank: false)
        __operation_class__(nullable: true)
        __is_federated_properties_loaded__(nullable: true)
        errors(nullable: true)
    }

    String toString() {
        return "$username";
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
}
