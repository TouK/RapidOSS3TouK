
import com.ifountain.core.domain.annotations.*;

class OpenNmsGraph 
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "graphOf"];
    
    
    };
    static datasources = ["RCMDB":["keys":["url":["nameInDs":"url"]]]]

    
    String url ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    OpenNmsObject graphOf ;
    
    
    static relations = [
    
        graphOf:[type:OpenNmsObject, reverseName:"graphs", isMany:false]
    
    ]
    
    static constraints={
    url(blank:false,nullable:false,key:[])
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     graphOf(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "graphOf"];
    
    public String toString()
    {
    	return "${getClass().getName()}[url:$url]";
    }
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}