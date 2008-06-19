package auth

class RsUser {
    static searchable = {
        roles cascade: "delete"
        permissionRelations cascade: "delete"
    }
    String username
    String passwordHash
    static hasMany = [roles:UserRoleRel, permissionRelations:UserPermissionRel];
    static mappedBy=["roles":"rsUser", "permissionRelations":"rsUser"]
    static constraints = {
        username(key: [], nullable: false, blank: false)
    }
    
    String toString(){
        return "$username";
    }
}
