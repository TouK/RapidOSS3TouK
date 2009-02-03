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
    String url = "";
    String rootTag = "";
    String contentPath = "";
    String keyAttribute = "";
    String fieldsUrl = "";
    String queryParameter = "";
    String totalCountAttribute = "";
    String offsetAttribute = "";
    String sortOrderAttribute = "";
    Long pollingInterval = 0;
    Boolean queryEnabled = true;
    Long maxRowsDisplayed = 100;
    String defaultQuery = "";
    List columns = [];
    List images = [];


    static relations = [
            columns: [type: UiColumn, reverseName: "component", isMany: true],
            images: [type: UiImage, reverseName: "component", isMany: true],
    ]

    static constraints = {
        url(nullable: false, blank: false)
        rootTag(nullable: false, blank: false)
        contentPath(nullable: false, blank: false)
        keyAttribute(nullable: false, blank: false)
        fieldsUrl(nullable: false, blank: false)
        queryParameter(nullable: true, blank: true)
        totalCountAttribute(nullable: false, blank: false)
        offsetAttribute(nullable: false, blank: false)
        sortOrderAttribute(nullable: false, blank: false)
        pollingInterval(nullable: true)
        queryEnabled(nullable: true)
        maxRowsDisplayed(nullable: true)
        defaultQuery(nullable: true, blank: true)
    }

    static propertyConfiguration = [:]
    static transients = ["columns", "images"];
    //AUTO_GENERATED_CODE
}