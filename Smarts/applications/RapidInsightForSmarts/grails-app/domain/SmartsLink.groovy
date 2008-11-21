
import com.ifountain.core.domain.annotations.*;

class SmartsLink  extends RsLink {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "vlans", "connectedTo"];
    
    
    };
    static datasources = [:]

    
    String a_AdminStatus ="";
    
    String a_DisplayName ="";
    
    String a_DuplexMode ="";
    
    Boolean a_IsFlapping =false;
    
    Long a_MaxSpeed =0;
    
    String a_Mode ="";
    
    String a_OperStatus ="";
    
    Boolean connectedSystemsUnresponsive =false;
    
    String z_AdminStatus ="";
    
    String z_DisplayName ="";
    
    String z_DuplexMode ="";
    
    Boolean z_IsFlapping =false;
    
    Long z_MaxSpeed =0;
    
    String z_Mode ="";
    
    String z_OperStatus ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    List vlans =[];
    
    List connectedTo =[];
    
    
    static relations = [
    
        vlans:[type:SmartsVlan, reverseName:"trunkCables", isMany:true]
    
        ,connectedTo:[type:SmartsNetworkAdapter, reverseName:"connectedVia", isMany:true]
    
    ]
    
    static constraints={
    a_AdminStatus(blank:true,nullable:true)
        
     a_DisplayName(blank:true,nullable:true)
        
     a_DuplexMode(blank:true,nullable:true)
        
     a_IsFlapping(nullable:true)
        
     a_MaxSpeed(nullable:true)
        
     a_Mode(blank:true,nullable:true)
        
     a_OperStatus(blank:true,nullable:true)
        
     connectedSystemsUnresponsive(nullable:true)
        
     z_AdminStatus(blank:true,nullable:true)
        
     z_DisplayName(blank:true,nullable:true)
        
     z_DuplexMode(blank:true,nullable:true)
        
     z_IsFlapping(nullable:true)
        
     z_MaxSpeed(nullable:true)
        
     z_Mode(blank:true,nullable:true)
        
     z_OperStatus(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "vlans", "connectedTo"];
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
    
}
