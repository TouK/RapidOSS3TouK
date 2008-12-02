package connection

class Connection {
    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = ["RCMDB": ["master": true, "keys": ["name": ["nameInDs": "name"]]]]

    String name = "";
    String rsOwner = "p"
    String connectionClass = "";
    Long maxNumberOfConnections = 10;
    Long minTimeout = 5;
    Long maxTimeout = 20;
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
