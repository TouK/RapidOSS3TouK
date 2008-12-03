
import com.ifountain.core.domain.annotations.*;

class RsLink  extends RsTopologyObject {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["connectedSystem"];
    
    
    };
    static datasources = [:]

    
    String a_ComputerSystemName ="";
    
    String a_Name ="";
    
    String z_ComputerSystemName ="";
    
    String z_Name ="";
    
    List connectedSystem =[];
    
    
    static relations = [
    
        connectedSystem:[type:RsComputerSystem, reverseName:"connectedVia", isMany:true]
    
    ]
    
    static constraints={
    a_ComputerSystemName(blank:true,nullable:true)
        
     a_Name(blank:true,nullable:true)
        
     z_ComputerSystemName(blank:true,nullable:true)
        
     z_Name(blank:true,nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["connectedSystem"];
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
    
}
