package ui.designer;
import com.ifountain.core.domain.annotations.*;

class UiTab 
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "url", "layout", "components", "javascript", "dialogs", "forms", "actions"];
    
    
        storageType "FileAndMemory"
    
    };
    static datasources = ["RCMDB":["keys":["name":["nameInDs":"name"]]]]

    
    String name ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    UiUrl url ;
    
    UiLayout layout ;
    
    List components =[];
    
    UiJavaScript javascript ;
    
    List dialogs =[];
    
    List forms =[];
    
    List actions =[];
    
    
    static relations = [
    
        url:[type:UiUrl, reverseName:"tabs", isMany:false]
    
        ,layout:[type:UiLayout, reverseName:"tab", isMany:false]
    
        ,components:[type:UiComponent, reverseName:"tab", isMany:true]
    
        ,javascript:[type:UiJavaScript, reverseName:"tab", isMany:false]
    
        ,dialogs:[type:UiDialog, reverseName:"tab", isMany:true]
    
        ,forms:[type:UiForm, reverseName:"tab", isMany:true]
    
        ,actions:[type:UiAction, reverseName:"tab", isMany:true]
    
    ]
    
    static constraints={
    name(blank:false,nullable:false,key:["url"])
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     javascript(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "url", "layout", "components", "javascript", "dialogs", "forms", "actions"];
    
    public String toString()
    {
    	return getProperty("name");
    }
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}