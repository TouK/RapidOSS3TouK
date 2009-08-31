package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 2, 2009
 * Time: 2:33:06 PM
 * To change this template use File | Settings | File Templates.
 */
class UiMenuItem {
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "component", "action", "childMenuItems", "parentMenuItem", "subscribedEvents"];


        storageType "FileAndMemory"

    };
    static datasources = ["RCMDB":["keys":["name":["nameInDs":"name"], "component":["nameInDs":"component"], "isActive":["nameInDs":"isActive"]]]]

    boolean isActive = true;
    String name ="";
    String label ="";
    String type = "component";
    String visible = "true";
    Long id ;
    Long version ;

    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);
    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __is_federated_properties_loaded__ ;
    UiComponent component ;
    Long componentId ;
    List childMenuItems = [];
    UiMenuItem parentMenuItem;
    List subscribedEvents = [];


    static relations = [
        component:[type:UiComponent, reverseName:"menuItems", isMany:false],
        childMenuItems:[type:UiMenuItem, reverseName:"parentMenuItem", isMany:true],
        subscribedEvents:[type:UiActionTrigger, reverseName:"menu", isMany:true],
        parentMenuItem:[type:UiMenuItem, reverseName:"childMenuItems", isMany:false]
    ]

    static constraints={
     name(blank:false,nullable:false,key:["componentId", "isActive"], matches:"[a-z_A-z]\\w*")
     label(blank:true,nullable:false);
     type(blank:false,nullable:false, inList:["component", "property", "toolbar"]);
     visible(blank:true,nullable:true);
     __operation_class__(nullable:true)

     __is_federated_properties_loaded__(nullable:true)

     errors(nullable:true)



    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "component", "action", "childMenuItems", "parentMenuItem", "subscribedEvents"];

    public String toString()
    {
    	return "${getClass().getName()}[name:${getProperty("name")}]";
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}