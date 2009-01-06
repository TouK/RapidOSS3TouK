
import com.ifountain.core.domain.annotations.*;

class RsMessage {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];


    };
    static datasources = ["RCMDB":["keys":["eventId":["nameInDs":"eventId"], "destination":["nameInDs":"destination"], "destinationType":["nameInDs":"destinationType"], "action":["nameInDs":"action"]]]]


    Long eventId =0;

    String destination ="";

    String destinationType ="";

    String action ="";

    Long insertedAt =0;

    Long sendAfter =0;

    Long sendAt =0;

    Long state =0;

    Long id ;

    Long version ;

    org.springframework.validation.Errors errors ;

    Object __operation_class__ ;

    Object __is_federated_properties_loaded__ ;


    static relations = [:]

    static constraints={
    eventId(nullable:false)

     destination(blank:false,nullable:false)

     destinationType(blank:false,nullable:false)

     action(blank:false,nullable:false,key:["eventId", "destination", "destinationType"])

     insertedAt(nullable:true)

     sendAfter(nullable:true)

     sendAt(nullable:true)

     state(nullable:true)

     __operation_class__(nullable:true)

     __is_federated_properties_loaded__(nullable:true)

     errors(nullable:true)


    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    
    public String toString()
    {
    	return "${getClass().getName()}[action:${getProperty("action")}, destination:${getProperty("destination")}, destinationType:${getProperty("destinationType")}, eventId:${getProperty("eventId")}]";
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE



}
