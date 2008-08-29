
import com.ifountain.core.domain.annotations.*;

class RsComputerSystem  extends RsSmartsObject {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = [:]

    
    String accessMode ="";
    
    String discoveredFirstAt ="";
    
    String discoveredLastAt ="";
    
    String discoveryErrorInfo ="";
    
    String discoveryTime ="";
    
    String location ="";
    
    String model ="";
    
    Long numberOfIPs =0;
    
    Long numberOfIPv6s =0;
    
    Long numberOfInterfaces =0;
    
    Long numberOfNetworkAdapters =0;
    
    Long numberOfPorts =0;
    
    String osVersion ="";
    
    String primaryOwnerContact ="";
    
    String primaryOwnerName ="";
    
    String readCommunity ="";
    
    String snmpAddress ="";
    
    Boolean supportsSNMP =false;
    
    String systemName ="";
    
    String systemObjectID ="";
    
    String vendor ="";
    
    String managementServer ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    

    static hasMany = [:]
    static constraints={
    accessMode(blank:true,nullable:true)
        
     discoveredFirstAt(blank:true,nullable:true)
        
     discoveredLastAt(blank:true,nullable:true)
        
     discoveryErrorInfo(blank:true,nullable:true)
        
     discoveryTime(blank:true,nullable:true)
        
     location(blank:true,nullable:true)
        
     model(blank:true,nullable:true)
        
     numberOfIPs(nullable:true)
        
     numberOfIPv6s(nullable:true)
        
     numberOfInterfaces(nullable:true)
        
     numberOfNetworkAdapters(nullable:true)
        
     numberOfPorts(nullable:true)
        
     osVersion(blank:true,nullable:true)
        
     primaryOwnerContact(blank:true,nullable:true)
        
     primaryOwnerName(blank:true,nullable:true)
        
     readCommunity(blank:true,nullable:true)
        
     snmpAddress(blank:true,nullable:true)
        
     supportsSNMP(nullable:true)
        
     systemName(blank:true,nullable:true)
        
     systemObjectID(blank:true,nullable:true)
        
     vendor(blank:true,nullable:true)
        
     managementServer(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static mappedBy=[:]
    static belongsTo = []
    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    //AUTO_GENERATED_CODE
    
}
