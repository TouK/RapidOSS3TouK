class Connection {
       String name;
       String connectionClass;

       static constraints = {
            name(unique:true, blank:false, nullable:false);
            connectionClass(blank:false, nullable:false);
       };

       String toString(){
           return "$name";
       }
}
