package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Mar 2, 2009
 * Time: 9:51:33 AM
 * To change this template use File | Settings | File Templates.
 */
class DesignerTrashPage {
    //AUTO_GENERATED_CODE
    static searchable = {
        except = ["errors", "__operation_class__", "__dynamic_property_storage__"];
        storageType "FileAndMemory"
    };
    static datasources = ["RCMDB": ["keys": ["webPage": ["nameInDs": "webPage"]]]]
    String webPage = "";
    Long id;
    Long version;
    String rsOwner = "p";

    Long rsInsertedAt =0;

    Long rsUpdatedAt =0;
    org.springframework.validation.Errors errors;
    Object __operation_class__;
    Object __dynamic_property_storage__;
    static relations = [:]
    static constraints = {
        webPage(blank: false, nullable: false, key:[])
        __operation_class__(nullable: true)
        __dynamic_property_storage__(nullable: true)
        errors(nullable: true)
    }

    static propertyConfiguration = [:]
    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__"];

    public String toString()
    {
        return getProperty("webPage");
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}