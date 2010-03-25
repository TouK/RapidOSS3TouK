package message

import com.ifountain.core.domain.annotations.*;

class RsMessageRule {

    //AUTO_GENERATED_CODE
    public static final String DEFAULT_DESTINATION = "__default"

    static searchable = {
        except = ["errors", "__operation_class__", "__dynamic_property_storage__"];

    };
    static datasources = ["RCMDB": ["keys": ["searchQueryId": ["nameInDs": "searchQueryId"], "userId": ["nameInDs": "userId"], "destinationType": ["nameInDs": "destinationType"]]]]

    String rsOwner = "p"
    Long searchQueryId = 0;
    Long userId = 0;
    Long calendarId = 0;
    String destinationType = "";
    Long delay = 0;
    String ruleType = "self"
    String users = "";
    String groups = "";
    Boolean sendClearEventType = false;
    Boolean enabled = true;

    Long id;

    Long version;

    Long rsInsertedAt = 0;

    Long rsUpdatedAt = 0;

    org.springframework.validation.Errors errors;

    Object __operation_class__;

    Object __dynamic_property_storage__;


    static relations = [:]

    static constraints = {
        searchQueryId(nullable: false, key: ["destinationType", "userId"])
        userId(nullable: false)
        calendarId(nullable: false)
        destinationType(blank: false, nullable: false)
        users(nullable: false)
        groups(nullable: false)
        ruleType(blank: false, nullable: false, inList: ["self", "public", "system"])
        delay(nullable: false)
        sendClearEventType(nullable: true)
        __operation_class__(nullable: true)
        __dynamic_property_storage__(nullable: true)
        errors(nullable: true)
    }

    static propertyConfiguration = [:]
    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__"];

    public String toString()
    {
        return "${getClass().getName()}[destinationType:${getProperty("destinationType")}, searchQueryId:${getProperty("searchQueryId")}, userId:${getProperty("userId")}]";
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE

}
