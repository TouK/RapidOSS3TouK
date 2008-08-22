
import com.ifountain.core.domain.annotations.*;

class Link extends SmartsObject
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["aa_AdminStatus", "aa_OperStatus", "aa_DisplayName", "zz_AdminStatus", "zz_OperStatus", "zz_DisplayName", "errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = [:]

    
    String aa_AdminStatus ="";
    
    String aa_OperStatus ="";
    
    String aa_DisplayName ="";
    
    String zz_AdminStatus ="";
    
    String zz_OperStatus ="";
    
    String zz_DisplayName ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    List connectedTo =[];
    
    List connectedSystems =[];
    

    static hasMany = [connectedTo:DeviceAdapter, connectedSystems:Device]
    static constraints={
    aa_AdminStatus(blank:true,nullable:true)
        
     aa_OperStatus(blank:true,nullable:true)
        
     aa_DisplayName(blank:true,nullable:true)
        
     zz_AdminStatus(blank:true,nullable:true)
        
     zz_OperStatus(blank:true,nullable:true)
        
     zz_DisplayName(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static mappedBy=["connectedTo":"connectedVia", "connectedSystems":"connectedVia"]
    static belongsTo = [Device]
    static propertyConfiguration= ["aa_AdminStatus":["nameInDs":"A_AdminStatus", "datasourceProperty":"smartDs", "lazy":true], "aa_OperStatus":["nameInDs":"A_OperStatus", "datasourceProperty":"smartDs", "lazy":true], "aa_DisplayName":["nameInDs":"A_DisplayName", "datasourceProperty":"smartDs", "lazy":true], "zz_AdminStatus":["nameInDs":"Z_AdminStatus", "datasourceProperty":"smartDs", "lazy":true], "zz_OperStatus":["nameInDs":"Z_OperStatus", "datasourceProperty":"smartDs", "lazy":true], "zz_DisplayName":["nameInDs":"Z_DisplayName", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["aa_AdminStatus", "aa_OperStatus", "aa_DisplayName", "zz_AdminStatus", "zz_OperStatus", "zz_DisplayName", "errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    //AUTO_GENERATED_CODE
}