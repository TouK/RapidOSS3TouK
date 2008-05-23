package auth;
class UserRoleRel {
    RsUser rsUser
    Role role
    
    static belongsTo = [RsUser, Role]
    static constraints={       
     	rsUser(unique:["role"])
    }
}
