import com.ifountain.core.domain.annotations.*;


class Device extends SmartsObject implements com.ifountain.rcmdb.domain.GeneratedModel
{

    //AUTO_GENERATED_CODE


    static datasources = [:]

    
    String discoveryTime ;
    
    Long discoveredLastAt =0;
    
    String vendor ;
    
    String discoveryErrorInfo ;
    
    String description ;
    
    String model ;
    
    String ipAddress ;
    
    String location ;
    
    String snmpReadCommunity ;
    

    static hasMany = [composedOf:DeviceComponent, connectedVia:Link, hostsAccessPoints:Ip]

    static constraints={
    discoveryTime(blank:true,nullable:true)
        
     discoveredLastAt(blank:false,nullable:false)
        
     vendor(blank:true,nullable:true)
        
     discoveryErrorInfo(blank:true,nullable:true)
        
     description(blank:false,nullable:false)
        
     model(blank:true,nullable:true)
        
     ipAddress(blank:true,nullable:true)
        
     location(blank:true,nullable:true)
        
     snmpReadCommunity(blank:true,nullable:true)
        
     
    }

    static mappedBy=["composedOf":"partOf", "connectedVia":"connectedSystems", "hostsAccessPoints":"hostedBy"]
    static belongsTo = []
    static propertyConfiguration= ["discoveryTime":["nameInDs":"DiscoveryTime", "datasourceProperty":"smartDs", "lazy":true], "discoveredLastAt":["nameInDs":"DiscoveredLastAt", "datasourceProperty":"smartDs", "lazy":true], "discoveryErrorInfo":["nameInDs":"DiscoveryErrorInfo", "datasourceProperty":"smartDs", "lazy":true], "description":["nameInDs":"Description", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["discoveryTime", "discoveredLastAt", "discoveryErrorInfo", "description"];
    
    //AUTO_GENERATED_CODE
}
