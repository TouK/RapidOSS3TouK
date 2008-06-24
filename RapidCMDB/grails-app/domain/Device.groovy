
import com.ifountain.core.domain.annotations.*;

class Device extends SmartsObject
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["discoveryErrorInfo", "discoveryTime", "description", "discoveredLastAt"];
    };
    static datasources = [:]

    
    String ipAddress ="";
    
    String snmpReadCommunity ="";
    
    String discoveryErrorInfo ="";
    
    String location ="";
    
    String model ="";
    
    String vendor ="";
    
    String discoveryTime ="";
    
    String description ="";
    
    Long discoveredLastAt =0;
    

    static hasMany = [hostsAccessPoints:Ip, composedOf:DeviceComponent, connectedVia:Link]
    
    static constraints={
    ipAddress(blank:true,nullable:true)
        
     snmpReadCommunity(blank:true,nullable:true)
        
     discoveryErrorInfo(blank:true,nullable:true)
        
     location(blank:true,nullable:true)
        
     model(blank:true,nullable:true)
        
     vendor(blank:true,nullable:true)
        
     discoveryTime(blank:true,nullable:true)
        
     description(blank:false,nullable:true)
        
     discoveredLastAt(blank:false,nullable:true)
        
     
    }

    static mappedBy=["hostsAccessPoints":"hostedBy", "composedOf":"partOf", "connectedVia":"connectedSystems"]
    static belongsTo = []
    static propertyConfiguration= ["discoveryErrorInfo":["nameInDs":"DiscoveryErrorInfo", "datasourceProperty":"smartDs", "lazy":true], "discoveryTime":["nameInDs":"DiscoveryTime", "datasourceProperty":"smartDs", "lazy":true], "description":["nameInDs":"Description", "datasourceProperty":"smartDs", "lazy":true], "discoveredLastAt":["nameInDs":"DiscoveredLastAt", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["discoveryErrorInfo", "discoveryTime", "description", "discoveredLastAt"];
    
    //AUTO_GENERATED_CODE
}