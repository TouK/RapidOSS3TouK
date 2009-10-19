package ui.designer;
import com.ifountain.core.domain.annotations.*;

class UiAction
{

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__dynamic_property_storage__", "tab", "events", "subscribedEvents"];
        storageType "FileAndMemory"
    };
    static datasources = ["RCMDB": ["keys": ["name": ["nameInDs": "name"], "isActive": ["nameInDs": "isActive"]]]]

    boolean isActive = true;
    String name = "";
    Long tabId;
    String condition = "";

    Long id;

    Long version;

    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);

    org.springframework.validation.Errors errors;

    Object __operation_class__;

    Object __dynamic_property_storage__;

    UiTab tab;
    List triggers = [];
    List subscribedEvents = [];


    static relations = [
            tab: [type: UiTab, reverseName: "actions", isMany: false],
            triggers: [type: UiActionTrigger, reverseName: "action", isMany: true],
            subscribedEvents: [type: UiActionTrigger, reverseName: "triggeringAction", isMany: true]
    ]

    static constraints = {
        name(blank: false, nullable: false, key: ["tabId", "isActive"], matches:"[a-z_A-z]\\w*")

        condition(blank: true, nullable: true)

        __operation_class__(nullable: true)

        __dynamic_property_storage__(nullable: true)

        errors(nullable: true)

    }

    static propertyConfiguration = [:]
    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__", "tab", "events", "subscribedEvents"];

    public String toString()
    {
        return getProperty("name");
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}