
import com.ifountain.core.domain.annotations.*;

class RsHistoricalEvent
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = [:]

    
    String name ="";
    
    String creationClassName ="";
    
    String description ="";
    
    String displayName ="";
    
    String className ="";
    
    String instanceName ="";
    
    String eventName ="";
    
    String severity ="";
    
    String lastNotifiedAt ="";
    
    String lastCreatedAt ="";
    
    String active ="";
    
    String firstNotifiedAt ="";
    
    String lastClearedAt ="";
    
    String lastChangedAt ="";
    
    String isRoot ="";
    
    String acknowledged ="";
    
    String eventType ="";
    
    String eventState ="";
    
    String eventText ="";
    
    String impact ="";
    
    String certainty ="";
    
    String inMaintenance ="";
    
    String troubleTicketID ="";
    
    String owner ="";
    
    String category ="";
    
    String occurrenceCount ="";
    
    String classDisplayName ="";
    
    String instanceDisplayName ="";
    
    String eventDisplayName ="";
    
    String elementClassName ="";
    
    String elementName ="";
    
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
    

    static hasMany = [:]
    static constraints={
    name(blank:true,nullable:true)
        
     creationClassName(blank:true,nullable:true)
        
     description(blank:true,nullable:true)
        
     displayName(blank:true,nullable:true)
        
     className(blank:true,nullable:true)
        
     instanceName(blank:true,nullable:true)
        
     eventName(blank:true,nullable:true)
        
     severity(blank:true,nullable:true)
        
     lastNotifiedAt(blank:true,nullable:true)
        
     lastCreatedAt(blank:true,nullable:true)
        
     active(blank:true,nullable:true)
        
     firstNotifiedAt(blank:true,nullable:true)
        
     lastClearedAt(blank:true,nullable:true)
        
     lastChangedAt(blank:true,nullable:true)
        
     isRoot(blank:true,nullable:true)
        
     acknowledged(blank:true,nullable:true)
        
     eventType(blank:true,nullable:true)
        
     eventState(blank:true,nullable:true)
        
     eventText(blank:true,nullable:true)
        
     impact(blank:true,nullable:true)
        
     certainty(blank:true,nullable:true)
        
     inMaintenance(blank:true,nullable:true)
        
     troubleTicketID(blank:true,nullable:true)
        
     owner(blank:true,nullable:true)
        
     category(blank:true,nullable:true)
        
     occurrenceCount(blank:true,nullable:true)
        
     classDisplayName(blank:true,nullable:true)
        
     instanceDisplayName(blank:true,nullable:true)
        
     eventDisplayName(blank:true,nullable:true)
        
     elementClassName(blank:true,nullable:true)
        
     elementName(blank:true,nullable:true)
        
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

    static mappedBy=[:]
    static belongsTo = []
    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    //AUTO_GENERATED_CODE
}