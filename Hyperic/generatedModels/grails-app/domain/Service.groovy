
import com.ifountain.core.domain.annotations.*;

class Service  extends Resource {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "serviceOf"];
    };
    static datasources = [:]

    
    org.springframework.validation.Errors errors ;
    
    java.lang.Object __operation_class__ ;
    
    Server serviceOf ;
    static relations = [
            serviceOf:[isMany:false, reverseName:"hasServices", type:Server]
    ]

    static constraints={
    __operation_class__(nullable:true)
        
     errors(nullable:true)
        
     serviceOf(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__"];
    
    //AUTO_GENERATED_CODE
    
}
