import com.ifountain.core.domain.annotations.*;

class RsInMaintenanceSchedule {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["__operation_class__", "__dynamic_property_storage__", "errors"];


        storageType "FileAndMemory"

    };
    static datasources = ["RCMDB":["mappedName":"RCMDB", "keys":["schedKey":["nameInDs":"schedKey"]]]]


    Long id ;

    Long version ;

    Long rsInsertedAt =0;

    Long rsUpdatedAt =0;

    Object __operation_class__ ;

    Object __dynamic_property_storage__ ;

    String objectName ="";

    String schedKey ="";

    Long schedType =0;

    String info ="";

    Date maintStarting =new Date(0);

    Date maintEnding =new Date(0);

    Date schedStarting =new Date(0);

    Date schedEnding =new Date(0);

    String daysOfWeek ="";

    String daysOfMonth ="";

    Long startWith =0;

    Long repeatEvery =0;

    org.springframework.validation.Errors errors ;


    static relations = [:]

    static constraints={
    __operation_class__(nullable:true)

     __dynamic_property_storage__(nullable:true)

     objectName(blank:true,nullable:true)

     schedKey(blank:false,nullable:false,key:[])

     schedType(nullable:true)

     info(blank:true,nullable:true)

     maintStarting(nullable:true)

     maintEnding(nullable:true)

     schedStarting(nullable:true)

     schedEnding(nullable:true)

     daysOfWeek(blank:true,nullable:true)

     daysOfMonth(blank:true,nullable:true)

     startWith(nullable:true)

     repeatEvery(nullable:true)

     errors(nullable:true)


    }

    static propertyConfiguration= [:]
    static transients = ["__operation_class__", "__dynamic_property_storage__", "errors"];

    public String toString()
    {
    	return "${getClass().getName()}[schedKey:${getProperty("schedKey")}]";
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
    public static final long RUN_ONCE = 1;
    public static final long DAILY = 2;
    public static final long WEEKLY = 3;
    public static final long MONTHLY_BY_DATE = 4;
    public static final long MONTHLY_BY_DAY = 5;


}
