package auth;
class UserPermissionRel {
    static searchable = {
        except:["permission", "rsUser"]
     };
    RsUser rsUser
    Permission permission
    String target
    String rsOwner = "p"
    String actions
    static relations = [
            permission:[type:Permission, reverseName:"userRelations", isMany:false],
            rsUser:[isMany:false, reverseName:"permissionRelations", type:RsUser]
    ]
    static constraints = {
        target(nullable: true, blank: false)
        actions(nullable: false, blank: false)
        permission(nullable: true)
        rsUser(nullable: true, key:["permission"])
    }
}
