package ui.designer

import com.ifountain.core.domain.annotations.*;

class UiMetaData
{

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];


        storageType "File"

    };
    static datasources = ["RCMDB":["keys":["name":["nameInDs":"name"], "componentName":["nameInDs":"componentName"]]]]


    String name ="";

    String componentName ="";

    String type ="";

    Boolean required =false;

    String description ="";

    String inList ="";

    Long id ;

    Long version ;

    org.springframework.validation.Errors errors ;

    Object __operation_class__ ;

    Object __is_federated_properties_loaded__ ;


    static relations = [:]

    static constraints={
    name(blank:false,nullable:false)

     componentName(blank:false,nullable:false,key:["name"])

     type(blank:true,nullable:true)

     required(nullable:true)

     description(blank:true,nullable:true)

     inList(blank:true,nullable:true)

     __operation_class__(nullable:true)

     __is_federated_properties_loaded__(nullable:true)

     errors(nullable:true)


    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];

    public String toString()
    {
    	return "${getClass().getName()}[componentName:${getProperty("componentName")}, name:${getProperty("name")}]";
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}