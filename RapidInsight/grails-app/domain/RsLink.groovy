
import com.ifountain.core.domain.annotations.*;

class RsLink  extends RsTopologyObject {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "connectedSystem"];
    
    
    };
    static datasources = [:]

    
    String a_ComputerSystemName ="";
    
    String a_Name ="";
    
    String z_ComputerSystemName ="";
    
    String z_Name ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    List connectedSystem =[];
    
    
    static relations = [
    
        connectedSystem:[type:RsComputerSystem, reverseName:"connectedVia", isMany:true]
    
    ]
    
    static constraints={
    a_ComputerSystemName(blank:true,nullable:true)
        
     a_Name(blank:true,nullable:true)
        
     z_ComputerSystemName(blank:true,nullable:true)
        
     z_Name(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "connectedSystem"];
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
    
}
