package ui.designer;

import com.ifountain.core.domain.annotations.*;

class UiDialog
{

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "tab", "component"];


        storageType "FileAndMemory"

    };
    static datasources = ["RCMDB": ["keys": ["tab": ["nameInDs": "tab"], "isActive":["nameInDs":"isActive"], "component":["nameInDs":"component"]]]]

    boolean isActive = true;
    Long width = 400;

    Long height = 300;

    Long maxHeight = 0;

    Long minHeight = 0;

    Long minWidth = 0;

    Long maxWidth = 0;
    Boolean resizable = true;

    Long x = 0;

    Long y = 0;

    String title = "";

    Long id;
    Long componentId;

    Long version;

    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);

    org.springframework.validation.Errors errors;

    Object __operation_class__;

    Object __is_federated_properties_loaded__;

    UiTab tab;
    Long tabId;

    UiComponent component;


    static relations = [

            tab: [type: UiTab, reverseName: "dialogs", isMany: false], component: [type: UiComponent, reverseName: "dialog", isMany: false]

    ]

    static constraints = {
        tabId(key: ["componentId", "isActive"])
        x(nullable: true)
        y(nullable: true)
        maxHeight(nullable: true)
        minHeight(nullable: true)
        minWidth(nullable: true)
        maxWidth(nullable: true)
        resizable(nullable: true)
        component(nullable:false)
        title(blank: true, nullable: true)
        __operation_class__(nullable: true)
        __is_federated_properties_loaded__(nullable: true)
        errors(nullable: true)

    }

    static propertyConfiguration = [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "tab", "component"];

    public String toString()
    {
        return getProperty("component");
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}