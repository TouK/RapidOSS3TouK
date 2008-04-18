import com.ifountain.core.domain.annotations.*;


class DeviceAdapter extends DeviceComponent implements com.ifountain.rcmdb.domain.GeneratedModel
{

    //AUTO_GENERATED_CODE


    static datasources = [:]

    
    Long maxTransferUnit =0;
    
    String status ;
    
    String description ;
    
    String isManaged ;
    
    String macAddress ;
    
    String mode ;
    
    String deviceID ;
    
    String duplexMode ;
    
    String adminStatus ;
    
    String currentUtilization ;
    
    String type ;
    
    String isFlapping ;
    
    String peerSystemName ;
    
    String maxSpeed ;
    
    String operStatus ;
    
    Card realizedBy ;
    
    Link connectedVia ;
    

    static hasMany = [:]

    static constraints={
    maxTransferUnit(blank:false,nullable:false)
        
     status(blank:true,nullable:true)
        
     description(blank:true,nullable:true)
        
     isManaged(blank:true,nullable:true)
        
     macAddress(blank:true,nullable:true)
        
     mode(blank:true,nullable:true)
        
     deviceID(blank:true,nullable:true)
        
     duplexMode(blank:true,nullable:true)
        
     adminStatus(blank:true,nullable:true)
        
     currentUtilization(blank:true,nullable:true)
        
     type(blank:true,nullable:true)
        
     isFlapping(blank:true,nullable:true)
        
     peerSystemName(blank:true,nullable:true)
        
     maxSpeed(blank:true,nullable:true)
        
     operStatus(blank:true,nullable:true)
        
     realizedBy(nullable:true)
        
     connectedVia(nullable:true)
        
     
    }

    static mappedBy=["realizedBy":"realises", "connectedVia":"connectedTo"]
    static belongsTo = []
    static propertyConfiguration= ["maxTransferUnit":["nameInDs":"MaxTransferUnit", "datasourceProperty":"smartDs", "lazy":true], "status":["nameInDs":"Status", "datasourceProperty":"smartDs", "lazy":true], "mode":["nameInDs":"Mode", "datasourceProperty":"smartDs", "lazy":true], "deviceID":["nameInDs":"DeviceID", "datasourceProperty":"smartDs", "lazy":true], "duplexMode":["nameInDs":"DuplexMode", "datasourceProperty":"smartDs", "lazy":true], "adminStatus":["nameInDs":"AdminStatus", "datasourceProperty":"smartDs", "lazy":true], "currentUtilization":["nameInDs":"CurrentUtilization", "datasourceProperty":"smartDs", "lazy":true], "isFlapping":["nameInDs":"IsFlapping", "datasourceProperty":"smartDs", "lazy":true], "peerSystemName":["nameInDs":"PeerSystemName", "datasourceProperty":"smartDs", "lazy":true], "maxSpeed":["nameInDs":"MaxSpeed", "datasourceProperty":"smartDs", "lazy":true], "operStatus":["nameInDs":"OperStatus", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["maxTransferUnit", "status", "mode", "deviceID", "duplexMode", "adminStatus", "currentUtilization", "isFlapping", "peerSystemName", "maxSpeed", "operStatus"];
    
    //AUTO_GENERATED_CODE
}
