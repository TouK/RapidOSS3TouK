import com.ifountain.core.domain.annotations.*;


class Device extends SmartsObject implements com.ifountain.rcmdb.domain.GeneratedModel
{

    //AUTO_GENERATED_CODE


    static datasources = [:]

    
    String ipAddress ;
    
    String discoveryErrorInfo ;
    
    String description ;
    
    String location ;
    
    String snmpReadCommunity ;
    
    String vendor ;
    
    String model ;
    
    Long discoveredLastAt =0;
    
    String discoveryTime ;
    

    static hasMany = [composedOf:DeviceComponent, connectedVia:Link, hostsAccessPoints:Ip]

    static constraints={
    description(blank:false,nullable:false)

     discoveredLastAt(blank:false,nullable:false)

     discoveryErrorInfo(blank:true,nullable:true)

     discoveryTime(blank:true,nullable:true)

     ipAddress(blank:true,nullable:true)

     location(blank:true,nullable:true)

     model(blank:true,nullable:true)

     snmpReadCommunity(blank:true,nullable:true)

     vendor(blank:true,nullable:true)

     
    }

    static mappedBy=["composedOf":"partOf", "connectedVia":"connectedSystems", "hostsAccessPoints":"hostedBy"]
    static belongsTo = []
    static propertyConfiguration= ["discoveryErrorInfo":["nameInDs":"DiscoveryErrorInfo", "datasourceProperty":"smartDs", "lazy":true], "description":["nameInDs":"Description", "datasourceProperty":"smartDs", "lazy":true], "discoveredLastAt":["nameInDs":"DiscoveredLastAt", "datasourceProperty":"smartDs", "lazy":true], "discoveryTime":["nameInDs":"DiscoveryTime", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["discoveryErrorInfo", "description", "discoveredLastAt", "discoveryTime"];
    
    //AUTO_GENERATED_CODE    
}
