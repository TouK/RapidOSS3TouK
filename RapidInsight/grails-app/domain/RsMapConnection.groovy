
import com.ifountain.core.domain.annotations.*;

class RsMapConnection {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];


    };
    static datasources = ["RCMDB":["keys":["name":["nameInDs":"name"], "mapType":["nameInDs":"mapType"]]]]


    String name ="";

    String mapType ="default";

    String a_Name ="";

    String a_RsClassName ="";

    String z_Name ="";

    String z_RsClassName ="";

    org.springframework.validation.Errors errors ;

    Long id ;

    Long version ;

    Object __operation_class__ ;

    Object __is_federated_properties_loaded__ ;


    static relations = [:]

    static constraints={
    name(blank:false,nullable:false)

     mapType(blank:false,nullable:false,key:["name"])

     a_Name(blank:true,nullable:true)

     a_RsClassName(blank:true,nullable:true)

     z_Name(blank:true,nullable:true)

     z_RsClassName(blank:true,nullable:true)

     errors(nullable:true)

     __operation_class__(nullable:true)

     __is_federated_properties_loaded__(nullable:true)


    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];

    public String toString()
    {
    	return "${getClass().getName()}[mapType:${getProperty("mapType")}, name:${getProperty("name")}]";
    }
    
    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE


}
