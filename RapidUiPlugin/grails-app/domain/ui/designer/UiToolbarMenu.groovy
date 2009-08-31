package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 6, 2009
 * Time: 9:46:45 AM
 * To change this template use File | Settings | File Templates.
 */
class UiToolbarMenu {
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "objectMap"];
        storageType "FileAndMemory"

    };
    static datasources = ["RCMDB": ["keys": ["id": ["nameInDs": "id"]]]]

    boolean isActive = true;
    String label="";

    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);
    org.springframework.validation.Errors errors;
    Object __operation_class__;
    Object __is_federated_properties_loaded__;
    UiObjectMap objectMap;
    List menuItems=[];
    static relations = [
            objectMap: [type: UiObjectMap, reverseName: "toolbarMenus", isMany: false],
            menuItems: [type: UiMenuItem, isMany: true]
    ]

    static constraints = {
        label(blank:false)
        __operation_class__(nullable: true)
        __is_federated_properties_loaded__(nullable: true)
        errors(nullable: true)
    }

    static propertyConfiguration = [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "objectMap"];

    public String toString()
    {
        return getProperty("label");
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}