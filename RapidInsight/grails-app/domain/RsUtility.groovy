
import com.ifountain.core.domain.annotations.*;

class RsUtility
{

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];


    };
    static datasources = ["RCMDB":["keys":["name":["nameInDs":"name"]]]]


    String name ="";

    String value ="";

    org.springframework.validation.Errors errors ;

    Long id ;

    Long version ;

    Object __operation_class__ ;

    Object __is_federated_properties_loaded__ ;


    static relations = [:]

    static constraints={
    name(blank:false,nullable:false,key:[])

     value(blank:true,nullable:true)

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