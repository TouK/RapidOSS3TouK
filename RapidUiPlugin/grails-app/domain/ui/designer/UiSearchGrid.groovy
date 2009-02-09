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
        except = ["columns", "images"];
        storageType "File"
    };
    static datasources = [:]
    String url = "search?searchIn=RsEvent";
    String rootTag = "";
    String contentPath = "";
    String keyAttribute = "";
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


    static relations = [
            images: [type: UiImage, reverseName: "component", isMany: true],
    ]

    static constraints = {
        queryParameter(nullable: true, blank: true)
        pollingInterval(nullable: true)
        queryEnabled(nullable: true)
        maxRowsDisplayed(nullable: true)
        defaultQuery(nullable: true, blank: true)
    }

    static propertyConfiguration = [:]
    static transients = ["columns", "images"];
    //AUTO_GENERATED_CODE
}