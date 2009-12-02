package message

import com.ifountain.core.domain.annotations.*;

class RsMessage {

    public static String EVENT_TYPE_CREATE="create";
    public static String EVENT_TYPE_CLEAR="clear";

    public static Long STATE_IN_DELAY=0
    public static Long STATE_READY=1
    public static Long STATE_ABORT=2
    public static Long STATE_SENT=3
    public static Long STATE_NOT_EXISTS=4
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__dynamic_property_storage__"];


    };
    static datasources = ["RCMDB":["keys":["eventId":["nameInDs":"eventId"], "destination":["nameInDs":"destination"], "destinationType":["nameInDs":"destinationType"], "eventType":["nameInDs":"eventType"]]]]

    String rsOwner = "p"
    Long eventId =0;
    String destination ="";
    String destinationType ="";
    String eventType ="";
    Long insertedAt =0;
    Long sendAfter =0;
    Long sendAt =0;
    Long state =0;
    Long id ;
    Long version ;
    Long rsInsertedAt =0;
    Long rsUpdatedAt =0;
    
    org.springframework.validation.Errors errors ;

    Object __operation_class__ ;

    Object __dynamic_property_storage__ ;


    static relations = [:]

    static constraints={
    eventId(nullable:false)

     destination(blank:false,nullable:false)

     destinationType(blank:false,nullable:false)

     eventType(blank:false,nullable:false,key:["eventId", "destination", "destinationType"])

     insertedAt(nullable:true)

     sendAfter(nullable:true)

     sendAt(nullable:true)

     state(nullable:true)

     __operation_class__(nullable:true)

     __dynamic_property_storage__(nullable:true)

     errors(nullable:true)


    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__"];

    public String toString()
    {
    	return "${getClass().getName()}[eventType:${getProperty("eventType")}, destination:${getProperty("destination")}, destinationType:${getProperty("destinationType")}, eventId:${getProperty("eventId")}]";
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE




}
