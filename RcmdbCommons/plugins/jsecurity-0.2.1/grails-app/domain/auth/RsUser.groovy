package auth

class RsUser {
    def static final String RSADMIN = "rsadmin";
    static searchable = {
        except:["roles", "permissionRelations"]
     };
    String username
    String passwordHash
    List roles = [];
    List permissionRelations = [];
    static cascaded = ["roles":true, "permissionRelations":true]
    static relations = [
            roles:[type:UserRoleRel, reverseName:"rsUser", isMany:true],
            permissionRelations:[isMany:true, reverseName:"rsUser", type:UserPermissionRel]
    ]
    static constraints = {
        username(key: [], nullable: false, blank: false)
    }
    
    String toString(){
        return "$username";
    }
}
