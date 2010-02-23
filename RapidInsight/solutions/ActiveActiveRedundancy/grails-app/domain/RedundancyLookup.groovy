
import com.ifountain.core.domain.annotations.*;

class RedundancyLookup 
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["__operation_class__", "__dynamic_property_storage__", "errors"];
    
    
    };
    static datasources = ["RCMDB":["mappedName":"RCMDB", "keys":["name":["nameInDs":"name"]]]]

    
    Long id ;
    
    Long version ;
    
    Long rsInsertedAt =0;
    
    Long rsUpdatedAt =0;
    
    Object __operation_class__ ;
    
    Object __dynamic_property_storage__ ;
    
    String name ="";
    
    String value ="";
    
    org.springframework.validation.Errors errors ;
    
    
    static relations = [:]    
    
    static constraints={
    __operation_class__(nullable:true)
        
     __dynamic_property_storage__(nullable:true)
        
     name(blank:false,nullable:false,key:[])
        
     value(blank:true,nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["__operation_class__", "__dynamic_property_storage__", "errors"];
    
    public String toString()
    {
    	return "${getClass().getName()}[name:${getProperty("name")}]";
    }
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}