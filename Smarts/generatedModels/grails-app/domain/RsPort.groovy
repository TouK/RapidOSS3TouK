
import com.ifountain.core.domain.annotations.*;

class RsPort  extends RsNetworkAdapter {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
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
    

    static hasMany = [:]
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

    static mappedBy=[:]
    static belongsTo = []
    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    //AUTO_GENERATED_CODE
    
}
