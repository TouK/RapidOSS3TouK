class Device {
    
    static hasMany = [notifications:Notification];
    String creationClassName;
    String name;
    static constraints = {
         name(unique:true, blank:false, nullable:false);
         creationClassName(blank:false, nullable:false);
     };

     String toString(){
        return "$creationClassName::$name";
     }
}
