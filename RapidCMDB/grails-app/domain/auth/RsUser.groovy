package auth

import search.SearchQuery;
class RsUser {
    String username
    String passwordHash
	static hasMany = [roles: UserRoleRel, queries:SearchQuery];
    static constraints = {
        username(unique:true, nullable: false, blank: false)
    }
    
    String toString(){
        return "$username";
    }
}
