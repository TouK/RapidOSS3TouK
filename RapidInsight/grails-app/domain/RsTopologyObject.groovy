
import com.ifountain.core.domain.annotations.*;

class RsTopologyObject {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "memberOfGroup"];
    
    
    };
    static datasources = ["RCMDB":["keys":["name":["nameInDs":"name"]]]]

    
    String name ="";
    
    String className ="";
    
    String description ="";
    
    String displayName ="";
    
    Boolean isManaged =false;
    
    String rsDatasource ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    List memberOfGroup =[];
    
    
    static relations = [
    
        memberOfGroup:[type:RsGroup, reverseName:"consistsOf", isMany:true]
    
    ]
    
    static constraints={
    name(blank:false,nullable:false,key:[])
        
     className(blank:true,nullable:true)
        
     description(blank:true,nullable:true)
        
     displayName(blank:true,nullable:true)
        
     isManaged(nullable:true)
        
     rsDatasource(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "memberOfGroup"];
    
    public String toString()
    {
    	return "${getClass().getName()}[name:${getProperty("name")}]";
    }
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
    
}
