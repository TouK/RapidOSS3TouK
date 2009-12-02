package connector
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 12, 2009
 * Time: 2:52:32 PM
 * To change this template use File | Settings | File Templates.
 */
import datasource.AolDatasource


class AolConnector {
   static searchable = {
        except = ["errors", "__operation_class__", "__dynamic_property_storage__", "ds"];
    };
    static datasources = ["RCMDB": ["keys": ["name": ["nameInDs": "name"]]]]

    String rsOwner = "p";
    
    String name = "";

    Long id;

    Long version;

    Long rsInsertedAt =0;

    Long rsUpdatedAt =0;

    org.springframework.validation.Errors errors;

    Object __operation_class__;

    Object __dynamic_property_storage__;


    AolDatasource ds;



    static relations = [
            ds: [type: AolDatasource, isMany: false]
    ]

    static constraints = {
        name(blank: false, nullable: false, key: [])
        __operation_class__(nullable: true)

        __dynamic_property_storage__(nullable: true)

        errors(nullable: true)

        ds(nullable: true)
    }

    static propertyConfiguration = [:]
    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__", "ds"];

    public String toString()
    {
        return "${name}";
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
}