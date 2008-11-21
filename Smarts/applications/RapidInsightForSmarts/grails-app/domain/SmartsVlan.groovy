
import com.ifountain.core.domain.annotations.*;

class SmartsVlan  extends RsTopologyObject {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "connectedPorts", "connectedSystems", "layeredOver", "memberSystems", "trunkCables"];
    
    
    };
    static datasources = [:]

    
    String vlanKey ="";
    
    Long vlanNumber =0;
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    List connectedPorts =[];
    
    List connectedSystems =[];
    
    List layeredOver =[];
    
    List memberSystems =[];
    
    List trunkCables =[];
    
    
    static relations = [
    
        connectedPorts:[type:SmartsPort, reverseName:"partOfVlan", isMany:true]
    
        ,connectedSystems:[type:SmartsComputerSystem, reverseName:"connectedViaVlan", isMany:true]
    
        ,layeredOver:[type:SmartsComputerSystem, reverseName:"underlying", isMany:true]
    
        ,memberSystems:[type:SmartsComputerSystem, reverseName:"partOf", isMany:true]
    
        ,trunkCables:[type:SmartsLink, reverseName:"vlans", isMany:true]
    
    ]
    
    static constraints={
    vlanKey(blank:true,nullable:true)
        
     vlanNumber(nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "connectedPorts", "connectedSystems", "layeredOver", "memberSystems", "trunkCables"];
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
    
}
