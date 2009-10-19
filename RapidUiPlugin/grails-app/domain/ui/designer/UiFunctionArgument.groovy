package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 2, 2009
 * Time: 1:34:57 PM
 * To change this template use File | Settings | File Templates.
 */
class UiFunctionArgument {
    static searchable = {
        except = ["errors", "__operation_class__", "__dynamic_property_storage__", "action"];
        storageType "FileAndMemory"
    };
    static datasources = ["RCMDB":["keys":["id":["nameInDs":"id"]]]]

    boolean isActive = true;
    String value ="";
    Long id ;
    Long version ;

    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);
    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __dynamic_property_storage__ ;

    UiRequestAction action;


    static relations = [action:[type:UiFunctionAction, reverseName:"arguments", isMany:false]]

    static constraints={
     value(blank:true, nullable:true)
     __operation_class__(nullable:true)
     __dynamic_property_storage__(nullable:true)
     errors(nullable:true)
    }
    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__", "action"];

    public String toString()
    {
    	return getProperty("value");
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
}