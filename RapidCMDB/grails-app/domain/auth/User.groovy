package auth;
class User {
    String username
    String passwordHash
	static hasMany = [roles: UserRoleRel];
    static constraints = {
        username(unique:true, nullable: false, blank: false)
    }
    
    String toString(){
        return "$username";
    }
}
