
import com.ifountain.core.domain.annotations.*;

class TopoMap 
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = ["RCMDB":["keys":["mapName":["nameInDs":"mapName"], "username":["nameInDs":"username"]]]]

    
    String mapName ="";
    
    String username ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    List consistOfDevices =[];
    
    MapGroup group ;
    

    static hasMany = [consistOfDevices:MapNode]
    static constraints={
    mapName(blank:false,nullable:false)
        
     username(blank:false,nullable:false,key:["mapName"])
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     group(nullable:true)
        
     
    }

    static mappedBy=["consistOfDevices":"belongsToMap", "group":"maps"]
    static belongsTo = []
    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    public String toString()
    {
    	return "${getClass().getName()}[mapName:$mapName, username:$username]";
    }
    
    //AUTO_GENERATED_CODE
}