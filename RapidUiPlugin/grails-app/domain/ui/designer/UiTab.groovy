package ui.designer;
import com.ifountain.core.domain.annotations.*;

class UiTab 
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "webPage", "layout", "components", "dialogs", "actions"];
    
    
        storageType "FileAndMemory"
    
    };
    static datasources = ["RCMDB":["keys":["name":["nameInDs":"name"], "isActive":["nameInDs":"isActive"], "webPageId":["nameInDs":"webPageId"]]]]

    boolean isActive = true;
    String name ="";
    String title = "";
    String contentFile ="";

    Long id ;
    
    Long version ;

    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);
    
    org.springframework.validation.Errors errors ;
    
    Object __operation_class__ ;
    
    Object __is_federated_properties_loaded__ ;
    
    UiWebPage webPage ;
    Long webPageId ;

    UiLayout layout ;
    
    List components =[];
    
    List dialogs =[];
    
    List actions =[];

    
    static relations = [
    
        webPage:[type:UiWebPage, reverseName:"tabs", isMany:false]
    
        ,layout:[type:UiLayout, reverseName:"tab", isMany:false]
    
        ,components:[type:UiComponent, reverseName:"tab", isMany:true]
    
        ,dialogs:[type:UiDialog, reverseName:"tab", isMany:true]

        ,actions:[type:UiAction, reverseName:"tab", isMany:true]

    ]
    
    static constraints={
    name(blank:false,nullable:false,key:["webPageId", "isActive"], matches:"[a-z_A-z]\\w*")
    contentFile(blank:true,nullable:true)
    title(blank:true,nullable:true)

     __operation_class__(nullable:true)
        
     __is_federated_properties_loaded__(nullable:true)
        
     errors(nullable:true)
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    public String toString()
    {
    	return getProperty("name");
    }
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}