
import com.ifountain.core.domain.annotations.*;

class RsCard  extends RsComputerSystemComponent {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = [:]

    
    String serialNumber ="";
    
    String standbyStatus ="";
    
    String status ="";
    
    String type ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    
    static relations = [:]    
    
    static constraints={
    serialNumber(blank:true,nullable:true)
        
     standbyStatus(blank:true,nullable:true)
        
     status(blank:true,nullable:true)
        
     type(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    //AUTO_GENERATED_CODE

    
}
