package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 29, 2009
 * Time: 6:58:14 PM
 * To change this template use File | Settings | File Templates.
 */
class UiActionTrigger{
    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "component", "action", "triggeringAction", "menu"];
        storageType "FileAndMemory"
    };
    public static final String MENU_TYPE = "Menu"
    public static final String COMPONENT_TYPE = "Component event"
    public static final String ACTION_TYPE = "Action event"
    public static final String GLOBAL_TYPE = "Global event"
    String event = "";
    Boolean isActive = true;
    String type = COMPONENT_TYPE;
    static datasources = ["RCMDB":["keys":["id":["nameInDs":"id"]]]]

    UiComponent component ;
    UiAction triggeringAction ;
    UiAction action ;
    UiMenuItem menu ;

    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);
    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __is_federated_properties_loaded__ ;


    static relations = [
        component:[type:UiComponent, reverseName:"triggers", isMany:false]
        ,action:[type:UiAction, reverseName:"triggers", isMany:false]
        ,triggeringAction:[type:UiAction, reverseName:"subscribedEvents", isMany:false]
        ,menu:[type:UiMenuItem, reverseName:"subscribedEvents", isMany:false]
    ]
    static constraints={
        event(blank:false, nullable:false)
        action(nullable:false)
        component(nullable:true)
        menu(nullable:true)
        triggeringAction(nullable:true)
        type(blank:false, inList:[MENU_TYPE, COMPONENT_TYPE, ACTION_TYPE, GLOBAL_TYPE])
        __operation_class__(nullable:true)
        __is_federated_properties_loaded__(nullable:true)
        errors(nullable:true)
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "component", "action", "triggeringAction", "menu"];
}