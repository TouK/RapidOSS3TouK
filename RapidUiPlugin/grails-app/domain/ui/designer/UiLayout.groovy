package ui.designer;
import com.ifountain.core.domain.annotations.*;

class UiLayout 
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "tab", "units", "parentUnit"];
    
    
        storageType "FileAndMemory"
    
    };
    static datasources = ["RCMDB":["keys":["id":["nameInDs":"id"]]]]

    boolean isActive = true;
    Long id ;
    
    Long version ;

    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    UiTab tab ;
    
    List units =[];
    
    UiLayoutUnit parentUnit ;
    
    
    static relations = [
    
        tab:[type:UiTab, reverseName:"layout", isMany:false]
    
        ,units:[type:UiLayoutUnit, reverseName:"parentLayout", isMany:true]
    
        ,parentUnit:[type:UiLayoutUnit, reverseName:"childLayout", isMany:false]
    
    ]
    
    static constraints={
    __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
        
     tab(nullable:true)
        
     parentUnit(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "tab", "units", "parentUnit"];
    
    public String toString()
    {
    	return "${getClass().getName()}[id:${getProperty("id")}]";
    }
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}