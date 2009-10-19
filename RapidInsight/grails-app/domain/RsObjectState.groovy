
import com.ifountain.core.domain.annotations.*;

class RsObjectState {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__dynamic_property_storage__"];
    
    
        storageType "Memory"
    
    };
    static datasources = ["RCMDB":["keys":["objectId":["nameInDs":"objectId"]]]]

    
    Long objectId =0;
    
    Long state =0;
    
    org.springframework.validation.Errors errors ;
    
    Long id ;
    
    Long version ;

    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);
    
    Object __operation_class__ ;
    
    Object __dynamic_property_storage__ ;
    
    
    static relations = [:]    
    
    static constraints={
    objectId(nullable:false,key:[])
        
     state(nullable:true)
        
     errors(nullable:true)
        
     __operation_class__(nullable:true)
        
     __dynamic_property_storage__(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__"];
    
    public String toString()
    {
    	return "${getClass().getName()}[objectId:${getProperty("objectId")}]";
    }
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE

}
