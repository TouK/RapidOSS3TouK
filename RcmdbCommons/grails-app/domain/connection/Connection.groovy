package connection

class Connection {
    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = ["RCMDB": ["master": true, "keys": ["name": ["nameInDs": "name"]]]]

    String name = "";

    String connectionClass = "";
    int maxNumberOfConnections = 10;
    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __is_federated_properties_loaded__ ;

    static constraints = {
        name(blank: false, nullable: false, key: [])
        connectionClass(blank: true, nullable: true)
        __operation_class__(nullable:true)
        __is_federated_properties_loaded__(nullable:true)
        errors(nullable:true)
    };

    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];

    String toString() {
        return "$name";
    }
}
