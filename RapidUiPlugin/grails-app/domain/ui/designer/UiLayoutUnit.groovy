package ui.designer;
import com.ifountain.core.domain.annotations.*;

class UiLayoutUnit
{

    //AUTO_GENERATED_CODE
    static searchable = {
        except = ["errors", "__operation_class__", "__dynamic_property_storage__", "parentLayout", "childLayout", "component"];


        storageType "FileAndMemory"

    };
    static datasources = ["RCMDB":["keys":["type":["nameInDs":"type"]]]]

    boolean isActive = true;
    String contentFile ="";
    String gutter ="0px";
    Boolean scroll =false;
    Boolean useShim =false;

    Long id ;

    Long version ;

    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);

    org.springframework.validation.Errors errors ;

    Object __operation_class__ ;

    Object __dynamic_property_storage__ ;

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
      useShim(nullable:true)
      scroll(nullable:true)
     __operation_class__(nullable:true)

     __dynamic_property_storage__(nullable:true)

     errors(nullable:true)

     childLayout(nullable:true)

     component(nullable:true)


    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__", "parentLayout", "childLayout", "component"];

    public String toString()
    {
    	return getProperty("id");
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}