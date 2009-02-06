package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 6, 2009
 * Time: 9:40:11 AM
 * To change this template use File | Settings | File Templates.
 */
class UiObjectMapContent {
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "objectMap"];
        storageType "File"

    };
    static datasources = ["RCMDB": ["keys": ["name": ["nameInDs": "name"], "component": ["nameInDs": "component"], "isActive": ["nameInDs": "isActive"]]]]

    boolean isActive = true;
    String name="";
    Long x=0;
    Long y=0;
    Long width=0;
    Long height=0;
    String type="text";
    String dataKey="";
    org.springframework.validation.Errors errors;
    Object __operation_class__;
    Object __is_federated_properties_loaded__;
    UiObjectMap objectMap;
    static relations = [
            objectMap: [type: UiObjectMap, reverseName: "nodeContents", isMany: false]
    ]

    static constraints = {
        name(nullable: false, blank: false, key: ["objectMap", "isActive"]);
        x(nullable: false)
        y(nullable: false)
        width(nullable: false)
        height(nullable: false)
        dataKey(blank: false, nullable: false)
        type(blank: false, nullable: true, inList: ["text", "image", "gauge"])
        __operation_class__(nullable: true)
        __is_federated_properties_loaded__(nullable: true)
        errors(nullable: true)
    }

    static propertyConfiguration = [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "objectMap"];

    public String toString()
    {
        return "MapContent";
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}