
import com.ifountain.core.domain.annotations.*;

class RsInterface extends RsNetworkAdapter
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
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
    

    static hasMany = [:]
    static constraints={
    interfaceKey(blank:true,nullable:true)
        
     hasIPAddresses(nullable:true)
        
     hasIPv6Addresses(nullable:true)
        
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