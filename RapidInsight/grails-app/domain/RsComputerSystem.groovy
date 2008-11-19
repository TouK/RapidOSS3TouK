
import com.ifountain.core.domain.annotations.*;

class RsComputerSystem  extends RsTopologyObject {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "connectedVia"];
    
    
    };
    static datasources = [:]

    
    String location ="";
    
    String geocodes ="";
    
    String model ="";
    
    String osVersion ="";
    
    String primaryOwnerContact ="";
    
    String primaryOwnerName ="";
    
    String readCommunity ="";
    
    String snmpAddress ="";
    
    String systemName ="";
    
    String systemObjectID ="";
    
    String vendor ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    List connectedVia =[];
    
    
    static relations = [
    
        connectedVia:[type:RsLink, reverseName:"connectedSystem", isMany:true]
    
    ]
    
    static constraints={
    location(blank:true,nullable:true)
        
     geocodes(blank:true,nullable:true)
        
     model(blank:true,nullable:true)
        
     osVersion(blank:true,nullable:true)
        
     primaryOwnerContact(blank:true,nullable:true)
        
     primaryOwnerName(blank:true,nullable:true)
        
     readCommunity(blank:true,nullable:true)
        
     snmpAddress(blank:true,nullable:true)
        
     systemName(blank:true,nullable:true)
        
     systemObjectID(blank:true,nullable:true)
        
     vendor(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "connectedVia"];
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
    
}
