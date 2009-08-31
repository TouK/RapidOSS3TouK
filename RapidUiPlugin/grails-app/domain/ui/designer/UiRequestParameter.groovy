package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 2, 2009
 * Time: 1:34:57 PM
 * To change this template use File | Settings | File Templates.
 */
class UiRequestParameter {
    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "action"];

        storageType "FileAndMemory"

    };
    static datasources = ["RCMDB":["keys":["key":["nameInDs":"key"], "actionId":["nameInDs":"actionId"], "isActive":["nameInDs":"isActive"]]]]

    boolean isActive = true;
    String key ="";
    String value ="";
    Long id ;
    Long version ;

    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);
    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __is_federated_properties_loaded__ ;

    UiRequestAction action;
    Long actionId;


    static relations = [action:[type:UiRequestAction, reverseName:"parameters", isMany:false]]

    static constraints={
     key(blank:false,nullable:false,key:["isActive","actionId"])
     value(blank:true,nullable:true)
     __operation_class__(nullable:true)
     __is_federated_properties_loaded__(nullable:true)
     errors(nullable:true)


    }
    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "action"];

    public String toString()
    {
    	return getProperty("key");
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
}