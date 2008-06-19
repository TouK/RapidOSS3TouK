package auth;
class RolePermissionRel {
   static searchable = {
        role component: true
        permission component: true
    }
    Role role
    Permission permission
    String target
    String actions
    static mappedBy=["permission":"roleRelations", "role":"permissionRelations"]
    static constraints = {
        actions(nullable: false, blank: false)
        role(nullable: true)
        permission(nullable: true)
    }
}
