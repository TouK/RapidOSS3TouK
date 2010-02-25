import com.ifountain.core.domain.annotations.*;

class RsInMaintenanceSchedule {
    public static final long RUN_ONCE = 1;
    public static final long DAILY = 2;
    public static final long WEEKLY = 3;
    public static final long MONTHLY_BY_DATE = 4;
    public static final long MONTHLY_BY_DAY = 5;
    
    //AUTO_GENERATED_CODE
    static searchable = {
        except = ["errors", "__operation_class__", "__dynamic_property_storage__"];


        storageType "FileAndMemory"

    };
    static datasources = ["RCMDB": ["keys": ["id": ["nameInDs": "id"]]]]

    Long schedType = 0;
    Long startWith = 0;
    Long repeatEvery = 0;

    String objectName = "";

    String info = "";

    String daysOfWeek = "";
    String daysOfMonth = "";

    Date maintStarting = new Date(0);

    Date maintEnding = new Date(0);

    Date schedStarting = new Date(0);

    Date schedEnding = new Date(0);

    org.springframework.validation.Errors errors;

    Long id;

    Long version;

    Long rsInsertedAt = 0;

    Long rsUpdatedAt = 0;

    Object __operation_class__;

    Object __dynamic_property_storage__;


    static relations = [:]

    static constraints = {
        objectName(blank: true, nullable: true)

        schedType(inList: [RUN_ONCE, DAILY, WEEKLY, MONTHLY_BY_DATE, MONTHLY_BY_DAY])
        startWith(nullable: true)
        repeatEvery(nullable: true)
        info(blank: true, nullable: true)
        daysOfWeek(blank: true, nullable: true)
        daysOfMonth(blank: true, nullable: true)

        maintStarting(nullable: true)

        maintEnding(nullable: true)

        schedStarting(nullable: true)

        schedEnding(nullable: true)

        errors(nullable: true)

        __operation_class__(nullable: true)

        __dynamic_property_storage__(nullable: true)

    }

    static propertyConfiguration = [:]
    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__"];

    public String toString()
    {
        return "${getClass().getName()}[id:${getProperty("id")}]";
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE

}
