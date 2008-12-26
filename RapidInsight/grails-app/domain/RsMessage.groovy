
import com.ifountain.core.domain.annotations.*;

class RsMessage {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    
    };
    static datasources = ["RCMDB":["keys":["eventId":["nameInDs":"eventId"], "to":["nameInDs":"to"]]]]

    
    String eventId ="";
    
    String to ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    
    static relations = [:]    
    
    static constraints={
    eventId(blank:false,nullable:false)
        
     to(blank:false,nullable:false,key:["eventId"])
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    public String toString()
    {
    	return "${getClass().getName()}[eventId:${getProperty("eventId")}, to:${getProperty("to")}]";
    }
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE

    
}
