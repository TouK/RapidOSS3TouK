package datasource

import connection.JiraConnection

class JiraDatasource extends BaseDatasource{
    static searchable = {
        except = ["errors", "__operation_class__", "__dynamic_property_storage__", "connection"];
    };
    static datasources = [:]
    Long id;
    Long version;
    org.springframework.validation.Errors errors;
    Object __operation_class__;
    Object __dynamic_property_storage__;
    JiraConnection connection;
    Long reconnectInterval = 0;
    
    static relations = [
            connection: [isMany: false, reverseName: "jiraDatasources", type: JiraConnection]
    ]
    static constraints = {
        connection(nullable: true)
        __operation_class__(nullable: true)
        __dynamic_property_storage__(nullable: true)
        errors(nullable: true)
    }
    static transients = ["connection","errors", "__operation_class__", "__dynamic_property_storage__"]
}