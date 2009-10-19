
import com.ifountain.core.domain.annotations.*;

class RsHeartBeat
{

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__dynamic_property_storage__"];


    };
    static datasources = ["RCMDB":["keys":["objectName":["nameInDs":"objectName"]]]]


    String objectName ="";

    Long lastChangedAt =0;

    Long consideredDownAt =0;

    Long interval =0;

    org.springframework.validation.Errors errors ;

    Long id ;

    Long version ;

    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);

    Object __operation_class__ ;

    Object __dynamic_property_storage__ ;


    static relations = [:]

    static constraints={
    objectName(blank:false,nullable:false,key:[])

     lastChangedAt(nullable:true)

     consideredDownAt(nullable:true)

     interval(nullable:true)

     errors(nullable:true)

     __operation_class__(nullable:true)

     __dynamic_property_storage__(nullable:true)


    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__"];

    public String toString()
    {
    	return "${getClass().getName()}[objectName:${getProperty("objectName")}]";
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}