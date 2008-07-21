package auth;
class UserPermissionRel {
    static searchable = true;
    RsUser rsUser
    Permission permission
    String target
    String actions
    static mappedBy=["permission":"userRelations", "rsUser":"permissionRelations"]
    static constraints = {
        target(nullable: true, blank: false)
        actions(nullable: false, blank: false)
        permission(nullable: true)
        rsUser(nullable: true, key:["permission"])
    }
}
