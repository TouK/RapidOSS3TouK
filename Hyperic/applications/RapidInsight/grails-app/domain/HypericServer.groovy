
import com.ifountain.core.domain.annotations.*;

class HypericServer extends HypericResource
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "platform", "services"];
    
    
    };
    static datasources = [:]

    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    HypericPlatform platform ;
    
    List services =[];
    
    
    static relations = [
    
        platform:[type:HypericPlatform, reverseName:"servers", isMany:false]
    
        ,services:[type:HypericService, reverseName:"server", isMany:true]
    
    ]
    
    static constraints={
    __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     platform(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "platform", "services"];
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}