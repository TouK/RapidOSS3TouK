package auth;
class UserRoleRel {
    static searchable = true;
    RsUser rsUser
    Role role
    static mappedBy=["role":"users", "rsUser":"roles"]
    static belongsTo = [RsUser, Role]
    static constraints={       
     	rsUser(nullable:true)
     	role(nullable:true, key:["rsUser"])
    }
}
