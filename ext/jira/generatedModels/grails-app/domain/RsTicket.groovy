
import com.ifountain.core.domain.annotations.*;

class RsTicket 
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["description", "errors", "__operation_class__", "__is_federated_properties_loaded__", "relatedObjects", "relatedServices", "relatedEvent"];
    
    
    };
    static datasources = ["RCMDB":["keys":["name":["nameInDs":"name"]]], "jiraDs":["keys":["name":["nameInDs":"name"]]]]

    
    String name ="";
    
    String summary ="";
    
    String type ="";
    
    String status ="";
    
    String assignee ="";
    
    String priority ="";
    
    String rsDatasource ="";
    
    String description ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    List relatedObjects =[];
    
    List relatedServices =[];
    
    RsEvent relatedEvent ;
    
    
    static relations = [
    
        relatedObjects:[type:RsTopologyObject, reverseName:"relatedTickets", isMany:true]
    
        ,relatedServices:[type:RsService, reverseName:"relatedServiceTickets", isMany:true]
    
        ,relatedEvent:[type:RsEvent, reverseName:"relatedTicket", isMany:false]
    
    ]
    
    static constraints={
    name(blank:false,nullable:false,key:[])
        
     summary(blank:true,nullable:true)
        
     type(blank:true,nullable:true)
        
     status(blank:true,nullable:true)
        
     assignee(blank:true,nullable:true)
        
     priority(blank:true,nullable:true)
        
     rsDatasource(blank:true,nullable:true)
        
     description(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     relatedEvent(nullable:true)
        
     
    }

    static propertyConfiguration= ["description":["nameInDs":"description", "datasource":"jiraDs", "lazy":false]]
    static transients = ["description", "errors", "__operation_class__", "__is_federated_properties_loaded__", "relatedObjects", "relatedServices", "relatedEvent"];
    
    public String toString()
    {
    	return "${getClass().getName()}[name:${getProperty("name")}]";
    }
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}