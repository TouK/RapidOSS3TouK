package connector

import datasource.BaseDatasource

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Dec 14, 2009
* Time: 4:25:00 PM
* To change this template use File | Settings | File Templates.
*/
class NotificationConnector {

    static searchable = {
        except = ["errors", "__operation_class__", "__dynamic_property_storage__", "ds"];
    };
    static datasources = ["RCMDB": ["keys": ["name": ["nameInDs": "name"]]]]

    static DEFAULT_SENDER_SCRIPT="emailSender";

    String rsOwner = "p";

    String name = "";

    String type = "Email";

    Boolean showAsDestination =true;

    Long id;

    Long version;

    Long rsInsertedAt =0;

    Long rsUpdatedAt =0;

    org.springframework.validation.Errors errors;

    Object __operation_class__;

    Object __dynamic_property_storage__;


    BaseDatasource ds;





    static relations = [
            ds: [type: BaseDatasource, isMany: false]
    ]

    static constraints = {
        name(blank: false, nullable: false, key: [])

        type(blank: false, nullable: false)

        showAsDestination(nullable:true)

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