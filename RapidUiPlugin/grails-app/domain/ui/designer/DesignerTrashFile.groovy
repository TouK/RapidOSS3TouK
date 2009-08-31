package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Mar 2, 2009
 * Time: 9:51:33 AM
 * To change this template use File | Settings | File Templates.
 */
class DesignerTrashFile {
    //AUTO_GENERATED_CODE
    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
        storageType "FileAndMemory"
    };
    static datasources = ["RCMDB": ["keys": ["fileName": ["nameInDs": "fileName"]]]]
    String fileName = "";
    Long id;
    Long version;

    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);
    org.springframework.validation.Errors errors;
    Object __operation_class__;
    Object __is_federated_properties_loaded__;
    static relations = [:]
    static constraints = {
        fileName(blank: false, nullable: false, key:[])
        __operation_class__(nullable: true)
        __is_federated_properties_loaded__(nullable: true)
        errors(nullable: true)
    }

    static propertyConfiguration = [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];

    public String toString()
    {
        return getProperty("fileName");
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}