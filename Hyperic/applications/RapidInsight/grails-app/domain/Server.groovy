
import com.ifountain.core.domain.annotations.*;

class Server  extends Resource {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "hasServices", "serverOf"];
    };
    static datasources = [:]

    
    org.springframework.validation.Errors errors ;
    
    java.lang.Object __operation_class__ ;
    
    Platform serverOf ;
    
    static relations = [hasServices:[isMany:true, reverseName:"serviceOf", type:Service]
            ,serverOf:[isMany:false, type:Platform, reverseName:"hasServers"]
    ]
    static constraints={
    __operation_class__(nullable:true)
        
     errors(nullable:true)
        
     serverOf(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__"];
    
    //AUTO_GENERATED_CODE
    
}
