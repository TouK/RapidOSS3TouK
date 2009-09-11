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
        storageType "FileAndMemory"
    };
    static datasources = [:]
    String url = "search";
    String rootTag = "Objects";
    String contentPath = "Object";
    String keyAttribute = "id";
    String queryParameter = "query";
    String totalCountAttribute = "total";
    String offsetAttribute = "offset";
    String sortOrderAttribute = "sortOrder";
    String defaultFields = "";
    Long showMax = 0;
    Long lineSize = 3;
    Long pollingInterval = 0;
    Long timeout = 30;
    Long maxRowsDisplayed = 100;
    String defaultQuery = "";
    String defaultSearchClass = "RsEvent"
    String searchClassesUrl = "script/run/getClassesForSearch?rootClass=RsEvent&format=xml"
    List fields = [];
    List images = [];
    List propertyMenuItems = [];
    List subComponents = [];
    org.springframework.validation.Errors errors ;


    static relations = [
            fields: [type: UiSearchListField, reverseName: "component", isMany: true],
            images: [type: UiImage, reverseName: "component", isMany: true],
            propertyMenuItems: [type: UiMenuItem, isMany: true],
            subComponents: [type: UiSearchListTimeRangeSelector, reverseName: "searchList", isMany: true]
    ]

    static constraints = {
        url(blank:false)
        rootTag(blank:false)
        contentPath(blank:false)
        keyAttribute(blank:false)
        totalCountAttribute(blank:false)
        offsetAttribute(blank:false)
        sortOrderAttribute(blank:false)
        queryParameter(blank:false)
        pollingInterval(nullable: true)
        timeout(nullable: true)
        showMax(nullable: true)
        lineSize(nullable: true)
        defaultFields(nullable: true)
        maxRowsDisplayed(nullable: true)
        defaultQuery(nullable: true, blank: true)
        defaultSearchClass(nullable: false, blank: false)
        searchClassesUrl(nullable: false, blank: false)
    }

    static propertyConfiguration = [:]
    static transients = ["fields", "images"];
    //AUTO_GENERATED_CODE
}