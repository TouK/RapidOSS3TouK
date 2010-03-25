package message

import java.text.SimpleDateFormat

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Mar 15, 2010
* Time: 1:27:39 PM
*/
class RsMessageRuleCalendar {
    public static String EXCEPTION_DATE_FORMAT = "MM/dd/yyyy"
    //AUTO_GENERATED_CODE
    static searchable = {
        except = ["errors", "__operation_class__", "__dynamic_property_storage__"];
    };
    static datasources = ["RCMDB": ["keys": ["name": ["nameInDs": "name"], "username": ["nameInDs": "username"]]]]

    String rsOwner = "p"
    String name = "";
    String username = "";
    String days = "";
    String daysString = "";
    String exceptions = "";
    Long starting = 0;
    Long ending = 0;
    Boolean isPublic = false;

    Long id;
    Long version;
    Long rsInsertedAt =0;
    Long rsUpdatedAt =0;
    org.springframework.validation.Errors errors;
    Object __operation_class__;
    Object __dynamic_property_storage__;
    static relations = [:]
    static constraints = {
        name(nullable: false, key: ["username"])
        username(nullable: false, blank:false)
        days(nullable: false, blank:false)
        exceptions(nullable: false, blank:true, validator:{val, obj ->
             if(val != ""){
                 SimpleDateFormat format = new SimpleDateFormat(EXCEPTION_DATE_FORMAT);
                 List excs = Arrays.asList(val.split(','));
                 for(def i=0; i< excs.size(); i++){
                     try{
                         Date d = format.parse(excs[i])
                     }
                     catch(e){
                         return ["default.doesnt.match.message", EXCEPTION_DATE_FORMAT]
                         break;
                     }
                 }
             }
        })
        daysString(nullable: false, blank:false)
        isPublic(nullable: false)
        starting(nullable: false)
        ending(nullable: false, validator:{val, obj ->
            if(val <= obj.starting){
                return ['default.not.greater.than', obj.starting];
            }
        })
        __operation_class__(nullable: true)
        __dynamic_property_storage__(nullable: true)
        errors(nullable: true)
    }

    static propertyConfiguration = [:]
    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__"];

    public String toString()
    {
        return "${getClass().getName()}[name:${getProperty("name")}, username:${getProperty("username")}]";
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}