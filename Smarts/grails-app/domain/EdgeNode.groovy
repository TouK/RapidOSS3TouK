
import com.ifountain.core.domain.annotations.*;

class EdgeNode {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = ["RCMDB":["keys":["mapName":["nameInDs":"mapName"], "username":["nameInDs":"username"], "from":["nameInDs":"from"], "to":["nameInDs":"to"]]]]

    
    String from ="";
    
    String to ="";
    
    String username ="";
    
    String mapName ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    

    static relations = [:]
    static constraints={
    from(blank:false,nullable:false)
        
     to(blank:false,nullable:false)
        
     username(blank:false,nullable:false)
        
     mapName(blank:false,nullable:false,key:["username", "from", "to"])
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    public String toString()
    {
    	return "${getClass().getName()}[from:$from, mapName:$mapName, to:$to, username:$username]";
    }
    
    //AUTO_GENERATED_CODE



    
}
