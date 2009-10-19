package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 15, 2009
 * Time: 12:55:40 PM
 * To change this template use File | Settings | File Templates.
 */
class UiRowColor {
    static searchable = {
        except = ["errors", "__operation_class__", "__dynamic_property_storage__", "grid"];


        storageType "FileAndMemory"

    };
     static datasources = ["RCMDB": ["keys": ["id": ["nameInDs": "id"]]]]

    boolean isActive = true;
    String color ="#ffffff";
    String textColor = "#000000";
    String visible = "true";
    Long id ;
    Long version ;

    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);
    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __dynamic_property_storage__ ;
    UiSearchGrid grid;


    static relations = [
        grid:[type:UiSearchGrid, reverseName:"rowColors", isMany:false]
    ]

    static constraints={
     color(blank:false,nullable:false)
     visible(blank:false,nullable:false);
     textColor(blank:true,nullable:true);
     __operation_class__(nullable:true)

     __dynamic_property_storage__(nullable:true)

     errors(nullable:true)



    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__", "grid"];

    public String toString()
    {
    	return "${getClass().getName()}[color:${getProperty("color")}]";
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}