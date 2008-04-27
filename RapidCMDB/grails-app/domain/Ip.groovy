import com.ifountain.core.domain.annotations.*;


class Ip extends DeviceComponent
{

    //AUTO_GENERATED_CODE


    static datasources = [:]

    
    String interfaceOperStatus ;
    
    String ipStatus ;
    
    String interfaceName ;
    
    String interfaceAdminStatus ;
    
    String netMask ;
    
    String ipAddress ;
    
    Device hostedBy ;
    
    DeviceInterface layeredOver ;
    

    static hasMany = [:]

    static constraints={
    hostedBy(nullable:true)

     interfaceAdminStatus(blank:true,nullable:true)

     interfaceName(blank:true,nullable:true)

     interfaceOperStatus(blank:true,nullable:true)

     ipAddress(blank:false,nullable:false)

     ipStatus(blank:true,nullable:true)

     layeredOver(nullable:true)

     netMask(blank:true,nullable:true)

     
    }

    static mappedBy=["hostedBy":"hostsAccessPoints", "layeredOver":"underlying"]
    static belongsTo = []
    static propertyConfiguration= ["interfaceOperStatus":["nameInDs":"InterfaceOperStatus", "datasourceProperty":"smartDs", "lazy":true], "ipStatus":["nameInDs":"IPStatus", "datasourceProperty":"smartDs", "lazy":true], "interfaceName":["nameInDs":"InterfaceName", "datasourceProperty":"smartDs", "lazy":true], "interfaceAdminStatus":["nameInDs":"InterfaceAdminStatus", "datasourceProperty":"smartDs", "lazy":true], "netMask":["nameInDs":"NetMask", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["interfaceOperStatus", "ipStatus", "interfaceName", "interfaceAdminStatus", "netMask"];
    
    //AUTO_GENERATED_CODE    
}
