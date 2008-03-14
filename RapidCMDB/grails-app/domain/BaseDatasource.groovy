class BaseDatasource {
     String name;
     String adapterClass;
     Connection connection;

     static belongsTo = Connection;
     static constraints = {
         name(blank:false, nullable:false);
         adapterClass(blank:false, nullable:false);
         connection(nullable:false);
     };

     String toString(){
         return "$name";
     }
}
