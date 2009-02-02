package ui.designer;
import com.ifountain.core.domain.annotations.*;

class UiLayoutUnit
{

    //AUTO_GENERATED_CODE
    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "parentLayout", "childLayout", "component"];


        storageType "File"

    };
    static datasources = ["RCMDB":["keys":["type":["nameInDs":"type"]]]]

    boolean isActive = true;
    String contentFile ="";
    String gutter ="";
    Boolean scroll =false;
    Boolean useShim =false;

    Long id ;

    Long version ;

    org.springframework.validation.Errors errors ;

    Object __operation_class__ ;

    Object __is_federated_properties_loaded__ ;

    UiLayout parentLayout ;

    UiLayout childLayout ;

    UiComponent component ;


    static relations = [
        parentLayout:[type:UiLayout, reverseName:"units", isMany:false]
        ,childLayout:[type:UiLayout, reverseName:"parentUnit", isMany:false]
        ,component:[type:UiComponent, reverseName:"layoutUnit", isMany:false]
    ]

    static constraints={
      contentFile(blank:true,nullable:true)
      gutter(blank:true,nullable:true)
      scroll(nullable:true)
     __operation_class__(nullable:true)

     __is_federated_properties_loaded__(nullable:true)

     errors(nullable:true)

     childLayout(nullable:true)

     component(nullable:true)


    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "parentLayout", "childLayout", "component"];

    public String toString()
    {
    	return getProperty("id");
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}