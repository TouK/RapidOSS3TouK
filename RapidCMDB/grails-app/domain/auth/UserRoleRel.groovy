package auth;
class UserRoleRel {
    RsUser user
    Role role
    
    static belongsTo = [RsUser, Role]
    static constraints={       
     	user(unique:["role"])
    }
}
