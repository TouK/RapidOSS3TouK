package ui.designer;
import com.ifountain.core.domain.annotations.*;

class UiUrl 
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "tabs"];
    
    
        storageType "FileAndMemory"
    
    };
    static datasources = ["RCMDB":["keys":["url":["nameInDs":"url"]]]]

    
    String url ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    List tabs =[];
    
    
    static relations = [
    
        tabs:[type:UiTab, reverseName:"url", isMany:true]
    
    ]
    
    static constraints={
    url(blank:false,nullable:false,key:[])
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "tabs"];
    
    public String toString()
    {
    	return getProperty("url");
    }
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}