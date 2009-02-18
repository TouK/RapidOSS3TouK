package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 3, 2009
 * Time: 9:03:14 AM
 * To change this template use File | Settings | File Templates.
 */
class UiSearchGrid extends UiComponent {
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["columns", "images", "rowColors"];
        storageType "File"
    };
    static datasources = [:]
    String url = "search?searchIn=RsEvent";
    String rootTag = "Objects";
    String contentPath = "Object";
    String keyAttribute = "id";
    String fieldsUrl = "script/run/getViewFields?format=xml";
    String queryParameter = "query";
    String totalCountAttribute = "total";
    String offsetAttribute = "offset";
    String sortOrderAttribute = "sortOrder";
    Long pollingInterval = 0;
    Boolean queryEnabled = true;
    Long maxRowsDisplayed = 100;
    String defaultQuery = "";
    List images = [];
    List rowColors = [];


    static relations = [
            images: [type: UiImage, reverseName: "component", isMany: true],
            rowColors: [type: UiRowColor, reverseName: "grid", isMany: true]
    ]

    static constraints = {
        url(blank:false)
        rootTag(blank:false)
        contentPath(blank:false)
        keyAttribute(blank:false)
        fieldsUrl(blank:false)
        totalCountAttribute(blank:false)
        sortOrderAttribute(blank:false)
        offsetAttribute(blank:false)
        queryParameter(blank:false)
        pollingInterval(nullable: true)
        queryEnabled(nullable: true)
        maxRowsDisplayed(nullable: true)
        defaultQuery(nullable: true, blank: true)
    }

    static propertyConfiguration = [:]
    static transients = ["columns", "images", "rowColors"];
    //AUTO_GENERATED_CODE
}