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
package auth;
class Role {
    def static final String ADMINISTRATOR = "Administrator";
    def static final String USER = "User";

    static searchable = {
        except=["permissionRelations", "groups", "errors", "__operation_class__"]
     };
    Long id; 
    Long version;

    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);
    String name
    String rsOwner = "p"
    List permissionRelations = [];
    List groups = [];
    org.springframework.validation.Errors errors ;
    Object __operation_class__;
    static cascaded = ["permissionRelations":true]
    static transients = ["errors", "__operation_class__"]
    static relations = [
            permissionRelations:[type:RolePermissionRel, reverseName:"role", isMany:true],
            groups:[type:Group, reverseName:"role", isMany:true]
    ]
    static constraints = {
        name(nullable: false, blank: false, key: [])
        errors(nullable:true)
        __operation_class__(nullable:true)
    }
    
    String toString(){
        return "$name";
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
}
