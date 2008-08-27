
import com.ifountain.core.domain.annotations.*;

class RsIp extends RsComputerSystemComponent
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = [:]

    
    String address ="";
    
    String creationClassName ="";
    
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
    

    static hasMany = [:]
    static constraints={
    address(blank:true,nullable:true)
        
     creationClassName(blank:true,nullable:true)
        
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
        
     
    }

    static mappedBy=[:]
    static belongsTo = []
    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    //AUTO_GENERATED_CODE
}