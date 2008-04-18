import com.ifountain.core.domain.annotations.*;


class DeviceAdapter extends DeviceComponent implements com.ifountain.rcmdb.domain.GeneratedModel
{

    //AUTO_GENERATED_CODE


    static datasources = [:]

    
    String macAddress ;
    
    String maxSpeed ;
    
    String peerSystemName ;
    
    String deviceID ;
    
    String status ;
    
    String description ;
    
    String adminStatus ;
    
    String duplexMode ;
    
    String isFlapping ;
    
    String type ;
    
    String currentUtilization ;
    
    String operStatus ;
    
    Long maxTransferUnit =0;
    
    String isManaged ;
    
    String mode ;
    
    Card realizedBy ;
    
    Link connectedVia ;
    

    static hasMany = [:]

    static constraints={
    macAddress(blank:true,nullable:true)
        
     maxSpeed(blank:true,nullable:true)
        
     peerSystemName(blank:true,nullable:true)
        
     deviceID(blank:true,nullable:true)
        
     status(blank:true,nullable:true)
        
     description(blank:true,nullable:true)
        
     adminStatus(blank:true,nullable:true)
        
     duplexMode(blank:true,nullable:true)
        
     isFlapping(blank:true,nullable:true)
        
     type(blank:true,nullable:true)
        
     currentUtilization(blank:true,nullable:true)
        
     operStatus(blank:true,nullable:true)
        
     maxTransferUnit(blank:false,nullable:false)
        
     isManaged(blank:true,nullable:true)
        
     mode(blank:true,nullable:true)
        
     realizedBy(nullable:true)
        
     connectedVia(nullable:true)
        
     
    }

    static mappedBy=["realizedBy":"realises", "connectedVia":"connectedTo"]
    static belongsTo = []
    static propertyConfiguration= ["maxSpeed":["nameInDs":"MaxSpeed", "datasourceProperty":"smartDs", "lazy":true], "peerSystemName":["nameInDs":"PeerSystemName", "datasourceProperty":"smartDs", "lazy":true], "deviceID":["nameInDs":"DeviceID", "datasourceProperty":"smartDs", "lazy":true], "status":["nameInDs":"Status", "datasourceProperty":"smartDs", "lazy":true], "adminStatus":["nameInDs":"AdminStatus", "datasourceProperty":"smartDs", "lazy":true], "duplexMode":["nameInDs":"DuplexMode", "datasourceProperty":"smartDs", "lazy":true], "isFlapping":["nameInDs":"IsFlapping", "datasourceProperty":"smartDs", "lazy":true], "currentUtilization":["nameInDs":"CurrentUtilization", "datasourceProperty":"smartDs", "lazy":true], "operStatus":["nameInDs":"OperStatus", "datasourceProperty":"smartDs", "lazy":true], "maxTransferUnit":["nameInDs":"MaxTransferUnit", "datasourceProperty":"smartDs", "lazy":true], "mode":["nameInDs":"Mode", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["maxSpeed", "peerSystemName", "deviceID", "status", "adminStatus", "duplexMode", "isFlapping", "currentUtilization", "operStatus", "maxTransferUnit", "mode"];
    
    //AUTO_GENERATED_CODE
}
