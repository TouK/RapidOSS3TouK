
import com.ifountain.core.domain.annotations.*;

class RsIpNetwork  extends RsGroup {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "memberSystems"];
    };
    static datasources = [:]

    
    String netmask ="";
    
    String networkNumber ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    List memberSystems =[];
    
    
    static relations = [
    
        memberSystems:[type:RsComputerSystem, reverseName:"ipNetworks", isMany:true]
    
    ]
    
    static constraints={
    netmask(blank:true,nullable:true)
        
     networkNumber(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "memberSystems"];
    
    //AUTO_GENERATED_CODE


    
}
