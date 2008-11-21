
import com.ifountain.core.domain.annotations.*;

class HypericEvent {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "owner"];
    };
    static datasources = ["RCMDB":["keys":["aid":["nameInDs":"aid"]]]]

    
    java.lang.String fixed ="";
    
    java.lang.String aid ="";
    
    java.lang.String timestamp ="";
    
    java.lang.String name ="";
    
    org.springframework.validation.Errors errors ;
    
    java.lang.Object __operation_class__ ;
    
    Resource owner ;

    static relations = [owner:[isMany:false, reverseName:"hypericEvents", type:Resource]]


    static constraints={
    fixed(blank:true,nullable:true)
        
     aid(blank:false,nullable:false,key:[])
        
     timestamp(blank:true,nullable:true)
        
     name(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     errors(nullable:true)
        
     owner(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__"];
    
    public String toString()
    {
    	return "${getClass().getName()}[aid:$aid]";
    }
    
    //AUTO_GENERATED_CODE
    
}
