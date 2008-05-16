import com.ifountain.core.domain.annotations.*;


class DeviceAdapter  extends DeviceComponent {

    //AUTO_GENERATED_CODE

    static searchable = true;
    static datasources = [:]

    
    String status ;
    
    String deviceID ;
    
    String operStatus ;
    
    String description ;
    
    String adminStatus ;
    
    String duplexMode ;
    
    String peerSystemName ;
    
    String type ;
    
    String maxSpeed ;
    
    String isManaged ;
    
    String currentUtilization ;
    
    Long maxTransferUnit =0;
    
    String isFlapping ;
    
    String macAddress ;
    
    String mode ;
    
    Card realizedBy ;
    
    Link connectedVia ;
    

    static hasMany = [:]

    

    static constraints={
    status(blank:true,nullable:true)
        
     deviceID(blank:true,nullable:true)
        
     operStatus(blank:true,nullable:true)
        
     description(blank:true,nullable:true)
        
     adminStatus(blank:true,nullable:true)
        
     duplexMode(blank:true,nullable:true)
        
     peerSystemName(blank:true,nullable:true)
        
     type(blank:true,nullable:true)
        
     maxSpeed(blank:true,nullable:true)
        
     isManaged(blank:true,nullable:true)
        
     currentUtilization(blank:true,nullable:true)
        
     maxTransferUnit(blank:false,nullable:false)
        
     isFlapping(blank:true,nullable:true)
        
     macAddress(blank:true,nullable:true)
        
     mode(blank:true,nullable:true)
        
     realizedBy(nullable:true)
        
     connectedVia(nullable:true)
        
     
    }

    static mappedBy=["realizedBy":"realises", "connectedVia":"connectedTo"]
    static belongsTo = []
    static propertyConfiguration= ["status":["nameInDs":"Status", "datasourceProperty":"smartDs", "lazy":true], "deviceID":["nameInDs":"DeviceID", "datasourceProperty":"smartDs", "lazy":true], "operStatus":["nameInDs":"OperStatus", "datasourceProperty":"smartDs", "lazy":true], "adminStatus":["nameInDs":"AdminStatus", "datasourceProperty":"smartDs", "lazy":true], "duplexMode":["nameInDs":"DuplexMode", "datasourceProperty":"smartDs", "lazy":true], "peerSystemName":["nameInDs":"PeerSystemName", "datasourceProperty":"smartDs", "lazy":true], "maxSpeed":["nameInDs":"MaxSpeed", "datasourceProperty":"smartDs", "lazy":true], "currentUtilization":["nameInDs":"CurrentUtilization", "datasourceProperty":"smartDs", "lazy":true], "maxTransferUnit":["nameInDs":"MaxTransferUnit", "datasourceProperty":"smartDs", "lazy":true], "isFlapping":["nameInDs":"IsFlapping", "datasourceProperty":"smartDs", "lazy":true], "mode":["nameInDs":"Mode", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["status", "deviceID", "operStatus", "adminStatus", "duplexMode", "peerSystemName", "maxSpeed", "currentUtilization", "maxTransferUnit", "isFlapping", "mode"];
    
    //AUTO_GENERATED_CODE






}
