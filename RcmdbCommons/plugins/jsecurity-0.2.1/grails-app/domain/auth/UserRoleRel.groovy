package auth;
class UserRoleRel {
    static searchable = {
        except:["role", "rsUser"]
     };
    RsUser rsUser
    Role role

    static relations = [
            rsUser:[type:RsUser, reverseName:"roles", isMany:false],
            role:[isMany:false, reverseName:"users", type:Role]
    ]

    static constraints={       
     	rsUser(nullable:true)
     	role(nullable:true, key:["rsUser"])
    }
}
