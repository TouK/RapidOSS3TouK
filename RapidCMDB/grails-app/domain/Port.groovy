
import com.ifountain.core.domain.annotations.*;

class Port extends DeviceAdapter
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["portNumber", "portKey", "errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = [:]

    
    String portType ="";
    
    String portNumber ="";
    
    String portKey ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    static relations  =[:]
    static constraints={
    portType(blank:true,nullable:true)
        
     portNumber(blank:true,nullable:true)
        
     portKey(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= ["portNumber":["nameInDs":"PortNumber", "datasourceProperty":"smartDs", "lazy":false], "portKey":["nameInDs":"PortKey", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["portNumber", "portKey", "errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    //AUTO_GENERATED_CODE
}