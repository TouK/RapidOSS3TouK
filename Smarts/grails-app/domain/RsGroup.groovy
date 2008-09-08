
import com.ifountain.core.domain.annotations.*;

class RsGroup  extends RsSmartsObject {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "consistsOf"];
    };
    static datasources = [:]

    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    List consistsOf =[];
    
    
    static relations = [
    
        consistsOf:[type:RsSmartsObject, reverseName:"memberOfGroup", isMany:true]
    
    ]
    
    static constraints={
    __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "consistsOf"];
    
    //AUTO_GENERATED_CODE


    
}
