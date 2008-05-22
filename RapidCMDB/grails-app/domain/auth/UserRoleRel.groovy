package auth;
class UserRoleRel {
    User user
    Role role
    
    static belongsTo = [User, Role]
    static constraints={       
     	user(unique:["role"])
    }
}
