package ui.designer;
import com.ifountain.core.domain.annotations.*;

class UiWebPage
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "tabs"];
    
    
        storageType "FileAndMemory"
    
    };
    static datasources = ["RCMDB":["keys":["name":["nameInDs":"name"], "isActive":["nameInDs":"isActive"]]]]

    boolean isActive = true;
    String name ="";
    
    Long id ;
    
    Long version ;

    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    List tabs =[];
    
    
    static relations = [
    
        tabs:[type:UiTab, reverseName:"webPage", isMany:true]
    
    ]
    
    static constraints={
        name(blank:false,nullable:false,key:["isActive"], matches:"[a-z_A-z]\\w*")
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "tabs"];
    
    public String toString()
    {
    	return getProperty("name");
    }
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}