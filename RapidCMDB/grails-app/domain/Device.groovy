class Device {
    
    static hasMany = [notifications:Notification];
    String creationClassName;
    String instanceName;
    static constraints = {
         instanceName(unique:true, blank:false, nullable:false);
         creationClassName(blank:false, nullable:false);
     };

     String toString(){
        return "$creationClassName::$instanceName";
     }
}
