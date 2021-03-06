
import com.ifountain.core.domain.annotations.*;

class InstrumentationParameters 
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__dynamic_property_storage__"];
    
    
    };
    static datasources = ["RCMDB":["keys":["name":["nameInDs":"name"]]]]

    
    String name ="";
    
    String description ="";
    
    Boolean enabled =false;
    
    org.springframework.validation.Errors errors ;
    
    Long id ;
    
    Long version ;

    Long rsInsertedAt =0;

    Long rsUpdatedAt =0;
    
    Object __operation_class__ ;
    
    Object __dynamic_property_storage__ ;
    
    
    static relations = [:]    
    
    static constraints={
    name(blank:false,nullable:false,key:[])
        
     description(blank:true,nullable:true)
        
     enabled(nullable:true)
        
     errors(nullable:true)
        
     __operation_class__(nullable:true)
        
     __dynamic_property_storage__(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__"];
    
    public String toString()
    {
    	return "${getClass().getName()}[name:${getProperty("name")}]";
    }
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}