package auth;
class Permission {
     static searchable = {
        except:["userRelations", "roleRelations"]    
     };
    String type
    String possibleActions
    List userRelations = [];
    List roleRelations = [];
    static cascaded = ["userRelations":true, "roleRelations":true]
    static relations = [userRelations:[type:UserPermissionRel, reverseName:"permission", isMany:true], roleRelations:[isMany:true, reverseName:"permission", type:RolePermissionRel]]
    static constraints = {
        type(nullable: false, blank: false, key: [])
        possibleActions(nullable:false, blank: false)
    }
}
