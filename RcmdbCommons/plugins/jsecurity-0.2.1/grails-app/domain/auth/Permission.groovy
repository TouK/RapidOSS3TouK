package auth;
class Permission {
     static searchable = true;
    String type
    String possibleActions
    List userRelations = [];
    List roleRelations = [];
    static cascaded = ["userRelations":true, "roleRelations":true]
    static hasMany = [userRelations:UserPermissionRel, roleRelations:RolePermissionRel]
    static mappedBy=["userRelations":"permission", "roleRelations":"permission"]
    static constraints = {
        type(nullable: false, blank: false, key: [])
        possibleActions(nullable:false, blank: false)
    }
}
