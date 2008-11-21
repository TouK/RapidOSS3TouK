
import com.ifountain.core.domain.annotations.*;

class SmartsComputerSystem  extends RsComputerSystem {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "hsrpGroup", "connectedViaVlan", "underlying", "partOf", "ipNetworks", "composedOf", "hostsAccessPoints"];
    
    
    };
    static datasources = [:]

    
    String accessMode ="";
    
    String discoveredFirstAt ="";
    
    String discoveredLastAt ="";
    
    String discoveryErrorInfo ="";
    
    String discoveryTime ="";
    
    Long numberOfIPs =0;
    
    Long numberOfIPv6s =0;
    
    Long numberOfInterfaces =0;
    
    Long numberOfNetworkAdapters =0;
    
    Long numberOfPorts =0;
    
    Boolean supportsSNMP =false;
    
    String managementServer ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    SmartsHSRPGroup hsrpGroup ;
    
    List connectedViaVlan =[];
    
    List underlying =[];
    
    List partOf =[];
    
    List ipNetworks =[];
    
    List composedOf =[];
    
    List hostsAccessPoints =[];
    
    
    static relations = [
    
        hsrpGroup:[type:SmartsHSRPGroup, reverseName:"computerSystems", isMany:false]
    
        ,connectedViaVlan:[type:SmartsVlan, reverseName:"connectedSystems", isMany:true]
    
        ,underlying:[type:SmartsVlan, reverseName:"layeredOver", isMany:true]
    
        ,partOf:[type:SmartsVlan, reverseName:"memberSystems", isMany:true]
    
        ,ipNetworks:[type:SmartsIpNetwork, reverseName:"memberSystems", isMany:true]
    
        ,composedOf:[type:SmartsComputerSystemComponent, reverseName:"partOf", isMany:true]
    
        ,hostsAccessPoints:[type:SmartsIp, reverseName:"hostedBy", isMany:true]
    
    ]
    
    static constraints={
    accessMode(blank:true,nullable:true)
        
     discoveredFirstAt(blank:true,nullable:true)
        
     discoveredLastAt(blank:true,nullable:true)
        
     discoveryErrorInfo(blank:true,nullable:true)
        
     discoveryTime(blank:true,nullable:true)
        
     numberOfIPs(nullable:true)
        
     numberOfIPv6s(nullable:true)
        
     numberOfInterfaces(nullable:true)
        
     numberOfNetworkAdapters(nullable:true)
        
     numberOfPorts(nullable:true)
        
     supportsSNMP(nullable:true)
        
     managementServer(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     hsrpGroup(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "hsrpGroup", "connectedViaVlan", "underlying", "partOf", "ipNetworks", "composedOf", "hostsAccessPoints"];
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
    
}
