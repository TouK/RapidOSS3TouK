
import com.ifountain.core.domain.annotations.*;

class Device extends SmartsObject
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["connectedVia",  "hostsAccessPoints", "composedOf", "errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = [:]

    
    String location ="";
    
    String model ="";
    
    String snmpReadCommunity ="";
    
    String vendor ="";
    
    String discoveredLastAt ="0";
    
    String description ="";
    
    String discoveryErrorInfo ="";
    
    String discoveryTime ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    List connectedVia =[];
    
    List hostsAccessPoints =[];
    
    List composedOf =[];
    static relations = [connectedVia:[isMany:true, type:Link, reverseName:"connectedSystems"],
        hostsAccessPoints:[isMany:true, type:Ip, reverseName:"hostedBy"],
        composedOf:[isMany:true, type:DeviceComponent, reverseName:"partOf"]

    ]
    static constraints={
    location(blank:true,nullable:true)
        
     model(blank:true,nullable:true)
        
     snmpReadCommunity(blank:true,nullable:true)
        
     vendor(blank:true,nullable:true)
        
     discoveredLastAt(blank:true,nullable:true)
        
     description(blank:true,nullable:true)
        
     discoveryErrorInfo(blank:true,nullable:true)
        
     discoveryTime(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }
    
    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    //AUTO_GENERATED_CODE
}