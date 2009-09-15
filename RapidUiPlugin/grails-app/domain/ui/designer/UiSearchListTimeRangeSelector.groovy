package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: ifountain
 * Date: Sep 4, 2009
 * Time: 2:53:05 PM
 * To change this template use File | Settings | File Templates.
 */
class UiSearchListTimeRangeSelector {
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "searchList", "searchGrid"];


        storageType "FileAndMemory"

    };
    static datasources = ["RCMDB": ["keys": ["id": ["nameInDs": "id"]]]]

    boolean isActive = true;
    String url ="script/run/getTimeRangeData";
    String buttonConfigurationUrl = "script/run/getTimeRangeButtonConfiguration";
    String fromTimeProperty ="fromTime";
    String toTimeProperty ="toTime";
    String timeAxisLabelProperty ="timeAxisLabel";
    String stringFromTimeProperty ="stringFromTime";
    String stringToTimeProperty ="stringToTime";
    String valueProperties ="value";
    String tooltipProperty = "tooltip";

    Long id ;

    Long version ;
    org.springframework.validation.Errors errors ;

    Object __operation_class__ ;

    Object __is_federated_properties_loaded__ ;

    UiSearchGrid searchGrid;
    UiSearchList searchList;
    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);

    static relations = [
        searchGrid:[type:UiSearchGrid, reverseName:"subComponents", isMany:false],
        searchList:[type:UiSearchList, reverseName:"subComponents", isMany:false]
    ]

    static constraints={
    url(blank:false,nullable:false)
    buttonConfigurationUrl(blank:false,nullable:false)
    fromTimeProperty(blank:false,nullable:false)
    toTimeProperty(blank:false,nullable:false)
    timeAxisLabelProperty(blank:false,nullable:false)
    stringFromTimeProperty(blank:false,nullable:false)
    stringToTimeProperty(blank:false,nullable:false)
    tooltipProperty(blank:false,nullable:false)
    valueProperties(blank:false,nullable:false)


     searchGrid(nullable:true)
     searchList(nullable:true)
     __operation_class__(nullable:true)

     __is_federated_properties_loaded__(nullable:true)

     errors(nullable:true)
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__","searchGrid", "searchList"];

    public String toString()
    {
    	return getProperty("id");
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}