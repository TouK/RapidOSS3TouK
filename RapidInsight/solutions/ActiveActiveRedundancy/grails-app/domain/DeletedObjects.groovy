
import com.ifountain.core.domain.annotations.*;

class DeletedObjects 
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["__operation_class__", "__dynamic_property_storage__", "errors"];
    
    
        storageType "File"
    
    };
    static datasources = ["RCMDB":["mappedName":"RCMDB", "keys":["modelName":["nameInDs":"modelName"], "searchQuery":["nameInDs":"searchQuery"]]]]

    
    Long id ;
    
    Long version ;
    
    Long rsInsertedAt =0;
    
    Long rsUpdatedAt =0;
    
    Object __operation_class__ ;
    
    Object __dynamic_property_storage__ ;
    
    String modelName ="";
    
    String searchQuery ="";
    
    org.springframework.validation.Errors errors ;
    
    
    static relations = [:]    
    
    static constraints={
    __operation_class__(nullable:true)
        
     __dynamic_property_storage__(nullable:true)
        
     modelName(blank:false,nullable:false)
        
     searchQuery(blank:false,nullable:false,key:["modelName"])
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["__operation_class__", "__dynamic_property_storage__", "errors"];
    
    public String toString()
    {
    	return "${getClass().getName()}[modelName:${getProperty("modelName")}, searchQuery:${getProperty("searchQuery")}]";
    }
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}