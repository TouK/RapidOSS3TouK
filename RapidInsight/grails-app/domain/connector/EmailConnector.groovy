package connector
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 12, 2009
 * Time: 2:52:32 PM
 * To change this template use File | Settings | File Templates.
 */
import datasource.EmailDatasource


class EmailConnector {
   static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "ds"];
    };
    static datasources = ["RCMDB": ["keys": ["name": ["nameInDs": "name"]]]]

    String rsOwner = "p";
    
    String name = "";

    Long id;

    Long version;

    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);

    org.springframework.validation.Errors errors;

    Object __operation_class__;

    Object __is_federated_properties_loaded__;


    EmailDatasource ds;



    static relations = [
            ds: [type: EmailDatasource, isMany: false]
    ]

    static constraints = {
        name(blank: false, nullable: false, key: [])
        __operation_class__(nullable: true)

        __is_federated_properties_loaded__(nullable: true)

        errors(nullable: true)

        ds(nullable: true)
    }

    static propertyConfiguration = [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "ds"];

    public String toString()
    {
        return "${name}";
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
}