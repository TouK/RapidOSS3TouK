package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 5, 2009
 * Time: 4:52:42 PM
 * To change this template use File | Settings | File Templates.
 */
class UiTimelineBand {
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__dynamic_property_storage__", "component", "column"];
        storageType "FileAndMemory"

    };
    static datasources = ["RCMDB": ["keys": ["id": ["nameInDs": "id"]]]]

    boolean isActive = true;
    Long width=50;
    Long intervalPixels=0;
    String intervalUnit="day";
    Boolean showText="";
    Double trackHeight=0;
    Long trackGap=0;
    Long syncWith=-1;
    Long layoutWith=-1;
    Boolean highlight=false;
    Date date=new Date(0);
    Long textWidth=200;
    UiTimeline timeline;

    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);
    org.springframework.validation.Errors errors ;

    Object __operation_class__ ;

    Object __dynamic_property_storage__ ;
    static relations = [
            timeline: [type: UiTimeline, reverseName: "bands", isMany: false]
    ]

    static constraints = {
        width(nullable: false, min:new Long(1), max:new Long(100))
        intervalUnit(nullable: false, inList:["millisecond", "second", "minute", "hour", "day", "week", "month", "year", "decade", "century", "millennium", "epoch","era"])
        trackHeight(nullable: true)
        trackGap(nullable: true)
        trackHeight(nullable: true)
        syncWith(nullable: true)
        layoutWith(nullable: true)
        textWidth(nullable: true)
        date(nullable: true)
        __operation_class__(nullable: true)
        __dynamic_property_storage__(nullable: true)
        errors(nullable: true)
    }

    static propertyConfiguration = [:]
    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__", "component", "column"];

    public String toString()
    {
        return getProperty("src");
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}