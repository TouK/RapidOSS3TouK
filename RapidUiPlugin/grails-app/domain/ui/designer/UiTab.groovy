package ui.designer;
import com.ifountain.core.domain.annotations.*;

class UiTab 
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "url", "layout", "components", "dialogs", "actions"];
    
    
        storageType "File"
    
    };
    static datasources = ["RCMDB":["keys":["name":["nameInDs":"name"], "isActive":["nameInDs":"isActive"], "url":["nameInDs":"url"]]]]

    boolean isActive = true;
    String name ="";
    
    String javascriptFile ="";

    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    UiUrl url ;
    
    UiLayout layout ;
    
    List components =[];
    
    List dialogs =[];
    
    List actions =[];
    
    
    static relations = [
    
        url:[type:UiUrl, reverseName:"tabs", isMany:false]
    
        ,layout:[type:UiLayout, reverseName:"tab", isMany:false]
    
        ,components:[type:UiComponent, reverseName:"tab", isMany:true]
    
        ,dialogs:[type:UiDialog, reverseName:"tab", isMany:true]

        ,actions:[type:UiAction, reverseName:"tab", isMany:true]
    
    ]
    
    static constraints={
    name(blank:false,nullable:false,key:["url", "isActive"])
    javascriptFile(blank:true,nullable:true)

     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "url", "layout", "components", "dialogs", "actions"];
    
    public String toString()
    {
    	return getProperty("name");
    }
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}