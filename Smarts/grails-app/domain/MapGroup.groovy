
import com.ifountain.core.domain.annotations.*;

class MapGroup {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["maps","errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = ["RCMDB":["keys":["groupName":["nameInDs":"groupName"], "username":["nameInDs":"username"]]]]

    
    String username ="";
    
    String groupName ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    List maps =[];
    
    static relations = [maps:[reverseName:"group", isMany:true, type:TopoMap]]
    static constraints={
    username(blank:false,nullable:false)
        
     groupName(blank:false,nullable:false,key:["username"])
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    public String toString()
    {
    	return "${getClass().getName()}[groupName:$groupName, username:$username]";
    }
    
    //AUTO_GENERATED_CODE


    
}
