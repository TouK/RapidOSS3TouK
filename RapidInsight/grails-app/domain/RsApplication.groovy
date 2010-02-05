
import com.ifountain.core.domain.annotations.*;

class RsApplication extends RsTopologyObject
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "hostedBy"];
    
    
    };
    static datasources = [:]

    
    org.springframework.validation.Errors errors ;
    
    RsComputerSystem hostedBy ;
    
    
    static relations = [
    
        hostedBy:[type:RsComputerSystem, reverseName:"hostsServices", isMany:false]
    
    ]
    
    static constraints={
    errors(nullable:true)
        
     hostedBy(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "hostedBy"];
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}