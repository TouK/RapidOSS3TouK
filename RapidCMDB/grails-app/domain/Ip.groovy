
import com.ifountain.core.domain.annotations.*;

class Ip extends DeviceComponent
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["ipStatus", "interfaceName", "interfaceOperStatus", "interfaceAdminStatus", "netMask"];
    };
    static datasources = [:]

    
    String ipStatus ="";
    
    String interfaceName ="";
    
    String interfaceOperStatus ="";
    
    String interfaceAdminStatus ="";
    
    String netMask ="";
    
    String ipAddress ="";
    
    DeviceInterface layeredOver ;
    
    Device hostedBy ;
    

    static hasMany = [:]
    
    static constraints={
    ipStatus(blank:true,nullable:true)
        
     interfaceName(blank:true,nullable:true)
        
     interfaceOperStatus(blank:true,nullable:true)
        
     interfaceAdminStatus(blank:true,nullable:true)
        
     netMask(blank:true,nullable:true)
        
     ipAddress(blank:false,nullable:true)
        
     layeredOver(nullable:true)
        
     hostedBy(nullable:true)
        
     
    }

    static mappedBy=["layeredOver":"underlying", "hostedBy":"hostsAccessPoints"]
    static belongsTo = []
    static propertyConfiguration= ["ipStatus":["nameInDs":"IPStatus", "datasourceProperty":"smartDs", "lazy":true], "interfaceName":["nameInDs":"InterfaceName", "datasourceProperty":"smartDs", "lazy":true], "interfaceOperStatus":["nameInDs":"InterfaceOperStatus", "datasourceProperty":"smartDs", "lazy":true], "interfaceAdminStatus":["nameInDs":"InterfaceAdminStatus", "datasourceProperty":"smartDs", "lazy":true], "netMask":["nameInDs":"NetMask", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["ipStatus", "interfaceName", "interfaceOperStatus", "interfaceAdminStatus", "netMask"];
    
    //AUTO_GENERATED_CODE
}