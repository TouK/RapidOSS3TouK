
import com.ifountain.core.domain.annotations.*;

class OpenNmsIpInterface  extends OpenNmsObject {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "node", "services"];
    
    
    };
    static datasources = [:]

    
    String ipAddress ="";
    
    Date lastPolledAt =new Date(0);
    
    String snmpInterfaceId ="";
    
    String netmask ="";
    
    String macAddress ="";
    
    String ifIndex ="";
    
    String ifDescription ="";
    
    String ifType ="";
    
    String ifName ="";
    
    String ifSpeed ="";
    
    String ifAlias ="";
    
    String adminStatus ="";
    
    String operStatus ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    OpenNmsNode node ;
    
    List services =[];
    
    
    static relations = [
    
        node:[type:OpenNmsNode, reverseName:"ipInterfaces", isMany:false]
    
        ,services:[type:OpenNmsService, reverseName:"ipInterface", isMany:true]
    
    ]
    
    static constraints={
    ipAddress(blank:true,nullable:true)
        
     lastPolledAt(nullable:true)
        
     snmpInterfaceId(blank:true,nullable:true)
        
     netmask(blank:true,nullable:true)
        
     macAddress(blank:true,nullable:true)
        
     ifIndex(blank:true,nullable:true)
        
     ifDescription(blank:true,nullable:true)
        
     ifType(blank:true,nullable:true)
        
     ifName(blank:true,nullable:true)
        
     ifSpeed(blank:true,nullable:true)
        
     ifAlias(blank:true,nullable:true)
        
     adminStatus(blank:true,nullable:true)
        
     operStatus(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     node(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "node", "services"];
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE


    
}
