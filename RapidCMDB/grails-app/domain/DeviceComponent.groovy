
import com.ifountain.core.domain.annotations.*;

class DeviceComponent extends SmartsObject
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["partOf", "errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = [:]

    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    Device partOf ;
    

    static hasMany = [:]
    static constraints={
    __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     partOf(nullable:true)
        
     
    }

    static mappedBy=["partOf":"composedOf"]
    static belongsTo = []
    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    //AUTO_GENERATED_CODE
}