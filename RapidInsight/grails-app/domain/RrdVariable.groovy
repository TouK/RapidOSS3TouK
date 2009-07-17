
import com.ifountain.core.domain.annotations.*;

class RrdVariable 
{

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "archives"];
    
    
    };
    static datasources = ["RCMDB":["keys":["name":["nameInDs":"name"]]]]

    
    String name ="";
    
    String resource ="";
    
    String type ="GAUGE";
    
    Long heartbeat =120;
    
    Double min =Double.NaN;
    
    Double max =Double.NaN;

    Long startTime =0;
    
    Long frequency =60;
    
    org.springframework.validation.Errors errors ;
    
    Long id ;
    
    Long version ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    List archives =[];
    
    
    static relations = [
    
        archives:[type:RrdArchive, reverseName:"variables", isMany:true]
    
    ]
    
    static constraints={
    name(blank:false,nullable:false,key:[])
        
     resource(blank:true,nullable:true)
        
     type(blank:true,nullable:true)
        
     heartbeat(nullable:true)
        
     min(nullable:true)
        
     max(nullable:true)

     startTime(nullable:true)
        
     frequency(nullable:true)
        
     errors(nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "archives"];
    
    public String toString()
    {
    	return "${getClass().getName()}[name:${getProperty("name")}]";
    }
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}