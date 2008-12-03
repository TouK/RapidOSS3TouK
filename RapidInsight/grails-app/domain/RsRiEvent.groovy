
import com.ifountain.core.domain.annotations.*;

class RsRiEvent  extends RsEvent {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = [];
    
    
        storageType "FileAndMemory"
    
    };
    static datasources = [:]

    
    String description ="";
    
    String identifier ="";
    
    
    static relations = [:]    
    
    static constraints={
    description(blank:true,nullable:true)
        
     identifier(blank:true,nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = [];
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
    
}
