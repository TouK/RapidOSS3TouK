
import com.ifountain.core.domain.annotations.*;

class Service  extends Resource {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__"];
    };
    static datasources = [:]

    
    org.springframework.validation.Errors errors ;
    
    java.lang.Object __operation_class__ ;
    
    Server serviceOf ;
    

    static hasMany = [:]
    
    static constraints={
    __operation_class__(nullable:true)
        
     errors(nullable:true)
        
     serviceOf(nullable:true)
        
     
    }

    static mappedBy=["serviceOf":"hasServices"]
    static belongsTo = []
    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__"];
    
    //AUTO_GENERATED_CODE
    
}
