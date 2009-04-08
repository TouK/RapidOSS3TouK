
import com.ifountain.core.domain.annotations.*;

class RsInMaintenanceSchedule {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    
        storageType "FileAndMemory"
    
    };
    static datasources = ["RCMDB":["keys":["id":["nameInDs":"id"]]]]

    
    String objectName ="";
    
    String info ="";
    
    Date starting =new Date(0);
    
    Date ending =new Date(0);
    
    Boolean active =false;
    
    org.springframework.validation.Errors errors ;
    
    Long id ;
    
    Long version ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    
    static relations = [:]    
    
    static constraints={
    objectName(blank:true,nullable:true)
        
     info(blank:true,nullable:true)
        
     starting(nullable:true)
        
     ending(nullable:true)
        
     active(nullable:true)
        
     errors(nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    public String toString()
    {
    	return "${getClass().getName()}[id:${getProperty("id")}]";
    }
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
    
}
