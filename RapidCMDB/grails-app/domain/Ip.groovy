
import com.ifountain.core.domain.annotations.*;

class Ip extends DeviceComponent
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["hostedBy", "layeredOver", "netMask", "interfaceAdminStatus", "interfaceName", "interfaceOperStatus", "ipStatus", "interfaceKey", "errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = [:]

    
    String ipAddress ="";
    
    String netMask ="";
    
    String interfaceAdminStatus ="";
    
    String interfaceName ="";
    
    String interfaceOperStatus ="";
    
    String ipStatus ="";
    
    String interfaceKey ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    Device hostedBy ;
    
    DeviceInterface layeredOver ;
    

    static hasMany = [:]
    static constraints={
    ipAddress(blank:true,nullable:true)
        
     netMask(blank:true,nullable:true)
        
     interfaceAdminStatus(blank:true,nullable:true)
        
     interfaceName(blank:true,nullable:true)
        
     interfaceOperStatus(blank:true,nullable:true)
        
     ipStatus(blank:true,nullable:true)
        
     interfaceKey(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     hostedBy(nullable:true)
        
     layeredOver(nullable:true)
        
     
    }

    static mappedBy=["hostedBy":"hostsAccessPoints", "layeredOver":"underlying"]
    static belongsTo = []
    static propertyConfiguration= ["netMask":["nameInDs":"NetMask", "datasourceProperty":"smartDs", "lazy":true], "interfaceAdminStatus":["nameInDs":"InterfaceAdminStatus", "datasourceProperty":"smartDs", "lazy":true], "interfaceName":["nameInDs":"InterfaceName", "datasourceProperty":"smartDs", "lazy":true], "interfaceOperStatus":["nameInDs":"InterfaceOperStatus", "datasourceProperty":"smartDs", "lazy":true], "ipStatus":["nameInDs":"IPStatus", "datasourceProperty":"smartDs", "lazy":true], "interfaceKey":["nameInDs":"InterfaceKey", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["netMask", "interfaceAdminStatus", "interfaceName", "interfaceOperStatus", "ipStatus", "interfaceKey", "errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    //AUTO_GENERATED_CODE
}