package auth;
class Role {
    def static final String ADMINISTRATOR = "Administrator";
    def static final String USER = "User";

    static searchable = {
        except:["permissionRelations", "groups"]
     };
    String name
    String rsOwner = "p"
    List permissionRelations = [];
    List groups = [];
    static cascaded = ["permissionRelations":true]
    static relations = [
            permissionRelations:[type:RolePermissionRel, reverseName:"role", isMany:true],
            groups:[type:Group, reverseName:"role", isMany:true]
    ]
    static constraints = {
        name(nullable: false, blank: false, key: [])
    }
    
    String toString(){
        return "$name";
    }
}
