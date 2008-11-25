
import com.ifountain.core.domain.annotations.*;

class OpenNmsObject {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "graphs"];
    
    
    };
    static datasources = ["RCMDB":["keys":["openNmsId":["nameInDs":"openNmsId"]]]]

    
    String openNmsId ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    List graphs =[];
    
    
    static relations = [
    
        graphs:[type:OpenNmsGraph, reverseName:"graphOf", isMany:true]
    
    ]
    
    static constraints={
    openNmsId(blank:false,nullable:false,key:[])
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "graphs"];
    
    public String toString()
    {
    	return "${getClass().getName()}[openNmsId:$openNmsId]";
    }
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE


    
}
