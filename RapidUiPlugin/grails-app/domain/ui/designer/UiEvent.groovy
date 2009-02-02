package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 29, 2009
 * Time: 6:58:14 PM
 * To change this template use File | Settings | File Templates.
 */
class UiEvent{
    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "component", "action"];
        storageType "File"
    };
    String eventName = "";
    Boolean isActive = true;
    static datasources = ["RCMDB":["keys":["id":["nameInDs":"id"]]]]

    UiComponent component ;
    UiAction action ;
    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __is_federated_properties_loaded__ ;


    static relations = [
        component:[type:UiComponent, reverseName:"events", isMany:false]
        ,action:[type:UiAction, reverseName:"events", isMany:false]
    ]
    static constraints={
        eventName(blank:false, nullable:false)
        action(nullable:false)
        component(nullable:true)
        __operation_class__(nullable:true)
        __is_federated_properties_loaded__(nullable:true)
        errors(nullable:true)
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "component", "action"];
}