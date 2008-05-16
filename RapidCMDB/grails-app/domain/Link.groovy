import com.ifountain.core.domain.annotations.*;


class Link  extends SmartsObject {

    //AUTO_GENERATED_CODE

    static searchable = true;
    static datasources = [:]

    
    String a_DisplayName ;
    
    String z_OperStatus ;
    
    String z_DisplayName ;
    
    String a_AdminStatus ;
    
    String a_OperStatus ;
    
    String z_AdminStatus ;
    

    static hasMany = [connectedTo:DeviceAdapter, connectedSystems:Device]

    

    static constraints={
    a_DisplayName(blank:true,nullable:true)
        
     z_OperStatus(blank:true,nullable:true)
        
     z_DisplayName(blank:true,nullable:true)
        
     a_AdminStatus(blank:true,nullable:true)
        
     a_OperStatus(blank:true,nullable:true)
        
     z_AdminStatus(blank:true,nullable:true)
        
     
    }

    static mappedBy=["connectedTo":"connectedVia", "connectedSystems":"connectedVia"]
    static belongsTo = [Device]
    static propertyConfiguration= ["a_DisplayName":["nameInDs":"A_DisplayName", "datasourceProperty":"smartDs", "lazy":true], "z_OperStatus":["nameInDs":"Z_OperStatus", "datasourceProperty":"smartDs", "lazy":true], "z_DisplayName":["nameInDs":"Z_DisplayName", "datasourceProperty":"smartDs", "lazy":true], "a_AdminStatus":["nameInDs":"A_AdminStatus", "datasourceProperty":"smartDs", "lazy":true], "a_OperStatus":["nameInDs":"A_OperStatus", "datasourceProperty":"smartDs", "lazy":true], "z_AdminStatus":["nameInDs":"Z_AdminStatus", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["a_DisplayName", "z_OperStatus", "z_DisplayName", "a_AdminStatus", "a_OperStatus", "z_AdminStatus"];
    
    //AUTO_GENERATED_CODE






}
