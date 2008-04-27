import com.ifountain.core.domain.annotations.*;


class DeviceAdapter extends DeviceComponent
{

    //AUTO_GENERATED_CODE


    static datasources = [:]

    
    String mode ;
    
    String peerSystemName ;
    
    String operStatus ;
    
    String currentUtilization ;
    
    String deviceID ;
    
    String maxSpeed ;
    
    String description ;
    
    String isManaged ;
    
    String status ;
    
    String adminStatus ;
    
    String isFlapping ;
    
    String type ;
    
    Long maxTransferUnit =0;
    
    String duplexMode ;
    
    String macAddress ;
    
    Card realizedBy ;
    
    Link connectedVia ;
    

    static hasMany = [:]

    static constraints={
    adminStatus(blank:true,nullable:true)

     connectedVia(nullable:true)

     currentUtilization(blank:true,nullable:true)

     description(blank:true,nullable:true)

     deviceID(blank:true,nullable:true)

     duplexMode(blank:true,nullable:true)

     isFlapping(blank:true,nullable:true)

     isManaged(blank:true,nullable:true)

     macAddress(blank:true,nullable:true)

     maxSpeed(blank:true,nullable:true)

     maxTransferUnit(blank:false,nullable:false)

     mode(blank:true,nullable:true)

     operStatus(blank:true,nullable:true)

     peerSystemName(blank:true,nullable:true)

     realizedBy(nullable:true)

     status(blank:true,nullable:true)

     type(blank:true,nullable:true)

     
    }

    static mappedBy=["realizedBy":"realises", "connectedVia":"connectedTo"]
    static belongsTo = []
    static propertyConfiguration= ["mode":["nameInDs":"Mode", "datasourceProperty":"smartDs", "lazy":true], "peerSystemName":["nameInDs":"PeerSystemName", "datasourceProperty":"smartDs", "lazy":true], "operStatus":["nameInDs":"OperStatus", "datasourceProperty":"smartDs", "lazy":true], "currentUtilization":["nameInDs":"CurrentUtilization", "datasourceProperty":"smartDs", "lazy":true], "deviceID":["nameInDs":"DeviceID", "datasourceProperty":"smartDs", "lazy":true], "maxSpeed":["nameInDs":"MaxSpeed", "datasourceProperty":"smartDs", "lazy":true], "status":["nameInDs":"Status", "datasourceProperty":"smartDs", "lazy":true], "adminStatus":["nameInDs":"AdminStatus", "datasourceProperty":"smartDs", "lazy":true], "isFlapping":["nameInDs":"IsFlapping", "datasourceProperty":"smartDs", "lazy":true], "maxTransferUnit":["nameInDs":"MaxTransferUnit", "datasourceProperty":"smartDs", "lazy":true], "duplexMode":["nameInDs":"DuplexMode", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["mode", "peerSystemName", "operStatus", "currentUtilization", "deviceID", "maxSpeed", "status", "adminStatus", "isFlapping", "maxTransferUnit", "duplexMode"];
    
    //AUTO_GENERATED_CODE    
}
