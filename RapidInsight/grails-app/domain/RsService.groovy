
import com.ifountain.core.domain.annotations.*;

class RsService {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    
        storageType "FileAndMemory"
    
    };
    static datasources = ["RCMDB":["keys":["name":["nameInDs":"name"]]]]

    
    String name ="";
    
    String status ="";
    
    Long lastChangedAt =0;
    
    Long interval =0;
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    
    static relations = [:]    
    
    static constraints={
    name(blank:false,nullable:false,key:[])
        
     status(blank:true,nullable:true)
        
     lastChangedAt(nullable:true)
        
     interval(nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    public String toString()
    {
    	return "${getClass().getName()}[name:${getProperty("name")}]";
    }
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE


    
}
