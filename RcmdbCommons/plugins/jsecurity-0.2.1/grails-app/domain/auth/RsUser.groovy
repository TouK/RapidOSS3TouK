package auth

class RsUser {
    def static final String RSADMIN = "rsadmin";
    static searchable = {
        except:["groups", "permissionRelations"]
     };
    String username
    String rsOwner = "p"
    String passwordHash
    List groups = [];
    List permissionRelations = [];
    RsUserInformation userInformation;

    static cascaded = ["permissionRelations":true]
    static relations = [
            groups:[type:Group, reverseName:"users", isMany:true],
            permissionRelations:[isMany:true, reverseName:"rsUser", type:UserPermissionRel],
            userInformation:[type:RsUserInformation,isMany:false]
    ]
    static constraints = {
        username(key: [], nullable: false, blank: false)
        userInformation(nullable:true)
    }

    String toString(){
        return "$username";
    }
}
