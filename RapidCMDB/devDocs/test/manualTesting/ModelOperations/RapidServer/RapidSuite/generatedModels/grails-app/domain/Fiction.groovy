
import com.ifountain.core.domain.annotations.*;

class Fiction extends Book
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "mainCharacter"];
    
    
    };
    static datasources = [:]

    
    String mainCharacterName ="";
    
    Long id ;
    
    Long version ;
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    Person mainCharacter ;
    
    
    static relations = [
    
        mainCharacter:[type:Person, reverseName:"referringBooks", isMany:false]
    
    ]
    
    static constraints={
    mainCharacterName(blank:true,nullable:true)
        
     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     mainCharacter(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "mainCharacter"];
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}