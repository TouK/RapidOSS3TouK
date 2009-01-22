package ui.designer;
import com.ifountain.core.domain.annotations.*;

class UiForm 
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "tab"];
    
    
        storageType "FileAndMemory"
    
    };
    static datasources = ["RCMDB":["keys":["name":["nameInDs":"name"]]]]

    
    String name ="";
    
    Long width =0;
    
    String htmlContent ="";
    
    String saveUrl ="";
    
    String createUrl ="";
    
    String editUrl ="";
    
    String updateUrl ="";
    
    String submitAction ="GET";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    UiTab tab ;
    
    
    static relations = [
    
        tab:[type:UiTab, reverseName:"forms", isMany:false]
    
    ]
    
    static constraints={
    name(blank:false,nullable:false,key:["tab"])
        
     width(nullable:true)
        
     htmlContent(blank:true,nullable:true)
        
     saveUrl(blank:true,nullable:true)
        
     createUrl(blank:true,nullable:true)
        
     editUrl(blank:true,nullable:true)
        
     updateUrl(blank:true,nullable:true)
        
     submitAction(inList:["GET", "POST"])
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "tab"];
    
    public String toString()
    {
    	return getProperty("name");
    }
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}