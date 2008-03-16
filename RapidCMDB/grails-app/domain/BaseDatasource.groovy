class BaseDatasource {
     String name;
     Connection connection;

     static belongsTo = Connection;
     static constraints = {
         name(blank:false, nullable:false);
         connection(nullable:false);
     };

     String toString(){
         return "$name";
     }

     def getProperty(Map keys, String propName)
     {
         return null;
     }

     def getProperties(Map keys, List properties)
     {
         return null;
     }
}
