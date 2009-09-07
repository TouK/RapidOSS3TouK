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
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "list", "grid"];


        storageType "FileAndMemory"

    };
    static datasources = ["RCMDB": ["keys": ["id": ["nameInDs": "id"]]]]

    boolean isActive = true;
    String url ="script/run/getTimeRangeData";
    String buttonConfigurationUrl = "script/run/getTimeRangeButtonConfiguration";
    String timeProperty ="time";
    String valueProperties ="value";

    Long id ;

    Long version ;
    org.springframework.validation.Errors errors ;

    Object __operation_class__ ;

    Object __is_federated_properties_loaded__ ;

    UiSearchGrid grid =null;
    UiSearchList list =null;
    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);

    static relations = [
        grid:[type:UiSearchGrid, reverseName:"subComponents", isMany:false],
        list:[type:UiSearchList, reverseName:"subComponents", isMany:false]
    ]

    static constraints={
    url(blank:false,nullable:false)
    buttonConfigurationUrl(blank:false,nullable:false)
    timeProperty(blank:false,nullable:false)
    valueProperties(blank:false,nullable:false)
    grid(nullable:true)
    list(nullable:true)
     __operation_class__(nullable:true)

     __is_federated_properties_loaded__(nullable:true)

     errors(nullable:true)
    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__","grid", "list"];

    public String toString()
    {
    	return getProperty("id");
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}