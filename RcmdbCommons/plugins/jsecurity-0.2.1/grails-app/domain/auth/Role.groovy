package auth;
class Role {
    def static final String ADMINISTRATOR = "Administrator";
    def static final String USER = "User";

    static searchable = true;
    String name
    List permissionRelations = [];
    List users = [];
    static cascaded = ["users":true, "permissionRelations":true]
    static mappedBy=["permissionRelations":"role", "users":"role"]
    static hasMany = [permissionRelations:RolePermissionRel, users:UserRoleRel];
    static constraints = {
        name(nullable: false, blank: false, key: [])
    }
    
    String toString(){
        return "$name";
    }
}
