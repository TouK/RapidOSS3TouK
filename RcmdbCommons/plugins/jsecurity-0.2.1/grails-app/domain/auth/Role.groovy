package auth;
class Role {
    def static final String ADMINISTRATOR = "Administrator";
    def static final String USER = "User";

    static searchable = {
        except:["users", "permissionRelations"]
     };
    String name
    List permissionRelations = [];
    List users = [];
    static cascaded = ["users":true, "permissionRelations":true]
    static relations = [
            permissionRelations:[type:RolePermissionRel, reverseName:"role", isMany:true],
            users:[isMany:true, reverseName:"role", type:UserRoleRel]
    ]
    static constraints = {
        name(nullable: false, blank: false, key: [])
    }
    
    String toString(){
        return "$name";
    }
}
