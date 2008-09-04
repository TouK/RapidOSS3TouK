
import com.ifountain.core.domain.annotations.*;

class NetcoolJournal {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__"];
    };
    static datasources = ["RCMDB":["keys":["keyfield":["nameInDs":"keyfield"], "servername":["nameInDs":"servername"]]]]

    
    Long serverserial =0;
    
    String keyfield ="";
    
    String text ="";
    
    Long chrono =0;
    
    String servername ="";
    
    String connectorname ="";
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    static relations = [:]
    static constraints={
    serverserial(nullable:true)
        
     keyfield(blank:false,nullable:false)
        
     text(blank:true,nullable:true)
        
     chrono(nullable:true)
        
     servername(blank:false,nullable:false,key:["keyfield"])
        
     connectorname(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     errors(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__"];
    
    public String toString()
    {
    	return "${getClass().getName()}[keyfield:$keyfield, servername:$servername]";
    }
    
    //AUTO_GENERATED_CODE
    
}
