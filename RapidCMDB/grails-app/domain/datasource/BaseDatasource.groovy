package datasource;
class BaseDatasource {
     String name;
     static constraints = {
         name(blank:false, nullable:false, unique:true);
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
