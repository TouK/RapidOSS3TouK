package message

import com.ifountain.core.domain.annotations.*;

class RsMessageRule {

    //AUTO_GENERATED_CODE


    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];

    };
    static datasources = ["RCMDB": ["keys": ["searchQueryId": ["nameInDs": "searchQueryId"], "userId": ["nameInDs": "userId"], "destinationType": ["nameInDs": "destinationType"]]]]

    String rsOwner = "p"
    Long searchQueryId = 0;
    Long userId = 0;
    String destinationType = "";
    Long delay = 0;
    Boolean clearAction = false;
    Boolean enabled = true;

    Long id;

    Long version;

    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);

    org.springframework.validation.Errors errors;

    Object __operation_class__;

    Object __is_federated_properties_loaded__;


    static relations = [:]

    static constraints = {
        searchQueryId(nullable: false, key: ["destinationType", "userId"])

        userId(nullable: false)

        destinationType(blank: false, nullable: false)


        delay(nullable: false)

        clearAction(nullable: true)

        __operation_class__(nullable: true)

        __is_federated_properties_loaded__(nullable: true)

        errors(nullable: true)

    }

    static propertyConfiguration = [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];

    public String toString()
    {
        return "${getClass().getName()}[destinationType:${getProperty("destinationType")}, searchQueryId:${getProperty("searchQueryId")}, userId:${getProperty("userId")}]";
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE

}
