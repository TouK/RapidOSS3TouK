package ui.designer;

import com.ifountain.core.domain.annotations.*;

class UiDialog
{

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "tab", "component"];


        storageType "FileAndMemory"

    };
    static datasources = ["RCMDB": ["keys": ["id": ["nameInDs": "id"]]]]


    Long width = 0;

    Long height = 0;

    Long maxHeight = 0;

    Long minHeight = 0;

    Long minWidth = 0;

    Long maxWidth = 0;

    String title = "";

    Long id;

    Long version;

    org.springframework.validation.Errors errors;

    Object __operation_class__;

    Object __is_federated_properties_loaded__;

    UiTab tab;

    UiComponent component;


    static relations = [

            tab: [type: UiTab, reverseName: "dialogs", isMany: false], component: [type: UiComponent, reverseName: "dialog", isMany: false]

    ]

    static constraints = {
        tab(key: ["component"])
        width(nullable: true)

        height(nullable: true)

        maxHeight(nullable: true)

        minHeight(nullable: true)

        minWidth(nullable: true)

        maxWidth(nullable: true)

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