
import com.ifountain.core.domain.annotations.*;

class Ip extends DeviceComponent
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["interfaceOperStatus", "netMask", "interfaceAdminStatus", "ipStatus", "interfaceName"];
    };
    static datasources = [:]

    
    String interfaceOperStatus ="";
    
    String netMask ="";
    
    String ipAddress ="";
    
    String interfaceAdminStatus ="";
    
    String ipStatus ="";
    
    String interfaceName ="";
    
    DeviceInterface layeredOver ;
    
    Device hostedBy ;
    

    static hasMany = [:]
    
    static constraints={
    interfaceOperStatus(blank:true,nullable:true)
        
     netMask(blank:true,nullable:true)
        
     ipAddress(blank:true,nullable:true)
        
     interfaceAdminStatus(blank:true,nullable:true)
        
     ipStatus(blank:true,nullable:true)
        
     interfaceName(blank:true,nullable:true)
        
     layeredOver(nullable:true)
        
     hostedBy(nullable:true)
        
     
    }

    static mappedBy=["layeredOver":"underlying", "hostedBy":"hostsAccessPoints"]
    static belongsTo = []
    static propertyConfiguration= ["interfaceOperStatus":["nameInDs":"InterfaceOperStatus", "datasourceProperty":"smartDs", "lazy":true], "netMask":["nameInDs":"NetMask", "datasourceProperty":"smartDs", "lazy":true], "interfaceAdminStatus":["nameInDs":"InterfaceAdminStatus", "datasourceProperty":"smartDs", "lazy":true], "ipStatus":["nameInDs":"IPStatus", "datasourceProperty":"smartDs", "lazy":true], "interfaceName":["nameInDs":"InterfaceName", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["interfaceOperStatus", "netMask", "interfaceAdminStatus", "ipStatus", "interfaceName"];
    
    //AUTO_GENERATED_CODE
}