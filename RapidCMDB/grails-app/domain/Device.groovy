import com.ifountain.core.domain.annotations.*;


class Device extends SmartsObject implements com.ifountain.domain.GeneratedModel
{

    //AUTO_GENERATED_CODE


    static datasources = [:]

    
    Long discoveredLastAt =0;
    
    String snmpReadCommunity ;
    
    String ipAddress ;
    
    String location ;
    
    String model ;
    
    String discoveryErrorInfo ;
    
    String description ;
    
    String discoveryTime ;
    
    String vendor ;
    

    static hasMany = [composedOf:DeviceComponent, connectedVia:Link, hostsAccessPoints:Ip]

    static constraints={
    discoveredLastAt(blank:false,nullable:false)
        
     snmpReadCommunity(blank:true,nullable:true)
        
     ipAddress(blank:true,nullable:true)
        
     location(blank:true,nullable:true)
        
     model(blank:true,nullable:true)
        
     discoveryErrorInfo(blank:true,nullable:true)
        
     description(blank:false,nullable:false)
        
     discoveryTime(blank:true,nullable:true)
        
     vendor(blank:true,nullable:true)
        
     
    }

    static mappedBy=["composedOf":"partOf", "connectedVia":"connectedSystems", "hostsAccessPoints":"hostedBy"]
    static belongsTo = []
    static propertyConfiguration= ["discoveredLastAt":["nameInDs":"DiscoveredLastAt", "datasourceProperty":"smartDs", "lazy":true], "discoveryErrorInfo":["nameInDs":"DiscoveryErrorInfo", "datasourceProperty":"smartDs", "lazy":true], "description":["nameInDs":"Description", "datasourceProperty":"smartDs", "lazy":true], "discoveryTime":["nameInDs":"DiscoveryTime", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["discoveredLastAt", "discoveryErrorInfo", "description", "discoveryTime"];
    
    //AUTO_GENERATED_CODE    
}
