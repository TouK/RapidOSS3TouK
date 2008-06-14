package auth

import search.SearchQuery;
class RsUser {
    static searchable = {
        roles cascade: "delete"
        queries cascade: "delete"
        permissionRelations cascade: "delete"
    }
    String username
    String passwordHash
    static hasMany = [roles:UserRoleRel, permissionRelations:UserPermissionRel, queries:SearchQuery];
    static mappedBy=["roles":"rsUser", "permissionRelations":"rsUser", "queries":"user"]
    static constraints = {
        username(key: [], nullable: false, blank: false)
    }
    
    String toString(){
        return "$username";
    }
}
