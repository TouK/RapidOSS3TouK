
import com.ifountain.core.domain.annotations.*;

class RsHistoricalEvent {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = ["RCMDB":["keys":["id":["nameInDs":"id"]]]]

    
    String name ="";
    
    Boolean active =true;
    
    String source ="";
    
    String owner ="";

    String rsDatasource;

    Boolean acknowledged =false;
    
    Long severity =0;
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    
    static relations = [:]    
    
    static constraints={
    name(blank:true,nullable:true)
        
     active(nullable:true)

     rsDatasource(blank:true,nullable:true)
        
     source(blank:true,nullable:true)
        
     owner(blank:true,nullable:true)
        
     acknowledged(nullable:true)
        
     severity(nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    public String toString()
    {
    	return "${getClass().getName()}[id:$id]";
    }
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
    
}
