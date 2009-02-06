package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 3, 2009
 * Time: 10:06:03 AM
 * To change this template use File | Settings | File Templates.
 */
class UiSearchList extends UiComponent{
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["fields", "images", "propertyMenuItems"];
        storageType "File"
    };
    static datasources = [:]
    String url = "../search?searchIn=RsEvent";
    String rootTag = "";
    String contentPath = "";
    String keyAttribute = "";
    String queryParameter = "query";
    String totalCountAttribute = "total";
    String offsetAttribute = "offset";
    String sortOrderAttribute = "sortOrder";
    String defaultFields = "";
    Long showMax = 0;
    Long lineSize = 3;
    Long pollingInterval = 0;
    Long maxRowsDisplayed = 100;
    String defaultQuery = "";
    List fields = [];
    List images = [];
    List propertyMenuItems = [];


    static relations = [
            fields: [type: UiSearchListField, reverseName: "component", isMany: true],
            images: [type: UiImage, reverseName: "component", isMany: true],
            propertyMenuItems: [type: UiMenuItem, isMany: true]
    ]

    static constraints = {
        queryParameter(nullable: true, blank: true)
        pollingInterval(nullable: true)
        showMax(nullable: true)
        lineSize(nullable: true)
        defaultFields(nullable: true)
        maxRowsDisplayed(nullable: true)
        defaultQuery(nullable: true, blank: true)
    }

    static propertyConfiguration = [:]
    static transients = ["fields", "images"];
    //AUTO_GENERATED_CODE
}