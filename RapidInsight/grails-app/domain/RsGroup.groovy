
import com.ifountain.core.domain.annotations.*;

class RsGroup  extends RsTopologyObject {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["consistsOf"];
    
    
    };
    static datasources = [:]

    
    List consistsOf =[];
    
    
    static relations = [
    
        consistsOf:[type:RsTopologyObject, reverseName:"memberOfGroup", isMany:true]
    
    ]
    
    static constraints={
    
    }

    static propertyConfiguration= [:]
    static transients = ["consistsOf"];
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE


    
}
