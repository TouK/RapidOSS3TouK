import com.ifountain.core.domain.annotations.*;


class Device extends SmartsObject implements com.ifountain.rcmdb.domain.GeneratedModel
{

    //AUTO_GENERATED_CODE


    static datasources = [:]

    
    String ipAddress ;
    
    String location ;
    
    String discoveryTime ;
    
    String vendor ;
    
    String model ;
    
    String description ;
    
    String discoveryErrorInfo ;
    
    Long discoveredLastAt =0;
    
    String snmpReadCommunity ;
    

    static hasMany = [composedOf:DeviceComponent, connectedVia:Link, hostsAccessPoints:Ip]

    static constraints={
    ipAddress(blank:true,nullable:true)
        
     location(blank:true,nullable:true)
        
     discoveryTime(blank:true,nullable:true)
        
     vendor(blank:true,nullable:true)
        
     model(blank:true,nullable:true)
        
     description(blank:false,nullable:false)
        
     discoveryErrorInfo(blank:true,nullable:true)
        
     discoveredLastAt(blank:false,nullable:false)
        
     snmpReadCommunity(blank:true,nullable:true)
        
     
    }

    static mappedBy=["composedOf":"partOf", "connectedVia":"connectedSystems", "hostsAccessPoints":"hostedBy"]
    static belongsTo = []
    static propertyConfiguration= ["discoveryTime":["nameInDs":"DiscoveryTime", "datasourceProperty":"smartDs", "lazy":true], "description":["nameInDs":"Description", "datasourceProperty":"smartDs", "lazy":true], "discoveryErrorInfo":["nameInDs":"DiscoveryErrorInfo", "datasourceProperty":"smartDs", "lazy":true], "discoveredLastAt":["nameInDs":"DiscoveredLastAt", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["discoveryTime", "description", "discoveryErrorInfo", "discoveredLastAt"];
    
    //AUTO_GENERATED_CODE
}
