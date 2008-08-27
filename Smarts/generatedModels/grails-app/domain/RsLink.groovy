
import com.ifountain.core.domain.annotations.*;

class RsLink extends RsSmartsObject
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = [:]

    
    String a_ComputerSystemName ="";
    
    String a_Name ="";
    
    String a_AdminStatus ="";
    
    String a_DisplayName ="";
    
    String a_DuplexMode ="";
    
    Boolean a_IsFlapping =false;
    
    Long a_MaxSpeed =0;
    
    String a_Mode ="";
    
    String a_OperStatus ="";
    
    Boolean connectedSystemsUnresponsive =false;
    
    String z_ComputerSystemName ="";
    
    String z_Name ="";
    
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
    

    static hasMany = [:]
    static constraints={
    a_ComputerSystemName(blank:true,nullable:true)
        
     a_Name(blank:true,nullable:true)
        
     a_AdminStatus(blank:true,nullable:true)
        
     a_DisplayName(blank:true,nullable:true)
        
     a_DuplexMode(blank:true,nullable:true)
        
     a_IsFlapping(nullable:true)
        
     a_MaxSpeed(nullable:true)
        
     a_Mode(blank:true,nullable:true)
        
     a_OperStatus(blank:true,nullable:true)
        
     connectedSystemsUnresponsive(nullable:true)
        
     z_ComputerSystemName(blank:true,nullable:true)
        
     z_Name(blank:true,nullable:true)
        
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

    static mappedBy=[:]
    static belongsTo = []
    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    //AUTO_GENERATED_CODE
}