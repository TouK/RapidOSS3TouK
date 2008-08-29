
import com.ifountain.core.domain.annotations.*;

class Server  extends Resource {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__"];
    };
    static datasources = [:]

    
    org.springframework.validation.Errors errors ;
    
    java.lang.Object __operation_class__ ;
    
    Platform serverOf ;
    

    static hasMany = [hasServices:Service]
    
    static constraints={
    __operation_class__(nullable:true)
        
     errors(nullable:true)
        
     serverOf(nullable:true)
        
     
    }

    static mappedBy=["hasServices":"serviceOf", "serverOf":"hasServers"]
    static belongsTo = []
    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__"];
    
    //AUTO_GENERATED_CODE
    
}
