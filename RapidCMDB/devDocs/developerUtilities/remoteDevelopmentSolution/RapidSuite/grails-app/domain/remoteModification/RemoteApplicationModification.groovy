/*
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
package remoteModification;

class RemoteApplicationModification {
    public static final String DELETE = "delete"
    public static final String COPY = "copy"
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = [:]


    String filePath = "";
    String completeFilePath = "";
    String rsDirectory = ".";
    String targetUploadDir = ".";
    Date lastChangedAt = new Date(0);
    String operation = "";
    String content = "";
    Boolean commited = false;
    Boolean ignored = false;
    Boolean isActive = true;
    Date commitedAt = new Date(0);
    String comment = "";

    Long id;

    Long version;

    org.springframework.validation.Errors errors;

    Object __operation_class__;

    Object __is_federated_properties_loaded__;

    List realises = [];


    static relations = [:]
    static constraints = {
        filePath(blank: false, nullable: false)
        completeFilePath(blank: false, nullable: false)
        lastChangedAt(nullable: false)
        operation(blank: false, nullable: false)
        content(blank: true, nullable: true)
        comment(blank: true, nullable: true)
        commited(nullable: false)
        commitedAt(nullable: false)
        __operation_class__(nullable: true)
        __is_federated_properties_loaded__(nullable: true)
        errors(nullable: true)
    }

    static propertyConfiguration = [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];

    //AUTO_GENERATED_CODE
}
