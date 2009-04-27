
import com.ifountain.core.domain.annotations.*;

class RsInMaintenance {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];


        storageType "FileAndMemory"

    };
    static datasources = ["RCMDB":["keys":["objectName":["nameInDs":"objectName"]]]]


    String objectName ="";

    String info ="";

    String source="";

    Date ending =new Date(0);

    org.springframework.validation.Errors errors ;

    Long id ;

    Long version ;

    Object __operation_class__ ;

    Object __is_federated_properties_loaded__ ;


    static relations = [:]

    static constraints={
    objectName(blank:false,nullable:false,key:[])

     info(blank:true,nullable:true)

     source(blank:true,nullable:true)

     ending(nullable:true)

     errors(nullable:true)

     __operation_class__(nullable:true)

     __is_federated_properties_loaded__(nullable:true)


    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];

    public String toString()
    {
    	return "${getClass().getName()}[objectName:${getProperty("objectName")}]";
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE


}