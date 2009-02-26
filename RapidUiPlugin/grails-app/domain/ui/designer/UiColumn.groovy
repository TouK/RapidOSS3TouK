package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 3, 2009
 * Time: 9:13:12 AM
 * To change this template use File | Settings | File Templates.
 */
class UiColumn {
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "component"];
        storageType "File"

    };
    static datasources = ["RCMDB": ["keys": ["attributeName": ["nameInDs": "attributeName"], "isActive": ["nameInDs": "isActive"], "component": ["nameInDs": "component"]]]]

    boolean isActive = true;
    String attributeName = "";
    String colLabel = "";
    Long width = 100;
    Long columnIndex = 0;
    Boolean sortBy = false;
    String sortOrder = "asc";

    Long id;
    Long componentId;

    Long version;

    org.springframework.validation.Errors errors;

    Object __operation_class__;

    Object __is_federated_properties_loaded__;

    UiComponent component;
    static relations = [
            component: [type: UiComponent, reverseName: "columns", isMany: false]
    ]

    static constraints = {
        attributeName(blank: false, nullable: false, key: ["componentId", "isActive"])
        colLabel(blank: true, nullable: false)
        sortBy(nullable: true)
        sortOrder(blank:false, nullable: true, inList:["asc", "desc"]);
        __operation_class__(nullable: true)
        __is_federated_properties_loaded__(nullable: true)
        errors(nullable: true)
    }

    static propertyConfiguration = [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "component"];

    public String toString()
    {
        return getProperty("attributeName");
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}