package auth;
class RolePermissionRel {
   static searchable = true;
    Role role
    Permission permission
    String target
    String actions
    static mappedBy=["permission":"roleRelations", "role":"permissionRelations"]
    static constraints = {
        actions(nullable: false, blank: false)
        role(nullable: true, key:["permission"])
        permission(nullable: true)
    }
}
