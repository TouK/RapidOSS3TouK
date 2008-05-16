import com.ifountain.core.domain.annotations.*;


class Ip  extends DeviceComponent {

    //AUTO_GENERATED_CODE

    static searchable = true;
    static datasources = [:]

    
    String interfaceAdminStatus ;
    
    String ipStatus ;
    
    String interfaceName ;
    
    String netMask ;
    
    String ipAddress ;
    
    String interfaceOperStatus ;
    
    Device hostedBy ;
    
    DeviceInterface layeredOver ;
    

    static hasMany = [:]

    

    static constraints={
    interfaceAdminStatus(blank:true,nullable:true)
        
     ipStatus(blank:true,nullable:true)
        
     interfaceName(blank:true,nullable:true)
        
     netMask(blank:true,nullable:true)
        
     ipAddress(blank:false,nullable:false)
        
     interfaceOperStatus(blank:true,nullable:true)
        
     hostedBy(nullable:true)
        
     layeredOver(nullable:true)
        
     
    }

    static mappedBy=["hostedBy":"hostsAccessPoints", "layeredOver":"underlying"]
    static belongsTo = []
    static propertyConfiguration= ["interfaceAdminStatus":["nameInDs":"InterfaceAdminStatus", "datasourceProperty":"smartDs", "lazy":true], "ipStatus":["nameInDs":"IPStatus", "datasourceProperty":"smartDs", "lazy":true], "interfaceName":["nameInDs":"InterfaceName", "datasourceProperty":"smartDs", "lazy":true], "netMask":["nameInDs":"NetMask", "datasourceProperty":"smartDs", "lazy":true], "interfaceOperStatus":["nameInDs":"InterfaceOperStatus", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["interfaceAdminStatus", "ipStatus", "interfaceName", "netMask", "interfaceOperStatus"];
    
    //AUTO_GENERATED_CODE






}
