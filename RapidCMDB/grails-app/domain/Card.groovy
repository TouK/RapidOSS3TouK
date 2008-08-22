
import com.ifountain.core.domain.annotations.*;

class Card extends DeviceComponent
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["status", "errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = [:]

    
    String status ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    List realises =[];
    

    static hasMany = [realises:DeviceAdapter]
    static constraints={
    status(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static mappedBy=["realises":"realizedBy"]
    static belongsTo = []
    static propertyConfiguration= ["status":["nameInDs":"Status", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["status", "errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    //AUTO_GENERATED_CODE
}