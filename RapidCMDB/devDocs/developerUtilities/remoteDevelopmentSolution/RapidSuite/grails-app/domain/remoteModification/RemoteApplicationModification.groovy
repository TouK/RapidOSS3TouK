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
        except = ["errors", "__operation_class__", "__dynamic_property_storage__"];
    };
    static datasources = [:]


    String relativeFilePath = "";
    String completeFilePath = "";
    String targetRsFilePath = ".";
    String targetUploadFilePath = ".";
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

    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);

    org.springframework.validation.Errors errors;

    Object __operation_class__;

    Object __dynamic_property_storage__;

    List realises = [];


    static relations = [:]
    static constraints = {
        relativeFilePath(blank: false, nullable: false)
        completeFilePath(blank: false, nullable: false)
        targetUploadFilePath(blank: false, nullable: false)
        targetRsFilePath(blank: false, nullable: false)
        lastChangedAt(nullable: false)
        operation(blank: false, nullable: false)
        content(blank: true, nullable: true)
        comment(blank: true, nullable: true)
        commited(nullable: false)
        commitedAt(nullable: false)
        __operation_class__(nullable: true)
        __dynamic_property_storage__(nullable: true)
        errors(nullable: true)
    }

    static propertyConfiguration = [:]
    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__"];

    //AUTO_GENERATED_CODE
}
