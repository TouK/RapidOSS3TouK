package auth;
class RolePermissionRel {
   static searchable = {
        except:["permission", "role"]
     };
    Role role
    Permission permission
    String target
    String rsOwner = "p"
    String actions
    static relations = [
            permission:[type:Permission, reverseName:"roleRelations", isMany:false],
            role:[isMany:false, reverseName:"permissionRelations", type:Role]
    ]
    static constraints = {
        actions(nullable: false, blank: false)
        role(nullable: true, key:["permission"])
        permission(nullable: true)
    }
}
