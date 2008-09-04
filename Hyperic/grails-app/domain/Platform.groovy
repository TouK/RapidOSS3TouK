
import com.ifountain.core.domain.annotations.*;

class Platform  extends Resource {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "hasServers"];
    };
    static datasources = [:]

    
    org.springframework.validation.Errors errors ;
    
    java.lang.Object __operation_class__ ;
    
    static relations = [hasServers:[isMany:true, reverseName:"serverOf", type:Server]]
    static constraints={
    __operation_class__(nullable:true)
        
     errors(nullable:true)
        
     
    }
    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__"];
    
    //AUTO_GENERATED_CODE
    
}
