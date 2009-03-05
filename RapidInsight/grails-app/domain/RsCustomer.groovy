
import com.ifountain.core.domain.annotations.*;

class RsCustomer  extends RsGroup {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors"];
    
    
    };
    static datasources = [:]

    
    org.springframework.validation.Errors errors ;
    
    
    static relations = [:]    
    
    static constraints={
    errors(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors"];
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
    
}
