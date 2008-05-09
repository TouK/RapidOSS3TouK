import com.ifountain.core.domain.annotations.*;


class Device  extends SmartsObject {

    //AUTO_GENERATED_CODE


    static datasources = [:]

    
    String snmpReadCommunity ;
    
    String vendor ;
    
    String model ;
    
    Long discoveredLastAt =0;
    
    String description ;
    
    String discoveryTime ;
    
    String discoveryErrorInfo ;
    
    String ipAddress ;
    
    String location ;
    

    static hasMany = [composedOf:DeviceComponent, connectedVia:Link, hostsAccessPoints:Ip]

    

    static constraints={
    snmpReadCommunity(blank:true,nullable:true)
        
     vendor(blank:true,nullable:true)
        
     model(blank:true,nullable:true)
        
     discoveredLastAt(blank:false,nullable:false)
        
     description(blank:false,nullable:false)
        
     discoveryTime(blank:true,nullable:true)
        
     discoveryErrorInfo(blank:true,nullable:true)
        
     ipAddress(blank:true,nullable:true)
        
     location(blank:true,nullable:true)
        
     
    }

    static mappedBy=["composedOf":"partOf", "connectedVia":"connectedSystems", "hostsAccessPoints":"hostedBy"]
    static belongsTo = []
    static propertyConfiguration= ["discoveredLastAt":["nameInDs":"DiscoveredLastAt", "datasourceProperty":"smartDs", "lazy":true], "description":["nameInDs":"Description", "datasourceProperty":"smartDs", "lazy":true], "discoveryTime":["nameInDs":"DiscoveryTime", "datasourceProperty":"smartDs", "lazy":true], "discoveryErrorInfo":["nameInDs":"DiscoveryErrorInfo", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["discoveredLastAt", "description", "discoveryTime", "discoveryErrorInfo"];
    
    //AUTO_GENERATED_CODE






}
