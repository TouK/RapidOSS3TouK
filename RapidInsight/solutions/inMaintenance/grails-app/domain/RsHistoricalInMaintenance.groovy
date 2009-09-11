
import com.ifountain.core.domain.annotations.*;

class RsHistoricalInMaintenance {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
        storageType "File"

    };
    static datasources = ["RCMDB":["keys":["id":["nameInDs":"id"]]]]


    String objectName ="";

    String info ="";

    String source="";

    Date starting =new Date(0);

    Date ending =new Date(0);

    org.springframework.validation.Errors errors ;

    Long id ;

    Long version ;

    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);

    Object __operation_class__ ;

    Object __is_federated_properties_loaded__ ;


    static relations = [:]

    static constraints={
    objectName(blank:false,nullable:false)

     info(blank:true,nullable:true)

     source(blank:true,nullable:true)

     starting(nullable:true)

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
