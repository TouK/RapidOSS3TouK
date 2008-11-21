
import com.ifountain.core.domain.annotations.*;

class SmartsPort  extends SmartsNetworkAdapter {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "partOfVlan"];
    
    
    };
    static datasources = [:]

    
    String designatedBridge ="";
    
    Long designatedPort =0;
    
    Boolean isConnectedToManagedSystem =false;
    
    Boolean isConnectedToSystem =false;
    
    String managedState ="";
    
    String portKey ="";
    
    Long portNumber =0;
    
    String portType ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    List partOfVlan =[];
    
    
    static relations = [
    
        partOfVlan:[type:SmartsVlan, reverseName:"connectedPorts", isMany:true]
    
    ]
    
    static constraints={
    designatedBridge(blank:true,nullable:true)
        
     designatedPort(nullable:true)
        
     isConnectedToManagedSystem(nullable:true)
        
     isConnectedToSystem(nullable:true)
        
     managedState(blank:true,nullable:true)
        
     portKey(blank:true,nullable:true)
        
     portNumber(nullable:true)
        
     portType(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "partOfVlan"];
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
    
}
