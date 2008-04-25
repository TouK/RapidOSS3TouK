import com.ifountain.core.domain.annotations.*;


class Link extends SmartsObject implements com.ifountain.rcmdb.domain.GeneratedModel
{

    //AUTO_GENERATED_CODE


    static datasources = [:]

    
    String z_DisplayName ;
    
    String z_AdminStatus ;
    
    String a_DisplayName ;
    
    String a_OperStatus ;
    
    String a_AdminStatus ;
    
    String z_OperStatus ;
    

    static hasMany = [connectedTo:DeviceAdapter, connectedSystems:Device]

    static constraints={
    a_AdminStatus(blank:true,nullable:true)

     a_DisplayName(blank:true,nullable:true)

     a_OperStatus(blank:true,nullable:true)

     z_AdminStatus(blank:true,nullable:true)

     z_DisplayName(blank:true,nullable:true)

     z_OperStatus(blank:true,nullable:true)

     
    }

    static mappedBy=["connectedTo":"connectedVia", "connectedSystems":"connectedVia"]
    static belongsTo = [Device]
    static propertyConfiguration= ["z_DisplayName":["nameInDs":"Z_DisplayName", "datasourceProperty":"smartDs", "lazy":true], "z_AdminStatus":["nameInDs":"Z_AdminStatus", "datasourceProperty":"smartDs", "lazy":true], "a_DisplayName":["nameInDs":"A_DisplayName", "datasourceProperty":"smartDs", "lazy":true], "a_OperStatus":["nameInDs":"A_OperStatus", "datasourceProperty":"smartDs", "lazy":true], "a_AdminStatus":["nameInDs":"A_AdminStatus", "datasourceProperty":"smartDs", "lazy":true], "z_OperStatus":["nameInDs":"Z_OperStatus", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["z_DisplayName", "z_AdminStatus", "a_DisplayName", "a_OperStatus", "a_AdminStatus", "z_OperStatus"];
    
    //AUTO_GENERATED_CODE    
}
