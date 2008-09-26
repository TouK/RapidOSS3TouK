package auth

class RsUser {
    def static final String RSADMIN = "rsadmin";
    static searchable = {
        except:["groups", "permissionRelations"]
     };
    String username
    String passwordHash
    List groups = [];
    List permissionRelations = [];
    static cascaded = ["permissionRelations":true]
    static relations = [
            groups:[type:Group, reverseName:"users", isMany:true],
            permissionRelations:[isMany:true, reverseName:"rsUser", type:UserPermissionRel]
    ]
    static constraints = {
        username(key: [], nullable: false, blank: false)
    }
    
    String toString(){
        return "$username";
    }
}
