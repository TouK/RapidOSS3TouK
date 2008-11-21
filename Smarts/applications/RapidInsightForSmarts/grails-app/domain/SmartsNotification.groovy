
import com.ifountain.core.domain.annotations.*;

class SmartsNotification  extends RsEvent {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "causedBy", "causes"];
    
    
        storageType "FileAndMemory"
    
    };
    static datasources = [:]

    
    String creationClassName ="";
    
    String description ="";
    
    String displayName ="";
    
    String className ="";
    
    String instanceName ="";
    
    String eventName ="";
    
    Boolean isRoot =false;
    
    Boolean isProblem =false;
    
    String eventType ="";
    
    String eventState ="";
    
    String eventText ="";
    
    Long impact =0;
    
    Long certainty =0;
    
    Boolean inMaintenance =false;
    
    String troubleTicketID ="";
    
    String category ="";
    
    Long lastNotifiedAt =0;
    
    String classDisplayName ="";
    
    String instanceDisplayName ="";
    
    String eventDisplayName ="";
    
    String elementClassName ="";
    
    String sourceDomainName ="";
    
    String userDefined1 ="";
    
    String userDefined2 ="";
    
    String userDefined3 ="";
    
    String userDefined4 ="";
    
    String userDefined5 ="";
    
    String userDefined6 ="";
    
    String userDefined7 ="";
    
    String userDefined8 ="";
    
    String userDefined9 ="";
    
    String userDefined10 ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    List causedBy =[];
    
    List causes =[];
    
    
    static relations = [
    
        causedBy:[type:SmartsNotification, reverseName:"causes", isMany:true]
    
        ,causes:[type:SmartsNotification, reverseName:"causedBy", isMany:true]
    
    ]
    
    static constraints={
    creationClassName(blank:true,nullable:true)
        
     description(blank:true,nullable:true)
        
     displayName(blank:true,nullable:true)
        
     className(blank:true,nullable:true)
        
     instanceName(blank:true,nullable:true)
        
     eventName(blank:true,nullable:true)
        
     isRoot(nullable:true)
        
     isProblem(nullable:true)
        
     eventType(blank:true,nullable:true)
        
     eventState(blank:true,nullable:true)
        
     eventText(blank:true,nullable:true)
        
     impact(nullable:true)
        
     certainty(nullable:true)
        
     inMaintenance(nullable:true)
        
     troubleTicketID(blank:true,nullable:true)
        
     category(blank:true,nullable:true)
        
     lastNotifiedAt(nullable:true)
        
     classDisplayName(blank:true,nullable:true)
        
     instanceDisplayName(blank:true,nullable:true)
        
     eventDisplayName(blank:true,nullable:true)
        
     elementClassName(blank:true,nullable:true)
        
     sourceDomainName(blank:true,nullable:true)
        
     userDefined1(blank:true,nullable:true)
        
     userDefined2(blank:true,nullable:true)
        
     userDefined3(blank:true,nullable:true)
        
     userDefined4(blank:true,nullable:true)
        
     userDefined5(blank:true,nullable:true)
        
     userDefined6(blank:true,nullable:true)
        
     userDefined7(blank:true,nullable:true)
        
     userDefined8(blank:true,nullable:true)
        
     userDefined9(blank:true,nullable:true)
        
     userDefined10(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "causedBy", "causes"];
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
    
}
