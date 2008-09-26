package map

import com.ifountain.core.domain.annotations.*;

class MapNode {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["belongsToMap", "errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = ["RCMDB":["keys":["nodeIdentifier":["nameInDs":"nodeIdentifier"], "username":["nameInDs":"username"], "mapName":["nameInDs":"mapName"]]]]

    String rsOwner = "p"
    String nodeIdentifier ="";
    
    Long xlocation =0;
    
    Long ylocation =0;
    
    String username ="";
    
    String mapName ="";

    String expandable = "";
    String expanded = "";

    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    TopoMap belongsToMap ;
    
    static relations = [belongsToMap:[reverseName:"consistOfDevices", isMany:false, type:TopoMap]]
    static constraints={
    nodeIdentifier(blank:false,nullable:false)
        
     xlocation(nullable:true)
        
     ylocation(nullable:true)

     expandable(nullable:true)
     expanded(nullable:true)

     username(blank:false,nullable:false)
        
     mapName(blank:false,nullable:false,key:["nodeIdentifier", "username"])
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     belongsToMap(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    public String toString()
    {
    	return "${getClass().getName()}[mapName:$mapName, nodeIdentifier:$nodeIdentifier, username:$username]";
    }
    
    //AUTO_GENERATED_CODE



    
}
