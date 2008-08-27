
import com.ifountain.core.domain.annotations.*;

class RsIpNetwork extends RsSmartsObject
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = [:]

    
    Object connectedSystems ="null";
    
    Object connectedTo ="null";
    
    String netmask ="";
    
    String networkNumber ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    

    static hasMany = [:]
    static constraints={
    connectedSystems(nullable:true)
        
     connectedTo(nullable:true)
        
     netmask(blank:true,nullable:true)
        
     networkNumber(blank:true,nullable:true)
        
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