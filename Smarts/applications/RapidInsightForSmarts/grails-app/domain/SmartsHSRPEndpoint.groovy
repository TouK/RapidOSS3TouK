
import com.ifountain.core.domain.annotations.*;

class SmartsHSRPEndpoint  extends SmartsComputerSystemComponent {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "hsrpGroup"];
    
    
    };
    static datasources = [:]

    
    String hsrpEndpointKey ="";
    
    String isReady ="";
    
    String isSwitchOverActive ="";
    
    String groupNumber ="";
    
    String numberOfComponents ="";
    
    String numberOfFaultyComponents ="";
    
    String virtualIP ="";
    
    String virtualMAC ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    SmartsHSRPGroup hsrpGroup ;
    
    
    static relations = [
    
        hsrpGroup:[type:SmartsHSRPGroup, reverseName:"endPoints", isMany:false]
    
    ]
    
    static constraints={
    hsrpEndpointKey(blank:true,nullable:true)
        
     isReady(blank:true,nullable:true)
        
     isSwitchOverActive(blank:true,nullable:true)
        
     groupNumber(blank:true,nullable:true)
        
     numberOfComponents(blank:true,nullable:true)
        
     numberOfFaultyComponents(blank:true,nullable:true)
        
     virtualIP(blank:true,nullable:true)
        
     virtualMAC(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     hsrpGroup(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "hsrpGroup"];
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
    
}
