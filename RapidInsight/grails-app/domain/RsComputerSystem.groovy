
import com.ifountain.core.domain.annotations.*;

class RsComputerSystem  extends RsTopologyObject {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["connectedVia"];
    
    
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
    
    List connectedVia =[];
    
    
    static relations = [
    
        connectedVia:[type:RsLink, reverseName:"connectedSystems", isMany:true]
    
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
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["connectedVia"];
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE


    
}
