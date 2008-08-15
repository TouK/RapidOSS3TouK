package auth

class RsUser {
    static searchable = true;
    String username
    String passwordHash
    List roles = [];
    List permissionRelations = [];
    static cascaded = ["roles":true, "permissionRelations":true]
    static hasMany = [roles:UserRoleRel, permissionRelations:UserPermissionRel];
    static mappedBy=["roles":"rsUser", "permissionRelations":"rsUser"]
    static constraints = {
        username(key: [], nullable: false, blank: false)
    }
    
    String toString(){
        return "$username";
    }
}
