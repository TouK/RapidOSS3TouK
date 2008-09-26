package datasource;
class BaseDatasource {
     static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = ["RCMDB":["master":true, "keys":["name":["nameInDs":"name"]]]]
    Long id;
    Long version;
    String name ="";
    String rsOwner = "p"
    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __is_federated_properties_loaded__ ;
    static constraints={
        name(blank:false,nullable:false,key:[])
        __operation_class__(nullable:true)
        __is_federated_properties_loaded__(nullable:true)
        errors(nullable:true)
    }
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];

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
