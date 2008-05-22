package auth;
class Role {
    String name

    static hasMany = [users: UserRoleRel];
    static constraints = {
        name(nullable: false, blank: false, unique: true)
    }
    
    String toString(){
        return "$name";
    }
}
