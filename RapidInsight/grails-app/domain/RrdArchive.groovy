
import com.ifountain.core.domain.annotations.*;

class RrdArchive 
{
        
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "variables"];
    
    
    };
    static datasources = ["RCMDB":["keys":["name":["nameInDs":"name"]]]]

    
    String name ="";
    
    String function ="AVERAGE";
    
    Double xff =0.5;
    
    Long step =1;
    
    Long row =100;
    
    org.springframework.validation.Errors errors ;
    
    Long id ;
    
    Long version ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    List variables =[];
    
    
    static relations = [
    
        variables:[type:RrdVariable, reverseName:"archives", isMany:true]
    
    ]
    
    static constraints={
    name(blank:false,nullable:false,key:[])
        
     function(blank:true,nullable:true)
        
     xff(nullable:true)
        
     step(nullable:true)
        
     row(nullable:true)
        
     errors(nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "variables"];
    
    public String toString()
    {
    	return "${getClass().getName()}[name:${getProperty("name")}]";
    }
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}