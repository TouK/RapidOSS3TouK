package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 3, 2009
 * Time: 9:13:20 AM
 * To change this template use File | Settings | File Templates.
 */
class UiImage {
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "component", "column"];
        storageType "FileAndMemory"

    };
    static datasources = ["RCMDB": ["keys": ["id": ["nameInDs": "id"]]]]

    boolean isActive = true;
    String src = "";
    String visible = "true";
    String align = "left";
    Long id;
    Long version;
    org.springframework.validation.Errors errors;
    Object __operation_class__;
    Object __is_federated_properties_loaded__;
    UiComponent component;
    UiColumn column;
    static relations = [
            component: [type: UiComponent, reverseName: "images", isMany: false]
            ,column: [type: UiColumn, reverseName: "images", isMany: false]
    ]

    static constraints = {
        src(blank:false)
        visible(blank: true, nullable: true)
        align(blank: true, nullable: true, inList:["left", "right", "center"])
        __operation_class__(nullable: true)
        __is_federated_properties_loaded__(nullable: true)
        errors(nullable: true)
    }

    static propertyConfiguration = [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "component", "column"];

    public String toString()
    {
        return getProperty("src");
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}