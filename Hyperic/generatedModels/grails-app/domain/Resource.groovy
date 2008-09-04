
import com.ifountain.core.domain.annotations.*;

class Resource {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "hypericEvents"];
    };
    static datasources = ["RCMDB":["keys":["resource_name":["nameInDs":"resource_name"]]]]

    
    java.lang.String resource_name ="";
    
    java.lang.String status ="";
    
    org.springframework.validation.Errors errors ;
    
    java.lang.Object __operation_class__ ;
    static relations = [hypericEvents:[isMany:true, reverseName:"owner", type:HypericEvent]]
    static constraints={
    resource_name(blank:false,nullable:false,key:[])
        
     status(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     errors(nullable:true)
        
     
    }
    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__"];
    
    public String toString()
    {
    	return "${getClass().getName()}[resource_name:$resource_name]";
    }
    
    //AUTO_GENERATED_CODE
    
}
