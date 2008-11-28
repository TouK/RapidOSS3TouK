
import com.ifountain.core.domain.annotations.*;

class OpenNmsNode extends OpenNmsObject
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "ipInterfaces"];
    
    
    };
    static datasources = [:]

    
    String nodeName ="";
    
    String dpName ="";
    
    Date createdAt =new Date(0);
    
    String type ="";
    
    Date lastPolledAt =new Date(0);
    
    String sysOid ="";
    
    String sysName ="";
    
    String sysDescription ="";
    
    String sysLocation ="";
    
    String sysContact ="";
    
    String netbiosName ="";
    
    String domainName ="";
    
    String operatingSystem ="";
    
    String foreignSource ="";
    
    String foreignId ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    List ipInterfaces =[];
    
    
    static relations = [
    
        ipInterfaces:[type:OpenNmsIpInterface, reverseName:"node", isMany:true]
    
    ]
    
    static constraints={
    nodeName(blank:true,nullable:true)
        
     dpName(blank:true,nullable:true)
        
     createdAt(nullable:true)
        
     type(blank:true,nullable:true)
        
     lastPolledAt(nullable:true)
        
     sysOid(blank:true,nullable:true)
        
     sysName(blank:true,nullable:true)
        
     sysDescription(blank:true,nullable:true)
        
     sysLocation(blank:true,nullable:true)
        
     sysContact(blank:true,nullable:true)
        
     netbiosName(blank:true,nullable:true)
        
     domainName(blank:true,nullable:true)
        
     operatingSystem(blank:true,nullable:true)
        
     foreignSource(blank:true,nullable:true)
        
     foreignId(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "ipInterfaces"];
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}