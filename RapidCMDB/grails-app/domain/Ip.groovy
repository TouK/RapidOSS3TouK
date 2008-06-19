class Ip  extends DeviceComponent {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["ipStatus", "netMask", "interfaceAdminStatus", "interfaceName", "interfaceOperStatus"];
    };
    static datasources = [:]

    
    String ipStatus ;
    
    String netMask ;
    
    String interfaceAdminStatus ;
    
    String interfaceName ;
    
    String interfaceOperStatus ;
    
    String ipAddress ;
    
    Device hostedBy ;
    
    DeviceInterface layeredOver ;
    

    static hasMany = [:]
    
    static constraints={
    ipStatus(blank:true,nullable:true)
        
     netMask(blank:true,nullable:true)
        
     interfaceAdminStatus(blank:true,nullable:true)
        
     interfaceName(blank:true,nullable:true)
        
     interfaceOperStatus(blank:true,nullable:true)
        
     ipAddress(blank:false,nullable:false)
        
     hostedBy(nullable:true)
        
     layeredOver(nullable:true)
        
     
    }

    static mappedBy=["hostedBy":"hostsAccessPoints", "layeredOver":"underlying"]
    static belongsTo = []
    static propertyConfiguration= ["ipStatus":["nameInDs":"IPStatus", "datasourceProperty":"smartDs", "lazy":true], "netMask":["nameInDs":"NetMask", "datasourceProperty":"smartDs", "lazy":true], "interfaceAdminStatus":["nameInDs":"InterfaceAdminStatus", "datasourceProperty":"smartDs", "lazy":true], "interfaceName":["nameInDs":"InterfaceName", "datasourceProperty":"smartDs", "lazy":true], "interfaceOperStatus":["nameInDs":"InterfaceOperStatus", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["ipStatus", "netMask", "interfaceAdminStatus", "interfaceName", "interfaceOperStatus"];
    
    //AUTO_GENERATED_CODE







}
