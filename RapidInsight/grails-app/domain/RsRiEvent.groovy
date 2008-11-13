
import com.ifountain.core.domain.annotations.*;

class RsRiEvent  extends RsEvent {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];


        storageType "FileAndMemory"

    };
    static datasources = [:]


    Long count =1;

    String eventName ="";

    String node ="";

    Long id ;

    Long version ;

    org.springframework.validation.Errors errors ;

    Object __operation_class__ ;

    Object __is_federated_properties_loaded__ ;


    static relations = [:]

    static constraints={
    count(nullable:true)

     eventName(blank:true,nullable:true)

     node(blank:true,nullable:true)

     __operation_class__(nullable:true)

     __is_federated_properties_loaded__(nullable:true)

     errors(nullable:true)


    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE

}
