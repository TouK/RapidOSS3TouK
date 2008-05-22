package auth;
class User {
    String username
    String passwordHash

    static constraints = {
        username(unique:true, nullable: false, blank: false)
    }
}
