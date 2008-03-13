class Customer {
    static hasMany = [slas:ServiceLevelAgree];
//    Set slas = new HashSet();
    String name;
}
