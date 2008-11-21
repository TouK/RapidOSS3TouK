
import com.ifountain.core.domain.annotations.*;

class SmartsIp  extends SmartsComputerSystemComponent {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "hostedBy"];
    
    
    };
    static datasources = [:]

    
    String address ="";
    
    String description ="";
    
    String ipStatus ="";
    
    String interfaceAdminStatus ="";
    
    String interfaceMode ="";
    
    String interfaceName ="";
    
    String interfaceOperStatus ="";
    
    String interfaceType ="";
    
    String netmask ="";
    
    String networkNumber ="";
    
    Boolean responsive =false;
    
    String status ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    SmartsComputerSystem hostedBy ;
    
    
    static relations = [
    
        hostedBy:[type:SmartsComputerSystem, reverseName:"hostsAccessPoints", isMany:false]
    
    ]
    
    static constraints={
    address(blank:true,nullable:true)
        
     description(blank:true,nullable:true)
        
     ipStatus(blank:true,nullable:true)
        
     interfaceAdminStatus(blank:true,nullable:true)
        
     interfaceMode(blank:true,nullable:true)
        
     interfaceName(blank:true,nullable:true)
        
     interfaceOperStatus(blank:true,nullable:true)
        
     interfaceType(blank:true,nullable:true)
        
     netmask(blank:true,nullable:true)
        
     networkNumber(blank:true,nullable:true)
        
     responsive(nullable:true)
        
     status(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     hostedBy(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "hostedBy"];
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
    
}
