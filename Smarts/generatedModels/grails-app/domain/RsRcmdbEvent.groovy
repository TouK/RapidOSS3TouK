
import com.ifountain.core.domain.annotations.*;

class RsRcmdbEvent extends RsEvent
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = [:]

    
    Long count =1;
    
    String eventName ="";
    
    Date firstNotifiedAt =new Date(0);
    
    Date lastNotifiedAt =new Date(0);
    
    Date lastChangedAt =new Date(0);
    
    Date lastClearedAt =new Date(0);
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    
    static relations = [:]    
    
    static constraints={
    count(nullable:true)
        
     eventName(blank:true,nullable:true)
        
     firstNotifiedAt(nullable:true)
        
     lastNotifiedAt(nullable:true)
        
     lastChangedAt(nullable:true)
        
     lastClearedAt(nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    //AUTO_GENERATED_CODE
}