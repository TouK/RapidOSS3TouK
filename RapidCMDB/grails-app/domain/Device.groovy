
import com.ifountain.core.domain.annotations.*;

class Device extends SmartsObject
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["description", "discoveryErrorInfo", "discoveryTime", "discoveredLastAt"];
    };
    static datasources = [:]

    
    String location ="";
    
    String snmpReadCommunity ="";
    
    String description ="";
    
    String ipAddress ="";
    
    String discoveryErrorInfo ="";
    
    String discoveryTime ="";
    
    String model ="";
    
    Long discoveredLastAt =0;
    
    String vendor ="";
    

    static hasMany = [connectedVia:Link, composedOf:DeviceComponent, hostsAccessPoints:Ip]
    
    static constraints={
    location(blank:true,nullable:true)
        
     snmpReadCommunity(blank:true,nullable:true)
        
     description(blank:true,nullable:true)
        
     ipAddress(blank:true,nullable:true)
        
     discoveryErrorInfo(blank:true,nullable:true)
        
     discoveryTime(blank:true,nullable:true)
        
     model(blank:true,nullable:true)
        
     discoveredLastAt(blank:true,nullable:true)
        
     vendor(blank:true,nullable:true)
        
     
    }

    static mappedBy=["connectedVia":"connectedSystems", "composedOf":"partOf", "hostsAccessPoints":"hostedBy"]
    static belongsTo = []
    static propertyConfiguration= ["description":["nameInDs":"Description", "datasourceProperty":"smartDs", "lazy":true], "discoveryErrorInfo":["nameInDs":"DiscoveryErrorInfo", "datasourceProperty":"smartDs", "lazy":true], "discoveryTime":["nameInDs":"DiscoveryTime", "datasourceProperty":"smartDs", "lazy":true], "discoveredLastAt":["nameInDs":"DiscoveredLastAt", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["description", "discoveryErrorInfo", "discoveryTime", "discoveredLastAt"];
    
    //AUTO_GENERATED_CODE
}