
import com.ifountain.core.domain.annotations.*;

class SmartsHsrpGroup extends RsTopologyObject
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    
    };
    static datasources = [:]

    
    String activeInterfaceName ="";
    
    String activeSystemName ="";
    
    Long atRiskThreshold =0;
    
    String groupNumber ="";
    
    Boolean hsrpEpStateChanged =false;
    
    Boolean isAnyComponentDown =false;
    
    Boolean isAnyHSRPEndpointActive =false;
    
    Boolean isEveryComponentDown =false;
    
    Boolean isEveryHSRPEndpointReady =false;
    
    Boolean isGroupPartOfSingleUnresponsiveSystem =false;
    
    Boolean isVirtualIPUnresponsive =false;
    
    Long numberOfComponents =0;
    
    Long numberOfFaultyComponents =0;
    
    String virtualIP ="";
    
    String virtualMAC ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    
    static relations = [:]    
    
    static constraints={
    activeInterfaceName(blank:true,nullable:true)
        
     activeSystemName(blank:true,nullable:true)
        
     atRiskThreshold(nullable:true)
        
     groupNumber(blank:true,nullable:true)
        
     hsrpEpStateChanged(nullable:true)
        
     isAnyComponentDown(nullable:true)
        
     isAnyHSRPEndpointActive(nullable:true)
        
     isEveryComponentDown(nullable:true)
        
     isEveryHSRPEndpointReady(nullable:true)
        
     isGroupPartOfSingleUnresponsiveSystem(nullable:true)
        
     isVirtualIPUnresponsive(nullable:true)
        
     numberOfComponents(nullable:true)
        
     numberOfFaultyComponents(nullable:true)
        
     virtualIP(blank:true,nullable:true)
        
     virtualMAC(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}