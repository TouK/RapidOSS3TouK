package datasource;
class BaseDatasource {
     static searchable = {
        except = [];
    };
    static datasources = ["RCMDB":["master":true, "keys":["name":["nameInDs":"name"]]]]
    Long id;
    Long version;
    String name ="";
    static constraints={
    name(blank:false,nullable:false,key:[])
        
     
    }
    static transients = [];

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
