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
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "component", "action", "childMenuItems", "parentMenuItem"];


        storageType "File"

    };
    static datasources = ["RCMDB":["keys":["name":["nameInDs":"name"], "component":["nameInDs":"component"], "isActive":["nameInDs":"isActive"]]]]

    boolean isActive = true;
    String name ="";
    String label ="";
    String type = "component";
    String visible;
    Long id ;
    Long version ;
    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __is_federated_properties_loaded__ ;
    UiComponent component ;
    List childMenuItems = [];
    UiMenuItem parentMenuItem;
    UiAction action;


    static relations = [
        component:[type:UiComponent, reverseName:"menuItems", isMany:false],
        action:[type:UiAction, reverseName:"menuItems", isMany:false],
        childMenuItems:[type:UiMenuItem, reverseName:"parentMenuItem", isMany:true],
        parentMenuItem:[type:UiMenuItem, reverseName:"childMenuItems", isMany:false]
    ]

    static constraints={
     name(blank:false,nullable:false,key:["component", "isActive"])
     label(blank:false,nullable:false);
     type(blank:false,nullable:false, inList:["component", "property", "toolbar"]);
     action(nullable:true);
     visible(blank:true,nullable:true);
     __operation_class__(nullable:true)

     __is_federated_properties_loaded__(nullable:true)

     errors(nullable:true)



    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "component", "action", "childMenuItems", "parentMenuItem"];

    public String toString()
    {
    	return "${getClass().getName()}[name:${getProperty("name")}]";
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}