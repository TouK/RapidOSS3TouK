
import com.ifountain.core.domain.annotations.*;

class RrdGraphTemplate 
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    
    };
    static datasources = ["RCMDB":["keys":["name":["nameInDs":"name"]]]]

    
    String name ="";
    
    Long width =500;
    
    Long height =250;
    
    String verticalLabel ="";
    
    Long max =0;
    
    Long min =0;
    
    String title ="";
    
    String color ="";
    
    String type ="line";
    
    String description ="";
    
    org.springframework.validation.Errors errors ;
    
    Long id ;
    
    Long version ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    
    static relations = [:]    
    
    static constraints={
    name(blank:false,nullable:false,key:[])
        
     width(nullable:true)
        
     height(nullable:true)
        
     verticalLabel(blank:true,nullable:true)
        
     max(nullable:true)
        
     min(nullable:true)
        
     title(blank:true,nullable:true)
        
     color(blank:true,nullable:true)
        
     type(blank:true,nullable:true)
        
     description(blank:true,nullable:true)
        
     errors(nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    public String toString()
    {
    	return "${getClass().getName()}[name:${getProperty("name")}]";
    }
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}