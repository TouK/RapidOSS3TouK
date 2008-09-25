
import com.ifountain.core.domain.annotations.*;

class RsEvent {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = ["RCMDB":["keys":["name":["nameInDs":"name"]]]]

    
    String name ="";
    
    Boolean active =true;
    
    String owner ="";
    
    Boolean acknowledged =false;
    
    Long severity =0;
    
    String source ="";
    
    Long firstNotifiedAt =0;
    
    Long lastNotifiedAt =0;
    
    Long lastChangedAt =0;
    
    Long lastClearedAt =0;
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    
    static relations = [:]    
    
    static constraints={
    name(blank:false,nullable:false,key:[])
        
     active(nullable:true)
        
     owner(blank:true,nullable:true)
        
     acknowledged(nullable:true)
        
     severity(nullable:true)
        
     source(blank:true,nullable:true)
        
     firstNotifiedAt(nullable:true)
        
     lastNotifiedAt(nullable:true)
        
     lastChangedAt(nullable:true)
        
     lastClearedAt(nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    public String toString()
    {
    	return "${getClass().getName()}[name:$name]";
    }
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
    
}
