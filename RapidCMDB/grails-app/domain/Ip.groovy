import com.ifountain.core.domain.annotations.*;


class Ip extends DeviceComponent implements com.ifountain.rcmdb.domain.GeneratedModel
{

    //AUTO_GENERATED_CODE


    static datasources = [:]

    
    String netMask ;
    
    String interfaceName ;
    
    String interfaceOperStatus ;
    
    String ipAddress ;
    
    String interfaceAdminStatus ;
    
    String ipStatus ;
    
    Device hostedBy ;
    
    DeviceInterface layeredOver ;
    

    static hasMany = [:]

    static constraints={
    netMask(blank:true,nullable:true)
        
     interfaceName(blank:true,nullable:true)
        
     interfaceOperStatus(blank:true,nullable:true)
        
     ipAddress(blank:false,nullable:false)
        
     interfaceAdminStatus(blank:true,nullable:true)
        
     ipStatus(blank:true,nullable:true)
        
     hostedBy(nullable:true)
        
     layeredOver(nullable:true)
        
     
    }

    static mappedBy=["hostedBy":"hostsAccessPoints", "layeredOver":"underlying"]
    static belongsTo = []
    static propertyConfiguration= ["netMask":["nameInDs":"NetMask", "datasourceProperty":"smartDs", "lazy":true], "interfaceName":["nameInDs":"InterfaceName", "datasourceProperty":"smartDs", "lazy":true], "interfaceOperStatus":["nameInDs":"InterfaceOperStatus", "datasourceProperty":"smartDs", "lazy":true], "interfaceAdminStatus":["nameInDs":"InterfaceAdminStatus", "datasourceProperty":"smartDs", "lazy":true], "ipStatus":["nameInDs":"IPStatus", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["netMask", "interfaceName", "interfaceOperStatus", "interfaceAdminStatus", "ipStatus"];
    
    //AUTO_GENERATED_CODE
}
