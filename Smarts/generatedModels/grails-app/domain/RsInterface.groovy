
import com.ifountain.core.domain.annotations.*;

class RsInterface  extends RsNetworkAdapter {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "underlying", "layeredOver"];
    };
    static datasources = [:]

    
    String interfaceKey ="";
    
    Boolean hasIPAddresses =false;
    
    Boolean hasIPv6Addresses =false;
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    List underlying =[];
    
    List layeredOver =[];
    
    
    static relations = [
    
        underlying:[type:RsInterface, reverseName:"layeredOver", isMany:true]
    
        ,layeredOver:[type:RsInterface, reverseName:"underlying", isMany:true]
    
    ]
    
    static constraints={
    interfaceKey(blank:true,nullable:true)
        
     hasIPAddresses(nullable:true)
        
     hasIPv6Addresses(nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "underlying", "layeredOver"];
    
    //AUTO_GENERATED_CODE

    
}
