
import com.ifountain.core.domain.annotations.*;

class RsCustomer extends RsGroup
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = [];
    
    
    };
    static datasources = [:]

    
    
    static relations = [:]    
    
    static constraints={
    
    }

    static propertyConfiguration= [:]
    static transients = [];
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}