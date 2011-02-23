package message

import com.ifountain.core.domain.annotations.*;

class RsMessageRule {
    public static final String DEFAULT_DESTINATION = "Default"

    //AUTO_GENERATED_CODE
    static searchable = {
        except = ["__operation_class__", "__dynamic_property_storage__", "errors"];


    };
    static datasources = ["RCMDB":["mappedName":"RCMDB", "keys":["searchQueryId":["nameInDs":"searchQueryId"], "destinationType":["nameInDs":"destinationType"], "users":["nameInDs":"users"], "groups":["nameInDs":"groups"]]]]


    Long id ;
    Long version ;
    Long rsInsertedAt =0;
    Long rsUpdatedAt =0;
    Object __operation_class__ ;
    Object __dynamic_property_storage__ ;

    String rsOwner = "p";
    Long searchQueryId =0;
    String destinationType ="";
    String users ="_";
    String groups ="_";
    Long calendarId =0;
    Long delay =0;
    String ruleType ="self";
    String addedByUser ="";
    Boolean sendClearEventType =false;
    Boolean enabled =true;

    org.springframework.validation.Errors errors ;


    static relations = [:]

    static constraints={
    __operation_class__(nullable:true)

     __dynamic_property_storage__(nullable:true)

     searchQueryId(nullable:false)

     destinationType(blank:false,nullable:false)

     users(blank:false,nullable:false)

     groups(blank:false,nullable:false,key:["searchQueryId", "destinationType", "users"])

     calendarId(nullable:true)

     delay(nullable:true)

     ruleType(blank:true,nullable:true)

     addedByUser(blank:true,nullable:true)

     sendClearEventType(nullable:true)

     enabled(nullable:true)

     errors(nullable:true)


    }

    static propertyConfiguration= [:]
    static transients = ["__operation_class__", "__dynamic_property_storage__", "errors"];

    public String toString()
    {
    	return "${getClass().getName()}[destinationType:${getProperty("destinationType")}, groups:${getProperty("groups")}, searchQueryId:${getProperty("searchQueryId")}, users:${getProperty("users")}]";
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE

}
