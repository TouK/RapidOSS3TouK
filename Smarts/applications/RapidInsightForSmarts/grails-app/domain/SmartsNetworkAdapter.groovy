
import com.ifountain.core.domain.annotations.*;

class SmartsNetworkAdapter  extends SmartsComputerSystemComponent {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "realizedBy", "connectedVia"];
    
    
    };
    static datasources = [:]

    
    String adminStatus ="";
    
    String deviceID ="";
    
    String displayClassName ="";
    
    String displayName ="";
    
    String duplexMode ="";
    
    String duplexSource ="";
    
    String interfaceAlias ="";
    
    String interfaceCode ="";
    
    Long interfaceNumber =0;
    
    Boolean isConnector =false;
    
    Boolean isFlapping =false;
    
    Boolean isNetworkAdapterNotOperating =false;
    
    String lastChangedAt ="";
    
    Long maxSpeed =0;
    
    Long maxTransferUnit =0;
    
    Long maximumUptime =0;
    
    Long mib2IfType =0;
    
    String mode ="";
    
    String operStatus ="";
    
    String peerSystemName ="";
    
    String peerSystemType ="";
    
    String status ="";
    
    String systemModel ="";
    
    String systemName ="";
    
    String systemObjectID ="";
    
    String systemType ="";
    
    String systemVendor ="";
    
    String type ="";
    
    String cardName ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    SmartsCard realizedBy ;
    
    SmartsLink connectedVia ;
    
    
    static relations = [
    
        realizedBy:[type:SmartsCard, reverseName:"realizes", isMany:false]
    
        ,connectedVia:[type:SmartsLink, reverseName:"connectedTo", isMany:false]
    
    ]
    
    static constraints={
    adminStatus(blank:true,nullable:true)
        
     deviceID(blank:true,nullable:true)
        
     displayClassName(blank:true,nullable:true)
        
     displayName(blank:true,nullable:true)
        
     duplexMode(blank:true,nullable:true)
        
     duplexSource(blank:true,nullable:true)
        
     interfaceAlias(blank:true,nullable:true)
        
     interfaceCode(blank:true,nullable:true)
        
     interfaceNumber(nullable:true)
        
     isConnector(nullable:true)
        
     isFlapping(nullable:true)
        
     isNetworkAdapterNotOperating(nullable:true)
        
     lastChangedAt(blank:true,nullable:true)
        
     maxSpeed(nullable:true)
        
     maxTransferUnit(nullable:true)
        
     maximumUptime(nullable:true)
        
     mib2IfType(nullable:true)
        
     mode(blank:true,nullable:true)
        
     operStatus(blank:true,nullable:true)
        
     peerSystemName(blank:true,nullable:true)
        
     peerSystemType(blank:true,nullable:true)
        
     status(blank:true,nullable:true)
        
     systemModel(blank:true,nullable:true)
        
     systemName(blank:true,nullable:true)
        
     systemObjectID(blank:true,nullable:true)
        
     systemType(blank:true,nullable:true)
        
     systemVendor(blank:true,nullable:true)
        
     type(blank:true,nullable:true)
        
     cardName(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     realizedBy(nullable:true)
        
     connectedVia(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "realizedBy", "connectedVia"];
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
    
}
