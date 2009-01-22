package ui.designer;
import com.ifountain.core.domain.annotations.*;

class UiJavaScript 
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "tab"];
    
    
        storageType "FileAndMemory"
    
    };
    static datasources = ["RCMDB":["keys":["id":["nameInDs":"id"]]]]

    
    String content ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    UiTab tab ;
    
    
    static relations = [
    
        tab:[type:UiTab, reverseName:"javascript", isMany:false]
    
    ]
    
    static constraints={
    content(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     tab(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "tab"];
    
    public String toString()
    {
    	return "${getClass().getName()}[id:${getProperty("id")}]";
    }
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}