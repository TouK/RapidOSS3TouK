package auth;
class Role {
    static searchable = {
        users cascade: "delete"
        permissionRelations cascade: "delete"
    }
    String name
    List permissionRelations = [];
    List users = [];
    static mappedBy=["permissionRelations":"role", "users":"role"]
    static hasMany = [permissionRelations:RolePermissionRel, users:UserRoleRel];
    static constraints = {
        name(nullable: false, blank: false, key: [])
    }
    
    String toString(){
        return "$name";
    }
}
