
import com.ifountain.core.domain.annotations.*;

class OpenNmsService extends OpenNmsObject
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "ipInterface"];
    
    
    };
    static datasources = [:]

    
    String serviceName ="";
    
    Date lastGoodAt =new Date(0);
    
    Date lastFailedAt =new Date(0);
    
    String qualifier ="";
    
    String status ="";
    
    String source ="";
    
    String notify ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    OpenNmsIpInterface ipInterface ;
    
    
    static relations = [
    
        ipInterface:[type:OpenNmsIpInterface, reverseName:"services", isMany:false]
    
    ]
    
    static constraints={
    serviceName(blank:true,nullable:true)
        
     lastGoodAt(nullable:true)
        
     lastFailedAt(nullable:true)
        
     qualifier(blank:true,nullable:true)
        
     status(blank:true,nullable:true)
        
     source(blank:true,nullable:true)
        
     notify(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     ipInterface(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "ipInterface"];
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}