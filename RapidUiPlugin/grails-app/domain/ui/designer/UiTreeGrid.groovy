package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 3, 2009
 * Time: 9:51:24 AM
 * To change this template use File | Settings | File Templates.
 */
class UiTreeGrid extends UiComponent{
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["columns", "rootImages"];
        storageType "File"
    };
    static datasources = [:]
    String url = "";
    String rootTag = "";
    String contentPath = "";
    String keyAttribute = "";
    Long pollingInterval = 0;
    Boolean expanded = false;
    String tooltip = "";
    List columns = [];
    List rootImages = [];


    static relations = [
            columns: [type: UiTreeGridColumn, reverseName: "component", isMany: true],
            rootImages: [type: UiRootImage, reverseName: "component", isMany: true],
    ]

    static constraints = {
        url(nullable: false, blank: false)
        rootTag(nullable: false, blank: false)
        contentPath(nullable: false, blank: false)
        keyAttribute(nullable: false, blank: false)
        pollingInterval(nullable: true)
        expanded(nullable: true)
        tooltip(nullable: true, blank: true)
    }

    static propertyConfiguration = [:]
    static transients = ["columns", "rootImages"];
    //AUTO_GENERATED_CODE
}