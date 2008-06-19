class DeviceAdapter  extends DeviceComponent {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["status", "adminStatus", "maxTransferUnit", "duplexMode", "operStatus", "peerSystemName", "currentUtilization", "deviceID", "isFlapping", "mode", "maxSpeed"];
    };
    static datasources = [:]

    
    String status ;
    
    String adminStatus ;
    
    Long maxTransferUnit =0;
    
    String duplexMode ;
    
    String operStatus ;
    
    String type ;
    
    String isManaged ;
    
    String description ;
    
    String peerSystemName ;
    
    String macAddress ;
    
    String currentUtilization ;
    
    String deviceID ;
    
    String isFlapping ;
    
    String mode ;
    
    String maxSpeed ;
    
    Card realizedBy ;
    
    Link connectedVia ;
    

    static hasMany = [:]
    
    static constraints={
    status(blank:true,nullable:true)
        
     adminStatus(blank:true,nullable:true)
        
     maxTransferUnit(blank:false,nullable:false)
        
     duplexMode(blank:true,nullable:true)
        
     operStatus(blank:true,nullable:true)
        
     type(blank:true,nullable:true)
        
     isManaged(blank:true,nullable:true)
        
     description(blank:true,nullable:true)
        
     peerSystemName(blank:true,nullable:true)
        
     macAddress(blank:true,nullable:true)
        
     currentUtilization(blank:true,nullable:true)
        
     deviceID(blank:true,nullable:true)
        
     isFlapping(blank:true,nullable:true)
        
     mode(blank:true,nullable:true)
        
     maxSpeed(blank:true,nullable:true)
        
     realizedBy(nullable:true)
        
     connectedVia(nullable:true)
        
     
    }

    static mappedBy=["realizedBy":"realises", "connectedVia":"connectedTo"]
    static belongsTo = []
    static propertyConfiguration= ["status":["nameInDs":"Status", "datasourceProperty":"smartDs", "lazy":true], "adminStatus":["nameInDs":"AdminStatus", "datasourceProperty":"smartDs", "lazy":true], "maxTransferUnit":["nameInDs":"MaxTransferUnit", "datasourceProperty":"smartDs", "lazy":true], "duplexMode":["nameInDs":"DuplexMode", "datasourceProperty":"smartDs", "lazy":true], "operStatus":["nameInDs":"OperStatus", "datasourceProperty":"smartDs", "lazy":true], "peerSystemName":["nameInDs":"PeerSystemName", "datasourceProperty":"smartDs", "lazy":true], "currentUtilization":["nameInDs":"CurrentUtilization", "datasourceProperty":"smartDs", "lazy":true], "deviceID":["nameInDs":"DeviceID", "datasourceProperty":"smartDs", "lazy":true], "isFlapping":["nameInDs":"IsFlapping", "datasourceProperty":"smartDs", "lazy":true], "mode":["nameInDs":"Mode", "datasourceProperty":"smartDs", "lazy":true], "maxSpeed":["nameInDs":"MaxSpeed", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["status", "adminStatus", "maxTransferUnit", "duplexMode", "operStatus", "peerSystemName", "currentUtilization", "deviceID", "isFlapping", "mode", "maxSpeed"];
    
    //AUTO_GENERATED_CODE







}
