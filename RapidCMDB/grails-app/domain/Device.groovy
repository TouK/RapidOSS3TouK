import com.ifountain.core.domain.annotations.*;


class Device  extends SmartsObject {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["description", "discoveredLastAt", "discoveryTime", "discoveryErrorInfo"];
    };
    static datasources = [:]

    
    String snmpReadCommunity ;
    
    String description ;
    
    String model ;
    
    Long discoveredLastAt =0;
    
    String location ;
    
    String discoveryTime ;
    
    String discoveryErrorInfo ;
    
    String vendor ;
    
    String ipAddress ;
    

    static hasMany = [composedOf:DeviceComponent, connectedVia:Link, hostsAccessPoints:Ip]
    
    static constraints={
    snmpReadCommunity(blank:true,nullable:true)
        
     description(blank:false,nullable:false)
        
     model(blank:true,nullable:true)
        
     discoveredLastAt(blank:false,nullable:false)
        
     location(blank:true,nullable:true)
        
     discoveryTime(blank:true,nullable:true)
        
     discoveryErrorInfo(blank:true,nullable:true)
        
     vendor(blank:true,nullable:true)
        
     ipAddress(blank:true,nullable:true)
        
     
    }

    static mappedBy=["composedOf":"partOf", "connectedVia":"connectedSystems", "hostsAccessPoints":"hostedBy"]
    static belongsTo = []
    static propertyConfiguration= ["description":["nameInDs":"Description", "datasourceProperty":"smartDs", "lazy":true], "discoveredLastAt":["nameInDs":"DiscoveredLastAt", "datasourceProperty":"smartDs", "lazy":true], "discoveryTime":["nameInDs":"DiscoveryTime", "datasourceProperty":"smartDs", "lazy":true], "discoveryErrorInfo":["nameInDs":"DiscoveryErrorInfo", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["description", "discoveredLastAt", "discoveryTime", "discoveryErrorInfo"];
    
    //AUTO_GENERATED_CODE







}
