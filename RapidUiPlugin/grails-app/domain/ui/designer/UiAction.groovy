
package ui.designer;
import com.ifountain.core.domain.annotations.*;

class UiAction 
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "tab", "events", "menuItems"];
        storageType "File"
    };
    static datasources = ["RCMDB":["keys":["name":["nameInDs":"name"], "isActive":["nameInDs":"isActive"]]]]

    boolean isActive = true;
    String name ="";
    
    String condition ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    UiTab tab ;
    List events = [];
    List menuItems = [];

    
    static relations = [
        tab:[type:UiTab, reverseName:"actions", isMany:false],
        menuItems:[type:UiMenuItem, reverseName:"action", isMany:true],
        events:[type:UiEvent, reverseName:"action", isMany:true]
    ]
    
    static constraints={
    name(blank:false,nullable:false,key:["tab", "isActive"])
        
     condition(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "tab", "events", "menuItems"];
    
    public String toString()
    {
    	return "${getClass().getName()}[name:${getProperty("name")}]";
    }
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}