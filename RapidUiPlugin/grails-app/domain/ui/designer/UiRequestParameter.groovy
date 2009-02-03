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

        storageType "File"

    };
    static datasources = ["RCMDB":["keys":["key":["nameInDs":"key"], "action":["nameInDs":"action"], "isActive":["nameInDs":"isActive"]]]]

    boolean isActive = true;
    String key ="";
    String value ="";
    Long id ;
    Long version ;
    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __is_federated_properties_loaded__ ;

    UiRequestAction action;


    static relations = [action:[type:UiRequestAction, reverseName:"parameters", isMany:false]]

    static constraints={
     key(blank:false,nullable:false,key:["isActive","action"])
     value(blank:true,nullable:true)
     action(nullable:false)
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