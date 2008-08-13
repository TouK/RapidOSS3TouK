package auth;
class Permission {
    static searchable = {
        userRelations cascade: "delete"
        roleRelations cascade: "delete"
    }
    String type
    String possibleActions
    List userRelations = [];
    List roleRelations = [];
    static hasMany = [userRelations:UserPermissionRel, roleRelations:RolePermissionRel]
    static mappedBy=["userRelations":"permission", "roleRelations":"permission"]
    static constraints = {
        type(nullable: false, blank: false, key: [])
        possibleActions(nullable:false, blank: false)
    }
}
