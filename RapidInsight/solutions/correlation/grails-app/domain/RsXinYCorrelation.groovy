
import com.ifountain.core.domain.annotations.*;

class RsXinYCorrelation 
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__dynamic_property_storage__"];
    
    
        storageType "Memory"
    
    };
    static datasources = ["RCMDB":["keys":["id":["nameInDs":"id"]]]]

    
    String eventId ="";
    
    String identifier ="";
    
    Long willExpireAt =0;
    
    org.springframework.validation.Errors errors ;
    
    Long id ;
    
    Long version ;

    Long rsInsertedAt =0;

    Long rsUpdatedAt =0;
    
    Object __operation_class__ ;
    
    Object __dynamic_property_storage__ ;
    
    
    static relations = [:]    
    
    static constraints={
    eventId(blank:true,nullable:true)
        
     identifier(blank:true,nullable:true)
        
     willExpireAt(nullable:true)
        
     errors(nullable:true)
        
     __operation_class__(nullable:true)
        
     __dynamic_property_storage__(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__"];
    
    public String toString()
    {
    	return "${getClass().getName()}[id:${getProperty("id")}]";
    }
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}