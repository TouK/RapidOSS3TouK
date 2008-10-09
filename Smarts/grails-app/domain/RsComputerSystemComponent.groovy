
import com.ifountain.core.domain.annotations.*;

class RsComputerSystemComponent  extends RsTopologyObject {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "partOf", "layeredOver", "underlying"];
    };
    static datasources = [:]

    
    String computerSystemName ="";
    
    String tag ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    RsComputerSystem partOf ;
    
    List layeredOver =[];
    
    List underlying =[];
    
    
    static relations = [
    
        partOf:[type:RsComputerSystem, reverseName:"composedOf", isMany:false]
    
        ,layeredOver:[type:RsComputerSystemComponent, reverseName:"underlying", isMany:true]
    
        ,underlying:[type:RsComputerSystemComponent, reverseName:"layeredOver", isMany:true]
    
    ]
    
    static constraints={
    computerSystemName(blank:true,nullable:true)
        
     tag(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     partOf(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "partOf", "layeredOver", "underlying"];
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
    
}
