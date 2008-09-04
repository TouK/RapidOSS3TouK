
import com.ifountain.core.domain.annotations.*;

class HypericServer {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__"];
    };
    static datasources = ["RCMDB":["keys":["username":["nameInDs":"username"]]]]

    
    java.lang.String event_timestamp ="";
    
    java.lang.String relation_timestamp ="";
    
    java.lang.String status_timestamp ="";
    
    java.lang.String username ="";
    
    java.lang.String password ="";
    
    org.springframework.validation.Errors errors ;
    
    java.lang.Object __operation_class__ ;
    

    static relations = [:]
    static constraints={
    event_timestamp(blank:true,nullable:true)
        
     relation_timestamp(blank:true,nullable:true)
        
     status_timestamp(blank:true,nullable:true)
        
     username(blank:false,nullable:false,key:[])
        
     password(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     errors(nullable:true)
        
     
    }
    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__"];
    
    public String toString()
    {
    	return "${getClass().getName()}[username:$username]";
    }
    
    //AUTO_GENERATED_CODE
    
}
