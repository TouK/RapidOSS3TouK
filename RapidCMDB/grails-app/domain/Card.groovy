
import com.ifountain.core.domain.annotations.*;

class Card extends DeviceComponent
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["realises", "status", "errors", "__operation_class__", "__is_federated_properties_loaded__", "realises"];
    };
    static datasources = [:]

    
    String status ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    List realises =[];
    

    static relations = [realises:[isMany:true, type:DeviceAdapter, reverseName:"realizedBy"]]
    static constraints={
    status(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= ["status":["nameInDs":"Status", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["status", "errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    //AUTO_GENERATED_CODE
}