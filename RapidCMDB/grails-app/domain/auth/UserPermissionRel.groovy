package auth;
class UserPermissionRel {
    RsUser user
    Permission permission
    String target
    String actions

    static constraints = {
        target(nullable: true, blank: false)
        actions(nullable: false, blank: false)
    }
}
