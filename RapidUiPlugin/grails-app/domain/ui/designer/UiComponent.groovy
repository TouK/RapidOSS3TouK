
package ui.designer;
import com.ifountain.core.domain.annotations.*;

class UiComponent 
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "tab", "layoutUnit", "dialog", "events", "menuItems"];
    
    
        storageType "File"
    
    };
    static datasources = ["RCMDB":["keys":["name":["nameInDs":"name"], "isActive":["nameInDs":"isActive"], "tab":["nameInDs":"tab"]]]]

    boolean isActive = true;
    String name ="";
    
    String title ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    UiTab tab ;
    
    UiLayoutUnit layoutUnit ;
    
    UiDialog dialog ;
    List events = [];
    List menuItems = [];

    
    static relations = [
    
        tab:[type:UiTab, reverseName:"components", isMany:false]
        ,layoutUnit:[type:UiLayoutUnit, reverseName:"component", isMany:false]
        ,dialog:[type:UiDialog, reverseName:"component", isMany:false]
        ,events:[type:UiEvent, reverseName:"component", isMany:true]
        ,menuItems:[type:UiMenuItem, reverseName:"component", isMany:true]

    ]
    
    static constraints={
    name(blank:false,nullable:false,key:["tab", "isActive"])
        
     title(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     layoutUnit(nullable:true)
        
     dialog(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "tab", "layoutUnit", "dialog", "events", "menuItems"];
    
    public String toString()
    {
    	return getProperty("name");
    }
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}